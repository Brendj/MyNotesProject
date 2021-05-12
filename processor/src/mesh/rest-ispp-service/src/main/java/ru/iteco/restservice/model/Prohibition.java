/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "cf_prohibitions")
public class Prohibition {
    @Id
    @Column(name = "idofprohibitions")
    private Long idOfProhibitions;

    @Column(name = "createdate")
    private Date createdate;

    @Column(name = "updatedate")
    private Date updatedate;
}
