/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import java.util.LinkedList;
import java.util.List;

public class GuardianDesc {
    protected GuardianDescItemParamList guardianDescParam;

    public static final String FIELD_FIRST_NAME = "firstName";
    public static final String FIELD_SECOND_NAME = "secondName";
    public static final String FIELD_SURNAME = "surname";
    public static final String FIELD_GROUP = "group";
    public static final String FIELD_ORG_NAME = "orgName";
    public static final String FIELD_LEGALITY = "legality";
    public static final String FIELD_RELATION_DEGREE = "relationDegree";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_GUARDIAN_BIRTHDAY = "guardianBirthday";


    public GuardianDescItemParamList getGuardianDescParams() {
        return guardianDescParam;
    }

    public void setGuardianDescParams(GuardianDescItemParamList guardianDescParam) {
        this.guardianDescParam = guardianDescParam;
    }

    public static class GuardianDescItemParam {
        public String paramName;
        public String paramValue;

        public GuardianDescItemParam(String paramName, String paramValue) {
            this.paramName = paramName;
            this.paramValue = paramValue;
        }

        public GuardianDescItemParam() {
        }
    }

    public static class GuardianDescItemParamList {
        protected List<GuardianDescItemParam> param;

        public GuardianDescItemParamList() {
            param = new LinkedList<GuardianDescItemParam>();
        }

        public List<GuardianDescItemParam> getParam() {
            return param;
        }

        public void setParam(List<GuardianDescItemParam> param) {
            this.param = param;
        }
    }
}
