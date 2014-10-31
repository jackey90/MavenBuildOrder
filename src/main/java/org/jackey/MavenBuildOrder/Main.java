package org.jackey.MavenBuildOrder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class Main {
	public static List<MavenModule> allModules = new ArrayList<MavenModule>();
	public static List<MavenModule> orderModules = new ArrayList<MavenModule>();
	private static Properties parentProperties;

	public static void main(String[] args) {
		if (args == null || args.length != 1 || args[0].length() <= 0) {
			usage();
		}
		String pomPath = args[0];
		parentProperties = new PomAnalyser(pomPath + File.separator
				+ "pom.parent").genProperties();
		recursiveModules(pomPath);
		for (int i = 0; i < allModules.size(); i++) {
			if (!allModules.get(i).isBuilt()) {
				recurseOrder(allModules.get(i).getPath());
				orderModules.add(allModules.get(i));
				allModules.get(i).setBuilt(true);
			}
		}
		for (MavenModule mavenModule : orderModules) {
			System.out.println(mavenModule.toString());
		}
		
		
	}

	public static Properties getParentProperties() {
		return parentProperties;
	}

	public static void setParentProperties(Properties parentProperties) {
		Main.parentProperties = parentProperties;
	}

	public static List<String> getModules(String pomDir, MavenModule module) {
		if (!StringUtil.isNullOrEmpty(pomDir)) {
			PomAnalyser pa = new PomAnalyser(pomDir);
			module.clone(pa.getThisModule());
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
		if (!allModules.contains(module)) {
			allModules.add(module);
		}
	}

	public static void recurseOrder(String pomDir) {
		if (!StringUtil.isNullOrEmpty(pomDir)) {
			PomAnalyser pa = new PomAnalyser(pomDir);
			List<MavenModule> rootDependencies = pa.getRootDependencies();
			List<MavenModule> plugins = pa.getPlugins();
			List<MavenModule> pluginDependencies = pa.getPluginDependencies();
			List<MavenModule> extensions = pa.getBuildExtensions();
			recurseOrder(rootDependencies);
			recurseOrder(plugins);
			recurseOrder(pluginDependencies);
			recurseOrder(extensions);
		}
	}

	public static void recurseOrder(List<MavenModule> moduleList) {
		if (moduleList != null && moduleList.size() > 0) {
			for (int i = 0; i < moduleList.size(); i++) {
				int j;
				boolean found = false;
				MavenModule mavenModule = moduleList.get(i);
				for (j = 0; j < allModules.size(); j++) {
					MavenModule mudule = allModules.get(j);
					if (!mudule.isBuilt() && mudule.equals(mavenModule)) {
						recurseOrder(mudule.getPath());
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

	public static void usage() {
		System.out.println("Usage :");
		System.out.println("java -jar MavenBuildOrder [pom path]");
		System.out
				.println("eg : java -jar MavenBuildOrder D:\\EASAP\\nucleus\\MAIN");
		System.exit(0);
	}

}
