package com.appliedcoding.utils;

import com.appliedcoding.snakegame.exception.SnakeException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

//See: https://gist.github.com/rponte/5753ef3ad9f526e48fc282cc3364ac26
public class XmlSerializer {

    private static final String encoding = "UTF-8";

    public static void toFileXml(Object o, String filename) {
        try {
            JAXBContext context = JAXBContext.newInstance(o.getClass());

            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

            marshaller.marshal(o, new File(filename));
        } catch (JAXBException e) {
            throw new SnakeException(e);
        }
    }

    public static <T> T fromXml(String xml, Class<T> clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setAdapter(new NormalizedStringAdapter());

            Object o = unmarshaller.unmarshal(new StringReader(xml));

            return clazz.cast(o);
        } catch (JAXBException e) {
            throw new SnakeException(e);
        }
    }

    public static <T> T fromXml(File file, Class<T> clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setAdapter(new NormalizedStringAdapter());

            try (Reader reader = new InputStreamReader(new FileInputStream(file), encoding);) {
                Object o = unmarshaller.unmarshal(reader);
                return clazz.cast(o);
            }
        } catch (Exception e) {
            throw new SnakeException(e);
        }
    }
}