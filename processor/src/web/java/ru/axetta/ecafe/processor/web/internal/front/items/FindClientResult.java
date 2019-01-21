/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FindClientResult")
public class FindClientResult extends ClientResponse {
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
}
