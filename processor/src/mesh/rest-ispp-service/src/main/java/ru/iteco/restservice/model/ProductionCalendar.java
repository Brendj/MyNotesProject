package ru.iteco.restservice.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by nuc on 04.05.2021.
 */
@Entity
@Table(name = "cf_production_calendar")
public class ProductionCalendar {
    @Id
    @Column(name = "idofproductioncalendar")
    private Long id;

    @Type(type = "ru.iteco.restservice.model.type.DateBigIntType")
    @Column(name = "day")
    private Date day;

    @Column(name = "flag")
    private Integer flag;

    @Column(name = "version")
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
