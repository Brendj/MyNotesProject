package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.CardBalanceReportPrinterType;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.13
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public class CardBalanceReportPrinterSettingValue extends AbstractParserBySettingValue{

    private String a; //a: Microsoft XPS DocumentItem Writer - название принтера
    private String b; //b: 42 - общая ширина ленты принтера (возможные значения 42,48, по умолчанию 42)
    private String c;//c: 1 - ширина разделителя между колонками (возможные значения 1,2,3, по умолчанию 1)
    private String d; //d: 22 - ширина колонки наименование (определяется по формуле: d = b – c*2 – e – f)
    private String e;//e: 6 - ширина колонки номер карты (возможные значения 8,10,12,14, по умолчанию 12)
    private String f;//f: 12 - ширина колонки баланс (возможные значения 7,8,9,10,11,12, по умолчанию 12)
    private String g;//g: текстовое поле выводимое на принтере

    private static final int DEFAULT_CAPACITY = 7;

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

    public boolean check(){
        int d1 = getValuesByD();
        int d2 = Integer.parseInt(d);
        return d1==d2;
    }

    @Override
    protected int getECafeSettingArrayCapacity() {
        return DEFAULT_CAPACITY;
    }

    @Override
    protected Integer getOrgSettingTypeByIndex(Integer index) {
        return CardBalanceReportPrinterType.getGlobalIdByECafeSettingValueIndex(index);
    }

    @Override
    protected Integer getIndexByOrgSettingType(Integer type) {
        return CardBalanceReportPrinterType.getECafeSettingValueIndexByGlobalId(type);
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
