package ru.webapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;

public class LocalDateXmlAdapter extends XmlAdapter<String, LocalDate> {
    private static final Logger log = LoggerFactory.getLogger(LocalDateXmlAdapter.class);

    @Override
    public LocalDate unmarshal(String str) throws Exception {
        log.debug("Unmarshalling LocalDate from string: {}", str);
        LocalDate date = LocalDate.parse(str);
        log.debug("Unmarshalled LocalDate: {}", date);
        return date;
    }

    @Override
    public String marshal(LocalDate ld) throws Exception {
        String result = ld.toString();
        log.debug("Marshalling LocalDate to string: {}", result);
        return result;
    }
}
