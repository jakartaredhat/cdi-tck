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
package org.jboss.cdi.tck.tests.decorators.invocation.ejb;

import jakarta.ejb.Stateful;
import jakarta.inject.Inject;

/**
 * @author pmuir
 *
 */
@Stateful
public class PigStyImpl implements PigSty {

    @Inject
    private Pig pig;

    private static boolean BEAN_CALLED;

    public void clean() {
        BEAN_CALLED = true;
        assert pig instanceof Pig;
    }

    public static boolean isBeanCalled() {
        return BEAN_CALLED;
    }

    public static void reset() {
        BEAN_CALLED = false;
    }

}
