/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.productGuide;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.FieldProcessor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 19.05.12
 * Time: 13:09
 * To change this template use File | Settings | File Templates.
 */
public class ProductGuideManager {

    private static final Logger logger = LoggerFactory.getLogger(ProductGuideManager.class);

    public enum FieldId {
        CODE, FULL_NAME, OKP_CODE, PRODUCT_NAME, DELETED_STATE
    }

    static FieldProcessor.Def[] fieldInfo={
            //new FieldProcessor.Def(0, false, true, "Идентификаторв", null, FieldId.ID_OF_PRODUCT_GUIDE, false),
            new FieldProcessor.Def(0, true, false, "CODE", null, FieldId.CODE, true),
            new FieldProcessor.Def(1, false, false, "FULL_NAME", null, FieldId.FULL_NAME, true),
            new FieldProcessor.Def(2, false, false, "OKP", null, FieldId.OKP_CODE, true),
            new FieldProcessor.Def(3, false, false, "PRODUCT_NAME", null, FieldId.PRODUCT_NAME, true),
            new FieldProcessor.Def(4, false, false, "DELETED_STATE", "0", FieldId.DELETED_STATE, true),
    };

    public static class ProductGuideFieldConfig extends FieldProcessor.Config {

        public ProductGuideFieldConfig() {
            super(fieldInfo, true);
        }

        @Override
        public void checkRequiredFields() throws Exception {
            if (nFields>0) {
                for (FieldProcessor.Def fd : currentConfig) {
                    if (fd.requiredForInsert && fd.realPos==-1) throw new Exception("В списке полей отсутствует обязательное поле: "+fd.fieldName);
                }
                //if (getField(FieldId.CARD_ID).realPos!=-1) {
                //    if (getField(FieldId.CARD_TYPE).realPos==-1) throw new Exception("В списке полей отсутствует обязательное поле: "+getField(FieldId.CARD_TYPE).fieldName);
                //}
            }
        }
    }

    public static long insertProductGuide(ProductGuideManager.ProductGuideFieldConfig config) throws Exception {
        config.checkRequiredFields();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            ConfigurationProvider configurationProviderp = (ConfigurationProvider) DAOUtils
                    .findConfigurationProvider(persistenceSession, MainPage.getSessionInstance().getCurrentConfigurationProvider());

            Product product = new Product();
            product.setOkpCode(config.getValue(FieldId.OKP_CODE));
            product.setDeletedState(!config.getValueBool(FieldId.DELETED_STATE));
            product.setCode(config.getValue(FieldId.CODE));
            product.setFullName(config.getValue(FieldId.FULL_NAME));
            product.setProductName(config.getValue(FieldId.PRODUCT_NAME));
            product.setUserCreate(MainPage.getSessionInstance().getCurrentUser());
            product.setCreatedDate(new Date());
            persistenceSession.save(product);

            //configurationProviderp.getProducts().add(productGuide);
            persistenceSession.update(configurationProviderp);

            persistenceTransaction.commit();
            persistenceTransaction = null;
            return product.getIdOfProductGuide();
        } catch (Exception e) {
            logger.info("Ошибка при обновлении данных клиента", e);
            throw new Exception(e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }
}
