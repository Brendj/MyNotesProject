package ru.iteco.msp.audit;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;
import java.util.Objects;

@Embeddable
public class AuditEntity {
    @Column(name = "createdate", nullable = false)
    private Date createDate;

    @Column(name = "lastupdate", nullable = false)
    private Date updateDate;

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditEntity)) return false;
        AuditEntity that = (AuditEntity) o;
        return getCreateDate().equals(that.getCreateDate()) &&
                getUpdateDate().equals(that.getUpdateDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCreateDate(), getUpdateDate());
    }
}
