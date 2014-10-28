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
	private String groupId;
	private String artifactId;
	private List<String> modules;

	protected PomAnalyser(Document doc) {
		this.doc = doc;
		init();
	}

	protected PomAnalyser(String path) {
		doc = getDocument(path);
		init();
	}

	public PomAnalyser(File file) {
		doc = getDocument(file);
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
}
