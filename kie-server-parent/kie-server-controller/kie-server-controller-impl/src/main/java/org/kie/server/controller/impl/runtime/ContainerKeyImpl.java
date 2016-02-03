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

import org.kie.server.controller.api.model.runtime.ContainerKey;

public class ContainerKeyImpl implements ContainerKey {

    private String serverTemplateId;
    private String containerSpecId;

    private String url;


    @Override
    public String getServerTemplateId() {
        return serverTemplateId;
    }

    @Override
    public String getContainerSpecId() {
        return containerSpecId;
    }


    @Override
    public String getUrl() {
        return url;
    }

    public void setServerTemplateId(String serverTemplateId) {
        this.serverTemplateId = serverTemplateId;
    }

    public void setContainerSpecId(String containerSpecId) {
        this.containerSpecId = containerSpecId;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
