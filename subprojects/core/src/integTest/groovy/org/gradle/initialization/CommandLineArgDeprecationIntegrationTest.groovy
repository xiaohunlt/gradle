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

package org.gradle.initialization

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import spock.lang.Unroll

class CommandLineArgDeprecationIntegrationTest extends AbstractIntegrationSpec {
    @Unroll
    def "deprecation warning appears when using #deprecatedArgs"() {
        when:
        executer.expectDeprecationWarning()
        args(deprecatedArgs)

        then:
        succeeds('help')
        outputContains(message)

        where:
        issue                                          | deprecatedArgs        | message
        'https://github.com/gradle/gradle/issues/1425' | '--recompile-scripts' | '--recompile-scripts has been deprecated and is scheduled to be removed in Gradle'
        'https://github.com/gradle/gradle/issues/3077' | '--no-rebuild'        | '--no-rebuild/-a has been deprecated and is scheduled to be removed in Gradle'
        'https://github.com/gradle/gradle/issues/3077' | '-a'                  | '--no-rebuild/-a has been deprecated and is scheduled to be removed in Gradle'
    }
}
