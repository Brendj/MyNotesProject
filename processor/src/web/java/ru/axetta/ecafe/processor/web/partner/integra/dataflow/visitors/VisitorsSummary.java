/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow.visitors;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * User: Shamil
 * Date: 15.09.14
 */
//@XmlAccessorType(XmlAccessType.FIELD)
//@XmlType(name = "VisitorsSummary")
public class VisitorsSummary {

    @XmlAttribute(name = "id")
    public Long id = null;

    @XmlAttribute(name = "studentsTotal")
    public int studentsTotal = 0;
    @XmlAttribute(name = "studentsInside")
    public Long studentsInside = 0L;
    @XmlAttribute(name = "employee")
    public Long employee = 0L;
    @XmlAttribute(name = "others1")
    public Long others1 = 0L;
    @XmlAttribute(name = "others2")
    public Long others2 = 0L;
    @XmlAttribute(name = "others3")
    public Long others3 = 0L;
    @XmlAttribute(name = "cardless")
    public Long cardless = 0L;
    @XmlAttribute(name = "exitsCardless")
    public Long exitsCardless = 0L;

    @Transient
    public boolean isEmpty(){
        if(id != null && id != 0){
            return false;
        }
        if(studentsTotal != 0){
            return false;
        }
        if(studentsInside != 0){
            return false;
        }
        if(employee != 0){
            return false;
        }
        if(others1 != 0){
            return false;
        }
        if(others2 != 0){
            return false;
        }
        if(others3 != 0){
            return false;
        }
        if(cardless != 0){
            return false;
        }
        if(exitsCardless != 0){
            return false;
        }
        return true;
    }
}
