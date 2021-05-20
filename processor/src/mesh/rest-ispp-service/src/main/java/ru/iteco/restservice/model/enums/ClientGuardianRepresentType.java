/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.enums;

public enum ClientGuardianRepresentType {
    UNKNOWN(-1, "Не определено"),
    NOT_IN_LAW(0, "Не является законным представителем"),
    IN_LAW(1, "Законный представитель"),
    GUARDIAN(2, "Расширенные полномочия");

    Integer val;
    String description;

    ClientGuardianRepresentType(Integer val, String description) {
        this.description = description;
        this.val = val;
    }

    @Override
    public String toString(){
        return description;
    }

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static ClientGuardianRepresentType of(Integer i){
        if(i == null){
            return null;
        }
        for(ClientGuardianRepresentType type : ClientGuardianRepresentType.values()){
            if(i.equals(type.val)){
                return type;
            }
        }
        return UNKNOWN;
    }
}
