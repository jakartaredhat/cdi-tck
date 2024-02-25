/*
 * Copyright 2021, Red Hat, Inc., and individual contributors
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
package org.jboss.cdi.tck.tests.full.implementation.producer.method.lifecycle;

import static org.jboss.cdi.tck.TestGroups.CDI_FULL;
import static org.jboss.cdi.tck.cdi.Sections.PRODUCER_OR_DISPOSER_METHODS_INVOCATION;
import static org.jboss.cdi.tck.cdi.Sections.SPECIALIZATION;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.util.AnnotationLiteral;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

@SpecVersion(spec = "cdi", version = "2.0")
@Test(groups = CDI_FULL)
public class ProducerMethodLifecycleTest extends AbstractTest {

    private AnnotationLiteral<Pet> PET_LITERAL = new Pet.Literal();

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder().withTestClassPackage(ProducerMethodLifecycleTest.class)
                .build();
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = PRODUCER_OR_DISPOSER_METHODS_INVOCATION, id = "c"),
            @SpecAssertion(section = SPECIALIZATION, id = "cb") })
    public void testProducerMethodFromSpecializedBeanUsed() {
        SpiderProducer.reset();
        PreferredSpiderProducer.reset();
        Bean<Tarantula> spiderBean = getBeans(Tarantula.class, PET_LITERAL).iterator().next();
        CreationalContext<Tarantula> spiderBeanCc = getCurrentManager().createCreationalContext(spiderBean);
        Tarantula tarantula = spiderBean.create(spiderBeanCc);
        assert PreferredSpiderProducer.getTarantulaCreated() == tarantula;
        assert !SpiderProducer.isTarantulaCreated();
    }
}
