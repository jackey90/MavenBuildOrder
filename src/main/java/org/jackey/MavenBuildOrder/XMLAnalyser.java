package org.jackey.MavenBuildOrder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class XMLAnalyser {
	protected final Document doc;

	protected XMLAnalyser(Document doc) {
		this.doc = doc;
	}

	protected XMLAnalyser(String path) {
		doc = getDocument(path);
	}

	public XMLAnalyser(File file) {
		doc = getDocument(file);
	}

	@SuppressWarnings("unchecked")
	public List<Node> getNodeList(String nodePath) {
		if (doc != null) {
			return doc.selectNodes(nodePath);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public List<String> getNodeStringList(String nodePath) {
		if (doc != null) {
			List nodeList = doc.selectNodes(nodePath);
			if (nodeList != null && nodeList.size() > 0) {
				List<String> list = new ArrayList<String>();
				for (Object node : nodeList) {
					if (((Node) node).getText() != null) {
						list.add(((Node) node).getText());
					}
				}

				return list;

			}
		}

		return null;
	}

	public String getNodeString(String nodePath) {
		if (doc != null) {
			Node node = doc.selectSingleNode(nodePath);
			if (node != null) {
				return node.getText();
			}
		}
		return null;
	}

	public void warning(String str) {
		PrintUtil.warning(str);
	}

	public void error(String str) {
		PrintUtil.error(str);
	}

	public boolean isNullOrEmpty(String str) {
		return StringUtil.isNullOrEmpty(str);
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
