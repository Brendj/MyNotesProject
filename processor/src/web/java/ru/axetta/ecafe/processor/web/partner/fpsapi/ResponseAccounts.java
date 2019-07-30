/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.fpsapi;

import ru.axetta.ecafe.processor.web.partner.fpsapi.dataflow.Result;

import java.util.LinkedList;
import java.util.List;

public class ResponseAccounts extends Result {
    private List<AccountsItem> accounts;

    public ResponseAccounts() { this.accounts = new LinkedList<AccountsItem>(); }

    public List<AccountsItem> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountsItem> accounts) {
        this.accounts = accounts;
    }
}
