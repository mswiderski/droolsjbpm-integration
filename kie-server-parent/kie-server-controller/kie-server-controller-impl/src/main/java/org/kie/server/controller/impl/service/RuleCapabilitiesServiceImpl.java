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

import java.util.List;

import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.service.RuleCapabilitiesService;
import org.kie.server.controller.api.storage.KieServerTemplateStorage;
import org.kie.server.controller.impl.KieServerInstanceManager;
import org.kie.server.controller.impl.spec.ContainerSpecImpl;
import org.kie.server.controller.impl.spec.RuleConfigImpl;

public class RuleCapabilitiesServiceImpl implements RuleCapabilitiesService {

    private KieServerTemplateStorage templateStorage;
    private KieServerInstanceManager kieServerInstanceManager;

    @Override
    public void scanNow(String serverTemplateId, String containerSpecId) {

    }

    @Override
    public void startScanner(String serverTemplateId, String containerSpecId, long interval) {

        ServerTemplate serverTemplate = templateStorage.load(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("No server template found for id " + serverTemplateId);
        }

        ContainerSpec containerSpec = serverTemplate.getContainerSpec(containerSpecId);

        ContainerConfig containerConfig = containerSpec.getConfigs().get(Capability.RULE);
        if (containerConfig == null) {
            containerConfig = new RuleConfigImpl();
            containerSpec.getConfigs().put(Capability.RULE, containerConfig);
        }

        ((RuleConfigImpl) containerConfig).setPollInterval(interval);
        ((RuleConfigImpl) containerConfig).setScannerStatus(KieScannerStatus.STARTED);

        templateStorage.update(serverTemplate);

        List<Container> containers = kieServerInstanceManager.startScanner(serverTemplate, containerSpec, interval);
    }

    @Override
    public void stopScanner(String serverTemplateId, String containerSpecId) {
        ServerTemplate serverTemplate = templateStorage.load(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("No server template found for id " + serverTemplateId);
        }

        ContainerSpec containerSpec = serverTemplate.getContainerSpec(containerSpecId);

        ContainerConfig containerConfig = containerSpec.getConfigs().get(Capability.RULE);
        if (containerConfig == null) {
            containerConfig = new RuleConfigImpl();
            containerSpec.getConfigs().put(Capability.RULE, containerConfig);
        }

        ((RuleConfigImpl) containerConfig).setPollInterval(null);
        ((RuleConfigImpl) containerConfig).setScannerStatus(KieScannerStatus.STOPPED);

        templateStorage.update(serverTemplate);

        List<Container> containers = kieServerInstanceManager.stopScanner(serverTemplate, containerSpec);
    }

    @Override
    public void upgradeContainer(String serverTemplateId, String containerSpecId, ReleaseId releaseId) {
        ServerTemplate serverTemplate = templateStorage.load(serverTemplateId);
        if (serverTemplate == null) {
            throw new RuntimeException("No server template found for id " + serverTemplateId);
        }

        ContainerSpec containerSpec = serverTemplate.getContainerSpec(containerSpecId);

        ((ContainerSpecImpl)containerSpec).setReleasedId(releaseId);

        templateStorage.update(serverTemplate);

        List<Container> containers = kieServerInstanceManager.upgradeContainer(serverTemplate, containerSpec);
    }

    public KieServerTemplateStorage getTemplateStorage() {
        return templateStorage;
    }

    public void setTemplateStorage(KieServerTemplateStorage templateStorage) {
        this.templateStorage = templateStorage;
    }

    public KieServerInstanceManager getKieServerInstanceManager() {
        return kieServerInstanceManager;
    }

    public void setKieServerInstanceManager(KieServerInstanceManager kieServerInstanceManager) {
        this.kieServerInstanceManager = kieServerInstanceManager;
    }
}
