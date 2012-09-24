/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 19.09.12
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class OrgSynchMonitorPage extends BasicWorkspacePage
    {
    private DAOService daoService = DAOService.getInstance ();
    private List <Org> items = Collections.emptyList ();
    private Date lastUpdate;


    public List <Org> getItemList ()
        {
        items = daoService.getOrderedSynchOrgsList ();
        if (lastUpdate == null)
            {
            lastUpdate = new Date ();
            }
        lastUpdate.setTime (System.currentTimeMillis ());
        return items;
        }


    public Date getLastUpdate ()
        {
        return lastUpdate;
        }


    public long getCurrentTimeMillis ()
        {
        return System.currentTimeMillis ();
        }


    public String getPageFilename()
        {
        return "org/sync_monitor";
        }
    }