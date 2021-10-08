package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.ClientGroup;

import java.util.Optional;

public interface ClientGroupReadOnlyRepo extends JpaRepository<ClientGroup, Long> {
    @Query(value = "select cg.groupName from ClientGroup cg "
            + "where cg.clientGroupId.idOfOrg = :idOfOrg and cg.clientGroupId.idOfClientGroup = :idOfClientGroup")
    Optional<String> getClientGroupName(@Param("idOfOrg") Long idOfOrg, @Param("idOfClientGroup") Long idOfClientGroup);
}
