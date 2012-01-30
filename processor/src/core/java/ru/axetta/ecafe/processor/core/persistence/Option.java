/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 11.11.11
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public class Option {
    public final static int OPTION_WITH_OPERATOR=2,
            OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT=3, OPTION_CLEAN_MENU=4,
            OPTION_MENU_DAYS_FOR_DELETION=5, OPTION_MAX=5;

    public static String getDefaultValue(int nOption) {
        switch (nOption) {
            case OPTION_WITH_OPERATOR: return "0";
            case OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT: return "0";
            case OPTION_CLEAN_MENU: return "1";
            case OPTION_MENU_DAYS_FOR_DELETION: return "30";
        }
        return null;
    }


    private Long idOfOption;
    private String optionText;

    Option() {
        // For Hibernate
    }

    public Option(Long idOfOption, String optionText) {
        this.idOfOption = idOfOption;
        this.optionText = optionText;
    }

    public Long getIdOfOption() {
        return idOfOption;
    }

    public void setIdOfOption(Long idOfOption) {
        this.idOfOption = idOfOption;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Option option = (Option) o;

        if (!idOfOption.equals(option.idOfOption)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idOfOption.hashCode();
    }

    @Override
    public String toString() {
        return "Option{" + "idOfOption=" + idOfOption + ", optionText='" + optionText + '\'' + '}';
    }

}
