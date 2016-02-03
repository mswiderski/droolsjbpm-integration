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

package org.kie.server.controller.api.model.spec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "container-spec-key")
public class ContainerSpecKey {

    @XmlElement(name = "container-id")
    private String id;
    @XmlElement(name = "container-name")
    private String containerName;
    @XmlElement(name = "server-template-key")
    private ServerTemplateKey serverTemplateKey;

    public ContainerSpecKey() {

    }

    public ContainerSpecKey( final String id,
            final String containerName,
            final ServerTemplateKey serverTemplateKey ) {
        this.id = id;
        this.containerName = containerName;
        this.serverTemplateKey = serverTemplateKey;
    }

    public String getId() {
        return id;
    }

    public String getContainerName() {
        return containerName;
    }

    public ServerTemplateKey getServerTemplateKey() {
        return serverTemplateKey;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public void setServerTemplateKey(ServerTemplateKey serverTemplateKey) {
        this.serverTemplateKey = serverTemplateKey;
    }

    @Override
    public String toString() {
        return "ContainerSpecKey{" +
                "id='" + id + '\'' +
                ", containerName='" + containerName + '\'' +
                ", serverTemplateKey=" + serverTemplateKey +
                '}';
    }
}
