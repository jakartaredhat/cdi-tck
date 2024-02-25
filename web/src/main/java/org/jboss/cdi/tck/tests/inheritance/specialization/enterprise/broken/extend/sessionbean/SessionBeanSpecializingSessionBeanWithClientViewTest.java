/*
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.cdi.tck.tests.inheritance.specialization.enterprise.broken.extend.sessionbean;

import static org.jboss.cdi.tck.TestGroups.INTEGRATION;
import static org.jboss.cdi.tck.cdi.Sections.DIRECT_AND_INDIRECT_SPECIALIZATION;

import jakarta.enterprise.inject.spi.DefinitionException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.ee.WebArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * Bean types of LoginActionBean are LoginAction, Object (all local interfaces of the bean and their superinterfaces).
 * Bean types of MockLoginActionBean are MockLoginActionBean, LoginActionBean, Object (the bean class and all superclasses).
 * Specializing bean must have all bean types of the specialized bean.
 *
 * @author Matus Abaffy
 */
@SpecVersion(spec = "cdi", version = "2.0")
public class SessionBeanSpecializingSessionBeanWithClientViewTest extends AbstractTest {

    @ShouldThrowException(DefinitionException.class)
    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder().withTestClassPackage(SessionBeanSpecializingSessionBeanWithClientViewTest.class).build();
    }

    @Test(groups = INTEGRATION)
    @SpecAssertion(section = DIRECT_AND_INDIRECT_SPECIALIZATION, id = "l")
    public void testDeployment() {
    }

}
