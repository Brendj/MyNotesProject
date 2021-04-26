package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iteco.restservice.model.wt.WtDish;

public interface WtDishReadOnlyRepo extends JpaRepository<WtDish, Long> {

}
