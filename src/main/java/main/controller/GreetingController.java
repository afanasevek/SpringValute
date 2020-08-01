package main.controller;

import main.Repo.HistoryRepository;
import main.Repo.MoneyRepository;
import main.model.HistorySearch;
import main.model.Money;
import main.model.SearchObject;
import main.tools.XMLHandler;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Controller
public class GreetingController {
    @Autowired
    MoneyRepository repository;
    @Autowired
    HistoryRepository historyRepository;

    @GetMapping()
    public String getGreetingForm(Model model) throws IOException, SAXException, ParserConfigurationException {
        ArrayList<Money> list = new ArrayList<>();
        list.addAll((List)repository.findAll());
        if (list.isEmpty()){
            repository.saveAll(xmlParser());
        }
        Comparator<Money> comparator = Comparator.comparing(obj -> obj.getName());

        model.addAttribute("index",new SearchObject());
        model.addAttribute("choose",new SearchObject());
        model.addAttribute("list", xmlParser());
        model.addAttribute("history", historyRepository.findAll());
        return "index";
    }

    @PostMapping()
    public String sendGreetingForm(@ModelAttribute SearchObject object, Model model) throws IOException, SAXException, ParserConfigurationException {
        if (object.getValue().matches("\\D+")){
            return "error";
        }
        if (object.getValue().contains(",")){
            String s =object.getValue().replace(",",".");
            Optional<Money> optional =repository.findById(object.getIdTo());
            if (optional.isEmpty()){
                return "error";
            }
            Optional<Money> optional2 =repository.findById(object.getIdFrom());
            if (optional2.isEmpty()){
                return "error";
            }
            if (optional.get().getDate().isBefore(LocalDate.now())){
                repository.deleteAll();
                repository.saveAll(xmlParser());
            }
            BigDecimal result = new BigDecimal(converter(object));
            result = result.setScale(2, RoundingMode.HALF_UP);
            HistorySearch search = new HistorySearch();
            search.setDate(LocalDate.now());
            search.setNameFrom(optional2.get().getName());
            search.setNameTo(optional.get().getName());
            search.setValueFrom(Double.parseDouble(s));
            search.setValueTo(result.doubleValue());
            historyRepository.save(search);
            model.addAttribute("converter",result);
            return "result";
        }
        Optional<Money> optional =repository.findById(object.getIdTo());
        if (optional.isEmpty()){
            return "error";
        }
        Optional<Money> optional2 =repository.findById(object.getIdFrom());
        if (optional2.isEmpty()){
            return "error";
        }
        if (optional.get().getDate().isBefore(LocalDate.now())){
            repository.deleteAll();
            repository.saveAll(xmlParser());
        }
        BigDecimal result = new BigDecimal(converter(object));
        result = result.setScale(2, RoundingMode.HALF_UP);
        HistorySearch search = new HistorySearch();
        search.setDate(LocalDate.now());
        search.setNameFrom(optional2.get().getName());
        search.setNameTo(optional.get().getName());
        search.setValueFrom(Double.parseDouble(object.getValue()));
        search.setValueTo(result.doubleValue());
        historyRepository.save(search);
        model.addAttribute("converter",result);
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
        if (object.getValue().contains(",")){
            String s =object.getValue().replace(",",".");
            Optional<Money> optional =repository.findById(object.getIdTo());
            Optional<Money> optional2 =repository.findById(object.getIdFrom());
            double RUB_1 = optional.get().getNominal()/optional.get().getValue();
            double RUB_TO = Double.parseDouble(s)*RUB_1*optional2.get().getValue();
            return  RUB_TO;
        }
        Optional<Money> optional =repository.findById(object.getIdTo());
        Optional<Money> optional2 =repository.findById(object.getIdFrom());
        double RUB_1 = optional.get().getNominal()/optional.get().getValue();
        double RUB_TO = Double.parseDouble(object.getValue())*RUB_1*optional2.get().getValue();
        return  RUB_TO;
    }
}

