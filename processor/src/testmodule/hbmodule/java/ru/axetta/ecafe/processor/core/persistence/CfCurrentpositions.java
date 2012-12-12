package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class CfCurrentpositions {

    private long idofposition;

    public long getIdofposition() {
        return idofposition;
    }

    public void setIdofposition(long idofposition) {
        this.idofposition = idofposition;
    }

    private long idofcontragentdebtor;

    public long getIdofcontragentdebtor() {
        return idofcontragentdebtor;
    }

    public void setIdofcontragentdebtor(long idofcontragentdebtor) {
        this.idofcontragentdebtor = idofcontragentdebtor;
    }

    private long idofcontragentcreditor;

    public long getIdofcontragentcreditor() {
        return idofcontragentcreditor;
    }

    public void setIdofcontragentcreditor(long idofcontragentcreditor) {
        this.idofcontragentcreditor = idofcontragentcreditor;
    }

    private long summa;

    public long getSumma() {
        return summa;
    }

    public void setSumma(long summa) {
        this.summa = summa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfCurrentpositions that = (CfCurrentpositions) o;

        if (idofcontragentcreditor != that.idofcontragentcreditor) {
            return false;
        }
        if (idofcontragentdebtor != that.idofcontragentdebtor) {
            return false;
        }
        if (idofposition != that.idofposition) {
            return false;
        }
        if (summa != that.summa) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofposition ^ (idofposition >>> 32));
        result = 31 * result + (int) (idofcontragentdebtor ^ (idofcontragentdebtor >>> 32));
        result = 31 * result + (int) (idofcontragentcreditor ^ (idofcontragentcreditor >>> 32));
        result = 31 * result + (int) (summa ^ (summa >>> 32));
        return result;
    }

    private CfContragents cfContragentsByIdofcontragentcreditor;

    public CfContragents getCfContragentsByIdofcontragentcreditor() {
        return cfContragentsByIdofcontragentcreditor;
    }

    public void setCfContragentsByIdofcontragentcreditor(CfContragents cfContragentsByIdofcontragentcreditor) {
        this.cfContragentsByIdofcontragentcreditor = cfContragentsByIdofcontragentcreditor;
    }

    private CfContragents cfContragentsByIdofcontragentdebtor;

    public CfContragents getCfContragentsByIdofcontragentdebtor() {
        return cfContragentsByIdofcontragentdebtor;
    }

    public void setCfContragentsByIdofcontragentdebtor(CfContragents cfContragentsByIdofcontragentdebtor) {
        this.cfContragentsByIdofcontragentdebtor = cfContragentsByIdofcontragentdebtor;
    }
}
