package main.model;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;

public class XMLProcessor {
    private Document xmlFile;
    private Element xmlRootElement;
    private TreeWalker nodeTreeWalker;
    private NodeFilter nodeFilter;

    public XMLProcessor() {
        this.nodeFilter = (node) -> {
            if (node.getNodeType() == Node.ELEMENT_NODE || node.getParentNode().getNodeName().equals("rank"))
                return NodeFilter.FILTER_ACCEPT;
            else
                return NodeFilter.FILTER_REJECT; };
    }

    public void setXMLFile(Document xmlFile) {
        this.xmlFile = xmlFile;
        this.xmlRootElement = xmlFile.getDocumentElement();
        this.nodeTreeWalker = ((DocumentTraversal) this.xmlFile).createTreeWalker(
                this.xmlRootElement, NodeFilter.SHOW_ALL, nodeFilter, false);
    }

    public TreeWalker getNodeTreeWalker() {
        return this.nodeTreeWalker;
    }
}
