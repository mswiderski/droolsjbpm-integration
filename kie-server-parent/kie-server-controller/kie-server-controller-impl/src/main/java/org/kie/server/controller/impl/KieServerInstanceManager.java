/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.server.controller.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.server.api.KieServerConstants;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.KieContainerResourceList;
import org.kie.server.api.model.KieScannerResource;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.Message;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.Severity;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.ServerInstanceKey;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;

public class KieServerInstanceManager {

    private static KieServerInstanceManager INSTANCE = new KieServerInstanceManager();

    public static KieServerInstanceManager getInstance() {
        return INSTANCE;
    }

    public List<org.kie.server.controller.api.model.runtime.Container> startScanner(ServerTemplate serverTemplate, final ContainerSpec containerSpec, final long interval) {

        return callRemoteKieServerOperation(serverTemplate, containerSpec, new RemoteKieServerOperation<Void>(){
            @Override
            public Void doOperation(KieServicesClient client, List<Message> messages, Container container) {
                KieScannerResource scannerResource = new KieScannerResource();
                scannerResource.setPollInterval(interval);
                scannerResource.setStatus(KieScannerStatus.STARTED);

                ServiceResponse<KieScannerResource> response = client.updateScanner(containerSpec.getId(), scannerResource);
                if (response.getType().equals(ServiceResponse.ResponseType.SUCCESS)) {
                    messages.add(new Message(Severity.INFO, "Scanner started successfully on server instance " + container.getUrl()));
                } else {
                    Message message = new Message(Severity.ERROR, "Scanner failed to start on server instance " + container.getUrl());
                    message.addMessage(response.getMsg());
                    messages.add(message);
                }

                return null;
            }
        });
    }

    public List<org.kie.server.controller.api.model.runtime.Container> stopScanner(ServerTemplate serverTemplate, final ContainerSpec containerSpec) {

        return callRemoteKieServerOperation(serverTemplate, containerSpec, new RemoteKieServerOperation<Void>(){
            @Override
            public Void doOperation(KieServicesClient client, List<Message> messages, Container container) {
                KieScannerResource scannerResource = new KieScannerResource();
                scannerResource.setPollInterval(null);
                scannerResource.setStatus(KieScannerStatus.STOPPED);

                ServiceResponse<KieScannerResource> response = client.updateScanner(containerSpec.getId(), scannerResource);
                if (response.getType().equals(ServiceResponse.ResponseType.SUCCESS)) {
                    messages.add(new Message(Severity.INFO, "Scanner stopped successfully on server instance " + container.getUrl()));
                } else {
                    Message message = new Message(Severity.ERROR, "Scanner failed to stop on server instance " + container.getUrl());
                    message.addMessage(response.getMsg());
                    messages.add(message);
                }

                return null;
            }
        });
    }

    public List<org.kie.server.controller.api.model.runtime.Container> scanNow(ServerTemplate serverTemplate, final ContainerSpec containerSpec) {

        return callRemoteKieServerOperation(serverTemplate, containerSpec, new RemoteKieServerOperation<Void>(){
            @Override
            public Void doOperation(KieServicesClient client, List<Message> messages, Container container) {
                KieScannerResource scannerResource = new KieScannerResource();
                scannerResource.setPollInterval(null);
                scannerResource.setStatus(KieScannerStatus.SCANNING);

                ServiceResponse<KieScannerResource> response = client.updateScanner(containerSpec.getId(), scannerResource);
                if (response.getType().equals(ServiceResponse.ResponseType.SUCCESS)) {
                    messages.add(new Message(Severity.INFO, "Scanner (scan now) invoked successfully on server instance " + container.getUrl()));
                } else {
                    Message message = new Message(Severity.ERROR, "Scanner (scan now) failed on server instance " + container.getUrl());
                    message.addMessage(response.getMsg());
                    messages.add(message);
                }

                return null;
            }
        });
    }


    public List<org.kie.server.controller.api.model.runtime.Container> startContainer(ServerTemplate serverTemplate, final ContainerSpec containerSpec) {

        return callRemoteKieServerOperation(serverTemplate, containerSpec, new RemoteKieServerOperation<Void>(){
            @Override
            public Void doOperation(KieServicesClient client, List<Message> messages, Container container) {
                KieContainerResource containerResource = new KieContainerResource();
                containerResource.setContainerId(containerSpec.getId());
                containerResource.setReleaseId(containerSpec.getReleasedId());

                ServiceResponse<KieContainerResource> response = client.createContainer(containerSpec.getId(), containerResource);
                if (response.getType().equals(ServiceResponse.ResponseType.SUCCESS)) {
                    messages.add(new Message(Severity.INFO, "Container " + containerSpec.getId() + " started successfully on server instance " + container.getUrl()));
                } else {
                    Message message = new Message(Severity.ERROR, "Container " + containerSpec.getId() + " failed to start on server instance " + container.getUrl());
                    message.addMessage(response.getMsg());
                    messages.add(message);
                }

                return null;
            }
        });
    }

    public List<org.kie.server.controller.api.model.runtime.Container> stopContainer(ServerTemplate serverTemplate, final ContainerSpec containerSpec) {

        return callRemoteKieServerOperation(serverTemplate, containerSpec, new RemoteKieServerOperation<Void>(){
            @Override
            public Void doOperation(KieServicesClient client, List<Message> messages, Container container) {

                ServiceResponse<Void> response = client.disposeContainer(containerSpec.getId());
                if (response.getType().equals(ServiceResponse.ResponseType.SUCCESS)) {
                    messages.add(new Message(Severity.INFO, "Container " + containerSpec.getId() + " stopped successfully on server instance " + container.getUrl()));
                } else {
                    Message message = new Message(Severity.ERROR, "Container " + containerSpec.getId() + " failed to stop on server instance " + container.getUrl());
                    message.addMessage(response.getMsg());
                    messages.add(message);
                }

                return null;
            }
        });
    }

    public List<org.kie.server.controller.api.model.runtime.Container> upgradeContainer(ServerTemplate serverTemplate, final ContainerSpec containerSpec) {

        return callRemoteKieServerOperation(serverTemplate, containerSpec, new RemoteKieServerOperation<Void>(){
            @Override
            public Void doOperation(KieServicesClient client, List<Message> messages, Container container) {

                ServiceResponse<ReleaseId> response = client.updateReleaseId(containerSpec.getId(), containerSpec.getReleasedId());
                if (response.getType().equals(ServiceResponse.ResponseType.SUCCESS)) {
                    messages.add(new Message(Severity.INFO, "Container " + containerSpec.getId() + " started successfully on server instance " + container.getUrl()));
                } else {
                    Message message = new Message(Severity.ERROR, "Container " + containerSpec.getId() + " failed to start on server instance " + container.getUrl());
                    message.addMessage(response.getMsg());
                    messages.add(message);
                }

                return null;
            }
        });
    }

    public List<org.kie.server.controller.api.model.runtime.Container> getContainers(ServerInstanceKey serverInstanceKey) {

        List<org.kie.server.controller.api.model.runtime.Container> containers = new ArrayList<org.kie.server.controller.api.model.runtime.Container>();
        if (serverInstanceKey == null || serverInstanceKey.getUrl() == null) {

            return containers;
        }

        KieServicesClient client = getClient(serverInstanceKey.getUrl());

        ServiceResponse<KieContainerResourceList> response = client.listContainers();
        if (response.getResult().equals(ServiceResponse.ResponseType.SUCCESS)) {
            KieContainerResourceList resourceList = response.getResult();

            for (KieContainerResource containerResource : resourceList.getContainers()) {

                Container container = new Container();
                container.setContainerSpecId(containerResource.getContainerId());
                container.setContainerName(containerResource.getContainerId());
                container.setServerInstanceId(serverInstanceKey.getServerInstanceId());
                container.setUrl(serverInstanceKey.getUrl() + "/containers/"+containerResource.getContainerId());
                container.setResolvedReleasedId(containerResource.getResolvedReleaseId());
                container.setServerTemplateId(serverInstanceKey.getServerTemplateId());

                containers.add(container);
            }

        }
        return containers;
    }


    /*
     * helper methods
     */

    protected List<org.kie.server.controller.api.model.runtime.Container> callRemoteKieServerOperation(ServerTemplate serverTemplate, ContainerSpec containerSpec, RemoteKieServerOperation operation) {
        List<org.kie.server.controller.api.model.runtime.Container> containers = new ArrayList<org.kie.server.controller.api.model.runtime.Container>();
        if (serverTemplate.getServerInstanceKeys() != null || serverTemplate.getServerInstanceKeys().isEmpty()) {

            return containers;
        }

        for (ServerInstanceKey instanceUrl : serverTemplate.getServerInstanceKeys()) {
            List<Message> messages = new ArrayList<Message>();

            Container container = new Container();
            container.setContainerSpecId(containerSpec.getId());
            container.setServerTemplateId(serverTemplate.getId());
            container.setServerInstanceId(serverTemplate.getId()+"@"+instanceUrl);
            container.setUrl(instanceUrl + "/containers/"+containerSpec.getId());
            container.setMessages(messages);

            try {
                KieServicesClient client = getClient(instanceUrl.getUrl());

                operation.doOperation(client, messages, container);
            } catch (Exception e) {
                Message message = new Message(Severity.WARN, "Unable to connect to " + instanceUrl);
                message.addMessage(e.getMessage());
                messages.add(message);
            }

            containers.add(container);
        }

        return containers;
    }


    protected KieServicesClient getClient(String url) {

        KieServicesConfiguration configuration = KieServicesFactory.newRestConfiguration(url, getUser(), getPassword());
        configuration.setTimeout(60000);

        configuration.setMarshallingFormat(MarshallingFormat.JSON);

        KieServicesClient kieServicesClient =  KieServicesFactory.newKieServicesClient(configuration);

        return kieServicesClient;
    }

    protected String getUser() {
        return System.getProperty(KieServerConstants.CFG_KIE_USER, "kieserver");
    }

    protected String getPassword() {
        return System.getProperty(KieServerConstants.CFG_KIE_PASSWORD, "kieserver1!");
    }

    private class RemoteKieServerOperation<T> {

        public T doOperation(KieServicesClient client, List<Message> messages, Container container) {

            return null;
        }
    }
}
