/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.web.partner.foodpayment.DTO.EditClientsGroupsGroupDTO;

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
    ClientGroup getClientGroupByOrgIdAndGroupName(Long orgId, String groupName) throws Exception;
    List<Client> getClientsForContractIds(ClientGroup newClientGroup, List<Long> contractIds, boolean strictEditMode) throws Exception;
    List<Client> getClientsForGroups(ClientGroup newClientGroup, List<String> oldGroups, boolean strictEditMode) throws Exception;
    List<EditClientsGroupsGroupDTO> moveClientsInGroup(ClientGroup newClientGroup, List<Client> clients, String username) throws Exception;
}
