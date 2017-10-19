/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.nativeplatform.toolchain.internal;

import org.gradle.api.tasks.WorkResult;
import org.gradle.language.base.internal.compile.Compiler;
import org.gradle.nativeplatform.toolchain.NativeCompilerVersion;
import org.gradle.util.VersionNumber;

import java.io.File;
import java.util.List;

public class VersionedNativeCompiler<T extends NativeCompileSpec> implements Compiler<T> {

    private final NativeCompilerVersion compilerVersion;
    private final Compiler<T> compiler;
    private final List<File> systemIncludes;

    public VersionedNativeCompiler(Compiler<T> compiler, String type, VersionNumber version, List<File> systemIncludes) {
        this.compiler = compiler;
        this.systemIncludes = systemIncludes;
        this.compilerVersion = new DefaultNativeCompilerVersion(type, version);
    }

    @Override
    public WorkResult execute(T spec) {
        return compiler.execute(spec);
    }

    public NativeCompilerVersion getVersion() {
        return compilerVersion;
    }

    public List<File> getSystemIncludes() {
        return systemIncludes;
    }

}
