package ru.axetta.ecafe.processor.web.rest.items;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.04.14
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
public class ComplexItem {

    private int idOfComplex;
    private String name;
    private Long price;
    private Integer[] checkarr;

    public int getIdOfComplex() {
        return idOfComplex;
    }

    public void setIdOfComplex(int idOfComplex) {
        this.idOfComplex = idOfComplex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer[] getCheckarr() {
        return checkarr;
    }

    public void setCheckarr(Integer[] checkarr) {
        this.checkarr = checkarr;
    }
}
