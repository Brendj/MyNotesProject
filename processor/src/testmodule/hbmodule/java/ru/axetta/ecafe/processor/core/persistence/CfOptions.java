package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfOptions {

    private long idofoption;

    public long getIdofoption() {
        return idofoption;
    }

    public void setIdofoption(long idofoption) {
        this.idofoption = idofoption;
    }

    private String optiontext;

    public String getOptiontext() {
        return optiontext;
    }

    public void setOptiontext(String optiontext) {
        this.optiontext = optiontext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfOptions cfOptions = (CfOptions) o;

        if (idofoption != cfOptions.idofoption) {
            return false;
        }
        if (optiontext != null ? !optiontext.equals(cfOptions.optiontext) : cfOptions.optiontext != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofoption ^ (idofoption >>> 32));
        result = 31 * result + (optiontext != null ? optiontext.hashCode() : 0);
        return result;
    }
}
