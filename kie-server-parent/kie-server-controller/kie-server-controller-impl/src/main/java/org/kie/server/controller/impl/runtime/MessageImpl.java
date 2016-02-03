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

import org.kie.server.controller.api.model.runtime.Message;
import org.kie.server.controller.api.model.runtime.Severity;

public class MessageImpl implements Message {

    private Severity severity;
    private Collection<String> messages = new ArrayList<String>();

    public MessageImpl() {
    }

    public MessageImpl(Severity severity, String message) {
        this.severity = severity;
        addMessage(message);
    }

    public MessageImpl(Severity severity, Collection<String> messages) {
        this.severity = severity;
        this.messages = messages;
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }

    @Override
    public Collection<String> getMessages() {
        return messages;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void setMessages(Collection<String> messages) {
        this.messages = messages;
    }

    public void addMessage(String message) {
        this.messages.add(message);
    }
}
