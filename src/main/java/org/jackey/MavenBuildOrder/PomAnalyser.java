package org.jackey.MavenBuildOrder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class PomAnalyser {
	private Document doc;
	private Element root;
	private String path;
	private MavenModule thisModule;
	private List<String> modules;
	private Properties properties;
	private static Properties parentProperties;

	protected PomAnalyser(String path) {
		this.path = path;
		doc = getDocument(path + File.separator + "pom.xml");
		init();
	}

	private void init() {
		root = doc.getRootElement();
		if (parentProperties == null) {
			parentProperties = Main.getParentProperties();
		}
		genProperties();
		genThisModule();
		genModules();
	}

	public Properties genProperties() {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
			Model model = reader.read(new FileReader(path + File.separator
					+ "pom.xml"));
			properties = model.getProperties();
			if (properties != null) {
				while (!canOver(properties)) {
					for (Entry<Object, Object> entry : properties.entrySet()) {
						String key = (String) entry.getKey();
						String value = getPropertyValue(properties, key);
					}
				}
			}

//			System.out
//					.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			for (Entry<Object, Object> entry : properties.entrySet()) {
//				String key = (String) entry.getKey();
//				System.out.println(key + "  " + properties.getProperty(key));
//			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		if (parentProperties != null) {
			properties.putAll(parentProperties);
		}
		return properties;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public MavenModule getThisModule() {
		return thisModule;
	}

	public void setThisModule(MavenModule thisModule) {
		this.thisModule = thisModule;
	}

	private void genThisModule() {
		thisModule = new MavenModule();
		thisModule.setGroupId(root.element("groupId").getText());
		thisModule.setArtifactId(root.element("artifactId").getText());
		thisModule.setPath(path);
		String version = root.element("version").getText();
		if (version.contains("$")) {
			if (properties != null) {
				version = getPropertyValue(properties, version);
			}
		}
		thisModule.setVersion(version);

	}

	private void genModules() {
		Element modulesEle = root.element("modules");
		if (notNull(modulesEle)) {
			List<Element> list = modulesEle.elements("module");
			if (notNull(list)) {
				modules = new ArrayList<String>();
				for (Element ele : list) {
					modules.add(ele.getText());
				}
			}
		}
	}

	public MavenModule genMavenModule(Element element) {
		if (notNull(element)) {
			if (notNull(element.element("groupId"))
					&& notNull(element.element("artifactId"))) {
				String groupId = element.element("groupId").getText();
				String artifactId = element.element("artifactId").getText();
				MavenModule mockModule = new MavenModule(groupId, artifactId);
				return mockModule;
			}
		}

		return null;
	}

	public List<MavenModule> getMavenModules(List<Element> list) {
		if (notNull(list)) {
			List<MavenModule> resultList = new ArrayList<MavenModule>();
			for (int i = 0; i < list.size(); i++) {
				Element ele = list.get(i);
				MavenModule mavenModule = genMavenModule(ele);
				if (notNull(mavenModule)) {
					resultList.add(mavenModule);
				}
			}
			return resultList;
		}
		return null;
	}

	public List<MavenModule> genDependencies(Element element) {
		if (notNull(element)) {
			Element dependenciesEle = element.element("dependencies");
			if (notNull(dependenciesEle)) {
				List<Element> list = dependenciesEle.elements("dependency");
				return getMavenModules(list);

			}
		}
		return null;
	}

	public List<MavenModule> getRootDependencies() {
		return genDependencies(root);
	}

	public List<MavenModule> getPlugins() {
		if (notNull(root)) {
			Element buildEle = root.element("build");
			if (notNull(buildEle)) {
				Element pluginsEle = buildEle.element("plugins");
				if (notNull(pluginsEle)) {
					List<Element> plugins = pluginsEle.elements("plugin");
					return getMavenModules(plugins);
				}
			}
		}

		return null;
	}

	public List<MavenModule> getPluginDependencies() {
		if (notNull(root)) {
			Element buildEle = root.element("build");
			if (notNull(buildEle)) {
				Element pluginsEle = buildEle.element("plugins");
				if (notNull(pluginsEle)) {
					List<Element> plugins = pluginsEle.elements("plugin");
					if (notNull(plugins)) {
						List<MavenModule> resultList = new ArrayList<MavenModule>();
						for (int i = 0; i < plugins.size(); i++) {
							List<MavenModule> dependencies = genDependencies(plugins
									.get(i));
							if (notNull(dependencies)) {
								resultList.addAll(dependencies);
							}
						}
						if (resultList.size() > 0) {
							return resultList;
						}
					}
				}
			}
		}
		return null;
	}

	public List<MavenModule> getBuildExtensions() {
		if (notNull(root)) {
			Element buildEle = root.element("build");
			if (notNull(buildEle)) {
				Element extensionsEle = buildEle.element("extensions");
				if (notNull(extensionsEle)) {
					List<Element> extensionList = extensionsEle
							.elements("extension");
					return getMavenModules(extensionList);
				}
			}
		}
		return null;
	}

	public Document getDoc() {
		return doc;
	}

	public Element getRoot() {
		return root;
	}

	public List<String> getModules() {
		return modules;
	}

	private Document getDocument(String path) {
		SAXReader reader = new SAXReader();
		if (path != null && path.endsWith(".xml")) {
			try {
				return reader.read(path);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private Document getDocument(File file) {
		SAXReader reader = new SAXReader();
		if (file != null) {
			try {
				return reader.read(file);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private boolean notNull(Object obj) {
		return obj != null;
	}

	private boolean canOver(Properties p) {
		Enumeration e = p.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = p.getProperty(key);
			if (key.startsWith("com.ea")) {
				if (value.contains("$")) {
					return false;
				}
			}
		}
		return true;
	}

	private String getPropertyValue(Properties properties, String key) {
		String value = properties.getProperty(key);
		if (!key.contains("$") && (value == null || !value.contains("$"))) {
			return value;
		} else {
			if (key.contains("$")) {
				value = key;
			}
			String[] array = value.split("\\$");
			for (int i = 0; i < array.length; i++) {
				String str = array[i];
				if (str.length() > 0) {
					str = str.substring(str.indexOf("{") + 1, str.indexOf("}"));
					String tempValue = getPropertyValue(properties, str);
					if (tempValue != null) {
						if (!tempValue.contains("$")) {
							value = value.replace("${" + str + "}", tempValue);
						}
					}
				}
			}
			properties.setProperty(key, value);
			return value;
		}
	}
}
