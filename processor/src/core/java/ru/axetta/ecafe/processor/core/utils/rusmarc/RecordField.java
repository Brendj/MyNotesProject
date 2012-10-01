/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils.rusmarc;

import java.util.LinkedList;
import java.util.List;

/**
 * ПОЛЕ, Field - определенная строка символов, идентифицированная меткой, содержащая одно или более подполей. Поле - часть записи, соответствующая одной области библиографического описания, одной предметной рубрике, одному шифру хранения и т.д. Может содержать данные переменной длины (переменное поле) и фиксированной длины (фиксированное поле).
 */
public class RecordField extends FieldDecoder {
    /**
     * МЕТКА, Tag - совокупность трех цифровых символов, используемая для идентификации поля.
     */
    public String tag;

    /**
     * ИНДИКАТОР, Indicator - символ, цифровой (Numeric) или буквенный (Alphabetic), связанный с переменным полем, представляющий дополнительную информацию о содержании поля, взаимосвязи между данным полем и другими полями в записи или об указаниях компьютеру оперировать данными определенным образом.<br/>
     * Пример:<br/>
     * Значение "1" первого индикатора в поле заглавия указывает, что в данном каталоге должна быть отдельная библиографическая запись на заглавие. В карточном каталоге это означает, что для единицы описания должна распечатываться карточка с добавочной библиографической записью на заглавие, а в справке о добавочных записях должно быть указано "Заглавие".
     */
    public char indicator1;

    /**
     * ИНДИКАТОР, Indicator - символ, цифровой (Numeric) или буквенный (Alphabetic), связанный с переменным полем, представляющий дополнительную информацию о содержании поля, взаимосвязи между данным полем и другими полями в записи или об указаниях компьютеру оперировать данными определенным образом.<br/>
     * С помощью второго индикатора на экране дисплея указывается количество символов в начале поля (включая пробелы (spaces)), которые не должны учитываться компьютером в процессе сортировки и расстановки. Для заглавия The waste lands второй индикатор устанавливается на цифре "4", чтобы первые четыре символа ("T", "h", "e", пробел) не учитывались при сортировке и заглавие сортировалось в файле на слово "waste".
     */
    public char indicator2;

    public List<RecordSubField> subFields;

    public RecordField(String tag) {
        this.tag = tag;
        indicator1 = ' ';
        indicator2 = ' ';
    }

    public byte[] getSub(char subFieldCode) {
        for (RecordSubField rsf : subFields)
            if (rsf.subFieldCode == subFieldCode)
                return rsf.data;
        return null;
    }

    public void addSub(RecordSubField rsf) {
        if (subFields == null)
            subFields = new LinkedList<RecordSubField>();
        subFields.add(rsf);
    }

    public void readSub(String[] enc) {
        if (subFields != null)
            for (RecordSubField rsf : subFields)
                rsf.read(enc);
        else
            read(enc);
    }


    public String toString() {
        String s = "";
        if (subFields == null)
            s += tag + ":     " + dataString + '\r';
        else {
            String prefix = tag + ": " + indicator1 + "" + indicator2 + " ";
            boolean flag = false;
            for (RecordSubField rsf : subFields) {
                s += prefix + "$" + rsf.subFieldCode + " " + rsf.dataString + '\r';
                if (flag) continue;
                flag = true;
                prefix = "        ";
            }
        }
        return s;
    }

    public StringBuilder getStringData() {
        StringBuilder s = new StringBuilder();
        if (subFields == null) {
            if (dataString != null) {
                s.append(dataString);
            }
            s.append(Record.FIELD_SEPARATOR);
        } else {
            s.append(indicator1);
            s.append(indicator2);
            for (RecordSubField rsf : subFields) {
                s.append(Record.DELIMITER).append(rsf.subFieldCode).append(rsf.dataString);
            }
            s.append(Record.FIELD_SEPARATOR);
        }
        return s;
    }

    @Override
    public int hashCode() {
        return (tag + dataString + indicator1 + "" + indicator2).hashCode();
    }

    public static boolean equals(RecordField rf1, RecordField rf2) {
        return ((rf1.dataString == null && rf2.dataString == null) || (rf1.dataString != null && rf1.dataString.equals(rf2.dataString))) && rf1.tag.equals(rf2.tag) && rf1.indicator1 == rf2.indicator1 && rf1.indicator2 == rf2.indicator2 && ((rf1.subFields == null && rf2.subFields == null) || (rf1.subFields != null && rf1.subFields.equals(rf2.subFields)));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RecordField && equals(this, (RecordField) o);
    }

    public List<RecordSubField> getSubFields(char subTag) {
        List<RecordSubField> res = new LinkedList<RecordSubField>();
        if (subFields != null)
            for (RecordSubField rsf : subFields)
                if (rsf.subFieldCode == subTag)
                    res.add(rsf);
        return res;
    }

    public RecordSubField getSubField(char subTag) {
        if (subFields != null)
            for (RecordSubField rsf : subFields)
                if (rsf.subFieldCode == subTag)
                    return rsf;
        return null;
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone"})
    @Override
    public RecordField clone() throws CloneNotSupportedException {
        RecordField rf = new RecordField(tag);
        rf.indicator1 = indicator1;
        rf.indicator2 = indicator2;
        rf.data = data;
        if (subFields != null) {
            rf.subFields = new LinkedList<RecordSubField>();
            for (RecordSubField rsf : subFields) {
                RecordSubField rsfTmp = new RecordSubField(rsf.subFieldCode, rsf.data);
                rsfTmp.dataString = rsf.dataString;
                rf.subFields.add(rsfTmp);
            }
        } else
            rf.dataString = dataString;
        return rf;
    }
}