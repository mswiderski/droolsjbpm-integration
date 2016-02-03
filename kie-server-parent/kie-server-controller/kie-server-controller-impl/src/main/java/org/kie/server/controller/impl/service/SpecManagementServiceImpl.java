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

package org.kie.server.controller.impl.service;

import java.util.Collection;

import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ServerConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.server.controller.api.service.SpecManagementService;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.server.controller.impl.spec.ServerTemplateImpl;
import org.kie.server.controller.impl.storage.InMemoryKieServerTemplateStorage;

public class SpecManagementServiceImpl implements SpecManagementService {

    private KieServerTemplateStorage templateStorage = InMemoryKieServerTemplateStorage.getInstance();

    @Override
    public void saveContainerSpec(String serverTemplateId, ContainerSpec containerSpec) {
        ServerTemplate serverTemplate = templateStorage.load(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("No server template found for id " + serverTemplateId);
        }

        serverTemplate.addContainerSpec(containerSpec);

        templateStorage.update(serverTemplate);
    }

    @Override
    public void saveServerTemplate(ServerTemplate serverTemplate) {
        if (templateStorage.exists(serverTemplate.getId())) {
            throw new RuntimeException("Server template for id " + serverTemplate.getId() + " already exists");
        }

        templateStorage.store(serverTemplate);
    }

    @Override
    public ServerTemplate getServerTemplate(String serverTemplateId) {
        return templateStorage.load(serverTemplateId);
    }

    @Override
    public Collection<ServerTemplateKey> listServerTemplateKeys() {
        return templateStorage.loadKeys();
    }

    @Override
    public Collection<ServerTemplate> listServerTemplates() {
        return templateStorage.load();
    }

    @Override
    public Collection<ContainerSpec> getContainers(String serverTemplateId) {
        ServerTemplate serverTemplate = templateStorage.load(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("No server template found for id " + serverTemplateId);
        }

        return serverTemplate.getContainersSpec();
    }

    @Override
    public void deleteContainerSpec(String serverTemplateId, String containerSpecId) {
        ServerTemplate serverTemplate = templateStorage.load(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("No server template found for id " + serverTemplateId);
        }

        serverTemplate.deleteContainerSpec(containerSpecId);

        templateStorage.update(serverTemplate);
    }

    @Override
    public void deleteServerTemplate(String serverTemplateId) {
        templateStorage.delete(serverTemplateId);
    }

    @Override
    public void copyServerTemplate(String serverTemplateId, String newServerTemplateId, String newServerTemplateName) {
        ServerTemplate serverTemplate = templateStorage.load(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("No server template found for id " + serverTemplateId);
        }
        ((ServerTemplateImpl)serverTemplate).setId(newServerTemplateId);
        ((ServerTemplateImpl)serverTemplate).setName(newServerTemplateName);

        templateStorage.store(serverTemplate);
    }

    @Override
    public void updateContainerConfig(String serverTemplateId, String containerSpecId, Capability capability, ContainerConfig containerConfig) {
        ServerTemplate serverTemplate = templateStorage.load(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("No server template found for id " + serverTemplateId);
        }

        ContainerSpec containerSpec = serverTemplate.getContainerSpec(containerSpecId);
        if (containerSpec == null) {
            throw new RuntimeException("No container spec found for id " + containerSpecId + " within server template with id " + serverTemplateId);
        }

        containerSpec.getConfigs().put(capability, containerConfig);

        templateStorage.update(serverTemplate);
    }

    @Override
    public void updateServerTemplateConfig(String serverTemplateId, Capability capability, ServerConfig serverTemplateConfig) {
        ServerTemplate serverTemplate = templateStorage.load(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("No server template found for id " + serverTemplateId);
        }

        serverTemplate.getConfigs().put(capability, serverTemplateConfig);

        templateStorage.update(serverTemplate);
    }


    public KieServerTemplateStorage getTemplateStorage() {
        return templateStorage;
    }

    public void setTemplateStorage(KieServerTemplateStorage templateStorage) {
        this.templateStorage = templateStorage;
    }
}
