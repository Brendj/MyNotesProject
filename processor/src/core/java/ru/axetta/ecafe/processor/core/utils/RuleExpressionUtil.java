/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 22.04.12
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class RuleExpressionUtil {
    public static final String ARGUMENT_NAMES[] = {
        "generateDate",
        "generateTime",
        "generateDurationMillis",
        "idOfOrg",
        "shortName",
        "officialName",
        "groupName",
        "idOfClient",
        "email",
        "contractPerson.surname",
        "contractPerson.firstName",
        "contractPerson.secondName",
        "contractPerson.abbreviation",
        "person.surname",
        "person.firstName",
        "person.secondName",
        "person.abbreviation",
        "phone",
        "mobile",
        "idOfContragent",
        "contragentName",
        "category",
        "idOfMenuSourceOrg",
        "enterEventType"};

    // Аргементы, которые предназначены не для фильтрации, а для передачи параметров
    private static int POST_ARGS[] = {23};

    public static boolean isPostArgument(String argName) {
        for (int i = 0; i < ARGUMENT_NAMES.length; i++) {
            if (ARGUMENT_NAMES[i].equals(argName))
                for (int j = 0; j < POST_ARGS.length; j++)
                    if (i == POST_ARGS[j])
                        return true;
        }
        return false;
    }
}
