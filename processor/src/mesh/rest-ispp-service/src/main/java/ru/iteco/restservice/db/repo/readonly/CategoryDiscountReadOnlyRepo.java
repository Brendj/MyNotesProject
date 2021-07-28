package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iteco.restservice.model.CategoryDiscount;

/**
 * Created by nuc on 04.05.2021.
 */
public interface CategoryDiscountReadOnlyRepo extends JpaRepository<CategoryDiscount, Long> {
}
