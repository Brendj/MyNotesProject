/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.hibernate.type.EnumType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 09.11.12
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public class ECafeSettings extends DistributedObject{

    @Override
    public void preProcess(Session session) throws DistributedObjectException {}

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element,"Value", settingValue);
        setAttribute(element,"Text", settingText);
        setAttribute(element,"Id", settingsId.getId());
    }

    @Override
    protected ECafeSettings parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringValue = getStringAttributeValue(node, "Value", 128);
        if(stringValue!=null) setSettingValue(stringValue);
        Integer longId = getIntegerAttributeValue(node, "Id");
        if(longId!=null){
            setSettingsId(SettingsIds.fromInteger(longId));
        }
        String stringSettingText = getStringAttributeValue(node, "Text", 128);
        if(stringSettingText!=null) setSettingText(stringSettingText);
        setSendAll(SendToAssociatedOrgs.SendToSelf);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((ECafeSettings) distributedObject).getOrgOwner());
        setSettingValue(((ECafeSettings) distributedObject).getSettingValue());
        setSettingText(((ECafeSettings) distributedObject).getSettingText());
        setSettingsId(((ECafeSettings) distributedObject).getSettingsId());
    }

    private String settingValue;
    private String settingText;
    private SettingsIds settingsId;

    public SettingsIds getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(SettingsIds settingsId) {
        this.settingsId = settingsId;
    }

    public String getSettingText() {
        return settingText;
    }

    public void setSettingText(String settingText) {
        this.settingText = settingText;
    }


    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public AbstractParserBySettingValue getSplitSettingValue() throws Exception {
        SettingValueParser settingValueParser = new SettingValueParser(settingValue, settingsId);
        return settingValueParser.getParserBySettingValue();
    }

    public static class SettingValueParser {
        private AbstractParserBySettingValue parserBySettingValue;

        public SettingValueParser(String value,SettingsIds type) throws Exception {
            String[] values = value.split(";");
            switch (type){
                case CashierCheckPrinter: parserBySettingValue = new CashierCheckPrinterSettingValue(values);break;
                case SalesReportPrinter: parserBySettingValue = new SalesReportPrinterSettingValue(values);break;
                case CardBalanceReportPrinter: parserBySettingValue = new CardBalanceReportPrinterSettingValue(values); break;
                case AutoPlanPaymentSetting: parserBySettingValue = new AutoPlanPaymentSettingSettingValue(values); break;
            }
        }

        public AbstractParserBySettingValue getParserBySettingValue() {
            return parserBySettingValue;
        }
    }

    public static abstract class AbstractParserBySettingValue {

        protected AbstractParserBySettingValue(String[] values) throws ParseException {
            build(values);
        }

        protected abstract void build(String[] values) throws ParseException;
    }

    public static class CashierCheckPrinterSettingValue extends AbstractParserBySettingValue{

        private String name; // имя принтера
        private int s; //: 42 - общая ширина ленты принтера (возможные значения  42,48)
        private int a; //: 1 - ширина разделителя между колонками (возможные значения 1,2,3)
        private int b; //: 2 - ширина колонки количество (возможные значения 2,4)
        private int c; //: 12 - ширина колонки стоимость (возможные значения 7,8,9,10,11,12)
        private int d; //: 26 - ширина колонки товар (возможные значения определяется на основе остатка от общей длинны минус все остальные колонки)

        public CashierCheckPrinterSettingValue(String[] values) throws ParseException {
            super(values);
        }

        @Override
        protected void build(String[] values) {
            this.name = values[0];
            this.s = Integer.parseInt(values[1]);
            this.a = Integer.parseInt(values[2]);
            this.b = Integer.parseInt(values[4]);
            this.c = Integer.parseInt(values[5]);
            this.d = Integer.parseInt(values[3]);
        }

        public boolean check(){
            return d==valuesByD();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getS() {
            return s;
        }

        public void setS(int s) {
            this.s = s;
        }

        public Integer[] valuesByS(){
            return new Integer[]{42,48};
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public Integer[] valuesByA(){
            return new Integer[]{1,2,3};
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public Integer[] valuesByB(){
            return new Integer[]{2,4};
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }

        public Integer[] valuesByC(){
            return new Integer[]{7,8,9,10,11,12};
        }

        public int getD() {
            return d;
        }

        public void setD(int d) {
            this.d = d;
        }

        public Integer valuesByD(){
            return (s - a*2 - b - c);
        }
    }

    public static class SalesReportPrinterSettingValue extends AbstractParserBySettingValue{

        private String name; // имя принтера
        private int s; //: 42 - общая ширина ленты принтера (возможные значения  42,48)
        private int a; //: 1 - ширина разделителя между колонками (возможные значения 1,2,3)
        private int b; //: 2 - ширина колонки количество (возможные значения 2,4)
        private int c; //: 12 - ширина колонки стоимость (возможные значения 7,8,9,10,11,12)
        private int d; //: 26 - ширина колонки товар (возможные значения определяется на основе остатка от общей длинны минус все остальные колонки)

        public SalesReportPrinterSettingValue(String[] values) throws ParseException {
            super(values);
        }

        @Override
        protected void build(String[] values) {
            this.name = values[0];
            this.s = Integer.parseInt(values[1]);
            this.a = Integer.parseInt(values[2]);
            this.b = Integer.parseInt(values[4]);
            this.c = Integer.parseInt(values[5]);
            this.d = Integer.parseInt(values[3]);
        }

        public boolean check(){
            return d==valuesByD();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getS() {
            return s;
        }

        public void setS(int s) {
            this.s = s;
        }

        public Integer[] valuesByS(){
            return new Integer[]{42,48};
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public Integer[] valuesByA(){
            return new Integer[]{1,2,3};
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public Integer[] valuesByB(){
            return new Integer[]{6,7, 8};
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }

        public Integer[] valuesByC(){
            return new Integer[]{10,11,12,13,14,15};
        }

        public int getD() {
            return d;
        }

        public void setD(int d) {
            this.d = d;
        }

        public Integer valuesByD(){
            return (s - a*2 - b - c);
        }
    }

    public static class CardBalanceReportPrinterSettingValue extends AbstractParserBySettingValue{

        private String name; // имя принтера
        private int s; //: 42 - общая ширина ленты принтера (возможные значения  42,48)
        private int a; //: 1 - ширина разделителя между колонками (возможные значения 1,2,3)
        private int b; //: 2 - ширина колонки количество (возможные значения 2,4)
        private int c; //: 12 - ширина колонки стоимость (возможные значения 7,8,9,10,11,12)
        private int d; //: 26 - ширина колонки товар (возможные значения определяется на основе остатка от общей длинны минус все остальные колонки)

        public CardBalanceReportPrinterSettingValue(String[] values) throws ParseException {
            super(values);
        }

        @Override
        protected void build(String[] values) {
            this.name = values[0];
            this.s = Integer.parseInt(values[1]);
            this.a = Integer.parseInt(values[2]);
            this.b = Integer.parseInt(values[4]);
            this.c = Integer.parseInt(values[5]);
            this.d = Integer.parseInt(values[3]);
        }
        public boolean check(){
            return d==valuesByD();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getS() {
            return s;
        }

        public void setS(int s) {
            this.s = s;
        }

        public Integer[] valuesByS(){
            return new Integer[]{42,48};
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public Integer[] valuesByA(){
            return new Integer[]{1,2,3};
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public Integer[] valuesByB(){
            return new Integer[]{ 8,10,12,14};
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }

        public Integer[] valuesByC(){
            return new Integer[]{7,8,9,10,11,12};
        }

        public int getD() {
            return d;
        }

        public void setD(int d) {
            this.d = d;
        }

        public Integer valuesByD(){
            return (s - a*2 - b - c);
        }
    }

    public static class AutoPlanPaymentSettingSettingValue extends AbstractParserBySettingValue{

        private boolean offOnFlag; //булевое значение ввкл выкл
        private Date payTime; //  0:00 - время автооплаты
        private int porog;//100 - порог срабатывания (от 0 до 100)
        private static SimpleDateFormat dateOnlyFormat;

        static {
            TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
            dateOnlyFormat = new SimpleDateFormat("HH:mm");
            dateOnlyFormat.setTimeZone(utcTimeZone);
        }


        public AutoPlanPaymentSettingSettingValue(String[] values) throws ParseException {
            super(values);
        }

        @Override
        protected void build(String[] values) throws ParseException {
            this.offOnFlag = !values[0].equals("0");
            this.payTime = dateOnlyFormat.parse(values[1]);
            this.porog = Integer.getInteger(values[2]);
        }

        public boolean isOffOnFlag() {
            return offOnFlag;
        }

        public void setOffOnFlag(boolean offOnFlag) {
            this.offOnFlag = offOnFlag;
        }

        public Date getPayTime() {
            return payTime;
        }

        public void setPayTime(Date payTime) {
            this.payTime = payTime;
        }

        public int getPorog() {
            return porog;
        }

        public void setPorog(int porog) {
            this.porog = porog;
        }
    }

}
