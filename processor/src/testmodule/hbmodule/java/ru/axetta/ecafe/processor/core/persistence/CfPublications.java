package ru.axetta.ecafe.processor.core.persistence;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 12.12.12
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class CfPublications {

    private long idofpublication;

    public long getIdofpublication() {
        return idofpublication;
    }

    public void setIdofpublication(long idofpublication) {
        this.idofpublication = idofpublication;
    }

    private byte[] dataofpublication;

    public byte[] getDataofpublication() {
        return dataofpublication;
    }

    public void setDataofpublication(byte[] dataofpublication) {
        this.dataofpublication = dataofpublication;
    }

    private String author;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title2;

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    private String publicationdate;

    public String getPublicationdate() {
        return publicationdate;
    }

    public void setPublicationdate(String publicationdate) {
        this.publicationdate = publicationdate;
    }

    private String publisher;

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    private String isbn;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    private boolean validisbn;

    public boolean isValidisbn() {
        return validisbn;
    }

    public void setValidisbn(boolean validisbn) {
        this.validisbn = validisbn;
    }

    private int hash;

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    private long globalversion;

    public long getGlobalversion() {
        return globalversion;
    }

    public void setGlobalversion(long globalversion) {
        this.globalversion = globalversion;
    }

    private long orgowner;

    public long getOrgowner() {
        return orgowner;
    }

    public void setOrgowner(long orgowner) {
        this.orgowner = orgowner;
    }

    private boolean deletedstate;

    public boolean isDeletedstate() {
        return deletedstate;
    }

    public void setDeletedstate(boolean deletedstate) {
        this.deletedstate = deletedstate;
    }

    private String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    private long lastupdate;

    public long getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(long lastupdate) {
        this.lastupdate = lastupdate;
    }

    private long deletedate;

    public long getDeletedate() {
        return deletedate;
    }

    public void setDeletedate(long deletedate) {
        this.deletedate = deletedate;
    }

    private long createddate;

    public long getCreateddate() {
        return createddate;
    }

    public void setCreateddate(long createddate) {
        this.createddate = createddate;
    }

    private int sendall;

    public int getSendall() {
        return sendall;
    }

    public void setSendall(int sendall) {
        this.sendall = sendall;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CfPublications that = (CfPublications) o;

        if (createddate != that.createddate) {
            return false;
        }
        if (deletedate != that.deletedate) {
            return false;
        }
        if (deletedstate != that.deletedstate) {
            return false;
        }
        if (globalversion != that.globalversion) {
            return false;
        }
        if (hash != that.hash) {
            return false;
        }
        if (idofpublication != that.idofpublication) {
            return false;
        }
        if (lastupdate != that.lastupdate) {
            return false;
        }
        if (orgowner != that.orgowner) {
            return false;
        }
        if (sendall != that.sendall) {
            return false;
        }
        if (validisbn != that.validisbn) {
            return false;
        }
        if (author != null ? !author.equals(that.author) : that.author != null) {
            return false;
        }
        if (!Arrays.equals(dataofpublication, that.dataofpublication)) {
            return false;
        }
        if (guid != null ? !guid.equals(that.guid) : that.guid != null) {
            return false;
        }
        if (isbn != null ? !isbn.equals(that.isbn) : that.isbn != null) {
            return false;
        }
        if (publicationdate != null ? !publicationdate.equals(that.publicationdate) : that.publicationdate != null) {
            return false;
        }
        if (publisher != null ? !publisher.equals(that.publisher) : that.publisher != null) {
            return false;
        }
        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }
        if (title2 != null ? !title2.equals(that.title2) : that.title2 != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idofpublication ^ (idofpublication >>> 32));
        result = 31 * result + (dataofpublication != null ? Arrays.hashCode(dataofpublication) : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (title2 != null ? title2.hashCode() : 0);
        result = 31 * result + (publicationdate != null ? publicationdate.hashCode() : 0);
        result = 31 * result + (publisher != null ? publisher.hashCode() : 0);
        result = 31 * result + (isbn != null ? isbn.hashCode() : 0);
        result = 31 * result + (validisbn ? 1 : 0);
        result = 31 * result + hash;
        result = 31 * result + (int) (globalversion ^ (globalversion >>> 32));
        result = 31 * result + (int) (orgowner ^ (orgowner >>> 32));
        result = 31 * result + (deletedstate ? 1 : 0);
        result = 31 * result + (guid != null ? guid.hashCode() : 0);
        result = 31 * result + (int) (lastupdate ^ (lastupdate >>> 32));
        result = 31 * result + (int) (deletedate ^ (deletedate >>> 32));
        result = 31 * result + (int) (createddate ^ (createddate >>> 32));
        result = 31 * result + sendall;
        return result;
    }

    private Collection<CfJournals> cfJournalsesByIdofpublication;

    public Collection<CfJournals> getCfJournalsesByIdofpublication() {
        return cfJournalsesByIdofpublication;
    }

    public void setCfJournalsesByIdofpublication(Collection<CfJournals> cfJournalsesByIdofpublication) {
        this.cfJournalsesByIdofpublication = cfJournalsesByIdofpublication;
    }
}
