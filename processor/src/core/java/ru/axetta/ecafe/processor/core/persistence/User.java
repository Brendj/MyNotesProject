/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ProcessorUtils;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.axetta.ecafe.processor.core.utils.RequestUtils;

import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 02.06.2009
 * Time: 10:39:31
 * To change this template use File | Settings | File Templates.
 */
public class User {
    public static final String USER_ID_ATTRIBUTE_NAME = "ru.axetta.ecafe.userId";
    public static final String USER_IP_ADDRESS_ATTRIBUTE_NAME = "ru.axetta.ecafe.ipAddress";

    private static final String PASS_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String PASS_SPECIAL_SYMBOLS = "0123456789[]{},.<>;:|\\/?!`~@#$%^&*()-_=+";
    private static final int MIN_PASSWORD_LENGTH = 6;

    private static boolean successfullySendEMP = false;

    protected static Logger logger;
    static {
        try {  logger = LoggerFactory.getLogger(User.class); } catch (Throwable ignored) {}
    }

    public static boolean isSuccessfullySendEMP() {
        return successfullySendEMP;
    }

    public static void setSuccessfullySendEMP(boolean successfullySendEMP) {
        User.successfullySendEMP = successfullySendEMP;
    }

    public String getLastSmsCode() {
        return lastSmsCode;
    }

    public void setLastSmsCode(String lastSmsCode) {
        this.lastSmsCode = lastSmsCode;
    }

    public Date getSmsCodeEnterDate() {
        return smsCodeEnterDate;
    }

    public void setSmsCodeEnterDate(Date smsCodeDate) {
        this.smsCodeEnterDate = smsCodeDate;
    }

    public Date getSmsCodeGenerateDate() {
        return smsCodeGenerateDate;
    }

    public void setSmsCodeGenerateDate(Date smsCodeGenerateDate) {
        this.smsCodeGenerateDate = smsCodeGenerateDate;
    }

    public Boolean getNeedChangePassword() {
        return needChangePassword || (CalendarUtils.getDifferenceInDays(passwordDate, new Date(System.currentTimeMillis())) >
                RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_PERIOD_PASSWORD_CHANGE));
    }

    public void setNeedChangePassword(Boolean needChangePassword) {
        this.needChangePassword = needChangePassword;
    }

    public void doChangePassword(String plainPassword) throws Exception {
        if (this.cypheredPassword.equals(encryptPassword(plainPassword))) {
            throw new CredentialException("Не допускается использовать пароль, совпадающий с действующим на данный момент");
        }
        this.setPassword(plainPassword);
    }

    public static void changePasswordExternal(String userName, String newPassword, String newPasswordConfirm, String remoteAddr) throws Exception {
        User user = DAOReadonlyService.getInstance().findUserByUserName(userName);
        try {
            if (!newPassword.equals(newPasswordConfirm)) {
                throw new CredentialException("Неверный ввод пароля: введенные значения не совпадают");
            }
            if (!User.passwordIsEnoughComplex(newPassword)) {
                throw new CredentialException("Пароль не удовлетворяет требованиям безопасности: минимальная длина - 6 символов, "
                        + "должны присутствовать прописные и заглавные латинские буквы + хотя бы одна цифра или спецсимвол");
            }

            user.doChangePassword(newPassword);
            user.setNeedChangePassword(false);
            user.setPasswordDate(new Date(System.currentTimeMillis()));
            DAOService.getInstance().setUserInfo(user);
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.CHANGE_GRANTS, remoteAddr,
                            userName, user, true, null, null);
            DAOService.getInstance().writeAuthJournalRecord(record);
        } catch (CredentialException e) {
            SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                    .createUserEditRecord(SecurityJournalAuthenticate.EventType.CHANGE_GRANTS, remoteAddr,
                            userName, user, false,
                            SecurityJournalAuthenticate.DenyCause.USER_EDIT_BAD_PARAMETERS.getIdentification(), e.getMessage());
            DAOService.getInstance().writeAuthJournalRecord(record);
            throw e;
        }
    }

    public static void checkSmsCode(String userName, String smsCode) throws Exception {
        User user = DAOReadonlyService.getInstance().findUserByUserName(userName);
        String reqCode = user.getLastSmsCode();
        if (reqCode != null && smsCode != null && reqCode.equals(smsCode)) {
            user.setSmsCodeEnterDate(new Date(System.currentTimeMillis()));
            DAOService.getInstance().setUserInfo(user);
        } else {
            throw new CredentialException("Введен неверный код активации");
        }
    }

    public static boolean sendSmsAgain(String userName) throws Exception {
        logger.info(String.format("Start of sending SMS code for the user %s", userName));
        Boolean requstSMS = User.requestSmsCode(userName);
        logger.info(String.format("End of sending SMS code for the user %s", userName));
        return requstSMS;
    }

    public Date getPasswordDate() {
        return passwordDate;
    }

    public void setPasswordDate(Date passwordDate) {
        this.passwordDate = passwordDate;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public User incAttemptNumbersAndBlock() {
        Integer currentAttempts = attemptNumber == null ? 0 : attemptNumber;
        this.setAttemptNumber(currentAttempts + 1);
        if (attemptNumber > RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_MAX_AUTH_FAULT_COUNT)) {
            this.setBlocked(true);
            this.setBlockedUntilDate(new Date(System.currentTimeMillis() + RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_TMP_BLOCK_ACC_TIME) * 60 * 1000));
        }
        return DAOService.getInstance().setUserInfo(this);
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getIdOfGroup() {
        return idOfGroup;
    }

    public void setIdOfGroup(Long idOfGroup) {
        this.idOfGroup = idOfGroup;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean group) {
        isGroup = group;
    }

    public enum WebArmRole {
        WA_OPP(10000,"Ответственный за питание"),
        WA_OEE(10001,"Ответственный за проход"),
        WA_OPP_OEE(10002,"Ответственный за питание и проход");

        private Integer identification;
        private String description;

        WebArmRole(Integer identification, String description) {
            this.description = description;
            this.identification = identification;
        }

        static Map<Integer, WebArmRole> integerDefaultRoleMap = new HashMap<Integer, WebArmRole>();
        static {
            for (WebArmRole role: WebArmRole.values()){
                integerDefaultRoleMap.put(role.identification, role);
            }
        }

        public static WebArmRole parse(Integer identification){
            return integerDefaultRoleMap.get(identification);
        }

        public Integer getIdentification() {
            return identification;
        }

        public String getDescription() { return description; }
    }

    public enum DefaultRole{
        DEFAULT(0,"Настраиваемая роль"),
        ADMIN(1,"Администратор"),
        SUPPLIER(2,"Поставщик питания"),
        MONITORING(3,"Мониторинг"),
        ADMIN_SECURITY(4, "Администратор ИБ"),
        SUPPLIER_REPORT(5, "Отчетность поставщика питания"),
        CARD_OPERATOR(6, "Оператор по картам"),
        DIRECTOR(7, "Директор школы"),
        PRODUCTION_DIRECTOR(8, "Заведующий производством"),
        INFORMATION_SYSTEM_OPERATOR(9,"Оператор ИС"),
        CLASSROOM_TEACHER(10,"Классный руководитель"),
        CLASSROOM_TEACHER_WITH_FOOD_PAYMENT(11,"Классный руководитель с оплатой питания"),
        WA_ADMIN_SECURITY(12, "Администратор ИБ (веб арм админа)");


        private Integer identification;
        private String description;

        static Map<Integer,DefaultRole> integerDefaultRoleMap = new HashMap<Integer, DefaultRole>();
        static {
            for (DefaultRole defaultRole: DefaultRole.values()){
                integerDefaultRoleMap.put(defaultRole.identification,defaultRole);
            }
        }

        private DefaultRole(Integer identification, String description) {
            this.description = description;
            this.identification = identification;
        }

        public static DefaultRole parse(Integer identification){
            return integerDefaultRoleMap.get(identification);
        }

        public Integer getIdentification() {
            return identification;
        }

        public static Boolean isDefault(Integer identification){
            return DEFAULT.equals(integerDefaultRoleMap.get(identification));
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private Long idOfUser;
    private long version;
    private String userName;
    private String cypheredPassword;
    private String phone;
    private Date updateTime;
    private Set<Contragent> contragents = new HashSet<Contragent>();
    private Set<Function> functions = new HashSet<Function>();
    private String email;
    private Integer idOfRole;
    private String roleName;
    private String lastEntryIP;
    private Date lastEntryTime;
    private Boolean blocked;
    private Boolean deletedState;
    private Date deleteDate;
    private String lastSmsCode;
    private Date smsCodeEnterDate;
    private Date smsCodeGenerateDate;
    private Boolean needChangePassword;
    private String region;
    private Set<UserOrgs> userOrgses = new HashSet<UserOrgs>();
    private Date blockedUntilDate;
    private Date passwordDate;
    private Integer attemptNumber;
    private Person person;
    private String department;
    private Long idOfGroup;
    private Boolean isGroup;
    private Org org;
    private Client client;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getIdOfRole() {
        return idOfRole;
    }

    public void setIdOfRole(Integer idOfRole) {
        this.idOfRole = idOfRole;
    }

    public Boolean isDefaultRole(){
         return DefaultRole.isDefault(idOfRole);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    protected User() {
        // For Hibernate
    }

    public User(String userName, String plainPassword, String phone, Date updateTime) throws Exception {
        this.userName = userName;
        this.cypheredPassword = encryptPassword(plainPassword);
        this.phone = phone;
        this.updateTime = updateTime;
        this.deletedState = false;
        this.person = null;
        this.isGroup = false;
    }

    public User(String userName) throws Exception {
        this.userName = userName;
        this.cypheredPassword = encryptPassword("dsfkjsd4783249#($*&Q)(*#sdfs-=212%^(#@HHSO@@#Upi");
        this.phone = "";
        this.idOfRole = DefaultRole.DEFAULT.getIdentification();
        this.updateTime = new Date();
        this.deletedState = false;
        this.person = null;
        this.isGroup = true;
    }

    public boolean isWebArmUser() {
        return idOfRole.equals(DefaultRole.WA_ADMIN_SECURITY.getIdentification())
                || idOfRole.equals(WebArmRole.WA_OPP.getIdentification())
                || idOfRole.equals(WebArmRole.WA_OEE.getIdentification())
                || idOfRole.equals(WebArmRole.WA_OPP_OEE.getIdentification());
    }

    public Long getIdOfUser() {
        return idOfUser;
    }

    private void setIdOfUser(Long idOfUser) {
        // For Hibernate
        this.idOfUser = idOfUser;
    }

    private long getVersion() {
        // For Hibernate only
        return version;
    }

    private void setVersion(long version) {
        // For Hibernate only
        this.version = version;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String getCypheredPassword() {
        // For Hibernate
        return cypheredPassword;
    }

    private void setCypheredPassword(String cypheredPassword) throws Exception {
        // For Hibernate
        this.cypheredPassword = cypheredPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Set<Contragent> getContragents() {
        return contragents;
    }

    public void setContragents(Set<Contragent> contragents) {
        this.contragents = contragents;
    }

    private Set<Function> getFunctionsInternal() {
        // For Hibernate only 
        return functions;
    }

    private void setFunctionsInternal(Set<Function> functions) {
        // For Hibernate only
        this.functions = functions;
    }

    public Set<Function> getFunctions() {
        return getFunctionsInternal();
    }

    public void setFunctions(Set<Function> functions) {
        this.functions = functions;
    }

    public void setPassword(String plainPassword) throws Exception {
        this.cypheredPassword = encryptPassword(plainPassword);
    }

    public String getLastEntryIP() {
        return lastEntryIP;
    }

    public void setLastEntryIP(String lastEntryIP) {
        this.lastEntryIP = lastEntryIP;
    }

    public Date getLastEntryTime() {
        return lastEntryTime;
    }

    public void setLastEntryTime(Date lastEntryTime) {
        this.lastEntryTime = lastEntryTime;
    }

    public Boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean hasPassword(String plainPassword) throws Exception {
        return StringUtils.equals(this.cypheredPassword, encryptPassword(plainPassword));
    }

    public boolean hasFunction(Session session, String functionName) throws Exception {
        List functions = session.createFilter(getFunctionsInternal(), "where this.functionName = ?")
                .setString(0, functionName).list();
        return !functions.isEmpty();
    }

    public boolean hasFunction(String functionName) throws Exception {
        Set<Function> functions = getFunctionsInternal();
        for (Function f : functions) {
            if (f.getFunctionName().equals(functionName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSecurityAdmin() {
        return idOfRole.equals(DefaultRole.ADMIN_SECURITY.ordinal());
    }

    public boolean isAdmin() {
        return idOfRole.equals(DefaultRole.ADMIN.ordinal());
    }

    public boolean isSupplier() {
        return idOfRole.equals(DefaultRole.SUPPLIER.ordinal());
    }

    public boolean isCardOperator() {
        return idOfRole.equals(DefaultRole.CARD_OPERATOR.ordinal());
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Set<UserOrgs> getUserOrgses() {
        return userOrgses;
    }

    public void setUserOrgses(Set<UserOrgs> userOrgses) {
        this.userOrgses = userOrgses;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public Date getBlockedUntilDate() {
        return blockedUntilDate;
    }

    public void setBlockedUntilDate(Date blockedUntilDate) {
        this.blockedUntilDate = blockedUntilDate;
    }

    public Org getOrg() { return org; }

    public void setOrg(Org org) { this.org = org; }

    public Client getClient(){ return client; }

    public void setClient(Client client) { this.client = client; }

    /*
    * Если прошел срок, в течение которого было запрещено создавать пользователя с ранее существующим аккаунтом, то
    * отправляем удаленного пользователя в архив (добавляем к имени _archieved_XXX
    */
    public static void testAndMoveToArchieve(User user, Session session) throws Exception {
        if (!user.getDeletedState()) {
            throw new RuntimeException("Уже существует пользователь с таким именем");
        } else {
            Date d = user.getDeleteDate();
            Integer days = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_PERIOD_BLOCK_LOGIN_REUSE);
            if (CalendarUtils.getDifferenceInDays(d, new Date(System.currentTimeMillis())) < days) {
                throw new RuntimeException("Пользователь с выбранным именем не может быть создан. Введите другое имя пользователя");
            } else {
                Integer postfix = 0;
                String newName = user.getUserName() + "_archieved_" + postfix.toString();
                User u = DAOUtils.findUser(session, newName);
                while (u != null) {
                    postfix++;
                    newName = newName + postfix.toString(); //user.getUserName() + "_archieved_" + postfix.toString();
                    u = DAOUtils.findUser(session, newName);
                }
                user.setUserName(newName);
                session.save(user);
                session.flush();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        final User user = (User) o;
        return idOfUser.equals(user.getIdOfUser());
    }

    @Override
    public int hashCode() {
        return idOfUser.hashCode();
    }

    @Override
    public String toString() {
        return "User{" + "idOfUser=" + idOfUser + ", userName='" + userName + '\'' + ", cypheredPassword='"
                + cypheredPassword + '\'' + ", phone='" + phone + '\'' + ", updateTime=" + updateTime + ", contragents="
                + contragents.toString() + '}';
    }

    private static String encryptPassword(String plainPassword) throws NoSuchAlgorithmException, IOException {
        MessageDigest hash = MessageDigest.getInstance("SHA1");
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(plainPassword.getBytes());
        DigestInputStream digestInputStream = new DigestInputStream(arrayInputStream, hash);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(digestInputStream, arrayOutputStream);
        return new String(Base64.encodeBase64(arrayOutputStream.toByteArray()), CharEncoding.US_ASCII);
    }

    public static boolean isNeedChangePassword(String userName) throws Exception {
        User user = DAOReadonlyService.getInstance().findUserByUserName(userName);
        return user.getNeedChangePassword();
    }

    public static boolean needEnterSmsCode(String userName) throws Exception {
        if (RuntimeContext.getInstance().getPropertiesValue("ecafe.processor.userCode.service.enabled", "true").equals("false")) return false;
        User user = DAOReadonlyService.getInstance().findUserByUserName(userName);
        if (StringUtils.isEmpty(user.getPhone()) || user.getPhone().equals("''")) {
            return false; //если не указан номер телефона, то и смс отправить некуда.
        }
        Integer days = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_PERIOD_SMS_CODE_ALIVE);
        boolean needRegenerateCode = false;
        if (StringUtils.isEmpty(user.getLastSmsCode()) || user.getSmsCodeGenerateDate() == null ||
                CalendarUtils.getDifferenceInDays(user.getSmsCodeGenerateDate(), new Date(System.currentTimeMillis())) > days) {
            needRegenerateCode = true;
        }
        if (needRegenerateCode) {
            logger.info(String.format("Start of sending SMS code for the user %s", userName));
            setSuccessfullySendEMP(requestSmsCode(userName));
            logger.info(String.format("End of sending SMS code for the user %s", userName));
            return true;
        }
        if (user.getSmsCodeEnterDate() == null) {
            //Если не пробовали достучаться до ЕМП, то считаем, что получилось
            setSuccessfullySendEMP(true);
            return true;
        }

        if (CalendarUtils.getDifferenceInDays(user.getSmsCodeEnterDate(), new Date(System.currentTimeMillis())) < days) {
            return false;
        } else {
            //Если не пробовали достучаться до ЕМП, то считаем, что получилось
            setSuccessfullySendEMP(true);
            return true;
        }
    }

    public static boolean requestSmsCode(String userName) throws Exception {
        User user = DAOReadonlyService.getInstance().findUserByUserName(userName);
        if (user == null) {
            logger.error(String.format("Cannot find user %s", userName));
            throw new Exception(String.format("Cannot find user %s", userName));
        }
        String code = ProcessorUtils.generateSmsCode();
        logger.info(String.format("Code generated successfully for user %s", userName));
        /*Client fakeClient = createFakeClient(user.getPhone());
        RuntimeContext.getAppContext().getBean(EventNotificationService.class)
                .sendMessageAsync(fakeClient, EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED,
                        new String[]{"linkingToken", code}, new Date());*/
        String errCode = sendServiceSMSRequest(user, code);
        if (errCode.equals("0")) {
            user.setLastSmsCode(code);
            user.setSmsCodeEnterDate(null);
            user.setSmsCodeGenerateDate(new Date(System.currentTimeMillis()));
            DAOService.getInstance().setUserInfo(user);
            logger.info(String.format("User %s data updated", userName));
            return true;
        } else {
            logger.error(String.format("Error sending SMS message. Service response:%s", errCode));
            return false;
        }
    }

    //todo Временная заглушка. В продакшене должен использоваться метод серверного обращения к сервису смс ниже - sendServiceSMSRequest
    public static String getStubSMS(String userName) {
        try {
            User user = DAOReadonlyService.getInstance().findUserByUserName(userName);
            if (user == null) {
                return "#";
            }
            return "http://utils.services.altarix.ru:8000/sms/output/?login=i-teco&passwd=e2GDH0&service=14&msisdn=".concat(user.getPhone())
                    .concat("&text=Код авторизации - ").concat(user.getLastSmsCode()).concat("&operation=send&type=sms");
        } catch (Exception e) {
            return "#";
        }
    }

    private static String sendServiceSMSRequest(final User user, String code) throws Exception {
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        Integer days = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_PERIOD_SMS_CODE_ALIVE);
        String comment = String.format("Период действия кода - %s дней", days);
        SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                .createUserEditRecord(SecurityJournalAuthenticate.EventType.GENERATE_SMS, request.getRemoteAddr(),
                        user.getUserName(), user, true, null, comment);
        DAOService.getInstance().writeAuthJournalRecord(record);
        logger.info("Successfully created a record in the database about the user login");
        if (RuntimeContext.getInstance().getSmsUserCodeSender() != null) {
            return RuntimeContext.getInstance().getSmsUserCodeSender().sendCodeAndGetError(user, code);
        }
        return "SMS sending service is not defined in the system";
    }



    public boolean loginAllowed() {
        if (idOfRole.equals(DefaultRole.ADMIN_SECURITY.getIdentification())) {
            return true; //для роли администратора безопасности проверку на необходимость блокировки по времени неактивности не выполняем
        }
        Integer days = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_SECURITY_PERIOD_BLOCK_UNUSED_LOGIN_AFTER);
        if (lastEntryTime == null && CalendarUtils.getDifferenceInDays(getUpdateTime(), new Date(System.currentTimeMillis())) > days) {
            block();
            return false; //пользователь никогда не выполнял вход и создан более days дней назад, блокируем его
        }
        if (lastEntryTime == null) {
            return true; //новый пользователь, создан менее days назад
        }
        if (CalendarUtils.getDifferenceInDays(lastEntryTime, new Date(System.currentTimeMillis())) > days) {
            block();
            return false;
        }
        return true;
    }

    private void block() {
        this.setBlocked(true);
        this.setBlockedUntilDate(new Date(System.currentTimeMillis() + CalendarUtils.FIFTY_YEARS_MILLIS));
        DAOService.getInstance().setUserInfo(this);

        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        User currentUser = DAOReadonlyService.getInstance().getUserFromSession();
        String currentUserName = (currentUser == null) ? null : currentUser.getUserName();
        String comment = String.format("Пользователь %s заблокирован", userName);
        SecurityJournalAuthenticate record = SecurityJournalAuthenticate
                .createUserEditRecord(SecurityJournalAuthenticate.EventType.BLOCK_USER, request.getRemoteAddr(),
                        currentUserName, currentUser, true,
                        SecurityJournalAuthenticate.DenyCause.USER_EDIT_BAD_PARAMETERS.getIdentification(), comment);
        DAOService.getInstance().writeAuthJournalRecord(record);
    }

    public boolean blockedDateExpired() {
        if ((blockedUntilDate == null) || (new Date().before(blockedUntilDate))) return false;
        this.setBlocked(false);
        this.setBlockedUntilDate(null);
        this.setAttemptNumber(0);
        //DAOService.getInstance().setUserInfo(this);
        return true;
    }

    public static boolean passwordIsEnoughComplex(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        boolean hasLetters = false;
        boolean hasLettersCaps = false;
        boolean hasSpecialSymbols = false;
        for (int i = 0; i < password.length(); i++) {
            if (PASS_LETTERS.contains(password.subSequence(i,i+1))) {
                hasLetters = true;
            }
            if (PASS_LETTERS.toUpperCase().contains(password.subSequence(i,i+1))) {
                hasLettersCaps = true;
            }
            if (PASS_SPECIAL_SYMBOLS.contains(password.subSequence(i,i+1))) {
                hasSpecialSymbols = true;
            }
        }
        return hasLetters && hasLettersCaps && hasSpecialSymbols;
    }
}
