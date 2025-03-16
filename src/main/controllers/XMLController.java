package main.controllers;

import main.enums.CharacterClass;
import main.model.*;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.TreeWalker;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class XMLController {
    private static DocumentBuilderFactory docBuildFactory;

    static {
        docBuildFactory = DocumentBuilderFactory.newInstance();
        docBuildFactory.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    }

    private XMLProcessor xmlProcessor;

    public XMLController() {
        this.xmlProcessor = new XMLProcessor();
    }

    public CustomizationSet parse(File dataSetFolder) throws Exception {
        CustomizationSet customizationSet = new CustomizationSet(dataSetFolder);
        ArrayList<File> classDataFiles = new ArrayList<>(Arrays.stream(dataSetFolder.listFiles()).filter(
                file -> Arrays.stream(CharacterClass.values()).anyMatch(
                    characterClass -> file.getName().substring(0, file.getName().indexOf(".xml")).equalsIgnoreCase(characterClass.name())
                )
        ).toList());

        for (File classDataFile : classDataFiles) {
            String className = classDataFile.getName().substring(0, classDataFile.getName().indexOf(".xml"));
            customizationSet.addClassData(this.parse(
                    CharacterClass.valueOf(className.toUpperCase()),
                    classDataFile.toURL().openStream()));
        }

        return customizationSet;
    }
    public CustomizationSet parse(HashMap<CharacterClass, InputStream> dataFileStreams) {
        CustomizationSet defaultDataSet = new CustomizationSet();

        dataFileStreams.forEach((cls, dataStream) -> {
            try {
                defaultDataSet.addClassData(this.parse(cls, dataStream));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        return defaultDataSet;
    }

    private ClassData parse(CharacterClass cls, InputStream dataStream) throws Exception {
        ClassData classData = new ClassData(cls);
        ArrayList<ItemContainer> dataItemContainers = new ArrayList<>();

        this.xmlProcessor.setXMLFile(docBuildFactory.newDocumentBuilder().parse(dataStream));
        TreeWalker nodeTreeWalker = this.xmlProcessor.getNodeTreeWalker();
        Node currentNode = nodeTreeWalker.getCurrentNode();
        int latestContainerId = -1;
        int latestTalentIndex = -1;

        while (currentNode != null) {
            if (currentNode.hasAttributes()) {
                switch (currentNode.getNodeName()) {
                    case "tree" -> {
                        latestContainerId++;

                        TalentTree talentTree = this.constructTalentTree(latestContainerId, currentNode);
                        talentTree.setBackground(cls.name() + talentTree.getName() + ".jpg");

                        dataItemContainers.add(talentTree);
                    }
                    case "talent" -> {
                        Talent talent = this.constructTalent(currentNode);

                        latestTalentIndex = talent.getIndex();
                        dataItemContainers.get(latestContainerId).addItem(talent);
                    }
                    case "note" -> {
                        Note note = this.constructNote(currentNode);

                        dataItemContainers.get(latestContainerId).addItem(note);
                    }
                    case "rank" -> {
                        ((Talent) dataItemContainers.get(latestContainerId).getItemByIndex(latestTalentIndex)).
                            getRankData().add(currentNode.getTextContent());
                    }
                }
            } else if (currentNode.getNodeName().equals("notes")) {
                latestContainerId++;

                dataItemContainers.add(new NoteCollection(latestContainerId));
            }

            currentNode = nodeTreeWalker.nextNode();
        }

        classData.setItemContainers(dataItemContainers);

        return classData;
    }

    public void writeToXML(CustomizationSet customDataSet, ClassData classData) throws IOException, XMLStreamException, TransformerException {
        StringWriter writer = new StringWriter();
        XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);

        xmlWriter.writeStartDocument("1.1");
        xmlWriter.writeStartElement("class");
        xmlWriter.writeAttribute("name", classData.getName());
        for (TalentTree tree : classData.getTalentTrees()) {
            xmlWriter.writeStartElement("tree");
            xmlWriter.writeAttribute("name", String.valueOf(tree.getName()));

            for (Talent talent : tree.getTalents().values()) {
                xmlWriter.writeStartElement("talent");
                xmlWriter.writeAttribute("index", String.valueOf(talent.getIndex()));
                xmlWriter.writeAttribute("name", talent.getItemName());
                xmlWriter.writeAttribute("icon", talent.getIconName());

                if (talent.hasPrerequisite()) xmlWriter.writeAttribute("requires", String.valueOf(talent.getPrerequisiteIndex()));

                for (int i = 0; i < talent.getRankData().size(); i++) {
                    xmlWriter.writeStartElement("rank");
                    xmlWriter.writeAttribute("index", String.valueOf(i + 1));
                    xmlWriter.writeCData(talent.getRankData().get(i));
                    xmlWriter.writeEndElement();
                }
                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        xmlWriter.writeStartElement("notes");
        for (Note note : classData.getNoteCollection().getNotes().values()) {
            xmlWriter.writeStartElement("note");
            xmlWriter.writeAttribute("index", String.valueOf(note.getIndex()));
            xmlWriter.writeAttribute("name", note.getItemName());
            xmlWriter.writeAttribute("icon", note.getIconName());
            xmlWriter.writeCData(note.getDescription());
            xmlWriter.writeEndElement();
        }
        xmlWriter.writeEndElement();

        xmlWriter.writeEndDocument();

        //region Formatted XML Output
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");

        StreamSource source = new StreamSource(new StringReader(writer.toString()));
        StreamResult result = new StreamResult(new FileWriter(customDataSet.getDataFolder().getPath() + File.separatorChar + classData.getName() + ".xml"));

        xmlWriter.flush();
        xmlWriter.close();
        writer.flush();
        writer.close();

        transformer.transform(source, result);
        //endregion
    }

    private String getValueOfAttribute(Node node, String attribute) {
        try {
            return node.getAttributes().getNamedItem(attribute).getNodeValue();
        } catch (Exception e) {
            return "-1";
        }
    }
    private TalentTree constructTalentTree(int id, Node node) {
        return new TalentTree(
                id,
                getValueOfAttribute(node, "name")
        );
    }
    private Talent constructTalent(Node node) {
        return new Talent(
                Integer.parseInt(getValueOfAttribute(node, "index")),
                getValueOfAttribute(node, "name"),
                getValueOfAttribute(node, "icon"),
                Integer.parseInt(getValueOfAttribute(node, "requires"))
        );
    }
    private Note constructNote(Node node) {
        return new Note(
                Integer.parseInt(getValueOfAttribute(node, "index")),
                getValueOfAttribute(node, "name"),
                getValueOfAttribute(node, "icon"),
                node.getTextContent()
        );
    }
}
