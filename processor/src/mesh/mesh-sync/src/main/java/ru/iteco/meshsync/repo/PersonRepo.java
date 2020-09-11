package ru.iteco.meshsync.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.iteco.meshsync.models.Person;

public interface PersonRepo extends JpaRepository<Person, String> {

    @Query(value = "SELECT count(DISTINCT o.organizationidfromnsi) > 0 FROM cf_orgs o WHERE o.organizationidfromnsi = ?1",
            nativeQuery = true)
    Boolean personFromSupportedOrg(Long organizationIdFromNSI);
}
