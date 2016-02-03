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

import org.kie.server.controller.api.model.spec.ProcessConfig;

public class ProcessConfigImpl implements ProcessConfig {

    private String runtimeStrategy;
    private String kBase;
    private String kSession;
    private String mergeMode;

    @Override
    public String getRuntimeStrategy() {
        return runtimeStrategy;
    }

    @Override
    public String getKBase() {
        return kBase;
    }

    @Override
    public String getKSession() {
        return kSession;
    }

    @Override
    public String getMergeMode() {
        return mergeMode;
    }

    public void setRuntimeStrategy(String runtimeStrategy) {
        this.runtimeStrategy = runtimeStrategy;
    }

    public void setKBase(String kBase) {
        this.kBase = kBase;
    }

    public void setKSession(String kSession) {
        this.kSession = kSession;
    }

    public void setMergeMode(String mergeMode) {
        this.mergeMode = mergeMode;
    }
}
