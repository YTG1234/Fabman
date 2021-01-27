package io.github.ytg1234.fabman.dataspec;

import java.util.Map;

public class PackageStorage {
	private Map<String, FabmanPackage> packages;

	public PackageStorage(Map<String, FabmanPackage> packages) {
		this.packages = packages;
	}

	public Map<String, FabmanPackage> getPackages() {
		return packages;
	}
}
