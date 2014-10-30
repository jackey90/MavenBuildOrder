package org.jackey.MavenBuildOrder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class PomAnalyser {
	private Document doc;
	private Element root;
	private String path;
	private String groupId;
	private String artifactId;
	private MavenModule thisModule;
	private List<String> modules;
	private Map<String, String> properties;

	protected PomAnalyser(String path) {
		this.path = path;
		doc = getDocument(path);
		init();
	}

	private void init() {
		root = doc.getRootElement();
		genModules();
	}
	
	private void genThisModule(){
		thisModule = new MavenModule();
		thisModule.setGroupId(root.element("groupId").getText());
		thisModule.setArtifactId(root.element("artifactId").getText());
	}
	
	private void genProperties(){
		
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

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PomAnalyser other = (PomAnalyser) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		return true;
	}

	private boolean notNull(Object obj) {
		return obj != null;
	}

}
