package ru.axetta.ecafe.processor.core.proactive.service;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVProactiveService;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPProaktivClient;
import ru.axetta.ecafe.processor.core.partner.etpmv.enums.StatusETPMessageType;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientDtisznDiscountInfo;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.proactive.kafka.model.response.BenefitCategoryChange;
import ru.axetta.ecafe.processor.core.proactive.kafka.model.response.PersonBenefitCategoryChanges;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class PersonBenefitCategoryService {

    private final Logger log = LoggerFactory.getLogger(PersonBenefitCategoryService.class);
    private static final ThreadLocal<SimpleDateFormat> format = ThreadLocal.withInitial(()
            -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
    private static final String DSZN_MOS_CODE = "48";
    private final AupdPersonService aupdPersonService;

    public PersonBenefitCategoryService(AupdPersonService aupdPersonService) {
        this.aupdPersonService = aupdPersonService;
    }

    @Async
    public void parseResponseMessage(PersonBenefitCategoryChanges responseData) {
        Session session = null;
        Transaction transaction = null;
        boolean sendToPortal = false;
        Client client = null;
        List<Client> guardians = null;
        Date endDate = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            client = DAOUtils.findClientByMeshGuid(session, responseData.getPerson_id());
            if (client == null)
                return;
            for (BenefitCategoryChange benefit : responseData.getBenefit_category_changes()) {
                if (!Objects.equals(benefit.getBenefit_category_code(), DSZN_MOS_CODE))
                    continue;
                if (!benefit.getIs_actual() || format.get().parse(benefit.getEnd_date()).before(new Date())) {
                    //удаление льготы по https://yt.iteco.dev/issue/ISPP-1149
                    String serviceNumber = RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).generateServiceNumber();
                    StatusETPMessageType status = StatusETPMessageType.REFUSE_TIMEOUT;
                    RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).sendStatus(System.currentTimeMillis(), serviceNumber, status);
                    DiscountManager.removeDtisznDiscount(session, client, Integer.valueOf(DSZN_MOS_CODE), true);
                    StatusETPMessageType status2 = StatusETPMessageType.REFUSE_SYSTEM;
                    RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).sendStatus(System.currentTimeMillis(), serviceNumber, status2);
                } else {
                    Integer categoryCode = Integer.parseInt(benefit.getBenefit_category_code());
                    Date startDate = format.get().parse(benefit.getBegin_date());
                    endDate = format.get().parse(benefit.getEnd_date());
                    DiscountManager.addDtisznDiscount(session, client, categoryCode, startDate, endDate, true);
                    guardians = ClientManager.findGuardiansByClient(session, client.getIdOfClient(), false);
                    sendToPortal = true;
                }
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            log.error("Error in parseResponseMessage: ", e);
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
        if (sendToPortal) {
            sendNotificationBenefitCreated(client, guardians, endDate);
        }
    }

    public void sendNotificationBenefitCreated(Client client, List<Client> guardians, Date expiration_date) {
        try {
            for (Client guardian : guardians) {
                String ssoid = aupdPersonService.getSsoidByPersonId(client.getMeshGUID());
                RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).sendMSPAssignedMessage(client, guardian, ssoid, expiration_date);
            }
        } catch (Exception e) {
            log.error("Error in sendNotificationBenefitCreated: ", e);
        }
    }

    @Async
    public void checkEndDateForBenefitCategory() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Date currentDate = new Date();
            Date startDate = CalendarUtils.startOfDay(currentDate);
            Date endDate = CalendarUtils.endOfDay(currentDate);

            List<ClientDtisznDiscountInfo> info = DAOUtils
                    .getExpiredDTISZNDiscountInfoByDayAndCode(session, startDate, endDate, Long.parseLong(DSZN_MOS_CODE));

            for (ClientDtisznDiscountInfo clientDtisznDiscountInfo : info) {
                //todo Добавть архивацию льгот
                //todo Нужно добавить вызов "переход к удалению ЛК, п.6.2 БФТ Проактив МоС;" после готовности метода https://yt.iteco.dev/issue/ISPP-1149
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            log.error("Error in checkEndDateForBenefitCategory: ", e);
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }
}
