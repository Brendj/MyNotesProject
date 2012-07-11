/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 09.07.12
 * Time: 23:10
 * To change this template use File | Settings | File Templates.
 */
public class Publication2 extends DistributedObject {

    //private Long idofpubl;
    private String isbn;
    private String data;
    private String author;
    private String title;
    private String title2;
    private String publicationdate;
    private String publisher;
    private String hash;

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element,"isbn", isbn);
        setAttribute(element,"data", data);
        setAttribute(element,"author", author);
        setAttribute(element,"title", title);
        setAttribute(element,"title2", title2);
        setAttribute(element,"publicationdate", publicationdate);
        setAttribute(element, "publisher", publisher);
    }

    @Override
    protected Publication2 parseAttributes(Node node) {

        String stringIsbn = getStringAttributeValue(node,"isbn",32);
        if(stringIsbn!=null) setIsbn(stringIsbn);
        String stringData= getStringAttributeValue(node,"data",32);
        if(stringData!=null) setData(stringData);
        String stringAuthor= getStringAttributeValue(node,"author",128);
        if(stringAuthor!=null) setAuthor(stringAuthor);
        String stringTitle= getStringAttributeValue(node,"title",512);
        if(stringTitle!=null) setTitle(stringTitle);
        String stringTitle2= getStringAttributeValue(node,"title2",2048);
        if(stringTitle2!=null) setTitle(stringTitle2);
        String stringPublicationdate = getStringAttributeValue(node,"publicationdate", 512);
        if(stringPublicationdate!=null) setPublicationdate(stringPublicationdate);
        String stringPublisher = getStringAttributeValue(node, "publisher", 512);
        if(stringPublisher!=null) setPublisher(stringPublisher);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setIsbn( ((Publication2) distributedObject).getIsbn());
        setData (((Publication2) distributedObject).getData());
        setAuthor( ((Publication2) distributedObject).getAuthor());
        setTitle (((Publication2) distributedObject).getTitle());
        setTitle2(((Publication2) distributedObject).getTitle2());
        setPublicationdate(((Publication2) distributedObject).getPublicationdate());
        setPublisher(((Publication2) distributedObject).getPublisher());
        setHash(((Publication2) distributedObject).getHash());
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

    @Override
    public String toString() {
        return "Publication{" +
                "isbn='" + isbn + '\'' +
                ", data='" + data + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", title2='" + title2 + '\'' +
                ", publicationdate='" + publicationdate + '\'' +
                ", publisher='" + publisher + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
