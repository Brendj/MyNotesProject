/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 22.12.14
 * Time: 13:38
 */

@Component
@Scope("session")
public class GroupControlSubscriptionsPage extends BasicWorkspacePage {

    private List<GroupControlSubscriptionsItem> groupControlSubscriptionsItems = new ArrayList<GroupControlSubscriptionsItem>();

    private Long lineResultSize;
    private Long successLineNumber;

    @Override
    public void onShow() throws Exception {
    }

    @Override
    public String getPageFilename() {
        return "service/msk/group_control_subscription";
    }

    public void subscriptionLoadFileListener(UploadEvent event) {
        UploadItem item = event.getUploadItem();

        BufferedReader bufferedReader = null;
        String line;
        String cvsSplitBy = ";";

        try {
            File file = item.getFile();

            bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
            while ((line = bufferedReader.readLine()) != null) {

                // разделитель
                String[] separatedData = line.split(cvsSplitBy);
                getGroupControlSubscriptionsItems()
                        .add(new GroupControlSubscriptionsItem(separatedData[0], separatedData[2], separatedData[3],
                                separatedData[4], Long.parseLong(separatedData[5]), "ok"));

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        lineResultSize = Long.valueOf(groupControlSubscriptionsItems.size());
    }

    public List<GroupControlSubscriptionsItem> getGroupControlSubscriptionsItems() {
        return groupControlSubscriptionsItems;
    }

    public Long getLineResultSize() {
        return lineResultSize;
    }

    public void setLineResultSize(Long lineResultSize) {
        this.lineResultSize = lineResultSize;
    }

    public Long getSuccessLineNumber() {
        return successLineNumber;
    }

    public void setSuccessLineNumber(Long successLineNumber) {
        this.successLineNumber = successLineNumber;
    }
}

