package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfReportinfo {

    private long idofreportinfo;

    public long getIdofreportinfo() {
        return idofreportinfo;
    }

    public void setIdofreportinfo(long idofreportinfo) {
        this.idofreportinfo = idofreportinfo;
    }

    private String rulename;

    public String getRulename() {
        return rulename;
    }

    public void setRulename(String rulename) {
        this.rulename = rulename;
    }

    private int documentformat;

    public int getDocumentformat() {
        return documentformat;
    }

    public void setDocumentformat(int documentformat) {
        this.documentformat = documentformat;
    }

    private String reportname;

    public String getReportname() {
        return reportname;
    }

    public void setReportname(String reportname) {
        this.reportname = reportname;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private int generationtime;

    public int getGenerationtime() {
        return generationtime;
    }

    public void setGenerationtime(int generationtime) {
        this.generationtime = generationtime;
    }

    private long startdate;

    public long getStartdate() {
        return startdate;
    }

    public void setStartdate(long startdate) {
        this.startdate = startdate;
    }

    private long enddate;

    public long getEnddate() {
        return enddate;
    }

    public void setEnddate(long enddate) {
        this.enddate = enddate;
    }

    private String reportfile;

    public String getReportfile() {
        return reportfile;
    }

    public void setReportfile(String reportfile) {
        this.reportfile = reportfile;
    }

    private String orgnum;

    public String getOrgnum() {
        return orgnum;
    }

    public void setOrgnum(String orgnum) {
        this.orgnum = orgnum;
    }

    private long idoforg;

    public long getIdoforg() {
        return idoforg;
    }

    public void setIdoforg(long idoforg) {
        this.idoforg = idoforg;
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

        CfReportinfo that = (CfReportinfo) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (documentformat != that.documentformat) {
            return false;
        }
        if (enddate != that.enddate) {
            return false;
        }
        if (generationtime != that.generationtime) {
            return false;
        }
        if (idoforg != that.idoforg) {
            return false;
        }
        if (idofreportinfo != that.idofreportinfo) {
            return false;
        }
        if (startdate != that.startdate) {
            return false;
        }
        if (orgnum != null ? !orgnum.equals(that.orgnum) : that.orgnum != null) {
            return false;
        }
        if (reportfile != null ? !reportfile.equals(that.reportfile) : that.reportfile != null) {
            return false;
        }
        if (reportname != null ? !reportname.equals(that.reportname) : that.reportname != null) {
            return false;
        }
        if (rulename != null ? !rulename.equals(that.rulename) : that.rulename != null) {
            return false;
        }
        if (tag != null ? !tag.equals(that.tag) : that.tag != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofreportinfo ^ (idofreportinfo >>> 32));
        result = 31 * result + (rulename != null ? rulename.hashCode() : 0);
        result = 31 * result + documentformat;
        result = 31 * result + (reportname != null ? reportname.hashCode() : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + generationtime;
        result = 31 * result + (int) (startdate ^ (startdate >>> 32));
        result = 31 * result + (int) (enddate ^ (enddate >>> 32));
        result = 31 * result + (reportfile != null ? reportfile.hashCode() : 0);
        result = 31 * result + (orgnum != null ? orgnum.hashCode() : 0);
        result = 31 * result + (int) (idoforg ^ (idoforg >>> 32));
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        return result;
    }
}
