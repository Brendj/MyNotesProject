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

    @Query("select distinct pm from ProhibitionMenu pm "
         + "join pm.categoryItem i "
         + "where pm.client = :client and i.wtCategory = :category")
    List<ProhibitionMenu> findProhibitionWithCategoryItemsByClientAndCategory(@Param("client") Client client,
            @Param("category") WtCategory category);

    @Query("select distinct pm from ProhibitionMenu pm "
            + "join pm.dish pmd "
            + "where pm.client = :client and :categoryItem in (pm.categoryItem)")
    List<ProhibitionMenu> findProhibitionWithDishByClientAndCategoryItem(@Param("client") Client client,
            @Param("categoryItem") WtCategoryItem categoryItem);

    @Query ("select max(pm.version) from ProhibitionMenu pm")
    Long getMaxVersion();
}
