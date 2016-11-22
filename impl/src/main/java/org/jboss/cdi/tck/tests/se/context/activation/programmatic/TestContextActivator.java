/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.cdi.tck.tests.se.context.activation.programmatic;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.RequestContextController;
import javax.inject.Inject;

@ApplicationScoped
public class TestContextActivator {

    @Inject
    RequestContextController requestContextController;

    @Inject
    RequestScopeCounter requestScopeCounter;


    public boolean activate() {
        return requestContextController.activate();
    }

    public int callRequestScopeBean() {
        return requestScopeCounter.increment();
    }

    public void deactivate() {
        requestContextController.deactivate();
    }

}
