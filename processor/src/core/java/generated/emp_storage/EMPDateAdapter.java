/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package generated.emp_storage;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 03.09.14
 * Time: 14:16
 * To change this template use File | Settings | File Templates.
 */
public class EMPDateAdapter extends XmlAdapter<String, XMLGregorianCalendar> {
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String marshal(XMLGregorianCalendar v) throws Exception {
        Date d = new Date(v.toGregorianCalendar().getTimeInMillis());
        return dateFormat.format(d);
    }

    @Override
    public XMLGregorianCalendar unmarshal(String v) throws Exception {
        Date date = dateFormat.parse(v);
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        return cal;
    }
}
