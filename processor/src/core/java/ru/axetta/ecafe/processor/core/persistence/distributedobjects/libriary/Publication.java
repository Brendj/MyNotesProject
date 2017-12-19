/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.libriary;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.LibraryDistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.Base64AndZip;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;
import ru.axetta.ecafe.processor.core.utils.rusmarc.ISBN;
import ru.axetta.ecafe.processor.core.utils.rusmarc.Record;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 09.07.12
 * Time: 23:10
 * To change this template use File | Settings | File Templates.
 */
public class Publication extends LibraryDistributedObject {

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
     private Boolean validISBN;
    private Set<Journal> journalInternal;
    private Set<Instance> instanceInternal;

    private BBKDetails bbkDetail;
    private String guidBBKDetail;
    private Long idOfLang;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("bbkDetail", "de", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("isbn"), "isbn");
        projectionList.add(Projections.property("data"), "data");
        projectionList.add(Projections.property("author"), "author");
        projectionList.add(Projections.property("title"), "title");
        projectionList.add(Projections.property("publisher"), "publisher");
        projectionList.add(Projections.property("hash"), "hash");
        projectionList.add(Projections.property("title2"), "title2");
        projectionList.add(Projections.property("publicationdate"), "publicationdate");
        projectionList.add(Projections.property("validISBN"), "validISBN");

        projectionList.add(Projections.property("de.guid"), "guidBBKDetail");
        projectionList.add(Projections.property("idOfLang"), "idOfLang");
        criteria.setProjection(projectionList);
    }

    @Override
    protected void appendAttributes(Element element) {
        String decodedString = Base64AndZip.enCode(data);
        XMLUtils.setAttributeIfNotNull(element, "Data", decodedString);
        XMLUtils.setAttributeIfNotNull(element, "IdOfLang", getIdOfLang());
        XMLUtils.setAttributeIfNotNull(element, "GuidBBKDetail", getGuidBBKDetail());
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        if(!(isbn==null || isbn.isEmpty() || publicationdate==null || publicationdate.isEmpty() || !validISBN)){
            Criteria criteria = session.createCriteria(Publication.class);
            criteria.add(Restrictions.eq("isbn",isbn));
            criteria.add(Restrictions.eq("publicationdate",publicationdate));
            criteria.add(Restrictions.eq("validISBN", true));
            criteria.add(Restrictions.ne("guid", guid));
            Publication publication = null;
            List publicationList = criteria.list();
            if (publicationList != null && !publicationList.isEmpty()) {
                publication = (Publication) publicationList.get(0);
            }
            session.clear();
            if(!(publication==null || publication.getDeletedState() || guid.equals(publication.getGuid()))){
                //Попытаемся слить записи Publication.Data по полям Русмарк.
                try
                {
                    mergeRecords(publication);
                    if (this.guidBBKDetail != null)
                        publication.setGuidBBKDetail(this.getGuidBBKDetail());
                    if (this.idOfLang != null)
                        publication.setIdOfLang(this.getIdOfLang());

                    mergedDistributedObject = publication;
                }
                catch (IOException exception) {}
                DistributedObjectException distributedObjectException =  new DistributedObjectException("Publication DATA_EXIST_VALUE isbn and publicationdate equals");
                distributedObjectException.setData(publication.getGuid());
                throw  distributedObjectException;
            }
        } else {
            Criteria criteria = session.createCriteria(Publication.class);
            criteria.add(Restrictions.eq("hash",hash));
            List<Publication> list = criteria.list();
            Publication publication = list.isEmpty() ? null : list.get(0);
            session.clear();
            if(!(publication==null || publication.getDeletedState() || guid.equals(publication.getGuid()))){
                try {
                    mergeRecords(publication);
                    if (this.guidBBKDetail != null)
                        publication.setGuidBBKDetail(this.getGuidBBKDetail());
                    if (this.idOfLang != null)
                        publication.setIdOfLang(this.getIdOfLang());
                    mergedDistributedObject = publication;
                }
                catch (IOException exception) {}
                DistributedObjectException distributedObjectException =  new DistributedObjectException("Publication DATA_EXIST_VALUE hash equals");
                distributedObjectException.setData(publication.getGuid());
                throw  distributedObjectException;
            }
        }
        BBKDetails bbkDetailLocal = DAOUtils.findDistributedObjectByRefGUID(BBKDetails.class, session, getGuidBBKDetail());
        if (null != bbkDetailLocal) {
            setBbkDetail(bbkDetailLocal);
        }
    }

    private void mergeRecords(Publication publication) throws IOException{
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(publication.getData()));
        Record recordOnServer = new Record(dataInputStream); //получили поле Data из записи, сохраненной в таблице сервера
        Record recordFromPacket = new Record(new DataInputStream(new ByteArrayInputStream(data))); //поле Data из клиентского пакета
        Record recordToSave = recordOnServer.MergeRecords(recordFromPacket, recordOnServer);
        publication.setData(recordToSave.getRUSMARCRecord());
    }

    @Override
    protected Publication parseAttributes(Node node) throws Exception {
        String data = XMLUtils.getStringAttributeValue(node, "Data", 65536);
        DataInputStream dataInputStream = new DataInputStream(
                new ByteArrayInputStream(Base64AndZip.decode(data.getBytes())));
        Record record = new Record(dataInputStream);

        setData(record.getRUSMARCRecord());

        ISBN isbn1 = record.getISBN();
        String stringIsbn = isbn1.toString();
        if (stringIsbn != null) {
            setIsbn(stringIsbn);
        }

        setValidISBN(isbn1.getState() == ISBN.StateEnum.Normal);

        Integer stringHash = record.getStringForHash().hashCode();
        setHash(stringHash);

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
        setGuidBBKDetail(XMLUtils.getStringAttributeValue(node, "GuidBBKDetail", 36));
        setIdOfLang(XMLUtils.getLongAttributeValue(node, "IdOfLang"));
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setData(((Publication) distributedObject).getData());
        setOrgOwner(distributedObject.getOrgOwner());
        setIsbn(((Publication) distributedObject).getIsbn());
        setAuthor(((Publication) distributedObject).getAuthor());
        setTitle(((Publication) distributedObject).getTitle());
        setTitle2(((Publication) distributedObject).getTitle2());
        setPublicationdate(((Publication) distributedObject).getPublicationdate());
        setPublisher(((Publication) distributedObject).getPublisher());
        setHash(((Publication) distributedObject).getHash());
        setValidISBN(((Publication) distributedObject).getValidISBN());
        setBbkDetail(((Publication) distributedObject).getBbkDetail());
        setGuidBBKDetail(((Publication) distributedObject).getGuidBBKDetail());
        setIdOfLang(((Publication) distributedObject).getIdOfLang());
    }

    public Set<Instance> getInstanceInternal() {
        return instanceInternal;
    }

    public void setInstanceInternal(Set<Instance> instanceInternal) {
        this.instanceInternal = instanceInternal;
    }

    public Set<Journal> getJournalInternal() {
        return journalInternal;
    }

    public void setJournalInternal(Set<Journal> journalInternal) {
        this.journalInternal = journalInternal;
    }

    public Boolean getValidISBN() {
        return validISBN;
    }

    public void setValidISBN(Boolean validISBN) {
        this.validISBN = validISBN;
    }

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

    public BBKDetails getBbkDetail() {
        return bbkDetail;
    }

    public void setBbkDetail(BBKDetails bbkDetails) {
        this.bbkDetail = bbkDetails;
    }

    public String getGuidBBKDetail() {
        return guidBBKDetail;
    }

    public void setGuidBBKDetail(String guidBBKDetail) {
        this.guidBBKDetail = guidBBKDetail;
    }

    public Long getIdOfLang() {
        return idOfLang;
    }

    public void setIdOfLang(Long idOfLang) {
        this.idOfLang = idOfLang;
    }

    @Override
    public String toString() {
        return String
                .format("Publication{isbn='%s', author='%s', title='%s', title2='%s', publicationdate='%s', publisher='%s', hash='%d'}",
                        isbn, author, title, title2, publicationdate, publisher, hash);
    }
}
