/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 11.10.13
 * Time: 17:22
 * To change this template use File | Settings | File Templates.
 */
public class RegistryChangeError {

    protected Long idOfRegistryChangeError;
    protected Long idOfOrg;
    protected Long revisionCreateDate;
    protected Long createDate;
    protected Long commentCreateDate;
    protected String error;
    protected String errorDetail;
    protected String comment;
    protected String commentAuthor;

    public RegistryChangeError() {
    }

    public RegistryChangeError(Long idOfRegistryChangeError, Long idOfOrg, Long revisionCreateDate, Long createDate,
            Long commentCreateDate, String error, String errorDetail, String comment, String commentAuthor) {
        this.idOfRegistryChangeError = idOfRegistryChangeError;
        this.idOfOrg = idOfOrg;
        this.revisionCreateDate = revisionCreateDate;
        this.createDate = createDate;
        this.commentCreateDate = commentCreateDate;
        this.error = error;
        this.errorDetail = errorDetail;
        this.comment = comment;
        this.commentAuthor = commentAuthor;
    }

    public Long getIdOfRegistryChangeError() {
        return idOfRegistryChangeError;
    }

    public void setIdOfRegistryChangeError(Long idOfRegistryChangeError) {
        this.idOfRegistryChangeError = idOfRegistryChangeError;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getRevisionCreateDate() {
        return revisionCreateDate;
    }

    public void setRevisionCreateDate(Long revisionCreateDate) {
        this.revisionCreateDate = revisionCreateDate;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getCommentCreateDate() {
        return commentCreateDate;
    }

    public void setCommentCreateDate(Long commentCreateDate) {
        this.commentCreateDate = commentCreateDate;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentAuthor() {
        return commentAuthor;
    }

    public void setCommentAuthor(String commentAuthor) {
        this.commentAuthor = commentAuthor;
    }

    public String getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }
}