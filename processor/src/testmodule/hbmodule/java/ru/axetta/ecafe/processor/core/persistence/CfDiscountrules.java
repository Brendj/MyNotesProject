package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDiscountrules {

    private long idofrule;

    public long getIdofrule() {
        return idofrule;
    }

    public void setIdofrule(long idofrule) {
        this.idofrule = idofrule;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private int complex0;

    public int getComplex0() {
        return complex0;
    }

    public void setComplex0(int complex0) {
        this.complex0 = complex0;
    }

    private int complex1;

    public int getComplex1() {
        return complex1;
    }

    public void setComplex1(int complex1) {
        this.complex1 = complex1;
    }

    private int complex2;

    public int getComplex2() {
        return complex2;
    }

    public void setComplex2(int complex2) {
        this.complex2 = complex2;
    }

    private int complex3;

    public int getComplex3() {
        return complex3;
    }

    public void setComplex3(int complex3) {
        this.complex3 = complex3;
    }

    private int complex4;

    public int getComplex4() {
        return complex4;
    }

    public void setComplex4(int complex4) {
        this.complex4 = complex4;
    }

    private int complex5;

    public int getComplex5() {
        return complex5;
    }

    public void setComplex5(int complex5) {
        this.complex5 = complex5;
    }

    private int complex6;

    public int getComplex6() {
        return complex6;
    }

    public void setComplex6(int complex6) {
        this.complex6 = complex6;
    }

    private int complex7;

    public int getComplex7() {
        return complex7;
    }

    public void setComplex7(int complex7) {
        this.complex7 = complex7;
    }

    private int complex8;

    public int getComplex8() {
        return complex8;
    }

    public void setComplex8(int complex8) {
        this.complex8 = complex8;
    }

    private int complex9;

    public int getComplex9() {
        return complex9;
    }

    public void setComplex9(int complex9) {
        this.complex9 = complex9;
    }

    private int priority;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    private String categoriesdiscounts;

    public String getCategoriesdiscounts() {
        return categoriesdiscounts;
    }

    public void setCategoriesdiscounts(String categoriesdiscounts) {
        this.categoriesdiscounts = categoriesdiscounts;
    }

    private int operationor;

    public int getOperationor() {
        return operationor;
    }

    public void setOperationor(int operationor) {
        this.operationor = operationor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDiscountrules that = (CfDiscountrules) o;

        if (complex0 != that.complex0) {
            return false;
        }
        if (complex1 != that.complex1) {
            return false;
        }
        if (complex2 != that.complex2) {
            return false;
        }
        if (complex3 != that.complex3) {
            return false;
        }
        if (complex4 != that.complex4) {
            return false;
        }
        if (complex5 != that.complex5) {
            return false;
        }
        if (complex6 != that.complex6) {
            return false;
        }
        if (complex7 != that.complex7) {
            return false;
        }
        if (complex8 != that.complex8) {
            return false;
        }
        if (complex9 != that.complex9) {
            return false;
        }
        if (idofrule != that.idofrule) {
            return false;
        }
        if (operationor != that.operationor) {
            return false;
        }
        if (priority != that.priority) {
            return false;
        }
        if (categoriesdiscounts != null ? !categoriesdiscounts.equals(that.categoriesdiscounts)
                : that.categoriesdiscounts != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofrule ^ (idofrule >>> 32));
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + complex0;
        result = 31 * result + complex1;
        result = 31 * result + complex2;
        result = 31 * result + complex3;
        result = 31 * result + complex4;
        result = 31 * result + complex5;
        result = 31 * result + complex6;
        result = 31 * result + complex7;
        result = 31 * result + complex8;
        result = 31 * result + complex9;
        result = 31 * result + priority;
        result = 31 * result + (categoriesdiscounts != null ? categoriesdiscounts.hashCode() : 0);
        result = 31 * result + operationor;
        return result;
    }

    private Collection<CfDiscountrulesCategorydiscounts> cfDiscountrulesCategorydiscountsesByIdofrule;

    public Collection<CfDiscountrulesCategorydiscounts> getCfDiscountrulesCategorydiscountsesByIdofrule() {
        return cfDiscountrulesCategorydiscountsesByIdofrule;
    }

    public void setCfDiscountrulesCategorydiscountsesByIdofrule(
            Collection<CfDiscountrulesCategorydiscounts> cfDiscountrulesCategorydiscountsesByIdofrule) {
        this.cfDiscountrulesCategorydiscountsesByIdofrule = cfDiscountrulesCategorydiscountsesByIdofrule;
    }

    private Collection<CfDiscountrulesCategoryorg> cfDiscountrulesCategoryorgsByIdofrule;

    public Collection<CfDiscountrulesCategoryorg> getCfDiscountrulesCategoryorgsByIdofrule() {
        return cfDiscountrulesCategoryorgsByIdofrule;
    }

    public void setCfDiscountrulesCategoryorgsByIdofrule(
            Collection<CfDiscountrulesCategoryorg> cfDiscountrulesCategoryorgsByIdofrule) {
        this.cfDiscountrulesCategoryorgsByIdofrule = cfDiscountrulesCategoryorgsByIdofrule;
    }
}
