package ru.axetta.ecafe.processor.web.rest.items;

import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.04.14
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */
public class CycleDiagramItem {

    private Date diagramDate;
    private List<ComplexItem> list;
    private Long weekSum;

    public Date getDiagramDate() {
        return diagramDate;
    }

    public void setDiagramDate(Date diagramDate) {
        this.diagramDate = diagramDate;
    }

    public List<ComplexItem> getList() {
        return list;
    }

    public void setList(List<ComplexItem> list) {
        this.list = list;
    }

    public Long getWeekSum() {
        return weekSum;
    }

    public void setWeekSum(Long weekSum) {
        this.weekSum = weekSum;
    }
}
