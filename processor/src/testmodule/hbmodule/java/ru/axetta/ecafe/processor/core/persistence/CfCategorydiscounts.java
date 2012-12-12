package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfCategorydiscounts {

    private long idofcategorydiscount;

    public long getIdofcategorydiscount() {
        return idofcategorydiscount;
    }

    public void setIdofcategorydiscount(long idofcategorydiscount) {
        this.idofcategorydiscount = idofcategorydiscount;
    }

    private String categoryname;

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    private String discountrules;

    public String getDiscountrules() {
        return discountrules;
    }

    public void setDiscountrules(String discountrules) {
        this.discountrules = discountrules;
    }

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private long lastupdate;

    public long getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(long lastupdate) {
        this.lastupdate = lastupdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfCategorydiscounts that = (CfCategorydiscounts) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (idofcategorydiscount != that.idofcategorydiscount) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (categoryname != null ? !categoryname.equals(that.categoryname) : that.categoryname != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (discountrules != null ? !discountrules.equals(that.discountrules) : that.discountrules != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofcategorydiscount ^ (idofcategorydiscount >>> 32));
        result = 31 * result + (categoryname != null ? categoryname.hashCode() : 0);
        result = 31 * result + (discountrules != null ? discountrules.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        return result;
    }

    private Collection<CfClientsCategorydiscounts> cfClientsCategorydiscountsesByIdofcategorydiscount;

    public Collection<CfClientsCategorydiscounts> getCfClientsCategorydiscountsesByIdofcategorydiscount() {
        return cfClientsCategorydiscountsesByIdofcategorydiscount;
    }

    public void setCfClientsCategorydiscountsesByIdofcategorydiscount(
            Collection<CfClientsCategorydiscounts> cfClientsCategorydiscountsesByIdofcategorydiscount) {
        this.cfClientsCategorydiscountsesByIdofcategorydiscount = cfClientsCategorydiscountsesByIdofcategorydiscount;
    }

    private Collection<CfDiscountrulesCategorydiscounts> cfDiscountrulesCategorydiscountsesByIdofcategorydiscount;

    public Collection<CfDiscountrulesCategorydiscounts> getCfDiscountrulesCategorydiscountsesByIdofcategorydiscount() {
        return cfDiscountrulesCategorydiscountsesByIdofcategorydiscount;
    }

    public void setCfDiscountrulesCategorydiscountsesByIdofcategorydiscount(
            Collection<CfDiscountrulesCategorydiscounts> cfDiscountrulesCategorydiscountsesByIdofcategorydiscount) {
        this.cfDiscountrulesCategorydiscountsesByIdofcategorydiscount = cfDiscountrulesCategorydiscountsesByIdofcategorydiscount;
    }
}
