package main.model.support;

public class SearchObject {
    private String value;
    private int idFrom;
    private int idTo;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIdFrom() {
        return idFrom;
    }

    public void setIdFrom(int idFrom) {
        this.idFrom = idFrom;
    }

    public int getIdTo() {
        return idTo;
    }

    public void setIdTo(int idTo) {
        this.idTo = idTo;
    }


    @Override
    public String toString() {
        return "SearchObject{" +
                "value=" + value +
                ", idFrom=" + idFrom +
                ", idTo=" + idTo +
                '}';
    }
}
