package ru.axetta.ecafe.processor.web.ui.guardianservice;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CGCardItem implements Comparable {
    private Long idOfCard;
    private Long idOfClient;
    private Long cardLastUpdate;
    private Long idOfClientGroup;
    private Long guardianLastUpdate;
    private Long cardOrg;

    public CGCardItem(Long idOfCard, Long idOfClient, Long cardLastUpdate, Long idOfClientGroup, Long guardianLastUpdate,
                      Long cardOrg) {
        this.idOfCard = idOfCard;
        this.idOfClient = idOfClient;
        this.cardLastUpdate = cardLastUpdate;
        this.idOfClientGroup = idOfClientGroup;
        this.guardianLastUpdate = guardianLastUpdate;
        this.cardOrg = cardOrg;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof CGCardItem)) {
            return 1;
        }
        CGCardItem item = (CGCardItem) o;
        if (this.cardLastUpdate == null && item.getCardLastUpdate() == null) return 0;
        if (this.cardLastUpdate != null && item.getCardLastUpdate() == null) return -1;
        if (this.cardLastUpdate == null && item.getCardLastUpdate() != null) return 1;
        int res = -cardLastUpdate.compareTo(item.getCardLastUpdate());
        if (res == 0) {
            int indexThis = CGItem.GROUPS.indexOf(this.idOfClientGroup);
            int indexItem = CGItem.GROUPS.indexOf(item.getIdOfClientGroup());
            if ((indexThis == -1) && (indexItem == -1)) return 0;
            if ((indexThis == -1) && (indexItem > -1)) return -1;
            if ((indexThis > -1) && (indexItem == -1)) return 1;
            int res2 = Integer.valueOf(indexThis).compareTo(indexItem);
            if (res2 == 0) {
                return -guardianLastUpdate.compareTo(item.getGuardianLastUpdate());
            } else {
                return res2;
            }
        } else {
            return res;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CGCardItem)) {
            return false;
        }
        CGCardItem item = (CGCardItem) o;
        return idOfClient.equals(item.getIdOfClient()) && equalsByCard(item);
    }

    private boolean equalsByCard(CGCardItem item) {
        if (this.idOfCard == null && item.getIdOfCard() == null) return true;
        if (this.idOfCard != null && item.getIdOfCard() == null) return false;
        if (this.idOfCard == null && item.getIdOfCard() != null) return false;
        return idOfCard.equals(item.getIdOfCard());
    }

    public Long getCardLastUpdate() {
        return cardLastUpdate;
    }

    public void setCardLastUpdate(Long cardLastUpdate) {
        this.cardLastUpdate = cardLastUpdate;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public Long getGuardianLastUpdate() {
        return guardianLastUpdate;
    }

    public void setGuardianLastUpdate(Long guardianLastUpdate) {
        this.guardianLastUpdate = guardianLastUpdate;
    }

    public Long getCardOrg() {
        return cardOrg;
    }

    public void setCardOrg(Long cardOrg) {
        this.cardOrg = cardOrg;
    }
}
