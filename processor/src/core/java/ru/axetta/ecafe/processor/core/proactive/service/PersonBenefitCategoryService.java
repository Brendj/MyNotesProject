package ru.axetta.ecafe.processor.core.proactive.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVDaoService;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVProactiveService;
import ru.axetta.ecafe.processor.core.partner.etpmv.enums.StatusETPMessageType;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientDtisznDiscountInfo;
import ru.axetta.ecafe.processor.core.persistence.DiscountChangeHistory;
import ru.axetta.ecafe.processor.core.persistence.proactive.ProactiveMessage;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.proactive.kafka.model.response.BenefitCategoryChange;
import ru.axetta.ecafe.processor.core.proactive.kafka.model.response.PersonBenefitCategoryChanges;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        String fio = null;
        Integer categoryCode = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            client = DAOUtils.findClientByMeshGuid(session, responseData.getPerson_id());
            if (client == null) {
                log.info(String.format("Client with guid=%s not found", responseData.getPerson_id()));
                return;
            }
            for (BenefitCategoryChange benefit : responseData.getBenefit_category_changes()) {
                if (!Objects.equals(benefit.getBenefit_category_code(), DSZN_MOS_CODE))
                    continue;
                categoryCode = Integer.parseInt(benefit.getBenefit_category_code());
                if (!benefit.getIs_actual() || format.get().parse(benefit.getEnd_date()).before(new Date())) {
                    //удаление льготы по https://yt.iteco.dev/issue/ISPP-1149
                    List<ProactiveMessage> proactiveMessages = RuntimeContext.getAppContext().
                            getBean(ETPMVDaoService.class).getProactiveMessages(client, categoryCode);
                    StatusETPMessageType status = StatusETPMessageType.REFUSE_TIMEOUT;
                    if (proactiveMessages.isEmpty())
                        RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).sendStatus(System.currentTimeMillis(), null, status, false);
                    else {
                        for (ProactiveMessage proactiveMessage : proactiveMessages) {
                            RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).sendStatus(System.currentTimeMillis(), proactiveMessage, status, false);
                        }
                    }
                    DiscountManager.removeDtisznDiscount(session, client, Integer.valueOf(DSZN_MOS_CODE), true, null);
                    StatusETPMessageType status2 = StatusETPMessageType.REFUSE_SYSTEM;
                    status2.setFullName(client.getPerson().getFullName());
                    if (proactiveMessages.isEmpty())
                        RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).sendStatus(System.currentTimeMillis(), null, status2, false);
                    else {
                        for (ProactiveMessage proactiveMessage : proactiveMessages) {
                            RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).sendStatus(System.currentTimeMillis(), proactiveMessage, status2, false);
                        }
                    }
                } else {
                    Date startDate = format.get().parse(benefit.getBegin_date());
                    endDate = format.get().parse(benefit.getEnd_date());
                    DiscountManager.addDtisznDiscount(session, client, categoryCode, startDate, endDate, true, DiscountChangeHistory.MODIFY_BY_PROACTIVE);
                    guardians = ClientManager.findGuardiansByClient(session, client.getIdOfClient(), false);
                    sendToPortal = true;
                    fio = client.getPerson().getFullName();
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
            sendNotificationBenefitCreated(client, guardians, categoryCode, fio);
        }
    }

    public void sendNotificationBenefitCreated(Client client, List<Client> guardians, Integer dtisznCode, String clientFIO) {
        try {
            for (Client guardian : guardians) {
                String ssoid = aupdPersonService.getSsoidByPersonId(guardian.getMeshGUID());
                if (StringUtils.isEmpty(ssoid)) {
                    log.info("Aupd ssoid is empty");
                    return;
                }
                RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).sendMSPAssignedMessage(client, guardian, dtisznCode, clientFIO, ssoid);
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
                //Получаем список АКТИВНЫХ представителей
                List<Client> clients = ClientManager.findGuardiansByClient(session, clientDtisznDiscountInfo.getClient().getIdOfClient(), false);
                for (Client guard: clients)
                {
                    //Получаем данные по льготе, созданные ранее
                    ProactiveMessage proactiveMessage = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getProactiveMessages(clientDtisznDiscountInfo.getClient(), guard, clientDtisznDiscountInfo.getDtisznCode().intValue());
                    if (proactiveMessage != null)
                        RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).sendStatus(System.currentTimeMillis(), proactiveMessage, StatusETPMessageType.REFUSE_TIMEOUT, true);
                }
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

    @Async
    public void deleteBenefitCategory() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            ArrayList<ProactiveMessage> proactiveMessages = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getProactiveMessageStatus(StatusETPMessageType.REFUSE_TIMEOUT);
            for (ProactiveMessage proactiveMessage: proactiveMessages)
            {
                 ClientDtisznDiscountInfo info = DAOUtils
                        .getDTISZNDiscountInfoByClientAndCode(session, proactiveMessage.getClient(), proactiveMessage.getDtisznCode().longValue());
                DiscountManager.removeDtisznDiscount(session, info.getClient(), info.getDtisznCode().intValue(), true, info);
                StatusETPMessageType status = StatusETPMessageType.REFUSE_SYSTEM;
                status.setFullName(info.getClient().getPerson().getFullName());
                RuntimeContext.getAppContext().getBean(ETPMVProactiveService.class).sendStatus(System.currentTimeMillis(), proactiveMessage, status, true);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            log.error("Error in deleteBenefitCategory: ", e);
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }
}
