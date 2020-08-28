/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO.GroupEmployee;

import java.util.LinkedList;
import java.util.List;

public class ResponseEmployees extends Result {
    private List<GroupEmployee> employeeGroups;

    public ResponseEmployees(){
        employeeGroups = new LinkedList<GroupEmployee>();
    }

    public void setEmployeeGroups(List<GroupEmployee> employeeGroups) {
        this.employeeGroups = employeeGroups;
    }

    public List<GroupEmployee> getEmployeeGroups(){
        return employeeGroups;
    }
}
