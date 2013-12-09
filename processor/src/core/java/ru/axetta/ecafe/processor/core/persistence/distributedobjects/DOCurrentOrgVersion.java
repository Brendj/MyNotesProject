package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequest;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.consumer.GoodRequestPosition;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.12.13
 * Time: 14:57
 * To change this template use File | Settings | File Templates.
 */
public class DOCurrentOrgVersion {

    public final static int GOOD_REQUEST = 0;
    public final static int GOOD_REQUEST_POSITION = 1;
    public final static Map<Integer, Class> map = new HashMap<Integer, Class>();
    static {
        map.put(GOOD_REQUEST, GoodRequest.class);
        map.put(GOOD_REQUEST_POSITION, GoodRequestPosition.class);
    }

    private Long iDDOOrgCurrentVersion;
    private Long idOfOrg;
    private Long lastVersion;
    private Integer objectId;

    public Long getiDDOOrgCurrentVersion() {
        return iDDOOrgCurrentVersion;
    }

    public void setiDDOOrgCurrentVersion(Long iDDOOrgCurrentVersion) {
        this.iDDOOrgCurrentVersion = iDDOOrgCurrentVersion;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Long lastVersion) {
        this.lastVersion = lastVersion;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }
}
