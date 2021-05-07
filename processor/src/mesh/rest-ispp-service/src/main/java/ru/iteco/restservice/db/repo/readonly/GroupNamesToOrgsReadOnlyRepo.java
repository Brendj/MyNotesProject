package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iteco.restservice.model.GroupNamesToOrgs;

import java.util.Optional;

/**
 * Created by nuc on 05.05.2021.
 */
public interface GroupNamesToOrgsReadOnlyRepo extends JpaRepository<GroupNamesToOrgs, Long> {
    Optional<GroupNamesToOrgs> findByIdOfOrgAndGroupName(Long idOfOrg, String groupName);
}
