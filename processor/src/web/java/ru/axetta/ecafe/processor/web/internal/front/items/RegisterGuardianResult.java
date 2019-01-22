/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;

@XmlType(name = "RegisterGuardianResult")
public class RegisterGuardianResult {
    public static final String FIELD_FIRST_NAME = "firstName";
    public static final String FIELD_SECOND_NAME = "secondName";
    public static final String FIELD_SURNAME = "surname";
    public static final String FIELD_GROUP = "group";
    public static final String FIELD_ORG_NAME = "orgName";
    public static final String FIELD_LEGALITY = "legality";
    public static final String FIELD_RELATION_DEGREE = "relationDegree";
    public static final String FIELD_GENDER = "gender";
    public static final String FIELD_GUARDIAN_BIRTHDAY = "guardianBirthday";
    public static final String FIELD_MOBILE = "mobile";
    public static final String FIELD_ORG_ID = "orgId";
    public static final String FIELD_CLIENT_ID = "clientId";
    public static final String FIELD_CLIENT_GUID = "clientGUID";

    protected RegisterGuardianItemParamList registerGuardianDescParams;

    public RegisterGuardianResult() {
        registerGuardianDescParams = new RegisterGuardianItemParamList();
    }

    public RegisterGuardianItemParamList getRegisterGuardianDescParams() {
        return registerGuardianDescParams;
    }

    public void setRegisterGuardianDescParams(RegisterGuardianItemParamList registerGuardianDescParams) {
        this.registerGuardianDescParams = registerGuardianDescParams;
    }

    public static class RegisterGuardianItemParam {
        public String paramName;
        public String paramValue;

        public RegisterGuardianItemParam() {}

        public RegisterGuardianItemParam(String paramName, String paramValue) {
            this.paramName = paramName;
            this.paramValue = paramValue;
        }
    }

    public static class RegisterGuardianItemParamList {
        protected List<RegisterGuardianItemParam> param;

        public RegisterGuardianItemParamList() {
            param = new LinkedList<RegisterGuardianItemParam>();
        }

        public List<RegisterGuardianItemParam> getParam() {
            return param;
        }

        public void setParam(List<RegisterGuardianItemParam> param) {
            this.param = param;
        }
    }
}

