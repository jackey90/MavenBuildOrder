package org.jackey.MavenBuildOrder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	public static List<MavenModule> allModules = new ArrayList<MavenModule>();
	public static List<MavenModule> orderModules = new ArrayList<MavenModule>();

	public static void main(String[] args) {
		if (args == null || args.length != 1 || args[0].length() <= 0) {
			usage();
		}
		String pomPath = args[0];
		recursiveModules(pomPath);
		for (int i = 0; i < allModules.size(); i++) {
			recursiceDependencies(allModules.get(i).getPath());
			orderModules.add(allModules.get(i));
			allModules.get(i).setBuilt(true);
		}
		for (MavenModule mavenModule : orderModules) {
			System.out.println(mavenModule.toString());
		}
	}

	public static List<String> getModules(String pomDir, MavenModule module) {
		if (!StringUtil.isNullOrEmpty(pomDir)) {
			String pomPath = pomDir + File.separator + "pom.xml";
			PomAnalyser pa = new PomAnalyser(pomPath);
			String groupId = pa.getGroupId();
			String artifactId = pa.getArtifactId();
			// System.out.println(groupId + "  " + artifactId);
			module.setPath(pomDir);
			module.setGroupId(groupId);
			module.setArtifactId(artifactId);

			List<String> modules = pa.getModules();
			if (modules != null && modules.size() > 0) {
				for (int i = 0; i < modules.size(); i++) {
					modules.set(i, pomDir + File.separator + modules.get(i));
				}
				return modules;
			}
		}
		return null;
	}

	public static void recursiveModules(String pomDir) {
		MavenModule module = new MavenModule();
		List<String> modules = getModules(pomDir, module);
		if (modules != null && modules.size() > 0) {
			for (int i = 0; i < modules.size(); i++) {
				recursiveModules(modules.get(i));
			}
		}
		allModules.add(module);
	}

	public static void recursiceDependencies(String pomDir) {
		if (!StringUtil.isNullOrEmpty(pomDir)) {
			pomDir = pomDir + File.separator + "pom.xml";
			PomAnalyser pa = new PomAnalyser(pomDir);
			List<MavenModule> dependencies = pa.genDependencies();
			if (dependencies != null && dependencies.size() > 0) {
				for (int i = 0; i < dependencies.size(); i++) {
					int j;
					boolean found = false;
					MavenModule denpendency = dependencies.get(i);
					for (j = 0; j < allModules.size(); j++) {
						MavenModule mudule = allModules.get(j);
						if (!mudule.isBuilt() && mudule.equals(denpendency)) {
							recursiceDependencies(mudule.getPath());
							found = true;
							break;
						}
					}
					if (found) {
						orderModules.add(allModules.get(j));
						allModules.get(j).setBuilt(true);
					}
				}
			}
		}
	}

	public static void usage() {
		System.out.println("Usage :");
		System.out.println("java -jar MavenBuildOrder [pom path]");
		System.out
				.println("eg : java -jar MavenBuildOrder D:\\EASAP\\nucleus\\MAIN");
		System.exit(0);
	}
}
