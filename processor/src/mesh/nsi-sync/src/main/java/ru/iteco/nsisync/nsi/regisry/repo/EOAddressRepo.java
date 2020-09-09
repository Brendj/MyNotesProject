package ru.iteco.nsisync.nsi.regisry.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iteco.nsisync.nsi.regisry.models.EOAddress;
import ru.iteco.nsisync.nsi.regisry.models.OrganizationRegistry;

public interface EOAddressRepo extends JpaRepository<EOAddress, Long> {
    Integer countByOrganizationRegistry(OrganizationRegistry o);
}
