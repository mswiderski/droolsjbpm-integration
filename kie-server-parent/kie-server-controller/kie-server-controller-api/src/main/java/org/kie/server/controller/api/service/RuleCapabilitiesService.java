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

package org.kie.server.controller.api.service;

import org.kie.server.api.model.ReleaseId;

public interface RuleCapabilitiesService {

    void scanNow(final String serverTemplateId, final String containerSpecId);

    void startScanner(final String serverTemplateId, final String containerSpecId, long interval);

    void stopScanner(final String serverTemplateId, final String containerSpecId);

    void upgradeContainer(final String serverTemplateId, final String containerSpecId, final ReleaseId releaseId);

}
