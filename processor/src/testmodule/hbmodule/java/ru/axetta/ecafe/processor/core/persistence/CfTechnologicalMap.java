package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfTechnologicalMap {

    private long idoftechnologicalmaps;

    public long getIdoftechnologicalmaps() {
        return idoftechnologicalmaps;
    }

    public void setIdoftechnologicalmaps(long idoftechnologicalmaps) {
        this.idoftechnologicalmaps = idoftechnologicalmaps;
    }

    private long idoftechmapgroups;

    public long getIdoftechmapgroups() {
        return idoftechmapgroups;
    }

    public void setIdoftechmapgroups(long idoftechmapgroups) {
        this.idoftechmapgroups = idoftechmapgroups;
    }

    private String nameoftechnologicalmap;

    public String getNameoftechnologicalmap() {
        return nameoftechnologicalmap;
    }

    public void setNameoftechnologicalmap(String nameoftechnologicalmap) {
        this.nameoftechnologicalmap = nameoftechnologicalmap;
    }

    private String numberoftechnologicalmap;

    public String getNumberoftechnologicalmap() {
        return numberoftechnologicalmap;
    }

    public void setNumberoftechnologicalmap(String numberoftechnologicalmap) {
        this.numberoftechnologicalmap = numberoftechnologicalmap;
    }

    private String technologyofpreparation;

    public String getTechnologyofpreparation() {
        return technologyofpreparation;
    }

    public void setTechnologyofpreparation(String technologyofpreparation) {
        this.technologyofpreparation = technologyofpreparation;
    }

    private String tempofpreparation;

    public String getTempofpreparation() {
        return tempofpreparation;
    }

    public void setTempofpreparation(String tempofpreparation) {
        this.tempofpreparation = tempofpreparation;
    }

    private double energyvalue;

    public double getEnergyvalue() {
        return energyvalue;
    }

    public void setEnergyvalue(double energyvalue) {
        this.energyvalue = energyvalue;
    }

    private double proteins;

    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    private double carbohydrates;

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    private double fats;

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    private double microelca;

    public double getMicroelca() {
        return microelca;
    }

    public void setMicroelca(double microelca) {
        this.microelca = microelca;
    }

    private double microelmg;

    public double getMicroelmg() {
        return microelmg;
    }

    public void setMicroelmg(double microelmg) {
        this.microelmg = microelmg;
    }

    private double microelp;

    public double getMicroelp() {
        return microelp;
    }

    public void setMicroelp(double microelp) {
        this.microelp = microelp;
    }

    private double microelfe;

    public double getMicroelfe() {
        return microelfe;
    }

    public void setMicroelfe(double microelfe) {
        this.microelfe = microelfe;
    }

    private double vitamina;

    public double getVitamina() {
        return vitamina;
    }

    public void setVitamina(double vitamina) {
        this.vitamina = vitamina;
    }

    private double vitaminb1;

    public double getVitaminb1() {
        return vitaminb1;
    }

    public void setVitaminb1(double vitaminb1) {
        this.vitaminb1 = vitaminb1;
    }

    private double vitaminb2;

    public double getVitaminb2() {
        return vitaminb2;
    }

    public void setVitaminb2(double vitaminb2) {
        this.vitaminb2 = vitaminb2;
    }

    private double vitaminpp;

    public double getVitaminpp() {
        return vitaminpp;
    }

    public void setVitaminpp(double vitaminpp) {
        this.vitaminpp = vitaminpp;
    }

    private double vitaminc;

    public double getVitaminc() {
        return vitaminc;
    }

    public void setVitaminc(double vitaminc) {
        this.vitaminc = vitaminc;
    }

    private double vitamine;

    public double getVitamine() {
        return vitamine;
    }

    public void setVitamine(double vitamine) {
        this.vitamine = vitamine;
    }

    private String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    private long globalversion;

    public long getGlobalversion() {
        return globalversion;
    }

    public void setGlobalversion(long globalversion) {
        this.globalversion = globalversion;
    }

    private long orgowner;

    public long getOrgowner() {
        return orgowner;
    }

    public void setOrgowner(long orgowner) {
        this.orgowner = orgowner;
    }

    private boolean deletedstate;

    public boolean isDeletedstate() {
        return deletedstate;
    }

    public void setDeletedstate(boolean deletedstate) {
        this.deletedstate = deletedstate;
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

    private long deletedate;

    public long getDeletedate() {
        return deletedate;
    }

    public void setDeletedate(long deletedate) {
        this.deletedate = deletedate;
    }

    private long idofusercreate;

    public long getIdofusercreate() {
        return idofusercreate;
    }

    public void setIdofusercreate(long idofusercreate) {
        this.idofusercreate = idofusercreate;
    }

    private long idofuseredit;

    public long getIdofuseredit() {
        return idofuseredit;
    }

    public void setIdofuseredit(long idofuseredit) {
        this.idofuseredit = idofuseredit;
    }

    private long idofuserdelete;

    public long getIdofuserdelete() {
        return idofuserdelete;
    }

    public void setIdofuserdelete(long idofuserdelete) {
        this.idofuserdelete = idofuserdelete;
    }

    private long idofconfigurationprovider;

    public long getIdofconfigurationprovider() {
        return idofconfigurationprovider;
    }

    public void setIdofconfigurationprovider(long idofconfigurationprovider) {
        this.idofconfigurationprovider = idofconfigurationprovider;
    }

    private int sendall;

    public int getSendall() {
        return sendall;
    }

    public void setSendall(int sendall) {
        this.sendall = sendall;
    }

    private int lifetime;

    public int getLifetime() {
        return lifetime;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfTechnologicalMap that = (CfTechnologicalMap) o;

        if (Double.compare(that.carbohydrates, carbohydrates) != 0) {
            return false;
        }
        if (createddate != that.createddate) {
            return false;
        }
        if (deletedate != that.deletedate) {
            return false;
        }
        if (deletedstate != that.deletedstate) {
            return false;
        }
        if (Double.compare(that.energyvalue, energyvalue) != 0) {
            return false;
        }
        if (Double.compare(that.fats, fats) != 0) {
            return false;
        }
        if (globalversion != that.globalversion) {
            return false;
        }
        if (idofconfigurationprovider != that.idofconfigurationprovider) {
            return false;
        }
        if (idoftechmapgroups != that.idoftechmapgroups) {
            return false;
        }
        if (idoftechnologicalmaps != that.idoftechnologicalmaps) {
            return false;
        }
        if (idofusercreate != that.idofusercreate) {
            return false;
        }
        if (idofuserdelete != that.idofuserdelete) {
            return false;
        }
        if (idofuseredit != that.idofuseredit) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (lifetime != that.lifetime) {
            return false;
        }
        if (Double.compare(that.microelca, microelca) != 0) {
            return false;
        }
        if (Double.compare(that.microelfe, microelfe) != 0) {
            return false;
        }
        if (Double.compare(that.microelmg, microelmg) != 0) {
            return false;
        }
        if (Double.compare(that.microelp, microelp) != 0) {
            return false;
        }
        if (orgowner != that.orgowner) {
            return false;
        }
        if (Double.compare(that.proteins, proteins) != 0) {
            return false;
        }
        if (sendall != that.sendall) {
            return false;
        }
        if (Double.compare(that.vitamina, vitamina) != 0) {
            return false;
        }
        if (Double.compare(that.vitaminb1, vitaminb1) != 0) {
            return false;
        }
        if (Double.compare(that.vitaminb2, vitaminb2) != 0) {
            return false;
        }
        if (Double.compare(that.vitaminc, vitaminc) != 0) {
            return false;
        }
        if (Double.compare(that.vitamine, vitamine) != 0) {
            return false;
        }
        if (Double.compare(that.vitaminpp, vitaminpp) != 0) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }
        if (nameoftechnologicalmap != null ? !nameoftechnologicalmap.equals(that.nameoftechnologicalmap)
                : that.nameoftechnologicalmap != null) {
            return false;
        }
        if (numberoftechnologicalmap != null ? !numberoftechnologicalmap.equals(that.numberoftechnologicalmap)
                : that.numberoftechnologicalmap != null) {
            return false;
        }
        if (technologyofpreparation != null ? !technologyofpreparation.equals(that.technologyofpreparation)
                : that.technologyofpreparation != null) {
            return false;
        }
        if (tempofpreparation != null ? !tempofpreparation.equals(that.tempofpreparation)
                : that.tempofpreparation != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (idoftechnologicalmaps ^ (idoftechnologicalmaps >>> 32));
        result = 31 * result + (int) (idoftechmapgroups ^ (idoftechmapgroups >>> 32));
        result = 31 * result + (nameoftechnologicalmap != null ? nameoftechnologicalmap.hashCode() : 0);
        result = 31 * result + (numberoftechnologicalmap != null ? numberoftechnologicalmap.hashCode() : 0);
        result = 31 * result + (technologyofpreparation != null ? technologyofpreparation.hashCode() : 0);
        result = 31 * result + (tempofpreparation != null ? tempofpreparation.hashCode() : 0);
        temp = energyvalue != +0.0d ? Double.doubleToLongBits(energyvalue) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = proteins != +0.0d ? Double.doubleToLongBits(proteins) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = carbohydrates != +0.0d ? Double.doubleToLongBits(carbohydrates) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = fats != +0.0d ? Double.doubleToLongBits(fats) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = microelca != +0.0d ? Double.doubleToLongBits(microelca) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = microelmg != +0.0d ? Double.doubleToLongBits(microelmg) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = microelp != +0.0d ? Double.doubleToLongBits(microelp) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = microelfe != +0.0d ? Double.doubleToLongBits(microelfe) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = vitamina != +0.0d ? Double.doubleToLongBits(vitamina) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = vitaminb1 != +0.0d ? Double.doubleToLongBits(vitaminb1) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = vitaminb2 != +0.0d ? Double.doubleToLongBits(vitaminb2) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = vitaminpp != +0.0d ? Double.doubleToLongBits(vitaminpp) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = vitaminc != +0.0d ? Double.doubleToLongBits(vitaminc) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = vitamine != +0.0d ? Double.doubleToLongBits(vitamine) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (int) (idofusercreate ^ (idofusercreate >>> 32));
        result = 31 * result + (int) (idofuseredit ^ (idofuseredit >>> 32));
        result = 31 * result + (int) (idofuserdelete ^ (idofuserdelete >>> 32));
        result = 31 * result + (int) (idofconfigurationprovider ^ (idofconfigurationprovider >>> 32));
        result = 31 * result + sendall;
        result = 31 * result + lifetime;
        return result;
    }

    private CfTechnologicalMapGroups cfTechnologicalMapGroupsByIdoftechmapgroups;

    public CfTechnologicalMapGroups getCfTechnologicalMapGroupsByIdoftechmapgroups() {
        return cfTechnologicalMapGroupsByIdoftechmapgroups;
    }

    public void setCfTechnologicalMapGroupsByIdoftechmapgroups(
            CfTechnologicalMapGroups cfTechnologicalMapGroupsByIdoftechmapgroups) {
        this.cfTechnologicalMapGroupsByIdoftechmapgroups = cfTechnologicalMapGroupsByIdoftechmapgroups;
    }

    private Collection<CfTechnologicalMapProducts> cfTechnologicalMapProductsesByIdoftechnologicalmaps;

    public Collection<CfTechnologicalMapProducts> getCfTechnologicalMapProductsesByIdoftechnologicalmaps() {
        return cfTechnologicalMapProductsesByIdoftechnologicalmaps;
    }

    public void setCfTechnologicalMapProductsesByIdoftechnologicalmaps(
            Collection<CfTechnologicalMapProducts> cfTechnologicalMapProductsesByIdoftechnologicalmaps) {
        this.cfTechnologicalMapProductsesByIdoftechnologicalmaps = cfTechnologicalMapProductsesByIdoftechnologicalmaps;
    }
}
