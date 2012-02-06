/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.journal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.TransactionJournalService;
import ru.axetta.ecafe.processor.core.persistence.TransactionJournal;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 25.01.12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */

public class JournalViewPage extends BasicWorkspacePage {


    public String getPageFilename() {
        return "journal/view";
    }

    List<TransactionJournal> transactionJournalList;

    @Override
    public void onShow() throws Exception {
        transactionJournalList = DAOService.getInstance().fetchTransactionJournal(100);
    }

    public List<TransactionJournal> getJournal() {
        return transactionJournalList;
    }

}
