/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.codegen.compiler;

import com.google.common.collect.Lists;
import com.synectiks.process.common.plugins.pipelineprocessor.codegen.PipelineClassloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.ToolProvider;
import javax.validation.constraints.NotNull;

public class JavaCompiler {
    private static final Logger log = LoggerFactory.getLogger(JavaCompiler.class);

    private static final javax.tools.JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();

    public JavaCompiler() {
    }

    @NotNull
    private Map<String, byte[]> compileFromSource(@NotNull String className, @NotNull String javaCode) {
        if (JAVA_COMPILER == null) {
            log.error("No compiler present, unable to compile {}", className);
            return Collections.emptyMap();
        }

        final InMemoryFileManager memoryFileManager = new InMemoryFileManager(JAVA_COMPILER.getStandardFileManager(null, null, null));
        List<Diagnostic> errors = Lists.newArrayList();
        JAVA_COMPILER.getTask(null, memoryFileManager, diagnostic -> {
            if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                errors.add(diagnostic);
            }
        }, null, null, Collections.singleton(new JavaSourceFromString(className, javaCode))).call();
        Map<String, byte[]> result = memoryFileManager.getAllClassBytes();
        if (!errors.isEmpty()) {
            throw new PipelineCompilationException(errors);
        }
        return result;
    }

    public Class loadFromString(@NotNull PipelineClassloader classLoader, @NotNull String className, @NotNull String source) throws ClassNotFoundException {
        for (Map.Entry<String, byte[]> entry : compileFromSource(className, source).entrySet()) {
            String name = entry.getKey();
            byte[] bytes = entry.getValue();
            classLoader.defineClass(name, bytes);
        }
        return classLoader.loadClass(className);
    }
}
