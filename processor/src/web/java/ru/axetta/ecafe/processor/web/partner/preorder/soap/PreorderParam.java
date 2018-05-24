/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.soap;

import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.ComplexListParam;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 24.05.2018.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PreorderParam")
public class PreorderParam {
    @XmlElement(name = "contractId")
    private Long contractId;
    @XmlElement(name = "date")
    @XmlSchemaType(name = "date")
    private Date date;
    @XmlElement(name = "complexItem")
    private List<ComplexListParam> complexes;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<ComplexListParam> getComplexes() {
        return complexes;
    }

    public void setComplexes(List<ComplexListParam> complexes) {
        this.complexes = complexes;
    }
}
