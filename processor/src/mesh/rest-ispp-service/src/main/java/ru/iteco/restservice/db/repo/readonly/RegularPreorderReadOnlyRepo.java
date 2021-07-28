package ru.iteco.restservice.db.repo.readonly;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.restservice.model.Client;
import ru.iteco.restservice.model.preorder.RegularPreorder;

import java.util.List;

public interface RegularPreorderReadOnlyRepo extends JpaRepository<RegularPreorder, Long> {
    @Query(value = "SELECT rp from RegularPreorder rp "
            + "WHERE rp.client = :client AND rp.deletedState = 0 order by rp.startDate")
    List<RegularPreorder> getRegularPreordersByClient(@Param("client") Client client);

    @Query(value = "select rp from RegularPreorder rp " +
            "where rp.client = :client and rp.deletedState = 0 and rp.idOfComplex = :idOfComplex")
    RegularPreorder getRegularPreorderByClientAndComplex(@Param("client") Client client,
                                                         @Param("idOfComplex") Integer idOfComplex);

    @Query(value = "select rp from RegularPreorder rp " +
            "where rp.client = :client and rp.deletedState = 0 and rp.idOfComplex = :idOfComplex " +
            "and rp.idOfDish = :idOfDish")
    RegularPreorder getRegularPreorderByClientComplexAndDish(@Param("client") Client client,
                                                         @Param("idOfComplex") Integer idOfComplex,
                                                         @Param("idOfDish") Long idOfDish);
}
