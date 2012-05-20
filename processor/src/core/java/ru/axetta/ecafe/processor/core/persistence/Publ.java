/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 03.05.12
 * Time: 21:15
 * To change this template use File | Settings | File Templates.
 */
public class Publ {
    private Long idofpubl;
    private String isbn;
    private String data;
    private String author;
    private String title;
    private String title2;
    private String publicationdate;
    private String publisher;
    private String hash;
    private long version;

    public Publ() {
    }

    public Publ(long idofpubl, String data, String author, String title, String title2, String publicationDate,
            String publisher, String hash, long version) {
        this.idofpubl = idofpubl;
        this.data = data;
        this.author = author;
        this.title = title;
        this.title2 = title2;
        this.publicationdate = publicationDate;
        this.publisher = publisher;
        this.hash = hash;
        this.version = version;
    }

    public Long getIdofpubl() {
        return idofpubl;
    }

    public void setIdofpubl(long idofpubl) {
        this.idofpubl = idofpubl;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
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

    public String getPublicationdate() {
        return publicationdate;
    }

    public void setPublicationdate(String publicationdate) {
        this.publicationdate = publicationdate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Publ that = (Publ) o;

        if (!(idofpubl == that.idofpubl)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idofpubl ^ (idofpubl >>> 32));
    }

    @Override
    public String toString() {
        return "Publ{" +
                "idofpubl=" + idofpubl +
                ", data='" + data + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", title2='" + title2 + '\'' +
                ", publicationdate='" + publicationdate + '\'' +
                ", publisher='" + publisher + '\'' +
                ", version=" + version +
                '}';
    }
}
