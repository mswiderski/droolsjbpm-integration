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

package org.kie.server.controller.impl.events;

import org.kie.server.controller.api.events.ServerInstanceUpdated;
import org.kie.server.controller.api.model.runtime.ServerInstance;

public class ServerInstanceUpdatedImpl implements ServerInstanceUpdated {

    private ServerInstance serverInstance;

    public ServerInstanceUpdatedImpl() {
    }

    public ServerInstanceUpdatedImpl(ServerInstance serverInstance) {
        this.serverInstance = serverInstance;
    }

    @Override
    public ServerInstance getServerInstance() {
        return serverInstance;
    }

    public void setServerInstance(ServerInstance serverInstance) {
        this.serverInstance = serverInstance;
    }
}
