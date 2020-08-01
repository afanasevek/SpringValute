package main.model;

import javax.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "history")
public class HistorySearch {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nameFrom;
    private String nameTo;
    private double valueFrom;
    private  double valueTo;
    private LocalDate date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameFrom() {
        return nameFrom;
    }

    public void setNameFrom(String nameFrom) {
        this.nameFrom = nameFrom;
    }

    public String getNameTo() {
        return nameTo;
    }

    public void setNameTo(String nameTo) {
        this.nameTo = nameTo;
    }

    public double getValueFrom() {
        return valueFrom;
    }

    public void setValueFrom(double valueFrom) {
        this.valueFrom = valueFrom;
    }

    public double getValueTo() {
        return valueTo;
    }

    public void setValueTo(double valueTo) {
        this.valueTo = valueTo;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
