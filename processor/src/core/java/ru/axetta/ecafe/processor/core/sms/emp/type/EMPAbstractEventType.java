/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 26.08.14
 * Time: 18:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class EMPAbstractEventType implements EMPEventType {
    protected static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public static final String CLIENT_GENDER_VALUE_MALE = "male";
    public static final String CLIENT_GENDER_VALUE_FEMALE = "female";
    protected static final int STREAM = 124;
    protected static final int INFORMATION_STREAM = 125;   //Информационный стрим
    protected int previousId;
    protected int id;
    protected int type;
    protected String name;
    protected String text;
    protected int stream;
    protected String ssoid;
    protected Long msisdn;
    protected long time;
    protected Map<String, String> params;

    public EMPAbstractEventType() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        if(params != null && type != EMPEventTypeFactory.ENTER_LIBRARY) {
            params.put("time", new SimpleDateFormat("HH:mm").format(new Date(time)));
        }
    }

    public int getPreviousId() {
        return previousId;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getStream() {
        return stream;
    }

    public Map<String, String> getParameters() {
        if(params == null) {
            params = new HashMap<String, String>();
        }
        return params;
    }

    public String getSsoid() {
        return ssoid;
    }

    public Long getMsisdn() {
        return msisdn;
    }

    public String buildText() {
        return buildText(false);
    }

    @Override
    public String buildText(boolean buildWithParams) {
        if(text == null || text.trim().length() < 1) {
            return "";
        }

        String result = new String(text);
        for(String k : params.keySet()) {
            String v = params.get(k);
            result = result.replaceAll("%" + k + "%", v);
        }
        return result;
    }

    protected void parseClientSimpleInfo(Client client, int type) {
        ssoid = client.getSsoid();
        if(!StringUtils.isBlank(client.getMobile()) && NumberUtils.isNumber(client.getMobile())) {
            msisdn = NumberUtils.toLong(client.getMobile().replaceAll("-", ""));
        }
        Person person = null;
        try {
            person = client.getPerson();
            if(person.getFirstName() == null) {
                person = null;
            }
        } catch (Exception e) {
            person = null;
        }
        if(person == null) {
            person = DAOService.getInstance().getPersonByClient(client);
        }
        Map<String, String> params = getParameters();

        Date currentDate = new Date(System.currentTimeMillis());


        params.put("surname", person.getSurname());
        params.put("name", person.getFirstName());
        params.put("account", "" + client.getContractId());

        if (type == EMPEventTypeFactory.ENTER_LIBRARY)
            params.put("gender", getGender(client));

        if (type != EMPEventTypeFactory.ENTER_LIBRARY) {
            params.put("date", DATE_FORMAT.format(currentDate));
            params.put("time", TIME_FORMAT.format(currentDate));
            if (client.getOrg() != null) {
                appendOrgParameters(client.getOrg().getIdOfOrg(), params);
            }
            appendBalance(client.getBalance(), params);
        }
    }

    protected void parseChildAndGuardianInfo(Client child, Client guardian, int type) {
        ssoid = guardian.getSsoid();
        if(!StringUtils.isBlank(guardian.getMobile()) && NumberUtils.isNumber(guardian.getMobile())) {
            msisdn = NumberUtils.toLong(guardian.getMobile().replaceAll("-", ""));
        }
        Person person = null;
        try {
            person = child.getPerson();
            if(person.getFirstName() == null) {
                person = null;
            }
        } catch (Exception e) {
            person = null;
        }
        if(person == null) {
            person = DAOService.getInstance().getPersonByClient(child);
        }
        Map<String, String> params = getParameters();

        Date currentDate = new Date(System.currentTimeMillis());

        params.put("account", "" + child.getContractId());
        params.put("surname", person.getSurname());
        params.put("name", person.getFirstName());

        if (type == EMPEventTypeFactory.ENTER_LIBRARY)
            params.put("gender", getGender(child));

        if (type != EMPEventTypeFactory.ENTER_LIBRARY) {
            params.put("date", DATE_FORMAT.format(currentDate));
            params.put("time", TIME_FORMAT.format(currentDate));
            appendOrgParameters(child.getOrg().getIdOfOrg(), params);
            appendBalance(child.getBalance(), params);
        }
    }

    protected void appendOrgParameters(Long idOfOrg, Map<String, String> params) {
        Org org = DAOService.getInstance().findOrById(idOfOrg);
        params.put("OrgName", getOrgName(org));
        params.put("OrgType", getOrgType(org));
        params.put("OrgId", getOrgId(org));
        params.put("OrgNum", getOrgNumber(org));
    }

    protected void appendBalance(Long balance, Map<String, String> params) {
        if(balance == null || balance.longValue() == 0L) {
            params.put("balance", "0,00");
        } else {
            String bal = Long.toString(balance/100) + ',' + Long.toString(Math.abs(balance)%100);
            params.put("balance", bal);
        }
    }

    @Override
    public String toString() {
        return buildText();
    }

    public String toFullString() {
        String result = buildText();
        result += String.format(", type=%s", type);
        for(String k : params.keySet()) {
            String v = params.get(k);
            result += String.format(", param %s=%s", k, v);
        }
        return result;
    }

    protected String getOrgName(Org org) {
        return org.getShortName();
    }

    protected String getOrgType(Org org) {
        return org.getType().toString();
    }

    protected String getOrgId(Org org) {
        return "" + org.getIdOfOrg();
    }

    protected String getOrgNumber(Org org) {
        String name = org.getShortName();
        String number = Org.extractOrgNumberFromName(name);
        return number;
    }

    protected static String findValueInParams(String valueNames[], String values[]) {
        if(valueNames == null || valueNames.length < 1) {
            return "";
        }
        for(int i=0; i<values.length-1; i+=2) {
            String name = values [i];
            String val = values[i+1];
            for(String vn : valueNames) {
                if(name.equals(vn)) {
                    return val;
                }
            }
        }
        return "";
    }

    private String getGender(Client client)
    {
        String genderString = "";
        switch (client.getGender()) {
            case 0: genderString = CLIENT_GENDER_VALUE_FEMALE; break;
            case 1: genderString = CLIENT_GENDER_VALUE_MALE; break;
            default: genderString = "";
        }
        return genderString;
    }
}