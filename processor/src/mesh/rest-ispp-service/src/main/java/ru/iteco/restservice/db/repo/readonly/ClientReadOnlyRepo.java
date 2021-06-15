/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.controller.guardian.responsedto.GuardianResponseDTO;
import ru.iteco.restservice.model.Client;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientReadOnlyRepo extends CrudRepository<Client, Long> {
    @Query(value = "SELECT DISTINCT childs FROM Client childs "
            + "JOIN FETCH childs.org AS org "
            + "JOIN FETCH childs.person AS person "
            + "JOIN FETCH childs.clientGroup AS cg "
            + "LEFT JOIN FETCH childs.discounts AS discounts "
            + "LEFT JOIN FETCH childs.preorderFlag AS pf "
            + "JOIN childs.guardians AS guardians "
            + "JOIN guardians.guardian AS guardian "
            + "WHERE guardian.mobile = :guardPhone AND guardians.deletedState = FALSE AND guardians.disabled = 0")
    List<Client> getClientsByGuardMobile(@Param("guardPhone") String guardMobile);

    Boolean existsByMobile(String mobile);

    Boolean existsByContractId(Long contractId);

    @EntityGraph("forClientResponseDTO")
    Optional<Client> getClientByMeshGuid(String meshGuid);

    @Query(value = "SELECT DISTINCT employee FROM Client employee "
            + "JOIN FETCH employee.org AS org "
            + "JOIN FETCH employee.person AS person "
            + "JOIN FETCH employee.clientGroup AS cg "
            + "LEFT JOIN FETCH employee.discounts AS discounts "
            + "LEFT JOIN FETCH employee.preorderFlag AS pf "
            + "WHERE employee.mobile = :mobile "
            + "AND ((cg.clientGroupId.idOfClientGroup BETWEEN 1100000000L AND 1100000029L) "
            + " OR (cg.clientGroupId.idOfClientGroup BETWEEN 1100000110L AND 1100000119L))")
    Client getEmployeeByMobile(@Param("mobile") String mobile);

    @Query(name = "getGuardiansByClient", nativeQuery = true)
    List<GuardianResponseDTO> getGuardiansByClient(@Param("contractId") Long contractId);

    @EntityGraph("getClientAndOrgByContractId")
    Optional<Client> getClientByContractId(@Param("contractId") Long contractId);

    @EntityGraph("getClientAndDiscountsByContractId")
    Optional<Client> getClientAndDiscountsByContractId(@Param("contractId") Long contractId);

    @EntityGraph("getClientAndOrgByContractId")
    Optional<Client> getClientByMobileAndContractId(String mobile, Long contractId);

    @Query("SELECT g FROM ClientGuardian cg "
         + "JOIN cg.guardian g "
         + "JOIN cg.children c "
         + "WHERE c.contractId = :contractId AND g.mobile LIKE :mobile "
         + "AND cg.deletedState = FALSE AND cg.disabled = 0 "
         + "ORDER BY g.clientRegistryVersion DESC ")
    List<Client> getGuardianByChild(@Param("contractId") Long contractId,
                                    @Param("mobile") String mobile,
                                    Pageable pageable);

    @Query(value = "select c from Client c where c.mobile = :mobile and (c.idOfClient = :idOfClient "
            + "or exists (select g.idOfClient from Client g, ClientGuardian cg " +
            "where g.mobile = :mobile and g.idOfClient = cg.guardian.idOfClient and cg.children.idOfClient = :idOfClient))")
    List<Client> getClientsByMobile(@Param("idOfClient") Long idOfClient, @Param("mobile") String mobile);
}
