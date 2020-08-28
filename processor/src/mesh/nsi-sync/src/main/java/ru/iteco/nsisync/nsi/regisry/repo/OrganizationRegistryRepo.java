package ru.iteco.nsisync.nsi.regisry.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iteco.nsisync.nsi.regisry.models.OrganizationRegistry;

public interface OrganizationRegistryRepo extends JpaRepository<OrganizationRegistry, Long> {
}
