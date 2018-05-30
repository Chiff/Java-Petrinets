package oop.chiff.javapetrinets;

import java.io.File;
import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


class XmlReader {
    private Document petrinet;

    XmlReader(File file) throws JAXBException {
        JAXBContext jContext = JAXBContext.newInstance(Document.class);
        Unmarshaller unmarshallerObj = jContext.createUnmarshaller();

        petrinet = (Document) unmarshallerObj.unmarshal(file);
    }

    XmlReader() throws JAXBException {
        JAXBContext jContext = JAXBContext.newInstance(Document.class);
        Unmarshaller unmarshallerObj = jContext.createUnmarshaller();

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<document>\n" +
                "</document>";
        StringReader reader = new StringReader(xml);

        petrinet = (Document) unmarshallerObj.unmarshal(reader);
    }

    public Document getPetrinet() {
        return petrinet;
    }
}
