package io.github.ytg1234.fabman.dataspec;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FabmanPackage {
	private String name;
	private String mavenUrl;
	private String group;
	private String artifact;
	private List<String> configurations;

	public FabmanPackage(@Nullable String name, String mavenUrl, String group, String artifact, List<String> configurations) {
		this.name = name;
		this.mavenUrl = mavenUrl;
		this.group = group;
		this.artifact = artifact;
		this.configurations = configurations;
	}

	@NotNull
	public String getGroup() {
		return group;
	}

	@NotNull
	public String getArtifact() {
		return artifact;
	}

	@Nullable
	public String getName() {
		return name;
	}

	@NotNull
	public String getMavenUrl() {
		return mavenUrl;
	}

	@Override
	public String toString() {
		return "Package slug is " + name + ", Gradle ID is " + group + ":" + artifact + ":<version>, and required maven repo is " + mavenUrl;
	}

	public List<String> getConfigurations() {
		return configurations;
	}
}
