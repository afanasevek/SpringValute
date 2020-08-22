package main.service;

import main.model.Valute;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Service
public class XMLHandler extends DefaultHandler {
    private String lastElementName;
    private List<Valute> monies = new ArrayList<>();
    private Valute valute = new Valute();
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        lastElementName = qName;
        if (qName.equals("Valute")){
            valute = new Valute();
        }
    }
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        valute.setDate(LocalDate.now());
        String information = new String(ch, start, length);
        information = information.replace("\n", "").trim();
        if (!information.isEmpty()) {
            if (lastElementName.equals("NumCode")) {
                valute.setNumCode(Integer.parseInt(information));
            }
            if (lastElementName.equals("CharCode")) {
                valute.setCharCode(information);
            }
            if (lastElementName.equals("Nominal")) {
                valute.setNominal(Integer.parseInt(information));
            }
            if (lastElementName.equals("Name")) {
                valute.setName(information);
            }
            if (lastElementName.equals("Value")) {
                String value = information.replaceAll("\\,", "\\.");
                valute.setValue(Double.parseDouble(value));
            }
        }
    }
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("Valute")){
            monies.add(valute);
            valute =null;
        }
    }
    @Override
    public void endDocument() throws SAXException {
        valute =null;
    }
    public List<Valute> getMonies() {
        return monies;
    }
    public void setMonies(List<Valute> monies) {
        this.monies = monies;
    }


}
