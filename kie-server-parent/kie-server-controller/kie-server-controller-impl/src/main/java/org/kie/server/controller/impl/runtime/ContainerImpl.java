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

import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.runtime.Message;

public class ContainerImpl extends ContainerKeyImpl implements Container {

    private String serverInstanceId;
    private ReleaseId resolvedReleasedId;

    private Collection<Message> messages = new ArrayList<Message>();


    @Override
    public String getServerInstanceId() {
        return serverInstanceId;
    }

    @Override
    public Collection<Message> getMessages() {
        if (messages == null) {
            messages = new ArrayList<Message>();
        }
        return messages;
    }

    @Override
    public ReleaseId getResolvedReleasedId() {
        return resolvedReleasedId;
    }


    public void setServerInstanceId(String serverInstanceId) {
        this.serverInstanceId = serverInstanceId;
    }

    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }

    public void setResolvedReleasedId(ReleaseId resolvedReleasedId) {
        this.resolvedReleasedId = resolvedReleasedId;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }
}
