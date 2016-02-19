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

package org.kie.server.controller.api.model.runtime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "server-instance-key")
public class ServerInstanceKey {

    @XmlElement(name = "server-instance-id")
    private String serverInstanceId;
    @XmlElement(name = "server-name")
    private String serverName;
    @XmlElement(name = "server-template-id")
    private String serverTemplateId;
    @XmlElement(name = "server-url")
    private String url;

    public ServerInstanceKey() {
    }

    public ServerInstanceKey(String serverInstanceId, String serverName, String serverTemplateId, String url) {
        this.serverInstanceId = serverInstanceId;
        this.serverName = serverName;
        this.serverTemplateId = serverTemplateId;
        this.url = url;
    }

    public String getServerTemplateId() {
        return serverTemplateId;
    }

    public String getServerName() {
        return serverName;
    }

    public String getServerInstanceId() {
        return serverInstanceId;
    }

    public String getUrl() {
        return url;
    }

    public void setServerInstanceId(String serverInstanceId) {
        this.serverInstanceId = serverInstanceId;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ServerInstanceKey{" +
                "serverInstanceId='" + serverInstanceId + '\'' +
                ", serverName='" + serverName + '\'' +
                ", serverTemplateId='" + serverTemplateId + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
