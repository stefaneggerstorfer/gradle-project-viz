package com.github.steg.projectviz;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectVizPlugin implements Plugin<Project> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectVizPlugin.class);
	
	private static final String TASK_NAME = "createProjectGraph";

	@Override
	public void apply(Project project) {
		LOGGER.debug("Adding task " + TASK_NAME);
		project.getTasks().create(TASK_NAME, CreateProjectGraphTask.class);
	}

}
