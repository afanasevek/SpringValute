package main.controller;

import main.Repo.MoneyRepository;
import main.model.Money;
import main.model.SearchObject;
import main.tools.XMLHandler;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class GreetingController {
    @Autowired
    MoneyRepository repository;

    @GetMapping()
    public String getGreetingForm(Model model) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<Money> list = new ArrayList<>();
        list.addAll((List)repository.findAll());
        if (list.isEmpty()){
            repository.saveAll(xmlParser());
        }
        model.addAttribute("index",new SearchObject());
        model.addAttribute("choose",new SearchObject());
        model.addAttribute("list", xmlParser());
        return "index";
    }

    @PostMapping("/greeting")
    public String sendGreetingForm(@ModelAttribute SearchObject object){
//        model.addAttribute("index",object);
        System.out.println(converter(object));

        return "result";
    }
    private static List<Money> xmlParser() throws ParserConfigurationException, SAXException, IOException {

        URL url = new URL("http://www.cbr.ru/scripts/XML_daily.asp");


        String fileName = "res/file.xml";
        FileUtils.copyURLToFile(url,new File(fileName));
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLHandler xmlHandler = new XMLHandler();
        parser.parse(new File(fileName), xmlHandler);
        return xmlHandler.getMonies();
    }
    private Double converter(SearchObject object){
        Optional<Money> optional =repository.findById(object.getIdTo());
        Optional<Money> optional2 =repository.findById(object.getIdFrom());
        double RUB_1 = optional.get().getNominal()/optional.get().getValue();
        double RUB_TO = object.getValue()*RUB_1*optional2.get().getValue();
        return  RUB_TO;
    }
}

