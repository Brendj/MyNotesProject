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
        setAttribute(element,"Id", settingsId.getId()+1);
    }

    @Override
    protected ECafeSettings parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if(longOrgOwner != null) setOrgOwner(longOrgOwner);
        String stringValue = getStringAttributeValue(node, "Value", 128);
        if(stringValue!=null) setSettingValue(stringValue);
        Integer intId = getIntegerAttributeValue(node, "Id");
        if(intId!=null){
            setSettingsId(SettingsIds.fromInteger(intId-1));
        }
        setSendAll(SendToAssociatedOrgs.SendToSelf);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((ECafeSettings) distributedObject).getOrgOwner());
        setSettingValue(((ECafeSettings) distributedObject).getSettingValue());
        setSettingsId(((ECafeSettings) distributedObject).getSettingsId());
    }

    private String settingValue;
    private SettingsIds settingsId;

    public SettingsIds getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(SettingsIds settingsId) {
        this.settingsId = settingsId;
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
            parse(values);
        }

        protected abstract void parse(String[] values) throws ParseException;

        public abstract String build();

    }

    public static class CashierCheckPrinterSettingValue extends AbstractParserBySettingValue{

        private String a;//a: Microsoft XPS Document Writer - название принтера
        private String b;//b: 42 - общая ширина ленты принтера (возможные значения 42,48, по умолчанию 42)
        private String c;//c: 1 - ширина разделителя между колонками (возможные значения 1,2,3, по умолчанию 1)
        private String d;//d: 19 – ширина колонки наименование (определяется по формуле: d = b – c*3 – e – f – g)
        private String e;//e: 3 - ширина колонки количество (возможные значения 2,3,4, по умолчанию 3)
        private String f;//f: 9 - ширина колонки стоимость (возможные значения 7,8,9,10,11, 12, по умолчанию 10)
        private String g;//g: 8 – ширина колонки цена (возможные значения 6,7,8,9,10,11, по умолчанию 10)
        private String h;//h: текстовое поле выводимое на принтере

        public CashierCheckPrinterSettingValue(String[] values) throws ParseException {
            super(values);
        }

        @Override
        protected void parse(String[] values) {
            this.a = values[0];
            this.b = values[1];
            this.c = values[2];
            this.d = values[3];
            this.e = values[4];
            this.f = values[5];
            this.g = values[6];
            if(values.length==8) {
                this.h = values[7];
            } else {
                this.h = "";
            }
        }

        @Override
        public String build() {
            return a+";"+b+";"+c+";"+d+";"+e+";"+f+";"+g+";" + h+";";
        }

        public boolean check(){
            int d1 = getValuesByD();
            int d2 = Integer.parseInt(d);
            return d1==d2;
        }

        public Integer getValuesByD(){
            int temp = (Integer.parseInt(b)-3*Integer.parseInt(c)-Integer.parseInt(e)-Integer.parseInt(f)-Integer.parseInt(g));
            this.d = String.valueOf(temp);
            return temp;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        public String getE() {
            return e;
        }

        public void setE(String e) {
            this.e = e;
        }

        public String getF() {
            return f;
        }

        public void setF(String f) {
            this.f = f;
        }

        public String getG() {
            return g;
        }

        public void setG(String g) {
            this.g = g;
        }

        public String getH() {
            return h;
        }

        public void setH(String h) {
            this.h = h;
        }
    }

    public static class SalesReportPrinterSettingValue extends AbstractParserBySettingValue{

        private String a;//a: Microsoft XPS Document Writer - название принтера
        private String b;//b: 42 - общая ширина ленты принтера (возможные значения 42,48, по умолчанию 42)
        private String c;//c: 1 - ширина разделителя между колонками (возможные значения 1,2,3, по умолчанию 1)
        private String d;//d: 22 - ширина колонки наименование (определяется по формуле: d = b – c*2 – e – f)
        private String e;//e: 6 - ширина колонки количество (возможные значения 6,7,8, по умолчанию 6)
        private String f;//f: 12 - ширина колонки стоимость (возможные значения 10,11,12,13,14,15, по умолчанию 12)
        private String g;//g: текстовое поле выводимое на принтере

        public SalesReportPrinterSettingValue(String[] values) throws ParseException {
            super(values);
        }

        @Override
        protected void parse(String[] values) {
            this.a = values[0];
            this.b = values[1];
            this.c = values[2];
            this.d = values[3];
            this.e = values[4];
            this.f = values[5];
            if(values.length==7) {
                this.g = values[6];
            } else {
                this.g = "";
            }
        }

        @Override
        public String build() {
            return a+";"+b+";"+c+";"+d+";"+e+";"+f+";"+g+";";
        }

        public boolean check(){
            int d1 = getValuesByD();
            int d2 = Integer.parseInt(d);
            return d1==d2;
        }

        public Integer getValuesByD(){
            int temp = (Integer.parseInt(b)-2*Integer.parseInt(c)-Integer.parseInt(e)-Integer.parseInt(f));
            this.d = String.valueOf(temp);
            return temp;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        public String getE() {
            return e;
        }

        public void setE(String e) {
            this.e = e;
        }

        public String getF() {
            return f;
        }

        public void setF(String f) {
            this.f = f;
        }

        public String getG() {
            return g;
        }

        public void setG(String g) {
            this.g = g;
        }
    }

    public static class CardBalanceReportPrinterSettingValue extends AbstractParserBySettingValue{

        private String a; //a: Microsoft XPS Document Writer - название принтера
        private String b; //b: 42 - общая ширина ленты принтера (возможные значения 42,48, по умолчанию 42)
        private String c;//c: 1 - ширина разделителя между колонками (возможные значения 1,2,3, по умолчанию 1)
        private String d; //d: 22 - ширина колонки наименование (определяется по формуле: d = b – c*2 – e – f)
        private String e;//e: 6 - ширина колонки номер карты (возможные значения 8,10,12,14, по умолчанию 12)
        private String f;//f: 12 - ширина колонки баланс (возможные значения 7,8,9,10,11,12, по умолчанию 12)
        private String g;//g: текстовое поле выводимое на принтере

        public CardBalanceReportPrinterSettingValue(String[] values) throws ParseException {
            super(values);
        }

        @Override
        protected void parse(String[] values) {
            this.a = values[0];
            this.b = values[1];
            this.c = values[2];
            this.d = values[3];
            this.e = values[4];
            this.f = values[5];
            if(values.length==7) {
                this.g = values[6];
            } else {
                this.g = "";
            }
        }

        @Override
        public String build() {
            return a+";"+b+";"+c+";"+d+";"+e+";"+f+";"+g+";";
        }

        public Integer getValuesByD(){
            int temp = (Integer.parseInt(b)-2*Integer.parseInt(c)-Integer.parseInt(e)-Integer.parseInt(f));
            this.d = String.valueOf(temp);
            return temp;
        }

        public Boolean check(){
            int d1 = getValuesByD();
            int d2 = Integer.parseInt(d);
            return d1==d2;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        public String getE() {
            return e;
        }

        public void setE(String e) {
            this.e = e;
        }

        public String getF() {
            return f;
        }

        public void setF(String f) {
            this.f = f;
        }

        public String getG() {
            return g;
        }

        public void setG(String g) {
            this.g = g;
        }
    }

    public static class AutoPlanPaymentSettingSettingValue extends AbstractParserBySettingValue{

        private boolean offOnFlag; //булевое значение ввкл выкл
        private Date payTime; //  0:00 - время автооплаты
        private int porog;//100 - порог срабатывания (от 0 до 100)
        private static SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("HH:mm");


        public AutoPlanPaymentSettingSettingValue(String[] values) throws ParseException {
            super(values);
        }

        @Override
        protected void parse(String[] values) throws ParseException {
            this.offOnFlag = values[0].equals("1");
            this.payTime = dateOnlyFormat.parse(values[1]);
            this.porog = Integer.parseInt(values[2]);
        }

        @Override
        public String build() {
            return (offOnFlag?1:0)+";"+dateOnlyFormat.format(payTime)+";"+porog+";";
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
