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

	public static void main(String[] args) {
		if (args == null || args.length != 1 || args[0].length() <= 0) {
			usage();
		}
		String pomPath = args[0];
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
			Model model = reader.read(new FileReader(pomPath + File.separator
					+ "pom.parent" + File.separator + "pom.xml"));
			Properties p = model.getProperties();
			while (!canOver(p)) {
				for (Entry<Object, Object> entry : p.entrySet()) {
					String key = (String) entry.getKey();
					String value = (String) entry.getValue();
					if (key.equals("com.ea.nucleus.version")) {
						System.out.println();
					}
					if (value.contains("$")) {
						String[] array = value.split("\\$");
						for (int i = 0; i < array.length; i++) {
							String str = array[i];
							if (str.length() > 0) {
								str = str.substring(str.indexOf("{") + 1,
										str.indexOf("}"));
								String tempValue = p.getProperty(str);
								if (tempValue != null) {
									if (!tempValue.contains("$")) {
										value = value.replace("${" + str + "}",
												tempValue);
									}
								}
							}
						}
						p.setProperty(key, value);
					}
					System.out.println(key + "  " + p.getProperty(key));
				}
				System.out
						.println("*********************************************");
				System.out.println(p.getProperty("com.ea.nucleus.version"));
			}
			System.out
			.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			for (Entry<Object, Object> entry : p.entrySet()) {
				String key = (String) entry.getKey();
				System.out.println(key + "  " + p.getProperty(key));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

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
		if (!allModules.contains(module)) {
			allModules.add(module);
		}
	}

	public static void recurseOrder(String pomDir) {
		if (!StringUtil.isNullOrEmpty(pomDir)) {
			pomDir = pomDir + File.separator + "pom.xml";
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

	private static boolean canOver(Properties p) {
		Enumeration e = p.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = p.getProperty(key);
			if (key.startsWith("com.ea")) {
				if (value.contains("$")) {
					System.out
							.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
									+ key + "  " + value);
					return false;
				}
			}
		}
		return true;
	}
}
