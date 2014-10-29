package org.jackey.MavenBuildOrder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	private List<String> modules;
	private List<PomAnalyser> dependencies;

	protected PomAnalyser(String path) {
		this.path = path;
		doc = getDocument(path);
		init();
	}

	private void genGroupId() {
		groupId = root.element("groupId").getText();
	}

	private void genArtifactId() {
		artifactId = root.element("artifactId").getText();
	}

	private void init() {
		root = doc.getRootElement();
		genGroupId();
		genArtifactId();
		genModules();
	}

	private void genModules() {
		Element modulesEle = root.element("modules");
		if (modulesEle != null) {
			List<Element> list = modulesEle.elements("module");
			if (list != null) {
				modules = new ArrayList<String>();
				for (Element ele : list) {
					modules.add(ele.getText());
				}
			}
		}
	}

	public List<MavenModule> genDependencies() {
		Element dependenciesEle = root.element("dependencies");
		if (dependenciesEle != null) {
			List<Element> list = dependenciesEle.elements("dependency");
			if (list != null) {
				List<MavenModule> resultList = new ArrayList<MavenModule>();
				for (int i = 0; i < list.size(); i++) {
					Element dependencyEle = list.get(i);
					String groupId = dependencyEle.element("groupId").getText();
					if (groupId.startsWith("com.ea")) {
						String artifactId = dependencyEle.element("artifactId").getText();
						MavenModule mockModule = new MavenModule(groupId,
								artifactId);
						resultList.add(mockModule);
					}
				}
				return resultList;
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

}
