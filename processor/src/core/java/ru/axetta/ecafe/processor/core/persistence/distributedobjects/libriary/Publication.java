/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.Base64AndZip;
import ru.axetta.rusmarc.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 09.07.12
 * Time: 23:10
 * To change this template use File | Settings | File Templates.
 */
public class Publication extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        //Publication publication = (Publication) DAOUtils.findDistributedObjectByRefGUID(session, guid);
        //if(!(publication==null || publication.getDeletedState() || guid.equals(publication.getGuid()))){
        if(!(isbn==null || isbn.isEmpty())){
            Criteria criteria = session.createCriteria(Publication.class);
            criteria.add(Restrictions.eq("isbn",isbn));
            Publication publication = (Publication) criteria.uniqueResult();
            if(!(publication==null || publication.getDeletedState() || guid.equals(publication.getGuid()))){
                DistributedObjectException distributedObjectException =  new DistributedObjectException("Publication DATA_EXIST_VALUE");
                distributedObjectException.setData(publication.getGuid());
                throw  distributedObjectException;
            }
        } else {
            Criteria criteria = session.createCriteria(Publication.class);
            criteria.add(Restrictions.eq("hash",hash));
            Publication publication = (Publication) criteria.uniqueResult();
            if(!(publication==null || publication.getDeletedState() || guid.equals(publication.getGuid()))){
                DistributedObjectException distributedObjectException =  new DistributedObjectException("Publication DATA_EXIST_VALUE");
                distributedObjectException.setData(publication.getGuid());
                throw  distributedObjectException;
            }
        }

    }

    private static final int AUTHOR = 2;
    private static final int TITLE = 0;
    private static final int TITLE2 = 1;
    private static final int PUBLISHER = 3;
    private static final int PUBLICATION_DATE = 4;

    private String isbn;
    private byte[] data;
    private String author;
    private String title;
    private String publisher;
    private Integer hash;
    private String title2;
    private String publicationdate;

    public String getPublicationdate() {
        return publicationdate;
    }

    public void setPublicationdate(String publicationdate) {
        this.publicationdate = publicationdate;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    @Override
    protected void appendAttributes(Element element) {
        String decodedString = null;
        try {
            decodedString = Base64AndZip.zipAndEncode(data);
        } catch (IOException e) {
            setDistributedObjectException(new DistributedObjectException("BUILD_DATA_PUBLICATION_VALUE"));
        }
        setAttribute(element, "Data", decodedString);
    }

    @Override
    protected Publication parseAttributes(Node node) throws Exception {

        String data = getStringAttributeValue(node, "Data", 65536);
        //String decodedString = new String(Base64AndZip.decodeAndUngzip(data.getBytes()), "UTF-8");
        DataInputStream dataInputStream = new DataInputStream(
                new ByteArrayInputStream(Base64AndZip.decodeAndUngzip(data.getBytes())));
        Record record = new Record(dataInputStream);

        setData(record.getRUSMARCRecord());

        String stringIsbn = record.getISBN();
        if (stringIsbn != null) {
            setIsbn(stringIsbn);
        }

        Integer stringHash = record.getStringForHash().hashCode();
        if (stringHash != null) {
            setHash(stringHash);
        }

        String[] info = record.getInfo();

        String stringAuthor = info[AUTHOR];
        if (stringAuthor != null) {
            setAuthor(stringAuthor);
        }
        String stringTitle = info[TITLE];
        if (stringTitle != null) {
            setTitle(stringTitle);
        }
        String stringTitle2 = info[TITLE2];
        if (stringTitle2 != null) {
            setTitle2(stringTitle2);
        }
        String stringPublicationDate = info[PUBLICATION_DATE];
        if (stringPublicationDate != null) {
            setPublicationdate(stringPublicationDate);
        }
        String stringPublisher = info[PUBLISHER];
        if (stringPublisher != null) {
            setPublisher(stringPublisher);
        }
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setIsbn(((Publication) distributedObject).getIsbn());
        setData(((Publication) distributedObject).getData());
        setAuthor(((Publication) distributedObject).getAuthor());
        setTitle(((Publication) distributedObject).getTitle());
        setTitle2(((Publication) distributedObject).getTitle2());
        setPublicationdate(((Publication) distributedObject).getPublicationdate());
        setPublisher(((Publication) distributedObject).getPublisher());
        setHash(((Publication) distributedObject).getHash());
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getHash() {
        return hash;
    }

    public void setHash(Integer hash) {
        this.hash = hash;
    }



    @Override
    public String toString() {
        return "Publication{" +
                "isbn='" + isbn + '\'' +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", title2='" + title2 + '\'' +
                ", publicationdate='" + publicationdate + '\'' +
                ", publisher='" + publisher + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
