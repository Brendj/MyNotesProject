package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfProjectstateData {

    private long period;

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String stringkey;

    public String getStringkey() {
        return stringkey;
    }

    public void setStringkey(String stringkey) {
        this.stringkey = stringkey;
    }

    private String stringvalue;

    public String getStringvalue() {
        return stringvalue;
    }

    public void setStringvalue(String stringvalue) {
        this.stringvalue = stringvalue;
    }

    private long generationdate;

    public long getGenerationdate() {
        return generationdate;
    }

    public void setGenerationdate(long generationdate) {
        this.generationdate = generationdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfProjectstateData that = (CfProjectstateData) o;

        if (generationdate != that.generationdate) {
            return false;
        }
        if (period != that.period) {
            return false;
        }
        if (type != that.type) {
            return false;
        }
        if (stringkey != null ? !stringkey.equals(that.stringkey) : that.stringkey != null) {
            return false;
        }
        if (stringvalue != null ? !stringvalue.equals(that.stringvalue) : that.stringvalue != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (period ^ (period >>> 32));
        result = 31 * result + type;
        result = 31 * result + (stringkey != null ? stringkey.hashCode() : 0);
        result = 31 * result + (stringvalue != null ? stringvalue.hashCode() : 0);
        result = 31 * result + (int) (generationdate ^ (generationdate >>> 32));
        return result;
    }
}
