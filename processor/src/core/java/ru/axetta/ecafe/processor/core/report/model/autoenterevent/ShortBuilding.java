package ru.axetta.ecafe.processor.core.report.model.autoenterevent;

/**
 * User: shamil
 * Date: 26.09.14
 * Time: 19:07
 */
public class ShortBuilding {
    private Long id;
    private String f05;
    private String f05l;

    public ShortBuilding() {
    }

    public ShortBuilding(String f05, String f05l) {
        this.f05 = f05;
        this.f05l = f05l;
    }

    public ShortBuilding(Long id, String f05, String f05l) {
        this.id = id;
        this.f05 = f05;
        this.f05l = f05l;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getF05() {
        return f05;
    }

    public void setF05(String f05) {
        this.f05 = f05;
    }

    public String getF05l() {
        return f05l;
    }

    public void setF05l(String f05l) {
        this.f05l = f05l;
    }
}
