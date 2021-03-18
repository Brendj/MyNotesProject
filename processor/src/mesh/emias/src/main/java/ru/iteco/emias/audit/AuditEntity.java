package ru.iteco.emias.audit;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Date;
import java.util.Objects;

@Embeddable
public class AuditEntity {
    @Column(name = "createdate", nullable = false)
    private Long createDate;

    @Column(name = "updatedate", nullable = false)
    private Long updateDate;

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Long updateDate) {
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
