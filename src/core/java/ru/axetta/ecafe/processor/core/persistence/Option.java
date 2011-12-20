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
