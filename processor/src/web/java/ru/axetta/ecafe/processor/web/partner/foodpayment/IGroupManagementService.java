/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import java.util.List;

public interface IGroupManagementService {
    boolean isFriendlyOrg(long orgId, long friendlyOrgId) throws Exception;
    void addOrgGroup(long orgId, String groupName) throws Exception;
    List<GroupInfo> getOrgGroups(long orgId) throws Exception;
    List<GroupEmployee> getEmployees(long orgId) throws Exception;
    void editEmployee(long orgId, String groupName, long contractId, Boolean status) throws Exception;
    ResponseClients getClientsList(List<String> groupsList, long idOfOrg) throws Exception;
    ResponseDiscounts getDiscountsList(Long orgId) throws Exception;
    ResponseDiscountClients processDiscountClientsList(Long orgId, Long discountId, Boolean status, List<Long> clients) throws Exception;
    ResponseDiscountGroups processDiscountGroupsList(Long orgId, Long discountId, Boolean status, List<String> groups) throws Exception;
    Long getIdOfOrgFromUser(String username) throws Exception;
    List<FriendlyOrgDTO> getFriendlyOrgs(Long orgId) throws Exception;
}
