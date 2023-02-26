package com.kgromov.service;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.nio.file.Path;

@Slf4j
public class ChangelogWriter {

    public void writeToChangelogFile(Document document, Path changelogFile) {
        try (FileOutputStream output = new FileOutputStream(changelogFile.toFile())) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
