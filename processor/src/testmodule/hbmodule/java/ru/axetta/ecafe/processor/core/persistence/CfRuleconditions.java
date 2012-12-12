package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfRuleconditions {

    private long idofrulecondition;

    public long getIdofrulecondition() {
        return idofrulecondition;
    }

    public void setIdofrulecondition(long idofrulecondition) {
        this.idofrulecondition = idofrulecondition;
    }

    private long idofreporthandlerule;

    public long getIdofreporthandlerule() {
        return idofreporthandlerule;
    }

    public void setIdofreporthandlerule(long idofreporthandlerule) {
        this.idofreporthandlerule = idofreporthandlerule;
    }

    private int conditionoperation;

    public int getConditionoperation() {
        return conditionoperation;
    }

    public void setConditionoperation(int conditionoperation) {
        this.conditionoperation = conditionoperation;
    }

    private String conditionargument;

    public String getConditionargument() {
        return conditionargument;
    }

    public void setConditionargument(String conditionargument) {
        this.conditionargument = conditionargument;
    }

    private String conditionconstant;

    public String getConditionconstant() {
        return conditionconstant;
    }

    public void setConditionconstant(String conditionconstant) {
        this.conditionconstant = conditionconstant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfRuleconditions that = (CfRuleconditions) o;

        if (conditionoperation != that.conditionoperation) {
            return false;
        }
        if (idofreporthandlerule != that.idofreporthandlerule) {
            return false;
        }
        if (idofrulecondition != that.idofrulecondition) {
            return false;
        }
        if (conditionargument != null ? !conditionargument.equals(that.conditionargument)
                : that.conditionargument != null) {
            return false;
        }
        if (conditionconstant != null ? !conditionconstant.equals(that.conditionconstant)
                : that.conditionconstant != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofrulecondition ^ (idofrulecondition >>> 32));
        result = 31 * result + (int) (idofreporthandlerule ^ (idofreporthandlerule >>> 32));
        result = 31 * result + conditionoperation;
        result = 31 * result + (conditionargument != null ? conditionargument.hashCode() : 0);
        result = 31 * result + (conditionconstant != null ? conditionconstant.hashCode() : 0);
        return result;
    }

    private CfReporthandlerules cfReporthandlerulesByIdofreporthandlerule;

    public CfReporthandlerules getCfReporthandlerulesByIdofreporthandlerule() {
        return cfReporthandlerulesByIdofreporthandlerule;
    }

    public void setCfReporthandlerulesByIdofreporthandlerule(
            CfReporthandlerules cfReporthandlerulesByIdofreporthandlerule) {
        this.cfReporthandlerulesByIdofreporthandlerule = cfReporthandlerulesByIdofreporthandlerule;
    }
}
