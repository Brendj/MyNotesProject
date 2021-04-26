package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ProhibitionMenu;

import java.util.List;

public interface ProhibitionMenuReadOnlyRepo extends JpaRepository<ProhibitionMenu, Long> {
    @Query("select pm from ProhibitionMenu pm where pm.client = :client and pm.deletedState = false")
    List<ProhibitionMenu> findByClientAndDeletedState(@Param("client") Client client);
}
