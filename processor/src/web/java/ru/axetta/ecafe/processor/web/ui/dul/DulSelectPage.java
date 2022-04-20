package ru.axetta.ecafe.processor.web.ui.dul;

import ru.axetta.ecafe.processor.core.persistence.DulGuide;

import java.util.List;

public class DulSelectPage {
    private DulGuide dulGuide;
    private List<DulGuide> dulGuideList;

    public DulGuide getDulGuide() {
        return dulGuide;
    }

    public void setDulGuide(DulGuide dulGuide) {
        this.dulGuide = dulGuide;
    }
}
