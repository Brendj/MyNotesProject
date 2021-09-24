/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.revise;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.service.nsi.ReviseLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("ReviseDAOService")
@Scope("singleton")
public class ReviseDAOService {
    private static final Logger logger = LoggerFactory.getLogger(ReviseDAOService.class);
    private static ReviseLogger reviseLogger = RuntimeContext.getAppContext().getBean(ReviseLogger.class);

    @PersistenceContext(unitName = "revisePU")
    private EntityManager entityManager;

    public DiscountItemsWithTimestamp getDiscountsUpdatedSinceDate(Date updated) {
        String sqlString = "select registry_guid, dszn_code, title, sd, sd_dszn, fd, fd_dszn, is_benefit_confirm, updated_at, is_del, mesh_guid "
                + " from benefits_for_ispp where updated_at > :updatedDate and mesh_guid is not null order by updated_at asc limit :lim";
        Query query = entityManager.createNativeQuery(sqlString);
        query.setParameter("updatedDate", updated);
        query.setParameter("lim", RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_REVISE_LIMIT));
        try {
            reviseLogger.logRequestDB(query, sqlString);
        } catch (Exception e) {
            logger.warn("Unable to log revise request");
        }
        DiscountItemsWithTimestamp discountItemList = parseDiscounts(query.getResultList());
        try {
            reviseLogger.logResponseDB(discountItemList.getItems());
        } catch (Exception e) {
            logger.warn("Unable to log revise response");
        }
        return discountItemList;
    }

    public DiscountItemsWithTimestamp getDiscountsByGUID(String guid) {
        String sqlString = "select registry_guid, dszn_code, title, sd, sd_dszn, fd, fd_dszn, is_benefit_confirm, updated_at, is_del, mesh_guid "
                + " from benefits_for_ispp where registry_guid = :guid or mesh_guid = :guid order by updated_at asc";
        Query query = entityManager.createNativeQuery(sqlString);
        query.setParameter("guid", guid);
        try {
            reviseLogger.logRequestDB(query, sqlString);
        } catch (Exception e) {
            logger.warn("Unable to log revise request");
        }
        DiscountItemsWithTimestamp discountItemList = parseDiscounts(query.getResultList());
        try {
            reviseLogger.logResponseDB(discountItemList.getItems());
        } catch (Exception e) {
            logger.warn("Unable to log revise response");
        }
        return discountItemList;
    }

    private DiscountItemsWithTimestamp parseDiscounts(List list) {
        List<DiscountItem> discountItemList = new ArrayList<DiscountItem>();
        Date updatedAt = null;
        for (Object o : list) {
            Object[] row = (Object[]) o;
            String registryGUID = (String) row[0];
            String dsznCodeString = (String) row[1];
            Integer dsznCode;
            try {
                dsznCode = Integer.parseInt(dsznCodeString.trim());
            } catch (NumberFormatException e) {
                logger.error(String.format("Unable to parse dsznCode: %s", dsznCodeString));
                continue;
            }
            String title = (String) row[2];
            Date sd = (Date) row[3];
            Date sdDszn = (Date) row[4];
            Date fd = (Date) row[5];
            Date fdDszn = (Date) row[6];
            Boolean isBenefitConfirmed = (Boolean) row[7];
            updatedAt = (Date) row[8];
            Boolean isDeleted = (Boolean) row[9];
            String mesh_guid = (String) row[10];
            discountItemList.add(new DiscountItem(registryGUID, dsznCode, title, sd, sdDszn, fd, fdDszn, isBenefitConfirmed,
                    updatedAt, isDeleted, mesh_guid));

        }
        return new DiscountItemsWithTimestamp(discountItemList, updatedAt);
    }

    public static class DiscountItemsWithTimestamp {
        private Date date;
        private List<DiscountItem> items;

        public DiscountItemsWithTimestamp(List<DiscountItem> items, Date date) {
            this.items = items;
            this.date = date;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public List<DiscountItem> getItems() {
            return items;
        }

        public void setItems(List<DiscountItem> items) {
            this.items = items;
        }
    }

    public static class DiscountItem {
        private String registryGUID;
        private String meshGUID;
        private Integer dsznCode;
        private String title;
        private Date sd;
        private Date sdDszn;
        private Date fd;
        private Date fdDszn;
        private Boolean isBenefitConfirm;
        private Date updatedAt;
        private Boolean isDeleted;

        public DiscountItem(String registryGUID, Integer dsznCode, String title, Date sd, Date sdDszn, Date fd, Date fdDszn,
                Boolean isBenefitConfirm, Date updatedAt, Boolean isDeleted, String meshGUID) {
            this.registryGUID = registryGUID;
            this.dsznCode = dsznCode;
            this.title = title;
            this.sd = sd;
            this.sdDszn = sdDszn;
            this.fd = fd;
            this.fdDszn = fdDszn;
            this.isBenefitConfirm = isBenefitConfirm;
            this.updatedAt = updatedAt;
            this.isDeleted = isDeleted;
            this.meshGUID = meshGUID;
        }

        public String getRegistryGUID() {
            return registryGUID;
        }

        public void setRegistryGUID(String registryGUID) {
            this.registryGUID = registryGUID;
        }

        public Integer getDsznCode() {
            return dsznCode;
        }

        public void setDsznCode(Integer dsznCode) {
            this.dsznCode = dsznCode;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Date getSd() {
            return sd;
        }

        public void setSd(Date sd) {
            this.sd = sd;
        }

        public Date getSdDszn() {
            return sdDszn;
        }

        public void setSdDszn(Date sdDszn) {
            this.sdDszn = sdDszn;
        }

        public Date getFd() {
            return fd;
        }

        public void setFd(Date fd) {
            this.fd = fd;
        }

        public Date getFdDszn() {
            return fdDszn;
        }

        public void setFdDszn(Date fdDszn) {
            this.fdDszn = fdDszn;
        }

        public Boolean getBenefitConfirm() {
            return isBenefitConfirm;
        }

        public void setBenefitConfirm(Boolean benefitConfirm) {
            isBenefitConfirm = benefitConfirm;
        }

        public Date getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Boolean getDeleted() {
            return isDeleted;
        }

        public void setDeleted(Boolean deleted) {
            isDeleted = deleted;
        }

        public String getMeshGUID() {
            return meshGUID;
        }

        public void setMeshGUID(String meshGUID) {
            this.meshGUID = meshGUID;
        }
    }
}
