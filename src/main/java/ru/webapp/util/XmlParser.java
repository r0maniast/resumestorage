package ru.webapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.Reader;
import java.io.Writer;

public class XmlParser {
    private static final Logger log = LoggerFactory.getLogger(XmlParser.class);

    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;

    public XmlParser(Class... classesBeToFind) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(classesBeToFind);
            marshaller = ctx.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8 ");
            unmarshaller = ctx.createUnmarshaller();
            log.info("Initialized JAXBContext and created Marshaller and Unmarshaller");
        } catch (JAXBException e) {
            log.error("Error initializing JAXBContext", e);
            throw new IllegalStateException(e);
        }
    }

    public <T> T unmarshall(Reader reader) {
        try {
            log.debug("Starting unmarshalling XML");
            T result = (T) unmarshaller.unmarshal(reader);
            log.debug("Completed unmarshalling XML");
            return result;
        } catch (JAXBException e) {
            log.error("Error during unmarshalling XML", e);
            throw new IllegalStateException(e);
        }
    }

    public void marshall(Object instance, Writer writer){
        try {
            log.debug("Starting marshalling object to XML: {}", instance.getClass().getName());
            marshaller.marshal(instance, writer);
            log.debug("Completed marshalling object to XML");
        } catch (JAXBException e) {
            log.error("Error during marshalling object to XML", e);
            throw new IllegalStateException(e);
        }
    }
}
