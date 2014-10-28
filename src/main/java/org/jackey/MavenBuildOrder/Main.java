package org.jackey.MavenBuildOrder;

import java.io.File;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		String pomPath = "D:\\jackey\\EA\\REL270.0";
		recursiveModules(pomPath);

	}

	public static List<String> getModules(String pomDir) {
		if (!StringUtil.isNullOrEmpty(pomDir)) {
			String pomPath = pomDir + File.separator + "pom.xml";
			PomAnalyser pa = new PomAnalyser(pomPath);
			String groupId = pa.getGroupId();
			String artifactId = pa.getArtifactId();
			System.out.println(groupId + "  " + artifactId);
			List<String> modules = pa.getModules();
			if (modules != null && modules.size() > 0) {
				System.out.println("not null");
				for (int i = 0; i < modules.size(); i++) {
					modules.set(i, pomDir + File.separator + modules.get(i));
				}
				return modules;
			}
		}

		return null;
	}

	public static void recursiveModules(String pomDir) {
		List<String> modules = getModules(pomDir);
		if (modules != null && modules.size() > 0) {
			for (int i = 0; i < modules.size(); i++) {
				recursiveModules(modules.get(i));
			}
		}
	}
}
