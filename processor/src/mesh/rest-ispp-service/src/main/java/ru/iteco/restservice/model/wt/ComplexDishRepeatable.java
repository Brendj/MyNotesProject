package ru.iteco.restservice.model.wt;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import ru.iteco.restservice.model.enums.EntityStateType;

import javax.persistence.*;

/**
 * Created by nuc on 06.05.2021.
 */
@Entity
@Table(name = "cf_wt_complexes_dishes_repeatable")
public class ComplexDishRepeatable {
    @Id
    @Column(name = "idofrelation")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idofcomplex")
    private WtComplex complex;

    @Column(name = "idofcomplex", insertable = false, updatable = false)
    private Long complexId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idofdish")
    private WtDish dish;

    @Column(name = "idofdish", insertable = false, updatable = false)
    private Long dishId;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "deletestate")
    private EntityStateType state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WtComplex getComplex() {
        return complex;
    }

    public void setComplex(WtComplex complex) {
        this.complex = complex;
    }

    public Long getComplexId() {
        return complexId;
    }

    public void setComplexId(Long complexId) {
        this.complexId = complexId;
    }

    public WtDish getDish() {
        return dish;
    }

    public void setDish(WtDish dish) {
        this.dish = dish;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public EntityStateType getState() {
        return state;
    }

    public void setState(EntityStateType state) {
        this.state = state;
    }
}
