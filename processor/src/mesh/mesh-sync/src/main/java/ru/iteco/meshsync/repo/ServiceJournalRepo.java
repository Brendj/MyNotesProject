package ru.iteco.meshsync.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.iteco.meshsync.models.ServiceJournal;

public interface ServiceJournalRepo extends JpaRepository<ServiceJournal, Long> {

    @Modifying
    @Query(value = "UPDATE cf_mh_service_journal SET decided = true WHERE personguid like ?",
            nativeQuery = true)
    int decideAllRowsForPerson(String personGUID);
}
