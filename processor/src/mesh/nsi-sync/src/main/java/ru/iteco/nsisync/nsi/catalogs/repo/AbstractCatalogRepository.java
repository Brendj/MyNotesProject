package ru.iteco.nsisync.nsi.catalogs.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.iteco.nsisync.nsi.catalogs.models.AbstractCatalog;

@NoRepositoryBean
public interface AbstractCatalogRepository<T extends AbstractCatalog, K extends Long> extends CrudRepository<T, K> {
    AbstractCatalog findFirstByTitleAndGlobalID(String title, Long globalId);
}
