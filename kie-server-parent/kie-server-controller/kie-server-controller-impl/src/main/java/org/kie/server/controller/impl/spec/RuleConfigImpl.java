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

import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.controller.api.model.spec.RuleConfig;

public class RuleConfigImpl implements RuleConfig {

    private Long pollInterval;
    private KieScannerStatus scannerStatus;

    @Override
    public Long getPollInterval() {
        return pollInterval;
    }

    @Override
    public KieScannerStatus getScannerStatus() {
        return scannerStatus;
    }

    public void setPollInterval(Long pollInterval) {
        this.pollInterval = pollInterval;
    }

    public void setScannerStatus(KieScannerStatus scannerStatus) {
        this.scannerStatus = scannerStatus;
    }
}
