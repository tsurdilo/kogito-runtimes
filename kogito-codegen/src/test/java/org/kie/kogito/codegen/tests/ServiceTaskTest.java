/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.codegen.AbstractCodegenTest;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;


public class ServiceTaskTest extends AbstractCodegenTest {

    
    @Test
    public void testBasicServiceProcessTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/ServiceProcess.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("ServiceProcess");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "john");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("s");
        assertThat(result.toMap().get("s")).isNotNull().isEqualTo("Hello john!");
    }
    
    @Test
    public void testServiceProcessDifferentOperationsTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/ServiceProcessDifferentOperations.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("ServiceProcessDifferentOperations");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "john");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("s");
        assertThat(result.toMap().get("s")).isNotNull().isEqualTo("Goodbye Hello john!!");
    }
    
    @Test
    public void testServiceProcessSameOperationsTask() throws Exception {
        
        Application app = generateCodeProcessesOnly("servicetask/ServiceProcessSameOperations.bpmn2");        
        assertThat(app).isNotNull();
                
        Process<? extends Model> p = app.processes().processById("ServiceProcessSameOperations");
        
        Model m = p.createModel();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("s", "john");
        m.fromMap(parameters);
        
        ProcessInstance<?> processInstance = p.createInstance(m);
        processInstance.start();
        
        assertThat(processInstance.status()).isEqualTo(org.kie.api.runtime.process.ProcessInstance.STATE_COMPLETED); 
        Model result = (Model)processInstance.variables();
        assertThat(result.toMap()).hasSize(1).containsKeys("s");
        assertThat(result.toMap().get("s")).isNotNull().isEqualTo("Hello Hello john!!");
    }
}
