package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfDiaryclasses {

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
    }

    private long idofclass;

    public long getIdofclass() {
        return idofclass;
    }

    public void setIdofclass(long idofclass) {
        this.idofclass = idofclass;
    }

    private String classname;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfDiaryclasses that = (CfDiaryclasses) o;

        if (idofclass != that.idofclass) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (classname != null ? !classname.equals(that.classname) : that.classname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (int) (idofclass ^ (idofclass >>> 32));
        result = 31 * result + (classname != null ? classname.hashCode() : 0);
        return result;
    }

    private CfOrgs cfOrgsByIdoforg;

    public CfOrgs getCfOrgsByIdoforg() {
        return cfOrgsByIdoforg;
    }

    public void setCfOrgsByIdoforg(CfOrgs cfOrgsByIdoforg) {
        this.cfOrgsByIdoforg = cfOrgsByIdoforg;
    }

    private Collection<CfDiarytimesheet> cfDiarytimesheets;

    public Collection<CfDiarytimesheet> getCfDiarytimesheets() {
        return cfDiarytimesheets;
    }

    public void setCfDiarytimesheets(Collection<CfDiarytimesheet> cfDiarytimesheets) {
        this.cfDiarytimesheets = cfDiarytimesheets;
    }

    private Collection<CfDiarytimesheet> cfDiarytimesheets_0;

    public Collection<CfDiarytimesheet> getCfDiarytimesheets_0() {
        return cfDiarytimesheets_0;
    }

    public void setCfDiarytimesheets_0(Collection<CfDiarytimesheet> cfDiarytimesheets_0) {
        this.cfDiarytimesheets_0 = cfDiarytimesheets_0;
    }

    private Collection<CfDiarytimesheet> cfDiarytimesheets_1;

    public Collection<CfDiarytimesheet> getCfDiarytimesheets_1() {
        return cfDiarytimesheets_1;
    }

    public void setCfDiarytimesheets_1(Collection<CfDiarytimesheet> cfDiarytimesheets_1) {
        this.cfDiarytimesheets_1 = cfDiarytimesheets_1;
    }

    private Collection<CfDiarytimesheet> cfDiarytimesheets_2;

    public Collection<CfDiarytimesheet> getCfDiarytimesheets_2() {
        return cfDiarytimesheets_2;
    }

    public void setCfDiarytimesheets_2(Collection<CfDiarytimesheet> cfDiarytimesheets_2) {
        this.cfDiarytimesheets_2 = cfDiarytimesheets_2;
    }

    private Collection<CfDiarytimesheet> cfDiarytimesheets_3;

    public Collection<CfDiarytimesheet> getCfDiarytimesheets_3() {
        return cfDiarytimesheets_3;
    }

    public void setCfDiarytimesheets_3(Collection<CfDiarytimesheet> cfDiarytimesheets_3) {
        this.cfDiarytimesheets_3 = cfDiarytimesheets_3;
    }

    private Collection<CfDiarytimesheet> cfDiarytimesheets_4;

    public Collection<CfDiarytimesheet> getCfDiarytimesheets_4() {
        return cfDiarytimesheets_4;
    }

    public void setCfDiarytimesheets_4(Collection<CfDiarytimesheet> cfDiarytimesheets_4) {
        this.cfDiarytimesheets_4 = cfDiarytimesheets_4;
    }

    private Collection<CfDiarytimesheet> cfDiarytimesheets_5;

    public Collection<CfDiarytimesheet> getCfDiarytimesheets_5() {
        return cfDiarytimesheets_5;
    }

    public void setCfDiarytimesheets_5(Collection<CfDiarytimesheet> cfDiarytimesheets_5) {
        this.cfDiarytimesheets_5 = cfDiarytimesheets_5;
    }

    private Collection<CfDiarytimesheet> cfDiarytimesheets_6;

    public Collection<CfDiarytimesheet> getCfDiarytimesheets_6() {
        return cfDiarytimesheets_6;
    }

    public void setCfDiarytimesheets_6(Collection<CfDiarytimesheet> cfDiarytimesheets_6) {
        this.cfDiarytimesheets_6 = cfDiarytimesheets_6;
    }

    private Collection<CfDiarytimesheet> cfDiarytimesheets_7;

    public Collection<CfDiarytimesheet> getCfDiarytimesheets_7() {
        return cfDiarytimesheets_7;
    }

    public void setCfDiarytimesheets_7(Collection<CfDiarytimesheet> cfDiarytimesheets_7) {
        this.cfDiarytimesheets_7 = cfDiarytimesheets_7;
    }

    private Collection<CfDiarytimesheet> cfDiarytimesheets_8;

    public Collection<CfDiarytimesheet> getCfDiarytimesheets_8() {
        return cfDiarytimesheets_8;
    }

    public void setCfDiarytimesheets_8(Collection<CfDiarytimesheet> cfDiarytimesheets_8) {
        this.cfDiarytimesheets_8 = cfDiarytimesheets_8;
    }

    private Collection<CfDiaryvalues> cfDiaryvalueses;

    public Collection<CfDiaryvalues> getCfDiaryvalueses() {
        return cfDiaryvalueses;
    }

    public void setCfDiaryvalueses(Collection<CfDiaryvalues> cfDiaryvalueses) {
        this.cfDiaryvalueses = cfDiaryvalueses;
    }
}
