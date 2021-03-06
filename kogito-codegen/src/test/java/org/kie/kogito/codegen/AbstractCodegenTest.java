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

package org.kie.kogito.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.drools.compiler.commons.jci.compilers.CompilationResult;
import org.drools.compiler.commons.jci.compilers.JavaCompiler;
import org.drools.compiler.commons.jci.compilers.JavaCompilerFactory;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.kie.kogito.Application;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.process.ProcessCodegen;
import org.kie.kogito.codegen.rules.RuleCodegen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractCodegenTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractCodegenTest.class);
    @SuppressWarnings("deprecation")
    private static final JavaCompiler JAVA_COMPILER = JavaCompilerFactory.getInstance().loadCompiler(JavaDialectConfiguration.CompilerType.NATIVE, "1.8");

    protected Application generateCodeProcessesOnly(String... processes) throws Exception {
        return generateCode(Arrays.asList(processes), Collections.emptyList());
    }
    
    protected Application generateCodeRulesOnly(String... rules) throws Exception {
        return generateCode(Collections.emptyList(), Arrays.asList(rules));
    }
    
    protected Application generateCode(List<String> processResources, List<String> rulesResources) throws Exception {
        ApplicationGenerator appGen =
                new ApplicationGenerator(this.getClass().getPackage().getName(), new File("target/codegen-tests"))
                                                                                                    .withDependencyInjection(false);
        if (!rulesResources.isEmpty()) {
            appGen.withGenerator(RuleCodegen.ofFiles(Paths.get("src", "test", "resources"),
                                                     rulesResources
                                                                   .stream()
                                                                   .map(resource -> new File("src/test/resources", resource))
                                                                   .collect(Collectors.toList())));
        }

        if (!processResources.isEmpty()) {
            appGen.withGenerator(ProcessCodegen.ofFiles(processResources
                                                                        .stream()
                                                                        .map(resource -> new File("src/test/resources", resource))
                                                                        .collect(Collectors.toList())));
        }

        Collection<GeneratedFile> generatedFiles = appGen.generate();

        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();

        String[] sources = new String[generatedFiles.size()];
        int index = 0;
        for (GeneratedFile entry : generatedFiles) {
            String fileName = entry.relativePath();
            sources[index++] = fileName;
            LOG.debug(new String(entry.contents()));
            srcMfs.write(fileName, entry.contents());
        }

        CompilationResult result = JAVA_COMPILER.compile(sources, srcMfs, trgMfs, this.getClass().getClassLoader());
        assertThat(result).isNotNull();
        assertThat(result.getErrors()).hasSize(0);
        
        TestClassLoader cl = new TestClassLoader(this.getClass().getClassLoader(), trgMfs.getMap());

        @SuppressWarnings("unchecked")
        Class<Application> app = (Class<Application>) Class.forName(this.getClass().getPackage().getName() + ".Application", true, cl);
        
        return app.newInstance();
    }

    private static class TestClassLoader extends URLClassLoader {

        private final Map<String, byte[]> extraClassDefs;

        public TestClassLoader(ClassLoader parent, Map<String, byte[]> extraClassDefs) {
            super(new URL[0], parent);
            this.extraClassDefs = new HashMap<>();

            for (Entry<String, byte[]> entry : extraClassDefs.entrySet()) {
                this.extraClassDefs.put(entry.getKey().replaceAll("/", ".").replaceFirst("\\.class", ""), entry.getValue());
            }
        }

        @Override
        protected Class<?> findClass(final String name) throws ClassNotFoundException {
            byte[] classBytes = this.extraClassDefs.remove(name);
            if (classBytes != null) {
                return defineClass(name, classBytes, 0, classBytes.length);
            }
            return super.findClass(name);
        }

    }
}
