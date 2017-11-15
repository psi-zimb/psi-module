/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.bahmni.module.bahmnipsi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.ModuleActivator;

public class BasicModuleActivator implements ModuleActivator {

    private Log log = LogFactory.getLog(this.getClass());

    public void startup() {
        log.info("Starting bahmnipsi Module");
    }

    public void shutdown() {
        log.info("Shutting down bahmnipsi Module");
    }

    @Override
    public void willRefreshContext() {
        log.info("Will contextRefreshed bahmnipsi Module");
    }

    @Override
    public void contextRefreshed() {
        log.info("contextRefreshed bahmnipsi Module");
    }

    @Override
    public void willStart() {
        log.info("Will Start bahmnipsi Module");
    }

    @Override
    public void started() {
        log.info("Started bahmnipsi Module");
    }

    @Override
    public void willStop() {
        log.info("Will be shutting down bahmnipsi Module");
    }

    @Override
    public void stopped() {
        log.info("Shutdown bahmnipsi Module");
    }
}
