/*
 * Copyright (C) 2016 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.utils.env;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 *
 * @author jjYBdx4IL
 */
public class Maven extends JavaProcess {

    public static final String REL_TGT_DIR = "target";

    public static String getMavenBasedir() {
        return System.getProperty("basedir");
    }

    public static File getMavenTargetDir() {
        return new File(getMavenBasedir(), REL_TGT_DIR);
    }

    /**
     * Use class file location on disk to find maven project basedir. This only
     * works for classes that are loaded from directories below the basedir, ie.
     * not via classes loaded from jar files. The directory structure is then
     * followed upwards until the maven project descriptor file pom.xml is
     * found.
     * 
     * @param classRef
     * @throws IllegalStateException
     *             if no pom.xml was found
     * @return
     */
    public static URI getBasedir(Class<?> classRef) {
        URL classLocURL = classRef.getClassLoader().getResource(classRef.getName().replace('.', '/') + ".class");
        if (classLocURL == null) {
            throw new IllegalStateException(
                    "class " + classRef.getName() + " not found on its classloader's classpath");
        }
        String classLoc = classLocURL.toExternalForm();
        String classpathEntry = classLoc.substring(0, classLoc.length() - classRef.getName().length() - 6);
        if (!classpathEntry.startsWith("file:/")) {
            throw new IllegalStateException("class " + classRef.getName()
                    + " has been loaded from resource not starting with file:/: " + classpathEntry);
        }
        String cpEntryFileLoc = classpathEntry.substring("file:/".length());
        File projectBaseDir = new File(cpEntryFileLoc).getParentFile();
        while (projectBaseDir != null && !new File(projectBaseDir, "pom.xml").exists()) {
            projectBaseDir = projectBaseDir.getParentFile();
        }
        if (projectBaseDir == null) {
            throw new IllegalStateException("maven project basedir not found for " + classRef.getName()
                    + " because no parent directory of " + cpEntryFileLoc + " has a pom.xml entry!");
        }
        return projectBaseDir.toURI();
    }
}
