package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import java.util.List;

public class IdChildResult extends IdResult {

    public List<Long> childIdList;

    public IdChildResult() {
        super();
    }

    public IdChildResult(Long id, List<Long> childIdList, Long resultCode, String description){
        this.id=id;
        this.childIdList = childIdList;
        this.resultCode=resultCode;
        this.description=description;
    }

}
