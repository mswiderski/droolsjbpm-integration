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

package org.kie.server.controller.impl.runtime;

import java.util.ArrayList;
import java.util.Collection;

import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.Message;
import org.kie.server.controller.api.model.runtime.ServerInstance;

public class ServerInstanceImpl extends ServerInstanceKeyImpl implements ServerInstance {

    private String version;

    private Collection<Message> messages = new ArrayList<Message>();
    private Collection<Container> containers = new ArrayList<Container>();

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Collection<Message> getMessages() {
        if (messages == null) {
            messages = new ArrayList<Message>();
        }
        return messages;
    }

    @Override
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
