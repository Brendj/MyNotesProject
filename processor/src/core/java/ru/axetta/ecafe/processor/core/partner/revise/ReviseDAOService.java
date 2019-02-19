/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.revise;

import ru.axetta.ecafe.processor.core.RuntimeContext;
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

    public List<DiscountItem> getDiscountsUpdatedSinceDate(Date updated) {
        String sqlString = "select registry_guid, dszn_code, title, sd, sd_dszn, fd, fd_dszn, is_benefit_confirm, updated_at, is_del "
                + " from benefits_for_ispp where updated_at >= :updatedDate and registry_guid is not null order by updated_at asc";
        Query query = entityManager.createNativeQuery(sqlString);
        query.setParameter("updatedDate", updated);
        try {
            reviseLogger.logRequestDB(query, sqlString);
        } catch (Exception e) {
            logger.warn("Unable to log revise request");
        }
        List<DiscountItem> discountItemList = parseDiscounts(query.getResultList());
        try {
            reviseLogger.logResponseDB(discountItemList);
        } catch (Exception e) {
            logger.warn("Unable to log revise response");
        }
        return discountItemList;
    }

    public List<DiscountItem> getDiscountsByGUID(String guid) {
        String sqlString = "select registry_guid, dszn_code, title, sd, sd_dszn, fd, fd_dszn, is_benefit_confirm, updated_at, is_del "
                + " from benefits_for_ispp where registry_guid = :guid order by updated_at asc";
        Query query = entityManager.createNativeQuery(sqlString);
        query.setParameter("guid", guid);
        try {
            reviseLogger.logRequestDB(query, sqlString);
        } catch (Exception e) {
            logger.warn("Unable to log revise request");
        }
        List<DiscountItem> discountItemList = parseDiscounts(query.getResultList());
        try {
            reviseLogger.logResponseDB(discountItemList);
        } catch (Exception e) {
            logger.warn("Unable to log revise response");
        }
        return discountItemList;
    }

    private List<DiscountItem> parseDiscounts(List list) {
        List<DiscountItem> discountItemList = new ArrayList<DiscountItem>();
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
            Date updatedAt = (Date) row[8];
            Boolean isDeleted = (Boolean) row[9];
            discountItemList.add(new DiscountItem(registryGUID, dsznCode, title, sd, sdDszn, fd, fdDszn, isBenefitConfirmed,
                    updatedAt, isDeleted));
        }
        return discountItemList;
    }

    public static class DiscountItem {
        private String registryGUID;
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
                Boolean isBenefitConfirm, Date updatedAt, Boolean isDeleted) {
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
    }
}
