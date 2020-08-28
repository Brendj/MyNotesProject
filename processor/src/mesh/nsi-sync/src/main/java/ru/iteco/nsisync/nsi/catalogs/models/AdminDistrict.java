package ru.iteco.nsisync.nsi.catalogs.models;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cf_kf_ct_AdminDistrict")
public class AdminDistrict extends AbstractCatalog {
    public AdminDistrict(Long globalID, Long systemObjectId, String title, Integer isDelete){
        this.globalID = globalID;
        this.systemObjectId = systemObjectId;
        this.title = title;
        this.isDelete = isDelete;
    }

    public AdminDistrict(){
        //for Hibernate
    }
}
