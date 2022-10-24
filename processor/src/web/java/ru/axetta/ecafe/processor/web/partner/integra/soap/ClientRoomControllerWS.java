/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.client.ClientPasswordRecover;
import ru.axetta.ecafe.processor.core.client.ClientStatsReporter;
import ru.axetta.ecafe.processor.core.client.ContractIdGenerator;
import ru.axetta.ecafe.processor.core.client.RequestWebParam;
import ru.axetta.ecafe.processor.core.client.items.ClientGuardianItem;
import ru.axetta.ecafe.processor.core.client.items.NotificationSettingItem;
import ru.axetta.ecafe.processor.core.daoservices.DOVersionRepository;
import ru.axetta.ecafe.processor.core.daoservices.questionary.QuestionaryService;
import ru.axetta.ecafe.processor.core.image.ImageUtils;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.logic.FinancialOpsManager;
import ru.axetta.ecafe.processor.core.logic.NotInformedSpecialMenuException;
import ru.axetta.ecafe.processor.core.partner.chronopay.ChronopayConfig;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVService;
import ru.axetta.ecafe.processor.core.partner.integra.IntegraPartnerConfig;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.ClientPaymentOrderProcessor;
import ru.axetta.ecafe.processor.core.partner.rbkmoney.RBKMoneyConfig;
import ru.axetta.ecafe.processor.core.persistence.Menu;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientDao;
import ru.axetta.ecafe.processor.core.persistence.dao.enterevents.EnterEventsRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.StateDiagram;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeedingType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.Circulation;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.OrderPublication;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary.Publication;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.*;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SettingsIds;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;
import ru.axetta.ecafe.processor.core.persistence.questionary.ClientAnswerByQuestionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryStatus;
import ru.axetta.ecafe.processor.core.persistence.questionary.QuestionaryType;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardNotFoundException;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardWrongStateException;
import ru.axetta.ecafe.processor.core.persistence.service.enterevents.EnterEventsService;
import ru.axetta.ecafe.processor.core.persistence.utils.*;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.core.service.*;
import ru.axetta.ecafe.processor.core.service.cardblock.CardBlockService;
import ru.axetta.ecafe.processor.core.service.finoperator.FinManager;
import ru.axetta.ecafe.processor.core.sms.emp.EMPProcessor;
import ru.axetta.ecafe.processor.core.sync.SectionType;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.CryptoUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ParameterStringUtils;
import ru.axetta.ecafe.processor.web.partner.iac.*;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.*;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.allEnterEvents.AllEventItem;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.allEnterEvents.AllEventList;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.allEnterEvents.DataAllEvents;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.org.OrgSummary;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.org.OrgSummaryResult;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.visitors.VisitorsSummary;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.visitors.VisitorsSummaryList;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.visitors.VisitorsSummaryResult;
import ru.axetta.ecafe.processor.web.partner.preorder.*;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.PreorderComplexGroup;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.PreorderComplexItemExt;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.PreorderListWithComplexesGroupResult;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.PreorderSaveListParam;
import ru.axetta.ecafe.processor.web.partner.preorder.soap.*;
import ru.axetta.ecafe.processor.web.partner.utils.HTTPData;
import ru.axetta.ecafe.processor.web.partner.utils.HTTPDataHandler;
import ru.axetta.ecafe.processor.web.ui.PaymentTextUtils;
import ru.axetta.ecafe.processor.web.ui.card.CardLockReason;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.message.Message;
import org.hibernate.*;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static ru.axetta.ecafe.processor.core.utils.CalendarUtils.truncateToDayOfMonth;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 12.12.11
 * Time: 19:22
 * To change this template use File | Settings | File Templates.
 */

@WebService(endpointInterface="ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController")
public class ClientRoomControllerWS extends HttpServlet implements ClientRoomController {

    @Resource
    private WebServiceContext context;

    private static final Logger logger = LoggerFactory.getLogger(ClientRoomControllerWS.class);

    private static final Long RC_CLIENT_AUTHORIZATION_FAILED = -101L;
    private static final Long RC_PARTNER_AUTHORIZATION_FAILED = -100L;
    private static final Long RC_OK = 0L;
    private static final Long RC_INTERNAL_ERROR = 100L;
    private static final Long RC_CLIENT_NOT_FOUND = 110L;
    private static final Long RC_SEVERAL_CLIENTS_WERE_FOUND = 120L;
    private static final Long RC_CLIENT_DOES_NOT_HAVE_THIS_SNILS = 130L;
    private static final Long RC_CLIENT_HAS_THIS_SNILS_ALREADY = 140L;
    private static final Long RC_INVALID_DATA = 150L;
    private static final Long RC_NO_CONTACT_DATA = 160L;
    private static final Long RC_CLIENT_FINANCIAL_OPERATION_ERROR = 170L;
    private static final Long RC_SETTINGS_NOT_FOUND = 180L;
    private static final Long RC_SUBSCRIPTION_FEEDING_DUPLICATE = 190L;
    private static final Long RC_LACK_OF_SUBBALANCE1 = 200L;
    private static final Long RC_ERROR_CREATE_SUBSCRIPTION_FEEDING = 210L;
    private static final Long RC_ERROR_CARD_EXISTS = 220L;
    private static final Long RC_ERROR_CARDREQUEST_EXISTS = 221L;
    private static final Long RC_SUBSCRIPTION_FEEDING_NOT_FOUND = 230L;
    private static final Long RC_PROHIBIT_EXIST = 300L;
    private static final Long RC_PROHIBIT_NOT_FOUND = 310L;
    private static final Long RC_PROHIBIT_REMOVED = 320L;
    private static final Long RC_SUBSCRIPTION_FEEDING_ACTIVATED = 330L;
    private static final Long RC_PUBLICATION_NOT_AVAILABLE = 340L;
    private static final Long RC_ORDER_PUBLICATION_NOT_FOUND = 350L;
    private static final Long RC_ORDER_PUBLICATION_CANT_BE_DELETED = 360L;
    private static final Long RC_ORDER_PUBLICATION_ALREADY_EXISTS = 370L;
    private static final Long RC_CLIENT_IS_NOT_WARD_OF_GUARDIAN = 380L;
    private static final Long RC_ORG_HOLDER_NOT_FOUND = 390L;
    private static final Long RC_CLIENT_GUID_NOT_FOUND = 400L;
    private static final Long RC_CLIENT_DOES_NOT_HAVE_PHOTO = 500L;
    private static final Long RC_IMAGE_SIZE_NOT_FOUND = 510L;
    private static final Long RC_IMAGE_NOT_VALIDATED = 520L;
    private static final Long RC_IMAGE_NOT_SAVED = 530L;
    private static final Long RC_IMAGE_NOT_DELETED = 540L;
    private static final Long RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO = 550L;
    private static final Long RC_CLIENT_PHOTO_UNDER_REGISTRY = 560L;
    private static final Long RC_CLIENT_GUARDIAN_NOT_FOUND = 570L;
    private static final Long RC_INVALID_OPERATION_VARIABLE_FEEDING = 580L;
    private static final Long RC_ERROR_CREATE_VARIABLE_FEEDING = 590L;
    private static final Long RC_ERROR_NOT_ALL_DAYS_FILLED_VARIABLE_FEEDING = 600L;
    private static final Long RC_CARD_NOT_FOUND = 610L;
    private static final Long RC_WRONG_STATE_OF_CARD = 615L;
    //private static final Long RC_START_WEEK_POSITION_NOT_FOUND = 620L;
    private static final Long RC_START_WEEK_POSITION_NOT_CORRECT = 630L;
    private static final Long RC_NOT_INFORMED_SPECIAL_MENU = 640L;
    private static final Long RC_NOT_ALLOWED_PREORDERS = 641L;
    private static final Long RC_PREORDERS_NOT_ENABLED = 642L;
    private static final Long RC_PREORDERS_NOT_STAFF = 643L;
    private static final Long RC_PREORDERS_NOT_UNIQUE_CLIENT = 644L;
    private static final Long RC_ORGANIZATION_NOT_FOUND = 650L;
    private static final Long RC_REQUIRED_FIELDS_ARE_NOT_FILLED = 660L;
    private static final Long RC_NOT_FOUND_MENUDETAIL = 670L;
    private static final Long RC_NOT_ENOUGH_BALANCE = 680L;
    private static final Long RC_REQUEST_NOT_FOUND_OR_CANT_BE_DELETED = 690L;
    private static final Long RC_NOT_EDITED_DAY = 700L;
    private static final Long RC_WRONG_GROUP = 710L;
    private static final Long RC_MOBILE_DIFFERENT_GROUPS = 711L;
    private static final Long RC_REGULAR_ALREADY_DELETED = 712L;
    private static final Long RC_REGULAR_WRONG_START_DATE = 713L;
    private static final Long RC_REGULAR_EXISTS = 714L;
    private static final Long RC_INVALID_CREATOR = 720L;


    private static final String RC_OK_DESC = "OK";
    private static final String RC_CLIENT_NOT_FOUND_DESC = "Клиент не найден";
    private static final String RC_NOT_ALL_ARG = "Не заполнены обязательные поля";
    private static final String RC_CLIENT_NO_LONGER_ACTIVE = "Клиент не активен в ИС ПП";
    private static final String RC_CLIENT_DOU = "Клиент является обучающимся дошкольной группы.";
    private static final String RC_SEVERAL_CLIENTS_WERE_FOUND_DESC = "По условиям найден более одного клиента";
    private static final String RC_CLIENT_AUTHORIZATION_FAILED_DESC = "Ошибка авторизации клиента";
    private static final String RC_INTERNAL_ERROR_DESC = "Внутренняя ошибка";
    private static final String RC_NO_CONTACT_DATA_DESC = "У лицевого счета нет контактных данных";
    private static final String RC_DO_NOT_ACCESS_TO_SUB_BALANCE_DESC = "Нет доступа к субсчетам";
    private static final String RC_SUBSCRIPTION_FEEDING_DUPLICATE_DESC = "У клиента уже есть активная подписка на АП.";
    private static final String RC_LACK_OF_SUBBALANCE1_DESC = "У клиента недостаточно средств на субсчете АП";
    private static final String RC_ERROR_CREATE_SUBSCRIPTION_FEEDING_DESC = "Неверная дата активация циклограммы";
    private static final String RC_ERROR_CARD_EXISTS_DESC = "Карта уже существует";
    private static final String RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC = "Услуга не подключена";
    private static final String RC_PROHIBIT_EXIST_DESC = "Запрет с данными параметрами уже существует";
    private static final String RC_PROHIBIT_REMOVED_DESC = "Запрет с данными параметрами был удален";
    private static final String RC_PROHIBIT_NOT_FOUND_DESC = "Запрет с данными параметрами не найден";
    private static final String RC_PUBLICATION_NOT_AVAILABLE_DESC = "Книга не найдена или нет свободных экземпляров";
    private static final String RC_ORDER_PUBLICATION_NOT_FOUND_DESC = "Заказ не найден";
    private static final String RC_ORDER_PUBLICATION_CANT_BE_DELETED_DESC = "Заказ не может быть удален";
    private static final String RC_ORDER_PUBLICATION_ALREADY_EXISTS_DESC = "Заказ на выбранную книгу уже существует";
    private static final String RC_CLIENT_IS_NOT_WARD_OF_GUARDIAN_DESC = "Клиент не найден или не является опекаемым для данного опекуна";
    private static final String RC_ORG_HOLDER_NOT_FOUND_DESC = "Не найдена организация - держатель экземпляра";
    private static final String RC_CLIENT_GUID_NOT_FOUND_DESC = "GUID клиента не найден";
    private static final String RC_IMAGE_NOT_SAVED_DESC = "Не удалось сохранить фото";
    private static final String RC_IMAGE_NOT_DELETED_DESC = "Не удалось удалить фото";
    private static final String RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO_DESC = "У клиента нет неподтвержденного фото";
    private static final String RC_CLIENT_PHOTO_UNDER_REGISTRY_DESC = "Фото клиента в процессе сверки";
    private static final String RC_CLIENT_GUARDIAN_NOT_FOUND_DESC = "Связка клиент-представитель не найдена";
    private static final String RC_INVALID_OPERATION_VARIABLE_FEEDING_DESC = "Подписку на вариативное питание приостановить нельзя";
    private static final String RC_ERROR_CREATE_VARIABLE_FEEDING_DESC = "В рамках данного вида питания можно выбрать только один вариант комплекса каждого вида рациона (завтрак, обед)";
    private static final String RC_ERROR_NOT_ALL_DAYS_FILLED_VARIABLE_FEEDING_DESC = "В рамках данного вида питания должен быть выбран один вариант комплекса каждого вида рациона (завтрак, обед) на каждый день циклограммы в пределах недели";
    private static final String RC_CARD_NOT_FOUND_DESC = "Карта не найдена";
    private static final String RC_WRONG_STATE_OF_CARD_DESC = "Запись не подлежит изменениям";
    //private static final String RC_START_WEEK_POSITION_NOT_FOUND_DESC = "Для циклограммы вариативного питания не указан номер стартовой недели";
    private static final String RC_START_WEEK_POSITION_NOT_CORRECT_DESC = "Номер стартовой недели некорректен";
    private static final String RC_NOT_INFORMED_SPECIAL_MENU_DESC = "Представитель не проинформирован об условиях предоставления услуги";
    private static final String RC_NOT_ALLOWED_PREORDERS_DESC = "Клиенту  не установлен флаг разрешения самостоятельного заказа";
    private static final String RC_PREORDERS_NOT_ENABLED_DESC = "В ОО клиента не включен функционал «Предзаказ»";
    private static final String RC_PREORDERS_NOT_STAFF_DESC = "Клиент не принадлежит группе сотрудников";
    private static final String RC_PREORDERS_NOT_UNIQUE_CLIENT_DESC = "Клиент из группы учащихся имеет не уникальный номер";
    private static final String RC_ORGANIZATION_NOT_FOUND_DESC = "Организация не найдена";
    private static final String RC_REQUIRED_FIELDS_ARE_NOT_FILLED_DESC = "Не заполнены обязательные параметры";
    private static final String RC_NOT_FOUND_MENUDETAIL_DESC = "На данный момент блюдо в меню не найдено";
    private static final String RC_NOT_ENOUGH_BALANCE_DESC = "Недостаточно средств на балансе лицевого счета";
    private static final String RC_REQUEST_NOT_FOUND_OR_CANT_BE_DELETED_DESC = "Заявление не найдено или имеет статус, в котором удаление запрещено";
    private static final String RC_NOT_EDITED_DAY_DESC = "День недоступен для редактирования предзаказа";
    private static final String RC_INVALID_MOBILE = "Неверный формат мобильного телефона";
    private static final String RC_INVALID_INPUT_DATA = "Неверные входные данные";
    private static final String RC_WRONG_GROUP_DESC = "Неверная группа клиента";
    private static final String RC_MOBILE_DIFFERENT_GROUPS_DESC = "Номер принадлежит клиентам из разных групп";
    private static final String RC_REGULAR_ALREADY_DELETED_DESC = "Для выбранного комплекса или блюда не настроен повтор заказа";
    private static final String RC_REGULAR_WRONG_DATE_DESC = "На выбранную дату начала повтора невозможно создать предзаказ";
    private static final String RC_REGULAR_EXISTS_DESC = "Уже существует повтор заказа с выбранными параметрами";
    private static final String RC_INVALID_CREATOR_DESC = "Данный клиент не может добавить представителя";
    private static final String RC_INVALID_REPREZENTIVE_TYPE = "Не указан тип представительства";
    private static final String RC_INVALID_REPREZENTIVE_CREATOR_TYPE = "Не указан тип предствавителя";
    private static final String RC_INVALID_REGULAR_RANGE_DESC = "Создание повтора питания на один день запрещено";
    private static final int MAX_RECS = 50;
    private static final int MAX_RECS_getPurchaseList = 500;
    private static final int MAX_RECS_getEventsList = 1000;

    private static final String COMMENT_MPGU_CREATE = "{Создано на mos.ru %s пользователем с номером телефона %s)}";
    private static final String COMMENT_BACK_CREATE = "{Создано в ИСПП при регистрации заявления на питание}";
    private static final String groupNotForMos = "сотрудн";

    private static final List<SectionType> typesForSummary = new ArrayList<SectionType>(
            Arrays.asList(SectionType.ACC_INC_REGISTRY, SectionType.ACCOUNT_OPERATIONS_REGISTRY,
                    SectionType.ACCOUNTS_REGISTRY, SectionType.ORGANIZATIONS_STRUCTURE, SectionType.CLIENT_REGISTRY));

    public static final int CIRCULATION_STATUS_FILTER_ALL = -1, CIRCULATION_STATUS_FILTER_ALL_ON_HANDS = -2;

    //private final Long[] orgs_VP_pilot = getVPOrgsList();

    private static final String QUERY_PUBLICATION_LIST = "select result.*, org.shortname from "
            + "(select fq.IdOfPublication, fq.Author, fq.Title, fq.Title2, fq.PublicationDate, fq.Publisher, fq.instancesAmount, count(ins.IdOfInstance) as instancesAvailable, fq.owner "
            + "from (select pub.IdOfPublication, pub.Author, pub.Title, pub.Title2, pub.PublicationDate, pub.Publisher, "
            + "count(ins.IdOfInstance) as instancesAmount, ins.OrgOwner as owner "
            + "from cf_publications pub inner join cf_instances ins on pub.IdOfPublication = ins.IdOfPublication "
            + "where ins.OrgOwner in (select friendlyorg from cf_friendly_organization where currentorg = :org) "
            + "CONDITION "
            + "group by pub.IdOfPublication, ins.OrgOwner order by Author limit :limit offset :offset) fq inner join "
            + "cf_instances ins on fq.IdOfPublication = ins.IdOfPublication "
            + "where ins.OrgOwner = fq.owner and not exists (select IdOfCirculation from cf_circulations cir inner join cf_issuable iss on cir.IdOfIssuable = iss.IdOfIssuable "
            + "where iss.IdOfInstance = ins.IdOfInstance and cir.RealRefundDate is null) "
            + "group by fq.IdOfPublication, fq.Author, fq.Title, fq.Title2, fq.PublicationDate, fq.Publisher, fq.instancesAmount, fq.owner "
            + "order by fq.Author) result inner join cf_orgs org on result.owner=org.IdOfOrg";

    private static final String QUERY_PUBLICATION_LIST_COUNT =
            "select count(*) from (select distinct pub.IdOfPublication, ins.orgOwner "
                    + "from cf_publications pub inner join cf_instances ins on pub.IdOfPublication = ins.IdOfPublication inner join cf_issuable iss on ins.IdOfInstance = iss.IdOfInstance "
                    + "where ins.OrgOwner in (select friendlyorg from cf_friendly_organization where currentorg = :org) and not exists (select IdOfCirculation from cf_circulations cir where cir.IdOfIssuable = iss.IdOfIssuable and cir.RealRefundDate is null) ";

    private static final String QUERY_PUBLICATION_LIST_COUNT_TAIL = ") result";//alias for inner select

    private final Set<Date> eeManualCleared = new HashSet<Date>();

    static class Processor {

        public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                Session persistenceSession, Transaction transaction) throws Exception {
        }

        public void process(Org org, Data data, ObjectFactory objectFactory, Session persistenceSession,
                Transaction transaction) throws Exception {
        }

        public void process(Client client, Data data, ObjectFactory objectFactory, Session persistenceSession)
                throws Exception {
        }
    }

    @Override
    public Result transferBalance(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "fromSub") Integer fromSub, @WebParam(name = "toSub") Integer toSub,
            @WebParam(name = "amount") Long amount) {

        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        Boolean enableSubBalanceOperation = RuntimeContext.getInstance()
                .getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
        if (enableSubBalanceOperation) {

            Client client;
            final DAOReadonlyService instance = DAOReadonlyService.getInstance();
            try {
                client = instance.getClientByContractId(contractId);
            } catch (Exception e) {
                logger.error("INTERNAL ERROR", e);
                r.resultCode = RC_INTERNAL_ERROR;
                r.description = RC_INTERNAL_ERROR_DESC;
                return r;
            }
            if (client == null) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
                return r;
            }

            try {
                FinancialOpsManager financialOpsManager = RuntimeContext.getAppContext()
                        .getBean(FinancialOpsManager.class);
                financialOpsManager.createSubAccountTransfer(client, fromSub, toSub, amount);
            } catch (FinancialOpsManager.AccountTransactionException ate) {
                r.resultCode = RC_CLIENT_FINANCIAL_OPERATION_ERROR;
                r.description = ate.getMessage();
                logger.error("Failed to process client room controller request", ate);
            } catch (Exception e) {
                r.resultCode = RC_INTERNAL_ERROR;
                r.description = RC_INTERNAL_ERROR_DESC;
                logger.error("Failed to process client room controller request", e);
            }
        } else {
            r.resultCode = RC_CLIENT_FINANCIAL_OPERATION_ERROR;
            r.description = RC_DO_NOT_ACCESS_TO_SUB_BALANCE_DESC;
        }

        return r;
    }

    @Override
    public Result transferBalance(@WebParam(name = "contractId") String san,
            @WebParam(name = "fromSub") Integer fromSub, @WebParam(name = "toSub") Integer toSub,
            @WebParam(name = "amount") Long amount) {

        authenticateRequest(null);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        Boolean enableSubBalanceOperation = RuntimeContext.getInstance()
                .getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
        if (enableSubBalanceOperation) {
            List<Client> clients = new ArrayList<Client>();
            final DAOReadonlyService instance = DAOReadonlyService.getInstance();
            try {
                clients = instance.findClientsBySan(san);
            } catch (Exception e) {
                logger.error("INTERNAL ERROR", e);
                r.resultCode = RC_INTERNAL_ERROR;
                r.description = RC_INTERNAL_ERROR_DESC;
                return r;
            }
            if (clients.size() > 1) {
                r.resultCode = RC_SEVERAL_CLIENTS_WERE_FOUND;
                r.description = RC_SEVERAL_CLIENTS_WERE_FOUND_DESC;
                return r;
            }
            if (clients.isEmpty() || clients.get(0) == null) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
                return r;
            }
            Client client = clients.get(0);
            try {
                FinancialOpsManager financialOpsManager = RuntimeContext.getAppContext()
                        .getBean(FinancialOpsManager.class);
                financialOpsManager.createSubAccountTransfer(client, fromSub, toSub, amount);
            } catch (FinancialOpsManager.AccountTransactionException ate) {
                r.resultCode = RC_CLIENT_FINANCIAL_OPERATION_ERROR;
                r.description = ate.getMessage();
                logger.error("Failed to process client room controller request", ate);
            } catch (Exception e) {
                r.resultCode = RC_INTERNAL_ERROR;
                r.description = RC_INTERNAL_ERROR_DESC;
                logger.error("Failed to process client room controller request", e);
            }
        } else {
            r.resultCode = RC_CLIENT_FINANCIAL_OPERATION_ERROR;
            r.description = RC_DO_NOT_ACCESS_TO_SUB_BALANCE_DESC;
        }

        return r;
    }

    @Override
    public ListOfComplaintBookEntriesResult getListOfComplaintBookEntriesByOrg(Long orgId) {
        return getListOfComplaintBookEntriesByCriteria(orgId, null);
    }

    @Override
    public ListOfComplaintBookEntriesResult getListOfComplaintBookEntriesByClient(Long contractId) {
        return getListOfComplaintBookEntriesByCriteria(null, contractId);
    }

    private ListOfComplaintBookEntriesResult getListOfComplaintBookEntriesByCriteria(Long orgId, Long contractId) {
        authenticateRequest(null);

        ListOfComplaintBookEntriesResult result = new ListOfComplaintBookEntriesResult();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Set<Long> setIdOfOrgs = null;
            Client client = null;
            if ((orgId != null) && (contractId == null)) {
                setIdOfOrgs = new HashSet<Long>();
                Org org = (Org) persistenceSession.load(Org.class, orgId);
                if (org == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Организация с указанным идентификатором не найдена";
                    return result;
                }
                setIdOfOrgs.add(orgId);
                List<Long> longList = DAOUtils.getListIdOfOrgList(persistenceSession, org.getIdOfOrg());
                setIdOfOrgs.addAll(longList);
                for (Org friendlyOrg : org.getFriendlyOrg()) {
                    setIdOfOrgs.add(friendlyOrg.getIdOfOrg());
                }
            } else if ((orgId == null) && (contractId != null)) {
                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                clientCriteria.add(Restrictions.eq("contractId", contractId));
                client = (Client) clientCriteria.uniqueResult();
                if (client == null) {
                    result.resultCode = RC_CLIENT_NOT_FOUND;
                    result.description = RC_CLIENT_NOT_FOUND_DESC;
                    return result;
                }
            } else {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = RC_INTERNAL_ERROR_DESC;
                return result;
            }

            ListOfComplaintBookEntries listOfComplaintBookEntries = new ListOfComplaintBookEntries();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria bookCriteria = persistenceSession.createCriteria(GoodComplaintBook.class);
            if (setIdOfOrgs != null) {
                bookCriteria.add(Restrictions.in("orgOwner", setIdOfOrgs));
            } else {
                bookCriteria.add(Restrictions.eq("client", client));
            }
            List bookEntries = bookCriteria.list();
            if (!bookEntries.isEmpty()) {
                for (Object entry : bookEntries) {
                    GoodComplaintBook goodComplaintBook = (GoodComplaintBook) entry;
                    ListOfComplaintBookEntriesExt listOfComplaintBookEntriesExt = objectFactory
                            .createListOfComplaintBookEntriesExt();

                    listOfComplaintBookEntriesExt.setContractId(goodComplaintBook.getClient().getContractId());
                    listOfComplaintBookEntriesExt.setGuid(goodComplaintBook.getGuid());
                    listOfComplaintBookEntriesExt.setDeletedState(goodComplaintBook.getDeletedState());
                    listOfComplaintBookEntriesExt
                            .setCreatedDate(getXMLGregorianCalendarByDate(goodComplaintBook.getCreatedDate()));
                    listOfComplaintBookEntriesExt.setOrgOwner(goodComplaintBook.getOrgOwner());
                    listOfComplaintBookEntriesExt.setGuidOfGood(goodComplaintBook.getGood().getGuid());
                    listOfComplaintBookEntriesExt.setNameOfGood(goodComplaintBook.getGood().getNameOfGood());

                    ListOfComplaintIterations listOfComplaintIterations = new ListOfComplaintIterations();
                    listOfComplaintBookEntriesExt.getIterations().add(listOfComplaintIterations);

                    Criteria iterationCriteria = persistenceSession.createCriteria(GoodComplaintIterations.class);
                    iterationCriteria.add(Restrictions.eq("complaint", goodComplaintBook));
                    List iterations = iterationCriteria.list();
                    if (!iterations.isEmpty()) {
                        for (Object iterObject : iterations) {
                            GoodComplaintIterations iteration = (GoodComplaintIterations) iterObject;
                            ListOfComplaintIterationsExt listOfComplaintIterationsExt = objectFactory
                                    .createListOfComplaintIterationsExt();

                            listOfComplaintIterationsExt.setIterationNumber(iteration.getIterationNumber());
                            listOfComplaintIterationsExt
                                    .setIterationStatus(iteration.getGoodComplaintIterationStatus().getStatusNumber());
                            listOfComplaintIterationsExt.setProblemDescription(iteration.getProblemDescription());
                            listOfComplaintIterationsExt.setConclusion(iteration.getConclusion());
                            listOfComplaintIterationsExt.setGuid(iteration.getGuid());
                            listOfComplaintIterationsExt.setDeletedState(iteration.getDeletedState());
                            listOfComplaintIterationsExt
                                    .setCreatedDate(getXMLGregorianCalendarByDate(iteration.getCreatedDate()));
                            listOfComplaintIterationsExt.setOrgOwner(iteration.getOrgOwner());

                            ListOfComplaintOrders listOfComplaintOrders = new ListOfComplaintOrders();
                            listOfComplaintIterationsExt.getOrders().add(listOfComplaintOrders);

                            Criteria orderCriteria = persistenceSession.createCriteria(GoodComplaintOrders.class);
                            orderCriteria.add(Restrictions.eq("complaintIteration", iteration));
                            List orders = orderCriteria.list();
                            for (Object orderObject : orders) {
                                GoodComplaintOrders order = (GoodComplaintOrders) orderObject;
                                ListOfComplaintOrdersExt listOfComplaintOrdersExt = objectFactory
                                        .createListOfComplaintOrdersExt();

                                OrderDetail orderDetail = order.getOrderDetail();
                                listOfComplaintOrdersExt.setIdOfOrderDetail(
                                        orderDetail.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
                                listOfComplaintOrdersExt.setMenuDetailName(orderDetail.getMenuDetailName());
                                listOfComplaintOrdersExt.setDateOfOrder(getXMLGregorianCalendarByDate(
                                        order.getOrderDetail().getOrder().getCreateTime()));
                                listOfComplaintOrdersExt.setGuid(order.getGuid());
                                listOfComplaintOrdersExt
                                        .setCreatedDate(getXMLGregorianCalendarByDate(order.getCreatedDate()));
                                listOfComplaintOrdersExt.setDeletedState(order.getDeletedState());
                                listOfComplaintOrdersExt.setOrgOwner(order.getOrgOwner());

                                listOfComplaintOrders.getO().add(listOfComplaintOrdersExt);
                            }

                            ListOfComplaintCauses listOfComplaintCauses = new ListOfComplaintCauses();
                            listOfComplaintIterationsExt.getCauses().add(listOfComplaintCauses);

                            Criteria causeCriteria = persistenceSession.createCriteria(GoodComplaintCauses.class);
                            causeCriteria.add(Restrictions.eq("complaintIteration", iteration));
                            List causes = causeCriteria.list();
                            for (Object causeObject : causes) {
                                GoodComplaintCauses cause = (GoodComplaintCauses) causeObject;
                                ListOfComplaintCausesExt listOfComplaintCausesExt = objectFactory
                                        .createListOfComplaintCausesExt();

                                listOfComplaintCausesExt.setCause(cause.getCause().getCauseNumber());
                                listOfComplaintCausesExt.setCauseDescription(cause.getTitle());
                                listOfComplaintCausesExt.setGuid(cause.getGuid());
                                listOfComplaintCausesExt
                                        .setCreatedDate(getXMLGregorianCalendarByDate(cause.getCreatedDate()));
                                listOfComplaintCausesExt.setDeletedState(cause.getDeletedState());
                                listOfComplaintCausesExt.setOrgOwner(cause.getOrgOwner());

                                listOfComplaintCauses.getC().add(listOfComplaintCausesExt);
                            }

                            listOfComplaintIterations.getI().add(listOfComplaintIterationsExt);
                        }
                    }

                    listOfComplaintBookEntries.getE().add(listOfComplaintBookEntriesExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            result.listOfComplaintBookEntries = listOfComplaintBookEntries;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public IdResult openComplaint(Long contractId, Long orderOrgId, List<Long> orderDetailIdList,
            List<Integer> causeNumberList, String description) {
        authenticateRequest(null);

        IdResult result = new IdResult();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            Criteria orderOrgCriteria = persistenceSession.createCriteria(Org.class);
            orderOrgCriteria.add(Restrictions.eq("idOfOrg", orderOrgId));
            Org orderOrg = (Org) orderOrgCriteria.uniqueResult();
            if (orderOrg == null) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = "Организации с указанным идентификатором не существует";
                return result;
            }

            // Проверяем, ссылаются ли все элементы списка деталей заказов на один и тот же товар,
            // и запоминаем этот товар
            if ((orderDetailIdList == null) || orderDetailIdList.isEmpty()) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = "Список деталей заказов не может быть пустым";
                return result;
            }
            Good problematicGood = null;
            List<GoodComplaintOrders> goodComplaintOrdersList = new ArrayList<GoodComplaintOrders>(
                    orderDetailIdList.size());
            for (Long idOfOrderDetail : orderDetailIdList) {
                CompositeIdOfOrderDetail compositeIdOfOrderDetail = new CompositeIdOfOrderDetail(orderOrg.getIdOfOrg(),
                        idOfOrderDetail);
                OrderDetail orderDetail = DAOUtils.findOrderDetail(persistenceSession, compositeIdOfOrderDetail);
                if (orderDetail == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description =
                            "Не найден элемент деталей заказов с указанным идентификатором и номером " + "организации";
                    return result;
                }

                // Проверка, существует ли Заказ, совершенный указанным клиентом и содержащий данную деталь заказа
                CompositeIdOfOrder compositeIdOfOrder = new CompositeIdOfOrder(client.getOrg().getIdOfOrg(),
                        orderDetail.getIdOfOrder());
                Criteria orderCriteria = persistenceSession.createCriteria(Order.class);
                orderCriteria.add(Restrictions.eq("compositeIdOfOrder", compositeIdOfOrder));
                Order order = (Order) orderCriteria.uniqueResult();
                if ((order == null) || !order.getClient().equals(client)) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Указанный клиент не совершал данного заказа";
                    return result;
                }

                Good goodFromOrder = orderDetail.getGood();
                if (goodFromOrder == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description =
                            "У переданного элемента списка деталей заказов с идентификатором " + +orderDetail
                                    .getIdOfOrder() + " не указана ссылка на товар";
                    return result;
                }

                if (problematicGood == null) {
                    problematicGood = goodFromOrder;
                } else if (!goodFromOrder.equals(problematicGood)) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description =
                            "Требуется передавать список деталей заказа, " + "ссылающиеся на один и тот же товар";
                    return result;
                }

                GoodComplaintOrders goodComplaintOrders = new GoodComplaintOrders();
                goodComplaintOrders.setOrderOrg(orderOrg);
                goodComplaintOrders.setOrderDetail(orderDetail);
                fillDisctributedObjectsCommonDetails(goodComplaintOrders, client.getOrg());

                goodComplaintOrdersList.add(goodComplaintOrders);
            }

            // Проверяем, подавалась ли ранее указанным клиентом жалоба на данный товар
            Criteria goodComplaintBookCriteria = persistenceSession.createCriteria(GoodComplaintBook.class);
            goodComplaintBookCriteria.add(Restrictions.eq("client", client));
            goodComplaintBookCriteria.add(Restrictions.eq("good", problematicGood));
            GoodComplaintBook goodComplaintBook = (GoodComplaintBook) goodComplaintBookCriteria.uniqueResult();

            int newIterationNumber;
            if (goodComplaintBook == null) {
                goodComplaintBook = new GoodComplaintBook();
                goodComplaintBook.setClient(client);
                goodComplaintBook.setGood(problematicGood);
                fillDisctributedObjectsCommonDetails(goodComplaintBook, client.getOrg());

                persistenceSession.save(goodComplaintBook);

                newIterationNumber = 0;
            } else {
                // Нахождение последней итерации по жалобе указанного клиента на данный товар
                // и проверка, было ли вынесено заключение по текущей итерации, т.к. до вынесения заключенися
                // переоткрывать жалобу запрещено
                Criteria iterationCriteria = persistenceSession.createCriteria(GoodComplaintIterations.class);
                iterationCriteria.add(Restrictions.eq("complaint", goodComplaintBook));
                List iterationObjects = iterationCriteria.list();
                GoodComplaintIterations lastIteration = null;
                for (Object iterationObject : iterationObjects) {
                    GoodComplaintIterations currentIteration = (GoodComplaintIterations) iterationObject;
                    if ((null == lastIteration) || (currentIteration.getIterationNumber() > lastIteration
                            .getIterationNumber())) {
                        lastIteration = currentIteration;
                    }
                }
                if (lastIteration == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = RC_INTERNAL_ERROR_DESC;
                    return result;
                }
                if (!GoodComplaintIterationStatus.conclusion.equals(lastIteration.getGoodComplaintIterationStatus())) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "По жалобе с указанным идентификатором еще не было вынесено заключения";
                    return result;
                }
                newIterationNumber = lastIteration.getIterationNumber() + 1;
            }

            // Выяснили, какой номер итерации назначать новой итерации
            GoodComplaintIterations newIteration = new GoodComplaintIterations();
            newIteration.setComplaint(goodComplaintBook);
            newIteration.setIterationNumber(newIterationNumber);
            newIteration.setProblemDescription(description);
            newIteration.setGoodComplaintIterationStatus(GoodComplaintIterationStatus.creation);
            fillDisctributedObjectsCommonDetails(newIteration, client.getOrg());
            persistenceSession.save(newIteration);

            // Получили сохраненную итерацию, следовательно, можно привязать к этой итерации
            // элементы деталей заказов из списка, полученного ранее, и сохранить их
            for (GoodComplaintOrders order : goodComplaintOrdersList) {
                order.setComplaintIteration(newIteration);
                persistenceSession.save(order);
            }

            // Осталось записать причины подачи жалобы
            if (causeNumberList == null) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = "Не указаны причины подачи жалобы";
                return result;
            }
            List<Integer> parsedCauseNumberList = new ArrayList<Integer>();
            for (Integer causeNumber : causeNumberList) {
                if (parsedCauseNumberList.contains(causeNumber)) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Список причин подачи жалобы должен содержать уникальные значения";
                    return result;
                } else {
                    parsedCauseNumberList.add(causeNumber);
                }
                GoodComplaintPossibleCauses cause = GoodComplaintPossibleCauses.getCauseByNumberNullSafe(causeNumber);
                if (cause == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description =
                            "Номер причины подачи жалобы " + causeNumber + " не определен в списке возможных причин";
                    return result;
                }
                GoodComplaintCauses goodComplaintCauses = new GoodComplaintCauses();
                goodComplaintCauses.setCause(cause);
                goodComplaintCauses.setComplaintIteration(newIteration);
                fillDisctributedObjectsCommonDetails(goodComplaintCauses, client.getOrg());
                persistenceSession.save(goodComplaintCauses);
            }

            result.id = goodComplaintBook.getGlobalId();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return result;
    }

    private void fillDisctributedObjectsCommonDetails(DistributedObject distributedObject, Org org) {
        distributedObject.setOrgOwner(org.getIdOfOrg());
        distributedObject.setCreatedDate(new Date());
        distributedObject.setDeletedState(false);
        distributedObject.setSendAll(SendToAssociatedOrgs.SendToMain);
        distributedObject.setGlobalVersion(DAOService.getInstance()
                .updateVersionByDistributedObjects(distributedObject.getClass().getSimpleName()));
    }

    @Override
    public Result changeComplaintStatusToConsideration(Long complaintId) {
        return changeComplaintStatus(complaintId, GoodComplaintIterationStatus.consideration, null);
    }

    @Override
    public Result changeComplaintStatusToInvestigation(Long complaintId) {
        return changeComplaintStatus(complaintId, GoodComplaintIterationStatus.investigation, null);
    }

    @Override
    public Result giveConclusionOnComplaint(Long complaintId, String conclusion) {
        return changeComplaintStatus(complaintId, GoodComplaintIterationStatus.conclusion, conclusion);
    }

    private Result changeComplaintStatus(Long complaintId, GoodComplaintIterationStatus status, String conclusion) {
        authenticateRequest(null);

        Result result = new Result();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria complaintCriteria = persistenceSession.createCriteria(GoodComplaintBook.class);
            complaintCriteria.add(Restrictions.eq("globalId", complaintId));
            GoodComplaintBook goodComplaintBook = (GoodComplaintBook) complaintCriteria.uniqueResult();
            if (goodComplaintBook == null) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = "Жалобы с указанным идентификатором не существует";
                return result;
            }

            Criteria iterationCriteria = persistenceSession.createCriteria(GoodComplaintIterations.class);
            iterationCriteria.add(Restrictions.eq("complaint", goodComplaintBook));
            List iterationObjects = iterationCriteria.list();
            GoodComplaintIterations iteration = null;
            for (Object iterationObject : iterationObjects) {
                GoodComplaintIterations goodComplaintIterations = (GoodComplaintIterations) iterationObject;
                if ((null == iteration) || (goodComplaintIterations.getIterationNumber() > iteration
                        .getIterationNumber())) {
                    iteration = goodComplaintIterations;
                }
            }
            if (iteration == null) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = RC_INTERNAL_ERROR_DESC;
                return result;
            }

            if (status.getStatusNumber() - iteration.getGoodComplaintIterationStatus().getStatusNumber() != 1) {
                result.resultCode = RC_INTERNAL_ERROR;
                result.description =
                        "Нельзя назначать итерации со статусом " + iteration.getGoodComplaintIterationStatus()
                                .getTitle() + " статус " + status.getTitle();
                return result;
            }
            iteration.setGoodComplaintIterationStatus(status);
            if (conclusion != null) {
                iteration.setConclusion(conclusion);
            }
            persistenceSession.save(iteration);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return result;
    }

    @Override
    public ListOfProductsResult getListOfProducts(Long orgId) {
        authenticateRequest(null);

        ListOfProductsResult result = new ListOfProductsResult();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Set<Long> setIdOfOrgs = new HashSet<Long>();
            if (orgId != null) {
                Org org = (Org) persistenceSession.load(Org.class, orgId);
                if (org == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Организация не найдена";
                    return result;
                }
                setIdOfOrgs.add(orgId);
                List<Long> longList = DAOUtils.getListIdOfOrgList(persistenceSession, org.getIdOfOrg());
                setIdOfOrgs.addAll(longList);
            }

            ListOfProductGroups listOfProductGroups = new ListOfProductGroups();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria productGroupCriteria = persistenceSession.createCriteria(ProductGroup.class);
            productGroupCriteria.add(Restrictions.in("orgOwner", setIdOfOrgs));
            List groupObjects = productGroupCriteria.list();
            if (!groupObjects.isEmpty()) {
                for (Object groupObject : groupObjects) {
                    ProductGroup productGroup = (ProductGroup) groupObject;
                    ListOfProductGroupsExt listOfProductGroupsExt = objectFactory.createListOfProductGroupsExt();
                    listOfProductGroupsExt.setNameOfGroup(productGroup.getNameOfGroup());
                    listOfProductGroupsExt.setClassificationCode(productGroup.getClassificationCode());
                    listOfProductGroupsExt.setDeletedState(productGroup.getDeletedState());
                    listOfProductGroupsExt.setGuid(productGroup.getGuid());
                    listOfProductGroupsExt.setOrgOwner(productGroup.getOrgOwner());
                    listOfProductGroupsExt.setCreatedDate(getXMLGregorianCalendarByDate(productGroup.getCreatedDate()));
                    ListOfProducts listOfProducts = new ListOfProducts();
                    listOfProductGroupsExt.getProducts().add(listOfProducts);
                    Criteria productCriteria = persistenceSession.createCriteria(Product.class);
                    productCriteria.add(Restrictions.eq("productGroup", productGroup));
                    productCriteria.add(Restrictions.in("orgOwner", setIdOfOrgs));
                    List objects = productCriteria.list();
                    if (!objects.isEmpty()) {
                        for (Object object : objects) {
                            Product product = (Product) object;
                            ListOfProductsExt listOfProductsExt = objectFactory.createListOfProductsExt();
                            listOfProductsExt.setCode(product.getCode());
                            listOfProductsExt.setOkpCode(product.getOkpCode());
                            listOfProductsExt.setClassificationCode(product.getClassificationCode());
                            listOfProductsExt.setDeletedState(product.getDeletedState());
                            listOfProductsExt.setGuid(product.getGuid());
                            listOfProductsExt.setProductName(product.getProductName());
                            listOfProductsExt.setFullName(product.getFullName());
                            listOfProductsExt.setDensity(product.getDensity());
                            listOfProductsExt.setOrgOwner(product.getOrgOwner());
                            listOfProductsExt.setCreatedDate(getXMLGregorianCalendarByDate(product.getCreatedDate()));

                            listOfProducts.getP().add(listOfProductsExt);
                        }
                    }

                    listOfProductGroups.getPG().add(listOfProductGroupsExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            result.listOfProductGroups = listOfProductGroups;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public ListOfGoodsResult getListOfGoods(Long orgId) {
        authenticateRequest(null);

        ListOfGoodsResult result = new ListOfGoodsResult();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Org org = null;
            if (orgId != null) {
                Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
                orgCriteria.add(Restrictions.eq("idOfOrg", orgId));
                org = (Org) orgCriteria.uniqueResult();
                if (org == null) {
                    result.resultCode = RC_INTERNAL_ERROR;
                    result.description = "Организация не найдена";
                    return result;
                }
            }
            // коментарии
            ListOfGoodGroups listOfGoodGroups = new ListOfGoodGroups();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria goodGroupCriteria = persistenceSession.createCriteria(GoodGroup.class);
            List groupObjects = goodGroupCriteria.list();
            if (!groupObjects.isEmpty()) {
                for (Object groupObject : groupObjects) {
                    GoodGroup goodGroup = (GoodGroup) groupObject;
                    ListOfGoodGroupsExt listOfGoodGroupsExt = objectFactory.createListOfGoodGroupsExt();
                    listOfGoodGroupsExt.setNameOfGoodsGroup(goodGroup.getNameOfGoodsGroup());
                    listOfGoodGroupsExt.setDeletedState(goodGroup.getDeletedState());
                    listOfGoodGroupsExt.setGuid(goodGroup.getGuid());
                    listOfGoodGroupsExt.setOrgOwner(goodGroup.getOrgOwner());
                    listOfGoodGroupsExt.setCreatedDate(getXMLGregorianCalendarByDate(goodGroup.getCreatedDate()));

                    ListOfGoods listOfGoods = new ListOfGoods();
                    listOfGoodGroupsExt.getGoods().add(listOfGoods);

                    Criteria goodCriteria = persistenceSession.createCriteria(Good.class);
                    goodCriteria.add(Restrictions.eq("goodGroup", goodGroup));
                    if (org != null) {
                        List<Long> menuExchangeRuleList = DAOUtils
                                .getListIdOfOrgList(persistenceSession, org.getIdOfOrg());
                        StringBuffer sqlRestriction = new StringBuffer();
                        for (Long idOfProvider : menuExchangeRuleList) {
                            sqlRestriction.append("orgOwner=");
                            sqlRestriction.append(idOfProvider);
                            sqlRestriction.append(" or ");
                        }
                        sqlRestriction.append("orgOwner=");
                        sqlRestriction.append(org.getIdOfOrg());
                        goodCriteria.add(Restrictions.sqlRestriction(sqlRestriction.toString()));
                    }
                    List objects = goodCriteria.list();
                    if (!objects.isEmpty()) {
                        for (Object object : objects) {
                            Good good = (Good) object;
                            ListOfGoodsExt listOfGoodsExt = objectFactory.createListOfGoodsExt();
                            listOfGoodsExt.setGoodsCode(good.getGoodsCode());
                            listOfGoodsExt.setGuid(good.getGuid());
                            listOfGoodsExt.setDeletedState(good.getDeletedState());
                            listOfGoodsExt.setOrgOwner(good.getOrgOwner());
                            listOfGoodsExt.setNameOfGood(good.getNameOfGood());
                            listOfGoodsExt.setFullName(good.getFullName());
                            listOfGoodsExt.setUnitsScale(good.getUnitsScale().ordinal());
                            listOfGoodsExt.setNetWeight(good.getNetWeight());
                            listOfGoodsExt.setLifetime(good.getLifeTime());
                            listOfGoodsExt.setMargin(good.getMargin());
                            listOfGoodsExt.setCreatedDate(getXMLGregorianCalendarByDate(good.getCreatedDate()));

                            listOfGoods.getG().add(listOfGoodsExt);
                        }
                    }

                    listOfGoodGroups.getGG().add(listOfGoodGroupsExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            result.listOfGoodGroups = listOfGoodGroups;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;


    }

    @Override
    public ProhibitionsListResult getDishProhibitionsList(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(null);

        ProhibitionsListResult prohibitionsListResult = new ProhibitionsListResult();
        prohibitionsListResult.resultCode = RC_OK;
        prohibitionsListResult.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            if (client == null) {
                prohibitionsListResult.resultCode = RC_CLIENT_NOT_FOUND;
                prohibitionsListResult.description = RC_CLIENT_NOT_FOUND_DESC;
                return prohibitionsListResult;
            }

            ProhibitionsList prohibitionsList = new ProhibitionsList();

            ObjectFactory objectFactory = new ObjectFactory();

            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            prohibitionCriteria.add(Restrictions.eq("client", client));
            List objects = prohibitionCriteria.list();
            if (!objects.isEmpty()) {
                for (Object object : objects) {
                    Prohibition prohibition = (Prohibition) object;
                    ProhibitionsListExt prohibitionsListExt = objectFactory.createProhibitionsListExt();
                    prohibitionsListExt.setGuid(prohibition.getGuid());
                    prohibitionsListExt.setDeletedState(prohibition.getDeletedState());
                    prohibitionsListExt.setCreatedDate(getXMLGregorianCalendarByDate(prohibition.getCreatedDate()));
                    prohibitionsListExt.setContactId(client.getContractId());
                    Product bannedProduct = prohibition.getProduct();
                    ProductGroup bannedProductGroup = prohibition.getProductGroup();
                    Good bannedGood = prohibition.getGood();
                    GoodGroup bannedGoodGroup = prohibition.getGoodGroup();
                    if (bannedProduct != null) {
                        prohibitionsListExt.setGuidOfProducts(bannedProduct.getGuid());
                    } else if (bannedProductGroup != null) {
                        prohibitionsListExt.setGuidOfProductGroups(bannedProductGroup.getGuid());
                    } else if (bannedGood != null) {
                        prohibitionsListExt.setGuidOfGood(bannedGood.getGuid());
                    } else if (bannedGoodGroup != null) {
                        prohibitionsListExt.setGuidOfGoodsGroup(bannedGoodGroup.getGuid());
                    }

                    ProhibitionExclusionsList exclusionsList = new ProhibitionExclusionsList();
                    prohibitionsListExt.getExclusions().add(exclusionsList);

                    Criteria exclusionCriteria = persistenceSession.createCriteria(ProhibitionExclusion.class);
                    exclusionCriteria.add(Restrictions.eq("prohibition", prohibition));
                    List exclusionObjects = exclusionCriteria.list();
                    if (!exclusionObjects.isEmpty()) {
                        for (Object exclusionObject : exclusionObjects) {
                            ProhibitionExclusion exclusion = (ProhibitionExclusion) exclusionObject;
                            ProhibitionExclusionsListExt prohibitionExclusionsListExt = objectFactory
                                    .createProhibitionExclusionsListExt();
                            prohibitionExclusionsListExt.setGuid(exclusion.getGuid());
                            prohibitionExclusionsListExt.setDeletedState(exclusion.getDeletedState());
                            prohibitionExclusionsListExt
                                    .setCreatedDate(getXMLGregorianCalendarByDate(exclusion.getCreatedDate()));
                            Good excludedGood = exclusion.getGood();
                            GoodGroup excludedGoodGroup = exclusion.getGoodsGroup();
                            if (excludedGood != null) {
                                prohibitionExclusionsListExt.setGuidOfGood(excludedGood.getGuid());
                            } else if (excludedGoodGroup != null) {
                                prohibitionExclusionsListExt.setGuidOfGoodsGroup(excludedGoodGroup.getGuid());
                            }

                            exclusionsList.getE().add(prohibitionExclusionsListExt);
                        }
                    }

                    prohibitionsList.getC().add(prohibitionsListExt);
                }
            }

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;

            prohibitionsListResult.prohibitionsList = prohibitionsList;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
            prohibitionsListResult.resultCode = RC_INTERNAL_ERROR;
            prohibitionsListResult.description = RC_INTERNAL_ERROR_DESC;
        }
        return prohibitionsListResult;
    }

    private XMLGregorianCalendar getXMLGregorianCalendarByDate(Date date) throws DatatypeConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        return xmlCalendar;
    }

    @Override
    public IdResult setProhibitionOnProduct(Long orgId, Long contractId, Long idOfProduct, Boolean isDeleted) {
        if (isDeleted != null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, Product.class, idOfProduct);
        } else {
            return setProhibitionOnObject(orgId, contractId, Product.class, idOfProduct);
        }
    }

    @Override
    public IdResult setProhibitionOnProductGroup(Long orgId, Long contractId, Long idOfProductGroup,
            Boolean isDeleted) {
        if (isDeleted != null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, ProductGroup.class, idOfProductGroup);
        } else {
            return setProhibitionOnObject(orgId, contractId, ProductGroup.class, idOfProductGroup);
        }
    }

    @Override
    public IdResult setProhibitionOnGood(Long orgId, Long contractId, Long idOfGood, Boolean isDeleted) {
        if (isDeleted != null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, Good.class, idOfGood);
        } else {
            return setProhibitionOnObject(orgId, contractId, Good.class, idOfGood);
        }
    }

    @Override
    public IdResult setProhibitionOnGoodGroup(Long orgId, Long contractId, Long idOfGoodGroup, Boolean isDeleted) {
        if (isDeleted != null && isDeleted) {
            return deteteProhibitionOnObject(orgId, contractId, GoodGroup.class, idOfGoodGroup);
        } else {
            return setProhibitionOnObject(orgId, contractId, GoodGroup.class, idOfGoodGroup);
        }
    }

    private IdResult deteteProhibitionOnObject(Long orgId, Long contractId, Class objectClass, Long idOfObject) {
        authenticateRequest(null);
        IdResult idResult = new IdResult();
        idResult.resultCode = RC_OK;
        idResult.description = RC_OK_DESC;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria orgCriteria = persistenceSession.createCriteria(Org.class);
            orgCriteria.add(Restrictions.eq("idOfOrg", orgId));
            Org org = (Org) orgCriteria.uniqueResult();
            if (org == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Организация не найдена";
                return idResult;
            }

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            if (client == null) {
                idResult.resultCode = RC_CLIENT_NOT_FOUND;
                idResult.description = RC_CLIENT_NOT_FOUND_DESC;
                return idResult;
            }

            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            if (objectClass.equals(Product.class)) {
                prohibitionCriteria.add(Restrictions.eq("product.globalId", idOfObject));
            } else if (objectClass.equals(ProductGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("productGroup.globalId", idOfObject));
            } else if (objectClass.equals(Good.class)) {
                prohibitionCriteria.add(Restrictions.eq("good.globalId", idOfObject));
            } else if (objectClass.equals(GoodGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("goodGroup.globalId", idOfObject));
            } else {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = RC_INTERNAL_ERROR_DESC;
                return idResult;
            }
            Prohibition prohibition = (Prohibition) prohibitionCriteria.uniqueResult();
            prohibition.setDeletedState(true);
            prohibition.setGlobalVersion(
                    DAOService.getInstance().updateVersionByDistributedObjects(Prohibition.class.getSimpleName()));
            persistenceSession.save(prohibition);
            idResult.id = prohibition.getGlobalId();
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            idResult.resultCode = RC_INTERNAL_ERROR;
            idResult.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idResult;
    }

    private IdResult setProhibitionOnObject(Long orgId, Long contractId, Class objectClass, Long idOfObject) {
        authenticateRequest(null);

        IdResult idResult = new IdResult();
        idResult.resultCode = RC_OK;
        idResult.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria orgCritera = persistenceSession.createCriteria(Org.class);
            orgCritera.add(Restrictions.eq("idOfOrg", orgId));
            Org org = (Org) orgCritera.uniqueResult();
            if (org == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Организация не найдена";
                return idResult;
            }

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            if (client == null) {
                idResult.resultCode = RC_CLIENT_NOT_FOUND;
                idResult.description = RC_CLIENT_NOT_FOUND_DESC;
                return idResult;
            }

            Prohibition prohibition = new Prohibition();
            prohibition.setClient(client);
            prohibition.setOrgOwner(org.getIdOfOrg());
            prohibition.setCreatedDate(new Date());
            prohibition.setDeletedState(false);
            prohibition.setSendAll(SendToAssociatedOrgs.SendToSelf);
            Criteria objectCriteria = persistenceSession.createCriteria(objectClass);
            objectCriteria.add(Restrictions.eq("globalId", idOfObject));
            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            if (objectClass.equals(Product.class)) {
                prohibitionCriteria.add(Restrictions.eq("product.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if (p == null) {
                    Product product = (Product) objectCriteria.uniqueResult();
                    if (product == null) {
                        idResult.resultCode = RC_INTERNAL_ERROR;
                        idResult.description = "Продукта с указанным id не существует";
                        return idResult;
                    }
                    prohibition.setProduct(product);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }
            } else if (objectClass.equals(ProductGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("productGroup.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if (p == null) {
                    ProductGroup productGroup = (ProductGroup) objectCriteria.uniqueResult();
                    if (productGroup == null) {
                        idResult.resultCode = RC_INTERNAL_ERROR;
                        idResult.description = "Группы продуктов с указанным id не существует";
                        return idResult;
                    }
                    prohibition.setProductGroup(productGroup);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }
            } else if (objectClass.equals(Good.class)) {
                prohibitionCriteria.add(Restrictions.eq("good.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if (p == null) {
                    Good good = (Good) objectCriteria.uniqueResult();
                    if (good == null) {
                        idResult.resultCode = RC_INTERNAL_ERROR;
                        idResult.description = "Товара с указанным id не существует";
                        return idResult;
                    }
                    prohibition.setGood(good);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }

            } else if (objectClass.equals(GoodGroup.class)) {
                prohibitionCriteria.add(Restrictions.eq("goodGroup.globalId", idOfObject));
                Prohibition p = (Prohibition) prohibitionCriteria.uniqueResult();
                if (p == null) {
                    GoodGroup goodGroup = (GoodGroup) objectCriteria.uniqueResult();
                    if (goodGroup == null) {
                        idResult.resultCode = RC_INTERNAL_ERROR;
                        idResult.description = "Группы товаров с указанным id не существует";
                        return idResult;
                    }
                    prohibition.setGoodGroup(goodGroup);
                } else {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Запрет с указанным id уже существует";
                    return idResult;
                }
            } else {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = RC_INTERNAL_ERROR_DESC;
                return idResult;
            }
            prohibition.setGlobalVersion(
                    DAOService.getInstance().updateVersionByDistributedObjects(Prohibition.class.getSimpleName()));
            persistenceSession.save(prohibition);

            idResult.id = prohibition.getGlobalId();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            idResult.resultCode = RC_INTERNAL_ERROR;
            idResult.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idResult;
    }

    @Override
    public IdResult excludeGoodFromProhibition(Long orgId, Long idOfProhibition, Long idOfGood) {
        return excludeObjectFromProhibition(orgId, idOfProhibition, Good.class, idOfGood);
    }

    @Override
    public IdResult excludeGoodGroupFromProhibition(Long orgId, Long idOfProhibition, Long idOfGoodGroup) {
        return excludeObjectFromProhibition(orgId, idOfProhibition, GoodGroup.class, idOfGoodGroup);
    }

    private IdResult excludeObjectFromProhibition(Long orgId, Long idOfProhibition, Class objectClass,
            Long idOfObject) {
        authenticateRequest(null);

        IdResult idResult = new IdResult();
        idResult.resultCode = RC_OK;
        idResult.description = RC_OK_DESC;

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria orgCritera = persistenceSession.createCriteria(Org.class);
            orgCritera.add(Restrictions.eq("idOfOrg", orgId));
            Org org = (Org) orgCritera.uniqueResult();
            if (org == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Организация не найдена";
                return idResult;
            }

            Criteria prohibitionCriteria = persistenceSession.createCriteria(Prohibition.class);
            prohibitionCriteria.add(Restrictions.eq("globalId", idOfProhibition));
            Prohibition prohibition = (Prohibition) prohibitionCriteria.uniqueResult();
            if (prohibition == null) {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = "Запрет с указанным id не найден";
                return idResult;
            }

            ProhibitionExclusion exclusion = new ProhibitionExclusion();
            exclusion.setProhibition(prohibition);
            exclusion.setOrgOwner(org.getIdOfOrg());
            exclusion.setCreatedDate(new Date());
            exclusion.setDeletedState(false);
            exclusion.setSendAll(SendToAssociatedOrgs.SendToSelf);
            Criteria objectCriteria = persistenceSession.createCriteria(objectClass);
            objectCriteria.add(Restrictions.eq("globalId", idOfObject));
            if (objectClass.equals(Good.class)) {
                Good good = (Good) objectCriteria.uniqueResult();
                if (good == null) {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Товар с указанным id не найден";
                    return idResult;
                }
                exclusion.setGood(good);
            } else if (objectClass.equals(GoodGroup.class)) {
                GoodGroup goodGroup = (GoodGroup) objectCriteria.uniqueResult();
                if (goodGroup == null) {
                    idResult.resultCode = RC_INTERNAL_ERROR;
                    idResult.description = "Группа товаров с указанным id не найдена";
                    return idResult;
                }
                exclusion.setGoodsGroup(goodGroup);
            } else {
                idResult.resultCode = RC_INTERNAL_ERROR;
                idResult.description = RC_INTERNAL_ERROR_DESC;
                return idResult;
            }
            exclusion.setGlobalVersion(DAOService.getInstance()
                    .updateVersionByDistributedObjects(ProhibitionExclusion.class.getSimpleName()));
            persistenceSession.save(exclusion);

            idResult.id = exclusion.getGlobalId();

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            idResult.resultCode = RC_INTERNAL_ERROR;
            idResult.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return idResult;
    }

    @Override
    public ClassStudentListResult getStudentListByIdOfClientGroup(Long idOfClientGroup) {
        authenticateRequest(null);

        ClassStudentListResult classStudentListResult = new ClassStudentListResult();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("idOfClientGroup", idOfClientGroup));
            List objects = clientCriteria.list();
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();
            ClassStudentList classStudentList = new ClassStudentList();
            if (!objects.isEmpty()) {
                for (Object object : objects) {
                    Client client = (Client) object;
                    ClientSummaryExt clientSummaryExt = objectFactory.createClientSummaryExt();
                    clientSummaryExt.setContractId(client.getContractId());
                    clientSummaryExt.setFirstName(client.getPerson().getFirstName());
                    clientSummaryExt.setLastName(client.getPerson().getSurname());
                    clientSummaryExt.setMiddleName(client.getPerson().getSecondName());
                    classStudentList.getC().add(clientSummaryExt);
                }
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            classStudentListResult.classStudentList = classStudentList;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return classStudentListResult;
    }

    @Override
    public ClientGroupListResult getGroupListByOrg(Long idOfOrg) {
        authenticateRequest(null);

        ClientGroupListResult clientGroupListResult = new ClientGroupListResult();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria clientGroupCriteria = persistenceSession.createCriteria(ClientGroup.class);
            clientGroupCriteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));

            List objects = clientGroupCriteria.list();
            ClientGroupList clientGroupList = new ClientGroupList();
            if (!objects.isEmpty()) {
                for (Object object : objects) {
                    ClientGroup clientGroup = (ClientGroup) object;
                    ClientGroupItem clientGroupItem = new ClientGroupItem();
                    clientGroupItem.setIdOfClientGroup(clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup());
                    clientGroupItem.setGroupName(clientGroup.getGroupName());
                    clientGroupList.getG().add(clientGroupItem);
                }
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            clientGroupListResult.clientGroupList = clientGroupList;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return clientGroupListResult;
    }

    class ClientRequest {

        final static int CLIENT_ID_INTERNALID = 0, CLIENT_ID_SAN = 1, CLIENT_ID_EXTERNAL_ID = 2, CLIENT_ID_GUID = 3, CLIENT_SUB_ID = 4;

        public Data process(Client client, Session session, Processor processor) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();
            try {
                processor.process(client, data, objectFactory, session);
            } catch (Exception e) {
                logger.error("Failed to process client room controller request", e);
                data.setResultCode(RC_INTERNAL_ERROR);
                data.setDescription(e.toString());
            }
            return data;
        }

        public Data process(Long contractId, Processor processor) {
            return process(contractId, processor, null);
        }

        public Data process(Long contractId, Processor processor, HTTPDataHandler handler) {

            Boolean enableSubBalanceOperation = RuntimeContext.getInstance()
                    .getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
            if (enableSubBalanceOperation) {
                String contractIdStr = String.valueOf(contractId);
                int len = contractIdStr.length();
                if (ContractIdGenerator.luhnTest(contractIdStr) || len < 2 || RuntimeContext.RegistryType.isSpb()) {
                    return process(contractId, CLIENT_ID_INTERNALID, processor, handler);
                } else {
                    return process(contractId, CLIENT_SUB_ID, processor, handler);
                }
            } else {
                return process(contractId, CLIENT_ID_INTERNALID, processor, handler);
            }
        }

        public Data process(Object id, int clientIdType, Processor processor, HTTPDataHandler handler) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();
            Integer subBalanceNum = 0;
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createExternalServicesPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
                if (clientIdType == CLIENT_ID_INTERNALID) {
                    clientCriteria.add(Restrictions.eq("contractId", (Long) id));
                } else if (clientIdType == CLIENT_ID_SAN) {
                    clientCriteria.add(Restrictions.ilike("san", (String) id, MatchMode.EXACT));
                } else if (clientIdType == CLIENT_ID_EXTERNAL_ID) {
                    clientCriteria.add(Restrictions.eq("externalId", (Long) id));
                } else if (clientIdType == CLIENT_ID_GUID) {
                    clientCriteria.add(Restrictions.eq("clientGUID", (String) id));
                } else if (clientIdType == CLIENT_SUB_ID) {
                    String subBalanceNumber = id.toString();
                    int len = subBalanceNumber.length();
                    if (ContractIdGenerator.luhnTest(subBalanceNumber.substring(0, len - 2))) {
                        subBalanceNum = Integer.parseInt(subBalanceNumber.substring(len - 2));
                        clientCriteria.add(Restrictions
                                .eq("contractId", Long.parseLong(subBalanceNumber.substring(0, len - 2))));
                    } else {
                        clientCriteria.add(Restrictions.eq("contractId", (Long) id));
                    }
                }

                List<Client> clients = clientCriteria.list();

                if (clients.isEmpty()) {
                    data.setResultCode(RC_CLIENT_NOT_FOUND);
                    data.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                } else if (clients.size() > 1) {
                    data.setResultCode(RC_SEVERAL_CLIENTS_WERE_FOUND);
                    data.setDescription(RC_SEVERAL_CLIENTS_WERE_FOUND_DESC);
                } else {
                    Client client = (Client) clients.get(0);
                    try {
                        client.getSubBalance(subBalanceNum);
                        processor.process(client, subBalanceNum, data, objectFactory, persistenceSession,
                                persistenceTransaction);
                        data.setIdOfContract(client.getContractId());
                        data.setResultCode(RC_OK);
                        data.setDescription(RC_OK_DESC);
                        if (handler != null) {
                            handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(),
                                    new Date(System.currentTimeMillis()), handler.getData().getSsoId(),
                                    client.getIdOfClient(), handler.getData().getOperationType());
                        }
                    } catch (NullPointerException e) {
                        data.setResultCode(RC_CLIENT_NOT_FOUND);
                        data.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                    } catch (DatatypeConfigurationException e)
                    {
                        data.setResultCode(RC_INVALID_DATA);
                        data.setDescription(RC_NOT_ALL_ARG);
                    }
                }
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed to process client room controller request", e);
                data.setResultCode(RC_INTERNAL_ERROR);
                data.setDescription(e.toString());
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
            return data;
        }

    }

    class OrgRequest {

        public Data process(long orgId, Processor processor) {
            ObjectFactory objectFactory = new ObjectFactory();
            Data data = objectFactory.createData();

            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                Criteria criteria = persistenceSession.createCriteria(Org.class);
                criteria.add(Restrictions.eq("idOfOrg", orgId));
                List<Org> orgs = criteria.list();

                if (orgs.isEmpty()) {
                    data.setResultCode(RC_INVALID_DATA);
                    data.setDescription("Организация не найдена");
                } else {
                    Org org = (Org) orgs.get(0);
                    processor.process(org, data, objectFactory, persistenceSession, persistenceTransaction);
                    data.setIdOfContract(null);
                    data.setResultCode(RC_OK);
                    data.setDescription("OK");
                }
                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Failed to process client room controller request", e);
                data.setResultCode(RC_INTERNAL_ERROR);
                data.setDescription(e.toString());
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
            return data;
        }

    }

    @Override
    public ClientSummaryResult getSummary(Long contractId) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processSummary(client, data, objectFactory, session);
            }
        });

        ClientSummaryResult clientSummaryResult = new ClientSummaryResult();
        clientSummaryResult.clientSummary = data.getClientSummaryExt();
        clientSummaryResult.resultCode = data.getResultCode();
        clientSummaryResult.description = data.getDescription();
        return clientSummaryResult;
    }

    @Override
    public ClientSummaryResult getSummary(String san) {
        HTTPData data1 = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data1);
        authenticateRequest(null, handler);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                            Session session, Transaction transaction) throws Exception {
                        processSummary(client, data, objectFactory, session);
                    }
                }, handler);

        ClientSummaryResult clientSummaryResult = new ClientSummaryResult();
        clientSummaryResult.clientSummary = data.getClientSummaryExt();
        clientSummaryResult.resultCode = data.getResultCode();
        clientSummaryResult.description = data.getDescription();
        return clientSummaryResult;
    }

    @Override
    public ClientSummaryResult getSummaryByTypedId(String id, int idType) {
        HTTPData data1 = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data1);
        authenticateRequest(null, handler);

        Object idVal = null;
        if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_INTERNALID) {
            idVal = Long.parseLong(id);
        } else if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_EXTERNAL_ID) {
            idVal = Long.parseLong(id);
        } else if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN) {
            idVal = id;
        } else if (idType == ClientRoomControllerWS.ClientRequest.CLIENT_ID_GUID) {
            idVal = id;
        } else {
            return new ClientSummaryResult(null, RC_INVALID_DATA, "idType invalid");
        }

        Data data = new ClientRequest().process(idVal, idType, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processSummary(client, data, objectFactory, session);
            }
        }, handler);

        ClientSummaryResult clientSummaryResult = new ClientSummaryResult();
        clientSummaryResult.clientSummary = data.getClientSummaryExt();
        clientSummaryResult.resultCode = data.getResultCode();
        clientSummaryResult.description = data.getDescription();
        return clientSummaryResult;
    }

    public static ClientSummaryBase processSummaryBase(Client client) {
        ClientSummaryBase clientSummaryBase = new ClientSummaryBase();
        clientSummaryBase.setContractId(client.getContractId());
        clientSummaryBase.setBalance(client.getBalance());
        clientSummaryBase.setFirstName(client.getPerson().getFirstName());
        clientSummaryBase.setLastName(client.getPerson().getSurname());
        clientSummaryBase.setMiddleName(client.getPerson().getSecondName());
        if (client.getClientGroup() == null) {
            clientSummaryBase.setGrade("");
            clientSummaryBase.setGroupPredefined(0);
        } else {
            clientSummaryBase.setGrade(client.getClientGroup().getGroupName());
            Long groupId = client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup();
            if (groupId >= ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()) {
                clientSummaryBase.setGroupPredefined(1);
            } else {
                clientSummaryBase.setGroupPredefined(0);
            }

        }
        clientSummaryBase.setOfficialName(client.getOrg().getShortNameInfoService());
        clientSummaryBase.setMobilePhone(client.getMobile());
        clientSummaryBase.setOrgId(client.getOrg().getIdOfOrg());
        clientSummaryBase.setOrgType(client.getOrg().getType());
        clientSummaryBase.setGuid(client.getClientGUID());
        clientSummaryBase.setSpecialMenu(client.getSpecialMenu() == null || !client.getSpecialMenu() ? 0 : 1);
        clientSummaryBase.setGender(client.getGender());
        return clientSummaryBase;
    }

    private void processSummary(Client client, Data data, ObjectFactory objectFactory, Session session)
            throws DatatypeConfigurationException {
        ClientSummaryExt clientSummaryExt = objectFactory.createClientSummaryExt();
        /* Номер контракта */
        clientSummaryExt.setContractId(client.getContractId());
        /* дата заключения контракта */
        clientSummaryExt.setDateOfContract(toXmlDateTime(client.getContractTime()));
        /* Текущий баланс клиента */
        clientSummaryExt.setBalance(client.getBalance());
        /* Баланс субсчета АП клиента */
        final Long subBalance1 = client.getSubBalance1() == null ? 0L : client.getSubBalance1();
        clientSummaryExt.setSubBalance1(subBalance1);
        /* Баланс основного счета клиента */
        clientSummaryExt.setSubBalance0(client.getBalance() - subBalance1);
        /* лимит овердрафта */
        clientSummaryExt.setOverdraftLimit(client.getLimit());
        /* Статус контракта (Текстовое значение) */
        clientSummaryExt.setStateOfContract(Client.CONTRACT_STATE_NAMES[client.getContractState()]);
        /*ограничения дневных затрат за день*/
        clientSummaryExt.setExpenditureLimit(client.getExpenditureLimit());
        /*порог снижения баланса для отправки уведомления*/
        clientSummaryExt.setThresholdBalanceNotify(client.getBalanceToNotify());
        /* ФИО Клиента */
        clientSummaryExt.setFirstName(client.getPerson().getFirstName());
        clientSummaryExt.setLastName(client.getPerson().getSurname());
        clientSummaryExt.setMiddleName(client.getPerson().getSecondName());
        /* Флаги увидомлений клиента (Истина/ложь)*/
        clientSummaryExt.setNotifyViaEmail(client.isNotifyViaEmail());
        clientSummaryExt.setNotifyViaSMS(client.isNotifyViaSMS());
        clientSummaryExt.setNotifyViaPUSH(client.isNotifyViaPUSH());
        /* контактный телефон и емайл адрес электронной почты */
        clientSummaryExt.setMobilePhone(client.getMobile());
        clientSummaryExt.setEmail(client.getEmail());
        //clientSummaryExt.setLastUpdateDate(toXmlDateTime(OrgRepository.getInstance().getLastProcessSectionsDate(client.getOrg().getIdOfOrg(),
        //        typesForSummary)));
        Contragent defaultMerchant = client.getOrg().getDefaultSupplier();
        if (defaultMerchant != null) {
            clientSummaryExt.setDefaultMerchantId(defaultMerchant.getIdOfContragent());
            clientSummaryExt.setDefaultMerchantInfo(
                    ParameterStringUtils.extractParameters("TSP.", defaultMerchant.getRemarks()));
        }
        EnterEvent ee = DAOUtils.getLastEnterEvent(session, client);
        if (ee != null) {
            clientSummaryExt.setLastEnterEventCode(ee.getPassDirection());
            clientSummaryExt.setLastEnterEventTime(toXmlDateTime(ee.getEvtDateTime()));
        }
        /* Группа к которой относится клиент (Наименование класса ученика) */
        Long groupId;
        if (client.getClientGroup() == null) {
            clientSummaryExt.setGrade("");
            clientSummaryExt.setGroupPredefined(0);
        } else {
            clientSummaryExt.setGrade(client.getClientGroup().getGroupName());
            groupId = client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup();
            if (groupId >= ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                    && groupId <= ClientGroup.Predefined.CLIENT_DISPLACED.getValue()) {
                clientSummaryExt.setGroupPredefined(1);
            } else {
                clientSummaryExt.setGroupPredefined(0);
            }
        }
        /* Краткое наименование Учебного учреждения для сервиса информирования*/
        clientSummaryExt.setOfficialName(client.getOrg().getShortNameInfoService());
        // Новые параметры:
        String phone = client.getPhone();
        if (phone != null) {
            clientSummaryExt.setPhone(phone);
        }

        String address = client.getAddress();
        if (address != null) {
            clientSummaryExt.setAddress(address);
        }


        clientSummaryExt.setLimit(client.getLimit());

        Integer freePayCount = client.getFreePayCount();
        if (freePayCount != null) {
            clientSummaryExt.setFreePayCount(client.getFreePayCount());
        }

        Integer freePayMaxCount = client.getFreePayMaxCount();
        if (freePayMaxCount != null) {
            clientSummaryExt.setFreePayMaxCount(client.getFreePayMaxCount());
        }

        Date lastFreePayTime = client.getLastFreePayTime();

        if (lastFreePayTime != null) {
            GregorianCalendar greLastFreePayTime = new GregorianCalendar();
            greLastFreePayTime.setTime(lastFreePayTime);
            XMLGregorianCalendar xmlLastFreePayTime = DatatypeFactory.newInstance()
                    .newXMLGregorianCalendar(greLastFreePayTime);

            clientSummaryExt.setLastFreePayTime(xmlLastFreePayTime);
        }

        clientSummaryExt.setDiscountMode(client.getDiscountMode());

        clientSummaryExt.setOrgId(client.getOrg().getIdOfOrg());
        clientSummaryExt.setOrgType(client.getOrg().getType());

        clientSummaryExt.setLastConfirmMobile(toXmlDateTime(client.getLastConfirmMobile()));
        clientSummaryExt.setGender(client.getGender());

        data.setClientSummaryExt(clientSummaryExt);
    }

    @Override
    public PhotoURLResult getPhotoURL(Long contractId, String guid, int size, boolean isNew) {
        PhotoURLResult result = new PhotoURLResult();
        Session session = null;
        Transaction transaction = null;
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = null;
                if (contractId != null && contractId > 0) {
                    client = findClient(session, contractId, null, result);
                } else if (!StringUtils.isEmpty(guid)) {
                    client = DAOUtils.findClientByGuid(session, guid);
                    if (client == null) {
                        result.resultCode = RC_CLIENT_NOT_FOUND;
                        result.description = RC_CLIENT_NOT_FOUND_DESC;
                    }
                } else {
                    throw new IllegalArgumentException("Не указан номер лицевого счета либо GUID клиента");
                }
                if (client == null) {
                    return result;
                }
                getPhotoUrl(size, result, client, ImageUtils.findClientPhoto(session, client.getIdOfClient()), isNew);
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        } catch (IllegalArgumentException e) {
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = e.getMessage();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    private void getPhotoUrl(int size, PhotoURLResult result, Client client, ClientPhoto photo, boolean isNew) {
        try {
            result.URL = ImageUtils.getPhotoURL(client, photo, size, isNew);
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            result.status = ImageUtils.getPhotoStatus(photo);
        } catch (ImageUtils.NoPhotoException e) {
            result.URL = ImageUtils.getDefaultImageURL();
            result.resultCode = RC_CLIENT_DOES_NOT_HAVE_PHOTO;
            result.description = e.getMessage();
        } catch (ImageUtils.NoNewPhotoException e) {
            result.resultCode = RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO;
            result.description = RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO_DESC;
        } catch (ImageUtils.NoSuchImageSizeException e) {
            result.resultCode = RC_IMAGE_SIZE_NOT_FOUND;
            result.description = e.getMessage();
        }
    }

    @Override
    public PhotoURLResult uploadPhoto(Long contractId, Long guardianContractId, Image photo, int size) {
        PhotoURLResult result = new PhotoURLResult();
        Session session = null;
        Transaction transaction = null;
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    return result;
                }
                Client guardian = findClient(session, guardianContractId, null, result);
                if (guardian == null) {
                    return result;
                }
                ClientPhoto clientPhoto = ImageUtils.findClientPhoto(session, client.getIdOfClient());
                if (clientPhoto != null) {
                    ImageUtils.saveImage(client, clientPhoto, photo, true);
                    clientPhoto.setIsNew(true);
                    clientPhoto.setIsCanceled(false);
                } else {
                    String imageName = ImageUtils
                            .saveImage(client.getContractId(), client.getIdOfClient(), photo, true);
                    clientPhoto = new ClientPhoto(client.getIdOfClient(), guardian, imageName, true);
                }
                session.saveOrUpdate(clientPhoto);
                transaction.commit();
                transaction = null;
                getPhotoUrl(size, result, client, clientPhoto, true);
            } catch (IOException e) {
                logger.error(RC_IMAGE_NOT_SAVED_DESC + ": " + e.getMessage(), e);
                result.resultCode = RC_IMAGE_NOT_SAVED;
                result.description = RC_IMAGE_NOT_SAVED_DESC + ": " + e.getMessage();
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (ImageUtils.ImageUtilsException e) {
            result.resultCode = RC_IMAGE_NOT_VALIDATED;
            result.description = e.getMessage();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public Result deleteNewPhoto(Long contractId) {
        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    return result;
                }
                ClientPhoto photo = ImageUtils.findClientPhoto(session, client.getIdOfClient());
                if (photo != null && photo.getIsNew()) {
                    boolean deleted = ImageUtils
                            .deleteImage(client.getContractId(), client.getIdOfClient(), photo.getName(), true);
                    if (!deleted) {
                        result.resultCode = RC_IMAGE_NOT_DELETED;
                        result.description = RC_IMAGE_NOT_DELETED_DESC;
                    } else {
                        result.resultCode = RC_OK;
                        result.description = RC_OK_DESC;
                        photo.setIsNew(false);
                        session.update(photo);
                    }
                } else {
                    result.resultCode = RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO;
                    result.description = RC_CLIENT_DOES_NOT_HAVE_NEW_PHOTO_DESC;
                }
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public PurchaseListResult getPurchaseList(Long contractId, final Date startDate, final Date endDate,
            final Short mode) {

        Long clientContractId = contractId;
        String contractIdstr = String.valueOf(contractId);
        if (ContractIdGenerator.luhnTest(contractIdstr) || RuntimeContext.RegistryType.isSpb()) {
            clientContractId = contractId;
        } else {
            int len = contractIdstr.length();
            if (len > 2 && ContractIdGenerator.luhnTest(contractIdstr.substring(0, len - 2))) {
                clientContractId = Long.parseLong(contractIdstr.substring(0, len - 2));
            }
        }

        HTTPData httpData = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(httpData);
        authenticateRequest(clientContractId, handler);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                if (subBalanceNum.equals(0)) {
                    processPurchaseList(client.getIdOfClient(), data, objectFactory, session, endDate, startDate, null, mode);
                }
                if (subBalanceNum.equals(1)) {
                    processPurchaseList(client.getIdOfClient(), data, objectFactory, session, endDate, startDate,
                            OrderTypeEnumType.SUBSCRIPTION_FEEDING, mode);
                }
            }
        }, handler);
        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();

        return purchaseListResult;
    }

    @Override
    public PurchaseListResult getPurchaseList(String san, final Date startDate, final Date endDate, final Short mode) {
        HTTPData httpData = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(httpData);
        authenticateRequest(null, handler);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                            Session session, Transaction transaction) throws Exception {
                        processPurchaseList(client.getIdOfClient(), data, objectFactory, session, endDate, startDate, null, mode);
                    }
                }, handler);

        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();
        return purchaseListResult;
    }

    @Override
    public PurchaseListResult getPurchaseSubscriptionFeedingList(String san, final Date startDate, final Date endDate,
            final Short mode) {
        HTTPData httpData = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(httpData);
        authenticateRequest(null, handler);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                            Session session, Transaction transaction) throws Exception {
                        processPurchaseList(client.getIdOfClient(), data, objectFactory, session, endDate, startDate,
                                OrderTypeEnumType.SUBSCRIPTION_FEEDING, mode);
                    }
                }, handler);

        PurchaseListResult purchaseListResult = new PurchaseListResult();
        purchaseListResult.purchaseList = data.getPurchaseListExt();
        purchaseListResult.resultCode = data.getResultCode();
        purchaseListResult.description = data.getDescription();
        return purchaseListResult;
    }

    private void processPurchaseList(Long idOfClient, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate, OrderTypeEnumType orderType, Short mode)
            throws DatatypeConfigurationException {
        int nRecs = 0;
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        String orderTypeCondition = (orderType == null ? "" : " and o.orderType = :orderType ");
        Query query = session.createQuery("select o from Order o where o.client.idOfClient = :client and o.createTime >= :startDate " +
                "and o.createTime < :endDate " + orderTypeCondition+ " order by o.createTime");
        query.setParameter("client", idOfClient);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", nextToEndDate);
        if (orderType != null) {
            query.setParameter("orderType", orderType);
        }
        List ordersList = query.getResultList();
        PurchaseListExt purchaseListExt = objectFactory.createPurchaseListExt();
        for (Object o : ordersList) {
            if (nRecs++ > MAX_RECS_getPurchaseList) {
                break;
            }
            Order order = (Order) o;
            PurchaseExt purchaseExt = objectFactory.createPurchaseExt();
            purchaseExt.setByCard(order.getSumByCard());
            purchaseExt.setSocDiscount(order.getSocDiscount());
            purchaseExt.setTrdDiscount(order.getTrdDiscount());
            purchaseExt.setDonation(order.getGrantSum());
            purchaseExt.setSum(order.getRSum());
            purchaseExt.setByCash(order.getSumByCash());
            purchaseExt.setLastUpdateDate(toXmlDateTime(OrgRepository.getInstance()
                    .getLastProcessSectionsDate(order.getOrg().getIdOfOrg(), SectionType.PAYMENT_REGISTRY)));
            if (order.getCard() == null) {
                purchaseExt.setIdOfCard(null);
            } else {
                purchaseExt.setIdOfCard(order.getCard().getIdOfCard());
            }
            //было так: purchaseExt.setIdOfCard(order.getCard().getCardPrintedNo());
            purchaseExt.setTime(toXmlDateTime(order.getCreateTime()));
            if (mode != null && mode == 1) {
                purchaseExt.setState(order.getState());
            }
            Set<OrderDetail> orderDetailSet = ((Order) o).getOrderDetails();
            for (OrderDetail od : orderDetailSet) {
                PurchaseElementExt purchaseElementExt = objectFactory.createPurchaseElementExt();
                purchaseElementExt.setIdOfOrderDetail(od.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
                purchaseElementExt.setAmount(od.getQty());
                purchaseElementExt.setName(getReadableComplexName(od));
                purchaseElementExt.setSum(od.getRPrice() * od.getQty());
                purchaseElementExt.setMenuType(od.getMenuType());
                if (od.isComplex()) {
                    purchaseElementExt.setType(1);
                } else if (od.isComplexItem()) {
                    purchaseElementExt.setType(2);
                } else {
                    purchaseElementExt.setType(0);
                }
                if (od.isFRationSpecified()) {
                    purchaseElementExt.setfRation(od.getfRation());
                }
                purchaseExt.getE().add(purchaseElementExt);
            }

            purchaseListExt.getP().add(purchaseExt);
        }
        data.setPurchaseListExt(purchaseListExt);
    }

    private String getReadableComplexName(OrderDetail orderDetail) {
        if (orderDetail.getMenuType() >= OrderDetail.TYPE_COMPLEX_MIN && orderDetail.getMenuType() <= OrderDetail.TYPE_COMPLEX_MAX) {
            if (!orderDetail.isFRationSpecified()) {
                return orderDetail.getMenuDetailName();
            } else {
                return OrderDetailFRationTypeWTdiet.getDescription(orderDetail.getfRation());
            }
        }
        return orderDetail.getMenuDetailName();
    }

    @Override
    public PurchaseListWithDetailsResult getPurchaseListWithDetails(Long contractId, final Date startDate,
            final Date endDate, final Short mode) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(null, handler);
        ObjectFactory objectFactory = new ObjectFactory();
        Org org = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getOrgByContractId(contractId);
        if(org == null) {
            PurchaseListWithDetailsResult result = new PurchaseListWithDetailsResult();
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = RC_CLIENT_NOT_FOUND_DESC;
            return result;
        }
        if (!org.getUseWebArm()) {
            return processPurchaseListWithDetails(contractId, objectFactory, startDate, endDate, mode, handler);
        } else {
            return processPurchaseWtListWithDetails(contractId, objectFactory, startDate, endDate, mode, handler);
        }
    }

    private PurchaseListWithDetailsResult processPurchaseListWithDetails(Long contractId, ObjectFactory objectFactory,
            Date startDate, Date endDate, Short mode, HTTPDataHandler handler) {
        PurchaseListWithDetailsResult result = new PurchaseListWithDetailsResult();
        PurchaseListWithDetailsExt purchaseListWithDetailsExt = objectFactory.createPurchaseListWithDetailsExt();
        try {
            Client client = DAOReadExternalsService.getInstance().findClient(null, contractId);

            if (client == null) {
                return result;
            }
            handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), new Date(System.currentTimeMillis()),
                    handler.getData().getSsoId(), client.getIdOfClient(), handler.getData().getOperationType());

            int nRecs = 0;
            Date nextToEndDate = DateUtils.addDays(endDate, 1);
            List<OrderItem> ordersList = DAOReadExternalsService.getInstance()
                    .getClientOrdersByPeriod(client, startDate, nextToEndDate);
            if (ordersList.size() == 0) {
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
                result.purchaseListWithDetailsExt = purchaseListWithDetailsExt;
                return result;
            }
            List<CompositeIdOfOrder> orders = getOrdersByOrderItems(ordersList);
            List<OrderDetail> detailsList = DAOReadExternalsService.getInstance().getOrderDetailsByOrders(orders);

            Set<Long> orderOrgIds = getOrgsByOrders(ordersList);
            Set<Long> menuIds = getIdOfMenusByOrderDetails(detailsList);
            if (orderOrgIds.size() == 0 || menuIds.size() == 0) {
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
                result.purchaseListWithDetailsExt = purchaseListWithDetailsExt;
                return result;
            }
            List<MenuDetail> menuDetails = DAOReadExternalsService.getInstance()
                    .getMenuDetailsByOrderDetails(orderOrgIds, menuIds, startDate, endDate);

            Map<Long, Date> lastProcessMap = new HashMap<Long, Date>();
            for (OrderItem order : ordersList) {
                if (nRecs++ > MAX_RECS_getPurchaseList) {
                    break;
                }
                PurchaseWithDetailsExt purchaseWithDetailsExt = objectFactory.createPurchaseWithDetailsExt();
                purchaseWithDetailsExt.setByCard(order.getSumByCard());
                purchaseWithDetailsExt.setSocDiscount(order.getSocDiscount());
                purchaseWithDetailsExt.setTrdDiscount(order.getTrdDiscount());
                purchaseWithDetailsExt.setDonation(order.getGrantSum());
                purchaseWithDetailsExt.setSum(order.getrSum());
                purchaseWithDetailsExt.setByCash(order.getSumByCash());
                purchaseWithDetailsExt.setLastUpdateDate(
                        toXmlDateTime(getLastPaymentRegistryDate(order.getIdOfOrg(), lastProcessMap)));
                purchaseWithDetailsExt.setIdOfCard(order.getIdOfCard());
                purchaseWithDetailsExt.setTime(toXmlDateTime(order.getCreateTime()));
                if (mode != null && mode == 1) {
                    purchaseWithDetailsExt.setState(order.getState());
                }
                for (OrderDetail od : findDetailsByOrder(order, detailsList)) {
                    PurchaseWithDetailsElementExt purchaseWithDetailsElementExt = objectFactory
                            .createPurchaseWithDetailsElementExt();
                    purchaseWithDetailsElementExt
                            .setIdOfOrderDetail(od.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
                    purchaseWithDetailsElementExt.setAmount(od.getQty());
                    purchaseWithDetailsElementExt.setName(od.getMenuDetailName());
                    purchaseWithDetailsElementExt.setSum(od.getRPrice() * od.getQty());
                    purchaseWithDetailsElementExt.setMenuType(od.getMenuType());
                    purchaseWithDetailsElementExt.setLastUpdateDate(
                            toXmlDateTime(getLastPaymentRegistryDate(order.getIdOfOrg(), lastProcessMap)));
                    if (od.isComplex()) {
                        purchaseWithDetailsElementExt.setType(1);
                    } else if (od.isComplexItem()) {
                        purchaseWithDetailsElementExt.setType(2);
                    } else {
                        purchaseWithDetailsElementExt.setType(0);
                    }
                    if (od.isFRationSpecified()) {
                        purchaseWithDetailsElementExt.setfRation(od.getfRation());
                    }
                    if (od.getIdOfMenuFromSync() != null) {

                        MenuDetail menuDetail = findMenuDetailByOrderDetail(od.getIdOfMenuFromSync(), menuDetails);

                        if (menuDetail != null) {
                            purchaseWithDetailsElementExt.setPrice(menuDetail.getPrice());
                            purchaseWithDetailsElementExt.setCalories(menuDetail.getCalories());
                            purchaseWithDetailsElementExt.setOutput(menuDetail.getMenuDetailOutput());
                            purchaseWithDetailsElementExt.setVitB1(menuDetail.getVitB1());
                            purchaseWithDetailsElementExt.setVitB2(menuDetail.getVitB2());
                            purchaseWithDetailsElementExt.setVitPp(menuDetail.getVitPp());
                            purchaseWithDetailsElementExt.setVitC(menuDetail.getVitC());
                            purchaseWithDetailsElementExt.setVitA(menuDetail.getVitA());
                            purchaseWithDetailsElementExt.setVitE(menuDetail.getVitE());
                            purchaseWithDetailsElementExt.setMinCa(menuDetail.getMinCa());
                            purchaseWithDetailsElementExt.setMinP(menuDetail.getMinP());
                            purchaseWithDetailsElementExt.setMinMg(menuDetail.getMinMg());
                            purchaseWithDetailsElementExt.setMinFe(menuDetail.getMinFe());
                            purchaseWithDetailsElementExt.setProtein(menuDetail.getProtein());
                            purchaseWithDetailsElementExt.setFat(menuDetail.getFat());
                            purchaseWithDetailsElementExt.setCarbohydrates(menuDetail.getCarbohydrates());
                        }
                    }

                    purchaseWithDetailsExt.getE().add(purchaseWithDetailsElementExt);
                }

                purchaseListWithDetailsExt.getP().add(purchaseWithDetailsExt);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;
        result.purchaseListWithDetailsExt = purchaseListWithDetailsExt;

        return result;
    }

    private PurchaseListWithDetailsResult processPurchaseWtListWithDetails(Long contractId, ObjectFactory objectFactory,
            Date startDate, Date endDate, Short mode, HTTPDataHandler handler) {
        PurchaseListWithDetailsResult result = new PurchaseListWithDetailsResult();
        PurchaseListWithDetailsExt purchaseListWithDetailsExt = objectFactory.createPurchaseListWithDetailsExt();
        try {
            Client client = DAOReadExternalsService.getInstance().findClient(null, contractId);

            if (client == null) {
                return result;
            }
            handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), new Date(System.currentTimeMillis()),
                    handler.getData().getSsoId(), client.getIdOfClient(), handler.getData().getOperationType());

            int nRecs = 0;
            Date nextToEndDate = DateUtils.addDays(endDate, 1);
            List<OrderItem> ordersList = DAOReadExternalsService.getInstance()
                    .getClientOrdersByPeriod(client, startDate, nextToEndDate);
            if (ordersList.size() == 0) {
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
                result.purchaseListWithDetailsExt = purchaseListWithDetailsExt;
                return result;
            }
            List<CompositeIdOfOrder> orders = getOrdersByOrderItems(ordersList);
            List<OrderDetail> detailsList = DAOReadExternalsService.getInstance().getOrderDetailsByOrders(orders);

            // получить блюда для детализации заказов orderdetails
            Set<WtDish> dishes = DAOReadExternalsService.getInstance()
                    .getWtDishesByOrderDetails(detailsList, startDate, endDate);

            Map<Long, Date> lastProcessMap = new HashMap<>();
            for (OrderItem order : ordersList) {
                if (nRecs++ > MAX_RECS_getPurchaseList) {
                    break;
                }
                // заполняем по заказам
                PurchaseWithDetailsExt purchaseWithDetailsExt = objectFactory.createPurchaseWithDetailsExt();
                purchaseWithDetailsExt.setByCard(order.getSumByCard());
                purchaseWithDetailsExt.setSocDiscount(order.getSocDiscount());
                purchaseWithDetailsExt.setTrdDiscount(order.getTrdDiscount());
                purchaseWithDetailsExt.setDonation(order.getGrantSum());
                purchaseWithDetailsExt.setSum(order.getrSum());
                purchaseWithDetailsExt.setByCash(order.getSumByCash());
                purchaseWithDetailsExt.setLastUpdateDate(
                        toXmlDateTime(getLastPaymentRegistryDate(order.getIdOfOrg(), lastProcessMap)));
                purchaseWithDetailsExt.setIdOfCard(order.getIdOfCard());
                purchaseWithDetailsExt.setTime(toXmlDateTime(order.getCreateTime()));
                if (mode != null && mode == 1) {
                    purchaseWithDetailsExt.setState(order.getState());
                }
                for (OrderDetail od : findDetailsByOrder(order, detailsList)) {
                    // заполняем по покупкам
                    PurchaseWithDetailsElementExt purchaseWithDetailsElementExt = objectFactory
                            .createPurchaseWithDetailsElementExt();
                    purchaseWithDetailsElementExt
                            .setIdOfOrderDetail(od.getCompositeIdOfOrderDetail().getIdOfOrderDetail());
                    purchaseWithDetailsElementExt.setAmount(od.getQty());
                    purchaseWithDetailsElementExt.setName(getReadableComplexName(od));
                    purchaseWithDetailsElementExt.setSum(od.getRPrice() * od.getQty());
                    purchaseWithDetailsElementExt.setMenuType(od.getMenuType());
                    purchaseWithDetailsElementExt.setLastUpdateDate(
                            toXmlDateTime(getLastPaymentRegistryDate(order.getIdOfOrg(), lastProcessMap)));
                    if (od.isComplex()) {
                        purchaseWithDetailsElementExt.setType(1);
                    } else if (od.isComplexItem()) {
                        purchaseWithDetailsElementExt.setType(2);
                    } else {
                        purchaseWithDetailsElementExt.setType(0);
                    }
                    if (od.isFRationSpecified()) {
                        purchaseWithDetailsElementExt.setfRation(od.getfRation());
                    }
                    // если пришли с синхронизацией - od.idOfDish должно быть заполнено (od.idOfComplex?)
                    if (od.getIdOfDish() != null && dishes != null) {
                        WtDish wtDish = findWtDishByOrderDetail(od.getIdOfDish(), dishes);
                        if (wtDish != null) {
                            purchaseWithDetailsElementExt
                                    .setPrice(wtDish.getPrice().multiply(new BigDecimal(100)).longValue());
                            purchaseWithDetailsElementExt.setCalories(
                                    wtDish.getCalories() == null ? (double) 0 : wtDish.getCalories().doubleValue());
                            purchaseWithDetailsElementExt.setOutput(wtDish.getQty() == null ? "" : wtDish.getQty());
                            purchaseWithDetailsElementExt.setVitB1(0.0);
                            purchaseWithDetailsElementExt.setVitB2(0.0);
                            purchaseWithDetailsElementExt.setVitPp(0.0);
                            purchaseWithDetailsElementExt.setVitC(0.0);
                            purchaseWithDetailsElementExt.setVitA(0.0);
                            purchaseWithDetailsElementExt.setVitE(0.0);
                            purchaseWithDetailsElementExt.setMinCa(0.0);
                            purchaseWithDetailsElementExt.setMinP(0.0);
                            purchaseWithDetailsElementExt.setMinMg(0.0);
                            purchaseWithDetailsElementExt.setMinFe(0.0);
                            purchaseWithDetailsElementExt.setProtein(
                                    wtDish.getProtein() == null ? (double) 0 : wtDish.getProtein().doubleValue());
                            purchaseWithDetailsElementExt
                                    .setFat(wtDish.getFat() == null ? (double) 0 : wtDish.getFat().doubleValue());
                            purchaseWithDetailsElementExt.setCarbohydrates(
                                    wtDish.getCarbohydrates() == null ? (double) 0
                                            : wtDish.getCarbohydrates().doubleValue());
                        }
                    }

                    purchaseWithDetailsExt.getE().add(purchaseWithDetailsElementExt);
                }

                purchaseListWithDetailsExt.getP().add(purchaseWithDetailsExt);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;
        result.purchaseListWithDetailsExt = purchaseListWithDetailsExt;

        return result;
    }

    private List<CompositeIdOfOrder> getOrdersByOrderItems(List<OrderItem> items) {
        List<CompositeIdOfOrder> result = new ArrayList<>();
        for (OrderItem item : items) {
            result.add(new CompositeIdOfOrder(item.getIdOfOrg(), item.getIdOfOrder()));
        }
        return result;
    }

    private Date getLastPaymentRegistryDate(Long idOfOrg, Map<Long, Date> map) {
        if (!map.containsKey(idOfOrg)) {
            map.put(idOfOrg,
                    OrgRepository.getInstance().getLastProcessSectionsDate(idOfOrg, SectionType.PAYMENT_REGISTRY));
        }
        return map.get(idOfOrg);
    }

    private List<OrderDetail> findDetailsByOrder(OrderItem order, List<OrderDetail> details) {
        List<OrderDetail> list = new ArrayList<OrderDetail>();
        for (OrderDetail detail : details) {
            if (detail.getIdOfOrder().equals(order.getIdOfOrder()) && detail
                    .getCompositeIdOfOrderDetail().getIdOfOrg().equals(order.getIdOfOrg())) {
                list.add(detail);
            }
        }
        return list;
    }

    private MenuDetail findMenuDetailByOrderDetail(Long idOfMenuFromSync, List<MenuDetail> menuDetails) {
        for (MenuDetail detail : menuDetails) {
            if (idOfMenuFromSync.equals(detail.getIdOfMenuFromSync())) {
                return detail;
            }
        }
        return null;
    }

    private WtDish findWtDishByOrderDetail(Long idOfDish, Set<WtDish> dishes) {
        for (WtDish wtDish : dishes) {
            if (idOfDish.equals(wtDish.getIdOfDish())) {
                return wtDish;
            }
        }
        return null;
    }

    private Set<Long> getOrgsByOrders(List<OrderItem> ordersList) {
        Set set = new HashSet<Long>();
        for (OrderItem order : ordersList) {
            set.add(order.getIdOfOrg());
        }
        return set;
    }

    private Set<Long> getIdOfMenusByOrderDetails(List<OrderDetail> detailsList) {
        Set set = new HashSet<Long>();
        for (OrderDetail detail : detailsList) {
            if (detail.getIdOfMenuFromSync() != null && detail.getIdOfMenuFromSync() != 0) {
                set.add(detail.getIdOfMenuFromSync());
            }
        }
        return set;
    }

    @Override
    public PaymentListResult getPaymentList(Long contractId, final Date startDate, final Date endDate) {

        Long clientContractId = contractId;
        String contractIdstr = String.valueOf(contractId);
        if (ContractIdGenerator.luhnTest(contractIdstr) || RuntimeContext.RegistryType.isSpb()) {
            clientContractId = contractId;
        } else {
            int len = contractIdstr.length();
            if (len > 2 && ContractIdGenerator.luhnTest(contractIdstr.substring(0, len - 2))) {
                clientContractId = Long.parseLong(contractIdstr.substring(0, len - 2));
            }
        }

        authenticateRequest(clientContractId);
        //authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processPaymentList(session, client, subBalanceNum, data, objectFactory, endDate, startDate);
            }
        });

        PaymentListResult paymentListResult = new PaymentListResult();
        paymentListResult.paymentList = data.getPaymentList();
        paymentListResult.resultCode = data.getResultCode();
        paymentListResult.description = data.getDescription();

        return paymentListResult;
    }

    @Override
    public PaymentListResult getPaymentList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                            Session session, Transaction transaction) throws Exception {
                        processPaymentList(session, client, subBalanceNum, data, objectFactory, endDate, startDate);
                    }
                }, null);

        PaymentListResult paymentListResult = new PaymentListResult();
        paymentListResult.paymentList = data.getPaymentList();
        paymentListResult.resultCode = data.getResultCode();
        paymentListResult.description = data.getDescription();

        return paymentListResult;
    }

    @Override
    public PaymentListResult getPaymentSubscriptionFeedingList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                            Session session, Transaction transaction) throws Exception {
                        processPaymentList(session, client, 1, data, objectFactory, endDate, startDate);
                    }
                }, null);

        PaymentListResult paymentListResult = new PaymentListResult();
        paymentListResult.paymentList = data.getPaymentList();
        paymentListResult.resultCode = data.getResultCode();
        paymentListResult.description = data.getDescription();

        return paymentListResult;
    }

    private void processPaymentList(Session session, Client client, Integer subBalanceNum, Data data,
            ObjectFactory objectFactory, Date endDate, Date startDate) throws Exception {
        List clientPaymentsList = DAOReadExternalsService.getInstance()
                .getPaymentsList(client, subBalanceNum, endDate, startDate);
        PaymentList paymentList = objectFactory.createPaymentList();
        int nRecs = 0;
        for (Object o : clientPaymentsList) {
            if (nRecs++ > MAX_RECS) {
                break;
            }
            Object[] row = (Object[]) o;
            Payment payment = new Payment();
            payment.setOrigin(PaymentTextUtils.buildTransferInfo(session, (String) row[6], (Integer) row[3], (String) row[4], (String) row[5]));
            payment.setSum(HibernateUtils.getDbLong(row[1]));
            payment.setTime(toXmlDateTime(new Date(((BigInteger) row[2]).longValue())));
            paymentList.getP().add(payment);
        }
        data.setPaymentList(paymentList);
    }

    @Override
    public MenuListResult getMenuList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                            Session session, Transaction transaction) throws Exception {
                        processMenuFirstDay(client.getOrg(), data, objectFactory, session, startDate, endDate);
                    }
                }, null);

        MenuListResult menuListResult = new MenuListResult();
        if (data.getMenuListExt() != null) {
            Collections.sort(data.getMenuListExt().getM());
        }
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }

    @Override
    public MenuListResult getMenuFirstDay(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                if (!client.getOrg().getUseWebArm()) {
                    processMenuFirstDay(client.getOrg(), data, objectFactory, session, startDate, endDate);
                } else {
                    processWtMenuFirstDay(client.getOrg(), data, objectFactory, session, startDate, endDate);
                }
            }
        });

        MenuListResult menuListResult = new MenuListResult();
        if (data.getMenuListExt() != null) {
            Collections.sort(data.getMenuListExt().getM());
        }
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }

    private void processMenuFirstDay(Org org, Data data, ObjectFactory objectFactory, Session session, Date startDate,
            Date endDate) throws DatatypeConfigurationException {
        Criteria menuCriteria = session.createCriteria(Menu.class);
        menuCriteria.createAlias("menuDetailsInternal", "detal", JoinType.LEFT_OUTER_JOIN);
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        menuCriteria.add(Restrictions.eq("org", org));
        menuCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
        menuCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        menuCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));
        menuCriteria.add(Restrictions
                .or(Restrictions.not(Restrictions.ilike("detal.groupName", groupNotForMos, MatchMode.ANYWHERE)),
                        Restrictions.isNull("detal.groupName")));
        menuCriteria.add(Restrictions.eq("detal.availableNow", 1));
        HibernateUtils.addAscOrder(menuCriteria, "detal.groupName");
        HibernateUtils.addAscOrder(menuCriteria, "detal.menuDetailName");

        ArrayList<Menu> menus = new ArrayList<>(new HashSet(menuCriteria.list()));

        generateMenuDetail(objectFactory, menus, session, data);
    }

    private void processWtMenuFirstDay(Org org, Data data, ObjectFactory objectFactory, Session session, Date startDate,
            Date endDate) throws DatatypeConfigurationException {
        generateWtMenuDetail(org, data, objectFactory, session, startDate, endDate);
    }

    private void generateMenuDetail(ObjectFactory objectFactory, List menus, Session session, Data data)
            throws DatatypeConfigurationException {

        MenuListExt menuListExt = objectFactory.createMenuListExt();
        int nRecs = 0;
        for (Object currObject : menus) {
            if (nRecs++ > MAX_RECS) {
                break;
            }

            Menu menu = (Menu) currObject;
            MenuDateItemExt menuDateItemExt = objectFactory.createMenuDateItemExt();
            menuDateItemExt.setDate(toXmlDateTime(menu.getMenuDate()));

            //Получаем все menuDetail для одного Menu
            for (Object o : menu.getMenuDetails()) {
                MenuDetail menuDetail = (MenuDetail) o;
                MenuItemExt menuItemExt = objectFactory.createMenuItemExt();
                menuItemExt.setGroup(menuDetail.getGroupName());
                menuItemExt.setName(menuDetail.getMenuDetailName());
                menuItemExt.setPrice(menuDetail.getPrice());
                menuItemExt.setCalories(menuDetail.getCalories());
                menuItemExt.setVitB1(menuDetail.getVitB1());
                menuItemExt.setVitB2(menuDetail.getVitB2());
                menuItemExt.setVitPp(menuDetail.getVitPp());
                menuItemExt.setVitC(menuDetail.getVitC());
                menuItemExt.setVitA(menuDetail.getVitA());
                menuItemExt.setVitE(menuDetail.getVitE());
                menuItemExt.setMinCa(menuDetail.getMinCa());
                menuItemExt.setMinP(menuDetail.getMinP());
                menuItemExt.setMinMg(menuDetail.getMinMg());
                menuItemExt.setMinFe(menuDetail.getMinFe());
                menuItemExt.setOutput(menuDetail.getMenuDetailOutput());
                menuItemExt.setAvailableNow(menuDetail.getAvailableNow());
                menuItemExt.setProtein(menuDetail.getProtein());
                menuItemExt.setCarbohydrates(menuDetail.getCarbohydrates());
                menuItemExt.setFat(menuDetail.getFat());
                menuDateItemExt.getE().add(menuItemExt);
            }

            menuListExt.getM().add(menuDateItemExt);
        }
        data.setMenuListExt(menuListExt);
    }

    private void generateWtMenuDetail(Org org, Data data, ObjectFactory objectFactory, Session session, Date startDate,
            Date endDate) throws DatatypeConfigurationException {

        MenuListExt menuListExt = objectFactory.createMenuListExt();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        Date menuDate = calendar.getTime();

        while (menuDate.getTime() < endDate.getTime()) {

            List<WtMenu> menus = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .getWtMenuByDates(menuDate, menuDate, org);
            int nRecs = 0;

            MenuDateItemExt menuDateItemExt = objectFactory.createMenuDateItemExt();
            menuDateItemExt.setDate(toXmlDateTime(menuDate));
            Set<WtDishInfo> wtDishSet = new HashSet<>();

            for (WtMenu menu : menus) {
                if (nRecs++ > MAX_RECS) {
                    break;
                }
                List<WtDishInfo> wtDishes = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                        .getWtDishesByMenuAndDates(menu, menuDate, menuDate);
                if (wtDishes != null && wtDishes.size() > 0) {
                    wtDishSet.addAll(wtDishes);
                }
            }
            Map<Long, Set<String>> menuGroups = DAOReadonlyService.getInstance().getWtMenuGroupsForAllDishes(org.getIdOfOrg());
            if (wtDishSet != null && wtDishSet.size() > 0) {
                //Получаем детализацию для одного Menu
                for (WtDishInfo wtDishInfo : wtDishSet) {
                    List<MenuItemExt> menuItemExt = getMenuItemExt(objectFactory, org.getIdOfOrg(), wtDishInfo, false, menuGroups);
                    menuDateItemExt.getE().addAll(menuItemExt);
                }
            }
            menuListExt.getM().add(menuDateItemExt);
            calendar.add(Calendar.DATE, 1);
            menuDate = calendar.getTime();
        }
        data.setMenuListExt(menuListExt);
    }

    @Override
    public MenuListWithComplexesResult getMenuListWithComplexes(Long contractId, final Date startDate,
            final Date endDate) {
        authenticateRequest(null);
        ObjectFactory objectFactory = new ObjectFactory();
        Org org = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).
                getOrgByContractId(contractId);
        if (!org.getUseWebArm()) {
            return processMenuListWithComplexes(contractId, startDate, endDate, objectFactory);
        } else {
            return processWtMenuListWithComplexes(contractId, startDate, endDate, objectFactory);
        }
    }

    private MenuListWithComplexesResult processMenuListWithComplexes(Long contractId, Date startDate, Date endDate,
            ObjectFactory objectFactory) {
        Session session = null;
        Transaction transaction = null;
        MenuListWithComplexesResult result = new MenuListWithComplexesResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = findClientByContractId(session, contractId, result);

            if (client == null) {
                return result;
            }

            Criteria criteria = session.createCriteria(ComplexInfo.class);
            criteria.createAlias("menuDetail", "detal", JoinType.LEFT_OUTER_JOIN);
            criteria.add(Restrictions.eq("org", client.getOrg()));
            criteria.add(Restrictions.gt("menuDate", startDate));
            criteria.add(Restrictions.lt("menuDate", endDate));
            criteria.add(Restrictions.eq("modeVisible", 1));
            criteria.add(Restrictions
                    .or(Restrictions.not(Restrictions.ilike("detal.groupName", groupNotForMos, MatchMode.ANYWHERE)),
                            Restrictions.isNull("detal.groupName")));

            //Следующее условие - заглушка, в соответствии с https://gitlab.iteco.dev/ispp/processor/issues/438
            criteria.add(Restrictions.or(Restrictions.and(Restrictions
                            .and(Restrictions.not(Restrictions.ilike("complexName", "сотруд", MatchMode.ANYWHERE)),
                                    Restrictions.not(Restrictions.ilike("complexName", "воспит", MatchMode.ANYWHERE))),
                    Restrictions.not(Restrictions.ilike("complexName", "учит", MatchMode.ANYWHERE))),
                    Restrictions.isNull("complexName")));

            List<ComplexInfo> complexInfoList = criteria.list();
            PreorderDAOService preorderDAOService = RuntimeContext.getAppContext().getBean(PreorderDAOService.class);

            List<MenuWithComplexesExt> list = new ArrayList<MenuWithComplexesExt>();
            Map<Date, Boolean> mapDatesForComplex = new HashMap<>();

            List<ProductionCalendar> productionCalendars = DAOUtils
                    .getAllDateFromProdactionCalendarDates(session, startDate, endDate);
            for (ComplexInfo ci : complexInfoList) {
                //Проверка по КУД
                Boolean goodComplex = mapDatesForComplex.get(ci.getMenuDate());
                if (goodComplex == null) {
                    goodComplex = isGoodDate(session, client.getOrg().getIdOfOrg(), client.getIdOfClientGroup(),
                            ci.getMenuDate(), productionCalendars);
                    mapDatesForComplex.put(ci.getMenuDate(), goodComplex);
                    if (!goodComplex) {
                        continue;
                    }
                } else {
                    if (!goodComplex) {
                        continue;
                    }
                }

                if (client.getClientGroup() != null
                        && client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup()
                        < ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()) {
                    //для учеников
                    PreorderComplexItemExt complexItemExt = new PreorderComplexItemExt(ci.getIdOfComplex(),
                            ci.getComplexName(), ci.getCurrentPrice(), ci.getModeOfAdd(), ci.getModeFree(),
                            ci.getModeVisible());
                    PreorderGoodParamsContainer complexParams = preorderDAOService
                            .getComplexParams(complexItemExt, client, ci.getMenuDate());

                    if (!preorderDAOService
                            .isAcceptableComplex(complexItemExt, client.getClientGroup(), client.hasDiscount(),
                                    complexParams, client.getAgeTypeGroup(), new ArrayList(client.getCategories()))) {
                        continue;
                    }
                } else {
                    //для предопределенных не включаем комплексы с какой-либо категорией
                    if (ci.getGood() != null && !ci.getGood().getAgeGroupType().equals(GoodAgeGroupType.UNSPECIFIED)) {
                        continue;
                    }
                }

                List<MenuItemExt> menuItemExtList = getMenuItemsExt(objectFactory, ci.getIdOfComplexInfo(), true);
                //Если у комплекса нет состава, то не выводим его
                if (menuItemExtList.isEmpty()) {
                    continue;
                }
                MenuWithComplexesExt menuWithComplexesExt = new MenuWithComplexesExt(ci);
                menuWithComplexesExt.setMenuItemExtList(menuItemExtList);
                list.add(menuWithComplexesExt);
            }
            result.getMenuWithComplexesList().setList(list);

            transaction.commit();
            transaction = null;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;

        return result;
    }

    private MenuListWithComplexesResult processWtMenuListWithComplexes(Long contractId, Date startDate, Date endDate,
            ObjectFactory objectFactory) {
        Session session = null;
        Transaction transaction = null;
        MenuListWithComplexesResult result = new MenuListWithComplexesResult();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Client client = findClientByContractId(session, contractId, result);
            if (client == null) {
                return result;
            }
            Org org = client.getOrg();

            Map<String, Boolean> complexSign = new HashMap<>();
            complexSign.put("Paid", false);
            complexSign.put("Free", false);
            complexSign.put("Elem", false);
            complexSign.put("Middle", false);

            // Льготные категории
            Set<CategoryDiscount> categoriesDiscount = client.getCategories();

            Set<Long> ageGroupIds = new HashSet<>();
            // 1 Группа клиента
            int clientGroup = checkClientGroup(client);
            if (clientGroup == 1) {
                ageGroupIds.add(6L); // Сотрудники
                ageGroupIds.add(7L); // Все
                complexSign.put("Paid", true);
            } else if (clientGroup == 2) {
                logger.error(RC_CLIENT_NO_LONGER_ACTIVE);
                result.resultCode = RC_INTERNAL_ERROR;
                result.description = RC_CLIENT_NO_LONGER_ACTIVE;
                return result;
            } else if (clientGroup == 3) {

                // 2 Возрастная группа
                if (client.getAgeTypeGroup() != null && !client.getAgeTypeGroup().isEmpty()) {
                    String ageGroupDesc = client.getAgeTypeGroup().toLowerCase();
                    if (ageGroupDesc.startsWith("дошкол")) {
                        complexSign.put("Free", true);
                    } else {
                        checkParallel(client, categoriesDiscount, ageGroupIds, complexSign);
                    }
                } else {
                    complexSign.put("Paid", true);
                }
            } else {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            // Получение дат в диапазоне
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            Date menuDate = calendar.getTime();
            List<MenuWithComplexesExt> list = new ArrayList<>();
            while (menuDate.getTime() < endDate.getTime()) {

                // Проверка даты по календарям
                if (RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                        .isAvailableDate(client, org, menuDate)) {
                    Set<WtComplex> wtComplexes = new HashSet<>();
                    Set<WtComplex> wtDiscComplexes = new HashSet<>();

                    // 6-9, 12 Платные комплексы по возрастным группам и группам
                    if (complexSign.get("Paid")) {
                        ageGroupIds.add(7L); // Все
                        Set<WtComplex> wtComComplexes = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                .getPaidWtComplexesByAgeGroups(menuDate, menuDate, ageGroupIds, org);
                        if (wtComComplexes.size() > 0) {
                            wtComplexes.addAll(wtComComplexes);
                        }
                    }

                    Set<WtComplex> resComplexes = new HashSet<>();

                    // Правила по льготам
                    if (categoriesDiscount.size() > 0) {

                        Set<WtDiscountRule> wtDiscountRuleSet = RuntimeContext.getAppContext()
                                .getBean(PreorderDAOService.class)
                                .getWtDiscountRulesByCategoryOrg(categoriesDiscount, org);

                        // 10 Льготные комплексы по правилам соц. скидок
                        if (complexSign.get("Free") && !complexSign.get("Elem") && !complexSign.get("Middle")) {
                            Set<WtDiscountRule> discRules = RuntimeContext.getAppContext()
                                    .getBean(PreorderDAOService.class)
                                    .getWtDiscountRulesWithMaxPriority(wtDiscountRuleSet);
                            resComplexes = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                    .getFreeWtComplexesByDiscountRules(menuDate, menuDate, discRules, org);
                            if (resComplexes.size() > 0) {
                                wtDiscComplexes.addAll(resComplexes);
                            }
                        }

                        // 13 Льготы для начальной школы
                        if (complexSign.get("Free") && complexSign.get("Elem")) {
                            CategoryDiscount discount = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                    .getElemDiscount();
                            Set<WtDiscountRule> discRules = RuntimeContext.getAppContext()
                                    .getBean(PreorderDAOService.class)
                                    .getWtDiscountRuleBySecondDiscount(wtDiscountRuleSet, discount);
                            discRules = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                    .getWtDiscountRulesWithMaxPriority(discRules);
                            resComplexes = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                    .getFreeWtComplexesByRulesAndAgeGroups(menuDate, menuDate, discRules, ageGroupIds, org);
                            if (resComplexes.size() > 0) {
                                wtDiscComplexes.addAll(resComplexes);
                            }
                        }

                        // 14 Льготы для средней и высшей школы
                        if (complexSign.get("Free") && complexSign.get("Middle")) {
                            ageGroupIds.add(7L); // Все
                            CategoryDiscount middleDiscount = RuntimeContext.getAppContext()
                                    .getBean(PreorderDAOService.class).getMiddleDiscount();
                            CategoryDiscount highDiscount = RuntimeContext.getAppContext()
                                    .getBean(PreorderDAOService.class).getHighDiscount();
                            Set<WtDiscountRule> discRules = RuntimeContext.getAppContext()
                                    .getBean(PreorderDAOService.class)
                                    .getWtDiscountRuleByTwoDiscounts(wtDiscountRuleSet, middleDiscount, highDiscount);
                            discRules = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                    .getWtDiscountRulesWithMaxPriority(discRules);
                            resComplexes = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                    .getFreeWtComplexesByRulesAndAgeGroups(menuDate, menuDate, discRules, ageGroupIds, org);
                            if (resComplexes.size() > 0) {
                                wtDiscComplexes.addAll(resComplexes);
                            }
                        }
                    }

                    // 11 Льготные комплексы для начальной школы
                    if (!complexSign.get("Free") && complexSign.get("Elem")) {
                        Set<WtDiscountRule> discRules = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                .getWtElemDiscountRules(org);
                        discRules = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                .getWtDiscountRulesWithMaxPriority(discRules);
                        resComplexes = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                .getFreeWtComplexesByRulesAndAgeGroups(menuDate, menuDate, discRules, ageGroupIds, org);
                        if (resComplexes.size() > 0) {
                            wtDiscComplexes.addAll(resComplexes);
                        }
                    }

                    if (wtDiscComplexes.size() > 0) {
                        wtComplexes.addAll(wtDiscComplexes);
                    }

                    if (wtComplexes.size() > 0) {
                        for (WtComplex wtComplex : wtComplexes) {
                            // Определяем подходящий состав комплекса
                            WtComplexesItem complexItem = RuntimeContext.getAppContext()
                                    .getBean(PreorderDAOService.class).getWtComplexItemByCycle(wtComplex, menuDate);
                            List<WtDish> wtDishes;
                            if (complexItem != null) {
                                wtDishes = DAOReadExternalsService.getInstance()
                                        .getWtDishesByComplexItemAndDates(complexItem, menuDate, menuDate);
                            } else {
                                // комплекс не выводим
                                continue;
                            }
                            List<MenuItemExt> menuItemExtList = getMenuItemsExt(objectFactory, org.getIdOfOrg(), wtDishes);
                            // Проверка типа питания
                            int isDiscountComplex = wtComplex.getWtComplexGroupItem().getIdOfComplexGroupItem()
                                    .intValue();
                            if (isDiscountComplex == 1) {
                                getComplexExt(org, menuDate, menuItemExtList, wtComplex, list, 1);
                            } else if (isDiscountComplex == 3 && wtDiscComplexes.contains(wtComplex)) {
                                getComplexExt(org, menuDate, menuItemExtList, wtComplex, list, 1);
                                getComplexExt(org, menuDate, menuItemExtList, wtComplex, list, 0);
                            } else {
                                getComplexExt(org, menuDate, menuItemExtList, wtComplex, list, 0);
                            }
                        }
                    }
                }
                calendar.add(Calendar.DATE, 1);
                menuDate = calendar.getTime();
            }
            if (list.size() > 0) {
                result.getMenuWithComplexesList().setList(list);
            }

            transaction.commit();
            transaction = null;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;
        return result;
    }

    private PreorderListWithComplexesGroupResult processPreorderComplexesWithWtMenuList(Long contractId, Date date) {
        Session session = null;
        Transaction transaction = null;
        PreorderListWithComplexesGroupResult groupResult = new PreorderListWithComplexesGroupResult();

        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Client client = findClientByContractId(session, contractId, groupResult);
            if (client == null) {
                return groupResult;
            }
            Org org = client.getOrg();

            // 1 Проверка на включение предзаказа
            if (!org.getPreordersEnabled()) {
                logger.error(RC_PREORDERS_NOT_ENABLED_DESC);
                groupResult.resultCode = RC_INTERNAL_ERROR;
                groupResult.description = RC_PREORDERS_NOT_ENABLED_DESC;
                return groupResult;
            }

            Map<String, Boolean> complexSign = new HashMap<>();
            complexSign.put("Paid", false);
            complexSign.put("Free", false);
            complexSign.put("Elem", false);
            complexSign.put("Middle", false);

            // Льготные категории
            Set<CategoryDiscount> categoriesDiscount = client.getCategories();

            Set<Long> ageGroupIds = new HashSet<>();
            // 2 Группа клиента
            int clientGroup = checkClientGroup(client);
            if (clientGroup == 1) {
                ageGroupIds.add(6L); // Сотрудники
                ageGroupIds.add(7L); // Все
                complexSign.put("Paid", true);
            } else if (clientGroup == 2) {
                logger.error(RC_CLIENT_NO_LONGER_ACTIVE);
                groupResult.resultCode = RC_INTERNAL_ERROR;
                groupResult.description = RC_CLIENT_NO_LONGER_ACTIVE;
                return groupResult;
            } else if (clientGroup == 3) {

                // 2 Возрастная группа
                if (client.getAgeTypeGroup() != null && !client.getAgeTypeGroup().isEmpty()) {
                    String ageGroupDesc = client.getAgeTypeGroup().toLowerCase();
                    if (ageGroupDesc.contains("дошкол")) {
                        logger.error(RC_CLIENT_DOU);
                        groupResult.resultCode = RC_INTERNAL_ERROR;
                        groupResult.description = RC_CLIENT_DOU;
                        return groupResult;
                    } else {
                        checkParallel(client, categoriesDiscount, ageGroupIds, complexSign);
                    }
                } else {
                    complexSign.put("Paid", true);
                }
            } else {
                groupResult.resultCode = RC_CLIENT_NOT_FOUND;
                groupResult.description = RC_CLIENT_NOT_FOUND_DESC;
                return groupResult;
            }

            groupResult = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .getPreorderComplexesWithWtMenuList(client, date, categoriesDiscount, ageGroupIds, complexSign);
            transaction.commit();
            transaction = null;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            groupResult.resultCode = RC_INTERNAL_ERROR;
            groupResult.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return groupResult;
    }

    private void checkParallel(Client client, Set<CategoryDiscount> categoriesDiscount, Set<Long> ageGroupIds,
            Map<String, Boolean> complexSign) {
        String parallelDesc = client.getParallel().trim();

        // 4 Параллель для клиента-нельготника + 3 Социальная льгота
        if (client.getParallel() != null && !checkSocialDiscount(categoriesDiscount)) {
            if (PreorderDAOService.ELEMENTARY_SCHOOL.contains(parallelDesc)) {
                ageGroupIds.add(3L); // 1-4
                complexSign.put("Elem", true);
            } else if (PreorderDAOService.MIDDLE_SCHOOL.contains(parallelDesc)) {
                ageGroupIds.add(4L); // 5-11
                complexSign.put("Middle", true);
            } else {
                ageGroupIds.add(5L); // Колледж
            }
            complexSign.put("Paid", true);
        }

        // 5 Параллель для клиента-льготника + 3 Социальная льгота
        if (client.getParallel() != null && checkSocialDiscount(categoriesDiscount)) {
            if (PreorderDAOService.ELEMENTARY_SCHOOL.contains(parallelDesc)) {
                ageGroupIds.add(3L); // 1-4
                complexSign.put("Elem", true);
            } else if (PreorderDAOService.MIDDLE_SCHOOL.contains(parallelDesc)) {
                ageGroupIds.add(4L); // 5-11
                complexSign.put("Middle", true);
            } else {
                ageGroupIds.add(5L); // Колледж
            }
            complexSign.put("Paid", true);
            complexSign.put("Free", true);
        }
    }

    private boolean checkSocialDiscount(Set<CategoryDiscount> categoriesDiscount) {
        if (categoriesDiscount.size() > 0) {
            CategoryDiscount reserveDiscount = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .getReserveDiscount();
            if (categoriesDiscount.size() == 1 && categoriesDiscount.contains(reserveDiscount)) {
                return false;
            }
            for (CategoryDiscount categoryDiscount : categoriesDiscount) {
                if (categoryDiscount.getCategoryType().getValue() == 0 ||   // льготное
                        categoryDiscount.getCategoryType().getValue() == 3) {   // льготное вариативное
                    return true;
                }
            }
        }
        return false;
    }

    private int checkClientGroup(Client client) {
        if (client.getClientGroup() != null) {
            Long clientGroupId = client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup();
            if (clientGroupId.equals(ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()) || clientGroupId
                    .equals(ClientGroup.Predefined.CLIENT_EMPLOYEE.getValue()) || clientGroupId
                    .equals(ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue()) || clientGroupId
                    .equals(ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue()) || clientGroupId
                    .equals(ClientGroup.Predefined.CLIENT_VISITORS.getValue()) || clientGroupId
                    .equals(ClientGroup.Predefined.CLIENT_OTHERS.getValue())) {
                return 1;
            }
            if (clientGroupId.equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue()) || clientGroupId
                    .equals(ClientGroup.Predefined.CLIENT_DELETED.getValue()) || clientGroupId
                    .equals(ClientGroup.Predefined.CLIENT_DISPLACED.getValue())) {
                return 2;
            }
            if (clientGroupId < ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()) {
                return 3;
            }
        }
        return -1;
    }

    private void getComplexExt(Org org, Date menuDate, List<MenuItemExt> menuItemExtList, WtComplex wtComplex,
            List<MenuWithComplexesExt> list, int isDiscountComplex) {
        MenuWithComplexesExt menuWithComplexesExt = new MenuWithComplexesExt(wtComplex, org, menuDate,
                isDiscountComplex);
        menuWithComplexesExt.setMenuItemExtList(menuItemExtList);
        list.add(menuWithComplexesExt);
    }

    public boolean isGoodDate(Session session, Long idOfOrg, Long idOfGroup, Date dateReq,
            List<ProductionCalendar> productionCalendars) {
        try {

            Date currentDate = CalendarUtils.startOfDay(dateReq);

            for (ProductionCalendar productionCalendar : productionCalendars) {
                if (CalendarUtils.startOfDay(productionCalendar.getDay()).equals(currentDate)) {
                    ///
                    //Идем в ПК, дата Д есть и указано что праздник (2) - то ничего не отдаем
                    if (productionCalendar.getFlag() == 2) {
                        return false;
                    }
                    //В ПК дата Д есть и указано  что выходной (1)
                    if (productionCalendar.getFlag() == 1) {
                        Boolean complete = getDateForGroupAndOrg(session, idOfOrg, idOfGroup, dateReq);
                        if (complete != null) {
                            return complete;
                        }
                        //2.1.4 В  КУДе (cf_specialdates)  для ид орг и  ид группы (п.2.1)  отсутствует или удалены, тогда проверяем - данная дата приходится на день недели «Суббота»:,
                        if (CalendarUtils.getDayOfWeek(dateReq) == Calendar.SATURDAY) {
                            if (DAOReadonlyService.getInstance().isSixWorkWeekGroup(idOfOrg, idOfGroup)) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                        return false;
                    }
                }
            }

            Boolean complete = getDateForGroupAndOrg(session, idOfOrg, idOfGroup, dateReq);
            if (complete != null) {
                return complete;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    private Boolean getDateForGroupAndOrg(Session session, Long idOfOrg, Long idOfGroup, Date dateReq) {
        CompositeIdOfSpecialDate compositeId = new CompositeIdOfSpecialDate(idOfOrg, dateReq);
        SpecialDate specialDateGroup;
        try {
            specialDateGroup = DAOUtils.findSpecialDateWithGroup(session, compositeId, idOfGroup);
            if (specialDateGroup.getDeleted()) {
                specialDateGroup = null;
            }
        } catch (Exception e) {
            specialDateGroup = null;
        }

        if (specialDateGroup != null) {
            if (!specialDateGroup.getIsWeekend()) {
                return true;
            } else {
                return false;
            }
        }

        SpecialDate specialDateOrg;
        try {
            specialDateOrg = DAOUtils.findSpecialDate(session, compositeId);
            if (specialDateOrg.getDeleted()) {
                specialDateOrg = null;
            }
        } catch (Exception e) {
            specialDateOrg = null;
        }

        if (specialDateOrg != null) {
            if (!specialDateOrg.getIsWeekend()) {
                return true;
            } else {
                return false;
            }
        }
        return null;
    }

    @Override
    public MenuListResult getMenuListByOrg(@WebParam(name = "orgId") Long orgId, final Date startDate,
            final Date endDate) {
        authenticateRequest(null);
        MenuListResult menuListResult = new MenuListResult();
        if (CalendarUtils.addDays(startDate, 1).getTime() < endDate.getTime()) {
            menuListResult.resultCode = RC_INVALID_DATA;
            menuListResult.description = RC_INVALID_INPUT_DATA;
            return menuListResult;
        }

        Data data = new OrgRequest().process(orgId, new Processor() {
            public void process(Org org, Data data, ObjectFactory objectFactory, Session session,
                    Transaction transaction) throws Exception {
                processMenuFirstDay(org, data, objectFactory, session, startDate, endDate);
            }
        });


        if (data.getMenuListExt() != null) {
            Collections.sort(data.getMenuListExt().getM());
        }
        menuListResult.menuList = data.getMenuListExt();
        menuListResult.resultCode = data.getResultCode();
        menuListResult.description = data.getDescription();
        return menuListResult;
    }


    @Override
    public ComplexListResult getComplexList(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                if (!client.getOrg().getUseWebArm()) {
                    processComplexList(client.getOrg(), data, objectFactory, session, startDate, endDate);
                } else {
                    processWtComplexList(client.getOrg(), data, objectFactory, session, startDate, endDate);
                }
            }
        });

        ComplexListResult complexListResult = new ComplexListResult();
        complexListResult.complexDateList = data.getComplexDateList();
        complexListResult.resultCode = data.getResultCode();
        complexListResult.description = data.getDescription();
        return complexListResult;
    }

    private void processMenuFirstDayWithProhibitions(Client client, Data data, ObjectFactory objectFactory,
            Session session, Date startDate, Date endDate) throws DatatypeConfigurationException {
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        Criteria menuByDayCriteria = session.createCriteria(Menu.class);
        menuByDayCriteria.createAlias("menuDetailsInternal", "detal", JoinType.LEFT_OUTER_JOIN);
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        menuByDayCriteria.add(Restrictions.eq("org", client.getOrg()));
        menuByDayCriteria.add(Restrictions.eq("menuSource", Menu.ORG_MENU_SOURCE));
        menuByDayCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        menuByDayCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));
        menuByDayCriteria.add(Restrictions
                .or(Restrictions.not(Restrictions.ilike("detal.groupName", groupNotForMos, MatchMode.ANYWHERE)),
                        Restrictions.isNull("detal.groupName")));
        List menus = new ArrayList<>(new HashSet(menuByDayCriteria.list()));

        generateMenuDetailWithProhibitions(session, client, objectFactory, menus, data);
    }

    private void processWtMenuFirstDayWithProhibitions(Client client, Data data, ObjectFactory objectFactory,
            Session session, Date startDate, Date endDate) throws DatatypeConfigurationException {

        List<WtMenu> menus = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                .getWtMenuByDates(startDate, endDate, client.getOrg());

        generateWtMenuDetailWithProhibitions(session, client, objectFactory, menus, data, startDate, endDate);
    }

    private void generateMenuDetailWithProhibitions(Session session, Client client, ObjectFactory objectFactory,
            List menus, Data data) throws DatatypeConfigurationException {
        Map<String, Long> ProhibitByFilter = new HashMap<String, Long>();
        Map<String, Long> ProhibitByName = new HashMap<String, Long>();
        Map<String, Long> ProhibitByGroup = new HashMap<String, Long>();

        Criteria prohibitionsCriteria = session.createCriteria(ProhibitionMenu.class);
        prohibitionsCriteria.add(Restrictions.eq("client", client));
        prohibitionsCriteria.add(Restrictions.eq("deletedState", false));

        List prohibitions = prohibitionsCriteria.list();
        for (Object prohibitObj : prohibitions) {
            ProhibitionMenu prohibition = (ProhibitionMenu) prohibitObj;

            switch (prohibition.getProhibitionFilterType()) {
                case PROHIBITION_BY_FILTER:
                    ProhibitByFilter.put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
                case PROHIBITION_BY_GOODS_NAME:
                    ProhibitByName.put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
                case PROHIBITION_BY_GROUP_NAME:
                    ProhibitByGroup.put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
            }
        }
        MenuListExt menuListExt = objectFactory.createMenuListExt();
        int nRecs = 0;
        for (Object currObject : menus) {
            if (nRecs++ > MAX_RECS) {
                break;
            }

            Menu menu = (Menu) currObject;
            MenuDateItemExt menuDateItemExt = objectFactory.createMenuDateItemExt();
            menuDateItemExt.setDate(toXmlDateTime(menu.getMenuDate()));

            Criteria menuDetailCriteria = session.createCriteria(MenuDetail.class);
            menuDetailCriteria.add(Restrictions.eq("menu", menu));
            menuDetailCriteria.add(Restrictions.eq("availableNow", 1));
            menuDetailCriteria.add(Restrictions
                    .or(Restrictions.not(Restrictions.ilike("groupName", groupNotForMos, MatchMode.ANYWHERE)),
                            Restrictions.isNull("groupName")));
            HibernateUtils.addAscOrder(menuDetailCriteria, "groupName");
            HibernateUtils.addAscOrder(menuDetailCriteria, "menuDetailName");
            List menuDetails = menuDetailCriteria.list();
            for (Object o : menuDetails) {
                MenuDetail menuDetail = (MenuDetail) o;

                MenuItemExt menuItemExt = objectFactory.createMenuItemExt();
                menuItemExt.setGroup(menuDetail.getGroupName());
                menuItemExt.setName(menuDetail.getMenuDetailName());
                menuItemExt.setPrice(menuDetail.getPrice());
                menuItemExt.setCalories(menuDetail.getCalories());
                menuItemExt.setVitB1(menuDetail.getVitB1());
                menuItemExt.setVitC(menuDetail.getVitC());
                menuItemExt.setVitA(menuDetail.getVitA());
                menuItemExt.setVitE(menuDetail.getVitE());
                menuItemExt.setMinCa(menuDetail.getMinCa());
                menuItemExt.setMinP(menuDetail.getMinP());
                menuItemExt.setMinMg(menuDetail.getMinMg());
                menuItemExt.setMinFe(menuDetail.getMinFe());
                menuItemExt.setOutput(menuDetail.getMenuDetailOutput());
                menuItemExt.setAvailableNow(menuDetail.getAvailableNow());
                menuItemExt.setProtein(menuDetail.getProtein());
                menuItemExt.setCarbohydrates(menuDetail.getCarbohydrates());
                menuItemExt.setFat(menuDetail.getFat());

                if (ProhibitByGroup.containsKey(menuDetail.getGroupName())) {
                    menuItemExt.setIdOfProhibition(ProhibitByGroup.get(menuDetail.getGroupName()));
                } else {
                    if (ProhibitByName.containsKey(menuDetail.getMenuDetailName())) {
                        menuItemExt.setIdOfProhibition(ProhibitByName.get(menuDetail.getMenuDetailName()));
                    } else {
                        //пробегаться в цикле.
                        for (String filter : ProhibitByFilter.keySet()) {
                            if (menuDetail.getMenuDetailName().contains(filter)) {
                                menuItemExt.setIdOfProhibition(ProhibitByFilter.get(filter));
                            }
                        }
                    }
                }
                menuDateItemExt.getE().add(menuItemExt);
            }
            menuListExt.getM().add(menuDateItemExt);
        }
        data.setMenuListExt(menuListExt);
    }

    private void generateWtMenuDetailWithProhibitions(Session session, Client client, ObjectFactory objectFactory,
            List<WtMenu> menus, Data data, Date startDate, Date endDate) throws DatatypeConfigurationException {
        Map<String, Long> ProhibitByFilter = new HashMap<>();
        Map<String, Long> ProhibitByName = new HashMap<>();
        Map<String, Long> ProhibitByGroup = new HashMap<>();

        Criteria prohibitionsCriteria = session.createCriteria(ProhibitionMenu.class);
        prohibitionsCriteria.add(Restrictions.eq("client", client));
        prohibitionsCriteria.add(Restrictions.eq("deletedState", false));

        List prohibitions = prohibitionsCriteria.list();
        for (Object prohibitObj : prohibitions) {
            ProhibitionMenu prohibition = (ProhibitionMenu) prohibitObj;

            switch (prohibition.getProhibitionFilterType()) {
                case PROHIBITION_BY_FILTER:
                    ProhibitByFilter.put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
                case PROHIBITION_BY_GOODS_NAME:
                    ProhibitByName.put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
                case PROHIBITION_BY_GROUP_NAME:
                    ProhibitByGroup.put(prohibition.getFilterText(), prohibition.getIdOfProhibitions());
                    break;
            }
        }

        MenuListExt menuListExt = objectFactory.createMenuListExt();
        int nRecs = 0;
        for (Object currObject : menus) {
            if (nRecs++ > MAX_RECS) {
                break;
            }

            WtMenu menu = (WtMenu) currObject;
            MenuDateItemExt menuDateItemExt = objectFactory.createMenuDateItemExt();

            List<WtDishInfo> wtDishes = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .getWtDishesByMenuAndDates(menu, startDate, endDate);

            Map<Long, Set<String>> menuGroups = DAOReadonlyService.getInstance().getWtMenuGroupsForAllDishes(client.getOrg().getIdOfOrg());
            if (wtDishes != null && wtDishes.size() > 0) {
                menuDateItemExt.setDate(toXmlDateTime(startDate));
                for (WtDishInfo wtDishInfo : wtDishes) {
                    List<MenuItemExt> menuItemExtList = getMenuItemExt(objectFactory, client.getOrg().getIdOfOrg(),
                            wtDishInfo, false, menuGroups);
                    // Добавляем блокировки
                    for (MenuItemExt menuItemExt : menuItemExtList) {
                        if (ProhibitByGroup.containsKey(menuItemExt.getGroup())) {
                            menuItemExt.setIdOfProhibition(ProhibitByGroup.get(menuItemExt.getGroup()));
                        } else {
                            if (ProhibitByName.containsKey(wtDishInfo.getDishName())) {
                                menuItemExt.setIdOfProhibition(ProhibitByName.get(wtDishInfo.getDishName()));
                            } else {
                                //пробегаться в цикле.
                                for (String filter : ProhibitByFilter.keySet()) {
                                    if (wtDishInfo.getDishName().contains(filter)) {
                                        menuItemExt.setIdOfProhibition(ProhibitByFilter.get(filter));
                                    }
                                }
                            }
                        }
                        menuDateItemExt.getE().add(menuItemExt);
                    }
                }
            }
            menuListExt.getM().add(menuDateItemExt);
        }
        data.setMenuListExt(menuListExt);
    }

    private void processComplexList(Org org, Data data, ObjectFactory objectFactory, Session session, Date startDate,
            Date endDate) throws DatatypeConfigurationException {


        Criteria complexCriteria = session.createCriteria(ComplexInfo.class);
        Calendar fromCal = Calendar.getInstance(), toCal = Calendar.getInstance();
        fromCal.setTime(startDate);
        toCal.setTime(endDate);
        truncateToDayOfMonth(fromCal);
        truncateToDayOfMonth(toCal);
        fromCal.add(Calendar.HOUR, -1);
        complexCriteria.add(Restrictions.eq("org", org));

        complexCriteria.add(Restrictions.ge("menuDate", fromCal.getTime()));
        complexCriteria.add(Restrictions.lt("menuDate", toCal.getTime()));

        //complexCriteria.add(Restrictions.lt("menuDate", DateUtils.addDays(endDate, 1)));

        List<ComplexInfo> complexes = complexCriteria.list();


        ArrayList<ArrayList<ComplexInfo>> sortedComplexes = new ArrayList<ArrayList<ComplexInfo>>();

        Date currDate = null;
        ArrayList<ComplexInfo> currComplexListWithSameDate = new ArrayList<ComplexInfo>();

        for (Object complexObject : complexes) {

            ComplexInfo currComplex = (ComplexInfo) complexObject;

            if (currDate == null) {
                currComplexListWithSameDate.add(currComplex);
                currDate = currComplex.getMenuDate();
                continue;
            }

            if (currComplex.getMenuDate().equals(currDate)) {
                currComplexListWithSameDate.add(currComplex);

            } else {

                ArrayList<ComplexInfo> newComplexes = new ArrayList<ComplexInfo>();
                newComplexes.addAll(currComplexListWithSameDate);

                sortedComplexes.add(newComplexes);

                currComplexListWithSameDate = new ArrayList<ComplexInfo>();
                currComplexListWithSameDate.add(currComplex);
                currDate = currComplex.getMenuDate();

            }


        }

        sortedComplexes.add(currComplexListWithSameDate);
        currDate = null;
        ComplexDateList complexDateList = new ComplexDateList();


        for (ArrayList<ComplexInfo> complexesWithSameDate : sortedComplexes) {

            ComplexDate complexDate = new ComplexDate();

            // boolean emptyComplexDate=true;

            // ComplexInfo currComplex=complexesWithSameDate.get(0);

            //currDate=currComplex.getMenuDate();


            // for(Object complexObject:complexes){
            // ArrayList<ArrayList<ComplexInfoDetail>> complexDetailsWithSameDate =new
            // ArrayList<ArrayList<ComplexInfoDetail>>();

            for (ComplexInfo complexInfo : complexesWithSameDate) {

                Complex complex = new Complex();

                Criteria complexDetailsCriteria = session.createCriteria(ComplexInfoDetail.class);
                complexDetailsCriteria.add(Restrictions.eq("complexInfo", complexInfo));


                List<ComplexInfoDetail> complexDetails = complexDetailsCriteria.list();

                if (!complexDetails.isEmpty()) {

                    for (ComplexInfoDetail complexInfoDetail : complexDetails) {
                        ComplexDetail complexDetail = new ComplexDetail();
                        complexDetail.setName(complexInfoDetail.getMenuDetail().getMenuDetailName());
                        complex.getE().add(complexDetail);
                        complex.setName(complexInfoDetail.getComplexInfo().getComplexName());
                    }

                    complexDate.getE().add(complex);
                    complexDate.setDate(toXmlDateTime(complexInfo.getMenuDate()));


                    // emptyComplexDate=false;
                    logger.info("complexName: " + complexInfo.getComplexName());

                    //  ArrayList<ComplexInfoDetail>complexDetailList=new ArrayList<ComplexInfoDetail>();
                    // complexDetailList.addAll(complexDetails);
                    // complexDetailsWithSameDate.add(complexDetailList);

                }


            }

            if (!complexDate.getE().isEmpty()) {

                complexDateList.getE().add(complexDate);
            }

        }


        data.setComplexDateList(complexDateList);

    }

    private void processWtComplexList(Org org, Data data, ObjectFactory objectFactory, Session session, Date startDate,
            Date endDate) throws DatatypeConfigurationException {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        Date menuDate = calendar.getTime();

        ComplexDateList complexDateList = new ComplexDateList();

        while (menuDate.getTime() < endDate.getTime()) {

            // Находим комлексы
            List<WtComplex> complexes = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .getWtComplexesByDates(menuDate, menuDate, org);

            ComplexDate complexDate = new ComplexDate();

            for (WtComplex wtComplex : complexes) {
                Complex complex = new Complex();

                // Определяем подходящий состав комплекса
                WtComplexesItem complexItem = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                        .getWtComplexItemByCycle(wtComplex, menuDate);
                List<WtDish> dishes;
                if (complexItem != null) {
                    dishes = DAOReadExternalsService.getInstance()
                            .getWtDishesByComplexItemAndDates(complexItem, menuDate, menuDate);
                } else {
                    // комплекс не выводим
                    continue;
                }

                //List<WtDish> dishes = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                //        .getWtDishesByComplexAndDates(wtComplex, menuDate, menuDate);

                if (!dishes.isEmpty()) {
                    for (WtDish dish : dishes) {
                        ComplexDetail complexDetail = new ComplexDetail();
                        complexDetail.setName(dish.getDishName());
                        complex.getE().add(complexDetail);
                    }
                    complex.setName(wtComplex.getWtDietType().getDescription());
                    complexDate.getE().add(complex);
                    complexDate.setDate(toXmlDateTime(menuDate));

                    logger.info("complexName: " + wtComplex.getName());
                }
            }
            if (!complexDate.getE().isEmpty()) {
                complexDateList.getE().add(complexDate);
            }
            calendar.add(Calendar.DATE, 1);
            menuDate = calendar.getTime();
        }
        data.setComplexDateList(complexDateList);
    }

    @Override
    public CardListResult getCardList(Long contractId) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processCardList(client, data, objectFactory);
            }
        });

        CardListResult cardListResult = new CardListResult();
        cardListResult.cardList = data.getCardList();
        cardListResult.resultCode = data.getResultCode();
        cardListResult.description = data.getDescription();
        return cardListResult;
    }

    @Override
    public CardListResult getCardList(String san) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                            Session session, Transaction transaction) throws Exception {
                        processCardList(client, data, objectFactory);
                    }
                }, null);

        CardListResult cardListResult = new CardListResult();
        cardListResult.cardList = data.getCardList();
        cardListResult.resultCode = data.getResultCode();
        cardListResult.description = data.getDescription();
        return cardListResult;
    }

    private void processCardList(Client client, Data data, ObjectFactory objectFactory)
            throws DatatypeConfigurationException {
        Set<Card> cardSet = client.getCards();
        CardList cardList = objectFactory.createCardList();
        for (Card card : cardSet) {
            CardItem cardItem = objectFactory.createCardItem();
            cardItem.setState(card.getState());
            cardItem.setType(card.getCardType());
            cardItem.setChangeDate(toXmlDateTime(card.getUpdateTime()));
            cardItem.setCrystalId(card.getCardNo());
            cardItem.setIdOfCard(card.getIdOfCard());
            cardItem.setLifeState(card.getLifeState());
            cardItem.setExpiryDate(toXmlDateTime(card.getValidTime()));

            cardList.getC().add(cardItem);
        }
        data.setCardList(cardList);
    }

    @Override
    public DataAllEvents getEnterEventList(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);
        DataAllEvents dataAllEvents = new DataAllEvents();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createExternalServicesPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            List<Client> clients = clientCriteria.list();

            if (clients.isEmpty()) {
                dataAllEvents.setResultCode(RC_CLIENT_NOT_FOUND);
                dataAllEvents.setDescription(RC_CLIENT_NOT_FOUND_DESC);
            } else if (clients.size() > 1) {
                dataAllEvents.setResultCode(RC_SEVERAL_CLIENTS_WERE_FOUND);
                dataAllEvents.setDescription(RC_SEVERAL_CLIENTS_WERE_FOUND_DESC);
            } else {
                Client client = clients.get(0);
                dataAllEvents.setEnterEventList(processAllEventList(client, persistenceSession, endDate, startDate));
                dataAllEvents.setResultCode(RC_OK);
                dataAllEvents.setDescription(RC_OK_DESC);
            }

        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            dataAllEvents.setResultCode(RC_INTERNAL_ERROR);
            dataAllEvents.setDescription(e.toString());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return dataAllEvents;
    }

    @Override
    public EnterEventListResult getNEnterEventList(@WebParam(name = "orgId") long orgId,
            @WebParam(name = "minDate") final Date minDate, @WebParam(name = "maxDate") final Date maxDate,
            @WebParam(name = "N") final int n) {
        Data data = null;
        EnterEventListResult enterEventListResult = new EnterEventListResult();
        try {
            data = processNEnterEventList(orgId, minDate, maxDate, n);
            enterEventListResult.enterEventList = data.getEnterEventList();
            enterEventListResult.resultCode = data.getResultCode();
            enterEventListResult.description = data.getDescription();
        } catch (Exception e) {
            e.printStackTrace();
            enterEventListResult.resultCode = RC_INTERNAL_ERROR;
            enterEventListResult.description = e.getMessage().toString();
        }
        return enterEventListResult;
    }

    @Override
    public EnterEventWithRepListResult getEnterEventWithRepList(Long contractId, final Date startDate,
            final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processEnterEventWithRepList(client, data, objectFactory, session, endDate, startDate, false);
            }
        });

        EnterEventWithRepListResult enterEventWithRepListResult = new EnterEventWithRepListResult();
        enterEventWithRepListResult.enterEventWithRepList = data.getEnterEventWithRepList();
        enterEventWithRepListResult.resultCode = data.getResultCode();
        enterEventWithRepListResult.description = data.getDescription();
        return enterEventWithRepListResult;
    }

    @Override
    public EnterEventListResult getEnterEventListByGuardian(Long contractId, final Date startDate, final Date endDate) {
        authenticateRequest(contractId);
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processEnterEventList(client, data, objectFactory, session, endDate, startDate, true);
            }
        });
        EnterEventListResult enterEventListResult = new EnterEventListResult();
        enterEventListResult.enterEventList = data.getEnterEventList();
        enterEventListResult.resultCode = data.getResultCode();
        enterEventListResult.description = data.getDescription();
        return enterEventListResult;
    }

    @Override
    public EnterEventListResult getEnterEventList(String san, final Date startDate, final Date endDate) {
        authenticateRequest(null);

        Data data = new ClientRequest()
                .process(san, ClientRoomControllerWS.ClientRequest.CLIENT_ID_SAN, new Processor() {
                    public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                            Session session, Transaction transaction) throws Exception {
                        processEnterEventList(client, data, objectFactory, session, endDate, startDate, false);
                    }
                }, null);

        EnterEventListResult enterEventListResult = new EnterEventListResult();
        enterEventListResult.enterEventList = data.getEnterEventList();
        enterEventListResult.resultCode = data.getResultCode();
        enterEventListResult.description = data.getDescription();
        return enterEventListResult;
    }

    private void processEnterEventWithRepList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate, boolean byGuardian) throws Exception {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        Criteria enterEventWithRepCriteria = session.createCriteria(EnterEvent.class);
        enterEventWithRepCriteria.add(byGuardian ? Restrictions.eq("guardianId", client.getIdOfClient())
                : Restrictions.eq("client", client));
        enterEventWithRepCriteria.add(Restrictions.ge("evtDateTime", startDate));
        enterEventWithRepCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        enterEventWithRepCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));

        Locale locale = new Locale("ru", "RU");
        Calendar calendar = Calendar.getInstance(locale);

        List<EnterEvent> enterEvents = enterEventWithRepCriteria.list();
        EnterEventWithRepList enterEventWithRepList = objectFactory.createEnterEventWithRepList();
        int nRecs = 0;
        Map<Long, Client> guardianMap = new HashMap<Long, Client>();
        for (EnterEvent enterEvent : enterEvents) {
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            EnterEventWithRepItem enterEventWithRepItem = objectFactory.createEnterEventWithRepItem();
            enterEventWithRepItem.setDateTime(toXmlDateTime(enterEvent.getEvtDateTime()));
            calendar.setTime(enterEvent.getEvtDateTime());
            enterEventWithRepItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventWithRepItem.setEnterName(enterEvent.getEnterName());
            enterEventWithRepItem.setDirection(enterEvent.getPassDirection());
            enterEventWithRepItem.setTemporaryCard(enterEvent.getIdOfTempCard() != null ? 1 : 0);
            enterEventWithRepItem.setLastUpdateDate(toXmlDateTime(OrgRepository.getInstance()
                    .getLastProcessSectionsDate(enterEvent.getOrg().getIdOfOrg(), SectionType.ENTER_EVENTS)));

            final Long guardianId = enterEvent.getGuardianId();
            if (guardianId != null) {
                Client guardian = guardianMap.get(guardianId);
                if (guardian == null) {
                    guardian = DAOUtils.findClient(session, guardianId);
                    guardianMap.put(guardianId, guardian);
                }
                enterEventWithRepItem.setRepId(guardian.getContractId());
                enterEventWithRepItem.setRepName(guardian.getPerson().getFullName());
                //enterEventItem.setGuardianSan(guardian.getSan());
                enterEventWithRepItem.setGuardianSan(DAOUtils.extractSanFromClient(session, guardianId));
            }
            final Long checkerId = enterEvent.getChildPassCheckerId();
            if (checkerId != null) {
                Client checker = DAOUtils.findClient(session, checkerId);
                enterEventWithRepItem.setChildPassCheckerContractId(checker.getContractId());
                enterEventWithRepItem.setChildPassChecker(checker.getPerson().getFullName());
            }
            enterEventWithRepItem.setChildPassCheckerMethod(enterEvent.getChildPassChecker());
            enterEventWithRepList.getE().add(enterEventWithRepItem);
        }
        data.setEnterEventWithRepList(enterEventWithRepList);
    }

    private AllEventList processAllEventList(Client client, Session session,
            Date endDate, Date startDate) throws Exception {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        /*Запрос на получение данных из таблицы EnterEvents*/
        Criteria enterEventCriteria = session.createCriteria(EnterEvent.class);
        enterEventCriteria.add(Restrictions.eq("client", client));
        enterEventCriteria.add(Restrictions.ge("evtDateTime", startDate));
        enterEventCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        enterEventCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));
        List<EnterEvent> enterEvents = enterEventCriteria.list();
        /* -- Запрос на получение данных из таблицы EnterEvents -- */

        /*Запрос на получение данных из таблицы EnterEventsManual*/
        Criteria manualEventCriteria = session.createCriteria(EnterEventManual.class);
        manualEventCriteria.add(Restrictions.eq("idOfClient", client.getIdOfClient()));
        manualEventCriteria.add(Restrictions.ge("evtDateTime", startDate));
        manualEventCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        manualEventCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));
        List<EnterEventManual> manualEvents = manualEventCriteria.list();
        /* -- Запрос на получение данных из таблицы EnterEventsManual -- */

        /*Запрос на получение данных из таблицы ExternalEventsManual*/
        List<ExternalEventType> externalType = new ArrayList();
        externalType.add(ExternalEventType.CULTURE);
        externalType.add(ExternalEventType.MUSEUM);
        externalType.add(ExternalEventType.LIBRARY);
        Criteria externalEventCriteria = session.createCriteria(ExternalEvent.class);
        externalEventCriteria.add(Restrictions.eq("client", client));
        externalEventCriteria.add(Restrictions.ge("evtDateTime", startDate));
        externalEventCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        externalEventCriteria.add(Restrictions.in("evtType", externalType));
        externalEventCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));
        List<ExternalEvent> externalEvents = externalEventCriteria.list();
        /* -- Запрос на получение данных из таблицы ExternalEventsManual -- */

        Locale locale = new Locale("ru", "RU");
        Calendar calendar = Calendar.getInstance(locale);

        AllEventList enterEventList = new AllEventList();
        int nRecs = 0;

        for (EnterEvent enterEvent : enterEvents) {
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            AllEventItem enterEventItem = new AllEventItem();
            enterEventItem.setDateTime(toXmlDateTime(enterEvent.getEvtDateTime()));
            calendar.setTime(enterEvent.getEvtDateTime());
            enterEventItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventItem.setDirection(enterEvent.getPassDirection());
            enterEventItem.setAddress(enterEvent.getOrg().getAddress());
            enterEventItem.setShortNameInfoService(enterEvent.getOrg().getShortNameInfoService());
            enterEventItem.setChildPassCheckerMethod(enterEvent.getChildPassChecker());
            final Long checkerId = enterEvent.getChildPassCheckerId();
            if (checkerId != null) {
                Client checker = DAOUtils.findClient(session, checkerId);
                enterEventItem.setChildPassChecker(checker.getPerson().getFullName());
            }
            enterEventList.getE().add(enterEventItem);
        }

        for (EnterEventManual manualEvent : manualEvents) {
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            AllEventItem enterEventItem = new AllEventItem();
            enterEventItem.setDateTime(toXmlDateTime(manualEvent.getEvtDateTime()));
            calendar.setTime(manualEvent.getEvtDateTime());
            enterEventItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventItem.setDirection(EnterEvent.CHECKED_BY_TEACHER_EXT);
            Org org = (Org) session.load(Org.class, manualEvent.getIdOfOrg());
            enterEventItem.setAddress(org.getAddress());
            enterEventItem.setShortNameInfoService(org.getShortNameInfoService());
            enterEventList.getE().add(enterEventItem);
        }
        for (ExternalEvent externalEvent : externalEvents) {
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            AllEventItem enterEventItem = new AllEventItem();
            enterEventItem.setDateTime(toXmlDateTime(externalEvent.getEvtDateTime()));
            calendar.setTime(externalEvent.getEvtDateTime());
            enterEventItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));

            if (externalEvent.getEvtType().equals(ExternalEventType.MUSEUM)
                    && externalEvent.getEvtStatus().equals(ExternalEventStatus.TICKET_GIVEN)) {
                enterEventItem.setDirection(1000);
                enterEventItem.setDirectionText("выдан билет в музее");
            }
            if (externalEvent.getEvtType().equals(ExternalEventType.MUSEUM)
                    && externalEvent.getEvtStatus().equals(ExternalEventStatus.TICKET_BACK)) {
                enterEventItem.setDirection(1001);
                enterEventItem.setDirectionText("возврат билета в музей");
            }
            if (externalEvent.getEvtType().equals(ExternalEventType.CULTURE)
                    && externalEvent.getEvtStatus().equals(ExternalEventStatus.TICKET_GIVEN)) {
                enterEventItem.setDirection(2000);
                enterEventItem.setDirectionText("вход в здание учреждения культуры");
            }
            if (externalEvent.getEvtType().equals(ExternalEventType.CULTURE)
                    && externalEvent.getEvtStatus().equals(ExternalEventStatus.TICKET_BACK)) {
                enterEventItem.setDirection(2001);
                enterEventItem.setDirectionText("выход из здания учреждения культуры");
            }
            if (externalEvent.getEvtType().equals(ExternalEventType.LIBRARY)) {
                enterEventItem.setDirection(3000);
                enterEventItem.setDirectionText("вход в библтотеку");
            }
            enterEventItem.setAddress(externalEvent.getAddress());
            enterEventItem.setShortNameInfoService(externalEvent.getOrgShortName());
            enterEventList.getE().add(enterEventItem);
        }
        return enterEventList;
    }

    private void processEnterEventList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date endDate, Date startDate, boolean byGuardian) throws Exception {
        Date nextToEndDate = DateUtils.addDays(endDate, 1);
        /*Запрос на получение данных из таблицы EnterEvents*/
        Criteria enterEventCriteria = session.createCriteria(EnterEvent.class);
        enterEventCriteria.add(byGuardian ? Restrictions.eq("guardianId", client.getIdOfClient())
                : Restrictions.eq("client", client));
        enterEventCriteria.add(Restrictions.ge("evtDateTime", startDate));
        enterEventCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        enterEventCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));
        /* -- Запрос на получение данных из таблицы EnterEvents -- */

        /*Запрос на получение данных из таблицы EnterEventsManual*/
        Criteria manualEventCriteria = session.createCriteria(EnterEventManual.class);
        manualEventCriteria.add(Restrictions.eq("idOfClient", client.getIdOfClient()));
        manualEventCriteria.add(Restrictions.ge("evtDateTime", startDate));
        manualEventCriteria.add(Restrictions.lt("evtDateTime", nextToEndDate));
        manualEventCriteria.addOrder(org.hibernate.criterion.Order.asc("evtDateTime"));
        List<EnterEventManual> manualEvents = manualEventCriteria.list();
        /* -- Запрос на получение данных из таблицы EnterEventsManual -- */

        Locale locale = new Locale("ru", "RU");
        Calendar calendar = Calendar.getInstance(locale);

        List<EnterEvent> enterEvents = enterEventCriteria.list();
        EnterEventList enterEventList = objectFactory.createEnterEventList();
        int nRecs = 0;

        Map<Long, Date> lastUpdatePrecessSectionDate = new HashMap<Long, Date>();
        Long localIdOfOrg;
        Date localDate;
        Map<Long, String> guardianSan = new HashMap<Long, String>();
        Long localGuardianId;
        String localSan;
        for (EnterEvent enterEvent : enterEvents) {
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            localIdOfOrg = enterEvent.getOrg().getIdOfOrg();
            localDate = lastUpdatePrecessSectionDate.get(localIdOfOrg);
            if (localDate == null) {
                lastUpdatePrecessSectionDate.put(localIdOfOrg,
                        OrgRepository.getInstance().getLastProcessSectionsDate(localIdOfOrg, SectionType.ENTER_EVENTS));
            }
            localGuardianId = enterEvent.getGuardianId();
            if (localGuardianId != null) {
                localSan = guardianSan.get(localGuardianId);
                if (localSan == null) {
                    guardianSan.put(localGuardianId, DAOUtils.extractSanFromClient(session, localGuardianId));
                }
            }
        }

        for (EnterEvent enterEvent : enterEvents) {
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            EnterEventItem enterEventItem = objectFactory.createEnterEventItem();
            enterEventItem.setDateTime(toXmlDateTime(enterEvent.getEvtDateTime()));
            calendar.setTime(enterEvent.getEvtDateTime());
            enterEventItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventItem.setEnterName(enterEvent.getEnterName());
            enterEventItem.setDirection(enterEvent.getPassDirection());
            enterEventItem.setTemporaryCard(enterEvent.getIdOfTempCard() != null ? 1 : 0);
            enterEventItem.setLastUpdateDate(
                    toXmlDateTime(lastUpdatePrecessSectionDate.get(enterEvent.getOrg().getIdOfOrg())));
            enterEventItem.setAddress(enterEvent.getOrg().getAddress());
            enterEventItem.setShortNameInfoService(enterEvent.getOrg().getShortNameInfoService());
            final Long guardianId = enterEvent.getGuardianId();
            if (guardianId != null) {
                enterEventItem.setGuardianSan(guardianSan.get(guardianId));
            }
            enterEventItem.setChildPassCheckerMethod(enterEvent.getChildPassChecker());
            final Long checkerId = enterEvent.getChildPassCheckerId();
            if (checkerId != null) {
                Client checker = DAOUtils.findClient(session, checkerId);
                enterEventItem.setChildPassChecker(checker.getPerson().getFullName());
            }
            enterEventList.getE().add(enterEventItem);
        }

        for (EnterEventManual manualEvent : manualEvents) {
            if (nRecs++ > MAX_RECS_getEventsList) {
                break;
            }
            EnterEventItem enterEventItem = objectFactory.createEnterEventItem();
            enterEventItem.setDateTime(toXmlDateTime(manualEvent.getEvtDateTime()));
            calendar.setTime(manualEvent.getEvtDateTime());
            enterEventItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventItem.setEnterName(manualEvent.getEnterName());
            enterEventItem.setDirection(EnterEvent.CHECKED_BY_TEACHER_EXT);
            enterEventItem.setTemporaryCard(0);
            enterEventItem.setLastUpdateDate(toXmlDateTime(OrgRepository.getInstance()
                    .getLastProcessSectionsDate(manualEvent.getIdOfOrg(), SectionType.ENTER_EVENTS)));
            Org org = (Org) session.load(Org.class, manualEvent.getIdOfOrg());
            enterEventItem.setAddress(org.getAddress());
            enterEventItem.setShortNameInfoService(org.getShortNameInfoService());
            enterEventList.getE().add(enterEventItem);
        }
        data.setEnterEventList(enterEventList);
    }

    private Data processNEnterEventList(long orgId, Date minDate, Date maxDate, int n) throws Exception {

        Data data = new Data();
        data.setResultCode(RC_OK);
        data.setDescription(RC_OK_DESC);

        List<EnterEvent> lastNEnterEvent = EnterEventsRepository.getInstance()
                .findLastNEnterEvent(orgId, minDate, maxDate, n);

        Locale locale = new Locale("ru", "RU");
        Calendar calendar = Calendar.getInstance(locale);

        EnterEventList enterEventList = new EnterEventList();
        for (EnterEvent enterEvent : lastNEnterEvent) {
            EnterEventItem enterEventItem = new EnterEventItem();
            enterEventItem.setDateTime(toXmlDateTime(enterEvent.getEvtDateTime()));
            calendar.setTime(enterEvent.getEvtDateTime());
            enterEventItem.setDay(translateDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
            enterEventItem.setEnterName(enterEvent.getEnterName());
            enterEventItem.setDirection(enterEvent.getPassDirection());
            enterEventItem.setTemporaryCard(enterEvent.getIdOfTempCard() != null ? 1 : 0);
            enterEventItem.setPassWithGuardian(enterEvent.getGuardianId());
            enterEventItem.setLastUpdateDate(toXmlDateTime(OrgRepository.getInstance()
                    .getLastProcessSectionsDate(enterEvent.getOrg().getIdOfOrg(), SectionType.ENTER_EVENTS)));
            final Long guardianId = enterEvent.getGuardianId();
            if (guardianId != null) {
                enterEventItem.setGuardianSan(ClientDao.getInstance().extractSanFromClient(guardianId));
            }
            enterEventList.getE().add(enterEventItem);

            if (enterEvent.getClient() != null) {
                enterEventItem.setIdOfClient(enterEvent.getClient().getIdOfClient());
            }
            enterEventItem.setIdOfCard(enterEvent.getIdOfCard());
            enterEventItem.setTurnstileAddr(enterEvent.getTurnstileAddr());
            enterEventItem.setVisitorFullName(enterEvent.getVisitorFullName());
        }
        data.setEnterEventList(enterEventList);

        return data;
    }

    private Integer translateDayOfWeek(int i) {
        if (i == Calendar.MONDAY) {
            return 0;
        } else if (i == Calendar.TUESDAY) {
            return 1;
        } else if (i == Calendar.WEDNESDAY) {
            return 2;
        } else if (i == Calendar.THURSDAY) {
            return 3;
        } else if (i == Calendar.FRIDAY) {
            return 4;
        } else if (i == Calendar.SATURDAY) {
            return 5;
        } else if (i == Calendar.SUNDAY) {
            return 6;
        }
        return -1;
    }

    public ClientsWithResultCode getClientsByGuardMobile(String mobile, Session session) {

        ClientsWithResultCode data = new ClientsWithResultCode();
        try {
            Map<Client, ClientWithAddInfo> clients = extractClientsFromGuardByGuardMobile(
                    Client.checkAndConvertMobile(mobile), session);
            if (clients.isEmpty()) {
                data.resultCode = RC_CLIENT_NOT_FOUND;
                data.description = "Клиент не найден";
            } else {
                boolean onlyNotActiveCG = true;
                for (Map.Entry<Client, ClientWithAddInfo> entry : clients.entrySet()) {
                    if (entry.getValue() != null) {
                        onlyNotActiveCG = false;
                        break;
                    }
                }
                if (onlyNotActiveCG) {
                    data.resultCode = RC_CLIENT_NOT_FOUND;
                    data.description = "Связка не активна";
                } else {
                    data.setClients(clients);
                    data.resultCode = RC_OK;
                    data.description = "OK";
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            data.resultCode = RC_INTERNAL_ERROR;
            data.description = e.toString();
        }
        return data;
    }

    public Map<Client, ClientWithAddInfo> extractClientsFromGuardByGuardMobile(String guardMobile, Session session)
            throws Exception {
        Map<Client, ClientWithAddInfo> result = new HashMap<Client, ClientWithAddInfo>();
        String query =
                "select client.idOfClient from cf_clients client where (client.phone=:guardMobile or client.mobile=:guardMobile) "
                        + "and client.IdOfClientGroup not in (:leaving, :deleted)"; //все клиенты с номером телефона
        Query q = session.createSQLQuery(query);
        q.setParameter("guardMobile", guardMobile);
        q.setParameter("leaving", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        q.setParameter("deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
        List<BigInteger> clients = q.list();

        if (clients != null && !clients.isEmpty()) {
            for (BigInteger id : clients) {
                Long londId = id.longValue();
                Query q2 = session.createQuery("select c, cg from ClientGuardian cg, Client c "
                        + "where cg.idOfChildren = c.idOfClient and cg.idOfGuardian = :idOfGuardian "
                        + "and cg.deletedState = false");  //все дети текущего клиента
                q2.setParameter("idOfGuardian", londId);
                List list = q2.list();
                if (list != null && list.size() > 0) {
                    for (Object o : list) {
                        Object[] row = (Object[]) o;
                        if (result.get(row[0]) == null || (result.get(row[0]) != null && result.get(row[0])
                                .isDisabled())) {
                            //если по клиенту инфы еще нет то добавляем, или инфа уже есть, но связка выключена, то обновляем инфу
                            Client child = (Client) row[0];
                            ClientGuardian cg = (ClientGuardian) row[1];
                            ClientWithAddInfo addInfo = new ClientWithAddInfo();
                            addInfo.setInformedSpecialMenu(ClientManager
                                    .getInformedSpecialMenu(session, child.getIdOfClient(), cg.getIdOfGuardian()) ? 1
                                    : null);
                            addInfo.setPreorderAllowed(ClientManager
                                    .getAllowedPreorderByClient(session, child.getIdOfClient(), cg.getIdOfGuardian())
                                    ? 1 : null);
                            addInfo.setClientCreatedFrom(cg.isDisabled() ? null : cg.getCreatedFrom());
                            addInfo.setDisabled(cg.isDisabled());
                            if (cg.getRepresentType() == null) {
                                addInfo.setRepresentType(ClientGuardianRepresentType.UNKNOWN);
                            } else {
                                addInfo.setRepresentType(cg.getRepresentType());
                            }
                            result.put(child, addInfo);
                        }
                    }
                } else {
                    ClientWithAddInfo addInfo = new ClientWithAddInfo();
                    addInfo.setInformedSpecialMenu(null);
                    addInfo.setClientCreatedFrom(ClientCreatedFromType.DEFAULT);
                    addInfo.setRepresentType(ClientGuardianRepresentType.UNKNOWN);
                    result.put(DAOUtils.findClient(session, londId), addInfo);
                }
            }
        }

        return result;
    }

    public List<Client> findListOfClientsByListOfIds(List<Long> idsOfClient, Session session) throws Exception {
        try {
            Query query = session.createQuery("from Client c where c.idOfClient in (:list)");
            query.setParameterList("list", idsOfClient);
            return query.list();
        } catch (Exception e) {
            return null;
        }
    }

    XMLGregorianCalendar toXmlDateTime(Date date) throws DatatypeConfigurationException {
        if (date == null) {
            return null;
        }
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar xc = DatatypeFactory.newInstance()
                .newXMLGregorianCalendarDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1,
                        c.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        xc.setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        return xc;
    }

    @Override
    public Long getContractIdByCardNo(@WebParam(name = "cardId") String cardId, @WebParam(name = "mode") int mode) {
        authenticateRequest(null);

        long lCardId = Long.parseLong(cardId);
        Long contractId = null;
        try {
            if (mode == 0) {
                contractId = getContractIdByCardNoInternal_OLDWAY(lCardId);
            }
            if (mode == 1) {
                contractId = getContractIdByCardNoInternal_NEWWAY(lCardId);
            }
        } catch (NoUniqueCardNoException e) {
            logger.error("getContractIdByCardNo NoUniqueCardNoException");
        } catch (Exception e) {
            logger.error("getContractIdByCardNo failed", e);
        }
        return contractId;
    }

    private Long getContractIdByCardNoInternal_OLDWAY(long cardId) throws Exception {
        Long contractId = DAOReadonlyService.getInstance().getContractIdByCardNo(cardId);
        if (contractId == null) {
            int days = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_TEMP_CARD_VALID_DAYS);
            contractId = DAOReadonlyService.getInstance().getContractIdByTempCardNoAndCheckValidDate(cardId, days);
        }
        return contractId;
    }

    private Long getContractIdByCardNoInternal_NEWWAY(long cardId) throws Exception {
        Long contractId = null;
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            Card card = DAOUtils.findCardByCardNoWithUniqueCheck(session, cardId);
            if (card != null && card.getState() == Card.ACTIVE_STATE) {
                contractId = card.getClient().getContractId();
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return contractId;
    }

    @Override
    public ClientSummaryExtListResult getSummaryByGuardMobile(String guardMobile) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(null, handler);
        Date date = new Date(System.currentTimeMillis());

        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("soap метод getSummaryByGuardMobile");
        clientsMobileHistory.setShowing("Портал");

        changeSsoid(guardMobile, clientsMobileHistory);

        Session session = null;
        try {
            LinkedList<ClientSummaryExt> clientSummaries = new LinkedList<ClientSummaryExt>();
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            ClientsWithResultCode cd = getClientsByGuardMobile(guardMobile, session);

            if (cd != null && cd.getClients() != null) {
                for (Map.Entry<Client, ClientWithAddInfo> entry : cd.getClients().entrySet()) {
                    if (entry.getValue().isDisabled()) {
                        continue;
                    }
                    Data dataProcess = new ClientRequest().process(entry.getKey(), session, new Processor() {
                        public void process(Client client, Data dataProcess, ObjectFactory objectFactory,
                                Session session) throws Exception {
                            processSummary(client, dataProcess, objectFactory, session);
                        }
                    });
                    ClientSummaryResult cs = new ClientSummaryResult();
                    if (!entry.getValue().equals(ClientCreatedFromType.DEFAULT)) {
                        dataProcess.getClientSummaryExt()
                                .setGuardianCreatedWhere(entry.getValue().getClientCreatedFrom().getValue());
                    }
                    dataProcess.getClientSummaryExt()
                            .setRoleRepresentative(entry.getValue().getRepresentType().getCode());
                    //////////////////////
                    try {
                        Integer temp = dataProcess.getClientSummaryExt().getRoleRepresentative();
                        temp = temp - 1;
                        if (temp == -1) {
                            temp = 2;
                        }
                        dataProcess.getClientSummaryExt().setRoleRepresentative(temp);
                    } catch (Exception e){}
                    /////////////////////
                    cs.clientSummary = dataProcess.getClientSummaryExt();
                    cs.resultCode = dataProcess.getResultCode();
                    cs.description = dataProcess.getDescription();
                    if (cs.clientSummary != null) {
                        clientSummaries.add(cs.clientSummary);
                        handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date,
                                handler.getData().getSsoId(), entry.getKey().getIdOfClient(),
                                handler.getData().getOperationType());
                    }
                }
            }

            ClientSummaryExtListResult clientSummaryExtListResult = new ClientSummaryExtListResult();
            clientSummaryExtListResult.clientSummary = clientSummaries;
            clientSummaryExtListResult.resultCode = cd.resultCode;
            clientSummaryExtListResult.description = cd.description;

            return clientSummaryExtListResult;
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public ClientRepresentativesResult getClientRepresentatives(String contractId) {
        Long contractIdLong = Long.valueOf(contractId);
        authenticateRequest(contractIdLong);

        Data data = new ClientRequest().process(contractIdLong, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processClientRepresentativeList(client, data, objectFactory, session);
            }
        });
        //////////////////////
        if (data.getClientRepresentativesList() != null ) {
            for (ClientRepresentative clientRepresentative : data.getClientRepresentativesList().getRep()) {
                if (clientRepresentative.getRoleRepresentative() != null) {
                    Integer temp = clientRepresentative.getRoleRepresentative();
                    temp = temp - 1;
                    if (temp == -1) {
                        temp = 2;
                    }
                    clientRepresentative.setRoleRepresentative(temp);
                }
            }
        }
        /////////////////////
        ClientRepresentativesResult clientRepresentativesResult = new ClientRepresentativesResult();
        clientRepresentativesResult.clientRepresentativesList = data.getClientRepresentativesList();
        clientRepresentativesResult.resultCode = data.getResultCode();
        clientRepresentativesResult.description = data.getDescription();

        return clientRepresentativesResult;
    }

    private void processClientRepresentativeList(Client client, Data data, ObjectFactory objectFactory,
            Session session) {
        try {
            Criteria criteria = session.createCriteria(ClientGuardian.class);
            criteria.add(Restrictions.eq("idOfChildren", client.getIdOfClient()));
            criteria.add(Restrictions.eq("disabled", false));
            criteria.add(Restrictions.eq("deletedState", false));
            List guardiansResults = criteria.list();

            ClientRepresentativesList clientRepresentativesList = new ClientRepresentativesList();

            for (Object o : guardiansResults) {
                ClientGuardian clientGuardian = (ClientGuardian) o;
                Client cl = DAOUtils.findClient(session, clientGuardian.getIdOfGuardian());
                if ((cl != null) && (!cl.isDontShowToExternal())) {
                    ClientRepresentative clientRepresentative = objectFactory.creteClientRepresentative();
                    clientRepresentative.setId(cl.getContractId());
                    clientRepresentative.setName(cl.getPerson().getFullName());
                    clientRepresentative.setEmail(cl.getEmail());
                    clientRepresentative.setMobile(cl.getMobile());
                    clientRepresentative.setNotifyviaemail(cl.isNotifyViaEmail());
                    clientRepresentative.setNotifyviapush(cl.isNotifyViaPUSH());
                    if (clientGuardian.getRepresentType() == null) {
                        clientRepresentative.setRoleRepresentative(ClientGuardianRepresentType.UNKNOWN.getCode());
                    } else {
                        clientRepresentative.setRoleRepresentative(clientGuardian.getRepresentType().getCode());
                    }
                    if (!clientGuardian.getCreatedFrom().equals(ClientCreatedFromType.DEFAULT)) {
                        clientRepresentative.setCreatedWhere(clientGuardian.getCreatedFrom().getValue());
                        clientRepresentative.setIdOfOrg(cl.getOrg().getIdOfOrg());
                        clientRepresentative.setOrgShortName(cl.getOrg().getShortName());
                    }

                    clientRepresentativesList.getRep().add(clientRepresentative);
                }
            }
            data.setClientRepresentativesList(clientRepresentativesList);
            data.setResultCode(RC_OK);
            data.setDescription(RC_OK_DESC);
        } catch (Exception e) {
            e.printStackTrace();
            data.setResultCode(RC_INTERNAL_ERROR);
            data.setDescription(RC_INTERNAL_ERROR_DESC);
        }
    }

    @Override
    public Result enableNotificationBySMS(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "state") boolean state) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        List<Long> contracts = getContracts(contractId, handler);
        if (!DAOService.getInstance().enableClientNotificationBySMS(contracts, state)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        } else {
            EMPProcessor processor = RuntimeContext.getAppContext().getBean(EMPProcessor.class);
            for (Long cId : contracts) {
                processor.updateNotificationParams(cId);
            }
        }
        return r;
    }

    private List<Long> getContracts(Long contractId, HTTPDataHandler handler) {
        List<Long> contracts = new ArrayList<Long>();
        contracts.add(contractId);
        try {
            String ssoid = handler.getData().getSsoId();
            List<Long> contractIds = DAOReadonlyService.getInstance().findContractsBySsoid(ssoid);
            for (Long cId : contractIds) {
                contracts.add(cId);
            }
        } catch (Exception ignore) {
        }
        return contracts;
    }

    @Override
    public Result enableNotificationByPUSH(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "state") boolean state) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        List<Long> contracts = getContracts(contractId, handler);
        if (!DAOService.getInstance().enableClientNotificationByPUSH(contracts, state)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        } else {
            EMPProcessor processor = RuntimeContext.getAppContext().getBean(EMPProcessor.class);
            for (Long cId : contracts) {
                processor.updateNotificationParams(cId);
            }
        }
        return r;
    }

    @Override
    public Result enableNotificationByEmail(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "state") boolean state) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        List<Long> contracts = getContracts(contractId, handler);
        if (!DAOService.getInstance().enableClientNotificationByEmail(contracts, state)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        } else {
            EMPProcessor processor = RuntimeContext.getAppContext().getBean(EMPProcessor.class);
            for (Long cId : contracts) {
                processor.updateNotificationParams(cId);
            }
        }
        return r;
    }

    @Override
    public Result changeMobilePhone(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "mobilePhone") String mobilePhone, @WebParam(name = "dateConfirm") Date dateConfirm) {
        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        mobilePhone = Client.checkAndConvertMobile(mobilePhone);
        if (mobilePhone == null) {
            r.resultCode = RC_INVALID_DATA;
            r.description = RC_INVALID_MOBILE;
            return r;
        }
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("soap метод changeMobilePhone");
        clientsMobileHistory.setShowing("Портал");
        if (!DAOService.getInstance().setClientMobilePhone(contractId, mobilePhone, dateConfirm, clientsMobileHistory)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeEmail(@WebParam(name = "contractId") Long contractId, @WebParam(name = "email") String email) {
        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        if (!DAOService.getInstance().setClientEmail(contractId, email)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public Result changeExpenditureLimit(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "roleRepresentative") Long roleRepresentative, @WebParam(name = "limit") long limit) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);
        Date date = new Date(System.currentTimeMillis());
        //authenticateRequest(contractId);

        Result r = new Result(RC_OK, RC_OK_DESC);
        if (limit < 0) {
            r = new Result(RC_INVALID_DATA, "Лимит не может быть меньше нуля");
            return r;
        }
        if (roleRepresentative != null && (roleRepresentative < 0 || roleRepresentative > 1)) {
            r = new Result(RC_INVALID_DATA, "Лимит может быть установлен только законным представителем");
            return r;
        }
        try {
            long version = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
            if (!DAOService.getInstance().setClientExpenditureLimit(contractId, limit, version)) {
                r = new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            } else {
                Long idOfClient = DAOReadonlyService.getInstance().getClientByContractId(contractId).getIdOfClient();
                handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date, handler.getData().getSsoId(),
                        idOfClient, handler.getData().getOperationType());
            }
            return r;
        } catch (Exception e) {
            logger.error("Error in changeExpenditureLimit: ", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    @Override
    public Result changeThresholdBalanceNotify(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "threshold") long threshold) {
        authenticateRequest(contractId);

        Result r = new Result(RC_OK, RC_OK_DESC);
        if (threshold < 0) {
            r = new Result(RC_INVALID_DATA, "Значение порога не может быть меньше нуля");
            return r;
        }
        if (!DAOService.getInstance().setClientBalanceToNotify(contractId, threshold)) {
            r = new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
        }
        return r;
    }

    @Override
    public PublicationListResult getPublicationListSimple(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "searchCondition") String searchCondition, @WebParam(name = "limit") int limit,
            @WebParam(name = "offset") int offset) {

        authenticateRequest(contractId);

        final String fSearchCondition = searchCondition;
        final int fLimit = limit;
        final int fOffset = offset;
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processPublicationListSimple(client, data, objectFactory, session, fSearchCondition, fLimit, fOffset);
            }
        });

        PublicationListResult pubListResult = new PublicationListResult();
        pubListResult.publicationList = data.getPublicationItemList();
        pubListResult.resultCode = data.getResultCode();
        pubListResult.description = data.getDescription();
        pubListResult.amountForCondition = data.getAmountForCondition();
        return pubListResult;
    }

    private void processPublicationListSimple(Client client, Data data, ObjectFactory objectFactory, Session session,
            String searchCondition, int limit, int offset) throws DatatypeConfigurationException {

        if (searchCondition.isEmpty()) {
            data.setPublicationItemList(new PublicationItemList());
            data.setAmountForCondition(0);
            return;
        }
        String str_condition =
                " and (upper(pub.Author) like :condition_like or upper(pub.Title) like :condition_like or upper(pub.Title2) like :condition_like "
                        + "or pub.PublicationDate = :condition_eq "
                        + "or upper(pub.Publisher) like :condition_like or pub.ISBN like :condition_isbn) ";

        Long org = client.getOrg().getIdOfOrg();

        StringBuilder bquery = new StringBuilder();
        bquery.append(QUERY_PUBLICATION_LIST);

        SQLQuery query = session.createSQLQuery(bquery.toString().replaceAll("CONDITION", str_condition));
        query.setParameter("org", org);
        query.setParameter("condition_like", "%" + searchCondition.toUpperCase() + "%");
        query.setParameter("condition_eq", searchCondition);
        query.setParameter("condition_isbn", searchCondition.replaceAll("-", "") + "%");
        query.setParameter("limit", limit);
        query.setParameter("offset", offset);

        data.setPublicationItemList(getPublicationListItem(objectFactory, query));

        bquery.setLength(0);
        bquery.append(QUERY_PUBLICATION_LIST_COUNT);
        bquery.append(str_condition);
        bquery.append(QUERY_PUBLICATION_LIST_COUNT_TAIL);
        query = session.createSQLQuery(bquery.toString());
        query.setParameter("org", org);
        query.setParameter("condition_like", "%" + searchCondition.toUpperCase() + "%");
        query.setParameter("condition_eq", searchCondition);
        query.setParameter("condition_isbn", searchCondition.replaceAll("-", "") + "%");
        data.setAmountForCondition(((BigInteger) query.uniqueResult()).intValue());
    }

    @Override
    public PublicationListResult getPublicationListAdvanced(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "author") String author, @WebParam(name = "title") String title,
            @WebParam(name = "title2") String title2, @WebParam(name = "publicationDate") String publicationDate,
            @WebParam(name = "publisher") String publisher, @WebParam(name = "isbn") String isbn,
            @WebParam(name = "limit") int limit, @WebParam(name = "offset") int offset) {

        authenticateRequest(contractId);

        final String fAuthor = author;
        final String fTitle = title;
        final String fTitle2 = title2;
        final String fPublicationDate = publicationDate;
        final String fPublisher = publisher;
        final String fISBN = isbn;
        final int fLimit = limit;
        final int fOffset = offset;
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processPublicationListAdvanced(client, data, objectFactory, session, fAuthor, fTitle, fTitle2,
                        fPublicationDate, fPublisher, fISBN, fLimit, fOffset);
            }
        });

        PublicationListResult pubListResult = new PublicationListResult();
        pubListResult.publicationList = data.getPublicationItemList();
        pubListResult.resultCode = data.getResultCode();
        pubListResult.description = data.getDescription();
        pubListResult.amountForCondition = data.getAmountForCondition();
        return pubListResult;
    }

    private void processPublicationListAdvanced(Client client, Data data, ObjectFactory objectFactory, Session session,
            String author, String title, String title2, String publicationDate, String publisher, String isbn,
            int limit, int offset) throws DatatypeConfigurationException {

        if ((author == null || author.isEmpty()) && (title == null || title.isEmpty()) &&
                (title2 == null || title2.isEmpty()) && (publicationDate == null || publicationDate.isEmpty())
                && (publisher == null || publisher.isEmpty()) && (isbn == null || isbn.isEmpty())) {
            //data.setPublicationItemList(new PublicationItemList());
            //data.setAmountForCondition(0);
            throw new DatatypeConfigurationException();
        }
        if (limit < 0 )
            limit = 0;
        Long org = client.getOrg().getIdOfOrg();

        StringBuilder bquery = new StringBuilder();
        bquery.append(QUERY_PUBLICATION_LIST);
        String str_condition = getConditionsForPublicationListAdvanced(author, title, title2, publicationDate,
                publisher, isbn);

        SQLQuery query = session.createSQLQuery(bquery.toString().replaceAll("CONDITION", str_condition));

        setUserParametersForPublicationList(query, author, title, title2, publicationDate, publisher, isbn);

        query.setParameter("org", org);
        query.setParameter("limit", limit);
        query.setParameter("offset", offset);

        data.setPublicationItemList(getPublicationListItem(objectFactory, query));

        bquery.setLength(0);
        bquery.append(QUERY_PUBLICATION_LIST_COUNT);
        bquery.append(str_condition);
        bquery.append(QUERY_PUBLICATION_LIST_COUNT_TAIL);

        query = session.createSQLQuery(bquery.toString());
        query.setParameter("org", org);
        setUserParametersForPublicationList(query, author, title, title2, publicationDate, publisher, isbn);
        data.setAmountForCondition(((BigInteger) query.uniqueResult()).intValue());
    }

    private String getConditionsForPublicationListAdvanced(String author, String title, String title2,
            String publicationDate, String publisher, String isbn) {
        StringBuilder builder = new StringBuilder();
        if (author != null && !author.isEmpty()) {
            builder.append(" and upper(pub.Author) like :author ");
        }
        if (title != null && !title.isEmpty()) {
            builder.append(" and upper(pub.Title) like :title ");
        }
        if (title2 != null && !title2.isEmpty()) {
            builder.append(" and upper(pub.Title2) like :title2 ");
        }
        if (publicationDate != null && !publicationDate.isEmpty()) {
            builder.append(" and pub.PublicationDate = :publicationDate ");
        }
        if (publisher != null && !publisher.isEmpty()) {
            builder.append(" and upper(pub.Publisher) like :publisher ");
        }
        if (isbn != null && !isbn.isEmpty()) {
            builder.append(" and pub.ISBN like :isbn ");
        }
        return builder.toString();
    }

    private void setUserParametersForPublicationList(SQLQuery query, String author, String title, String title2,
            String publicationDate, String publisher, String isbn) {
        if (author != null && !author.isEmpty()) {
            query.setParameter("author", "%" + author.toUpperCase() + "%");
        }
        if (title != null && !title.isEmpty()) {
            query.setParameter("title", "%" + title.toUpperCase() + "%");
        }
        if (title2 != null && !title2.isEmpty()) {
            query.setParameter("title2", "%" + title2.toUpperCase() + "%");
        }
        if (publicationDate != null && !publicationDate.isEmpty()) {
            query.setParameter("publicationDate", publicationDate);
        }
        if (publisher != null && !publisher.isEmpty()) {
            query.setParameter("publisher", "%" + publisher.toUpperCase() + "%");
        }
        if (isbn != null && !isbn.isEmpty()) {
            query.setParameter("isbn", isbn.replaceAll("-", "") + "%");
        }
    }

    private PublicationItemList getPublicationListItem(ObjectFactory objectFactory, SQLQuery query) {
        List list = query.list();

        PublicationItemList puList = objectFactory.createPublicationItemList();
        for (Iterator iterator = list.iterator(); iterator.hasNext(); ) {
            Object[] objs = (Object[]) iterator.next();

            PublicationInstancesItem pu = new PublicationInstancesItem();

            pu.setInstancesAmount(((BigInteger) objs[6]).intValue());
            pu.setInstancesAvailable(((BigInteger) objs[7]).intValue());
            pu.setOrgHolder(objs[9].toString());
            pu.setOrgHolderId(((BigInteger) objs[8]).longValue());
            PublicationItem pi = new PublicationItem();
            pi.setAuthor(objs[1].toString());
            pi.setPublisher(objs[5].toString());
            pi.setTitle(objs[2].toString());
            pi.setTitle2(objs[3].toString());
            pi.setPublicationDate(objs[4].toString());
            pi.setPublicationId(((BigInteger) objs[0]).longValue());
            pu.setPublication(pi);

            puList.getC().add(pu);
        }
        return puList;
    }

    @Override
    public OrderPublicationResult orderPublication(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "publicationId") Long publicationId, @WebParam(name = "orgHolderId") Long orgHolderId) {
        authenticateRequest(contractId);
        Session session = null;
        Transaction transaction = null;
        OrderPublicationResult result = new OrderPublicationResult();

        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    result.resultCode = RC_CLIENT_NOT_FOUND;
                    result.description = RC_CLIENT_NOT_FOUND_DESC;
                    return result;
                }

                Publication publication = findPublicationByPublicationId(session, publicationId);
                if (publication == null) {
                    result.resultCode = RC_PUBLICATION_NOT_AVAILABLE;
                    result.description = RC_PUBLICATION_NOT_AVAILABLE_DESC;
                    return result;
                }

                Org orgHolder = DAOUtils.findOrg(session, orgHolderId);
                if (orgHolder == null) {
                    result.resultCode = RC_ORG_HOLDER_NOT_FOUND;
                    result.description = RC_ORG_HOLDER_NOT_FOUND_DESC;
                    return result;
                }

                Criteria orderCriteria = session.createCriteria(OrderPublication.class);
                orderCriteria.add(Restrictions.eq("client", client));
                orderCriteria.add(Restrictions.eq("publication", publication));
                orderCriteria.add(Restrictions.eq("deletedState", false));
                orderCriteria.add(Restrictions.eq("orgOwner", orgHolderId));

                if (!orderCriteria.list().isEmpty()) {
                    result.resultCode = RC_ORDER_PUBLICATION_ALREADY_EXISTS;
                    result.description = RC_ORDER_PUBLICATION_ALREADY_EXISTS_DESC;
                    return result;
                }

                final String ORDER_PUBLICATION = "OrderPublication";
                final String bquery = "select count(ins.IdOfInstance) "
                        + "from cf_instances ins inner join cf_issuable iss on ins.IdOfInstance = iss.IdOfInstance "
                        + "where ins.OrgOwner = :org and ins.IdOfPublication = :pub and not exists "
                        + "(select IdOfCirculation from cf_circulations cir where cir.IdOfIssuable = iss.IdOfIssuable and cir.RealRefundDate is null) ";
                SQLQuery query = session.createSQLQuery(bquery.toString());
                query.setParameter("org", orgHolderId);
                query.setParameter("pub", publicationId);
                Long insCount = ((BigInteger) query.uniqueResult()).longValue();
                if (insCount > 0) {
                    Long maxVersion = DOVersionRepository.updateClassVersion(ORDER_PUBLICATION, session);
                    OrderPublication orderPublication = new OrderPublication();
                    orderPublication.setGlobalVersion(maxVersion);
                    orderPublication.setGlobalVersionOnCreate(maxVersion);
                    orderPublication.setCreatedDate(new Date());
                    orderPublication.setDeletedState(false);
                    orderPublication.setIdOfClient(client.getIdOfClient());
                    orderPublication.setOrgOwner(orgHolderId);
                    orderPublication.setSendAll(SendToAssociatedOrgs.DontSend);

                    orderPublication.setPublication(publication);
                    orderPublication.setClient(client);
                    session.persist(orderPublication);
                    result.id = orderPublication.getGlobalId();
                    result.resultCode = RC_OK;
                    result.description = RC_OK_DESC;
                } else {
                    result.resultCode = RC_PUBLICATION_NOT_AVAILABLE;
                    result.description = RC_PUBLICATION_NOT_AVAILABLE_DESC;
                }
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    private Publication findPublicationByPublicationId(Session session, long publicationId) {
        Criteria publicationCriteria = session.createCriteria(Publication.class);
        publicationCriteria.add(Restrictions.eq("globalId", publicationId));
        List<Publication> resultList = (List<Publication>) publicationCriteria.list();
        return resultList.isEmpty() ? null : resultList.get(0);
    }

    @Override
    public OrderPublicationListResult getOrderPublicationList(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processOrderPublicationList(client, data, objectFactory, session);
            }
        });

        OrderPublicationListResult orderListResult = new OrderPublicationListResult();
        orderListResult.orderPublicationList = data.getOrderPublicationItemList();
        orderListResult.resultCode = data.getResultCode();
        orderListResult.description = data.getDescription();
        return orderListResult;
    }

    private void processOrderPublicationList(Client client, Data data, ObjectFactory objectFactory, Session session)
            throws DatatypeConfigurationException {
        Criteria orderCriteria = session.createCriteria(OrderPublication.class);
        orderCriteria.add(Restrictions.eq("client", client));
        orderCriteria.add(Restrictions.eq("deletedState", false));
        orderCriteria.addOrder(org.hibernate.criterion.Order.asc("createdDate"));

        List<OrderPublication> orderList = orderCriteria.list();

        HashMap<Long, String> friendlyOrgNames = getFriendlyOrgNames(client);

        OrderPublicationItemList orList = objectFactory.createOrderPublicationItemList();
        for (OrderPublication c : orderList) {
            OrderPublicationItem oi = new OrderPublicationItem();
            oi.setOrderDate(toXmlDateTime(c.getCreatedDate()));
            oi.setOrderId(c.getGlobalId());
            oi.setOrderStatus(c.getStatus());
            oi.setOrgHolder(StringUtils.defaultString(friendlyOrgNames.get(c.getOrgOwner())));
            Publication p = c.getPublication();
            if (p != null) {
                PublicationItem pi = new PublicationItem();
                pi.setAuthor(p.getAuthor());
                pi.setPublisher(p.getPublisher());
                pi.setTitle(p.getTitle());
                pi.setTitle2(p.getTitle2());
                pi.setPublicationDate(p.getPublicationdate());
                pi.setPublicationId(p.getGlobalId());
                oi.setPublication(pi);
            }
            orList.getC().add(oi);
        }
        data.setOrderPublicationItemList(orList);
    }

    private HashMap<Long, String> getFriendlyOrgNames(Client client) {
        Set<Org> frienlyOrgs = client.getOrg().getFriendlyOrg();
        HashMap<Long, String> frienlyOrgNames = new HashMap<Long, String>();
        for (Org org : frienlyOrgs) {
            frienlyOrgNames.put(org.getIdOfOrg(), org.getShortName());
        }
        return frienlyOrgNames;
    }

    @Override
    public OrderPublicationDeleteResult deleteOrderPublication(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "orderId") Long orderId) {
        authenticateRequest(contractId);
        OrderPublicationDeleteResult result = new OrderPublicationDeleteResult();
        Session session = null;
        Transaction transaction = null;
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    result.resultCode = RC_CLIENT_NOT_FOUND;
                    result.description = RC_CLIENT_NOT_FOUND_DESC;
                    return result;
                }
                Criteria orderCriteria = session.createCriteria(OrderPublication.class);
                orderCriteria.add(Restrictions.eq("globalId", orderId));
                OrderPublication order = (OrderPublication) orderCriteria.uniqueResult();
                if (order == null) {
                    result.resultCode = RC_ORDER_PUBLICATION_NOT_FOUND;
                    result.description = RC_ORDER_PUBLICATION_NOT_FOUND_DESC;
                    return result;
                } else {
                    if (order.getStatus() != null && !order.getStatus().isEmpty()) {
                        result.resultCode = RC_ORDER_PUBLICATION_CANT_BE_DELETED;
                        result.description = RC_ORDER_PUBLICATION_CANT_BE_DELETED_DESC;
                        return result;
                    }
                    order.setDeletedState(true);
                    session.save(order);
                    transaction.commit();
                    transaction = null;
                }
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public ClassRegisterEventListByGUIDResult putClassRegisterEventListByGUID(
            ClassRegisterEventListByGUID registerEventList) {
        authenticateRequest(null);

        return сlassRegisterEventListByGUID(registerEventList);

    }

    private ClassRegisterEventListByGUIDResult сlassRegisterEventListByGUID(
            ClassRegisterEventListByGUID registerEventList) {
        ClassRegisterEventListByGUIDResult result = new ClassRegisterEventListByGUIDResult();
        ClassRegisterEventListByGUIDResultItem result_list = new ClassRegisterEventListByGUIDResultItem();
        result_list.classRegisterEventListByGUIDResultItem = new ArrayList<ClassRegisterEventByGUIDItem>();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            //удаляем события за предыдущие дни
            Date redtime = new Date(System.currentTimeMillis());
            redtime = CalendarUtils.calculateTodayStart(new GregorianCalendar(), redtime);
            if (!eeManualCleared.contains(redtime)) {
                Query query = persistenceSession
                        .createQuery("delete from EnterEventManual where evtDateTime <= :evtDateTime");
                query.setParameter("evtDateTime", redtime);
                Integer cnt = query.executeUpdate();
                eeManualCleared.add(redtime);
            }

            for (ClassRegisterEventByGUID event : registerEventList.registerEvent) {
                ClassRegisterEventByGUIDItem it = processClassRegisterEvent(persistenceSession, event);
                result_list.classRegisterEventListByGUIDResultItem.add(it);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            result.classRegisterEventListByGUIDResult = result_list;

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Internal error in putClassRegisterEventListByGUID", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return result;
    }

    private ClassRegisterEventByGUIDItem processClassRegisterEvent(Session persistenceSession,
            ClassRegisterEventByGUID event) {
        ClassRegisterEventByGUIDItem item = new ClassRegisterEventByGUIDItem();
        item.guid = event.guid;

        try {
            Criteria criteria = persistenceSession.createCriteria(Client.class);
            criteria.add(Restrictions.eq("clientGUID", event.guid));
            List<Client> clientList = criteria.list();
            if (clientList == null || clientList.size() == 0) {
                logger.warn(String.format("Client with guid=%s not found to save manual enter event", event.guid));
                throw new IllegalArgumentException(String.format("Клиент с guid=%s не найден", event.guid));
            }
            Client client = clientList.get(0);
            List<String> pList = new ArrayList<String>();
            pList.add(event.guid);
            Boolean doGenerateEvent = true;
            try {
                EnterEventStatusListResult res = processEnterEventStatusList(pList);
                //doGenerateEvent = !processEnterEventStatusList(pList).enterEventStatusList.getC().get(0).getInside();
                doGenerateEvent = !res.enterEventStatusList.getC().get(0).getInside();
                if (!doGenerateEvent) {
                    Date lastEventDate = res.enterEventStatusList.getC().get(0).getLastEnterEventDateTime()
                            .toGregorianCalendar().getTime();
                    Date dayOfLastEventDate = CalendarUtils.calculateTodayStart(new GregorianCalendar(), lastEventDate);
                    Date dayOfNewEventDate = CalendarUtils
                            .calculateTodayStart(new GregorianCalendar(), event.evtDateTime);
                    if (!dayOfLastEventDate.equals(dayOfNewEventDate) && dayOfLastEventDate.before(dayOfNewEventDate)) {
                        doGenerateEvent = true; //Если последнее событие по клиенту было не в этот день, а ренее, то новое событие присутствия надо генерировать
                    }
                }
            } catch (Exception doGenerateIsTrue) {
            }

            if (doGenerateEvent) {
                EnterEventManual newEvent = new EnterEventManual();
                newEvent.setEnterName(event.evtName);
                newEvent.setEvtDateTime(event.evtDateTime);
                newEvent.setIdOfClient(client.getIdOfClient());
                newEvent.setIdOfOrg(client.getOrg().getIdOfOrg());
                persistenceSession.save(newEvent);
            }

            item.state = 0;

        } catch (IllegalArgumentException e) {
            item.state = 100; //Клиент не найден
        } catch (Exception e) {
            logger.error("Internal error in putClassRegisterEventListByGUID", e);
            item.state = 101; //Internal Error
        }
        return item;
    }

    @Override
    public EnterEventStatusListResult getEnterEventStatusListByGUID(List<String> guids) {
        authenticateRequest(null);

        return processEnterEventStatusList(guids);

    }

    private EnterEventStatusListResult processEnterEventStatusList(List<String> guids) {
        EnterEventStatusListResult result = new EnterEventStatusListResult();
        EnterEventStatusItemList result_list = new EnterEventStatusItemList();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            String where_in = "'" + StringUtils.join(guids, "', '") + "'";
            String query_str = String
                    .format("select cli.clientGuid, ee.passDirection, to_timestamp(ee.evtdatetime / 1000) from cf_enterevents ee join cf_clients cli "
                            + "on (ee.idofclient = cli.idofclient and (ee.evtdatetime = (select evtdatetime from cf_enterevents "
                            + "where idofclient = cli.idofclient order by evtdatetime desc limit 1))) "
                            + "where cli.clientguid in (%s)", where_in);
            org.hibernate.Query q = persistenceSession.createSQLQuery(query_str);
            List resultList = q.list();
            for (Object entry : resultList) {
                Object record[] = (Object[]) entry;
                String guid = (String) record[0];
                Integer passDirection = (Integer) record[1];
                Date evtDate = record[2] == null ? null : new Date(((Timestamp) record[2]).getTime());
                Boolean inside = false;
                switch (passDirection) {
                    case EnterEvent.ENTRY:
                    case EnterEvent.DETECTED_INSIDE:
                    case EnterEvent.RE_ENTRY:
                    case EnterEvent.CHECKED_BY_TEACHER_EXT:
                    case EnterEvent.CHECKED_BY_TEACHER_INT:
                        inside = true;
                        break;
                }
                /*if (inside) {
                    Date dayOfLastEventDate = CalendarUtils.calculateTodayStart(new GregorianCalendar(), evtDate);
                    Date todayDate = CalendarUtils.calculateTodayStart(new GregorianCalendar(), new Date(System.currentTimeMillis()));
                    if (!dayOfLastEventDate.equals(todayDate) && dayOfLastEventDate.before(todayDate)) {
                        inside = false; //Если последнее событие по клиенту было не сегодня, то считаем, что его нет в здании
                    }
                }*/
                EnterEventStatusItem result_item = new EnterEventStatusItem();
                result_item.setGuid(guid);
                result_item.setInside(inside);
                GregorianCalendar c = new GregorianCalendar();
                c.setTime(evtDate);
                XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
                result_item.setLastEnterEventDateTime(date2);
                result_item.setLastEnterEventDirection(passDirection);
                result_list.getC().add(result_item);
            }

            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            result.enterEventStatusList = result_list;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = e.toString();
            result.enterEventStatusList = null;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public Result setGuardianshipDisabled(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "guardMobile") String guardMobile, @WebParam(name = "value") Boolean value,
            @WebParam(name = "roleRepresentativePrincipal") Integer roleRepresentativePrincipal) {
        authenticateRequest(contractId);
        MessageContext mc = context.getMessageContext();
        HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setReason("Веб метод setGuardianshipDisabled");
        clientGuardianHistory.setWebAdress(req.getRemoteAddr());
        clientGuardianHistory.setGuardian(guardMobile);
        return processSetGuardianship(contractId, guardMobile, value, roleRepresentativePrincipal, clientGuardianHistory);
    }

    private Result processSetGuardianship(Long contractId, String guardMobile, Boolean value,
            Integer roleRepresentativePrincipal, ClientGuardianHistory clientGuardianHistory) {
        Result result = new Result();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session session = null;
        Transaction persistenceTransaction = null;

        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("soap метод setGuardianshipDisabled");
        clientsMobileHistory.setShowing("Портал");
        try {
            if (StringUtils.isEmpty(guardMobile)) {
                throw new InvalidDataException("Не заполнен номер телефона опекуна");
            }

            if (roleRepresentativePrincipal != null && (roleRepresentativePrincipal < 0
                    || roleRepresentativePrincipal > 1)) {
                throw new InvalidDataException("Возможно только законным представителем");
            }
            session = runtimeContext.createPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                throw new ClientNotFoundException("Не удалось найти клиента по л/с " + contractId);
            }

            List<ClientGuardianItem> guardians = ClientManager.loadGuardiansByClient(session, client.getIdOfClient(), false);
            boolean guardianWithMobileFound = false;
            for (ClientGuardianItem item : guardians) {
                Client guardian = (Client) session.get(Client.class, item.getIdOfClient());
                if (guardian != null && stringEquals(guardian.getMobile(), guardMobile)) {
                    guardianWithMobileFound = true;
                    Criteria criteria = session.createCriteria(ClientGuardian.class);
                    criteria.add(Restrictions.eq("idOfChildren", client.getIdOfClient()));
                    criteria.add(Restrictions.eq("idOfGuardian", item.getIdOfClient()));
                    List<ClientGuardian> listOfClientGuardian = criteria.list();
                    if (listOfClientGuardian == null || listOfClientGuardian.isEmpty()) {
                        continue;
                    }
                    for (ClientGuardian cg : listOfClientGuardian) {
                        clientGuardianHistory.setClientGuardian(cg);
                        clientGuardianHistory.setChangeDate(new Date());
                        cg.setDisabled(value);
                        cg.setVersion(getClientGuardiansResultVersion(session));
                        cg.setLastUpdate(new Date());
                        //cg.setRepresentType(ClientGuardianRepresentType.fromInteger(roleRepresentativePrincipal));
                        session.persist(cg);
                    }
                }
            }
            if (value) {
                if (client.getMobile().equals(guardMobile)) {
                    client.initClientMobileHistory(clientsMobileHistory);
                    client.setMobile("");
                    logger.debug(
                            "class : ClientRoomControllerWS, method : processSetGuardianship line : 4790, idOfClient : "
                                    + client.getIdOfClient() + " mobile : " + client.getMobile());
                    session.persist(client);
                }
            } else {
                if (!guardianWithMobileFound) {
                    if (client.getMobile() == null || client.getMobile().isEmpty()) {
                        client.initClientMobileHistory(clientsMobileHistory);
                        client.setMobile(guardMobile);
                        logger.debug(
                                "class : ClientRoomControllerWS, method : processSetGuardianship line : 4797, idOfClient : "
                                        + client.getIdOfClient() + " mobile : " + client.getMobile());
                        session.persist(client);
                    } else {
                        throw new IllegalArgumentException(String.format(
                                "Невозможно активировать опекунскую связь между клиентом "
                                        + "с л/с %s и представителем с телефоном %s", contractId, guardMobile));
                    }
                }
            }

            session.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (InvalidDataException e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_REQUIRED_FIELDS_ARE_NOT_FILLED;
            result.description = e.getMessage();
        } catch (ClientNotFoundException e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = RC_CLIENT_NOT_FOUND_DESC;
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = e.getMessage();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private boolean stringEquals(String mobile, String guardMobile) {
        return mobile == null || mobile.isEmpty() ? guardMobile == null || guardMobile.isEmpty()
                : mobile.equals(guardMobile);
    }

    private Long getClientGuardiansResultVersion(Session session) {
        Long version;
        try {
            version = ClientManager.generateNewClientGuardianVersion(session);
        } catch (Exception ex) {
            logger.error("Failed get max client guardians version, ", ex);
            version = 0L;
        }
        return version;
    }

    @Override
    public Result clearMobileByContractId(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "phone") String phone) {
        authenticateRequest(contractId);
        return processClearMobile(contractId, phone);
    }

    private Result processClearMobile(Long contractId, String phone) {
        Result result = new Result();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session session = null;
        Transaction persistenceTransaction = null;
        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("soap метод clearMobileByContractId");
        clientsMobileHistory.setShowing("Портал");
        try {
            session = runtimeContext.createPersistenceSession();
            persistenceTransaction = session.beginTransaction();
            if (contractId == null) {
                return new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }
            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                return new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }
            String currentMobile = client.getMobile();

            if (phone.equals(currentMobile)) {
                client.initClientMobileHistory(clientsMobileHistory);
                client.setMobile("");
                logger.debug("class : ClientRoomControllerWS, method : processClearMobile line : 4860, idOfClient : "
                        + client.getIdOfClient() + " mobile : " + client.getMobile());
                session.persist(client);
                logger.info(String.format("Очищен номер телефона %s у клиента с ContractId=%s", phone,
                        client.getContractId()));
            }

            List<ClientGuardianItem> guardians = ClientManager.loadGuardiansByClient(session, client.getIdOfClient(), false);
            for (ClientGuardianItem item : guardians) {
                Client guardian = DAOUtils.findClientByContractId(session, item.getContractId());
                if (phone.equals(guardian.getMobile())) {
                    guardian.initClientMobileHistory(clientsMobileHistory);
                    guardian.setMobile("");
                    logger.debug(
                            "class : ClientRoomControllerWS, method : processClearMobile line : 4870, idOfClient : "
                                    + guardian.getIdOfClient() + " mobile : " + guardian.getMobile());
                    session.persist(guardian);
                    logger.info(String.format("Очищен номер телефона %s у клиента с ContractId=%s", phone,
                            guardian.getContractId()));
                }
            }
            session.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            session.close();
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = e.toString();
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;
        return result;
    }

    @Override
    public CirculationListResult getCirculationList(@WebParam(name = "contractId") Long contractId, int state) {
        authenticateRequest(contractId);

        final int fState = state;
        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processCirculationList(client, data, objectFactory, session, fState);
            }
        });

        CirculationListResult circListResult = new CirculationListResult();
        circListResult.circulationList = data.getCirculationItemList();
        circListResult.resultCode = data.getResultCode();
        circListResult.description = data.getDescription();
        return circListResult;
    }

    private void processCirculationList(Client client, Data data, ObjectFactory objectFactory, Session session,
            int state) throws DatatypeConfigurationException {
        Criteria circulationCriteria = session.createCriteria(Circulation.class);
        circulationCriteria.add(Restrictions.eq("client", client));
        if (state == CIRCULATION_STATUS_FILTER_ALL_ON_HANDS) {
            circulationCriteria.add(Restrictions.or(Restrictions.eq("status", Circulation.EXTENDED),
                    Restrictions.eq("status", Circulation.ISSUED)));
        } else if (state != CIRCULATION_STATUS_FILTER_ALL) {
            circulationCriteria.add(Restrictions.eq("status", state));
        }
        circulationCriteria.addOrder(org.hibernate.criterion.Order.desc("issuanceDate"));

        List<Circulation> circulationList = circulationCriteria.list();

        CirculationItemList ciList = objectFactory.createCirculationItemList();
        for (Circulation c : circulationList) {
            CirculationItem ci = new CirculationItem();
            ci.setIssuanceDate(toXmlDateTime(c.getIssuanceDate()));
            ci.setStatus(c.getStatus());
            ci.setRealRefundDate(toXmlDateTime(c.getRealRefundDate()));
            ci.setRefundDate(toXmlDateTime(c.getRefundDate()));
            Publication p = c.getIssuable().getInstance().getPublication();
            if (p != null) {
                PublicationItem pi = new PublicationItem();
                pi.setAuthor(p.getAuthor());
                pi.setPublisher(p.getPublisher());
                pi.setTitle(p.getTitle());
                pi.setTitle2(p.getTitle2());
                pi.setPublicationDate(p.getPublicationdate());
                pi.setPublicationId(p.getGlobalId());
                ci.setPublication(pi);
            }
            ciList.getC().add(ci);
        }
        data.setCirculationItemList(ciList);
    }

    @Override
    public Result authorizeClient(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "token") String token) {
        IntegraPartnerConfig.LinkConfig partnerLinkConfig = null;
        //logger.info("init authorizeClient");
        partnerLinkConfig = authenticateRequest(null);
        if (logger.isDebugEnabled()) {
            logger.debug("begin authorizeClient");
        }
        try {

            DAOService daoService = DAOService.getInstance();
            DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
            Client client = daoReadonlyService.getClientByContractId(contractId);
            if (client == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Client not found");
                }
                return new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }
            boolean authorized = false;
            if (partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH_BY_NAME) {
                String fullNameUpCase = client.getPerson().getFullName().replaceAll("\\s", "").toUpperCase();
                fullNameUpCase = fullNameUpCase + "Nb37wwZWufB";
                byte[] bytesOfMessage = fullNameUpCase.getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hash = md.digest(bytesOfMessage);
                BigInteger bigInt = new BigInteger(1, hash);
                String md5HashString = String.format("%0" + (hash.length << 1) + "X", bigInt);
                if (logger.isDebugEnabled()) {
                    logger.info("token    md5: " + token.toUpperCase());
                    logger.info("generate md5: " + md5HashString.toUpperCase());
                }
                if (md5HashString.toUpperCase().compareTo(token.toUpperCase()) == 0) {
                    authorized = true;
                    if (partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH) {
                        daoService.addIntegraPartnerAccessPermissionToClient(client.getIdOfClient(),
                                partnerLinkConfig.id);
                    }
                }
            }
            if (partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH_BY_MOBILE) {
                String key = CryptoUtils.MD5(client.getMobile());
                if (key.equalsIgnoreCase(token)) {
                    authorized = true;
                }
            }

            if (partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH_BY_PASSWORD) {
                if (client.hasEncryptedPasswordSHA1(token)) {
                    authorized = true;
                }
            }

            if (client.hasEncryptedPasswordSHA1(token)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("hasEncryptedPassword");
                }
                authorized = true;
                if (!authorized
                        && partnerLinkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH) {
                    daoService.addIntegraPartnerAccessPermissionToClient(client.getIdOfClient(), partnerLinkConfig.id);
                }
            }
            if (logger.isDebugEnabled()) {
                logger.debug("authorized" + String.valueOf(authorized));
            }
            if (authorized) {
                return new Result(RC_OK, RC_OK_DESC);
            } else {
                return new Result(RC_CLIENT_AUTHORIZATION_FAILED, RC_CLIENT_AUTHORIZATION_FAILED_DESC);
            }
        } catch (Exception e) {
            logger.error("Failed to authorized client", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    @Override
    public ActivateLinkingTokenResult activateLinkingToken(String linkingToken) {
        authenticateRequest(null);

        ActivateLinkingTokenResult result = new ActivateLinkingTokenResult();
        try {

            DAOService daoService = DAOService.getInstance();

            Client client = daoService.findAndDeleteLinkingToken(linkingToken);
            if (client == null) {
                result.resultCode = RC_INVALID_DATA;
                result.description = "Код активации не найден";
            } else {
                result.contractId = client.getContractId();
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
            }
        } catch (Exception e) {
            logger.error("Failed to activate linking token: " + linkingToken, e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public GenerateLinkingTokenResult generateLinkingToken(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);

        GenerateLinkingTokenResult result = new GenerateLinkingTokenResult();
        try {
            DAOService daoService = DAOService.getInstance();
            DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
            Client client = daoReadonlyService.getClientByContractId(contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            LinkingToken linkingToken = daoService.generateLinkingToken(client);
            result.linkingToken = linkingToken.getToken();
            result.contractId = contractId;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Failed to generate linking token", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public Result sendLinkingTokenByContractId(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);

        try {
            Result result = new Result();
            DAOService daoService = DAOService.getInstance();
            DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
            Client client = daoReadonlyService.getClientByContractId(contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            LinkingToken linkingToken = daoService.generateLinkingToken(client);
            String info = "";
            if (client.hasEmail()) {
                info += "e-mail";
            }
            if (client.hasMobile()) {
                if (info.length() > 0) {
                    info += ", ";
                }
                info += "SMS";
            }
            if (info.length() == 0) {
                result.resultCode = RC_NO_CONTACT_DATA;
                result.description = RC_NO_CONTACT_DATA_DESC;
            } else {
                RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                        .sendMessageAsync(client, EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED,
                                new String[]{"linkingToken", linkingToken.getToken()}, new Date());
                result.resultCode = RC_OK;
                result.description = "Код активации отправлен по " + info;
            }
            return result;
        } catch (Exception e) {
            logger.error("Failed to send linking token", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    @Override
    public Result sendLinkingTokenByMobile(@WebParam(name = "mobilePhone") String mobilePhone) {
        authenticateRequest(null);
        Result result = new Result();

        try {
            mobilePhone = Client.checkAndConvertMobile(mobilePhone);
            if (mobilePhone == null) {
                result.resultCode = RC_INVALID_DATA;
                result.description = RC_INVALID_MOBILE;
                return result;
            }

            DAOService daoService = DAOService.getInstance();
            DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
            List<Client> clientList = daoReadonlyService.findClientsByMobilePhone(mobilePhone);
            if (clientList.size() == 0) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            String codes = "";
            for (Client cl : clientList) {
                LinkingToken linkingToken = daoService.generateLinkingToken(cl);
                if (codes.length() > 0) {
                    codes += ", ";
                }
                codes += linkingToken.getToken();
            }
            RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                    .sendMessageAsync(clientList.get(0), EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED,
                            new String[]{"linkingToken", codes}, new Date());
            result.resultCode = RC_OK;
            result.description = "Код активации отправлен по SMS для " + clientList.size() + " л/с";
            return result;
        } catch (Exception e) {
            logger.error("Failed to send linking token", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        }
    }

    IntegraPartnerConfig.LinkConfig authenticateRequest(Long contractId, HTTPDataHandler handler) throws Error {
        if (context == null) {
            return null; //если вызов через создание класса напрямую, не в качестве веб-сервиса
        }
        MessageContext jaxwsContext = context.getMessageContext();
        HttpServletRequest request = (HttpServletRequest) jaxwsContext.get(SOAPMessageContext.SERVLET_REQUEST);
        String clientAddress = request.getRemoteAddr();
        ////
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        X509Certificate[] certificates = (X509Certificate[]) request
                .getAttribute("javax.servlet.request.X509Certificate");
        ////
        IntegraPartnerConfig.LinkConfig linkConfig = null;
        String DNs = "";
        if (certificates != null && certificates.length > 0) {
            for (int n = 0; n < certificates.length; ++n) {
                String dn = certificates[0].getSubjectDN().getName();
                linkConfig = runtimeContext.getIntegraPartnerConfig().getLinkConfigByCertDN(dn);
                if (linkConfig != null) {
                    break;
                }
                DNs += dn + ";";
            }
        }
        /////
        //данные для логирования запроса
        if (handler != null) {
            handler.setData(jaxwsContext);
        }
        // пробуем по имени и паролю
        if (linkConfig == null) {
            AuthorizationPolicy authorizationPolicy = (AuthorizationPolicy) jaxwsContext
                    .get("org.apache.cxf.configuration.security.AuthorizationPolicy");
            if (authorizationPolicy != null && authorizationPolicy.getUserName() != null) {
                linkConfig = runtimeContext.getIntegraPartnerConfig()
                        .getLinkConfigWithAuthTypeBasicMatching(authorizationPolicy.getUserName(),
                                authorizationPolicy.getPassword());
            }
        }
        /////
        if (linkConfig == null) {
            linkConfig = runtimeContext.getIntegraPartnerConfig()
                    .getLinkConfigWithAuthTypeNoneAndMatchingAddress(clientAddress);
        } else {
            // check remote addr
            if (!linkConfig.matchAddress(clientAddress)) {
                throw new Error("Integra partner auth failed: remote address does not match: " + clientAddress
                        + " for link config: " + linkConfig.id + "; request: ip=" + clientAddress + "; ssl DNs=" + DNs);
            }
        }
        /////
        if (linkConfig == null) {
            throw new Error(
                    "Integra partner auth failed: link config not found: ip=" + clientAddress + "; ssl DNs=" + DNs);
        }
        /////
        if (contractId != null && linkConfig.permissionType == IntegraPartnerConfig.PERMISSION_TYPE_CLIENT_AUTH) {
            Client client = null;
            try {
                client = DAOReadExternalsService.getInstance().findClient(null, contractId);
            } catch (Throwable e) {
            }
            if (client == null) {
                throw new Error("Integra partner auth failed: client not found: contractId=" + contractId + "; ip="
                        + clientAddress + "; ssl DNs=" + DNs);
            }

            if (!client.hasIntegraPartnerAccessPermission(linkConfig.id)) {
                throw new Error("Integra partner auth failed: access prohibited for client: contractId=" + contractId
                        + ", authorize client first; ip=" + clientAddress + "; ssl DNs=" + DNs);
            }
        }
        return linkConfig;
    }

    IntegraPartnerConfig.LinkConfig authenticateRequest(Long contractId) throws Error {
        return authenticateRequest(contractId, null);
    }

    @Override
    public Result changePassword(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "base64passwordHash") String base64passwordHash) {

        authenticateRequest(contractId);

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        if (!DAOService.getInstance().setClientPassword(contractId, base64passwordHash)) {
            r.resultCode = RC_CLIENT_NOT_FOUND;
            r.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return r;
    }

    @Override
    public SendResult sendPasswordRecoverURLFromEmail(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "request") RequestWebParam request) {
        ClientPasswordRecover clientPasswordRecover = RuntimeContext.getInstance().getClientPasswordRecover();
        SendResult sr = new SendResult();
        sr.resultCode = RC_OK;
        sr.description = RC_OK_DESC;
        try {
            int succeeded = clientPasswordRecover.sendPasswordRecoverURLFromEmail(contractId, request);
            sr.recoverStatus = succeeded;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            sr.resultCode = RC_INTERNAL_ERROR;
            sr.description = RC_INTERNAL_ERROR_DESC;


        }
        return sr;
    }

    @Override
    public SendResult sendPasswordRecoverBySms(@WebParam(name = "contractId") Long contractId) {
        ClientPasswordRecover clientPasswordRecover = RuntimeContext.getInstance().getClientPasswordRecover();
        SendResult sr = new SendResult();
        sr.resultCode = RC_OK;
        sr.description = RC_OK_DESC;
        try {
            int succeeded = clientPasswordRecover.sendPasswordRecoverBySms(contractId);
            sr.recoverStatus = succeeded;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            sr.resultCode = RC_INTERNAL_ERROR;
            sr.description = RC_INTERNAL_ERROR_DESC;
        }
        return sr;
    }

    @Override
    public CheckPasswordResult checkPasswordRestoreRequest(@WebParam(name = "request") RequestWebParam request) {
        ClientPasswordRecover clientPasswordRecover = RuntimeContext.getInstance().getClientPasswordRecover();
        CheckPasswordResult cpr = new CheckPasswordResult();
        cpr.resultCode = RC_OK;
        cpr.description = RC_OK_DESC;
        try {
            boolean succeeded = clientPasswordRecover.checkPasswordRestoreRequest(request);
            cpr.succeeded = succeeded;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            cpr.resultCode = RC_INTERNAL_ERROR;
            cpr.description = RC_INTERNAL_ERROR_DESC;

        }
        return cpr;
    }

    @Override
    public IdResult getIdOfClient(@WebParam(name = "contractId") Long contractId) {
        Long idOfClient = null;


        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        IdResult r = new IdResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;


        try {
            RuntimeContext runtimeContext = null;

            runtimeContext = RuntimeContext.getInstance();

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            idOfClient = client.getIdOfClient();


            r.id = idOfClient;
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return r;

    }

    public IdResult getIdOfContragent(@WebParam(name = "contragentName") String contragentName) {


        Long idOfContragent = null;

        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        IdResult r = new IdResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;

        try {
            RuntimeContext runtimeContext = null;

            runtimeContext = RuntimeContext.getInstance();

            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();


            Criteria contragentCriteria = persistenceSession.createCriteria(Contragent.class);
            contragentCriteria.add(Restrictions.eq("contragentName", contragentName));
            Contragent contragent = (Contragent) contragentCriteria.uniqueResult();
            idOfContragent = contragent.getIdOfContragent();

            r.id = idOfContragent;

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return r;
    }

    @Override
    public IdResult createPaymentOrder(@WebParam(name = "idOfClient") Long idOfClient,
            @WebParam(name = "idOfContragent") Long idOfContragent, @WebParam(name = "paymentMethod") int paymentMethod,
            @WebParam(name = "copecksAmount") Long copecksAmount,
            @WebParam(name = "contragentSum") Long contragentSum) {
        IdResult r = new IdResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;


        try {
            RuntimeContext runtimeContext = null;

            runtimeContext = RuntimeContext.getInstance();
            Long idOfClientPaymentOrder = runtimeContext.getClientPaymentOrderProcessor()
                    .createPaymentOrder(idOfClient, idOfContragent, paymentMethod, copecksAmount, contragentSum);
            r.id = idOfClientPaymentOrder;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;

        }
        return r;
    }


    @Override
    public Result changePaymentOrderStatus(@WebParam(name = "idOfClient") Long idOfClient,
            @WebParam(name = "idOfClientPaymentOrder") Long idOfClientPaymentOrder,
            @WebParam(name = "orderStatus") int orderStatus) {

        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        RuntimeContext runtimeContext = null;

        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        runtimeContext = RuntimeContext.getInstance();
        ClientPaymentOrderProcessor clientPaymentOrderProcessor = runtimeContext.getClientPaymentOrderProcessor();
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (orderStatus < ClientPaymentOrder.ORDER_STATUS_TRANSFER_ACCEPTED) {
                clientPaymentOrderProcessor.changePaymentOrderStatus(idOfClient, idOfClientPaymentOrder, orderStatus);
            } else {
                ClientPaymentOrder clientPaymentOrder = DAOUtils
                        .getClientPaymentOrderReference(persistenceSession, idOfClientPaymentOrder);
                clientPaymentOrderProcessor
                        .changePaymentOrderStatus(clientPaymentOrder.getContragent().getIdOfContragent(),
                                idOfClientPaymentOrder, orderStatus, clientPaymentOrder.getContragentSum(),
                                idOfClientPaymentOrder.toString(),
                                "changePaymentOrderStatus/".concat(idOfClientPaymentOrder.toString()));
            }
            persistenceSession.clear();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return r;

    }

    @Override
    public RBKMoneyConfigResult getRBKMoneyConfig() {
        RuntimeContext runtimeContext = null;
        RBKMoneyConfigResult r = new RBKMoneyConfigResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        runtimeContext = RuntimeContext.getInstance();
        RBKMoneyConfig rbkMoneyConfig = runtimeContext.getPartnerRbkMoneyConfig();
        RBKMoneyConfigExt rbkMoneyConfigExt = new RBKMoneyConfigExt();
        rbkMoneyConfigExt.setContragentName(rbkMoneyConfig.getContragentName());
        rbkMoneyConfigExt.setEshopId(rbkMoneyConfig.getEshopId());
        rbkMoneyConfigExt.setPurchaseUri(rbkMoneyConfig.getPurchaseUri().toString());
        rbkMoneyConfigExt.setRate(rbkMoneyConfig.getRate());
        rbkMoneyConfigExt.setSecretKey(rbkMoneyConfig.getSecretKey());
        rbkMoneyConfigExt.setServiceName(rbkMoneyConfig.getServiceName());
        rbkMoneyConfigExt.setShow(rbkMoneyConfig.getShow());
        r.rbkConfig = rbkMoneyConfigExt;
        return r;

    }

    @Override
    public ChronopayConfigResult getChronopayConfig() {
        RuntimeContext runtimeContext = null;
        ChronopayConfigResult r = new ChronopayConfigResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        runtimeContext = RuntimeContext.getInstance();
        ChronopayConfig chronopayConfig = runtimeContext.getPartnerChronopayConfig();
        ChronopayConfigExt chronopayConfigExt = new ChronopayConfigExt();
        chronopayConfigExt.setCallbackUrl(chronopayConfig.getCallbackUrl());
        chronopayConfigExt.setContragentName(chronopayConfig.getContragentName());
        chronopayConfigExt.setIp(chronopayConfig.getIp());
        chronopayConfigExt.setPurchaseUri(chronopayConfig.getPurchaseUri());
        chronopayConfigExt.setRate(chronopayConfig.getRate());
        chronopayConfigExt.setSharedSec(chronopayConfig.getSharedSec());
        chronopayConfigExt.setShow(chronopayConfig.getShow());

        r.chronopayConfig = chronopayConfigExt;
        return r;

    }

    @Override
    public ClientSmsListResult getClientSmsList(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(contractId);

        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        ClientSmsListResult r = new ClientSmsListResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Criteria clientCriteria = persistenceSession.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();

            Date nextToEndDate = DateUtils.addDays(endDate, 1);


            Criteria clientSmsCriteria = persistenceSession.createCriteria(ClientSms.class);
            clientSmsCriteria.add(Restrictions.ge("serviceSendTime", startDate));
            clientSmsCriteria.add(Restrictions.lt("serviceSendTime", nextToEndDate));
            clientSmsCriteria.add(Restrictions.eq("client", client));
            List clientSmsList = clientSmsCriteria.list();
            ClientSmsList clientSmsListR = new ClientSmsList();

            for (Object clientSmsObject : clientSmsList) {
                ClientSms clientSms = (ClientSms) clientSmsObject;
                AccountTransaction accountTransaction = clientSms.getTransaction();

                Sms sms = new Sms();

                sms.setDeliveryStatus(clientSms.getDeliveryStatus());
                sms.setPrice(clientSms.getPrice());

                sms.setContentsType(clientSms.getContentsType());

                GregorianCalendar greSendTime = new GregorianCalendar();
                greSendTime.setTime(clientSms.getServiceSendTime());
                XMLGregorianCalendar xmlSendTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(greSendTime);

                sms.setServiceSendTime(xmlSendTime);

                Long transactionSum = 0L;
                if (null != accountTransaction) {
                    transactionSum = accountTransaction.getTransactionSum();
                }

                sms.setTransactionSum(transactionSum);

                if (null != accountTransaction) {
                    Card card = accountTransaction.getCard();
                    if (null != card) {

                        sms.setCardNo(card.getCardNo());
                    }
                }
                clientSmsListR.getS().add(sms);


            }
            r.clientSmsList = clientSmsListR;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;

        }


        return r;
    }


    @Override
    public BanksData getBanks() {

        BanksData bd = new BanksData();
        bd.resultCode = RC_OK;
        bd.description = RC_OK_DESC;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;

        BanksList bankItemList = new BanksList();
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria banksCriteria = persistenceSession.createCriteria(Bank.class);

            List<Bank> banksList = (List<Bank>) banksCriteria.list();
            //List<BankItem>banks=new ArrayList<BankItem>();
            for (Bank bank : banksList) {
                BankItem bankItem = new BankItem();
                bankItem.setEnrollmentType(bank.getEnrollmentType());
                bankItem.setLogoUrl(bank.getLogoUrl());
                bankItem.setMinRate(bank.getMinRate());
                bankItem.setName(bank.getName());
                bankItem.setTerminalsUrl(bank.getTerminalsUrl());
                bankItem.setRate(bank.getRate());
                bankItem.setIdOfBank(bank.getIdOfBank());

                bankItemList.getBanks().add(bankItem);
            }

            bd.banksList = bankItemList;

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;


        } catch (Exception e) {
            bd.resultCode = RC_INTERNAL_ERROR;
            bd.description = RC_INTERNAL_ERROR_DESC;


            logger.error(e.getMessage(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return bd;

    }

    @Override
    public Result changePersonalInfo(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "limit") Long limit, @WebParam(name = "address") String address,
            @WebParam(name = "phone") String phone, @WebParam(name = "mobilePhone") String mobilePhone,
            @WebParam(name = "email") String email,
            @WebParam(name = "smsNotificationState") boolean smsNotificationState) {

        authenticateRequest(contractId);

        Result r = new Result(RC_OK, RC_OK_DESC);

        try {
            DAOService daoService = DAOService.getInstance();


            //change limit
            if (limit < 0) {
                r = new Result(RC_INVALID_DATA, "Лимит не может быть меньше нуля");
                return r;
            }
            long version = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
            if (!daoService.setClientExpenditureLimit(contractId, limit, version)) {
                r = new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }

            //change email
            if (!daoService.setClientEmail(contractId, email)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

            //change mobile phone
            mobilePhone = Client.checkAndConvertMobile(mobilePhone);
            if (mobilePhone == null) {
                r.resultCode = RC_INVALID_DATA;
                r.description = RC_INVALID_MOBILE;
                return r;
            }
            ClientsMobileHistory clientsMobileHistory =
                    new ClientsMobileHistory("soap метод changePersonalInfo");
            clientsMobileHistory.setShowing("Портал");
            if (!daoService.setClientMobilePhone(contractId, mobilePhone, null, clientsMobileHistory)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

            //enableNotificationBySms
            List<Long> list = new ArrayList<Long>();
            list.add(contractId);
            if (!daoService.enableClientNotificationBySMS(list, smsNotificationState)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }
            //change phone
            if (!daoService.setClientPhone(contractId, phone)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

            //change address
            if (!daoService.setClientAddress(contractId, address)) {
                r.resultCode = RC_CLIENT_NOT_FOUND;
                r.description = RC_CLIENT_NOT_FOUND_DESC;
            }

        } catch (Exception e) {
            logger.error("error in changePersonalInfo: ", e);
        }
        return r;
    }

    @WebMethod(operationName = "getHiddenPages")
    public HiddenPagesResult getHiddenPages() {
        RuntimeContext runtimeContext = null;
        HiddenPagesResult r = new HiddenPagesResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        runtimeContext = RuntimeContext.getInstance();
        String hiddenPages = runtimeContext
                .getPropertiesValue(RuntimeContext.PARAM_NAME_HIDDEN_PAGES_IN_CLIENT_ROOM, "");
        r.hiddenPages = hiddenPages;
        return r;
    }


    @Override
    public QuestionaryResultList getActiveMenuQuestions(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "currentDate") final Date currentDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                processQuestionaryList(client, data, objectFactory, session, currentDate, QuestionaryType.MENU);
            }
        });

        QuestionaryResultList questionaryResultList = new QuestionaryResultList();
        questionaryResultList.questionaryList = data.getQuestionaryList();
        questionaryResultList.resultCode = data.getResultCode();
        questionaryResultList.description = data.getDescription();
        return questionaryResultList;
    }

    @Override
    public Result setAnswerFromQuestion(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "IdOfAnswer") Long idOfAnswer) {
        authenticateRequest(contractId);
        Result r = new Result();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            QuestionaryService questionaryService = new QuestionaryService();
            questionaryService.registrationAnswerByClient(persistenceSession, contractId, idOfAnswer);

            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;
            logger.error(e.getMessage(), e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return r;
    }

    private void processQuestionaryList(Client client, Data data, ObjectFactory objectFactory, Session session,
            Date currentDate, QuestionaryType type) {
        DetachedCriteria orgCriteria = DetachedCriteria.forClass(Client.class).createAlias("org", "o", JoinType.NONE)
                .setProjection(Property.forName("org.idOfOrg"))
                .add(Restrictions.eq("contractId", client.getContractId()));
        Criteria questionaryCriteria = session.createCriteria(Questionary.class);
        questionaryCriteria.add(Restrictions.eq("status", QuestionaryStatus.START));
        questionaryCriteria.add(Restrictions.eq("questionaryType", type));
        questionaryCriteria.createAlias("orgs", "org", JoinType.INNER_JOIN);
        questionaryCriteria.add(Property.forName("org.idOfOrg").eq(orgCriteria));
        questionaryCriteria.add(Restrictions.ge("viewDate", currentDate));
        questionaryCriteria.addOrder(org.hibernate.criterion.Order.asc("viewDate"));
        questionaryCriteria.setMaxResults(24);

        List<Questionary> questionaries = questionaryCriteria.list();
        QuestionaryList questionaryList = objectFactory.createQuestionaryList();

        for (Questionary questionary : questionaries) {
            Criteria criteria = session.createCriteria(ClientAnswerByQuestionary.class);
            criteria.add(Restrictions.in("answer", questionary.getAnswers()));
            criteria.add(Restrictions.eq("client", client));
            List<ClientAnswerByQuestionary> list = (List<ClientAnswerByQuestionary>) criteria.list();
            QuestionaryItem questionaryItem = new QuestionaryItem(questionary);
            if (!list.isEmpty()) {
                questionaryItem.setCheckedAnswer(list.get(0).getAnswer().getIdOfAnswer());
            }
            questionaryItem.addAnswers(questionary.getAnswers());
            questionaryList.getQ().add(questionaryItem);
        }
        data.setQuestionaryList(questionaryList);
    }


    @Override
    public ClientNotificationSettingsResult getClientNotificationTypes() {

        ClientNotificationSettingsResult res = new ClientNotificationSettingsResult(RC_OK, RC_OK_DESC);

        List<ClientNotificationSettingsItem> list = new ArrayList<ClientNotificationSettingsItem>();
        try {
            for (ClientGuardianNotificationSetting.Predefined predef : ClientGuardianNotificationSetting.Predefined
                    .values()) {
                if (predef.getValue()
                        .equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                    continue;
                }
                ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                it.setTypeOfNotification(predef.getValue());
                it.setNameOfNotification(predef.getName());
                list.add(it);
            }
            res.setSettings(list);
        } catch (Exception e) {
            res.setResultCode(RC_INTERNAL_ERROR);
            res.setDescription(RC_INTERNAL_ERROR_DESC);
            logger.error(e.getMessage(), e);
        }
        return res;
    }

    @Override
    public ClientNotificationSettingsResult getClientGuardianNotificationSettings(
            @WebParam(name = "childContractId") Long childContractId,
            @WebParam(name = "guardianMobile") String guardianMobile) {
        authenticateRequest(childContractId);

        ClientNotificationSettingsResult res = new ClientNotificationSettingsResult(RC_OK, RC_OK_DESC);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;

        Set<ClientNotificationSettingsItem> set = new HashSet<ClientNotificationSettingsItem>();
        try {
            persistenceSession = runtimeContext.createReportPersistenceSession();
            Long childId = DAOReadonlyService.getInstance().getClientIdByContract(childContractId);

            if (childId == null) {
                res.setResultCode(RC_CLIENT_NOT_FOUND);
                res.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                return res;
            }

            List<Long> clientGuardians = DAOReadonlyService.getInstance()
                    .findClientGuardiansByMobile(childId, Client.checkAndConvertMobile(guardianMobile));
            if (clientGuardians == null || clientGuardians.size() == 0) {
                return getClientNotificationSettings(childContractId);
                /*res.setResultCode(RC_CLIENT_GUARDIAN_NOT_FOUND);
                res.setDescription(RC_CLIENT_GUARDIAN_NOT_FOUND_DESC);
                return res;*/
            }

            boolean clientHasCustomNotificationSettings = false;
            for (Long id : clientGuardians) {
                ClientGuardian clientGuardian = (ClientGuardian) persistenceSession.get(ClientGuardian.class, id);
                for (ClientGuardianNotificationSetting setting : clientGuardian.getNotificationSettings()) {
                    if (setting.getNotifyType()
                            .equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                        clientHasCustomNotificationSettings = true;
                        continue;
                    }
                    ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                    it.setTypeOfNotification(setting.getNotifyType());
                    it.setNameOfNotification(
                            ClientGuardianNotificationSetting.Predefined.parse(setting.getNotifyType()).getName());
                    set.add(it);
                }
                // если у клиента нет записи изменения настроек - выдаем настройки по умолчанию
                if (!clientHasCustomNotificationSettings) {
                    for (ClientGuardianNotificationSetting.Predefined predefined : ClientGuardianNotificationSetting.Predefined
                            .values()) {
                        if (predefined.isEnabledAtDefault()) {
                            ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                            it.setTypeOfNotification(predefined.getValue());
                            it.setNameOfNotification(predefined.getName());
                            set.add(it);
                        }
                    }
                }
            }
            List<ClientNotificationSettingsItem> list = new ArrayList<ClientNotificationSettingsItem>();
            for (ClientNotificationSettingsItem item : set) {
                list.add(item);
            }
            res.setSettings(list);
        } catch (Exception e) {
            res.setResultCode(RC_INTERNAL_ERROR);
            res.setDescription(RC_INTERNAL_ERROR_DESC);
            logger.error(e.getMessage(), e);
        } finally {
            HibernateUtils.close(persistenceSession, logger);
        }
        return res;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClientNotificationSettingsResult getClientNotificationSettings(
            @WebParam(name = "contractId") Long contractId) {

        authenticateRequest(contractId);

        ClientNotificationSettingsResult res = new ClientNotificationSettingsResult(RC_OK, RC_OK_DESC);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;

        List<ClientNotificationSettingsItem> list = new ArrayList<ClientNotificationSettingsItem>();
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            Client client = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (client == null) {
                res.setResultCode(RC_CLIENT_NOT_FOUND);
                res.setDescription(RC_CLIENT_NOT_FOUND_DESC);
                return res;
            }

            List<Client> guardians = ClientManager
                    .findGuardiansByClient(persistenceSession, client.getIdOfClient(), null);
            if (guardians.size() > 0) {
                retrieveNotificationsForClientGuardian(persistenceSession, list, client, guardians);
            } else {
                //----------------------------ниже старый код
                Set<ClientNotificationSettingsItem> set = new HashSet<ClientNotificationSettingsItem>();
                boolean clientHasCustomNotificationSettings = false;
                for (ClientNotificationSetting setting : client.getNotificationSettings()) {
                    if (setting.getNotifyType()
                            .equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                        clientHasCustomNotificationSettings = true;
                        continue;
                    }
                    ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                    it.setTypeOfNotification(setting.getNotifyType());
                    it.setNameOfNotification(
                            ClientNotificationSetting.Predefined.parse(setting.getNotifyType()).getName());
                    set.add(it);
                }
                // если у клиента нет записи изменения настроек - выдаем настройки по умолчанию
                if (!clientHasCustomNotificationSettings) {
                    for (ClientNotificationSetting.Predefined predefined : ClientNotificationSetting.Predefined
                            .values()) {
                        if (predefined.isEnabledAtDefault()) {
                            ClientNotificationSettingsItem it = new ClientNotificationSettingsItem();
                            it.setTypeOfNotification(predefined.getValue());
                            it.setNameOfNotification(predefined.getName());
                            set.add(it);
                        }
                    }
                }
                for (ClientNotificationSettingsItem item : set) {
                    list.add(item);
                }
            }
            res.setSettings(list);
        } catch (Exception e) {
            res.setResultCode(RC_INTERNAL_ERROR);
            res.setDescription(RC_INTERNAL_ERROR_DESC);
            logger.error(e.getMessage(), e);
        } finally {
            HibernateUtils.close(persistenceSession, logger);
        }
        return res;
    }

    private void retrieveNotificationsForClientGuardian(Session session, List<ClientNotificationSettingsItem> list,
            Client client, List<Client> guardians) {
        for (ClientGuardianNotificationSetting.Predefined pd : ClientGuardianNotificationSetting.Predefined.values()) {
            if (pd.getValue().equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            boolean found = false;
            for (Client guardian : guardians) {
                found = false;
                ClientGuardian clientGuardian = DAOReadonlyService.getInstance()
                        .findClientGuardianById(session, client.getIdOfClient(), guardian.getIdOfClient());
                if (clientGuardian == null) {
                    continue;
                }
                for (ClientGuardianNotificationSetting setting : clientGuardian.getNotificationSettings()) {
                    if (pd.getValue().equals(setting.getNotifyType())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    break;
                }
            }
            if (!found) {
                continue;
            } else {
                ClientNotificationSettingsItem item = new ClientNotificationSettingsItem();
                item.setNameOfNotification(pd.getName());
                item.setTypeOfNotification(pd.getValue());
                list.add(item);
            }
        }

    }

    @Override
    public ClientNotificationChangeResult setClientGuardianNotificationSettings(
            @WebParam(name = "childContractId") Long childContractId,
            @WebParam(name = "guardianMobile") String guardianMobile,
            @WebParam(name = "notificationType") List<Long> notificationTypes) {
        authenticateRequest(childContractId);

        ClientNotificationChangeResult res = new ClientNotificationChangeResult(RC_OK, RC_OK_DESC);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Long childId = DAOReadonlyService.getInstance().getClientIdByContract(childContractId);
            if (childId == null) {
                res.resultCode = RC_CLIENT_NOT_FOUND;
                res.description = RC_CLIENT_NOT_FOUND_DESC;
                return res;
            }

            List<Long> guardianIds = DAOReadonlyService.getInstance()
                    .findClientGuardiansByMobile(childId, Client.checkAndConvertMobile(guardianMobile));

            if (guardianIds == null || guardianIds.size() == 0) {
                return setClientNotificationSettings(childContractId,
                        notificationTypes); //Если нет представителей, работаем по старому алгоритму
            }
            for (Long id : guardianIds) {
                ClientGuardian clientGuardian = (ClientGuardian) persistenceSession.get(ClientGuardian.class, id);
                processNotificationsForClientGuardian(notificationTypes, clientGuardian);
                persistenceSession.update(clientGuardian);
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            res.resultCode = RC_INTERNAL_ERROR;
            res.description = RC_INTERNAL_ERROR_DESC;
            logger.error(e.getMessage(), e);
            HibernateUtils.rollback(persistenceTransaction, logger);
        } finally {
            HibernateUtils.close(persistenceSession, logger);
        }
        return res;
    }

    private void processNotificationsForClientGuardian(List<Long> notificationTypes, ClientGuardian clientGuardian)
            throws Exception {
        boolean pdFound;
        List<NotificationSettingItem> notificationItems = new ArrayList<NotificationSettingItem>();
        for (ClientGuardianNotificationSetting.Predefined pd : ClientGuardianNotificationSetting.Predefined.values()) {
            if (pd.getValue().equals(ClientGuardianNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                continue;
            }
            pdFound = false;
            for (Long notificationType : notificationTypes) {
                if (notificationType.equals(pd.getValue())) {
                    pdFound = true;
                    break;
                }
            }
            NotificationSettingItem item = new NotificationSettingItem(pd, pdFound);
            notificationItems.add(item);
        }

        ClientManager.attachNotifications(clientGuardian, notificationItems);
    }

    @Override
    public ClientNotificationChangeResult setClientNotificationSettings(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "notificationType") List<Long> notificationTypes) {
        authenticateRequest(contractId);

        ClientNotificationChangeResult res = new ClientNotificationChangeResult(RC_OK, RC_OK_DESC);
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Client client = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (client == null) {
                res.resultCode = RC_CLIENT_NOT_FOUND;
                res.description = RC_CLIENT_NOT_FOUND_DESC;
                return res;
            }
            List<Client> guardians = ClientManager
                    .findGuardiansByClient(persistenceSession, client.getIdOfClient(), null);
            for (Client guardian : guardians) {
                ClientGuardian clientGuardian = DAOReadonlyService.getInstance()
                        .findClientGuardianById(persistenceSession, client.getIdOfClient(), guardian.getIdOfClient());
                if (clientGuardian == null) {
                    continue;
                }
                processNotificationsForClientGuardian(notificationTypes, clientGuardian);
                persistenceSession.update(clientGuardian);
            }
            for (ClientNotificationSetting.Predefined pd : ClientNotificationSetting.Predefined.values()) {
                ClientNotificationSetting cns = new ClientNotificationSetting(client, pd.getValue());
                if ((notificationTypes != null && notificationTypes.contains(pd.getValue())) || pd.getValue()
                        .equals(ClientNotificationSetting.Predefined.SMS_SETTING_CHANGED.getValue())) {
                    client.getNotificationSettings().add(cns);
                } else {
                    client.getNotificationSettings().remove(cns);
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            res.resultCode = RC_INTERNAL_ERROR;
            res.description = RC_INTERNAL_ERROR_DESC;
            logger.error(e.getMessage(), e);
            HibernateUtils.rollback(persistenceTransaction, logger);
        } finally {
            HibernateUtils.close(persistenceSession, logger);
        }
        return res;
    }

    @Override
    public ClientConfirmPaymentData getStudentsByCanNotConfirmPayment(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ClientConfirmPaymentData studentsConfirmPaymentData = new ClientConfirmPaymentData();
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Client teacher = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (teacher == null) {
                studentsConfirmPaymentData.resultCode = RC_CLIENT_NOT_FOUND;
                studentsConfirmPaymentData.description = RC_CLIENT_NOT_FOUND_DESC;
                studentsConfirmPaymentData.studentsConfirmPaymentList = new StudentsConfirmPaymentList();
            } else {
                List<StudentMustPayItem> studentMustPayItemList = new ArrayList<StudentMustPayItem>();
                if (teacher.getCanConfirmGroupPayment()) {
                    List students = DAOUtils
                            .fetchStudentsByCanNotConfirmPayment(persistenceSession, teacher.getIdOfClient());
                    Long idOfClient = null;
                    Long sum = 0L;
                    for (Object object : students) {
                        Object[] student = (Object[]) object;
                        Long balance = Long.valueOf(String.valueOf(student[3]));
                        Long paySum = Long.valueOf(String.valueOf(student[4]));
                        Long currentIdOfClient = Long.valueOf(String.valueOf(student[6]));
                        if (idOfClient == null || (!idOfClient.equals(currentIdOfClient))) {
                            idOfClient = currentIdOfClient;
                            sum = 0L;
                        }
                        if (balance + sum < 0 && idOfClient.equals(currentIdOfClient)) {
                            sum += paySum;
                            StudentMustPayItem mustPayItem = new StudentMustPayItem();
                            mustPayItem.setFirstName(String.valueOf(student[0]));
                            mustPayItem.setSurname(String.valueOf(student[1]));
                            mustPayItem.setSecondName(String.valueOf(student[2]));
                            mustPayItem.setBalance(balance);
                            mustPayItem.setCreateTime((Date) student[5]);
                            mustPayItem.setPaySum(paySum);
                            studentMustPayItemList.add(mustPayItem);
                        }
                    }
                    studentsConfirmPaymentData.resultCode = RC_OK;
                    studentsConfirmPaymentData.description = RC_OK_DESC;
                    studentsConfirmPaymentData.studentsConfirmPaymentList = new StudentsConfirmPaymentList(
                            studentMustPayItemList);
                } else {
                    List students = DAOUtils.fetchTeacherByDoConfirmPayment(persistenceSession);
                    Long idOfClient = null;
                    Long sum = 0L;

                    for (Object object : students) {
                        Object[] student = (Object[]) object;
                        if (Long.valueOf(String.valueOf(student[7])).equals(teacher.getIdOfClient())) {
                            Long balance = Long.valueOf(String.valueOf(student[3]));
                            Long paySum = Long.valueOf(String.valueOf(student[4]));
                            Long currentIdOfClient = Long.valueOf(String.valueOf(student[6]));
                            if (idOfClient == null || (!idOfClient.equals(currentIdOfClient))) {
                                idOfClient = currentIdOfClient;
                                sum = 0L;
                            }
                            if (balance + sum < 0 && idOfClient.equals(currentIdOfClient)) {
                                sum += paySum;
                                StudentMustPayItem mustPayItem = new StudentMustPayItem();
                                Person person = teacher.getPerson();
                                mustPayItem.setFirstName(person.getFirstName());
                                mustPayItem.setSurname(person.getSurname());
                                mustPayItem.setSecondName(person.getSecondName());
                                mustPayItem.setBalance(balance);
                                mustPayItem.setCreateTime((Date) student[5]);
                                mustPayItem.setPaySum(paySum);
                                studentMustPayItemList.add(mustPayItem);
                            }
                        }
                    }
                }
                studentsConfirmPaymentData.resultCode = RC_OK;
                studentsConfirmPaymentData.description = RC_OK_DESC;
                studentsConfirmPaymentData.studentsConfirmPaymentList = new StudentsConfirmPaymentList(
                        studentMustPayItemList);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to Students By Can Not Confirm Payment settings", e);
            studentsConfirmPaymentData.resultCode = RC_INTERNAL_ERROR;
            studentsConfirmPaymentData.description = RC_INTERNAL_ERROR_DESC;
            studentsConfirmPaymentData.studentsConfirmPaymentList = new StudentsConfirmPaymentList();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
        return studentsConfirmPaymentData;
    }

    @Override
    public ClientStatsResult getClientStats(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
            @WebParam(name = "type") int type) {
        authenticateRequest(contractId);

        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        org.hibernate.Transaction persistenceTransaction = null;

        ClientStatsResult r = new ClientStatsResult();
        r.resultCode = RC_OK;
        r.description = RC_OK_DESC;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = findClientByContractId(persistenceSession, contractId, r);
            if (client == null) {
                return r;
            }

            r.stats = RuntimeContext.getAppContext().getBean(ClientStatsReporter.class)
                    .getStatsForClient(client, startDate, endDate);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            r.resultCode = RC_INTERNAL_ERROR;
            r.description = RC_INTERNAL_ERROR_DESC;

        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }


        return r;
    }

    @Override
    public ComplexInfoResult findComplexesWithSubFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "type") Integer type) {
        authenticateRequest(contractId);
        return findComplexesWithSubFeeding(contractId, null, type);
    }

    @Override
    public ComplexInfoResult findComplexesWithSubFeeding(@WebParam(name = "san") String san,
            @WebParam(name = "type") Integer type) {
        authenticateRequest(null);
        return findComplexesWithSubFeeding(null, san, type);
    }

    public ComplexInfoResult findComplexesWithSubFeeding(Long contractId, String san, Integer type) {
        Session session = null;
        Transaction transaction = null;
        ComplexInfoResult result = new ComplexInfoResult();
        try {
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                return result;
            }
            Org org = client.getOrg();

            boolean isParent = false;
            if (client.getClientGroup() != null) {
                final String groupName = client.getClientGroup().getGroupName();
                isParent = client.getIdOfClientGroup() >= ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES || ClientGroup
                        .predefinedGroupNames().contains(groupName);
            }
            boolean vp = (type == null ? false : type.equals(SubscriptionFeedingType.VARIABLE_TYPE.ordinal()));
            /*boolean vp = false;
            if (ArrayUtils.contains(getVPOrgsList(), org.getIdOfOrg())) {
                vp = true;
            }*/
            List<ComplexInfo> complexInfoList = DAOReadExternalsService.getInstance()
                    .findComplexesWithSubFeeding(org, isParent, vp);
            List<ComplexInfoExt> list = new ArrayList<ComplexInfoExt>();
            result.getComplexInfoList().setList(list);
            ObjectFactory objectFactory = new ObjectFactory();
            for (ComplexInfo ci : complexInfoList) {
                List<MenuItemExt> menuItemExtList = getMenuItemsExt(objectFactory, ci.getIdOfComplexInfo(), false);
                ComplexInfoExt complexInfoExt = new ComplexInfoExt(ci);
                complexInfoExt.setMenuItemExtList(menuItemExtList);
                list.add(complexInfoExt);
            }
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private List<MenuItemExt> getMenuItemsExt(ObjectFactory objectFactory, Long idOfComplexInfo, boolean isNotForMos) {
        List<MenuItemExt> menuItemExtList = new ArrayList<MenuItemExt>();
        List<MenuDetail> menuDetails = DAOReadExternalsService.getInstance()
                .getMenuDetailsByIdOfComplexInfo(idOfComplexInfo);
        for (MenuDetail menuDetail : menuDetails) {
            //Если не нужны Сотрудники и в названии группы встречается Сотрудник, то пропускаем такую запись
            if (!isNotForMos || (menuDetail.getGroupName().toUpperCase().indexOf(groupNotForMos.toUpperCase()) == -1)) {
                MenuItemExt menuItemExt = objectFactory.createMenuItemExt();
                menuItemExt.setGroup(menuDetail.getGroupName());
                menuItemExt.setName(menuDetail.getShortName());
                menuItemExt.setFullName(menuDetail.getMenuDetailName());
                menuItemExt.setPrice(menuDetail.getPrice());
                menuItemExt.setCalories(menuDetail.getCalories());
                menuItemExt.setVitB1(menuDetail.getVitB1());
                menuItemExt.setVitB2(menuDetail.getVitB2());
                menuItemExt.setVitPp(menuDetail.getVitPp());
                menuItemExt.setVitC(menuDetail.getVitC());
                menuItemExt.setVitA(menuDetail.getVitA());
                menuItemExt.setVitE(menuDetail.getVitE());
                menuItemExt.setMinCa(menuDetail.getMinCa());
                menuItemExt.setMinP(menuDetail.getMinP());
                menuItemExt.setMinMg(menuDetail.getMinMg());
                menuItemExt.setMinFe(menuDetail.getMinFe());
                menuItemExt.setOutput(menuDetail.getMenuDetailOutput());
                menuItemExt.setAvailableNow(menuDetail.getAvailableNow());
                menuItemExt.setProtein(menuDetail.getProtein());
                menuItemExt.setCarbohydrates(menuDetail.getCarbohydrates());
                menuItemExt.setFat(menuDetail.getFat());
                menuItemExt.setIdOfMenuDetail(menuDetail.getIdOfMenuDetail());
                menuItemExtList.add(menuItemExt);
            }
        }
        return menuItemExtList;
    }

    private List<MenuItemExt> getMenuItemsExt(ObjectFactory objectFactory, Long idOfOrg, List<WtDish> wtDishes) {
        List<MenuItemExt> menuItemExtList = new ArrayList<>();
        if (wtDishes != null && wtDishes.size() > 0) {
            for (WtDish wtDish : wtDishes) {
                WtDishInfo wtDishInfo = new WtDishInfo(wtDish);
                List<MenuItemExt> menuItemExt = getMenuItemExt(objectFactory, idOfOrg, wtDishInfo, true, null);
                menuItemExtList.addAll(menuItemExt);
            }
        }
        return menuItemExtList;
    }

    private List<MenuItemExt> getMenuItemExt(ObjectFactory objectFactory, Long idOfOrg, WtDishInfo wtDishInfo,
            boolean isGroupByCategory, Map<Long, Set<String>> menuGroups) {
        List<MenuItemExt> result = new ArrayList<>();
        Set<String> menuGroupSet = null;
        if (isGroupByCategory) {
            menuGroupSet = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getMenuGroupByWtDishAndCategories(wtDishInfo);
        } else {
            menuGroupSet = menuGroups.get(wtDishInfo.getIdOfDish());
        }
        for (String menuGroup : menuGroupSet) {
            MenuItemExt menuItemExt = objectFactory.createMenuItemExt();
            menuItemExt.setGroup(menuGroup);
            menuItemExt.setName(wtDishInfo.getDishName());
            menuItemExt.setPrice(wtDishInfo.getPrice());
            menuItemExt.setCalories(wtDishInfo.getCalories() == null ? (double) 0 : wtDishInfo.getCalories().doubleValue());
            menuItemExt.setOutput(wtDishInfo.getQty() == null ? "" : wtDishInfo.getQty());
            menuItemExt.setAvailableNow(1);
            menuItemExt.setCarbohydrates(wtDishInfo.getCarbohydrates() == null ? (double) 0 : wtDishInfo.getCarbohydrates().doubleValue());
            menuItemExt.setFat(wtDishInfo.getFat() == null ? (double) 0 : wtDishInfo.getFat().doubleValue());
            menuItemExt.setProtein(wtDishInfo.getProtein() == null ? (double) 0 : wtDishInfo.getProtein().doubleValue());
            menuItemExt.setVitB1(0.0);
            menuItemExt.setVitB2(0.0);
            menuItemExt.setVitPp(0.0);
            menuItemExt.setVitC(0.0);
            menuItemExt.setVitA(0.0);
            menuItemExt.setVitE(0.0);
            menuItemExt.setMinCa(0.0);
            menuItemExt.setMinP(0.0);
            menuItemExt.setMinMg(0.0);
            menuItemExt.setMinFe(0.0);
            menuItemExt.setIdOfMenuDetail(wtDishInfo.getIdOfDish());
            menuItemExt.setFullName(wtDishInfo.getComponentsOfDish());
            result.add(menuItemExt);
        }
        return result;
    }


    private <T extends Result> Client findClient(Session session, Long contractId, String san, final T res)
            throws Exception {
        if (contractId != null) {
            return findClientByContractId(session, contractId, res);
        } else if (san != null) {
            return findClientBySan(session, san, res);
        } else {
            return null;
        }
    }

    private <T extends Result> Client findClientByContractId(Session session, Long contractId, final T res)
            throws Exception {
        Client client = DAOUtils.findClientByContractId(session, contractId);
        if (client == null) {
            res.resultCode = RC_CLIENT_NOT_FOUND;
            res.description = RC_CLIENT_NOT_FOUND_DESC;
        }
        return client;
    }

    private <T extends Result> Client findClientBySan(Session session, String san, final T res) throws Exception {
        List<Client> clientList = DAOUtils.findClientBySan(session, san);
        if (clientList.isEmpty()) {
            res.resultCode = RC_CLIENT_NOT_FOUND;
            res.description = RC_CLIENT_NOT_FOUND_DESC;
            return null;
        }
        if (clientList.size() > 1) {
            res.resultCode = RC_SEVERAL_CLIENTS_WERE_FOUND;
            res.description = RC_SEVERAL_CLIENTS_WERE_FOUND_DESC;
            return null;
        }
        return clientList.get(0);
    }


    @Override
    public TransferSubBalanceListResult getTransferSubBalanceList(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(contractId);
        Session session = null;
        Transaction transaction = null;
        TransferSubBalanceListResult result = new TransferSubBalanceListResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Criteria criteria = session.createCriteria(SubAccountTransfer.class);
            criteria.add(Restrictions.eq("clientTransfer", client));
            criteria.add(Restrictions.between("createTime", startDate, endDate));
            criteria.setMaxResults(MAX_RECS);
            List list = criteria.list();
            List<TransferSubBalanceExt> t = result.transferSubBalanceListExt.getT();
            for (Object obj : list) {
                SubAccountTransfer subAccountTransfer = (SubAccountTransfer) obj;
                TransferSubBalanceExt transferSubBalance = new TransferSubBalanceExt();
                transferSubBalance.setCreateTime(subAccountTransfer.getCreateTime());
                transferSubBalance.setBalanceBenefactor(subAccountTransfer.getBalanceBenefactor());
                transferSubBalance.setBalanceBeneficiary(subAccountTransfer.getBalanceBeneficiary());
                transferSubBalance.setTransferSum(subAccountTransfer.getTransferSum());
                t.add(transferSubBalance);
            }
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public TransferSubBalanceListResult getTransferSubBalanceList(@WebParam(name = "san") String san,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(null);
        Session session = null;
        Transaction transaction = null;
        TransferSubBalanceListResult result = new TransferSubBalanceListResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, null, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Criteria criteria = session.createCriteria(SubAccountTransfer.class);
            criteria.add(Restrictions.eq("clientTransfer", client));
            criteria.add(Restrictions.between("createTime", startDate, endDate));
            criteria.setMaxResults(MAX_RECS);
            List list = criteria.list();
            List<TransferSubBalanceExt> t = result.transferSubBalanceListExt.getT();
            for (Object obj : list) {
                SubAccountTransfer subAccountTransfer = (SubAccountTransfer) obj;
                TransferSubBalanceExt transferSubBalance = new TransferSubBalanceExt();
                transferSubBalance.setCreateTime(subAccountTransfer.getCreateTime());
                transferSubBalance.setBalanceBenefactor(subAccountTransfer.getBalanceBenefactor());
                transferSubBalance.setBalanceBeneficiary(subAccountTransfer.getBalanceBeneficiary());
                transferSubBalance.setTransferSum(subAccountTransfer.getTransferSum());
                t.add(transferSubBalance);
            }
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }


    @Override
    public SubscriptionFeedingSettingResult getSubscriptionFeedingSetting(
            @WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        Session session = null;
        Transaction transaction = null;
        SubscriptionFeedingSettingResult result = new SubscriptionFeedingSettingResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Criteria criteria = session.createCriteria(ECafeSettings.class);
            final Org clientOrg = client.getOrg();
            final Long idOfOrg = clientOrg.getIdOfOrg();
            criteria.add(Restrictions.eq("orgOwner", idOfOrg));
            criteria.add(Restrictions.eq("settingsId", SettingsIds.SubscriberFeeding));
            criteria.add(Restrictions.eq("deletedState", false));
            List list = criteria.list();
            transaction.commit();
            if (list == null || list.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = "Отсутствуют настройки абонементного питания для организации " + clientOrg
                        .getShortNameInfoService();
                return result;
            }
            if (list.size() > 1) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = "Организация имеет более одной настройки " + clientOrg.getShortNameInfoService();
                return result;
            }
            ECafeSettings settings = (ECafeSettings) list.get(0);
            SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) settings
                    .getSplitSettingValue();
            SubscriptionFeedingSettingExt settingExt = new SubscriptionFeedingSettingExt();
            settingExt.setDayDeActivate(parser.getDayDeActivate());
            int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = 0;
            if (hoursForbidChange < 24) {
                dayForbidChange++;
            } else {
                dayForbidChange = (hoursForbidChange % 24 == 0 ? hoursForbidChange / 24 : hoursForbidChange / 24 + 1);
            }
            settingExt.setDayForbidChange(dayForbidChange);
            settingExt.setDayRequest(parser.getDayRequest());
            settingExt.setEnableFeeding(parser.isEnableFeeding());
            settingExt.setSixWorkWeek(parser.isSixWorkWeek());
            settingExt.setDaysToForbidChangeInPos(parser.getDaysToForbidChangeInPos());
            settingExt.setDayCreateVP(parser.getDayCreateVP());
            settingExt.setHoursForbidVP(parser.getHoursForbidVP());
            result.subscriptionFeedingSettingExt = settingExt;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public SubscriptionFeedingSettingResult getSubscriptionFeedingSetting(@WebParam(name = "san") String san) {
        authenticateRequest(null);
        Session session = null;
        Transaction transaction = null;
        SubscriptionFeedingSettingResult result = new SubscriptionFeedingSettingResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, null, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Criteria criteria = session.createCriteria(ECafeSettings.class);
            final Long idOfOrg = client.getOrg().getIdOfOrg();
            criteria.add(Restrictions.eq("orgOwner", idOfOrg));
            criteria.add(Restrictions.eq("settingsId", SettingsIds.SubscriberFeeding));
            List list = criteria.list();
            transaction.commit();
            transaction = null;
            if (list == null || list.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                        .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                                client.getOrg().getShortNameInfoService(), idOfOrg);
                return result;
            }
            if (list.size() > 1) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String.format("Организация имеет более одной настройки %s (IdOfOrg = %s)",
                        client.getOrg().getShortNameInfoService(), idOfOrg);
                return result;
            }
            ECafeSettings settings = (ECafeSettings) list.get(0);
            SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) settings
                    .getSplitSettingValue();
            SubscriptionFeedingSettingExt settingExt = new SubscriptionFeedingSettingExt();
            settingExt.setDayDeActivate(parser.getDayDeActivate());
            int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = 0;
            if (hoursForbidChange < 24) {
                dayForbidChange++;
            } else {
                dayForbidChange = (hoursForbidChange % 24 == 0 ? hoursForbidChange / 24 : hoursForbidChange / 24 + 1);
            }
            settingExt.setDayForbidChange(dayForbidChange);
            settingExt.setDayRequest(parser.getDayRequest());
            settingExt.setEnableFeeding(parser.isEnableFeeding());
            settingExt.setSixWorkWeek(parser.isSixWorkWeek());
            settingExt.setDaysToForbidChangeInPos(parser.getDaysToForbidChangeInPos());
            result.subscriptionFeedingSettingExt = settingExt;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public SubscriptionFeedingResult getCurrentSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "currentDay") Date currentDay, @WebParam(name = "type") Integer type) {
        authenticateRequest(contractId);
        return getCurrentSubscriptionFeeding(contractId, null, currentDay, type);
    }

    @Override
    public SubscriptionFeedingResult getCurrentSubscriptionFeeding(@WebParam(name = "san") String san,
            @WebParam(name = "currentDay") Date currentDay, @WebParam(name = "type") Integer type) {
        authenticateRequest(null);
        return getCurrentSubscriptionFeeding(null, san, currentDay, type);
    }

    private SubscriptionFeedingResult getCurrentSubscriptionFeeding(Long contractId, String san, Date currentDay,
            Integer type) {
        Session session = null;
        Transaction transaction = null;
        SubscriptionFeedingResult result = new SubscriptionFeedingResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            transaction.commit();
            transaction = null;
            SubscriptionFeedingService service = SubscriptionFeedingService.getInstance();
            SubscriptionFeeding sf = service
                    .getCurrentSubscriptionFeedingByClientToDay(session, client, currentDay, type);
            if (sf == null) {
                result.resultCode = RC_SUBSCRIPTION_FEEDING_NOT_FOUND;
                result.description = RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC;
            } else {
                result.setSubscriptionFeedingExt(new SubscriptionFeedingExt(sf));
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

        return result;
    }

/*    //@Override
    public SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(
          @WebParam(name = "contractId") Long contractId, @WebParam(name = "startDate") Date startDate,
          @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(contractId);
        return getSubscriptionFeedingHistoryList(contractId, null, startDate, endDate);
    }*/

/*    //@Override
    public SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(@WebParam(name = "san") String san,
          @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate) {
        authenticateRequest(null);
        return getSubscriptionFeedingHistoryList(null, san, startDate, endDate);
    }*/

/*    private SubscriptionFeedingListResult getSubscriptionFeedingHistoryList(Long contractId, String san, Date startDate,
          Date endDate) {
        Session session = null;
        Transaction transaction = null;
        SubscriptionFeedingListResult result = new SubscriptionFeedingListResult();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            transaction.commit();
            SubscriptionFeedingService subscriptionFeedingService = SubscriptionFeedingService.getInstance();
            List<SubscriptionFeeding> subscriptionFeedings = subscriptionFeedingService
                  .findSubscriptionFeedingByClient(client, startDate, endDate);
            for (SubscriptionFeeding subscriptionFeeding : subscriptionFeedings) {
                result.subscriptionFeedingListExt.getS().add(new SubscriptionFeedingExt(subscriptionFeeding));
            }

            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }

        return result;
    }*/

    @Override
    public SubscriptionFeedingJournalResult getSubscriptionFeedingJournal(Long contractId, Date startDate,
            Date endDate) {
        Session session = null;
        Transaction transaction = null;
        SubscriptionFeedingJournalResult result = new SubscriptionFeedingJournalResult();

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            transaction.commit();
            transaction = null;
            SubscriptionFeedingService subscriptionFeedingService = SubscriptionFeedingService.getInstance();
            List<SubscriptionFeeding> subscriptionFeedings = subscriptionFeedingService
                    .findSubscriptionFeedingByClient(client, startDate, endDate);
            for (SubscriptionFeeding subscriptionFeeding : subscriptionFeedings) {
                result.subscriptionFeedingJournalListExt.getS()
                        .add(new SubscriptionFeedingJournalExt(subscriptionFeeding));
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

        return result;
    }

    @Override
    public CycleDiagramList getCycleDiagramHistoryList(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
            @WebParam(name = "type") Integer type) {
        authenticateRequest(contractId);
        return getCycleDiagramHistoryList(contractId, null, startDate, endDate, type);
    }

    @Override
    public CycleDiagramList getCycleDiagramHistoryList(@WebParam(name = "san") String san,
            @WebParam(name = "startDate") Date startDate, @WebParam(name = "endDate") Date endDate,
            @WebParam(name = "type") Integer type) {
        authenticateRequest(null);
        return getCycleDiagramHistoryList(null, san, startDate, endDate, type);
    }

    private CycleDiagramList getCycleDiagramHistoryList(Long contractId, String san, Date startDate, Date endDate,
            Integer type) {
        Session session = null;
        Transaction transaction = null;
        CycleDiagramList result = new CycleDiagramList();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            SubscriptionFeedingService service = SubscriptionFeedingService.getInstance();
            List<CycleDiagram> cycleDiagrams = service.findCycleDiagramsByClient(client, startDate, endDate, type);
            for (CycleDiagram cycleDiagram : cycleDiagrams) {
                result.cycleDiagramListExt.getC().add(new CycleDiagramOut(cycleDiagram));
            }
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public CycleDiagramList getCycleDiagramList(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "type") Integer type) {
        authenticateRequest(contractId);
        return getCycleDiagramList(contractId, null, type);
    }

    @Override
    public CycleDiagramList getCycleDiagramList(@WebParam(name = "san") String san,
            @WebParam(name = "type") Integer type) {
        authenticateRequest(null);
        return getCycleDiagramList(null, san, type);
    }

    private CycleDiagramList getCycleDiagramList(Long contractId, String san, Integer type) {
        Session session = null;
        Transaction transaction = null;
        CycleDiagramList result = new CycleDiagramList();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            transaction.commit();
            transaction = null;
            SubscriptionFeedingService service = SubscriptionFeedingService.getInstance();
            List<CycleDiagram> cycleDiagrams = service.findCycleDiagramsByClient(client, type);
            for (CycleDiagram cycleDiagram : cycleDiagrams) {
                result.cycleDiagramListExt.getC().add(new CycleDiagramOut(cycleDiagram));
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

/*    @Override
    public Result activateSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram) {
        authenticateRequest(contractId);
        return activateSubscriptionFeeding(contractId, null, cycleDiagram);
    }*/

    @Override
    public Result activateCurrentSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "dateActivateSubscription") Date dateActivateSubscription) {
        authenticateRequest(contractId);
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        DateFormat df = CalendarUtils.getDateFormatLocal();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                    .geteCafeSettingses(client.getOrg().getIdOfOrg(), SettingsIds.SubscriberFeeding, false);
            if (settings.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                        .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                                client.getOrg().getShortNameInfoService(), client.getOrg().getIdOfOrg());
                return result;
            }
            Date dateActivateSubs = CalendarUtils.truncateToDayOfMonth(dateActivateSubscription);
            SubscriptionFeeding subscriptionFeeding = SubscriptionFeedingService
                    .getCurrentSubscriptionFeedingByClientToDay(session, client, dateActivateSubs, null);
            if (subscriptionFeeding == null) {
                result.resultCode = RC_SUBSCRIPTION_FEEDING_NOT_FOUND;
                result.description = RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC;
                return result;
            }
            if (subscriptionFeeding.getDateActivateSubscription() == null) {
                ECafeSettings cafeSettings = settings.get(0);
                SubscriberFeedingSettingSettingValue parser;
                parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
                Date dayForbid = subscriptionFeeding.getFirstDateCanChangeRegister(parser);
                if (dateActivateSubscription.getTime() < dayForbid.getTime()) {
                    result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                    result.description =
                            "Неверная дата активация подписки (" + dateActivateSubscription.toString() + " < "
                                    + dayForbid.toString() + ")";
                    return result;
                }
                subscriptionFeeding.setDateActivateSubscription(dateActivateSubscription);
                session.save(subscriptionFeeding);
                transaction.commit();
                transaction = null;
                result.resultCode = RC_OK;
                result.description = String.format("Подписка успешно активирована, начнет действовать после " + df
                        .format(dateActivateSubscription));
                return result;
            } else {
                result.resultCode = RC_SUBSCRIPTION_FEEDING_DUPLICATE;
                result.description = String.format("Подписка уже активирована (дата ее активации " + df
                        .format(subscriptionFeeding.getDateActivateSubscription()) + ")");
                return result;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

/*    @Override
    public Result activateSubscriptionFeeding(@WebParam(name = "san") String san,
          @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram) {
        authenticateRequest(null);
        return activateSubscriptionFeeding(null, san, cycleDiagram);
    }*/

    /*private Result activateSubscriptionFeeding(Long contractId, String san, CycleDiagramExt cycleDiagram) {
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, null, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Org clientOrg = client.getOrg();
            Date currentDate = new Date();
            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                  .geteCafeSettingses(client.getOrg().getIdOfOrg(), SettingsIds.SubscriberFeeding, false);
            if (settings.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                      .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                            client.getOrg().getShortName(), client.getOrg().getIdOfOrg());
                return result;
            }
            SubscriptionFeeding subscriptionFeeding =
                  SubscriptionFeedingService.getCurrentSubscriptionFeedingByClientToDay(session, client, currentDate);
            if(subscriptionFeeding==null){
                result.resultCode = RC_SUBSCRIPTION_FEEDING_NOT_FOUND;
                result.description = RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC;
                return result;
            }
            ECafeSettings cafeSettings = settings.get(0);
            SubscriberFeedingSettingSettingValue parser;
            parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();

            //Date dayForbid = GetFirstCanChangeSF(settings);

            final int hoursForbidChange = parser.getHoursForbidChange();
            int dayForbidChange = (hoursForbidChange %24==0? hoursForbidChange /24: hoursForbidChange /24+1);
            Date dayForbid = CalendarUtils.addDays(currentDate, dayForbidChange);
            if(dayForbid.getHours()>=12){
                dayForbid = CalendarUtils.addOneDay(currentDate);
            }
            Date activateDate = cycleDiagram.getDateActivationDiagram();
            if(activateDate.getTime()<dayForbid.getTime()){
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Неверная дата активация подписки";
                return result;
            }

            SubscriptionFeeding sf = new SubscriptionFeeding();
            sf.setCreatedDate(new Date());
            sf.fill(subscriptionFeeding);
            sf.setDateActivateSubscription(activateDate);
            sf.setStaff(null);
            sf.setDeletedState(false);
            Long sfVersion = daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName());
            sf.setGlobalVersionOnCreate(sfVersion);
            sf.setGlobalVersion(sfVersion);
            Criteria criteria = session.createCriteria(CycleDiagram.class);
            criteria.add(Restrictions.eq("client", client));
            criteria.add(Restrictions.eq("dateActivationDiagram", cycleDiagram.getDateActivationDiagram()));
            List list = criteria.list();
            SubscriptionFeedingService sfService = SubscriptionFeedingService.getInstance();
            if (list.isEmpty()) {
                // создаем новую
                CycleDiagram diagram = new CycleDiagram();
                diagram.setCreatedDate(new Date());
                diagram.setClient(client);
                diagram.setOrgOwner(clientOrg.getIdOfOrg());
                diagram.setIdOfClient(client.getIdOfClient());
                diagram.setDateActivationDiagram(cycleDiagram.getDateActivationDiagram());
                diagram.setStateDiagram(StateDiagram.WAIT);
                diagram.setDeletedState(false);
                diagram.setSendAll(SendToAssociatedOrgs.SendToSelf);
                Long version = DAOService.getInstance()
                      .updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName());
                diagram.setGlobalVersionOnCreate(version);
                diagram.setGlobalVersion(version);
                List<ComplexInfo> availableComplexes = sfService.findComplexesWithSubFeeding(clientOrg);
                diagram.setMonday(cycleDiagram.getMonday());
                diagram.setMondayPrice(sfService.getPriceOfDay(cycleDiagram.getMonday(), availableComplexes));
                diagram.setTuesday(cycleDiagram.getTuesday());
                diagram.setTuesdayPrice(sfService.getPriceOfDay(cycleDiagram.getTuesday(), availableComplexes));
                diagram.setWednesday(cycleDiagram.getWednesday());
                diagram.setWednesdayPrice(
                      sfService.getPriceOfDay(cycleDiagram.getWednesday(), availableComplexes));
                diagram.setThursday(cycleDiagram.getThursday());
                diagram.setThursdayPrice(
                      sfService.getPriceOfDay(cycleDiagram.getThursday(), availableComplexes));
                diagram.setFriday(diagram.getFriday());
                diagram.setFridayPrice(sfService.getPriceOfDay(cycleDiagram.getFriday(), availableComplexes));
                diagram.setSaturday(cycleDiagram.getSaturday());
                diagram.setSaturdayPrice(
                      sfService.getPriceOfDay(cycleDiagram.getSaturday(), availableComplexes));
                diagram.setSunday("");
                diagram.setSundayPrice(0L);
                diagram.setStaff(null);
                session.save(diagram);
            } else {
                // изменяем те что есть
                for (Object obj : list) {
                    CycleDiagram diagram = (CycleDiagram) obj;
                    diagram.setDateActivationDiagram(cycleDiagram.getDateActivationDiagram());
                    diagram.setStateDiagram(StateDiagram.WAIT);
                    diagram.setDeletedState(false);
                    diagram.setSendAll(SendToAssociatedOrgs.SendToSelf);
                    Long version = DAOService.getInstance()
                          .updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName());
                    //diagram.setGlobalVersionOnCreate(version);
                    diagram.setGlobalVersion(version);
                    List<ComplexInfo> availableComplexes = sfService.findComplexesWithSubFeeding(clientOrg);
                    diagram.setMonday(cycleDiagram.getMonday());
                    diagram.setMondayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getMonday(), availableComplexes));
                    diagram.setTuesday(cycleDiagram.getTuesday());
                    diagram.setTuesdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getTuesday(), availableComplexes));
                    diagram.setWednesday(cycleDiagram.getWednesday());
                    diagram.setWednesdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getWednesday(), availableComplexes));
                    diagram.setThursday(cycleDiagram.getThursday());
                    diagram.setThursdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getThursday(), availableComplexes));
                    diagram.setFriday(cycleDiagram.getFriday());
                    diagram.setFridayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getFriday(), availableComplexes));
                    diagram.setSaturday(cycleDiagram.getSaturday());
                    diagram.setSaturdayPrice(
                          sfService.getPriceOfDay(cycleDiagram.getSaturday(), availableComplexes));
                    diagram.setSunday("");
                    diagram.setSundayPrice(0L);
                    session.save(diagram);
                }
            }
            session.save(sf);
            transaction.commit();
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            HibernateUtils.rollback(transaction, logger);
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.close(session, logger);
        }
        return result;
    }*/

    @Override
    public Result suspendSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "suspendDate") Date suspendDate) {
        authenticateRequest(contractId);
        return suspendSubscriptionFeeding(contractId, null, suspendDate);
    }

    @Override
    public Result suspendSubscriptionFeeding(@WebParam(name = "san") String san,
            @WebParam(name = "suspendDate") Date suspendDate) {
        authenticateRequest(null);
        return suspendSubscriptionFeeding(null, san, suspendDate);
    }

    private Result suspendSubscriptionFeeding(Long contractId, String san, Date suspendDate) {
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            SubscriptionFeeding subscriptionFeeding = SubscriptionFeedingService
                    .getCurrentSubscriptionFeedingByClientToDay(session, client, suspendDate, null);
            if (subscriptionFeeding.getFeedingType().equals(SubscriptionFeedingType.VARIABLE_TYPE)) {
                result.resultCode = RC_INVALID_OPERATION_VARIABLE_FEEDING;
                result.description = RC_INVALID_OPERATION_VARIABLE_FEEDING_DESC;
                return result;
            }

            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                    .geteCafeSettingses(subscriptionFeeding.getOrgOwner(), SettingsIds.SubscriberFeeding, false);
            if (settings.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                        .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                                client.getOrg().getShortNameInfoService(), subscriptionFeeding.getOrgOwner());
                return result;
            }
            ECafeSettings cafeSettings = settings.get(0);
            SubscriberFeedingSettingSettingValue parser;
            parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
            Date dayForbid = subscriptionFeeding.getFirstDateCanChangeRegister(parser, new Date());
            if (suspendDate.getTime() < dayForbid.getTime()) {
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Неверная дата приостановки подписки";
                return result;
            }
            subscriptionFeeding.setLastDatePauseSubscription(suspendDate);
            subscriptionFeeding.setWasSuspended(true);
            subscriptionFeeding.setGlobalVersion(
                    daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName()));
            subscriptionFeeding.setLastUpdate(new Date());
            session.saveOrUpdate(subscriptionFeeding);
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result reopenSubscriptionFeeding(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "activateDate") Date activateDate) {
        authenticateRequest(contractId);
        return reopenSubscriptionFeeding(contractId, null, activateDate);
    }

    @Override
    public Result reopenSubscriptionFeeding(@WebParam(name = "san") String san,
            @WebParam(name = "activateDate") Date activateDate) {
        authenticateRequest(null);
        return reopenSubscriptionFeeding(null, san, activateDate);
    }

    private Result reopenSubscriptionFeeding(Long contractId, String san, Date activateDate) {
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            SubscriptionFeeding subscriptionFeeding = SubscriptionFeedingService
                    .getCurrentSubscriptionFeedingByClientToDay(session, client, activateDate, null);
            DAOService daoService = DAOService.getInstance();
            List<ECafeSettings> settings = daoService
                    .geteCafeSettingses(subscriptionFeeding.getOrgOwner(), SettingsIds.SubscriberFeeding, false);
            if (settings.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                        .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                                client.getOrg().getShortNameInfoService(), client.getOrg().getIdOfOrg());
                return result;
            }
            ECafeSettings cafeSettings = settings.get(0);
            SubscriberFeedingSettingSettingValue parser;
            parser = (SubscriberFeedingSettingSettingValue) cafeSettings.getSplitSettingValue();
            Date dayForbid = subscriptionFeeding.getFirstDateCanChangeRegister(parser, new Date());
            if (activateDate.getTime() < dayForbid.getTime()) {
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Неправильная дата возобновления подписки";
                return result;
            }
            Date currentDate = new Date();
            if (subscriptionFeeding.getDateActivateSubscription() == null
                    && subscriptionFeeding.getLastDatePauseSubscription() == null
                    && subscriptionFeeding.getWasSuspended() == false) {

                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Ошибка возобновления подписки в статусе \"Создана\"";
                return result;
            }
            if (subscriptionFeeding.getDateActivateSubscription() != null
                    && subscriptionFeeding.getLastDatePauseSubscription() == null
                    && subscriptionFeeding.getWasSuspended() == false
                    && subscriptionFeeding.getDateActivateSubscription().getTime() > currentDate.getTime()) {
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Ошибка возобновления подписки в статусе \"Ожидает активации\"";
                return result;
            }
            if (subscriptionFeeding.getDateActivateSubscription() != null
                    && subscriptionFeeding.getLastDatePauseSubscription() == null
                    && subscriptionFeeding.getWasSuspended() == false
                    && subscriptionFeeding.getDateActivateSubscription().getTime() <= currentDate.getTime()) {
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = "Ошибка возобновления подписки в статусе \"Активна\"";
                return result;
            }
            SubscriptionFeeding sf = new SubscriptionFeeding();
            sf.setCreatedDate(new Date());
            sf.setLastUpdate(new Date());
            sf.fill(subscriptionFeeding);
            sf.setDateActivateSubscription(activateDate);
            sf.setLastDatePauseSubscription(null);
            sf.setWasSuspended(false);
            sf.setStaff(null);
            sf.setDeletedState(false);
            Long version = daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName());
            sf.setGlobalVersionOnCreate(version);
            sf.setGlobalVersion(version);
            session.persist(sf);
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result cancelSubscriptionFeeding(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        return cancelSubscriptionFeeding(contractId, null);
    }

    @Override
    public Result cancelSubscriptionFeeding(@WebParam(name = "san") String san) {
        authenticateRequest(null);
        return cancelSubscriptionFeeding(null, san);
    }

    private Result cancelSubscriptionFeeding(Long contractId, String san) {
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            SubscriptionFeeding subscriptionFeeding = SubscriptionFeedingService
                    .getCurrentSubscriptionFeedingByClientToDay(session, client, new Date(), null);
            if (subscriptionFeeding == null) {
                result.resultCode = RC_SUBSCRIPTION_FEEDING_NOT_FOUND;
                result.description = RC_SUBSCRIPTION_FEEDING_NOT_FOUND_DESC;
                return result;
            }
            DAOService daoService = DAOService.getInstance();
            subscriptionFeeding.setWasSuspended(false);
            subscriptionFeeding.setLastDatePauseSubscription(null);
            subscriptionFeeding.setGlobalVersion(
                    daoService.updateVersionByDistributedObjects(SubscriptionFeeding.class.getSimpleName()));
            subscriptionFeeding.setLastUpdate(new Date());
            session.saveOrUpdate(subscriptionFeeding);
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result putCycleDiagram(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram, @WebParam(name = "type") Integer type,
            @WebParam(name = "startWeekPosition") Integer startWeekPosition) {
        authenticateRequest(contractId);
        return putCycleDiagram(contractId, null, cycleDiagram, type, startWeekPosition);
    }

    @Override
    public Result putCycleDiagram(@WebParam(name = "san") String san,
            @WebParam(name = "cycleDiagram") CycleDiagramExt cycleDiagram, @WebParam(name = "type") Integer type,
            @WebParam(name = "startWeekPosition") Integer startWeekPosition) {
        authenticateRequest(null);
        return putCycleDiagram(null, san, cycleDiagram, type, startWeekPosition);
    }

    private Result putCycleDiagram(Long contractId, String san, CycleDiagramExt cycleDiagram, Integer type,
            Integer startWeekPosition) {
        Session session = null;
        Transaction transaction = null;
        Result result = new Result();
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = findClient(session, contractId, san, result);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Criteria settingCriteria = session.createCriteria(ECafeSettings.class);
            final Org clientOrg = client.getOrg();
            final Long idOfOrg = clientOrg.getIdOfOrg();
            settingCriteria.add(Restrictions.eq("orgOwner", idOfOrg));
            settingCriteria.add(Restrictions.eq("settingsId", SettingsIds.SubscriberFeeding));
            List settingsList = settingCriteria.list();
            if (settingsList.isEmpty()) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String
                        .format("Отсутствуют настройки абонементного питания для организации %s (IdOfOrg = %s)",
                                clientOrg.getShortNameInfoService(), idOfOrg);
                return result;
            }
            if (settingsList.size() > 1) {
                result.resultCode = RC_SETTINGS_NOT_FOUND;
                result.description = String.format("Организация имеет более одной настройки %s (IdOfOrg = %s)",
                        clientOrg.getShortNameInfoService(), idOfOrg);
                return result;
            }
            ECafeSettings settings = (ECafeSettings) settingsList.get(0);
            SubscriberFeedingSettingSettingValue parser = (SubscriberFeedingSettingSettingValue) settings
                    .getSplitSettingValue();
            Date dayForbid = SubscriptionFeeding.getFirstDateCanChangeRegister(parser, new Date());
            if (cycleDiagram.getDateActivationDiagram().getTime() < dayForbid.getTime()) {
                result.resultCode = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING;
                result.description = RC_ERROR_CREATE_SUBSCRIPTION_FEEDING_DESC;
                return result;
            }
            Criteria criteria = session.createCriteria(CycleDiagram.class);
            criteria.add(Restrictions.eq("client", client));
            criteria.add(Restrictions.eq("dateActivationDiagram", cycleDiagram.getDateActivationDiagram()));
            List list = criteria.list();
            SubscriptionFeedingService sfService = SubscriptionFeedingService.getInstance();
            boolean vp = (type == null ? false : type.equals(SubscriptionFeedingType.VARIABLE_TYPE.ordinal()));
            if (vp && startWeekPosition == null) {
                startWeekPosition = 1;
                //result.resultCode = RC_START_WEEK_POSITION_NOT_FOUND;
                //result.description = RC_START_WEEK_POSITION_NOT_FOUND_DESC;
                //return result;
            } else if (vp) {
                String complexesByDayOfWeek;
                int cnt = 0;
                int countMatches = 0;
                for (int i = 1; i < 8; i++) {
                    complexesByDayOfWeek = getCycleDiagramValueByDayOfWeek(i, cycleDiagram);
                    cnt = StringUtils.countMatches(complexesByDayOfWeek, "|");
                    if (cnt > countMatches) {
                        countMatches = cnt;
                    }
                }
                if (startWeekPosition < 1 || startWeekPosition > countMatches + 1) {
                    result.resultCode = RC_START_WEEK_POSITION_NOT_CORRECT;
                    result.description = RC_START_WEEK_POSITION_NOT_CORRECT_DESC;
                    return result;
                }
            }
            /* boolean vp = false;
            if (ArrayUtils.contains(getVPOrgsList(), clientOrg.getIdOfOrg())) {
                vp = true;
            }*/
            if (vp) {
                boolean sixWorkWeek = parser.isSixWorkWeek();
                if (!allDaysWithData(sixWorkWeek, cycleDiagram)) {
                    result.resultCode = RC_ERROR_NOT_ALL_DAYS_FILLED_VARIABLE_FEEDING;
                    result.description = RC_ERROR_NOT_ALL_DAYS_FILLED_VARIABLE_FEEDING_DESC;
                    return result;
                }
                boolean allOk = true;
                String complexesByDayOfWeek;
                for (int i = 1; i < 8; i++) {
                    complexesByDayOfWeek = getCycleDiagramValueByDayOfWeek(i, cycleDiagram);
                    if (complexesByDayOfWeek == null) {
                        continue;
                    }
                    String[] complexesByDayArray = complexesByDayOfWeek
                            .split("\\|"); // разделитель в виде верт. черты - несколько недель
                    for (String complexesByDay : complexesByDayArray) {
                        allOk = allowCreateCycleDiagramByComplexesInDay(complexesByDay, sfService, clientOrg,
                                cycleDiagram
                                        .getDateActivationDiagram()); //для каждой недели проверка на допустимое сочетание комплексов
                        if (!allOk) {
                            break;
                        }
                    }
                    if (!allOk) {
                        break;
                    }
                }
                if (!allOk) {
                    result.resultCode = RC_ERROR_CREATE_VARIABLE_FEEDING;
                    result.description = RC_ERROR_CREATE_VARIABLE_FEEDING_DESC;
                    return result;
                }
            }
            List<ComplexInfo> availableComplexes = sfService.findComplexesWithSubFeeding(clientOrg, vp);
            cycleDiagram.setMonday(getCycleDiagramDayValue(cycleDiagram.getMonday(), "monday", contractId));
            cycleDiagram.setTuesday(getCycleDiagramDayValue(cycleDiagram.getTuesday(), "tuesday", contractId));
            cycleDiagram.setWednesday(getCycleDiagramDayValue(cycleDiagram.getWednesday(), "wednesday", contractId));
            cycleDiagram.setThursday(getCycleDiagramDayValue(cycleDiagram.getThursday(), "thursday", contractId));
            cycleDiagram.setFriday(getCycleDiagramDayValue(cycleDiagram.getFriday(), "friday", contractId));
            cycleDiagram.setSaturday(getCycleDiagramDayValue(cycleDiagram.getSaturday(), "saturday", contractId));
            if (list.isEmpty()) {
                // создаем новую
                CycleDiagram diagram = new CycleDiagram();
                diagram.setCreatedDate(new Date());
                diagram.setClient(client);
                diagram.setOrgOwner(clientOrg.getIdOfOrg());
                diagram.setIdOfClient(client.getIdOfClient());
                diagram.setDateActivationDiagram(cycleDiagram.getDateActivationDiagram());
                diagram.setStateDiagram(StateDiagram.WAIT);
                diagram.setDeletedState(false);
                diagram.setSendAll(SendToAssociatedOrgs.SendToSelf);
                Long version = DAOService.getInstance()
                        .updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName());
                diagram.setGlobalVersionOnCreate(version);
                diagram.setGlobalVersion(version);
                diagram.setMonday(cycleDiagram.getMonday());
                diagram.setMondayPrice(sfService.getPriceOfDay(cycleDiagram.getMonday(), availableComplexes));
                diagram.setTuesday(cycleDiagram.getTuesday());
                diagram.setTuesdayPrice(sfService.getPriceOfDay(cycleDiagram.getTuesday(), availableComplexes));
                diagram.setWednesday(cycleDiagram.getWednesday());
                diagram.setWednesdayPrice(sfService.getPriceOfDay(cycleDiagram.getWednesday(), availableComplexes));
                diagram.setThursday(cycleDiagram.getThursday());
                diagram.setThursdayPrice(sfService.getPriceOfDay(cycleDiagram.getThursday(), availableComplexes));
                diagram.setFriday(cycleDiagram.getFriday());
                diagram.setFridayPrice(sfService.getPriceOfDay(cycleDiagram.getFriday(), availableComplexes));
                diagram.setSaturday(cycleDiagram.getSaturday());
                diagram.setSaturdayPrice(sfService.getPriceOfDay(cycleDiagram.getSaturday(), availableComplexes));
                diagram.setSunday("");
                diagram.setSundayPrice("0");
                diagram.setStaff(null);
                diagram.setFeedingType(vp ? SubscriptionFeedingType.VARIABLE_TYPE : SubscriptionFeedingType.ABON_TYPE);
                if (type != null && type.equals(SubscriptionFeedingType.VARIABLE_TYPE.ordinal())) {
                    diagram.setStartWeekPosition(startWeekPosition);
                }
                session.save(diagram);
            } else {
                // изменяем те что есть
                for (Object obj : list) {
                    CycleDiagram diagram = (CycleDiagram) obj;
                    diagram.setDateActivationDiagram(cycleDiagram.getDateActivationDiagram());
                    diagram.setStateDiagram(StateDiagram.WAIT);
                    diagram.setDeletedState(false);
                    diagram.setSendAll(SendToAssociatedOrgs.SendToSelf);
                    Long version = DAOService.getInstance()
                            .updateVersionByDistributedObjects(CycleDiagram.class.getSimpleName());
                    //diagram.setGlobalVersionOnCreate(version);
                    diagram.setGlobalVersion(version);
                    diagram.setMonday(cycleDiagram.getMonday());
                    diagram.setMondayPrice(sfService.getPriceOfDay(cycleDiagram.getMonday(), availableComplexes));
                    diagram.setTuesday(cycleDiagram.getTuesday());
                    diagram.setTuesdayPrice(sfService.getPriceOfDay(cycleDiagram.getTuesday(), availableComplexes));
                    diagram.setWednesday(cycleDiagram.getWednesday());
                    diagram.setWednesdayPrice(sfService.getPriceOfDay(cycleDiagram.getWednesday(), availableComplexes));
                    diagram.setThursday(cycleDiagram.getThursday());
                    diagram.setThursdayPrice(sfService.getPriceOfDay(cycleDiagram.getThursday(), availableComplexes));
                    diagram.setFriday(cycleDiagram.getFriday());
                    diagram.setFridayPrice(sfService.getPriceOfDay(cycleDiagram.getFriday(), availableComplexes));
                    diagram.setSaturday(cycleDiagram.getSaturday());
                    diagram.setSaturdayPrice(sfService.getPriceOfDay(cycleDiagram.getSaturday(), availableComplexes));
                    diagram.setSunday("");
                    diagram.setSundayPrice("0");
                    if (type != null && type.equals(SubscriptionFeedingType.VARIABLE_TYPE.ordinal())) {
                        diagram.setStartWeekPosition(startWeekPosition);
                    }
                    session.save(diagram);
                }
            }
            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private String getCycleDiagramDayValue(String sourceValue, String dayOfWeek, Long contractId) {
        if (sourceValue == null) {
            RuntimeContext.getAppContext().getBean(CommonTaskService.class)
                    .writeToCommonLog(RuntimeContext.getInstance().getNodeName(), "error",
                            String.format("Null cycle diagram for %s, contractId = %s", dayOfWeek, contractId));
            return "";
        }
        return sourceValue;
    }

    private boolean allDaysWithData(boolean sixWorkWeek, CycleDiagramExt cycleDiagram) {
        int countInMonday = countSubstring(cycleDiagram.getMonday(), '|');
        return !(StringUtils.isEmpty(cycleDiagram.getMonday()) || StringUtils.isEmpty(cycleDiagram.getTuesday())
                || StringUtils.isEmpty(cycleDiagram.getWednesday()) || StringUtils.isEmpty(cycleDiagram.getThursday())
                || StringUtils.isEmpty(cycleDiagram.getFriday()) || (StringUtils.isEmpty(cycleDiagram.getSaturday())
                && sixWorkWeek)) && (countInMonday == countSubstring(cycleDiagram.getTuesday(), '|')
                && countInMonday == countSubstring(cycleDiagram.getWednesday(), '|') && countInMonday == countSubstring(
                cycleDiagram.getThursday(), '|') && countInMonday == countSubstring(cycleDiagram.getFriday(), '|') && (
                sixWorkWeek ? countInMonday == countSubstring(cycleDiagram.getSaturday(), '|') : true));
    }

    private int countSubstring(String source, char search) {
        int result = 0;
        try {
            for (char ch : source.toCharArray()) {
                if (search == ch) {
                    result++;
                }
            }
        } catch (Exception ignore) {
            return 0;
        }
        return result;
    }

    private String getCycleDiagramValueByDayOfWeek(int day, CycleDiagramExt cycleDiagram) {
        switch (day) {
            case 1:
                return cycleDiagram.getMonday();
            case 2:
                return cycleDiagram.getTuesday();
            case 3:
                return cycleDiagram.getWednesday();
            case 4:
                return cycleDiagram.getThursday();
            case 5:
                return cycleDiagram.getFriday();
            case 6:
                return cycleDiagram.getSaturday();
            case 7:
                return cycleDiagram.getSunday();
            default:
                return null;
        }
    }

    private boolean allowCreateCycleDiagramByComplexesInDay(String complexesByDay, SubscriptionFeedingService sfService,
            Org org, Date date) {
        List<Integer> complexIds = new ArrayList<Integer>();
        for (String s : complexesByDay.split(";")) {
            complexIds.add(Integer.parseInt(s));
        }
        return complexIds.size() == 1 || sfService.allowCreateByRootComplexes(org, complexIds, date);
    }

    @Override
    public MenuListWithProhibitionsResult getMenuListWithProhibitions(Long contractId, final Date startDate,
            final Date endDate) {
        authenticateRequest(contractId);

        Data data = new ClientRequest().process(contractId, new Processor() {
            public void process(Client client, Integer subBalanceNum, Data data, ObjectFactory objectFactory,
                    Session session, Transaction transaction) throws Exception {
                if (!client.getOrg().getUseWebArm()) {
                    processMenuFirstDayWithProhibitions(client, data, objectFactory, session, startDate, endDate);
                } else {
                    processWtMenuFirstDayWithProhibitions(client, data, objectFactory, session, startDate, endDate);
                }
            }
        });
        MenuListWithProhibitionsResult menuListWithProhibitionsResult = new MenuListWithProhibitionsResult();
        if (data.getMenuListExt() != null) {
            Collections.sort(data.getMenuListExt().getM());
        }
        menuListWithProhibitionsResult.menuList = data.getMenuListExt();
        menuListWithProhibitionsResult.resultCode = data.getResultCode();
        menuListWithProhibitionsResult.description = data.getDescription();
        return menuListWithProhibitionsResult;
    }

    //возврат id
    //ProhibitionResult
    @Override
    public ProhibitionsResult addProhibition(Long contractId, String filterText, Integer filterType) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);
        Date date = new Date(System.currentTimeMillis());
        Session session = null;
        Transaction transaction = null;
        ProhibitionsResult result = new ProhibitionsResult();
        try {
            try {
                Date currentDate = new Date();
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    result.resultCode = RC_CLIENT_NOT_FOUND;
                    result.description = RC_CLIENT_NOT_FOUND_DESC;
                    return result;
                }
                handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date,
                        handler.getData().getSsoId(), client.getIdOfClient(), handler.getData().getOperationType());

                long maxVersion = DAOUtils.nextVersionByProhibitionsMenu(session);

                ProhibitionMenu prohibitionMenu = null;

                Criteria prohibitionsCriteria = session.createCriteria(ProhibitionMenu.class);
                prohibitionsCriteria.add(Restrictions.eq("client", client));
                prohibitionsCriteria.add(Restrictions.eq("filterText", filterText));
                final ProhibitionFilterType typeBuId = ProhibitionFilterType.getTypeBuId(filterType);
                prohibitionsCriteria.add(Restrictions.eq("prohibitionFilterType", typeBuId));
                prohibitionMenu = (ProhibitionMenu) prohibitionsCriteria.uniqueResult();

                if (prohibitionMenu != null) {
                    if (prohibitionMenu.getDeletedState()) {
                        prohibitionMenu.setDeletedState(false);
                        prohibitionMenu.setVersion(maxVersion);
                        prohibitionMenu.setUpdateDate(new Date());
                        result.prohibitionId = prohibitionMenu.getIdOfProhibitions();
                        session.update(prohibitionMenu);
                    } else {
                        result.prohibitionId = prohibitionMenu.getIdOfProhibitions();
                        result.resultCode = RC_PROHIBIT_EXIST;
                        result.description = RC_PROHIBIT_EXIST_DESC;
                        return result;
                    }
                } else {
                    prohibitionMenu = new ProhibitionMenu();
                    prohibitionMenu.setVersion(maxVersion);
                    prohibitionMenu.setClient(client);
                    prohibitionMenu.setCreateDate(currentDate);
                    prohibitionMenu.setFilterText(filterText);
                    prohibitionMenu.setProhibitionFilterType(typeBuId);
                    prohibitionMenu.setDeletedState(false);
                    session.save(prohibitionMenu);
                    result.prohibitionId = prohibitionMenu.getIdOfProhibitions();
                }
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    //возврат result
    @Override
    public ProhibitionsResult removeProhibition(Long contractId, Long prohibitionId) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(contractId, handler);
        Date date = new Date(System.currentTimeMillis());
        Session session = null;
        Transaction transaction = null;
        ProhibitionsResult result = new ProhibitionsResult();
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                Client client = findClient(session, contractId, null, result);
                if (client == null) {
                    result.resultCode = RC_CLIENT_NOT_FOUND;
                    result.description = RC_CLIENT_NOT_FOUND_DESC;
                    return result;
                }

                handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date,
                        handler.getData().getSsoId(), client.getIdOfClient(), handler.getData().getOperationType());

                long maxVersion = DAOUtils.nextVersionByProhibitionsMenu(session);
                ProhibitionMenu prohibitionMenu = null;

                if (prohibitionId != null) {
                    Criteria prohibitionsCriteria = session.createCriteria(ProhibitionMenu.class);
                    prohibitionsCriteria.add(Restrictions.eq("idOfProhibitions", prohibitionId));
                    prohibitionsCriteria.add(Restrictions.eq("client", client));
                    prohibitionMenu = (ProhibitionMenu) prohibitionsCriteria.uniqueResult();

                    if (prohibitionMenu != null) {
                        if (prohibitionMenu.getDeletedState()) {
                            result.resultCode = RC_PROHIBIT_REMOVED;
                            result.description = RC_PROHIBIT_REMOVED_DESC;
                            return result;
                        }
                        prohibitionMenu.setVersion(maxVersion);
                        prohibitionMenu.setDeletedState(true);
                        prohibitionMenu.setUpdateDate(new Date());
                        session.update(prohibitionMenu);
                    } else {
                        result.resultCode = RC_PROHIBIT_NOT_FOUND;
                        result.description = RC_PROHIBIT_NOT_FOUND_DESC;
                        return result;
                    }
                } else {
                    result.resultCode = RC_PROHIBIT_NOT_FOUND;
                    result.description = RC_PROHIBIT_NOT_FOUND_DESC;
                    return result;
                }
                transaction.commit();
                transaction = null;
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public OrgSummaryResult getOrgSummary(@WebParam(name = "orgId") Long orgId) {
        OrgSummaryResult result = new OrgSummaryResult();
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            Org org = DAOUtils.findOrg(session, orgId);
            if (org != null) {
                result.orgSummary = new OrgSummary(org);
            } else {
                result.notFound();
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.internalError();
        }
        return result;
    }

    @Override
    public VisitorsSummaryResult getVisitorsSummary() {
        return getVisitorsSummaryByDate(Calendar.getInstance().getTimeInMillis());
    }

    @Override
    public VisitorsSummaryResult getVisitorsSummaryByDate(Long datetime) {
        VisitorsSummaryResult result = new VisitorsSummaryResult();
        Session session = null;
        result.orgsList = new VisitorsSummaryList();

        EnterEventsService enterEventsService = (EnterEventsService) RuntimeContext.getAppContext()
                .getBean(EnterEventsService.class);
        List<DAOEnterEventSummaryModel> dataClients = new ArrayList<DAOEnterEventSummaryModel>();
        List<DAOEnterEventSummaryModel> dataOthers = new ArrayList<DAOEnterEventSummaryModel>();
        List<DAOEnterEventSummaryModel> dataVisitors = new ArrayList<DAOEnterEventSummaryModel>();
        try {
            dataClients = enterEventsService.getEnterEventsSummaryNotEmptyClient(datetime);
            dataOthers = enterEventsService.getEnterEventsSummaryEmptyClient(datetime);
            dataVisitors = enterEventsService.getEnterEventsSummaryVisitors(datetime);
        } catch (Exception i) {
            result.resultCode = ResultConst.CODE_INTERNAL_ERROR;
            result.description = ResultConst.DESCR_INTERNAL_ERROR;
        }

        Map<Long, VisitorsSummary> visitorsSummaryList = new HashMap<Long, VisitorsSummary>();

        parseVisitorsSummary(visitorsSummaryList, dataClients);
        parseVisitorsSummary(visitorsSummaryList, dataOthers);
        parseVisitorsSummary(visitorsSummaryList, dataVisitors);

        //visitorsSummaryList.addAll( parseVisitorsSummary(dataOthers) );

        result.orgsList.org = new LinkedList<VisitorsSummary>(visitorsSummaryList.values());
        result.orgsList.orgCount = visitorsSummaryList.values().size();
        if (result.orgsList.org.size() == 0) {
            result.description = ResultConst.DESCR_NOT_FOUND;
            result.resultCode = ResultConst.CODE_NOT_FOUND;
        }
        return result;
    }

    private static Map<Long, VisitorsSummary> parseVisitorsSummary(Map<Long, VisitorsSummary> visitorsSummaryList,
            List<DAOEnterEventSummaryModel> data) {
        VisitorsSummary visitorsSummary = null;
        for (DAOEnterEventSummaryModel model : data) {
            if (visitorsSummary == null || !model.getIdOfOrg().equals(visitorsSummary.id)) {
                visitorsSummary = visitorsSummaryList.get(model.getIdOfOrg());
                if (visitorsSummary == null) {
                    visitorsSummary = new VisitorsSummary();
                    visitorsSummary.id = model.getIdOfOrg();
                    visitorsSummaryList.put(visitorsSummary.id, visitorsSummary);
                }
            }
            if (model.getIdOfClient() == null) {
                if (model.getIdOfVisitor() != null) {
                    if ((model.getPassDirection() != 1) && (model.getPassDirection() != 7)) {
                        visitorsSummary.others3++;
                    }
                } else {
                    if (model.getEventCode() == 112) {
                        visitorsSummary.cardless++;
                    }
                    if ((model.getPassDirection() == 1) || (model.getPassDirection() == 7)) {
                        visitorsSummary.exitsCardless++;
                    }
                }
            }

            if (model.getIdOfClient() != null) {
                visitorsSummary.studentsTotal++;
                if ((model.getPassDirection() != 1) && (model.getPassDirection() != 7)) {
                    if (model.getIdOfClientGroup() != null) {
                        if (model.getIdOfClientGroup() < ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()) {
                            visitorsSummary.studentsInside++;
                        } else if ((model.getIdOfClientGroup() >= ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue())
                                && (model.getIdOfClientGroup() < ClientGroup.Predefined.CLIENT_PARENTS.getValue())) {
                            visitorsSummary.employee++;
                        } else if (model.getIdOfClientGroup() >= ClientGroup.Predefined.CLIENT_PARENTS.getValue()) {
                            visitorsSummary.others1++;
                        }
                    } else {
                        visitorsSummary.others2++;
                    }
                }
            }
        }
        // visitorsSummaryList.put(visitorsSummary.id,visitorsSummary);
        return visitorsSummaryList;
    }

    @Override
    public ClientGuidResult getClientGuidByContractId(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);

        ClientGuidResult result = new ClientGuidResult();
        try {
            DAOReadonlyService daoService = DAOReadonlyService.getInstance();
            Client client = daoService.getClientByContractId(contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            if (client.getClientGUID() == null) {
                result.resultCode = RC_CLIENT_GUID_NOT_FOUND;
                result.description = RC_CLIENT_GUID_NOT_FOUND_DESC;
                return result;
            }
            result.clientGUID = client.getClientGUID();
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Failed to generate linking token", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public ClientContractIdResult getContractIdByGUID(@WebParam(name = "GUID") String guid) {
        ClientContractIdResult result = new ClientContractIdResult();
        try {
            DAOReadonlyService daoService = DAOReadonlyService.getInstance();

            Client client = daoService.getClientByGuid(guid);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
            } else {
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
                result.setContractId(client.getContractId());
            }
        } catch (Exception e) {
            logger.error("Failed get contractID by GUID", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public Result addGuardian(@WebParam(name = "firstName") String firstName,
            @WebParam(name = "secondName") String secondName, @WebParam(name = "surname") String surname,
            @WebParam(name = "mobile") String mobile, @WebParam(name = "gender") Integer gender,
            @WebParam(name = "childContractId") Long childContractId,
            @WebParam(name = "creatorMobile") String creatorMobile,
            @WebParam(name = "passportNumber") String passportNumber,
            @WebParam(name = "passportSeries") String passportSeries, @WebParam(name = "typeCard") Integer typeCard,
            @WebParam(name = "roleRepresentative") Integer roleRepresentative,
            @WebParam(name = "roleRepresentativePrincipal") Integer roleRepresentativePrincipal,
            @WebParam(name = "degree") Long relation) {

        if (roleRepresentativePrincipal != null) {
            if (roleRepresentativePrincipal != 0 && roleRepresentativePrincipal != 1) {
                return new Result(RC_INVALID_CREATOR, RC_INVALID_CREATOR_DESC);
            }
            //roleRepresentativePrincipal += 1;
            //if (roleRepresentativePrincipal == 3)
            //    roleRepresentativePrincipal = 0;
        }

        if (roleRepresentative != null) {
            //Конвертер
            roleRepresentative += 1;
            if (roleRepresentative == 3) {
                roleRepresentative = 0;
            }
        }


        authenticateRequest(null);

        MessageContext mc = context.getMessageContext();
        HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setReason("Веб метод addGuardian");
        clientGuardianHistory.setWebAdress(req.getRemoteAddr());
        clientGuardianHistory.setGuardian(mobile);

        String mobilePhoneCreator = Client.checkAndConvertMobile(creatorMobile);
        String mobilePhone = Client.checkAndConvertMobile(mobile);
        if (StringUtils.isEmpty(firstName) || StringUtils.isEmpty(surname) || StringUtils.isEmpty(mobilePhone)
                || childContractId == null || StringUtils.isEmpty(mobilePhoneCreator)) {
            return new Result(RC_INVALID_DATA, RC_NOT_ALL_ARG);
        }
        if (StringUtils.isEmpty(mobilePhone)) {
            return new Result(RC_INVALID_DATA, RC_INVALID_MOBILE);
        }

        //if (roleRepresentative == null)
        //{
        //    return new Result(RC_INVALID_DATA, RC_INVALID_REPREZENTIVE_TYPE);
        //}

        //if (roleRepresentativePrincipal == null)
        //{
        //    return new Result(RC_INVALID_DATA, RC_INVALID_REPREZENTIVE_CREATOR_TYPE);
        //}

        //Проверка на возможность создания
        Client clientChild = DAOReadonlyService.getInstance().getClientByContractId(childContractId);
        if (clientChild == null) {
            return new Result(RC_INVALID_DATA, RC_CLIENT_NOT_FOUND_DESC);
        }
        List<Client> clientGuardians = null;
        try {
            clientGuardians = DAOReadExternalsService.getInstance()
                    .findGuardiansByClient(clientChild.getIdOfClient(), null);
        } catch (Exception e)
        {
            clientGuardians = new ArrayList<>();
        }

        if (clientGuardians.isEmpty())
            return new Result(RC_INVALID_CREATOR, RC_INVALID_CREATOR_DESC);


        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            String dateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
            String remark = String.format(COMMENT_MPGU_CREATE, dateString, creatorMobile);
            Client client = findClient(session, childContractId, null, result);
            if (client == null) {
                return new Result(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }

            Org org = client.getOrg();
            int count = 0;
            Client guardian = null;
            if (secondName == null) {
                secondName = "";
            }
            //List<Client> guardians = ClientManager.findGuardiansByClient(session, client.getIdOfClient());
            List<Client> exClients = DAOUtils
                    .findClientsByFIO(session, org.getFriendlyOrg(), firstName, surname, secondName, mobilePhone);
            for (Client cl : exClients) {
                if (cl.getClientGroup() == null || cl.getClientGroup().getCompositeIdOfClientGroup()
                        .getIdOfClientGroup().equals(ClientGroup.Predefined.CLIENT_DELETED.getValue()) ||
                        cl.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup()
                        .equals(ClientGroup.Predefined.CLIENT_LEAVING.getValue())) {
                    continue;
                }
                /*if (guardians.contains(cl)) {
                    return new Result(RC_INVALID_DATA, "Клиент уже зарегистрирован");
                }*/
                count++;
                guardian = cl;
            }
            if (count > 1) {
                return new Result(RC_SEVERAL_CLIENTS_WERE_FOUND, RC_SEVERAL_CLIENTS_WERE_FOUND_DESC);
            }
            if (guardian == null) {
                if (gender != null && gender.equals(2)) {
                    gender = 0;
                } else {
                    gender = 1;
                }
                ClientsMobileHistory clientsMobileHistory =
                        new ClientsMobileHistory("soap метод addGuardian");
                clientsMobileHistory.setShowing("Портал");
                guardian = ClientManager
                        .createGuardianTransactionFree(session, firstName, secondName, surname, mobile, remark, gender,
                                org, ClientCreatedFromType.MPGU, creatorMobile, null, passportNumber, passportSeries,
                                null, null, clientsMobileHistory);
            } else {
                long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
                guardian.setClientRegistryVersion(clientRegistryVersion);
                guardian.setCreatedFromDesc(creatorMobile);

                session.update(guardian);

                DulDetail dulDetail = new DulDetail();
                dulDetail.setNumber(passportNumber);
                dulDetail.setSeries(passportSeries);
                dulDetail.setIdOfClient(guardian.getIdOfClient());
                dulDetail.setDocumentTypeId(Client.PASSPORT_RF_TYPE);

                RuntimeContext.getAppContext().getBean(DulDetailService.class)
                        .saveDulOnlyISPP(session, Collections.singletonList(dulDetail), guardian.getIdOfClient());
            }

            ClientGuardian clientGuardian = DAOUtils
                    .findClientGuardian(session, client.getIdOfClient(), guardian.getIdOfClient());
            if (clientGuardian == null) {
                String description = null;
                if (relation != null) {
                    description = ClientGuardianRelationType.fromInteger(relation.intValue()).getDescription();
                }
                clientGuardian = ClientManager
                        .createClientGuardianInfoTransactionFree(session, guardian, description, null,  false,
                                client.getIdOfClient(), ClientCreatedFromType.MPGU, roleRepresentative, clientGuardianHistory);
            } else if (clientGuardian.getDeletedState() || clientGuardian.isDisabled()) {
                boolean enableSpecialNotification = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATIONS_SPECIAL);
                Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
                clientGuardianHistory.setCreatedFrom(ClientCreatedFromType.MPGU);
                clientGuardian.restore(newGuardiansVersions, enableSpecialNotification);
                clientGuardian.setCreatedFrom(ClientCreatedFromType.MPGU);
                session.update(clientGuardian);
            }
            session.flush();
            result = addCardRequest(session, typeCard, passportNumber, passportSeries, guardian, creatorMobile,
                    clientGuardian, clientGuardianHistory);

            transaction.commit();
            transaction = null;
            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Result addCardRequest(Session session, Integer typeCard, String passportNumber, String passportSeries,
            Client guardian, String creatorMobile, ClientGuardian clientGuardian, ClientGuardianHistory clientGuardianHistory) {
        if (typeCard != null) {
            if (StringUtils.isEmpty(passportNumber) || StringUtils.isEmpty(passportSeries)) {
                return new Result(RC_INVALID_DATA, "Не указаны серия и номер паспорта для создания заявки на карту");
            }
            //допустимые типы карт - Mifare или соц. карта москвича - смотрим по массиву типов карт в Card
            if (!(typeCard == 1 || typeCard == 8)) {
                return new Result(RC_INVALID_DATA, "Неверный тип карты");
            }
            Set<Card> cards = guardian.getCards();
            if (cards != null) {
                for (Card card : cards) {
                    if (card.getState().equals(Card.ACTIVE_STATE) && card.getCardType().equals(typeCard)) {
                        return new Result(RC_ERROR_CARD_EXISTS, "У клиента уже есть активная карта выбранного типа");
                    }
                }
            }

            if (DAOUtils.cardRequestExists(session, guardian)) {
                return new Result(RC_ERROR_CARDREQUEST_EXISTS, "Заявка на выдачу карты уже существует");
            }
            Long nextVersion = DAOUtils.nextVersionByCardRequest(session);
            CardRequest cardRequest = new CardRequest(guardian, typeCard, creatorMobile, nextVersion);
            session.save(cardRequest);
            Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
            clientGuardianHistory.setClientGuardian(clientGuardian);
            clientGuardianHistory.setChangeDate(new Date());
            clientGuardian.setCardRequest(cardRequest);
            clientGuardian.setVersion(newGuardiansVersions);
            session.update(clientGuardian);
        }
        return new Result(RC_OK, RC_OK_DESC);
    }

    /*private void testClientsForGuardianMethods(List<Client> clients) throws IllegalArgumentException {
        Long idO = null;
        for (Client c : clients) {
            if (idO == null) idO = c.getOrg().getIdOfOrg();
            if (!idO.equals(c.getOrg().getIdOfOrg())) {
                throw new IllegalArgumentException("Л/с опекаемых должны принадлежать одной организации");
            }
        }
    }*/

    /*Пока выключаем метод, т.к. не используется
    @Override
    public Result changeGuardian(@WebParam(name = "contractId") Long contractId, @WebParam(name = "firstName") String firstName,
            @WebParam(name = "secondName") String secondName, @WebParam(name = "surname") String surname,
            @WebParam(name = "gender") Integer gender, @WebParam(name = "contracts") ListOfContracts contracts) {
        authenticateRequest(contractId);

        if (StringUtils.isEmpty(firstName) || StringUtils.isEmpty(surname) || contracts == null
                || gender == null || CollectionUtils.isEmpty(contracts.getContractIds())) {
            return new Result(RC_INVALID_DATA, RC_NOT_ALL_ARG);
        }

        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();

                Client client = findClientByContractId(session, contractId, result);
                if (client == null) {
                    result.resultCode = RC_CLIENT_NOT_FOUND;
                    result.description = RC_CLIENT_NOT_FOUND_DESC;
                    return result;
                }

                String dateString = new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis()));
                String remarkChange = String.format(COMMENT_MPGU_CHANGE, dateString);

                List<Client> clients = DAOUtils.findClientsByListOfContractId(session, contracts.getContractIds()); //список клиентов-опекаемых из входного параметра
                testClientsForGuardianMethods(clients);
                List<Client> children = ClientManager.findChildsByClient(session, client.getIdOfClient(), true);

                boolean found;
                for (Client clPar : clients) { //clPar - клиенты-опекаемые с л/с из входного параметра
                    found = false;
                    for (Client clDB : children) {  //clDB - клиенты-опекаемых, уже существующие у представителя client
                        if (clPar.equals(clDB)) {
                            found = true;
                            ClientGuardian cg = DAOReadonlyService.getInstance().findClientGuardianByIdIncludeDisabled(session, clPar.getIdOfClient(), client.getIdOfClient(), true);
                            if (cg.getDeletedState() || cg.isDisabled()) {
                                Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
                                cg.restore(newGuardiansVersions);
                                session.update(cg);
                            }
                            break;
                        }
                    }
                    if (!found) {
                        //создание связки
                        ClientManager.createClientGuardianInfoTransactionFree(session, client, null, false, clPar.getIdOfClient());
                        children.add(clPar);
                    }
                }
                for (Client clDB : children) {
                    found = false;
                    for (Client clPar : clients) {
                        if (clDB.equals(clPar)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        ClientGuardian cg = DAOReadonlyService.getInstance().findClientGuardianById(session, clDB.getIdOfClient(), client.getIdOfClient());
                        if (cg != null) {
                            Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
                            cg.delete(newGuardiansVersions);
                            session.update(cg);
                        }
                    }
                }
                FieldProcessor.Config modifyConfig = new ClientManager.ClientFieldConfigForUpdate();
                modifyConfig.setValue(ClientManager.FieldId.SURNAME, surname);
                modifyConfig.setValue(ClientManager.FieldId.NAME, firstName);
                modifyConfig.setValue(ClientManager.FieldId.SECONDNAME, secondName);
                modifyConfig.setValue(ClientManager.FieldId.GENDER, gender);
                ClientManager.modifyClientTransactionFree((ClientManager.ClientFieldConfigForUpdate) modifyConfig,
                        client.getOrg(), remarkChange, client, session, true);

                transaction.commit();
                transaction = null;
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
            }  catch (IllegalArgumentException e) {
                return new Result(RC_INVALID_DATA, e.getMessage());
            } finally {
                HibernateUtils.rollback(transaction, logger);
                HibernateUtils.close(session, logger);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }

        return result;
    }*/

    @Override
    public Result removeGuardian(@WebParam(name = "guardianContractId") Long guardianContractId,
            @WebParam(name = "childContractId") Long childContractId) {
        authenticateRequest(guardianContractId);
        MessageContext mc = context.getMessageContext();
        HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setReason("Веб метод removeGuardian");
        clientGuardianHistory.setWebAdress(req.getRemoteAddr());
        clientGuardianHistory.setGuardian(guardianContractId.toString());
        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client child = findClientByContractId(session, childContractId, result);
            Client guardian = findClientByContractId(session, guardianContractId, result);


            if (guardian == null || child == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            ClientGuardian cg = DAOReadonlyService.getInstance()
                    .findClientGuardianById(session, child.getIdOfClient(), guardian.getIdOfClient());
            if (cg == null || !cg.getCreatedFrom().equals(ClientCreatedFromType.MPGU)) {
                result.resultCode = RC_INVALID_DATA;
                result.description = "Представитель не найден или был создан в другой системе. Удаление невозможно";
                return result;
            }

            Long newGuardiansVersions = ClientManager.generateNewClientGuardianVersion(session);
            clientGuardianHistory.setClientGuardian(cg);
            clientGuardianHistory.setChangeDate(new Date());
            cg.setDisabled(true);
            cg.setVersion(newGuardiansVersions);
            session.update(cg);

            DAOUtils.disableCardRequest(session, guardian.getIdOfClient());
            MigrantsUtils.disableMigrantRequestIfExists(session, child.getOrg().getIdOfOrg(), guardian.getIdOfClient());

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }

        return result;
    }

    @Override
    public MuseumEnterInfo getMuseumEnterInfo(@WebParam(name = "cardId") String cardId) {
        authenticateRequest(null);
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            Long lCardId = Long.parseLong(cardId);
            lCardId = SummaryCardsMSRService.convertCardId(lCardId);
            Card card = null;
            try {
                card = DAOUtils.findCardByCardNoWithUniqueCheck(session, lCardId);
            } catch (NoUniqueCardNoException e) {
                lCardId = SummaryCardsMSRService.convertCardIdForLongCardNo(Long.parseLong(cardId));
                card = DAOUtils.findCardByLongCardNo(session, lCardId);
            }
            if (card == null) {
                return new MuseumEnterInfo(RC_CARD_NOT_FOUND, RC_CARD_NOT_FOUND_DESC);
            }
            Client client = card.getClient();
            if (client == null) {
                return new MuseumEnterInfo(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }

            String guid = client.getClientGUID();
            String meshGuid = client.getMeshGUID();
            if (StringUtils.isEmpty(guid) && StringUtils.isEmpty(meshGuid)) {
                return new MuseumEnterInfo(RC_CLIENT_GUID_NOT_FOUND, RC_CLIENT_GUID_NOT_FOUND_DESC);
            }
            Date currentDate = new Date();
            boolean clientPredefined = client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup()
                    >= ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                    && client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup()
                    <= ClientGroup.Predefined.CLIENT_DELETED.getValue();
            if ((card.getState() == CardState.ISSUED.getValue() || card.getState() == CardState.TEMPISSUED.getValue())
                    && card.getValidTime().after(currentDate) && !clientPredefined) {
                return new MuseumEnterInfo(RC_OK, RC_OK_DESC, guid, meshGuid, 0L, "Карта активна");
            } else if (!clientPredefined) {
                return new MuseumEnterInfo(RC_OK, RC_OK_DESC, guid, meshGuid, 2L, "Карта не активна");
            } else {
                return new MuseumEnterInfo(RC_OK, RC_OK_DESC, guid, meshGuid, 1L,
                        "Карта принадлежит другой группе держателей «Москвенка»");
            }
        } catch (Exception e) {
            logger.error("Error in getMuseumEnterInfo method:", e);
            return new MuseumEnterInfo(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public CultureEnterInfo getCultureEnterInfo(@WebParam(name = "cardId") String cardId) {
        authenticateRequest(null);
        Session session = null;
        try {
            CultureEnterInfo cultureEnterInfo = new CultureEnterInfo();
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            Long lCardId = Long.parseLong(cardId);
            lCardId = SummaryCardsMSRService.convertCardId(lCardId);
            Card card = null;
            try {
                card = DAOUtils.findCardByCardNoWithUniqueCheck(session, lCardId);
            } catch (NoUniqueCardNoException e) {
                lCardId = SummaryCardsMSRService.convertCardIdForLongCardNo(Long.parseLong(cardId));
                card = DAOUtils.findCardByLongCardNo(session, lCardId);
            }
            if (card == null || !card.isActive()) {
                return new CultureEnterInfo(RC_CARD_NOT_FOUND, RC_CARD_NOT_FOUND_DESC);
            }
            Client client = card.getClient();
            if (client == null || client.isDeletedOrLeaving()) {
                return new CultureEnterInfo(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            }

            if (client.getAgeTypeGroup() != null && ArrayUtils.contains(Client.GROUP_NAME_SCHOOL, client.getAgeTypeGroup())) {
                //Если клиент школьник
                if (StringUtils.isEmpty(client.getClientGUID()) && StringUtils.isEmpty(client.getMeshGUID())) {
                    return new CultureEnterInfo(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
                }
                cultureEnterInfo.setFullAge(getFullAge(client));
                cultureEnterInfo.setGuid(client.getClientGUID());
                cultureEnterInfo.setMesId(client.getMeshGUID());
                cultureEnterInfo.setGroupName(client.getClientGroup().getGroupName());
            } else {
                List<Client> childsList = ClientManager.findChildsByClient(session, client.getIdOfClient());
                if (childsList.size() == 0) {
                    return new CultureEnterInfo(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
                }
                //Если клиент опекун
                cultureEnterInfo.setGroupName(client.getClientGroup().getGroupName());
                cultureEnterInfo.getChildrens().add(new CultureEnterInfo());
                cultureEnterInfo.setFullAge(getFullAge(client));
                for (Client child : childsList) {
                    if (child.getAgeTypeGroup() != null) {
                        boolean clientPredefined =
                                (child.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup()
                                        < ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                                        || child.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup()
                                        > ClientGroup.Predefined.CLIENT_DELETED.getValue()) && (
                                        child.getAgeTypeGroup().equals(Client.GROUP_NAME[Client.GROUP_BEFORE_SCHOOL])
                                                || child.getAgeTypeGroup()
                                                .equals(Client.GROUP_NAME[Client.GROUP_BEFORE_SCHOOL_OUT]) || child
                                                .getAgeTypeGroup()
                                                .equals(Client.GROUP_NAME[Client.GROUP_BEFORE_SCHOOL_STEP]));
                        //Добавляем в список только дошкольников
                        if (clientPredefined) {
                            CultureEnterInfo cultureEnterInfoChield = new CultureEnterInfo();
                            cultureEnterInfoChield.setGuid(child.getClientGUID());
                            cultureEnterInfoChield.setMesId(child.getMeshGUID());
                            cultureEnterInfoChield.setGroupName(child.getClientGroup().getGroupName());
                            cultureEnterInfoChield.setFullAge(getFullAge(child));
                            cultureEnterInfo.getChildrens().get(0).getChild().add(cultureEnterInfoChield);
                        }
                    }
                }
                if (cultureEnterInfo.getChildrens().get(0).getChild().size() == 0) {
                    return new CultureEnterInfo(RC_CARD_NOT_FOUND, RC_CARD_NOT_FOUND_DESC);
                }
            }
            cultureEnterInfo.setValidityCard(true);
            cultureEnterInfo.resultCode = RC_OK;
            cultureEnterInfo.description = RC_OK_DESC;
            return cultureEnterInfo;
        } catch (Exception e) {
            logger.error("Error in getCultureEnterInfo method:", e);
            return new CultureEnterInfo(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    private Boolean getFullAge(Client client) {
        return (client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup()
                >= ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                && client.getClientGroup().getCompositeIdOfClientGroup().getIdOfClientGroup()
                <= ClientGroup.Predefined.CLIENT_OTHERS.getValue());
    }

    @Override
    public Result enterMuseum(@WebParam(name = "guid") String guid, @WebParam(name = "mesId") String mesId,
            @WebParam(name = "museumCode") String museumCode,
            @WebParam(name = "museumName") String museumName, @WebParam(name = "accessTime") Date accessTime,
            @WebParam(name = "ticketStatus") Integer ticketStatus) {
        authenticateRequest(null);
        if (StringUtils.isEmpty(guid) && StringUtils.isEmpty(mesId)) {
            return new Result(RC_INVALID_DATA, RC_CLIENT_GUID_NOT_FOUND_DESC);
        }
        if (!StringUtils.isEmpty(guid) && !StringUtils.isEmpty(mesId)) {
            return new Result(RC_INVALID_DATA, "В запросе должен быть указан только один идентификатор - guid или mesId");
        }
        if (accessTime == null) {
            return new Result(RC_INVALID_DATA, "Время события не может быть пустым");
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client cl = null;
            if (!StringUtils.isEmpty(guid)) {
                cl = DAOUtils.findClientByGuid(session, guid);
            }
            if (!StringUtils.isEmpty(mesId)) {
                cl = DAOUtils.findClientByMeshGuid(session, mesId);
            }
            if (cl == null) {
                return new Result(RC_INVALID_DATA, RC_CLIENT_GUID_NOT_FOUND_DESC);
            }
            //здесь сохранение события в таблицу и отправка уведомления
            if (museumName != null && museumName.length() > 255) {
                museumName = museumName.substring(0, 255);
            }
            Card card = cl.findActiveCard(session, null);
            if (card != null) {
                RuntimeContext.getAppContext().getBean(CardBlockService.class)
                        .saveLastCardActivity(session, card.getIdOfCard(), CardActivityType.ENTER_MUSEUM);
            }
            ExternalEventVersionHandler handler = new ExternalEventVersionHandler(session);
            ExternalEvent event = new ExternalEvent(cl, museumCode, museumName, ExternalEventType.MUSEUM, accessTime,
                    ExternalEventStatus.fromInteger(ticketStatus), card == null ? null : card.getCardNo(),
                    card == null ? null : card.getCardType(), handler);
            session.save(event);
            transaction.commit();
            transaction = null;

            //отправка уведомления
            if (CalendarUtils.isDateToday(accessTime)) {
                ExternalEventNotificationService notificationService = RuntimeContext.getAppContext()
                        .getBean(ExternalEventNotificationService.class);
                notificationService.sendNotification(cl, event);
            }
            return new Result(RC_OK, RC_OK_DESC);
        } catch (IllegalArgumentException e) {
            logger.error("Error in enterMuseum method:", e);
            return new Result(RC_INTERNAL_ERROR, e.getMessage());
        } catch (Exception e) {
            logger.error("Error in enterMuseum method:", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public Result enterCulture(@WebParam(name = "guid") String guid, @WebParam(name = "mesId") String mesId,
            @WebParam(name = "orgCode") String orgCode,
            @WebParam(name = "CultureName") String cultureName,
            @WebParam(name = "CultureShortName") String cultureShortName,
            @WebParam(name = "CultureAddress") String cultureAddress, @WebParam(name = "accessTime") Date accessTime,
            @WebParam(name = "eventsStatus") Long eventsStatus) {

        authenticateRequest(null);
        if (StringUtils.isEmpty(guid) && StringUtils.isEmpty(mesId)) {
            return new Result(RC_INVALID_DATA, RC_CLIENT_GUID_NOT_FOUND_DESC);
        }

        if (!StringUtils.isEmpty(guid) && !StringUtils.isEmpty(mesId)) {
            return new Result(RC_INVALID_DATA, "В запросе должен быть указан только один идентификатор - guid или mesId");
        }

        if (StringUtils.isEmpty(orgCode)) {
            return new Result(RC_INVALID_DATA, "Код организации не может быть пустым");
        }

        if (StringUtils.isEmpty(cultureName)) {
            return new Result(RC_INVALID_DATA, "Ниаменование не может быть пустым");
        }

        if (StringUtils.isEmpty(cultureShortName)) {
            return new Result(RC_INVALID_DATA, "Краткое наименование не может быть пустым");
        }

        if (StringUtils.isEmpty(cultureAddress)) {
            return new Result(RC_INVALID_DATA, "Адрес организации не может быть пустым");
        }

        if (accessTime == null) {
            return new Result(RC_INVALID_DATA, "Время события не может быть пустым");
        }

        if (eventsStatus == null || (eventsStatus < 0 || eventsStatus > 5)) {
            return new Result(RC_INVALID_DATA, "Некорректный статус события");
        }

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Client cl = null;
            if (!StringUtils.isEmpty(guid)) {
                cl = DAOUtils.findClientByGuid(session, guid);
            }
            if (!StringUtils.isEmpty(mesId)) {
                cl = DAOUtils.findClientByMeshGuid(session, mesId);
            }
            if (cl == null) {
                return new Result(RC_INVALID_DATA, RC_CLIENT_GUID_NOT_FOUND_DESC);
            }
            Card card = cl.findActiveCard(session, null);
            if (card != null) {
                RuntimeContext.getAppContext().getBean(CardBlockService.class)
                        .saveLastCardActivity(session, card.getIdOfCard(), CardActivityType.ENTER_MUSEUM);
            }
            //здесь сохранение события в таблицу и отправка уведомления
            if (cultureName != null && cultureName.length() > 255) {
                cultureName = cultureName.substring(0, 255);
            }
            ExternalEventVersionHandler handler = new ExternalEventVersionHandler(session);
            ExternalEvent event = new ExternalEvent(cl, orgCode, cultureName, cultureAddress, ExternalEventType.CULTURE,
                    accessTime, ExternalEventStatus.fromInteger(eventsStatus.intValue()), card == null ? null : card.getCardNo(),
                    card == null ? null : card.getCardType(), handler);
            session.save(event);
            transaction.commit();
            transaction = null;

            //отправка уведомления
            if (CalendarUtils.isDateToday(accessTime)) {
                ExternalEventNotificationService notificationService = RuntimeContext.getAppContext()
                        .getBean(ExternalEventNotificationService.class);
                notificationService.setCultureShortName(cultureShortName);
                notificationService.sendNotification(cl, event);
            }
            return new Result(RC_OK, RC_OK_DESC);
        } catch (IllegalArgumentException e) {
            logger.error("Error in enterCulture method:", e);
            return new Result(RC_INTERNAL_ERROR, e.getMessage());
        } catch (Exception e) {
            logger.error("Error in enterCulture method:", e);
            return new Result(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public ClientSummaryBaseListResult getSummaryByStaffMobileMin(String staffMobile) {
        authenticateRequest(null);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            transaction = session.beginTransaction();

            List<Long> staffGroups = new ArrayList<>();

            Query query = session.createQuery("select c from Client c where c.mobile = :mobile");
            query.setParameter("mobile", Client.checkAndConvertMobile(staffMobile));
            List<Client> clients = query.list();
            ClientSummaryBaseListResult result = processClientSummaryByMobileResult(session, clients, "staff");

            transaction.commit();
            transaction = null;
            return result;
        } catch (Exception e) {
            ClientSummaryBaseListResult result = new ClientSummaryBaseListResult();
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
            return result;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public ClientSummaryBaseListResult getSummaryByChildMobileMin(String childMobile) {
        authenticateRequest(null);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            transaction = session.beginTransaction();

            Query query = session.createQuery(
                    "select c from Client c where c.mobile = :mobile and c.idOfClientGroup < :clientGroup");
            query.setParameter("mobile", Client.checkAndConvertMobile(childMobile));
            query.setParameter("clientGroup", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            List<Client> clients = query.list();
            ClientSummaryBaseListResult result = processClientSummaryByMobileResult(session, clients, "child");

            transaction.commit();
            transaction = null;
            return result;
        } catch (Exception e) {
            ClientSummaryBaseListResult result = new ClientSummaryBaseListResult();
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
            return result;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private ClientSummaryBaseListResult processClientSummaryByMobileResult(Session session, List<Client> clients,
            String mode) throws Exception {
        ClientSummaryBaseListResult result = new ClientSummaryBaseListResult();
        if (clients.size() == 0) {
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = RC_CLIENT_NOT_FOUND_DESC;
            return result;
        }
        if (clients.size() > 1) {
            result.resultCode = RC_SEVERAL_CLIENTS_WERE_FOUND;
            result.description = RC_SEVERAL_CLIENTS_WERE_FOUND_DESC;
            return result;
        }
        Client child = clients.get(0);
        if (!child.getOrg().getPreordersEnabled()) {
            result.resultCode = RC_PREORDERS_NOT_ENABLED;
            result.description = RC_PREORDERS_NOT_ENABLED_DESC;
            return result;
        }
        boolean allowed = ClientManager.getAllowedPreorderByClient(session, child.getIdOfClient(), null);
        if (mode.equals("child")) {
            List<ClientGuardianItem> guardians = ClientManager.loadGuardiansByClient(session, child.getIdOfClient(), false);
            boolean informed = false;
            for (ClientGuardianItem item : guardians) {
                if (item.getInformedSpecialMenu()) {
                    informed = true;
                    break;
                }
            }
            if (!informed) {
                result.resultCode = RC_NOT_INFORMED_SPECIAL_MENU;
                result.description = RC_NOT_INFORMED_SPECIAL_MENU_DESC;
                return result;
            }
            if (!allowed) {
                result.resultCode = RC_NOT_ALLOWED_PREORDERS;
                result.description = RC_NOT_ALLOWED_PREORDERS_DESC;
                return result;
            }
        } else if (mode.equals("staff") && !child.isSotrudnikMsk()) {
            result.resultCode = RC_PREORDERS_NOT_STAFF;
            result.description = RC_PREORDERS_NOT_STAFF_DESC;
            return result;
        }
        ClientSummaryBase summaryBase = new ClientSummaryBase();
        summaryBase.setContractId(child.getContractId());
        summaryBase.setFirstName(child.getPerson().getFirstName());
        summaryBase.setLastName(child.getPerson().getSurname());
        summaryBase.setMiddleName(child.getPerson().getSecondName());
        summaryBase.setBalance(child.getBalance());
        summaryBase.setOfficialName(child.getOrg().getOfficialName());
        summaryBase.setGrade(child.getClientGroup().getGroupName());
        if (mode.equals("child")) {
            summaryBase.setPreorderAllowed(1); //т.к. если флаг выключен, то выше уже кидаем ошибку
        } else if (mode.equals("staff")) {
            summaryBase.setPreorderAllowed(allowed ? 1 : 0);
        }
        summaryBase.setAddress(child.getOrg().getShortAddress());

        List<ClientSummaryBase> list = new ArrayList<>();
        list.add(summaryBase);
        result.setClientSummary(list);
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;
        return result;
    }

    @Override
    public ClientSummaryBaseListResult getSummaryByGuardMobileMin(String guardMobile) {
        HTTPData data = new HTTPData();
        HTTPDataHandler handler = new HTTPDataHandler(data);
        authenticateRequest(null, handler);
        Date date = new Date(System.currentTimeMillis());

        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("soap метод getSummaryByGuardMobileMin");
        clientsMobileHistory.setShowing("Портал");
        changeSsoid(guardMobile, clientsMobileHistory);

        Session session = null;
        Transaction transaction = null;
        try {
            List<ClientSummaryBase> clientSummaries = new ArrayList<ClientSummaryBase>();
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            transaction = session.beginTransaction();
            ClientsWithResultCode cd = getClientsByGuardMobile(guardMobile, session);

            if (cd != null && cd.getClients() != null) {
                for (Map.Entry<Client, ClientWithAddInfo> entry : cd.getClients().entrySet()) {
                    if (entry.getValue().isDisabled()) {
                        continue;
                    }
                    ClientSummaryBase base = processSummaryBase(entry.getKey());
                    base.setGuardianCreatedWhere(entry.getValue().getClientCreatedFrom().getValue());
                    base.setInformedSpecialMenu(entry.getValue().getInformedSpecialMenu());
                    base.setPreorderAllowed(entry.getValue().getPreorderAllowed());
                    base.setIsInside(DAOReadExternalsService.getInstance()
                            .isClientInside(session, entry.getKey().getIdOfClient()));
                    if (base != null) {
                        clientSummaries.add(base);
                        handler.saveLogInfoService(logger, handler.getData().getIdOfSystem(), date,
                                handler.getData().getSsoId(), entry.getKey().getIdOfClient(),
                                handler.getData().getOperationType());
                    }
                }
            }

            ClientSummaryBaseListResult clientSummaryBaseListResult = new ClientSummaryBaseListResult();
            clientSummaryBaseListResult.setClientSummary(clientSummaries);
            clientSummaryBaseListResult.resultCode = cd.resultCode;
            clientSummaryBaseListResult.description = cd.description;
            transaction.commit();
            transaction = null;

            return clientSummaryBaseListResult;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public TransactionInfoListResult getOrderTransactions() {
        authenticateRequest(null);
        TransactionInfoListResult result = new TransactionInfoListResult();
        List<Long> ids = new ArrayList<Long>();
        try {
            List transactions = FinManager.getInstance().getOrdersAndTransactions();
            TransactionInfoList list = new TransactionInfoList();
            for (Object o : transactions) {
                Object[] row = (Object[]) o;
                Long idOfTransaction = ((BigInteger) row[0]).longValue();
                Long transactionSum = ((BigInteger) row[1]).longValue();
                Integer sourceType = (Integer) row[2];
                Date transactionDate = new Date(((BigInteger) row[3]).longValue());
                Long balanceBefore = ((BigInteger) row[4]).longValue();
                Long balanceAfter = ((BigInteger) row[5]).longValue();
                Long idOfOrder = ((BigInteger) row[6]).longValue();
                Long contractId = ((BigInteger) row[7]).longValue();
                TransactionInfo info = new TransactionInfo(idOfTransaction, transactionSum, sourceType,
                        toXmlDateTime(transactionDate), balanceBefore, balanceAfter, idOfOrder, contractId);
                list.getItems().add(info);
                ids.add(idOfTransaction);
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            result.transactionItems = list;
            return result;
        } catch (Exception e) {
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
            return result;
        } finally {
            if (result.resultCode.equals(RC_OK)) {
                FinManager.getInstance().markTransactionsAsSentToExternal(ids);
            }
        }
    }

    @Override
    public GuardianInfoListResult getGuardiansFromDate(Long dateTime) {
        GuardianInfoListResult result = new GuardianInfoListResult();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            String sqlQuery =
                    "SELECT p.surname, p.firstname, p.secondname, c.mobile, c.ssoid, c.contractdate, c.createdfrom AS guardiancreatedfrom, "
                            + "       cg.lastupdate, cg.createdfrom clientguardiancreatedfrom, cg.relation,  c2.clientguid, c.contractid, "
                            + "       cg.deletedstate, cg.disabled " + "FROM cf_client_guardian cg "
                            + "INNER JOIN cf_clients c ON c.idofclient=cg.idofguardian AND c.ssoid IS NOT NULL AND c.ssoid "
                            + "           SIMILAR TO '_{8}-_{4}-_{4}-_{4}-_{12}' AND (c.mobile = '') IS NOT TRUE "
                            + "INNER JOIN cf_persons p ON p.idofperson=c.idofperson "
                            + "INNER JOIN cf_clients c2 ON c2.idofclient=cg.idofchildren "
                            + "WHERE cg.lastupdate>=:lastUpdate";

            Query query = session.createSQLQuery(sqlQuery);
            query.setParameter("lastUpdate", dateTime);

            GuardianInfoList guardianInfoList = new GuardianInfoList();
            List list = query.list();
            for (Object o : list) {
                Object vals[] = (Object[]) o;

                String surname = (String) vals[0];
                String firstname = (String) vals[1];
                String secondname = (String) vals[2];
                String mobile = (String) vals[3];
                String ssoid = (String) vals[4];
                Date contractDate = new Date(((BigInteger) vals[5]).longValue());
                Integer guardianCreatedFrom = (Integer) vals[6];
                Date lastUpdate = new Date(((BigInteger) vals[7]).longValue());
                Integer createdFrom = (Integer) vals[8];
                Integer relation = (Integer) vals[9];
                String guid = (String) vals[10];
                Long contractID = ((BigInteger) vals[11]).longValue();
                Boolean isDeleted = (Boolean) vals[12];
                Integer isDisabled = (Integer) vals[13];

                GuardianInfo guardianInfo = new GuardianInfo(surname, firstname, secondname, mobile, ssoid,
                        toXmlDateTime(contractDate), guardianCreatedFrom, createdFrom, relation, guid, contractID,
                        isDeleted, (isDisabled == 0) ? Boolean.FALSE : Boolean.TRUE, toXmlDateTime(lastUpdate));

                guardianInfoList.getItems().add(guardianInfo);
            }

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            result.guardianItems = guardianInfoList;
            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
            return result;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public PreorderClientSummaryResult getPreorderClientSummary(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "guardianMobile") String guardianMobile) {
        authenticateRequest(contractId);
        PreorderClientSummaryResult result = new PreorderClientSummaryResult();
        try {
            ClientResult cr = getClientOrError(contractId, guardianMobile);
            if (!cr.resultCode.equals(RC_OK)) {
                result.resultCode = cr.resultCode;
                result.description = cr.description;
                return result;
            }
            Client client = cr.getClient();

            result = getPreorderClientSummaryResultOnDate(client, new Date());

            return result;
        } catch (Exception e) {
            logger.error("Error in getPreorderClientSummary: ", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
            return result;
        }
    }

    //todo отладочный метод - удалить
    @WebMethod(operationName = "getPreorderClientSummaryOnDate")
    public PreorderClientSummaryResult getPreorderClientSummaryOnDate(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "guardianMobile") String guardianMobile, @WebParam(name = "date") Date date) {
        authenticateRequest(contractId);
        PreorderClientSummaryResult result = new PreorderClientSummaryResult();
        try {
            ClientResult cr = getClientOrError(contractId, guardianMobile);
            if (!cr.resultCode.equals(RC_OK)) {
                result.resultCode = cr.resultCode;
                result.description = cr.description;
                return result;
            }
            Client client = cr.getClient();

            result = getPreorderClientSummaryResultOnDate(client, date);

            return result;
        } catch (Exception e) {
            logger.error("Error in getPreorderClientSummary: ", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
            return result;
        }
    }

    private PreorderClientSummaryResult getPreorderClientSummaryResultOnDate(Client client, Date date)
            throws Exception {
        PreorderClientSummaryResult result = new PreorderClientSummaryResult();
        PreorderDAOService preorderDAOService = RuntimeContext.getAppContext().getBean(PreorderDAOService.class);
        Integer syncCountDays = PreorderComplex.getDaysOfRegularPreorders();
        Date today = CalendarUtils.startOfDay(date);

        Date endDate = CalendarUtils.addDays(today, 13);
        endDate = CalendarUtils.endOfDay(endDate);
        Long preordersSum14 = preorderDAOService.getPreordersSum(client, today, endDate);
        result.setPreorderSum14Days(preordersSum14);

        endDate = CalendarUtils.addDays(today, 2);
        endDate = CalendarUtils.endOfDay(endDate);
        Long preordersSum3 = preorderDAOService.getPreordersSum(client, today, endDate);
        result.setPreorderSum3Days(preordersSum3);

        result.setForbiddenDays(DAOUtils.getPreorderFeedingForbiddenDays(client));
        Map<String, Integer[]> sd = preorderDAOService
                .getSpecialDates(CalendarUtils.addHours(today, 12), syncCountDays, client.getOrg().getIdOfOrg(),
                        client);
        PreorderCalendar calendar = new PreorderCalendar();
        for (Map.Entry<String, Integer[]> entry : sd.entrySet()) {
            PreorderCalendarItem item = new PreorderCalendarItem();
            today = CalendarUtils.startOfDay(CalendarUtils.parseDate(entry.getKey()));
            endDate = CalendarUtils.endOfDay(today);
            item.setDate(toXmlDateTime(today));
            item.setEditForbidden((entry.getValue())[0]);
            item.setPreorderExists((entry.getValue())[1]);
            item.setAddress(preorderDAOService.getAddress((entry.getValue())[2]));
            Long sum = preorderDAOService.getPreordersSum(client, today, endDate);
            item.setSumm(sum);
            calendar.getItems().add(item);
        }
        result.setCalendar(calendar);
        SubscriptionFeeding sf = preorderDAOService.getClientSubscriptionFeeding(client);
        result.setSubscriptionFeeding((sf == null) ? 0 : 1);
        result.resultCode = RC_OK;
        result.description = RC_OK_DESC;
        return result;
    }

    private ClientResult getClientOrError(Long contractId, String guardianMobile) {
        ClientResult result = new ClientResult();
        Client client;
        client = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getClientByContractId(contractId);
        if (client == null) {
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = RC_CLIENT_NOT_FOUND_DESC;
            return result;
        }

        Org org = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                .findOrg(client.getOrg().getIdOfOrg());
        if (!org.getPreordersEnabled()) {
            result.resultCode = RC_SETTINGS_NOT_FOUND;
            result.description = "У организации не работает функционал предзаказов";
            return result;
        }

        boolean informed = false;

        if (client.isSotrudnikMsk()) {
            informed = ClientManager.getInformedSpecialMenuWithoutSession(client.getIdOfClient(), null);
        } else if (client.isStudent() && (client.getMobile() == null || !client.getMobile().equals(guardianMobile))) {
            ClientGuardianResult cgr = getClientGuardianOrError(client, guardianMobile);
            if (!cgr.resultCode.equals(RC_OK)) {
                result.resultCode = cgr.resultCode;
                result.description = cgr.description;
                return result;
            }

            for (ClientGuardian cg : cgr.getClientGuardian()) {
                if (ClientManager.getInformedSpecialMenuWithoutSession(client.getIdOfClient(), cg.getIdOfGuardian())) {
                    informed = true;
                    break;
                }
            }
        }
        if (client.isStudent() && client.getMobile() != null && client.getMobile().equals(guardianMobile)) {
            if (!ClientManager.getAllowedPreorderByClientWithoutSession(client.getIdOfClient(), null)) {
                result.resultCode = RC_NOT_ALLOWED_PREORDERS;
                result.description = RC_NOT_ALLOWED_PREORDERS_DESC;
                return result;
            } else {
                informed = true;
            }
        }
        if (!informed) {
            result.resultCode = RC_NOT_INFORMED_SPECIAL_MENU;
            result.description = RC_NOT_INFORMED_SPECIAL_MENU_DESC;
            return result;
        }

        result.resultCode = RC_OK;
        result.setClient(client);
        return result;
    }

    private ClientGuardianResult getClientGuardianOrError(Client client, String guardianMobile) {
        ClientGuardianResult result = new ClientGuardianResult();
        List<ClientGuardian> guardians = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                .getClientGuardian(client, guardianMobile);
        if (guardians.size() == 0) {
            result.resultCode = RC_CLIENT_GUARDIAN_NOT_FOUND;
            result.description = RC_CLIENT_GUARDIAN_NOT_FOUND_DESC;
            return result;
        }

        result.setClientGuardian(guardians);
        result.resultCode = RC_OK;
        return result;
    }

    @Override
    public Result setPreorderAllowed(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "guardianMobile") String guardianMobile,
            @WebParam(name = "staffMobile") String childMobile, @WebParam(name = "value") Boolean value) {
        authenticateRequest(contractId);
        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            if ((client.isStudent() && (StringUtils.isEmpty(guardianMobile) || StringUtils.isEmpty(childMobile))) || (
                    client.isSotrudnikMsk() && (!StringUtils.isEmpty(guardianMobile) || !StringUtils
                            .isEmpty(childMobile)))) {
                //если для ученика не указаны телефоны представителя и самого ученика или наоборот, для сотрудника они указаны, то это ошибка
                result.resultCode = RC_INVALID_DATA;
                result.description = RC_INVALID_INPUT_DATA;
                return result;
            }

            if (client.isSotrudnikMsk()) {
                ClientManager.setPreorderAllowedForClient(session, client, value);
            } else {

                String mobile = Client.checkAndConvertMobile(childMobile);
                if (mobile == null) {
                    result.resultCode = RC_INVALID_DATA;
                    result.description = RC_INVALID_MOBILE;
                    return result;
                }
                Long version = getClientGuardiansResultVersion(session);
                List<Client> guardians = ClientManager.findGuardiansByClient(session, client.getIdOfClient());
                boolean guardianWithMobileFound = false;
                for (Client guardian : guardians) {
                    if (!StringUtils.isEmpty(guardian.getMobile()) && guardian.getMobile()
                            .equals(Client.checkAndConvertMobile(guardianMobile))) {
                        guardianWithMobileFound = true;
                        ClientsMobileHistory clientsMobileHistory =
                                new ClientsMobileHistory("soap метод setPreorderAllowed");
                        clientsMobileHistory.setShowing("Портал");
						MessageContext mc = context.getMessageContext();
                        HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
                        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
                        clientGuardianHistory.setReason("Веб метод setPreorderAllowed");
                        clientGuardianHistory.setWebAdress(req.getRemoteAddr());
                        clientGuardianHistory.setGuardian(guardianMobile);
                        ClientManager.setPreorderAllowed(session, client, guardian, mobile, value, version,
                                clientsMobileHistory, clientGuardianHistory);
                    }
                }

                if (!guardianWithMobileFound) {
                    result.resultCode = RC_CLIENT_GUARDIAN_NOT_FOUND;
                    result.description = RC_CLIENT_GUARDIAN_NOT_FOUND_DESC;
                    return result;
                }
            }

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (NotInformedSpecialMenuException ne) {
            result.resultCode = RC_NOT_INFORMED_SPECIAL_MENU;
            result.description = RC_NOT_INFORMED_SPECIAL_MENU_DESC;
        } catch (Exception e) {
            logger.error("Error in setPreorderAllowed", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result setInformedSpecialMenuForClient(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            ClientManager.setInformedSpecialMenu(session, client, client.isStudent());

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Error in setInformedSpecialMenu", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result setInformedSpecialMenu(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "guardianMobile") String guardianMobile) {
        authenticateRequest(contractId);
        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        MessageContext mc = context.getMessageContext();
        HttpServletRequest req = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setReason("Веб метод setInformedSpecialMenu");
        clientGuardianHistory.setWebAdress(req.getRemoteAddr());
        clientGuardianHistory.setGuardian(guardianMobile);
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            Long version = getClientGuardiansResultVersion(session);
            if (client.isStudent()) {
                List<Client> guardians = ClientManager.findGuardiansByClient(session, client.getIdOfClient());
                boolean guardianWithMobileFound = false;
                for (Client guardian : guardians) {
                    if (!StringUtils.isEmpty(guardian.getMobile()) && guardian.getMobile()
                            .equals(Client.checkAndConvertMobile(guardianMobile))) {
                        guardianWithMobileFound = true;
                        ClientManager.setInformSpecialMenu(session, client, guardian, version, clientGuardianHistory);
                    }
                }
                if (!guardianWithMobileFound) {
                    result.resultCode = RC_CLIENT_GUARDIAN_NOT_FOUND;
                    result.description = RC_CLIENT_GUARDIAN_NOT_FOUND_DESC;
                    return result;
                }
            } else if (client.isSotrudnikMsk() && StringUtils.isEmpty(guardianMobile)) {
                ClientManager.setInformSpecialMenu(session, client, null, version, clientGuardianHistory);
            } else if ((client.isSotrudnikMsk() || client.isSotrudnik()) && !StringUtils.isEmpty(guardianMobile)) {
                if (client.getMobile().equals(Client.checkAndConvertMobile(guardianMobile))) {
                    ClientManager.setInformSpecialMenu(session, client, null, version, clientGuardianHistory);
                } else {
                    result.resultCode = RC_INVALID_DATA;
                    result.description = RC_INVALID_MOBILE;
                    return result;
                }
            } else {
                result.resultCode = RC_WRONG_GROUP;
                result.description = RC_WRONG_GROUP_DESC;
                return result;
            }

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Error in setInformedSpecialMenu", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private ClientGroupResult getClientGroupResult(Session session, List<Client> clients) {
        Integer value = PreorderUtils.getClientGroupResult(session, clients);
        if (value >= PreorderUtils.SOAP_RC_CLIENT_NOT_FOUND) {
            if (value.equals(PreorderUtils.SOAP_RC_CLIENT_NOT_FOUND)) {
                return new ClientGroupResult(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);
            } else if (value.equals(PreorderUtils.SOAP_RC_SEVERAL_CLIENTS_WERE_FOUND)) {
                return new ClientGroupResult(RC_SEVERAL_CLIENTS_WERE_FOUND, RC_SEVERAL_CLIENTS_WERE_FOUND_DESC);
            } else if (value.equals(PreorderUtils.SOAP_RC_PREORDERS_NOT_UNIQUE_CLIENT)) {
                return new ClientGroupResult(RC_PREORDERS_NOT_UNIQUE_CLIENT, RC_PREORDERS_NOT_UNIQUE_CLIENT_DESC);
            } else if (value.equals(PreorderUtils.SOAP_RC_WRONG_GROUP)) {
                return new ClientGroupResult(RC_WRONG_GROUP, RC_WRONG_GROUP_DESC);
            } else if (value.equals(PreorderUtils.SOAP_RC_MOBILE_DIFFERENT_GROUPS)) {
                return new ClientGroupResult(RC_MOBILE_DIFFERENT_GROUPS, RC_MOBILE_DIFFERENT_GROUPS_DESC);
            }
        }
        ClientGroupResult result = new ClientGroupResult(RC_OK, RC_OK_DESC);
        result.setValue(value);
        return result;


        /*Map<Integer, Integer> map = new HashMap<>();
        boolean isStudent = false;
        boolean isParent = false;
        boolean isTrueParent = false;
        boolean isEmployee = false;
        boolean isEmployeeParent = false;
        for (Client client : clients) {
            int type = 0;
            if (client.isStudent()) {
                type = ClientGroupResult.STUDENT;
                isStudent = true;
            }
            if (client.isParentMsk()) {
                type = ClientGroupResult.PARENT;
                isParent = true;
                isTrueParent = ClientManager.clientHasChildren(session, client.getIdOfClient());
            }
            if (client.isSotrudnikMsk()) {
                type = ClientGroupResult.EMPLOYEE;
                isEmployee = true;
                isEmployeeParent = ClientManager.clientHasChildren(session, client.getIdOfClient());
            }
            if (type == 0) continue;
            Integer count = map.get(type);
            if (count == null) count = 0;
            map.put(type, count + 1);
        }
        if (map.size() == 0) return new ClientGroupResult(RC_CLIENT_NOT_FOUND, RC_CLIENT_NOT_FOUND_DESC);

        for (Integer value : map.keySet()) {
            if (map.get(value) > 1) return new ClientGroupResult(RC_SEVERAL_CLIENTS_WERE_FOUND, RC_SEVERAL_CLIENTS_WERE_FOUND_DESC);
        }

        if ((isStudent && isParent && isEmployee) || (isEmployee && isStudent) || (isParent && isStudent)) {
            return new ClientGroupResult(RC_PREORDERS_NOT_UNIQUE_CLIENT, RC_PREORDERS_NOT_UNIQUE_CLIENT_DESC);
        }
        Integer value;
        if (isEmployeeParent && !isStudent && !isParent)
            value = ClientGroupResult.PARENT_EMPLOYEE;
        else if (isTrueParent && !isStudent && !isEmployee)
            value = ClientGroupResult.PARENT;
        else if (isEmployee && !isStudent && !isParent)
            value = ClientGroupResult.EMPLOYEE;
        else if (isStudent && !isEmployee && !isParent)
            value = ClientGroupResult.STUDENT;
        else value = null;
        if (value == null) {
            if (map.size() == 1) {
                return new ClientGroupResult(RC_WRONG_GROUP, RC_WRONG_GROUP_DESC);
            } else {
                return new ClientGroupResult(RC_MOBILE_DIFFERENT_GROUPS, RC_MOBILE_DIFFERENT_GROUPS_DESC);
            }
        }

        ClientGroupResult result = new ClientGroupResult(RC_OK, RC_OK_DESC);
        result.setValue(value);

        return result;*/
    }

    @Override
    public ClientGroupResult getTypeClients(@WebParam(name = "mobile") String mobile) {
        authenticateRequest(null);
        String mobilePhone = Client.checkAndConvertMobile(mobile);
        if (mobilePhone == null) {
            return new ClientGroupResult(RC_INVALID_DATA, RC_INVALID_MOBILE);
        }

        ClientsMobileHistory clientsMobileHistory =
                new ClientsMobileHistory("soap метод getTypeClients");
        clientsMobileHistory.setShowing("Портал");

        changeSsoid(mobile, clientsMobileHistory);

        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createExternalServicesPersistenceSession();
            transaction = session.beginTransaction();


            Query query = session.createQuery("select c from Client c where c.mobile = :mobile");
            query.setParameter("mobile", mobilePhone);
            List<Client> clients = query.list();
            ClientGroupResult result = getClientGroupResult(session, clients);
            //ClientSummaryBaseListResult result = processClientSummaryByMobileResult(session, clients, "child");

            transaction.commit();
            transaction = null;
            return result;
        } catch (Exception e) {
            return new ClientGroupResult(RC_INTERNAL_ERROR, RC_INTERNAL_ERROR_DESC);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public Result setSpecialMenu(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "value") Boolean value) {
        authenticateRequest(contractId);
        Result result = new Result();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            client.setSpecialMenu(value);
            long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
            client.setClientRegistryVersion(clientRegistryVersion);
            client.setUpdateTime(new Date());
            session.update(client);

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Error in setSpecialMenu", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public PreorderAllComplexesResult getPreorderAllComplexes(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        PreorderAllComplexesResult result = new PreorderAllComplexesResult();
        try {
            result = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .getPreordersWithMenuListSinceDate(contractId, CalendarUtils.startOfDay(new Date()));
            RegularPreordersList regularPreordersList = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .getRegularPreordersList(contractId, true);
            result.setRegularPreorders(regularPreordersList);
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Error in getPreorderAllComplexes", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public PreorderComplexesResult getPreorderComplexes(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "date") Date date) {
        authenticateRequest(contractId);
        PreorderComplexesResult result = new PreorderComplexesResult();
        Org org = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).
                getOrgByContractId(contractId);
        try {
            PreorderListWithComplexesGroupResult res;
            if (!org.getUseWebArm()) {
                res = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                        .getPreorderComplexesWithMenuList(contractId, date);
            } else {
                res = processPreorderComplexesWithWtMenuList(contractId, date);
            }
            if (res.resultCode == null || res.resultCode.equals(RC_OK)) {
                List<PreorderComplexGroup> list = res.getComplexesWithGroups();
                if (list != null && list.size() > 0) {
                    ComplexGroup complexGroup = new ComplexGroup();
                    complexGroup.setComplexesWithGroups(res.getComplexesWithGroups());
                    result.setComplexGroup(complexGroup);
                }
                RegularPreordersList regularPreordersList = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getRegularPreordersList(contractId, true);
                if (regularPreordersList.getRegularPreorders() != null
                        && regularPreordersList.getRegularPreorders().size() > 0) {
                    result.setRegularPreorders(regularPreordersList);
                }
                result.resultCode = RC_OK;
                result.description = RC_OK_DESC;
            } else {
                result.resultCode = res.resultCode;
                result.description = res.description;
            }
        } catch (Exception e) {
            logger.error("Error in getPreorderComplexes", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public Result putPreorderComplex(@WebParam(name = "preorders") PreorderParam preorders,
            @WebParam(name = "guardianMobile") String guardianMobile,
            @WebParam(name = "externalSystem") Integer externalSystem) {
        authenticateRequest(preorders.getContractId());
        Result result = new Result();
        try {
            PreorderSaveListParam preorderSaveListParam = new PreorderSaveListParam(preorders);
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                        .savePreorderComplexes(preorderSaveListParam, guardianMobile, externalSystem);
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        }  catch (MenuDetailNotExistsException | InvalidDatePreorderDishException e) {
            result.resultCode = RC_NOT_FOUND_MENUDETAIL;
            result.description = RC_NOT_FOUND_MENUDETAIL_DESC;
        } catch (NotEditedDayException e) {
            result.resultCode = RC_NOT_EDITED_DAY;
            result.description = RC_NOT_EDITED_DAY_DESC;
        } catch (RegularAlreadyDeleted e) {
            result.resultCode = RC_REGULAR_ALREADY_DELETED;
            result.description = RC_REGULAR_ALREADY_DELETED_DESC;
        } catch (RegularWrongStartDate e) {
            result.resultCode = RC_REGULAR_WRONG_START_DATE;
            result.description = RC_REGULAR_WRONG_DATE_DESC;
        } catch (RegularExistsException e) {
            result.resultCode = RC_REGULAR_EXISTS;
            result.description = RC_REGULAR_EXISTS_DESC;
        } catch (RegularRangeException e) {
            result.resultCode = RC_INVALID_CREATOR;
            result.description = RC_INVALID_REGULAR_RANGE_DESC;
        } catch (Exception e) {
            logger.error("Error in putPreorderComplex", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        }
        return result;
    }

    @Override
    public PeopleQuantityInOrgResult getPeopleQuantityByOrg(@WebParam(name = "organizationUid") String ogrn) {
        authenticateRequest(null);
        PeopleQuantityInOrgResult result = new PeopleQuantityInOrgResult();
        List<PeopleQuantityInGroup> items = new LinkedList<PeopleQuantityInGroup>();
        Session session = null;
        Transaction transaction = null;
        try {
            if (ogrn.isEmpty()) {
                throw new IllegalArgumentException("organizationUid is empty string");
            }
            Date eventDate = new Date();
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            List<Object[]> dataFromDB = DAOUtils.getNumberAllUsersInOrg(session, ogrn, eventDate);
            for (Object[] row : dataFromDB) {
                String groupname = (String) row[0];
                BigInteger quantity = (BigInteger) row[1];
                PeopleQuantityInGroup item = new PeopleQuantityInGroup();
                item.setGroupName(groupname);
                item.setQuantity(quantity);
                items.add(item);
            }
            result.setPeopleQuantity(items);
            result.setTimeUpdate(eventDate);
            result.setOrganizationUid(ogrn);
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in putPreorderComplex", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public AddRegistrationCardResult addRegistrationCard(String regid, String suid, String organizationSuid,
            String cardId, Date validdate, String firstName, String surname, String secondName, Date birthDate,
            String grade, String codeBenefit, Date startDate, Date endDate, String lsnum) {
        authenticateRequest(null);
        AddRegistrationCardResult result = new AddRegistrationCardResult();
        Session session = null;
        Transaction transaction = null;
        CardRegistrationService service = RuntimeContext.getAppContext().getBean(CardRegistrationService.class);
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByGuid(session, suid);
            Long contractId;
            try {
                contractId = Long.valueOf(lsnum);
            } catch (Exception e) {
                throw new RequiredFieldsAreNotFilledException("lsnum not specified");
            }
            Client clientByContractId = DAOUtils.findClientByContractId(session, contractId);

            if (null == client) {
                if (clientByContractId != null) {
                    throw new Exception("Client already found by contract Id");
                }
                ClientsMobileHistory clientsMobileHistory =
                        new ClientsMobileHistory("web метод addRegistrationCard");
                clientsMobileHistory.setShowing("Портал");
                client = service.registerNewClient(session, firstName, secondName, surname, birthDate, suid, regid,
                        organizationSuid, grade, codeBenefit, contractId, clientsMobileHistory);
            } else {
                if (clientByContractId != null && !client.equals(clientByContractId)) {
                    throw new Exception("Client already found by contract Id - 2");
                }
                if (!client.getContractId().equals(contractId)) {
                    client.setContractId(contractId);
                    long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
                    client.setClientRegistryVersion(clientRegistryVersion);
                    session.update(client);
                }
            }

            Org org = DAOUtils.findOrgByGuid(session, organizationSuid);

            if (null == org || !client.getOrg().getIdOfOrg().equals(org.getIdOfOrg())) {
                throw new OrganizationNotFoundException(
                        String.format("%s: guid = %s", RC_ORGANIZATION_NOT_FOUND_DESC, organizationSuid));
            }

            service.registerCard(session, Long.parseLong(cardId, 16), client);

            CardRegistrationService.ExternalInfo externalInfo = service
                    .loadExternalInfo(session, organizationSuid, suid, null);

            result.setContractId(contractId);
            result.setSupplierName(externalInfo.contragentName);
            result.setSupplierINN(externalInfo.contragentInn);
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            transaction.commit();
            transaction = null;
        } catch (ClientNotFoundException e) {
            logger.error("Error in addRegistrationCard", e);
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = RC_CLIENT_NOT_FOUND_DESC;
        } catch (OrganizationNotFoundException e) {
            logger.error("Error in addRegistrationCard", e);
            result.resultCode = RC_ORGANIZATION_NOT_FOUND;
            result.description = RC_ORGANIZATION_NOT_FOUND_DESC;
        } catch (RequiredFieldsAreNotFilledException e) {
            logger.error("Error in addRegistrationCard", e);
            result.resultCode = RC_REQUIRED_FIELDS_ARE_NOT_FILLED;
            result.description = RC_REQUIRED_FIELDS_ARE_NOT_FILLED_DESC;
        } catch (CardAlreadyUsedException e) {
            logger.error("Error in addRegistrationCard", e);
            CardRegistrationService.ExternalInfo externalInfo = service
                    .loadExternalInfo(session, organizationSuid, null, Long.parseLong(cardId, 16));
            result.setContractId(Long.valueOf(lsnum));
            result.setSupplierName(externalInfo.contragentName);
            result.setSupplierINN(externalInfo.contragentInn);
            result.resultCode = RC_ERROR_CARD_EXISTS;
            result.description = RC_ERROR_CARD_EXISTS_DESC;
        } catch (Exception e) {
            logger.error("Error in addRegistrationCard", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result setMultiCardModeForClient(@WebParam(name = "contractId") String contractId,
            @WebParam(name = "value") String value) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Result response = new Result();
        try {
            if (StringUtils.isEmpty(contractId) || StringUtils.isEmpty(value)) {
                throw new RequiredFieldsAreNotFilledException(RC_REQUIRED_FIELDS_ARE_NOT_FILLED_DESC);
            }
            if (!contractId.matches("\\d+")) {
                throw new InvalidDataException("Лицевой счет должен содержать только числовые символы");
            }
            Long contractIdLongVal = Long.parseLong(contractId);

            if (!value.toLowerCase().matches("true|false")) {
                throw new InvalidDataException(
                        "Значение для установки флага MultiCardMode должно быть задано как true или false");
            }
            Boolean multiCardModeFlag = Boolean.parseBoolean(value);

            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = DAOUtils.findClientByContractId(persistenceSession, contractIdLongVal);
            if (client == null) {
                throw new ClientNotFoundException("Не удалось найти Клиента с л/с: " + contractId);
            }

            if (client.getOrg() != null && client.getOrg().multiCardModeIsEnabled()) {
                Integer numbOfActiveCards = DAOUtils
                        .countActiveCardByIdOfClient(client.getIdOfClient(), persistenceSession);
                if (numbOfActiveCards != null && numbOfActiveCards > 1 && !multiCardModeFlag) {
                    throw new IllegalArgumentException("Клиент имеет 2 или более активные карты");
                } else {
                    client.setMultiCardMode(multiCardModeFlag);
                }
            } else {
                throw new IllegalArgumentException(
                        "ОО клиента не поддерживает функцию использования клиентами нескольких индификаторов");
            }
            long clientRegistryVersion = DAOUtils.updateClientRegistryVersion(persistenceSession);
            client.setClientRegistryVersion(clientRegistryVersion);

            persistenceSession.update(client);

            persistenceTransaction.commit();
            persistenceTransaction = null;

            response.resultCode = RC_OK;
            response.description = RC_OK_DESC;
        } catch (IllegalArgumentException e) {
            logger.error(
                    "Нарушение условий установки режима \"Использования нескольких индификаторов\" для клиента л/с "
                            + contractId, e);
            response.resultCode = RC_INTERNAL_ERROR;
            response.description = e.getMessage();
        } catch (ClientNotFoundException e) {
            logger.error("", e);
            response.resultCode = RC_CLIENT_NOT_FOUND;
            response.description = RC_CLIENT_NOT_FOUND_DESC;
        } catch (RequiredFieldsAreNotFilledException e) {
            logger.error("", e);
            response.resultCode = RC_REQUIRED_FIELDS_ARE_NOT_FILLED;
            response.description = RC_REQUIRED_FIELDS_ARE_NOT_FILLED_DESC;
        } catch (InvalidDataException e) {
            logger.error("", e);
            response.resultCode = RC_INVALID_DATA;
            response.description = e.getMessage();
        } catch (Exception e) {
            logger.error("Ошибка при попытке установить для Клиента (contractId=" + contractId
                    + ") значение для поля multiCardMode ", e);
            response.resultCode = RC_INTERNAL_ERROR;
            response.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return response;
    }

    @Override
    public CardInfo getClientCardInfo(@WebParam(name = "contractId") Long contractId) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        CardInfo result = new CardInfo();
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (client == null) {
                throw new ClientNotFoundException("Не удалось найти Клиента с л/с: " + contractId);
            }

            result.setClientHasActiveMultiCardMode(client.activeMultiCardMode());
            if (client.getOrg() != null) {
                result.setOrgEnabledMultiCardMod(client.getOrg().multiCardModeIsEnabled());
            }

            for (Card card : client.getCards()) {
                CardInfoItem item = new CardInfoItem();
                if (card.getCardType() <= 8 && card.getState().equals(CardState.ISSUED.getValue())) {
                    item.setCardPrintedNo(card.getCardPrintedNo());
                    item.setCardType(card.getCardType());
                    item.setState(card.getState());
                    item.setCardNo(card.getCardNo());
                } else if (card.getCardType() > 8) {
                    item.setCardPrintedNo(card.getCardPrintedNo());
                    item.setCardType(card.getCardType());
                    item.setState(card.getState());
                    item.setCardNo(card.getCardNo());
                } else {
                    continue;
                }
                result.getItems().add(item);
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;

            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (ClientNotFoundException e) {
            logger.error("", e);
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = RC_CLIENT_NOT_FOUND_DESC;
        } catch (Exception e) {
            logger.error("Ошибка при попытке собрать информацию об картах Клиента (contractId=" + contractId + ")", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public CashOutResult addRequestForCashOut(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "guardianMobile") String guardianMobile, @WebParam(name = "sum") Long sum,
            @WebParam(name = "guardianDataForCashOut") GuardianDataForCashOut guardianDataForCashOut) {
        authenticateRequest(contractId);
        CashOutResult result = new CashOutResult();
        Session session = null;
        Transaction transaction = null;

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }
            Query query = session.createQuery("select cg from ClientGuardian cg, Client g "
                    + "where g.idOfClient = cg.idOfGuardian and cg.idOfChildren = :idOfChildren and g.mobile = :guardianMobile "
                    + "and cg.deletedState = false and cg.disabled = false");
            query.setParameter("idOfChildren", client.getIdOfClient());
            query.setParameter("guardianMobile", guardianMobile);
            List<ClientGuardian> guardians = query.list();
            if (guardians.size() == 0) {
                result.resultCode = RC_CLIENT_GUARDIAN_NOT_FOUND;
                result.description = RC_CLIENT_GUARDIAN_NOT_FOUND_DESC;
                return result;
            }
            Long idOfGuardian = (guardians.size() == 1) ? guardians.get(0).getIdOfGuardian() : null;
            Client declarer = idOfGuardian == null ? null : (Client) session.get(Client.class, idOfGuardian);

            Date today = CalendarUtils.startOfDay(new Date());
            Date endDate = CalendarUtils.addDays(today, 13);
            endDate = CalendarUtils.endOfDay(endDate);
            Long preordersSum = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                    .getPreordersSum(client, today, endDate);
            if (client.getBalance() - preordersSum < sum) {
                result.resultCode = RC_NOT_ENOUGH_BALANCE;
                result.description = RC_NOT_ENOUGH_BALANCE_DESC;
                result.sumAvailable = client.getBalance() - preordersSum;
                return result;
            }
            String declarerInn = (guardianDataForCashOut == null) ? null : guardianDataForCashOut.getDeclarerInn();
            String declarerAccount =
                    (guardianDataForCashOut == null) ? null : guardianDataForCashOut.getDeclarerAccount();
            String declarerBank = (guardianDataForCashOut == null) ? null : guardianDataForCashOut.getDeclarerBank();
            String declarerBik = (guardianDataForCashOut == null) ? null : guardianDataForCashOut.getDeclarerBik();
            String declarerCorrAccount =
                    (guardianDataForCashOut == null) ? null : guardianDataForCashOut.getDeclarerCorrAccount();

            RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class)
                    .holdClientBalance(UUID.randomUUID().toString(), client, sum, declarer, client.getOrg(), null,
                            client.getOrg().getDefaultSupplier(), null, ClientBalanceHoldCreateStatus.PORTAL,
                            ClientBalanceHoldRequestStatus.CREATED, guardianMobile, declarerInn, declarerAccount,
                            declarerBank, declarerBik, declarerCorrAccount, null, null,
                            ClientBalanceHoldLastChangeStatus.PORTAL);

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Error in setInformedSpecialMenu", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public RequestForCashOutList getRequestForCashOutList(@WebParam(name = "contractId") Long contractId) {
        authenticateRequest(contractId);
        Session session = null;
        Transaction transaction = null;
        RequestForCashOutList result = new RequestForCashOutList();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            List<ClientBalanceHold> list = RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class)
                    .getClientBalanceHoldListByClient(session, client);
            if (list.size() > 0) {
                result.attachBalanceHoldList(list);
            }

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Error in setInformedSpecialMenu", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public Result removeRequestForCashOut(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "idOfRequest") Long idOfRequest) {
        authenticateRequest(contractId);
        Result result = new Result();
        Session session = null;
        Transaction transaction = null;

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                result.resultCode = RC_CLIENT_NOT_FOUND;
                result.description = RC_CLIENT_NOT_FOUND_DESC;
                return result;
            }

            ClientBalanceHold clientBalanceHold = (ClientBalanceHold) session.get(ClientBalanceHold.class, idOfRequest);
            if (clientBalanceHold == null || !clientBalanceHold.getClient().getContractId().equals(contractId) || !(
                    clientBalanceHold.getRequestStatus().equals(ClientBalanceHoldRequestStatus.CREATED)
                            || clientBalanceHold.getRequestStatus()
                            .equals(ClientBalanceHoldRequestStatus.SUBSCRIBED))) {
                result.resultCode = RC_REQUEST_NOT_FOUND_OR_CANT_BE_DELETED;
                result.description = RC_REQUEST_NOT_FOUND_OR_CANT_BE_DELETED_DESC;
                return result;
            }

            RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class)
                    .declineClientBalance(clientBalanceHold.getIdOfClientBalanceHold(),
                            ClientBalanceHoldRequestStatus.ANNULLED, ClientBalanceHoldLastChangeStatus.PORTAL);

            transaction.commit();
            transaction = null;
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
        } catch (Exception e) {
            logger.error("Error in setInformedSpecialMenu", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    @Override
    public CheckApplicationForFoodResult checkApplicationForFood(@WebParam(name = "clientGuid") String clientGuid,
            @WebParam(name = "meshGuid") String meshGuid) {
        CheckApplicationForFoodResult result = new CheckApplicationForFoodResult();
        if (StringUtils.isEmpty(clientGuid) && StringUtils.isEmpty(meshGuid)) {
            result.resultCode = RC_REQUIRED_FIELDS_ARE_NOT_FILLED;
            result.description = RC_REQUIRED_FIELDS_ARE_NOT_FILLED_DESC;
            return result;
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = null;
            if (!StringUtils.isEmpty(meshGuid)) {
                client = DAOUtils.findClientByMeshGuid(persistenceSession, meshGuid);
            }

            if (client == null) {
                client = DAOUtils.findClientByGuid(persistenceSession, clientGuid);
            }
            if (null == client) {
                throw new ClientNotFoundException(String.format("Unable to find client with guid={%s}", clientGuid));
            }

            ApplicationForFood applicationForFood = DAOUtils
                    .getLastApplicationForFoodByClient(persistenceSession, client);

            if (null == applicationForFood) {
                result.setApplicationExists(Boolean.FALSE);
            } else {
                if (applicationForFood.getStatus()
                        .equals(new ApplicationForFoodStatus(ApplicationForFoodState.DELIVERY_ERROR))
                        || applicationForFood.getStatus()
                        .equals(new ApplicationForFoodStatus(ApplicationForFoodState.DENIED_BENEFIT)) || applicationForFood.getStatus()
                        .equals(new ApplicationForFoodStatus(ApplicationForFoodState.DENIED_GUARDIANSHIP)) || applicationForFood.getStatus()
                        .equals(new ApplicationForFoodStatus(ApplicationForFoodState.DENIED_PASSPORT))) {
                    result.setApplicationExists(Boolean.FALSE);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss");
                    result.setApplicantName(applicationForFood.getApplicantName());
                    result.setApplicantSurname(applicationForFood.getApplicantSurname());
                    result.setApplicantSecondName(applicationForFood.getApplicantSecondName());
                    result.setRegDate(dateFormat.format(applicationForFood.getCreatedDate()));
                    result.setApplicationExists(Boolean.TRUE);
                }
            }

            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (ClientNotFoundException e) {
            logger.error(
                    String.format("Error in checkApplicationForFood while check for client with guid={%s}", clientGuid),
                    e);
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = RC_CLIENT_NOT_FOUND_DESC;
        } catch (Exception e) {
            logger.error(
                    String.format("Error in checkApplicationForFood while check for client with guid={%s}", clientGuid),
                    e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

        return result;
    }

    @Override
    public Result registerApplicationForFood(@WebParam(name = "clientGuid") String clientGuid,
            @WebParam(name = "categoryDiscount") Long categoryDiscount,
            @WebParam(name = "otherDiscount") Boolean otherDiscount,
            @WebParam(name = "guardianMobile") String guardianMobile,
            @WebParam(name = "guardianName") String guardianName,
            @WebParam(name = "guardianSurname") String guardianSurname,
            @WebParam(name = "guardianSecondName") String guardianSecondName,
            @WebParam(name = "serviceNumber") String serviceNumber) {

        String mobilePhone = Client.checkAndConvertMobile(guardianMobile);
        if (StringUtils.isEmpty(clientGuid) || (null == categoryDiscount && !otherDiscount) || StringUtils
                .isEmpty(mobilePhone) || StringUtils.isEmpty(guardianName) || StringUtils.isEmpty(guardianSurname)) {
            return new Result(RC_INVALID_DATA, RC_NOT_ALL_ARG);
        }
        if (StringUtils.isEmpty(mobilePhone)) {
            return new Result(RC_INVALID_DATA, RC_INVALID_MOBILE);
        }
        if (null == guardianSecondName) {
            guardianSecondName = "";
        }

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Result result = new Result();
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = DAOUtils.findClientByGuid(persistenceSession, clientGuid);
            if (null == client) {
                throw new ClientNotFoundException(String.format("Unable to find client with guid={%s}", clientGuid));
            }

            DAOUtils.createApplicationForFood(persistenceSession, client, otherDiscount ? null : Arrays.asList(categoryDiscount.intValue()),
                    mobilePhone, guardianName, guardianSecondName, guardianSurname, serviceNumber,
                    ApplicationForFoodCreatorType.PORTAL, null, null);
            DAOUtils.updateApplicationForFood(persistenceSession, client,
                    new ApplicationForFoodStatus(ApplicationForFoodState.REGISTERED));

            //if (!otherDiscount) {
            //    DAOUtils.updateApplicationForFood(persistenceSession, client, new ApplicationForFoodStatus(ApplicationForFoodState.PAUSED, null));
            //}
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (ClientNotFoundException e) {
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = RC_CLIENT_NOT_FOUND_DESC;
        } catch (Exception e) {
            logger.error(String.format("Error in registerApplicationForFood: clientGuid={%s}, categoryDiscount=%d, "
                            + "otherDiscount=\"%s\", guardianMobile=\"%s\"', guardianName=\"%s\", guardianSurname=\"%s\", "
                            + "guardianSecondName=\"%s\"", clientGuid, categoryDiscount, otherDiscount.toString(),
                    guardianMobile, guardianName, guardianSurname, guardianSecondName), e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public Result updateStatusOfApplicationForFood(@WebParam(name = "state") Integer stateCode,
            @WebParam(name = "declineReason") Integer declineReasonCode,
            @WebParam(name = "serviceNumber") String serviceNumber) {
        String code = stateCode.toString();
        if (declineReasonCode != null &&declineReasonCode > 0) {
            code += "." + declineReasonCode.toString();
        }
        ApplicationForFoodState state = ApplicationForFoodState.fromCode(code);
        if (state == null) {
            return new Result(RC_INVALID_DATA, "Не известный код состояния заявления");
        }
        if (StringUtils.isEmpty(serviceNumber)) {
            return new Result(RC_INVALID_DATA, "Не заполнен номер заявления");
        }
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Result result = new Result();
        ApplicationForFoodStatus status = new ApplicationForFoodStatus(state);
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            ApplicationForFood updatedApplicationForFood = DAOUtils
                    .updateApplicationForFoodByServiceNumber(persistenceSession, serviceNumber, status);
            if (updatedApplicationForFood == null) {
                throw new Exception(
                        "Result of update ApplicationForFood serviceNumber = " + serviceNumber + " is null");
            }
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;
            persistenceTransaction.commit();
            persistenceTransaction = null;
            RuntimeContext.getAppContext().getBean(ETPMVService.class)
                    .sendStatusAsync(System.currentTimeMillis() - 1000, serviceNumber, status.getApplicationForFoodState());
        } catch (Exception e) {
            logger.error(String.format("Can't update ApplicationForFood serviceNumber = %s", serviceNumber), e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public ContragentData getContragentForClient(@WebParam(name = "contractId") Long contractId) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ContragentData contragentData = new ContragentData();
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Client client = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (client == null) {
                throw new NullPointerException();
            }
            Contragent contragent = DAOUtils.getContragentbyContractId(persistenceSession, contractId);
            contragentData.setIdOfContragent(contragent.getIdOfContragent());
            contragentData.setContragentName(contragent.getContragentName());

            contragentData.getContactPerson().setSurname(contragent.getContactPerson().getSurname());
            contragentData.getContactPerson().setFirstName(contragent.getContactPerson().getFirstName());
            contragentData.getContactPerson().setSecondName(contragent.getContactPerson().getSecondName());
            contragentData.getContactPerson().setTitle(contragent.getTitle());

            contragentData.setAddress(contragent.getAddress());
            contragentData.setPhone(contragent.getPhone());
            contragentData.setMobile(contragent.getMobile());
            contragentData.setEmail(contragent.getEmail());
            contragentData.setFax(contragent.getFax());
            contragentData.setInn(contragent.getInn());
            contragentData.setBank(contragent.getBank());
            contragentData.setBic(contragent.getBic());
            contragentData.setCorrAccount(contragent.getCorrAccount());
            contragentData.setAccount(contragent.getAccount());
            contragentData.setKpp(contragent.getKpp());
            contragentData.setOgrn(contragent.getOgrn());
            contragentData.setOkato(contragent.getOkato());
            contragentData.setOktmo(contragent.getOktmo());
            contragentData.setRemarks(contragent.getRemarks());

            persistenceTransaction.commit();
            persistenceTransaction = null;

            contragentData.resultCode = RC_OK;
            contragentData.description = RC_OK_DESC;
        } catch (NullPointerException e) {
            contragentData.resultCode = 100L;
            contragentData.description = RC_CLIENT_NOT_FOUND_DESC;
        } catch (Exception e) {
            logger.error("Error in getContragentForClient", e);
            contragentData.resultCode = RC_INTERNAL_ERROR;
            contragentData.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return contragentData;
    }

    @Override
    public ETPDiscountsResult getETPDiscounts() {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        ETPDiscountsResult result = new ETPDiscountsResult();
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            ETPDiscountList discountList = new ETPDiscountList();
            List<ETPDiscountItem> itemList = new ArrayList<ETPDiscountItem>();
            List<CategoryDiscountDSZN> categoryDiscountDSZNList = DAOUtils
                    .getCategoryDiscountDSZNList(persistenceSession, true);
            for (CategoryDiscountDSZN discount : categoryDiscountDSZNList) {

                itemList.add(new ETPDiscountItem(discount));
            }
            discountList.setItemList(itemList);
            result.setDiscountList(discountList);
            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Error in getETPDiscounts", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public Result blockActiveCardByCardNoAndContractId(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "cardNo") Long cardNo) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Result result = new Result();
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (client == null) {
                throw new ClientNotFoundException("Не найден клиент с л/с " + contractId);
            }

            Criteria criteria = persistenceSession.createCriteria(Card.class);
            criteria.add(Restrictions.eq("cardNo", cardNo));
            criteria.add(Restrictions.eq("client", client));
            criteria.add(Restrictions.eq("state", Card.ACTIVE_STATE));

            Card card = (Card) criteria.uniqueResult();
            if (card == null) {
                throw new CardNotFoundException(
                        "У клиента с л/с " + contractId + " нет активной карты с UID " + cardNo);
            }

            CardManager cardManager = RuntimeContext.getInstance().getCardManager();
            cardManager.updateCard(client.getIdOfClient(), card.getIdOfCard(), card.getCardType(),
                    CardState.BLOCKED.getValue(), card.getValidTime(), card.getLifeState(),
                    CardLockReason.OTHER.getDescription(), card.getIssueTime(), card.getExternalId());

            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (ClientNotFoundException e) {
            logger.error("Error in blockActiveCardByCardNoAndContractId", e);
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = RC_CLIENT_NOT_FOUND_DESC;
        } catch (CardNotFoundException e) {
            logger.error("Error in blockActiveCardByCardNoAndContractId", e);
            result.resultCode = RC_CARD_NOT_FOUND;
            result.description = RC_CARD_NOT_FOUND_DESC;
        } catch (Exception e) {
            logger.error("Error in blockActiveCardByCardNoAndContractId", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    @Override
    public Result extendValidDateOfCard(@WebParam(name = "contractId") Long contractId,
            @WebParam(name = "UID") Long cardNo) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        Result result = new Result();
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            Client client = DAOUtils.findClientByContractId(persistenceSession, contractId);
            if (client == null) {
                throw new ClientNotFoundException("Не найден клиент с л/с " + contractId);
            }

            Criteria criteria = persistenceSession.createCriteria(Card.class);
            criteria.add(Restrictions.eq("cardNo", cardNo));
            criteria.add(Restrictions.eq("client", client));

            Card card = (Card) criteria.uniqueResult();
            if (card == null) {
                throw new CardNotFoundException("У клиента с л/с " + contractId + " нет карты с UID " + cardNo);
            } else if (!(card.getState().equals(CardState.ISSUED.getValue()) || card.getState()
                    .equals(CardState.TEMPISSUED.getValue()))) {
                throw new CardWrongStateException("Card must have state ISSUED or TEMPISSUED");
            }

            Integer period = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_PERIOD_OF_EXTENSION_CARDS);
            Date newValidDate = CalendarUtils.addYear(new Date(), period);

            CardManager cardManager = RuntimeContext.getInstance().getCardManager();
            cardManager.updateCardInSession(persistenceSession, client.getIdOfClient(), card.getIdOfCard(),
                    card.getCardType(), card.getState(), newValidDate, card.getLifeState(),
                    CardLockReason.EMPTY.getDescription(), card.getIssueTime(), card.getExternalId(), null,
                    card.getOrg().getIdOfOrg(), "Дата окончания изменена внешней системой", false);

            result.resultCode = RC_OK;
            result.description = RC_OK_DESC;

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (ClientNotFoundException e) {
            logger.error("Error in extendValidDateOfCard", e);
            result.resultCode = RC_CLIENT_NOT_FOUND;
            result.description = RC_CLIENT_NOT_FOUND_DESC;
        } catch (CardNotFoundException e) {
            logger.error("Error in extendValidDateOfCard", e);
            result.resultCode = RC_CARD_NOT_FOUND;
            result.description = RC_CARD_NOT_FOUND_DESC;
        } catch (CardWrongStateException e) {
            logger.error("Error in extendValidDateOfCard", e);
            result.resultCode = RC_WRONG_STATE_OF_CARD;
            result.description = RC_WRONG_STATE_OF_CARD_DESC;
        } catch (Exception e) {
            logger.error("Error in extendValidDateOfCard", e);
            result.resultCode = RC_INTERNAL_ERROR;
            result.description = RC_INTERNAL_ERROR_DESC;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return result;
    }

    private void changeSsoid(String clientMobile, ClientsMobileHistory clientsMobileHistory)
    {
        Session session = null;
        Transaction transaction = null;
        try {
            String cientMobile =Client.checkAndConvertMobile(clientMobile);
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Map<String, List> headers = (Map<String, List>) context.getMessageContext().get(Message.PROTOCOL_HEADERS);
            List<String> ssoids = headers.get("User_ssoid");
            String ssoid = "";
            if (ssoids != null && !ssoids.isEmpty()) {
                ssoid = ssoids.get(0).trim();
                if (!ssoid.isEmpty()) {
                    List<Client> clients = DAOReadonlyService.getInstance().getClientsListByMobilePhone(cientMobile);
                    List<Client> clientsSsoid = DAOReadonlyService.getInstance().getClientsBySoid(ssoid);
                    for (Client client : clients) {
                        if (client.getSsoid() == null || !client.getSsoid().equals(ssoid)) {
                            client.setSsoid(ssoid);
                            client.setUpdateTime(new Date());
                            session.update(client);
                        }
                    }
                    for (Client client : clientsSsoid) {
                        if (client.getMobile() == null || !client.getMobile().equals(cientMobile)) {
                            client.initClientMobileHistory(clientsMobileHistory);
                            client.setMobileNotClearSsoid(cientMobile);
                            client.setUpdateTime(new Date());
                            session.update(client);
                        }
                    }
                }
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error(String.format("Error work with ssoid. guardMobile = %s",
                    clientMobile), e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}
