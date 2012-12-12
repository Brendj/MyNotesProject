package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfReporthandlerules {

    private long idofreporthandlerule;

    public long getIdofreporthandlerule() {
        return idofreporthandlerule;
    }

    public void setIdofreporthandlerule(long idofreporthandlerule) {
        this.idofreporthandlerule = idofreporthandlerule;
    }

    private String rulename;

    public String getRulename() {
        return rulename;
    }

    public void setRulename(String rulename) {
        this.rulename = rulename;
    }

    private String subject;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    private int documentformat;

    public int getDocumentformat() {
        return documentformat;
    }

    public void setDocumentformat(int documentformat) {
        this.documentformat = documentformat;
    }

    private String route0;

    public String getRoute0() {
        return route0;
    }

    public void setRoute0(String route0) {
        this.route0 = route0;
    }

    private String route1;

    public String getRoute1() {
        return route1;
    }

    public void setRoute1(String route1) {
        this.route1 = route1;
    }

    private String route2;

    public String getRoute2() {
        return route2;
    }

    public void setRoute2(String route2) {
        this.route2 = route2;
    }

    private String route3;

    public String getRoute3() {
        return route3;
    }

    public void setRoute3(String route3) {
        this.route3 = route3;
    }

    private String route4;

    public String getRoute4() {
        return route4;
    }

    public void setRoute4(String route4) {
        this.route4 = route4;
    }

    private String route5;

    public String getRoute5() {
        return route5;
    }

    public void setRoute5(String route5) {
        this.route5 = route5;
    }

    private String route6;

    public String getRoute6() {
        return route6;
    }

    public void setRoute6(String route6) {
        this.route6 = route6;
    }

    private String route7;

    public String getRoute7() {
        return route7;
    }

    public void setRoute7(String route7) {
        this.route7 = route7;
    }

    private String route8;

    public String getRoute8() {
        return route8;
    }

    public void setRoute8(String route8) {
        this.route8 = route8;
    }

    private String route9;

    public String getRoute9() {
        return route9;
    }

    public void setRoute9(String route9) {
        this.route9 = route9;
    }

    private String remarks;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    private int enabled;

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    private String templatefilename;

    public String getTemplatefilename() {
        return templatefilename;
    }

    public void setTemplatefilename(String templatefilename) {
        this.templatefilename = templatefilename;
    }

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfReporthandlerules that = (CfReporthandlerules) o;

        if (documentformat != that.documentformat) {
            return false;
        }
        if (enabled != that.enabled) {
            return false;
        }
        if (idofreporthandlerule != that.idofreporthandlerule) {
            return false;
        }
        if (remarks != null ? !remarks.equals(that.remarks) : that.remarks != null) {
            return false;
        }
        if (route0 != null ? !route0.equals(that.route0) : that.route0 != null) {
            return false;
        }
        if (route1 != null ? !route1.equals(that.route1) : that.route1 != null) {
            return false;
        }
        if (route2 != null ? !route2.equals(that.route2) : that.route2 != null) {
            return false;
        }
        if (route3 != null ? !route3.equals(that.route3) : that.route3 != null) {
            return false;
        }
        if (route4 != null ? !route4.equals(that.route4) : that.route4 != null) {
            return false;
        }
        if (route5 != null ? !route5.equals(that.route5) : that.route5 != null) {
            return false;
        }
        if (route6 != null ? !route6.equals(that.route6) : that.route6 != null) {
            return false;
        }
        if (route7 != null ? !route7.equals(that.route7) : that.route7 != null) {
            return false;
        }
        if (route8 != null ? !route8.equals(that.route8) : that.route8 != null) {
            return false;
        }
        if (route9 != null ? !route9.equals(that.route9) : that.route9 != null) {
            return false;
        }
        if (rulename != null ? !rulename.equals(that.rulename) : that.rulename != null) {
            return false;
        }
        if (subject != null ? !subject.equals(that.subject) : that.subject != null) {
            return false;
        }
        if (tag != null ? !tag.equals(that.tag) : that.tag != null) {
            return false;
        }
        if (templatefilename != null ? !templatefilename.equals(that.templatefilename)
                : that.templatefilename != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofreporthandlerule ^ (idofreporthandlerule >>> 32));
        result = 31 * result + (rulename != null ? rulename.hashCode() : 0);
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + documentformat;
        result = 31 * result + (route0 != null ? route0.hashCode() : 0);
        result = 31 * result + (route1 != null ? route1.hashCode() : 0);
        result = 31 * result + (route2 != null ? route2.hashCode() : 0);
        result = 31 * result + (route3 != null ? route3.hashCode() : 0);
        result = 31 * result + (route4 != null ? route4.hashCode() : 0);
        result = 31 * result + (route5 != null ? route5.hashCode() : 0);
        result = 31 * result + (route6 != null ? route6.hashCode() : 0);
        result = 31 * result + (route7 != null ? route7.hashCode() : 0);
        result = 31 * result + (route8 != null ? route8.hashCode() : 0);
        result = 31 * result + (route9 != null ? route9.hashCode() : 0);
        result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
        result = 31 * result + enabled;
        result = 31 * result + (templatefilename != null ? templatefilename.hashCode() : 0);
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }

    private Collection<CfRuleconditions> cfRuleconditionsesByIdofreporthandlerule;

    public Collection<CfRuleconditions> getCfRuleconditionsesByIdofreporthandlerule() {
        return cfRuleconditionsesByIdofreporthandlerule;
    }

    public void setCfRuleconditionsesByIdofreporthandlerule(
            Collection<CfRuleconditions> cfRuleconditionsesByIdofreporthandlerule) {
        this.cfRuleconditionsesByIdofreporthandlerule = cfRuleconditionsesByIdofreporthandlerule;
    }
}
