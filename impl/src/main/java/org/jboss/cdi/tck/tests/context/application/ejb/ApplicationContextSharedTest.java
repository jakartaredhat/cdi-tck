/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.cdi.tck.tests.context.application.ejb;

import static org.jboss.cdi.tck.TestGroups.CONTEXTS;
import static org.jboss.cdi.tck.TestGroups.JAVAEE_FULL;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.Future;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.EnterpriseArchiveBuilder;
import org.jboss.cdi.tck.util.Timer;
import org.jboss.cdi.tck.util.Timer.StopCondition;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * EJB and related tests with the built-in application context.
 * 
 * @author David Allen
 * @author Martin Kouba
 */
@Test(groups = { JAVAEE_FULL, CONTEXTS })
@SpecVersion(spec = "cdi", version = "20091101")
public class ApplicationContextSharedTest extends AbstractTest {

    @Deployment(name = "TEST", order = 1)
    public static EnterpriseArchive createTestArchive() {
        return new EnterpriseArchiveBuilder().withTestClass(ApplicationContextSharedTest.class).setAsClientMode(false)
                .withClasses(BarBean.class, FMS.class, FMSModelIII.class, SimpleApplicationBean.class, FooRemote.class).build();
    }

    @Deployment(name = "REMOTE_EJB", order = 2, testable = false)
    public static EnterpriseArchive createEjbArchive() {
        return new EnterpriseArchiveBuilder().notTestArchive().noDefaultWebModule().withName("test-ejb.ear")
                .withEjbModuleName("test-ejb.jar").withClasses(FooBean.class, FooRemote.class, SimpleApplicationBean.class)
                .build();
    }

    @EJB
    BarBean bar;

    @EJB(lookup = "java:global/test-ejb/test-ejb/FooBean!org.jboss.cdi.tck.tests.context.application.ejb.FooRemote")
    FooRemote foo;

    @OperateOnDeployment("TEST")
    @Test
    @SpecAssertion(section = "6.7.3", id = "e")
    public void testApplicationContextShared() throws Exception {
        FMSModelIII.reset();
        FMS flightManagementSystem = getInstanceByType(FMS.class);
        flightManagementSystem.climb();

        Timer timer = new Timer().setDelay(20000l).addStopCondition(new StopCondition() {
            public boolean isSatisfied() {
                return FMSModelIII.isClimbed();
            }
        }).start();

        flightManagementSystem.descend();

        timer.addStopCondition(new StopCondition() {
            public boolean isSatisfied() {
                return FMSModelIII.isDescended();
            }
        }, true).start();

        assertTrue(flightManagementSystem.isSameBean());
    }

    @OperateOnDeployment("TEST")
    @Test
    @SpecAssertion(section = "6.7.3", id = "dc")
    public void testApplicationScopeActiveDuringCallToEjbTimeoutMethod() throws Exception {
        FMS flightManagementSystem = getInstanceByType(FMS.class);
        flightManagementSystem.climb();

        new Timer().setDelay(20000l).addStopCondition(new StopCondition() {
            public boolean isSatisfied() {
                return FMSModelIII.isClimbed();
            }
        }).start();

        assertTrue(flightManagementSystem.isApplicationScopeActive());
    }

    @OperateOnDeployment("TEST")
    @Test(dataProvider = ARQUILLIAN_DATA_PROVIDER)
    @SpecAssertions({ @SpecAssertion(section = "6.7.3", id = "db") })
    public void testApplicationScopeActiveDuringAsyncCallToEjb(SimpleApplicationBean simpleApplicationBean) throws Exception {
        Future<Double> result = bar.compute();
        Double id = result.get();
        assertNotEquals(id, -1.00);
        assertEquals(id, simpleApplicationBean.getId());
    }

    @OperateOnDeployment("TEST")
    @Test
    @SpecAssertion(section = "6.7.3", id = "da")
    public void testApplicationScopeActiveDuringRemoteCallToEjb() {
        assertNotNull(foo.ping());
    }

}
