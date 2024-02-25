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
package org.jboss.cdi.tck.shrinkwrap.ee;

import jakarta.ejb.Stateless;

/**
 * Dummy session bean used to make every EJB module <i>true</i> EJB module (JBoss AS7 sub-deployment).
 *
 * See also <a href="http://community.jboss.org/message/623471?tstart=0">Test EAR deploys on AS7, but test config can't be
 * found</a> or <a href="http://community.jboss.org/message/615412">War classes cannot see EJB classes</a> threads.
 *
 * @author Martin Kouba
 */
@Stateless(name = "org.jboss.cdi.tck.shrinkwrap.DummySessionBean")
public class DummySessionBean {

}
