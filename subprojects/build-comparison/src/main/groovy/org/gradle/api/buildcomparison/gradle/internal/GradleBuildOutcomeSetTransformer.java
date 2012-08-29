/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.api.buildcomparison.gradle.internal;

import org.gradle.api.Transformer;
import org.gradle.api.internal.filestore.FileStore;
import org.gradle.api.internal.filestore.FileStoreEntry;
import org.gradle.api.buildcomparison.outcome.internal.BuildOutcome;
import org.gradle.api.buildcomparison.outcome.internal.archive.GeneratedArchiveBuildOutcome;
import org.gradle.api.buildcomparison.outcome.internal.unknown.UnknownBuildOutcome;
import org.gradle.tooling.internal.provider.FileOutcomeIdentifier;
import org.gradle.tooling.model.internal.outcomes.GradleFileBuildOutcome;
import org.gradle.tooling.model.internal.outcomes.GradleBuildOutcome;
import org.gradle.tooling.model.internal.outcomes.ProjectOutcomes;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Transforms from the Gradle specific build outcomes into source agnostic outcomes.
 */
public class GradleBuildOutcomeSetTransformer implements Transformer<Set<BuildOutcome>, ProjectOutcomes> {

    private final FileStore<String> fileStore;

    private final List<String> zipArchiveTypes = Arrays.asList(
            FileOutcomeIdentifier.JAR_ARTIFACT.getTypeIdentifier(),
            FileOutcomeIdentifier.EAR_ARTIFACT.getTypeIdentifier(),
            FileOutcomeIdentifier.WAR_ARTIFACT.getTypeIdentifier(),
            FileOutcomeIdentifier.ZIP_ARTIFACT.getTypeIdentifier()
    );

    public GradleBuildOutcomeSetTransformer(FileStore<String> fileStore) {
        this.fileStore = fileStore;
    }

    public Set<BuildOutcome> transform(ProjectOutcomes rootProject) {
        Set<BuildOutcome> keyedOutcomes = new HashSet<BuildOutcome>();
        addBuildOutcomes(rootProject, rootProject, keyedOutcomes);
        return keyedOutcomes;
    }

    private void addBuildOutcomes(ProjectOutcomes projectOutcomes, ProjectOutcomes rootProject, Set<BuildOutcome> buildOutcomes) {
        for (GradleBuildOutcome outcome : projectOutcomes.getOutcomes()) {
            if (outcome instanceof GradleFileBuildOutcome) {
                addFileBuildOutcome((GradleFileBuildOutcome) outcome, rootProject, buildOutcomes);
            } else {
                new UnknownBuildOutcome(outcome.getTaskPath(), outcome.getDescription());
            }
        }

        for (ProjectOutcomes childProject : projectOutcomes.getChildren()) {
            addBuildOutcomes(childProject, rootProject, buildOutcomes);
        }
    }

    private void addFileBuildOutcome(GradleFileBuildOutcome outcome, ProjectOutcomes rootProject, Set<BuildOutcome> translatedOutcomes) {
        if (zipArchiveTypes.contains(outcome.getTypeIdentifier())) {
            File originalFile = outcome.getFile();
            String filestoreDestination = String.format("%s/%s", outcome.getTaskPath(), originalFile.getName());
            FileStoreEntry fileStoreEntry = fileStore.move(filestoreDestination, originalFile);
            File storedFile = fileStoreEntry.getFile();
            String relativePath = rootProject.getProjectDirectory().toURI().relativize(originalFile.toURI()).getPath();
            BuildOutcome buildOutcome = new GeneratedArchiveBuildOutcome(outcome.getTaskPath(), outcome.getDescription(), storedFile, relativePath);
            translatedOutcomes.add(buildOutcome);
        } else {
            translatedOutcomes.add(new UnknownBuildOutcome(outcome.getTaskPath(), outcome.getDescription()));
        }
    }

}
