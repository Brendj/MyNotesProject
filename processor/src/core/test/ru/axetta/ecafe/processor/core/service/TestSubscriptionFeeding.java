package ru.axetta.ecafe.processor.core.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Test;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.SubscriptionFeeding;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.SubscriberFeedingSettingSettingValue;

/**
 * Created with IntelliJ IDEA.
 * User: Skvortsov
 * Date: 09.10.15
 * Time: 4:28
 * To change this template use File | Settings | File Templates.
 */
public class TestSubscriptionFeeding {

    static public Date _testGetFirstDateCanChangeRegister (String sDate, String sHoursForbidChange, String sSixWorkWeek) throws Exception {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date date = df.parse(sDate);
        SubscriptionFeeding sf = new SubscriptionFeeding();
        sf.setCreatedDate(date);
        sf.setClient(null);
        sf.setOrgOwner(null);
        sf.setIdOfClient(null);
        sf.setDateActivateSubscription(null);
        sf.setDateCreateService(date);
        sf.setDeletedState(false);
        sf.setSendAll(null);
        sf.setWasSuspended(false);
        sf.setGlobalVersionOnCreate(null);
        sf.setGlobalVersion(null);
        sf.setStaff(null);

        // В данном тесте важен values[3] - hoursForbidChange и values[4] - sixWorkWeek (1 или 0)
        String[] values = {"5", "999", "1", "", ""};
        values[3] = sHoursForbidChange;
        values[4] = sSixWorkWeek;
        SubscriberFeedingSettingSettingValue parser = new SubscriberFeedingSettingSettingValue(values);
        Date resultDate = sf.getFirstDateCanChangeRegister(parser);
        System.out.println("DateCreateService=" + sf.getDateCreateService().toString() + "; hoursForbidChange=" + sHoursForbidChange + "; sSixWorkWeek=" + sSixWorkWeek + "; resultDate=" + resultDate);
        return resultDate;
    }

    @Test
    public void testGetFirstDateCanChangeRegister () {
        try {
            Date result;

            result = _testGetFirstDateCanChangeRegister("08/10/2015 10:00", "36", "0");
            result = _testGetFirstDateCanChangeRegister("08/10/2015 10:00", "36", "1");
            result = _testGetFirstDateCanChangeRegister("08/10/2015 16:00", "36", "0");
            result = _testGetFirstDateCanChangeRegister("08/10/2015 16:00", "36", "1");
            result = _testGetFirstDateCanChangeRegister("05/10/2015 10:00", "36", "0");
            result = _testGetFirstDateCanChangeRegister("05/10/2015 10:00", "36", "1");
            result = _testGetFirstDateCanChangeRegister("05/10/2015 16:00", "36", "0");
            result = _testGetFirstDateCanChangeRegister("05/10/2015 16:00", "36", "1");
        } catch (Exception exception) {
            System.out.println(exception.toString());
        }
    }
}
