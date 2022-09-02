package ru.axetta.ecafe.processor.core.pull.kafka.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVDaoService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.pull.model.AbstractPullData;
import ru.axetta.ecafe.processor.core.service.nsi.DTSZNDiscountsReviseService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.Errors;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.ActiveBenefitCategoriesGettingResponse;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.BenefitCategoryInfo;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.BenefitDocument;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.benefit.BenefitResponse;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian.GuardianResponse;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.guardian.RelatednessChecking2Response;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.passport.PassportBySerieNumberValidityCheckingResponse;
import ru.axetta.ecafe.processor.core.zlp.kafka.response.passport.PassportResponse;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class KafkaListenerServiceImpl {

    private static final ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() { return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"); }
    };

    private final Logger logger = LoggerFactory.getLogger(KafkaListenerServiceImpl.class);

    public ApplicationForFood processingActiveBenefitCategories(AbstractPullData data, String message)
    {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            BenefitResponse benefitResponse = (BenefitResponse) data;
            ActiveBenefitCategoriesGettingResponse activeBenefitCategoriesGettingResponse = benefitResponse.getActive_benefit_categories_getting_response();
            String requestId = activeBenefitCategoriesGettingResponse.getRequest_id();

            //Заполнение полей таблицы AppMezhvedRequest
            AppMezhvedRequest appMezhvedRequest = updateMezved(requestId, activeBenefitCategoriesGettingResponse.getErrors(), message, session);
            ApplicationForFood applicationForFood = appMezhvedRequest.getApplicationForFood();
            //Флаг того, что хотябы один ЛК был подтвержден
            boolean confirm = false;
            ApplicationForFoodDiscount applicationForFoodDiscountActive = null;
            //Проставление статуса подтверждения для ЛК
            for (ApplicationForFoodDiscount applicationForFoodDiscount: applicationForFood.getDtisznCodes())
            {
                for (BenefitCategoryInfo benefitCategoryInfo: activeBenefitCategoriesGettingResponse.getActive_benefit_categories_info())
                {
                    if (Objects.equals(applicationForFoodDiscount.getDtisznCode(), Integer.valueOf(benefitCategoryInfo.getBenefit_category_id())))
                    {
                        applicationForFoodDiscount.setConfirmed(true);
                        applicationForFoodDiscount.setStartDate(format.get().parse(benefitCategoryInfo.getBenefit_activity_date_from()));
                        applicationForFoodDiscount.setEndDate(format.get().parse(benefitCategoryInfo.getBenefit_activity_date_to()));
                        //Сначала ставим всем низкий приоритет
                        applicationForFoodDiscount.setAppointedMSP(false);
                        //Далее алгоритм определения самой приоритетной льготы
                        applicationForFoodDiscountActive = RuntimeContext.getAppContext().getBean(DTSZNDiscountsReviseService.class).getMaxPriorityDiscount(session, applicationForFoodDiscount, applicationForFoodDiscountActive);
                        session.save(applicationForFoodDiscount);
                        confirm = true;
                        break;
                    }
                }
            }
            if (applicationForFoodDiscountActive != null)
            {
                //Проставляем флаг высшего приоритета у льготы
                applicationForFoodDiscountActive.setAppointedMSP(true);
                session.save(applicationForFoodDiscountActive);
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
            if (confirm) {
                applicationForFood.setStatus(new ApplicationForFoodStatus(
                        ApplicationForFoodState.INFORMATION_RESPONSE_BENEFIT_CONFIRMED));
                return applicationForFood;
            }
            return null;
        } catch (Exception e) {
            logger.error("Error in processingActiveBenefitCategories: ", e);
            return null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }



    public ApplicationForFood processingPassportValidation(AbstractPullData data, String message)
    {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            PassportResponse passportData = (PassportResponse) data;
            PassportBySerieNumberValidityCheckingResponse passport = passportData.getPassport_by_serie_number_validity_checking_response();
            AppMezhvedRequest appMezhvedRequest = updateMezved(passport.getRequest_id(), passport.getErrors(), message, session);
            ApplicationForFood applicationForFood = appMezhvedRequest.getApplicationForFood();
            if (applicationForFood.getStatus().getApplicationForFoodState().getCode().startsWith("1080"))
//            if (CalendarUtils.daysBetween(appMezhvedRequest.getCreatedDate(), new Date()).size() >=5)
            {
                //Если прошло более 5 дней с подачи запроса
                applicationForFood.setStatus(new ApplicationForFoodStatus(
                        ApplicationForFoodState.DENIED_GUARDIANSHIP));
                applicationForFood.setDocConfirmed(ApplicationForFoodMezhvedState.NO_INFO);
            }
            else {
                if (passport.getPassportValidityInfo() != null &&
                        Objects.equals(passport.getPassportValidityInfo().getPassport_status_info().getPassport_status(), "Действителен")) {
                    applicationForFood.setStatus(new ApplicationForFoodStatus(
                            ApplicationForFoodState.INFORMATION_RESPONSE_PASSPORT));
                    applicationForFood.setDocConfirmed(ApplicationForFoodMezhvedState.CONFIRMED);
                } else {
                    applicationForFood.setStatus(new ApplicationForFoodStatus(
                            ApplicationForFoodState.DENIED_GUARDIANSHIP));
                    applicationForFood.setDocConfirmed(ApplicationForFoodMezhvedState.NO_INFO);
                }
            }
            session.save(applicationForFood);
            transaction.commit();
            transaction = null;
            if (applicationForFood.getDocConfirmed().equals(ApplicationForFoodMezhvedState.CONFIRMED))
                return applicationForFood;
            return null;
        } catch (Exception e) {
            logger.error("Error in processingPassportValidation: ", e);
            return null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public ApplicationForFood processingGuardianValidation(AbstractPullData data, String message)
    {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            GuardianResponse guardianData = (GuardianResponse) data;
            RelatednessChecking2Response relatednessChecking2Response = guardianData.getRelatedness_checking_2_response();

            AppMezhvedRequest appMezhvedRequest =
                    updateMezved(relatednessChecking2Response.getRequest_id(), relatednessChecking2Response.getErrors(), message, session);
            ApplicationForFood applicationForFood = appMezhvedRequest.getApplicationForFood();
            if (CalendarUtils.daysBetween(appMezhvedRequest.getCreatedDate(), new Date()).size() >=5)
            {
                //Если прошло более 5 дней с подачи запроса
                applicationForFood.setStatus(new ApplicationForFoodStatus(
                        ApplicationForFoodState.DENIED_GUARDIANSHIP));
                applicationForFood.setGuardianshipConfirmed(ApplicationForFoodMezhvedState.NO_INFO);

            }
            else {
                if (relatednessChecking2Response.getRelatednessInfo() != null &&
                        relatednessChecking2Response.getRelatednessInfo().getRelatedness_confirmation().toLowerCase().equals("да")) {
                    applicationForFood.setStatus(new ApplicationForFoodStatus(
                            ApplicationForFoodState.INFORMATION_RESPONSE_GUARDIAN));
                    applicationForFood.setGuardianshipConfirmed(ApplicationForFoodMezhvedState.CONFIRMED);
                } else {
                    applicationForFood.setStatus(new ApplicationForFoodStatus(
                            ApplicationForFoodState.DENIED_GUARDIANSHIP));
                    applicationForFood.setGuardianshipConfirmed(ApplicationForFoodMezhvedState.NO_INFO);
                }
            }
            session.save(applicationForFood);
            transaction.commit();
            transaction = null;
            if (applicationForFood.getGuardianshipConfirmed().equals(ApplicationForFoodMezhvedState.CONFIRMED))
                return applicationForFood;
            return null;
        } catch (Exception e) {
            logger.error("Error in processingGuardianValidation: ", e);
            return null;
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
