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

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.server.api.model.Message;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "server-instance-details")
public class ServerInstance extends ServerInstanceKey {

    @XmlElement(name = "server-version")
    private String version;
    @XmlElement(name = "server-messages")
    private Collection<Message> messages = new ArrayList<Message>();
    @XmlElement(name = "containers")
    private Collection<Container> containers = new ArrayList<Container>();

    public String getVersion() {
        return version;
    }

    public Collection<Message> getMessages() {
        if (messages == null) {
            messages = new ArrayList<Message>();
        }
        return messages;
    }

    public Collection<Container> getContainers() {
        if (containers == null) {
            containers = new ArrayList<Container>();
        }
        return containers;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public void setContainers(Collection<Container> containers) {
        this.containers = containers;
    }

    public void addContainer(Container container) {
        this.containers.add(container);
    }
}
