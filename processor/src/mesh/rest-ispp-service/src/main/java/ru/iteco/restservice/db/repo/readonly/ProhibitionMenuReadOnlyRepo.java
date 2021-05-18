package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ProhibitionMenu;
import ru.iteco.restservice.model.wt.WtCategory;
import ru.iteco.restservice.model.wt.WtCategoryItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProhibitionMenuReadOnlyRepo extends JpaRepository<ProhibitionMenu, Long> {
    @Query("select pm from ProhibitionMenu pm where pm.client = :client and pm.deletedState = false")
    List<ProhibitionMenu> findByClientAndDeletedState(@Param("client") Client client);

    @Query("select pm from ProhibitionMenu pm "
         + "inner join pm.category c "
         + "inner join c.categoryItems i "
         + "left join WtDish d on d.category = c "
         + "where pm.client = :client and pm.category = :category and pm.categoryItem = i and pm.dish = d ")
    List<ProhibitionMenu> findRowsForDeleteByClientAndCategory(@Param("client") Client client,
            @Param("category") WtCategory category);

    @Query("select pm from ProhibitionMenu pm "
            + "inner join pm.categoryItem i "
            + "left join WtDish d on i.wtCategory = d.category and pm.dish = d "
            + "where pm.client = :client and pm.categoryItem = :categoryItem  ")
    List<ProhibitionMenu> findRowsForDeleteByClientAndCategoryItems(@Param("client") Client client,
            @Param("categoryItem") WtCategoryItem categoryItem);
}
