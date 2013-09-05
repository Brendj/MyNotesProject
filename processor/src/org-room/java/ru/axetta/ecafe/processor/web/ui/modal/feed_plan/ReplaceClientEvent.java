/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.modal.feed_plan;

import ru.axetta.ecafe.processor.web.ui.feed.FeedPlanPage;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 05.09.13
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public class ReplaceClientEvent {
    private FeedPlanPage.ReplaceClient replaceClient;
    private FeedPlanPage.Client client;

    public ReplaceClientEvent (FeedPlanPage.ReplaceClient replaceClient, FeedPlanPage.Client client) {
        this.replaceClient = replaceClient;
        this.client = client;
    }

    public FeedPlanPage.ReplaceClient getReplaceClient() {
        return replaceClient;
    }

    public void setReplaceClient(FeedPlanPage.ReplaceClient replaceClient) {
        this.replaceClient = replaceClient;
    }

    public FeedPlanPage.Client getClient() {
        return client;
    }

    public void setClient(FeedPlanPage.Client client) {
        this.client = client;
    }
}
