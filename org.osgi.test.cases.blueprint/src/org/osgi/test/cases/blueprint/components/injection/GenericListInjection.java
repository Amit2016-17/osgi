/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.test.cases.blueprint.components.injection;

import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.osgi.test.cases.blueprint.services.BaseTestComponent;

public class GenericListInjection<A extends Double> extends BaseTestComponent {

    /**
     * Simple injection with a single string argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public GenericListInjection() {
        super("GenericListInjection");
    }

    /**
     * Simple injection with a converted array argument.
     *
     * @param componentId
     *               The component identifier used for test verification purposes.
     */
    public GenericListInjection(List<Integer> arg1) {
        super("GenericListInjection");
        setArgumentValue("arg1", arg1, List.class);
    }

    public void setString(List<String> arg1) {
        setPropertyValue("string", arg1, List.class);
    }

    public void setInteger(List<Integer> arg1) {
        setPropertyValue("integer", arg1, List.class);
    }

    public void setClassList(List<Class> arg1) {
        setPropertyValue("classList", arg1, List.class);
    }

    public void setQueue(Queue arg1) {
        setPropertyValue("queue", arg1, Queue.class);
    }

    public void setPointList(LinkedList<Point> arg1) {
        setPropertyValue("pointList", arg1, LinkedList.class);
    }

    public void setExtendsInteger(List<? extends Integer> arg1) {
        setPropertyValue("extendsInteger", arg1, List.class);
    }

    public void setSuperInteger(List<? super Integer> arg1) {
        setPropertyValue("superInteger", arg1, List.class);
    }

    public void setA(List<A> arg1) {
        setPropertyValue("a", arg1, List.class);
    }
}
