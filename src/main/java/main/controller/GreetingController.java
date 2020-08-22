package main.controller;

import main.Repo.HistoryRepository;
import main.Repo.ValuteRepository;
import main.model.HistorySearch;
import main.model.Valute;
import main.model.support.SearchObject;
import main.service.ValuteService;
import main.service.XMLHandler;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Controller
public class GreetingController {

    @Autowired
    private ValuteService valuteService;

    @Autowired
    ValuteRepository repository;
    @Autowired
    HistoryRepository historyRepository;

    @GetMapping()
    public String getGreetingForm(Model model) throws IOException, SAXException, ParserConfigurationException {
        if (valuteService.getAllValutes().isEmpty()){
            valuteService.saveAllValutes();
        }
        model.addAttribute("index",new SearchObject());
        model.addAttribute("choose",new SearchObject());
        model.addAttribute("list", valuteService.getAllValutes());
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
            if (valuteService.getValuteById(object.getIdTo()).isEmpty()){
                return "error";
            }
            if (valuteService.getValuteById(object.getIdFrom()).isEmpty()){
                return "error";
            }
            model.addAttribute("converter",valuteService.cheToTam(object, s));
            return "result";
        }
        String s =object.getValue();
        model.addAttribute("converter",valuteService.cheToTam(object, s));
        return "result";
    }

    private Double converter(SearchObject object){
        if (object.getValue().contains(",")){
            String s =object.getValue().replace(",",".");
            Optional<Valute> optional =repository.findById(object.getIdTo());
            Optional<Valute> optional2 =repository.findById(object.getIdFrom());
            double RUB_1 = optional.get().getNominal()/optional.get().getValue();
            double RUB_TO = (Double.parseDouble(s)*RUB_1*optional2.get().getValue())/optional2.get().getNominal();
            return  RUB_TO;
        }
        Optional<Valute> optional =repository.findById(object.getIdTo());
        Optional<Valute> optional2 =repository.findById(object.getIdFrom());
        double RUB_1 = optional.get().getNominal()/optional.get().getValue();
        double RUB_TO = (Double.parseDouble(object.getValue())*RUB_1*optional2.get().getValue())/optional2.get().getNominal();
        return  RUB_TO;
    }
}

