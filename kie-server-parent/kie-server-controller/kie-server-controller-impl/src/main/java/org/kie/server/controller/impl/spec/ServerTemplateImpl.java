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

package org.kie.server.controller.impl.spec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;

public class ServerTemplateImpl extends ServerTemplateKeyImpl implements ServerTemplate {

    private Collection<ContainerSpec> containersSpec = new ArrayList<ContainerSpec>();

    private Map<Capability, ServerConfig> configs = new HashMap<Capability, ServerConfig>();

    private Collection<String> serverInstances = new ArrayList<String>();

    @Override
    public Map<Capability, ServerConfig> getConfigs() {
        if (configs == null) {
            configs = new HashMap<Capability, ServerConfig>();
        }
        return configs;
    }

    @Override
    public Collection<ContainerSpec> getContainersSpec() {
        if (containersSpec == null) {
            containersSpec = new ArrayList<ContainerSpec>();
        }
        return Collections.unmodifiableCollection(containersSpec);
    }

    @Override
    public Collection<String> getServerInstances() {
        if (serverInstances == null) {
            serverInstances = new ArrayList<String>();
        }
        return serverInstances;
    }

    @Override
    public boolean hasContainerSpec(String containerSpecId) {
        for (ContainerSpec spec : getContainersSpec()) {
            if (containerSpecId.equals(spec.getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ContainerSpec getContainerSpec(String containerSpecId) {
        for (ContainerSpec spec : getContainersSpec()) {
            if (containerSpecId.equals(spec.getId())) {
                return spec;
            }
        }

        return null;
    }

    @Override
    public void addContainerSpec(ContainerSpec containerSpec) {
        getContainersSpec().add(containerSpec);
    }

    @Override
    public void deleteContainerSpec(String containerSpecId) {
        Iterator<ContainerSpec> iterator = getContainersSpec().iterator();

        while(iterator.hasNext()) {
            ContainerSpec spec = iterator.next();
            if (containerSpecId.equals(spec.getId())) {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean hasServerInstance(String location) {
        return getServerInstances().contains(location);
    }

    @Override
    public void addServerInstance(String location) {
        if (hasServerInstance(location)) {
            return;
        }
        getServerInstances().add(location);
    }

    @Override
    public void deleteServerInstance(String location) {
        getServerInstances().remove(location);
    }

    public void setContainersSpec(Collection<ContainerSpec> containersSpec) {
        this.containersSpec = containersSpec;
    }

    public void setConfigs(Map<Capability, ServerConfig> configs) {
        this.configs = configs;
    }
}
