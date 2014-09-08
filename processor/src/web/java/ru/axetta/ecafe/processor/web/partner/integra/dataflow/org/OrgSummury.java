package ru.axetta.ecafe.processor.web.partner.integra.dataflow.org;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;

/**
 * User: Shamil
 * Date: 08.09.14
 */
public class OrgSummury {
    public Long id;
    public String name;
    public String orgType;

    public OrgSummury() {
    }

    public OrgSummury(Org org) {
        this.id = org.getIdOfOrg();
        this.name = org.getOfficialName();
        this.setOrgType(org.getType());
    }

    public void setOrgType(OrganizationType type) {
        if (OrganizationType.KINDERGARTEN.equals(type) ){
            orgType = "ch";
        }else if(OrganizationType.SCHOOL.equals(type)){
            orgType = "sc";
        }else if (OrganizationType.PROFESSIONAL.equals(type)){
            orgType = "st";
        }else if (OrganizationType.SUPPLIER.equals(type)){
            orgType = "su";
        }
    }
}
