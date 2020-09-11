package ru.iteco.meshsync.repo;

import org.springframework.data.repository.Repository;
import ru.iteco.meshsync.models.TrainingForm;

public interface TrainingFormRepo extends Repository<TrainingForm, Long> {
    TrainingForm getByIdAndArchiveIsFalse(Integer id);
}
