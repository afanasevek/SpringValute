package main.tools;

import main.Repo.MoneyRepository;
import main.model.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class XMLHandler extends DefaultHandler {
    private String lastElementName;
    private List<Money> monies = new ArrayList<>();
    private Money money   = new Money();
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        lastElementName = qName;
        if (qName.equals("Valute")){
            money   = new Money();
        }
    }
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        money.setDate(LocalDate.now());
        String information = new String(ch, start, length);
        information = information.replace("\n", "").trim();
        if (!information.isEmpty()) {
            if (lastElementName.equals("NumCode")) {
                money.setNumCode(Integer.parseInt(information));
            }
            if (lastElementName.equals("CharCode")) {
                money.setCharCode(information);
            }
            if (lastElementName.equals("Nominal")) {
                money.setNominal(Integer.parseInt(information));
            }
            if (lastElementName.equals("Name")) {
                money.setName(information);
            }
            if (lastElementName.equals("Value")) {
                String value = information.replaceAll("\\,", "\\.");
                money.setValue(Double.parseDouble(value));
            }
        }
    }
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("Valute")){
            monies.add(money);
            money=null;
        }
    }
    @Override
    public void endDocument() throws SAXException {
        money=null;
    }
    public List<Money> getMonies() {
        return monies;
    }
    public void setMonies(List<Money> monies) {
        this.monies = monies;
    }


}
