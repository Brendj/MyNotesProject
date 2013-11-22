package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.07.13
 * Time: 12:35
 * To change this template use File | Settings | File Templates.
 */
public class StudentsConfirmPaymentList {

    @XmlElement(name = "StudentMustPayItems")
    protected List<StudentMustPayItem> studentMustPayItemList;

    public List<StudentMustPayItem> getStudentMustPayItemList() {
        if (studentMustPayItemList == null){
            studentMustPayItemList = new ArrayList<StudentMustPayItem>();
        }
        return studentMustPayItemList;
    }

    public StudentsConfirmPaymentList(List<StudentMustPayItem> studentMustPayItemList) {
        this.studentMustPayItemList = studentMustPayItemList;
    }

    public StudentsConfirmPaymentList() {
    }
}
