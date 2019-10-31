/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.xmlreport;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 08.12.14
 * Time: 15:01
 */
@Deprecated
public class XmlReportGenerator {

    public XmlReportGenerator() {
    }

    public Document createXmlFile(
            List<DailyFormationOfRegistriesService.DailyFormationOfRegistriesModel> dailyFormationOfRegistriesModelList)
            throws ParserConfigurationException {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element rootElement = document.createElement("DailyReport");
        document.appendChild(rootElement);

        Element report = document.createElement("Report");
        rootElement.appendChild(report);

        report.setAttribute("DateOfGeneration",
                CalendarUtils.dateToString(dailyFormationOfRegistriesModelList.get(0).getGeneratedDate()));

        for (DailyFormationOfRegistriesService.DailyFormationOfRegistriesModel dailyFormationOfRegistriesModel : dailyFormationOfRegistriesModelList) {

            Element contragent = document.createElement("ContragentTSP");

            contragent.setAttribute("ContragentId", String.valueOf(dailyFormationOfRegistriesModel.getContragentId()));
            contragent.setAttribute("ContragentName", dailyFormationOfRegistriesModel.getContragentName());

            for (DailyFormationOfRegistriesService.OrgItem orgItem : dailyFormationOfRegistriesModel.getOrgItemList()) {
                Element organization = document.createElement("Organization");
                contragent.appendChild(organization);
                organization.setAttribute("OrganizationId", String.valueOf(orgItem.getIdOfOrg()));
                organization.setAttribute("OrganizationName", orgItem.getOfficialName());
                organization.setAttribute("OrganizationAddress", orgItem.getAddress());
                organization.setAttribute("TotalBalance", String.valueOf(orgItem.getTotalBalance()));
                organization.setAttribute("RechargeAmount", String.valueOf(orgItem.getRechargeAmount()));
                organization.setAttribute("SalesAmount", String.valueOf(orgItem.getSalesAmount()));
            }
            report.appendChild(contragent);
        }
        return document;
    }

    public void unloadXmlFile(Document document, String date) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);

            String filePath = (String) RuntimeContext.getInstance().getConfigProperties()
                    .get("ecafe.processor.registries.path");

            File dir = new File(filePath);
            boolean bool = dir.mkdirs();

            File file = new File(filePath + "/registriesReport-" + date + ".xml");

            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}
