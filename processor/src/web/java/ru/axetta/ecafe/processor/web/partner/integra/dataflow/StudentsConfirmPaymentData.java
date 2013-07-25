package ru.axetta.ecafe.processor.web.partner.integra.dataflow;


/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.07.13
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
public class StudentsConfirmPaymentData {

    public StudentsConfirmPaymentList studentsConfirmPaymentList;
    public Long resultCode;
    public String description;

    public StudentsConfirmPaymentData(StudentsConfirmPaymentList studentsConfirmPaymentList, Long resultCode,
            String description) {
        this.studentsConfirmPaymentList = studentsConfirmPaymentList;
        this.resultCode = resultCode;
        this.description = description;
    }

    public StudentsConfirmPaymentData() {}
}
