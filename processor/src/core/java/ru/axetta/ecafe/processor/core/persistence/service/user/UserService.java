/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.user;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.User;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 14.07.15
 * Time: 15:16
 */

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private UserContragentsOrgsModel userContragentsOrgsModel;

    /**
     * По каждому User возвращается userContragentsOrgsModel, информация про контрагенотов и связанных
     */
    public UserContragentsOrgsModel getUserContragentsOrgs(String userName) {
        Session persistenceSession = (Session) entityManager.getDelegate();

        //Пользователь загружен по idOfUser - по айди
        User user = (User) persistenceSession.load(User.class, userName);

        // Результирующая мапа организаций
        Map<Contragent, Set<Org>> userOrgsMap = new HashMap<Contragent, Set<Org>>();

        for (Contragent contragent : user.getContragents()) {
            userOrgsMap.put(contragent, contragent.getOrgs());
        }
        userContragentsOrgsModel = new UserContragentsOrgsModel(user.getIdOfUser(), user.getRoleName(), userOrgsMap);
        return userContragentsOrgsModel;


    }

}
