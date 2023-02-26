package com.kgromov.service;

import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.xpath.XPathExpression;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.nio.file.Path;

public class ChangelogReader {

    @SneakyThrows
    public Document readChangelog(Path changelogFile) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(changelogFile.toFile());
        return  document;
       /* try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(changelogFile.toFile());
            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath xpath = xpathfactory.newXPath();
            XPathExpression expr = xpath.compile("//dependencies/dependency/version");
            NodeList versionedDependencies = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < versionedDependencies.getLength(); i++) {
                Node versionNode = versionedDependencies.item(i);
                String version = versionNode.getTextContent();
                String artifactId = ((Element) versionNode.getParentNode()).getElementsByTagName("artifactId").item(0).getTextContent();
                artifactIdToVersion.putIfAbsent(artifactId, version);
            }
            return artifactIdToVersion;
        } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e) {
            return new HashMap<>();
        }*/
    }
}
