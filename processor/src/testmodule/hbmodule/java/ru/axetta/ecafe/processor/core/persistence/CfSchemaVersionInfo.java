package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfSchemaVersionInfo {

    private long schemaversioninfoid;

    public long getSchemaversioninfoid() {
        return schemaversioninfoid;
    }

    public void setSchemaversioninfoid(long schemaversioninfoid) {
        this.schemaversioninfoid = schemaversioninfoid;
    }

    private int majorversionnum;

    public int getMajorversionnum() {
        return majorversionnum;
    }

    public void setMajorversionnum(int majorversionnum) {
        this.majorversionnum = majorversionnum;
    }

    private int middleversionnum;

    public int getMiddleversionnum() {
        return middleversionnum;
    }

    public void setMiddleversionnum(int middleversionnum) {
        this.middleversionnum = middleversionnum;
    }

    private int minorversionnum;

    public int getMinorversionnum() {
        return minorversionnum;
    }

    public void setMinorversionnum(int minorversionnum) {
        this.minorversionnum = minorversionnum;
    }

    private int buildversionnum;

    public int getBuildversionnum() {
        return buildversionnum;
    }

    public void setBuildversionnum(int buildversionnum) {
        this.buildversionnum = buildversionnum;
    }

    private long updatetime;

    public long getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    private String committext;

    public String getCommittext() {
        return committext;
    }

    public void setCommittext(String committext) {
        this.committext = committext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfSchemaVersionInfo that = (CfSchemaVersionInfo) o;

        if (buildversionnum != that.buildversionnum) {
            return false;
        }
        if (majorversionnum != that.majorversionnum) {
            return false;
        }
        if (middleversionnum != that.middleversionnum) {
            return false;
        }
        if (minorversionnum != that.minorversionnum) {
            return false;
        }
        if (schemaversioninfoid != that.schemaversioninfoid) {
            return false;
        }
        if (updatetime != that.updatetime) {
            return false;
        }
        if (committext != null ? !committext.equals(that.committext) : that.committext != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (schemaversioninfoid ^ (schemaversioninfoid >>> 32));
        result = 31 * result + majorversionnum;
        result = 31 * result + middleversionnum;
        result = 31 * result + minorversionnum;
        result = 31 * result + buildversionnum;
        result = 31 * result + (int) (updatetime ^ (updatetime >>> 32));
        result = 31 * result + (committext != null ? committext.hashCode() : 0);
        return result;
    }
}
