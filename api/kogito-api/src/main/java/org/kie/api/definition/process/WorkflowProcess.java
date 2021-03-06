/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.definition.process;

/**
 * A WorkflowProcess is a type of Process that uses a flow chart (as a collection of Nodes and Connections)
 * to model the business logic.
 */
public interface WorkflowProcess
    extends
    Process,
    NodeContainer {

    public static final String PUBLIC_VISIBILITY = "Public";
    public static final String PRIVATE_VISIBILITY = "Private";
    public static final String NONE_VISIBILITY = "None";
    
    String getVisibility();
}
