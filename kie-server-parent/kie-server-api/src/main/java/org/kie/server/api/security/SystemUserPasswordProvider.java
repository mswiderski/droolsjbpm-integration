/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.server.api.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.util.Scanner;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemUserPasswordProvider {

    private static final Logger logger = LoggerFactory.getLogger(SystemUserPasswordProvider.class);

    private final String defaultPassword = "kieserver1!";

    private SecretKeyFactory factory;
    private KeyStore ks;
    private char[] keystorePassword;

    private String keystoreLocation;

    public SystemUserPasswordProvider(String keystoreLocation, char[] keystorePassword){
        try {

            if (keystoreLocation != null) {
                ks = KeyStore.getInstance("JCEKS");

                File keyStoreFile = new File(keystoreLocation);
                if (keyStoreFile.exists()) {
                    FileInputStream keystoreInput = new FileInputStream(keyStoreFile);
                    try {
                        ks.load(keystoreInput, keystorePassword);

                        factory = SecretKeyFactory.getInstance("PBE");
                        // store key store password only if it was successfully loaded from key store
                        this.keystorePassword = keystorePassword;
                        this.keystoreLocation = keystoreLocation;
                    } finally {
                        keystoreInput.close();
                    }
                } else {
                    // .keystore file not created yet => create it
                    ks.load(null, null);
                    ks.store(new FileOutputStream(keystoreLocation), keystorePassword);

                    factory = SecretKeyFactory.getInstance("PBE");
                    // store key store password only if it was successfully loaded from key store
                    this.keystorePassword = keystorePassword;
                    this.keystoreLocation = keystoreLocation;
                }
            }
        } catch (Exception e) {
            logger.warn("Failed initialization of SystemUserPasswordProvider due to {}", e.getMessage());
        }
    }

    public char[] retrieveEntryPassword(String alias) throws Exception {
        if (keystorePassword == null) {
            return defaultPassword.toCharArray();
        }
        KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) ks.getEntry(alias, new KeyStore.PasswordProtection(keystorePassword));
        if (entry != null) {
            PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(entry.getSecretKey(), PBEKeySpec.class);

            return keySpec.getPassword();
        }
        // return default of no entry has been found
        return defaultPassword.toCharArray();
    }

    public void createKeyEntry(String alias, char[] password) throws Exception {

        SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(password));

        ks.setEntry(alias, new KeyStore.SecretKeyEntry(generatedSecret), new KeyStore.PasswordProtection(keystorePassword));
        FileOutputStream keystoreOutput = new FileOutputStream(keystoreLocation);
        try {
            ks.store(keystoreOutput, keystorePassword);
        } finally {
            keystoreOutput.close();
        }

    }

    public static void main(String[] args) throws Exception {
        char[] password = null;
        char[] kieServerPassword = null;

        if (args.length != 1) {
            System.out.println("Wrong usage: location of keystore file is mandatory");
            System.exit(0);
        }

        Scanner reader = new Scanner(System.in);

        // read password for keystore first to be able to write to it
        System.out.println("Insert keystore password>>");
        if (System.console() != null) {
            password = System.console().readPassword();
        } else {
            password = reader.nextLine().toCharArray();
        }
        SystemUserPasswordProvider provider = new SystemUserPasswordProvider(args[0], password);

        // read password that will be stored in keystore and then used as password for kie server interaction
        System.out.println("Insert kie server password>>");
        if (System.console() != null) {
            kieServerPassword = System.console().readPassword();
        } else {
            kieServerPassword = reader.nextLine().toCharArray();
        }
        provider.createKeyEntry("kieserver", kieServerPassword);

        System.out.println("Password stored in key store successfully");
        System.out.println("Would you like to display stored password? [y/n]");
        String displayPassword = reader.nextLine();

        if (displayPassword.equalsIgnoreCase("y")) {
            System.out.println(new String(provider.retrieveEntryPassword("kieserver")));
        }
    }
}
