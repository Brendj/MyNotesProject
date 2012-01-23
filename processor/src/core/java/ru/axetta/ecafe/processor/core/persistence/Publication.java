/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 16.09.11
 * Time: 13:14
 * To change this template use File | Settings | File Templates.
 */
public class Publication {
    private CompositeIdOfPublication compositeIdOfPublication;
    private String recordStatus;
    private String recordType;
    private String bibliographicLevel;
    private String hierarchicalLevel;
    private String codingLevel;
    private String formOfCatalogingDescription;
    private String data;
    private String author;
    private String title;
    private String title2;
    private String publicationDate;
    private String publisher;
    private long version;
    private Set<Circulation> circulations = new HashSet<Circulation>();

    public Publication() {
        // For Hibernate
    }

    public Publication(CompositeIdOfPublication compositeIdOfPublication, String recordStatus, String recordType,
            String bibliographicLevel, String hierarchicalLevel, String codingLevel, String formOfCatalogingDescription,
            long version) {
        this.compositeIdOfPublication = compositeIdOfPublication;
        this.recordStatus = recordStatus;
        this.recordType = recordType;
        this.bibliographicLevel = bibliographicLevel;
        this.hierarchicalLevel = hierarchicalLevel;
        this.codingLevel = codingLevel;
        this.formOfCatalogingDescription = formOfCatalogingDescription;
        this.version = version;
    }

    public CompositeIdOfPublication getCompositeIdOfPublication() {
        return compositeIdOfPublication;
    }

    public void setCompositeIdOfPublication(CompositeIdOfPublication compositeIdOfPublication) {
        this.compositeIdOfPublication = compositeIdOfPublication;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getBibliographicLevel() {
        return bibliographicLevel;
    }

    public void setBibliographicLevel(String bibliographicLevel) {
        this.bibliographicLevel = bibliographicLevel;
    }

    public String getHierarchicalLevel() {
        return hierarchicalLevel;
    }

    public void setHierarchicalLevel(String hierarchicalLevel) {
        this.hierarchicalLevel = hierarchicalLevel;
    }

    public String getCodingLevel() {
        return codingLevel;
    }

    public void setCodingLevel(String codingLevel) {
        this.codingLevel = codingLevel;
    }

    public String getFormOfCatalogingDescription() {
        return formOfCatalogingDescription;
    }

    public void setFormOfCatalogingDescription(String formOfCatalogingDescription) {
        this.formOfCatalogingDescription = formOfCatalogingDescription;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    private Set<Circulation> getCirculationsInternal() {
        // For Hibernate only
        return circulations;
    }

    private void setCirculationsInternal(Set<Circulation> cards) {
        // For Hibernate only
        this.circulations = circulations;
    }

    public Set<Circulation> getCirculations() {
        return Collections.unmodifiableSet(getCirculationsInternal());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Publication that = (Publication) o;

        if (!compositeIdOfPublication.equals(that.compositeIdOfPublication)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return compositeIdOfPublication.hashCode();
    }

    @Override
    public String toString() {
        return "Publication{" + "compositeIdOfPublication=" + compositeIdOfPublication + ", recordStatus='"
                + recordStatus + '\'' + ", recordType='" + recordType + '\'' + ", bibliographicLevel='"
                + bibliographicLevel + '\'' + ", hierarchicalLevel='" + hierarchicalLevel + '\'' + ", codingLevel='"
                + codingLevel + '\'' + ", formOfCatalogingDescription='" + formOfCatalogingDescription + '\''
                + ", data='" + data + '\'' + ", author='" + author + '\'' + ", title='" + title + '\'' + ", title2='"
                + title2 + '\'' + ", publicationDate='" + publicationDate + '\'' + ", publisher='" + publisher + '\''
                + ", version=" + version + ", circulations=" + circulations + '}';
    }
}
