package ru.axetta.ecafe.processor.core.persistence;

import java.util.Objects;

public class ReportHandleRuleRoute {
    private Long idOfReportHandleRuleRoute;
    private String route;
    private ReportHandleRule reportHandleRule;

    public ReportHandleRuleRoute(){
        // for Hibernate
    }

    public ReportHandleRuleRoute(String route, ReportHandleRule reportHandleRule) {
        this.route = route;
        this.reportHandleRule = reportHandleRule;
    }

    public Long getIdOfReportHandleRuleRoute() {
        return idOfReportHandleRuleRoute;
    }

    public void setIdOfReportHandleRuleRoute(Long idOfReportHandleRuleRoute) {
        this.idOfReportHandleRuleRoute = idOfReportHandleRuleRoute;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public ReportHandleRule getReportHandleRule() {
        return reportHandleRule;
    }

    public void setReportHandleRule(ReportHandleRule reportHandleRule) {
        this.reportHandleRule = reportHandleRule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportHandleRuleRoute route1 = (ReportHandleRuleRoute) o;
        return Objects.equals(idOfReportHandleRuleRoute, route1.idOfReportHandleRuleRoute) && route.equals(route1.route);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOfReportHandleRuleRoute, route);
    }
}
