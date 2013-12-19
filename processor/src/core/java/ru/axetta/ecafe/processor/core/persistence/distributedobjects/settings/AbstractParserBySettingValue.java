package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.13
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractParserBySettingValue {

    protected AbstractParserBySettingValue(String[] values) throws ParseException {
        parse(values);
    }

    protected abstract void parse(String[] values) throws ParseException;

    public abstract String build();
    public abstract boolean check();

}
