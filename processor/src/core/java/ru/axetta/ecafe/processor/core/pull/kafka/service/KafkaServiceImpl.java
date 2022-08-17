package ru.axetta.ecafe.processor.core.pull.kafka.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVDaoService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.Errors;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.ActiveBenefitCategoriesGettingResponse;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.BenefitCategoryInfo;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.BenefitDocument;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian.RelatednessChecking2Response;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.passport.PassportBySerieNumberValidityCheckingResponse;

import java.util.*;

@Service
public class KafkaServiceImpl {

    private final Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);

    public void processingActiveBenefitCategories(AbstractPullData data, String message)
    {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            ActiveBenefitCategoriesGettingResponse activeBenefitCategoriesGettingResponse = (ActiveBenefitCategoriesGettingResponse) data;
            String requestId = activeBenefitCategoriesGettingResponse.getRequest_id();

            //Заполнение полей таблицы AppMezhvedRequest
            AppMezhvedRequest appMezhvedRequest = updateMezved(requestId, activeBenefitCategoriesGettingResponse.getErrors(), message, session);

            //Проставление статуса подтверждения для ЛК
            for (ApplicationForFoodDiscount applicationForFoodDiscount: appMezhvedRequest.getApplicationForFood().getDtisznCodes())
            {
                for (BenefitCategoryInfo benefitCategoryInfo: activeBenefitCategoriesGettingResponse.getActive_benefit_categories_info())
                {
                    if (Objects.equals(applicationForFoodDiscount.getDtisznCode(), Integer.valueOf(benefitCategoryInfo.getBenefit_category_id())))
                    {
                        applicationForFoodDiscount.setConfirmed(true);
                        session.save(applicationForFoodDiscount);
                        break;
                    }
                }
            }
            //Сохранение сопуствующих документов
            for (BenefitDocument benefitDocument: activeBenefitCategoriesGettingResponse.getBenefit_activity_starting_reason_documents())
            {
                AppMezhvedResponseDocument appMezhvedResponseDocument = new AppMezhvedResponseDocument(benefitDocument,
                        requestId, AppMezhvedResponseDocDirection.STARTING);
                session.persist(appMezhvedResponseDocument);
            }
            for (BenefitDocument benefitDocument: activeBenefitCategoriesGettingResponse.getBenefit_activity_ending_reason_documents())
            {
                AppMezhvedResponseDocument appMezhvedResponseDocument = new AppMezhvedResponseDocument(benefitDocument,
                        requestId, AppMezhvedResponseDocDirection.ENDING);
                session.persist(appMezhvedResponseDocument);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void processingPassportValidation(AbstractPullData data, String message)
    {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            PassportBySerieNumberValidityCheckingResponse passport = (PassportBySerieNumberValidityCheckingResponse) data;

            AppMezhvedRequest appMezhvedRequest = updateMezved(passport.getRequest_id(), passport.getErrors(), message, session);

            if (CalendarUtils.daysBetween(appMezhvedRequest.getCreatedDate(), new Date()).size() >=5)
            {
                //Если прошло более 5 дней с подачи запроса
                appMezhvedRequest.getApplicationForFood().setStatus(new ApplicationForFoodStatus(
                        ApplicationForFoodState.DENIED_GUARDIANSHIP));
            }
            else {
                if (passport.getPassportValidityInfo() != null &&
                        Objects.equals(passport.getPassportValidityInfo().getPassport_status_info().getPassport_status(), "Действителен")) {
                    appMezhvedRequest.getApplicationForFood().setStatus(new ApplicationForFoodStatus(
                            ApplicationForFoodState.INFORMATION_RESPONSE_PASSPORT));
                } else {
                    appMezhvedRequest.getApplicationForFood().setStatus(new ApplicationForFoodStatus(
                            ApplicationForFoodState.DENIED_GUARDIANSHIP));
                }
            }
            session.save(appMezhvedRequest.getApplicationForFood());
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void processingGuardianValidation(AbstractPullData data, String message)
    {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            RelatednessChecking2Response relatednessChecking2Response = (RelatednessChecking2Response) data;

            AppMezhvedRequest appMezhvedRequest =
                    updateMezved(relatednessChecking2Response.getRequest_id(), relatednessChecking2Response.getErrors(), message, session);

            if (CalendarUtils.daysBetween(appMezhvedRequest.getCreatedDate(), new Date()).size() >=5)
            {
                //Если прошло более 5 дней с подачи запроса
                appMezhvedRequest.getApplicationForFood().setStatus(new ApplicationForFoodStatus(
                        ApplicationForFoodState.DENIED_GUARDIANSHIP));
            }
            else {
                if (relatednessChecking2Response.getRelatednessInfo() != null &&
                        relatednessChecking2Response.getRelatednessInfo().getRelatedness_confirmation().toLowerCase().equals("да")) {
                    appMezhvedRequest.getApplicationForFood().setStatus(new ApplicationForFoodStatus(
                            ApplicationForFoodState.INFORMATION_RESPONSE_GUARDIAN));
                } else {
                    appMezhvedRequest.getApplicationForFood().setStatus(new ApplicationForFoodStatus(
                            ApplicationForFoodState.DENIED_GUARDIANSHIP));
                }
            }
            session.save(appMezhvedRequest.getApplicationForFood());
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private AppMezhvedRequest updateMezved(String requestId, List<Errors> errors, String message, Session session)
    {
        ETPMVDaoService daoService = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class);
        AppMezhvedRequest appMezhvedRequest = daoService.getMezhvedData(requestId);
        if (errors != null)
            appMezhvedRequest.setResponseType(AppMezhvedResponseType.ERROR);
        else
            appMezhvedRequest.setResponseType(AppMezhvedResponseType.OK);
        appMezhvedRequest.setResponsePayload(message);
        appMezhvedRequest.setResponseDate(new Date());
        appMezhvedRequest.setLastUpdate(new Date());
        session.persist(appMezhvedRequest);
        return appMezhvedRequest;
    }
}
