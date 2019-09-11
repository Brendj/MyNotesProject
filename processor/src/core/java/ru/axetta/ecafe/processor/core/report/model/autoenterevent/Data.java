package ru.axetta.ecafe.processor.core.report.model.autoenterevent;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import javax.persistence.Transient;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.*;

/**
 * User: Shamil
 * Date: 29.09.14
 */
public class Data {
    @Transient
    private Long eventId ; //event id
    private String f01 ; // client ID
    private String f02 ; //name
    private String f03 ; //class

    private String f04 ; //date
    private String f05 ;  //building name

    private String f06 ; // enter
    private String f07 ; //exit
    private String f08 ; // insideTime

    private String f09 ; // enters
    private String f10 ; // entersSummary

    private Integer f11 ; // for sorting by date

    public Data() {
    }

    public Data(Map<String,Object> rs) throws SQLException {
        this.eventId = ((BigInteger)rs.get("idofenterevent")).longValue();
        this.f01 =  ((BigInteger)rs.get("idofclient")).toString();
        this.f02 = rs.get("surname")+ " " + rs.get("firstname") + " " + rs.get("secondname");
        this.f03 = (String) rs.get("groupname");
        this.f04 = CalendarUtils.dateShortToString(new Date(((BigInteger)rs.get("evtdatetime")).longValue()));
        this.f05 = (String) rs.get("officialname");
        if (( (Integer)rs.get("passdirection") == 0 ) || ( (Integer)rs.get("passdirection") == 6)){
            //Enter
            this.f06 = CalendarUtils.timeToString(new Date(((BigInteger)rs.get("evtdatetime")).longValue()));
            this.f09 = CalendarUtils.timeToString(new Date(((BigInteger)rs.get("evtdatetime")).longValue())) + "(+)";

        } else if (( (Integer)rs.get("passdirection") == 1 ) || ( (Integer)rs.get("passdirection") == 7)){
            //exit
            this.f07 = CalendarUtils.timeToString(new Date(((BigInteger)rs.get("evtdatetime")).longValue()));
            this.f09 = CalendarUtils.timeToString(new Date(((BigInteger)rs.get("evtdatetime")).longValue())) + "(-)";
        }
    }

    public Data(String f02, String f05) {
        this.f02 = f02;
        this.f05 = f05;
    }

    public Data(String f02, String f04, String f05) {
        this.f02 = f02;
        this.f04 = f04;
        this.f05 = f05;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getF01() {
        return f01;
    }

    public void setF01(String f01) {
        this.f01 = f01;
    }

    public String getF02() {
        return f02;
    }

    public void setF02(String f02) {
        this.f02 = f02;
    }

    public String getF03() {
        return f03;
    }

    public void setF03(String f03) {
        this.f03 = f03;
    }

    public String getF04() {
        return f04;
    }

    public void setF04(String f04) {
        this.f04 = f04;
    }

    public String getF05() {
        return f05;
    }

    public void setF05(String f05) {
        this.f05 = f05;
    }

    public String getF06() {
        return f06;
    }

    public void setF06(String f06) {
        this.f06 = f06;
    }

    public String getF07() {
        return f07;
    }

    public void setF07(String f07) {
        this.f07 = f07;
    }

    public String getF08() {
        return f08;
    }

    public void setF08(String f08) {
        this.f08 = f08;
    }

    public String getF09() {
        return f09;
    }

    public void setF09(String f09) {
        this.f09 = f09;
    }

    public String getF10() {
        return f10;
    }

    public void setF10(String f10) {
        this.f10 = f10;
    }

    public Integer getF11() {
        return f11;
    }

    public void setF11(Integer f11) {
        this.f11 = f11;
    }
}
