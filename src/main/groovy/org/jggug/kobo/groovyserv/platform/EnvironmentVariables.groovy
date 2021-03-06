/*
 * Copyright 2009-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jggug.kobo.groovyserv.platform

import org.jggug.kobo.groovyserv.utils.LogUtils

/**
 * @author UEHARA Junji
 * @author NAKANO Yasuharu
 */
@Singleton
class EnvironmentVariables {

    private final cache = [:]
    private final origGetenv = System.metaClass.getMetaMethod("getenv", [String] as Object[])
    private final origGetenvAll = System.metaClass.getMetaMethod("getenv", null)

    /**
     * Initializes something necessary.
     */
    static void setUp() {
        EnvironmentVariables.instance.replaceSystemGetenv()
    }

    /**
     * Replace System.getenv by the platform native method.
     */
    private void replaceSystemGetenv() {
        // for System.getenv("xxx")
        System.metaClass.'static'.getenv = { String envVarName ->
            String value = cache[envVarName]
            if (value == null) {
                value = origGetenv.doMethodInvoke(System, envVarName)
            }
            LogUtils.debugLog "getenv(${envVarName}) => $value"
            return value
        }
        // for System.getenv()["xxx"] or System.getenv().xxx
        System.metaClass.'static'.getenv = { ->
            def envMap = new HashMap(origGetenvAll.doMethodInvoke(System))
            envMap.putAll(cache) // overwritten by cache entries
            LogUtils.debugLog "getenv() => $envMap"
            return envMap
        }
        // for System.env["xxx"] or System.env.xxx
        System.metaClass.'static'.getEnv = { ->
            def envMap = new HashMap(origGetenvAll.doMethodInvoke(System))
            envMap.putAll(cache) // overwritten by cache entries
            LogUtils.debugLog "getenv() => $envMap"
            return envMap
        }
        LogUtils.debugLog "System.getenv is replaced"
    }

    /**
     * Sets the environment variable by using a platform native method.
     * This method is called by groovyserver before invoking Groovy script.
     *
     * @param envVar 'NAME=VALUE' style environment variable information.
     */
    void put(String envVar) {
        // Appling to native platform environment variables
        // for subprocess which is maybe invoked by user's Groovy script
        PlatformMethods.putenv(envVar)

        // Caching for next call of System.getenv
        def tokens = envVar.split('=', 2)
        def name = tokens[0]
        def value = (tokens.size() == 1) ? null : tokens[1]
        cache[name] = value
        LogUtils.debugLog "putenv(${name}, ${value})"
    }
}
