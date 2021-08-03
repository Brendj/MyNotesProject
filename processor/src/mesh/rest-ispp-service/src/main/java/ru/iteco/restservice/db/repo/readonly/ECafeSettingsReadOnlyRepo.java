package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.ECafeSettings;
import ru.iteco.restservice.model.enums.SettingsIds;

import java.util.List;

public interface ECafeSettingsReadOnlyRepo extends CrudRepository<ECafeSettings, Long> {
    @Query(value = "select es from ECafeSettings es where es.orgOwner = :idOfOrg " +
            "and es.settingsId = :settingsId and es.deletedState = false")
    List getECafeSettingsByOrg(@Param("idOfOrg") Long idOfOrg, @Param("settingsId") SettingsIds settingsId);
}
