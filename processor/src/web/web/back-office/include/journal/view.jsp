<%--
  ~ Copyright (c) 2012. Axetta LLC. All Rights Reserved.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: damir
  Date: 25.01.12
  Time: 12:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib prefix="rich" uri="http://richfaces.org/rich" %>
<%@ taglib prefix="a4j" uri="http://richfaces.org/a4j" %>

<h:panelGrid styleClass="borderless-grid" id="journalViewPanel"
             binding="#{mainPage.journalViewPage.pageComponent}">

   <h:dataTable id="journalTable" value="#{mainPage.journalViewPage.journal}" var="item">
       <h:column>
           <f:facet name="header">
               <h:outputText escape="true" value="Ид." />
           </f:facet>
           <h:outputText value="#{item.idOfTransactionJournal}" />
       </h:column>
       <h:column>
           <f:facet name="header">
               <h:outputText escape="true" value="Дата" />
           </f:facet>
           <h:outputText value="#{item.transDate}" converter="timeConverter" />
       </h:column>
   </h:dataTable>
</h:panelGrid>
