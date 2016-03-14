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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
        if(params != null) {
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

    protected void parseClientSimpleInfo(Client client) {
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

        params.put("date", DATE_FORMAT.format(currentDate));
        params.put("time", TIME_FORMAT.format(currentDate));
        params.put("account", "" + client.getContractId());
        params.put("surname", person.getSurname());
        params.put("name", person.getFirstName());

        Org org = client.getOrg();
        params.put("OrgName", getOrgName(org));
        params.put("OrgType", getOrgType(org));
        params.put("OrgId", getOrgId(org));
        params.put("OrgNum", getOrgNumber(org));

        BigDecimal balance = null;
        if(client.getBalance() == null || client.getBalance().longValue() == 0L) {
            balance = new BigDecimal(0D).setScale(2);
        } else {
            balance = new BigDecimal(Math.ceil((double) client.getBalance() / 100)).setScale(2, RoundingMode.CEILING);
        }
        params.put("balance", balance.toString());
    }

    protected void parseChildAndGuardianInfo(Client child, Client guardian) {
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

        params.put("date", DATE_FORMAT.format(currentDate));
        params.put("time", TIME_FORMAT.format(currentDate));
        params.put("account", "" + child.getContractId());
        params.put("surname", person.getSurname());
        params.put("name", person.getFirstName());
        BigDecimal balance = null;
        if(child.getBalance() == null || child.getBalance().longValue() == 0L) {
            balance = new BigDecimal(0D).setScale(2);
        } else {
            balance = new BigDecimal(Math.ceil((double) child.getBalance() / 100)).setScale(2, RoundingMode.CEILING);
        }
        params.put("balance", balance.toString());
    }

    @Override
    public String toString() {
        return buildText();
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
}