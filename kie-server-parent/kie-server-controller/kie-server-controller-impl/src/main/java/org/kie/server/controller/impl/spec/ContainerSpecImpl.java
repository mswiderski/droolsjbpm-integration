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

import java.util.HashMap;
import java.util.Map;

import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;

public class ContainerSpecImpl extends ContainerSpecKeyImpl implements ContainerSpec {

    private ReleaseId releasedId;
    private Map<Capability, ContainerConfig> configs = new HashMap<Capability, ContainerConfig>();

    @Override
    public ReleaseId getReleasedId() {
        return releasedId;
    }

    @Override
    public Map<Capability, ContainerConfig> getConfigs() {
        if (configs == null) {
            configs = new HashMap<Capability, ContainerConfig>();
        }
        return configs;
    }

    public void setReleasedId(ReleaseId releasedId) {
        this.releasedId = releasedId;
    }

    public void setConfigs(Map<Capability, ContainerConfig> configs) {
        this.configs = configs;
    }

    public void addConfig(Capability capability, ContainerConfig config) {
        this.configs.put(capability, config);
    }
}
