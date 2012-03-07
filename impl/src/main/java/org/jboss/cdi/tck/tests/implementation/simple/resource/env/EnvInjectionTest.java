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
package org.jboss.cdi.tck.tests.implementation.simple.resource.env;

import static org.jboss.cdi.tck.TestGroups.INTEGRATION;
import static org.jboss.cdi.tck.TestGroups.LIFECYCLE;

import java.io.Serializable;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.util.AnnotationLiteral;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

/**
 * Environment variable injection tests for simple beans.
 * 
 * @author Dan Allen
 */
@Test(groups = INTEGRATION)
@SpecVersion(spec = "cdi", version = "20091101")
public class EnvInjectionTest extends AbstractTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder().withTestClassPackage(EnvInjectionTest.class).withBeansXml("beans.xml")
                .withWebXml("web.xml").build();
    }

    @Test(groups = { LIFECYCLE })
    @SpecAssertion(section = "3.6.1", id = "bb")
    public void testInjectionOfEnv() {
        Bean<GreetingBean> greetingBean = getBeans(GreetingBean.class).iterator().next();
        CreationalContext<GreetingBean> greetingBeanCc = getCurrentManager().createCreationalContext(greetingBean);
        GreetingBean instance = greetingBean.create(greetingBeanCc);
        assert instance.greet() != null;
        assert instance.greet().equals("Hello there my friend");
    }

    @Test(groups = { LIFECYCLE })
    @SpecAssertions({ @SpecAssertion(section = "7.3.6", id = "la"), @SpecAssertion(section = "7.3.6", id = "ma"),
            @SpecAssertion(section = "7.3.6", id = "o") })
    public void testProduceEnvProxy() {
        @SuppressWarnings("serial")
        Bean<String> greetingEnvBean = getBeans(String.class, new AnnotationLiteral<Greeting>() {
        }).iterator().next();
        CreationalContext<String> greetingEnvCc = getCurrentManager().createCreationalContext(greetingEnvBean);
        String greeting = greetingEnvBean.create(greetingEnvCc);
        assert greeting != null;
        assert greeting.equals("Hello there my friend");
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = "3.6.2", id = "aa") })
    public void testResourceBeanTypes() {
        @SuppressWarnings("serial")
        Bean<String> greeting = getBeans(String.class, new AnnotationLiteral<Greeting>() {
        }).iterator().next();
        assert greeting.getTypes().size() == 5;
        assert rawTypeSetMatches(greeting.getTypes(), String.class, Object.class, Serializable.class, Comparable.class,
                CharSequence.class);
    }
}