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

package org.kie.server.controller.api.model.spec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.server.controller.api.model.spec.ProcessConfig;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "process-config")
public class ProcessConfig extends ContainerConfig {

    @XmlElement(name = "strategy")
    private String runtimeStrategy;
    @XmlElement(name = "kie-base-name")
    private String kBase;
    @XmlElement(name = "kie-session-name")
    private String kSession;
    @XmlElement(name = "merge-mode")
    private String mergeMode;

    public String getRuntimeStrategy() {
        return runtimeStrategy;
    }

    public String getKBase() {
        return kBase;
    }

    public String getKSession() {
        return kSession;
    }

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
