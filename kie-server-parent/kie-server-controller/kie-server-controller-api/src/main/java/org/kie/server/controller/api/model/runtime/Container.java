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
import org.kie.server.api.model.ReleaseId;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "container-details")
public class Container extends ContainerKey {

    @XmlElement(name="sever-instance-id")
    private String serverInstanceId;
    @XmlElement(name="container-release-id")
    private ReleaseId resolvedReleasedId;
    @XmlElement(name="messages")
    private Collection<Message> messages = new ArrayList<Message>();

    public Container() {

    }

    public Container( final String containerSpecId,
            final String containerName,
            final ServerInstanceKey serverInstanceKey,
            final Collection<Message> messages,
            final ReleaseId resolvedReleasedId,
            final String url ) {
        super( containerSpecId, containerName, serverInstanceKey );
        this.messages.addAll( messages );
        this.resolvedReleasedId = resolvedReleasedId;

    }

    public String getServerInstanceId() {
        return serverInstanceId;
    }

    public Collection<Message> getMessages() {
        if (messages == null) {
            messages = new ArrayList<Message>();
        }
        return messages;
    }

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

    public ServerInstanceKey getServerInstanceKey() {
        return new ServerInstanceKey(serverInstanceId, serverInstanceId, getServerTemplateId(), getUrl());
    }
}
