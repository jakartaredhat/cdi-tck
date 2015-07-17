/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc., and individual contributors
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
package org.jboss.cdi.tck.tests.event.observer.async.basic;

import static org.jboss.cdi.tck.cdi.Sections.EVENT;
import static org.jboss.cdi.tck.cdi.Sections.FIRING_EVENT_ASYNCHRONOUSLY;
import static org.jboss.cdi.tck.cdi.Sections.OBSERVER_RESOLUTION;
import static org.jboss.cdi.tck.tests.event.observer.async.basic.MixedObservers.MassachusettsInstituteObserver;
import static org.jboss.cdi.tck.tests.event.observer.async.basic.MixedObservers.OxfordUniversityObserver;
import static org.jboss.cdi.tck.tests.event.observer.async.basic.MixedObservers.StandfordUniversityObserver;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.cdi.tck.util.ActionSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

@SpecVersion(spec = "cdi", version = "2.0-EDR1")
public class MixedObserversTest extends AbstractTest {

    @Inject
    Event<ScientificExperiment> event;

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder().withTestClassPackage(MixedObserversTest.class).build();
    }

    @Test
    @SpecAssertions(@SpecAssertion(section = OBSERVER_RESOLUTION, id = "e"))
    public void testSyncEventIsDeliveredOnlyToSyncObservers() {

        ActionSequence.reset();
        event.fire(new ScientificExperiment());
        ActionSequence.assertSequenceDataEquals(MassachusettsInstituteObserver.class, StandfordUniversityObserver.class);
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = OBSERVER_RESOLUTION, id = "f"), @SpecAssertion(section = FIRING_EVENT_ASYNCHRONOUSLY, id = "b"),
            @SpecAssertion(section = EVENT, id = "ef"), @SpecAssertion(section = EVENT, id = "eda") })
    public void testQualifiedAsyncEventIsDelivered() throws InterruptedException {

        BlockingQueue<Experiment> queue = new LinkedBlockingQueue<>();

        event.select(American.AmericanLiteral.INSTANCE).fireAsync(new ScientificExperiment()).thenAccept(queue::offer);
        Experiment experiment = queue.poll(2, TimeUnit.SECONDS);
        assertTrue(experiment.getUniversities().containsAll(Arrays.asList(MixedObservers.class.getClasses())));

    }

    @Test
    @SpecAssertions({@SpecAssertion(section = FIRING_EVENT_ASYNCHRONOUSLY, id = "a")})
    public void testAsyncObserversCalledInDifferentThread() throws InterruptedException {
        BlockingQueue<Experiment> queue = new LinkedBlockingQueue<>();
        int threadId = (int) Thread.currentThread().getId();
        event.fireAsync(new ScientificExperiment()).thenAccept(queue::offer);
        
        Experiment experiment2 = queue.poll(2, TimeUnit.SECONDS);
        assertEquals(experiment2.getUniversities().size(), 3);
        assertTrue(experiment2.getUniversities().contains(MassachusettsInstituteObserver.class));
        assertTrue(experiment2.getUniversities().contains(StandfordUniversityObserver.class));
        assertTrue(experiment2.getUniversities().contains(OxfordUniversityObserver.class));

        assertEquals(threadId, MassachusettsInstituteObserver.threadId.get());
        assertEquals(MassachusettsInstituteObserver.threadId.get(), StandfordUniversityObserver.threadId.get());
        assertNotEquals(StandfordUniversityObserver.threadId.get(), OxfordUniversityObserver.threadId.get());

    }

}
