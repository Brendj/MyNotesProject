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
    @XmlAttribute(name = "students")
    public Long students = 0L;
    @XmlAttribute(name = "employee")
    public Long employee = 0L;
    @XmlAttribute(name = "others")
    public Long others = 0L;
    @XmlAttribute(name = "cardless")
    public Long cardless = 0L;
    @XmlAttribute(name = "exitsCardless")
    public Long exitsCardless = 0L;

    @Transient
    public boolean isEmpty(){
        if(id != null && id != 0){
            return false;
        }
        if(students != 0){
            return false;
        }
        if(employee != 0){
            return false;
        }
        if(others != 0){
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
