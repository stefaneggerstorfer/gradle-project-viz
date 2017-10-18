package com.github.steg.projectviz;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.gradle.api.DefaultTask;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.tasks.TaskAction;

import groovy.json.JsonOutput;

public class CreateProjectGraphTask extends DefaultTask {
	public CreateProjectGraphTask() {
		setGroup("Reporting");
		setDescription("Generate an interactive project dependencies HTML report.");
	}

	@TaskAction
	public void exec() throws IOException {
		Map<String, Set<String>> nodes = new HashMap<>();
		Set<Project> projects = getProject().getAllprojects();
		for (Project project : projects) {
			Configuration configuration = project.getConfigurations().findByName("runtime");
			if (configuration != null) {
				DomainObjectSet<ProjectDependency> projectDependencies = configuration.getAllDependencies()
						.withType(ProjectDependency.class);
				Set<String> projectDependenciesNames = projectDependencies.stream()
						.map(ProjectDependency::getName)
						.collect(Collectors.toSet());
				nodes.put(project.getName(), projectDependenciesNames);
			}
		}

		String json = JsonOutput.toJson(nodes);
		Path directory = getProject().getBuildDir().toPath().resolve("projectGraph");
		Files.createDirectories(directory);
		Files.write(directory.resolve("nodes.json"), Collections.singleton(json));

		copyResource(directory, "index.html");
		copyResource(directory, "vis.min.js");
		copyResource(directory, "vis-network.min.css");
	}

	private void copyResource(Path directory, String fileName) throws IOException {
		try (InputStream is = CreateProjectGraphTask.class.getResourceAsStream("/" + fileName)) {
			Files.copy(is, directory.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
		}
	}
}
