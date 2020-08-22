package main.service;

import main.Repo.HistoryRepository;
import main.Repo.ValuteRepository;
import main.model.HistorySearch;
import main.model.support.SearchObject;
import main.model.Valute;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ValuteService {

    @Autowired
    private XMLHandler xmlHandler;
    @Autowired
    private ValuteRepository valuteRepository;
    @Autowired
    private HistoryRepository historyRepository;

    public List<Valute> getAllValutes() {
        URL url = null;
        try {
            url = new URL("http://www.cbr.ru/scripts/XML_daily.asp");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String fileName = "res/file.xml";
        try {
            FileUtils.copyURLToFile(url, new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        try {
            parser.parse(new File(fileName), xmlHandler);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xmlHandler.getMonies();
    }

    public List<Valute> isRepositoryEmpty() {
        List<Valute> valuteList = new ArrayList<>();
        valuteList.addAll((List) valuteRepository.findAll());
        return valuteList;
    }

    public void saveAllValutes() {
        valuteRepository.saveAll(xmlHandler.getMonies());
    }

    public void saveHistory(HistorySearch historySearch) {
        historyRepository.save(historySearch);
    }


    private Double converter(SearchObject object) {
        if (object.getValue().contains(",")) {
            String s = object.getValue().replace(",", ".");
            Optional<Valute> optional = valuteRepository.findById(object.getIdTo());
            Optional<Valute> optional2 = valuteRepository.findById(object.getIdFrom());
            double RUB_1 = optional.get().getNominal() / optional.get().getValue();
            double RUB_TO = (Double.parseDouble(s) * RUB_1 * optional2.get().getValue()) / optional2.get().getNominal();
            return RUB_TO;
        }
        Optional<Valute> optional = valuteRepository.findById(object.getIdTo());
        Optional<Valute> optional2 = valuteRepository.findById(object.getIdFrom());
        double RUB_1 = optional.get().getNominal() / optional.get().getValue();
        double RUB_TO = (Double.parseDouble(object.getValue()) * RUB_1 * optional2.get().getValue()) / optional2.get().getNominal();
        return RUB_TO;
    }

    public BigDecimal cheToTam(SearchObject object, String test) {

        String s = object.getValue().replace(",", ".");
        Optional<Valute> optional = valuteRepository.findById(object.getIdTo());
        Optional<Valute> optional2 = valuteRepository.findById(object.getIdFrom());
        if (optional.get().getDate().isBefore(LocalDate.now())) {
            valuteRepository.deleteAll();
            saveAllValutes();
        }
        BigDecimal result = new BigDecimal(converter(object));
        result = result.setScale(2, RoundingMode.HALF_UP);
        HistorySearch search = new HistorySearch();
        search.setDate(LocalDate.now());
        search.setNameFrom(optional2.get().getName());
        search.setNameTo(optional.get().getName());
        search.setValueFrom(Double.parseDouble(s));
        search.setValueTo(result.doubleValue());
        saveHistory(search);
        return result;
    }
    public Optional getValuteById(int id){
        Optional<Valute> optional = valuteRepository.findById(id);
        return optional;
    }
}
