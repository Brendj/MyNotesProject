package ru.axetta.ecafe.processor.core.persistence;

import java.util.Objects;

public class DulGuide {
    private Long documentTypeId;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DulGuide)) return false;
        DulGuide dulGuide = (DulGuide) o;
        return documentTypeId.equals(dulGuide.documentTypeId);
    }

    public Long getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(Long documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
