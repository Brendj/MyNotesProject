package ru.iteco.restservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "cf_contragents")
public class Contragent implements Serializable {
    @Id
    @Column(name = "idofcontragent")
    private Long id;

    @Column(name = "contragentname")
    private String name;

    /*@JsonIgnore
    @OneToMany(mappedBy = "contragent", fetch = FetchType.LAZY)
    @Where(clause = "organizationtype != 2")
    private Set<Org> orgs;*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public Set<Org> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<Org> orgs) {
        this.orgs = orgs;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contragent)) return false;
        Contragent that = (Contragent) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
