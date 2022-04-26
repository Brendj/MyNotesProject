package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.CashierCheckPrinterType;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.13
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public class CashierCheckPrinterSettingValue extends AbstractParserBySettingValue{

    private String a;//a: Microsoft XPS DocumentItem Writer - название принтера
    private String b;//b: 42 - общая ширина ленты принтера (возможные значения 42,48, по умолчанию 42)
    private String c;//c: 1 - ширина разделителя между колонками (возможные значения 1,2,3, по умолчанию 1)
    private String d;//d: 19 – ширина колонки наименование (определяется по формуле: d = b – c*3 – e – f – g)
    private String e;//e: 3 - ширина колонки количество (возможные значения 2,3,4, по умолчанию 3)
    private String f;//f: 9 - ширина колонки стоимость (возможные значения 7,8,9,10,11, 12, по умолчанию 10)
    private String g;//g: 8 – ширина колонки цена (возможные значения 6,7,8,9,10,11, по умолчанию 10)
    private String h;//h: текстовое поле выводимое на принтере

    private static final int DEFAULT_CAPACITY = 8;

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
        if(values.length >= 7)
            this.g = values[6];
        else
            this.g = "10";
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

    @Override
    protected int getECafeSettingArrayCapacity() {
        return DEFAULT_CAPACITY;
    }

    @Override
    protected Integer getOrgSettingTypeByIndex(Integer index) {
        return CashierCheckPrinterType.getGlobalIdByECafeSettingValueIndex(index);
    }

    @Override
    protected Integer getIndexByOrgSettingType(Integer type) {
        return CashierCheckPrinterType.getECafeSettingValueIndexByGlobalId(type);
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
