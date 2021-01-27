package io.github.ytg1234.fabman.config;

import io.github.ytg1234.fabman.Dsl;

public class FabmanConfig {
	private Dsl dsl;
	private boolean multiProject;
	private boolean applyToRoot;

	public FabmanConfig(Dsl dsl, boolean multiProject, boolean applyToRoot) {
		this.dsl = dsl;
		this.multiProject = multiProject;
		this.applyToRoot = applyToRoot;
	}

	public Dsl getDsl() {
		return dsl;
	}

	public boolean isMultiProject() {
		return multiProject;
	}

	public boolean isApplyToRoot() {
		return applyToRoot;
	}
}
