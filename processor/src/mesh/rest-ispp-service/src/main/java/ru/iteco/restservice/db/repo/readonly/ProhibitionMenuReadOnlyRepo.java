package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.ProhibitionMenu;
import ru.iteco.restservice.model.wt.WtCategory;
import ru.iteco.restservice.model.wt.WtCategoryItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProhibitionMenuReadOnlyRepo extends JpaRepository<ProhibitionMenu, Long> {
    @Query("select pm from ProhibitionMenu pm where pm.client = :client and pm.deletedState = false")
    List<ProhibitionMenu> findByClientAndDeletedState(@Param("client") Client client);

    @Query("select distinct pm from ProhibitionMenu pm "
         + "join pm.categoryItem i "
         + "where pm.client = :client and i.wtCategory = :category and pm.deletedState = false")
    List<ProhibitionMenu> findProhibitionWithCategoryItemsByClientAndCategory(@Param("client") Client client,
            @Param("category") WtCategory category);

    @Query("select distinct pm from ProhibitionMenu pm "
         + "join pm.dish pmd "
         + "join pmd.categoryItems ci "
         + "where pm.client = :client and ci in :categoryItems ")
    List<ProhibitionMenu> findProhibitionWithDishByClientAndCategoryItems(@Param("client") Client client,
            @Param("categoryItems") Set<WtCategoryItem> categoryItem);

    @Query("select distinct pm from ProhibitionMenu pm "
            + "join pm.dish pmd "
            + "join pmd.categoryItems ci "
            + "where pm.client = :client and ci = :categoryItem and pm.deletedState = false")
    List<ProhibitionMenu> findProhibitionWithDishByClientAndCategoryItem(@Param("client") Client client,
            @Param("categoryItem") WtCategoryItem categoryItem);


    @Query ("select max(pm.version) from ProhibitionMenu pm")
    Long getMaxVersion();

    Optional<ProhibitionMenu> findByIdOfProhibitionsAndDeletedStateIsFalse(Long id);

    List<ProhibitionMenu> findByClientAndDeletedStateIsFalse(Long id);
}
