package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfSchedulerjobs {

    private long idofschedulerjob;

    public long getIdofschedulerjob() {
        return idofschedulerjob;
    }

    public void setIdofschedulerjob(long idofschedulerjob) {
        this.idofschedulerjob = idofschedulerjob;
    }

    private String jobclass;

    public String getJobclass() {
        return jobclass;
    }

    public void setJobclass(String jobclass) {
        this.jobclass = jobclass;
    }

    private String cronexpression;

    public String getCronexpression() {
        return cronexpression;
    }

    public void setCronexpression(String cronexpression) {
        this.cronexpression = cronexpression;
    }

    private String jobname;

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    private int enabled;

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfSchedulerjobs that = (CfSchedulerjobs) o;

        if (enabled != that.enabled) {
            return false;
        }
        if (idofschedulerjob != that.idofschedulerjob) {
            return false;
        }
        if (cronexpression != null ? !cronexpression.equals(that.cronexpression) : that.cronexpression != null) {
            return false;
        }
        if (jobclass != null ? !jobclass.equals(that.jobclass) : that.jobclass != null) {
            return false;
        }
        if (jobname != null ? !jobname.equals(that.jobname) : that.jobname != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofschedulerjob ^ (idofschedulerjob >>> 32));
        result = 31 * result + (jobclass != null ? jobclass.hashCode() : 0);
        result = 31 * result + (cronexpression != null ? cronexpression.hashCode() : 0);
        result = 31 * result + (jobname != null ? jobname.hashCode() : 0);
        result = 31 * result + enabled;
        return result;
    }
}
