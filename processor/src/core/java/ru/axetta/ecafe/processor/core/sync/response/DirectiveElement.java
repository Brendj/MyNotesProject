/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.TradeAccountConfigChange;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgRepository;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.request.DirectivesRequest;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 28.06.13
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class DirectiveElement implements AbstractToElement{

    private List<DirectiveItem> directiveItemList;

    public void process(Session session, Org org) throws Exception{

        directiveItemList = new ArrayList<DirectiveItem>();

        Boolean fullSync = org.getFullSyncParam();
        if(fullSync) {
            directiveItemList.add(new DirectiveItem("FullSync","1"));
            DAOUtils.falseFullSyncByOrg(session, org.getIdOfOrg());
        }

        Boolean commodityAccounting = org.getCommodityAccounting();
        directiveItemList.add(new DirectiveItem("CommodityAccounting",commodityAccounting?"1":"0"));
    }

    public void processForFullSync(DirectivesRequest directivesRequest, Org org) throws Exception {

        directiveItemList = new ArrayList<DirectiveItem>();

        Boolean disableEditingClientsFromAISReestr = org.getDisableEditingClientsFromAISReestr();
        directiveItemList.add(new DirectiveItem("DisableEditingClientsFromAISReestr", disableEditingClientsFromAISReestr?"1":"0"));

        Boolean usePaydableSubscriptionFeeding = org.getUsePaydableSubscriptionFeeding();
        directiveItemList.add(new DirectiveItem("UsePaydableSubscriptionFeeding", usePaydableSubscriptionFeeding?"1":"0"));

        Boolean useVariableDiscountFeeding = org.getVariableFeeding();
        directiveItemList.add(new DirectiveItem("UseVariableDiscountFeeding", useVariableDiscountFeeding?"1":"0"));

        Boolean usePlanOrders = org.getUsePlanOrders();
        directiveItemList.add(new DirectiveItem("UsePlanOrders",usePlanOrders?"1":"0"));

        OrgRepository orgRepository = OrgRepository.getInstance();
        Long paymentContragentId = orgRepository.isPaymentByCashierEnabled(org.getIdOfOrg());
        directiveItemList.add(new DirectiveItem("UseAccountDepositInPos",paymentContragentId != null?"1":"0", paymentContragentId!=null?""+paymentContragentId:null));

        Boolean oneActiveCard = OrgReadOnlyRepository.getInstance().isOneActiveCard(org.getIdOfOrg());
        directiveItemList.add(new DirectiveItem("UseOnlyOneActiveMainCard",oneActiveCard ?"1":"0"));

        Boolean commodityAccounting = org.getCommodityAccounting();
        directiveItemList.add(new DirectiveItem("CommodityAccounting",commodityAccounting?"1":"0"));

        Integer securityModeFlag = org.getSecurityLevel().getCode();
        directiveItemList.add(new DirectiveItem("IS_SECURITY_MODE_FLAG", securityModeFlag.toString()));

        Integer photoRegistryFlag = org.getPhotoRegistryDirective().getCode();
        directiveItemList.add(new DirectiveItem("IS_ALLOWED_PHOTO_REGISTRY", photoRegistryFlag.toString()));

        if(directivesRequest.getTradeConfigChangedSuccess() != null && directivesRequest.getTradeConfigChangedSuccess()) {
            //org.setTradeAccountConfigChangeDirective(TradeAccountConfigChange.NOT_CHANGED);
            DAOService.getInstance().saveTradeAccountConfigChangeDirective(org.getIdOfOrg());
        } else {
            Integer tradeAccountConfigChangedFlag = org.getTradeAccountConfigChangeDirective().getCode();
            if(tradeAccountConfigChangedFlag.equals(TradeAccountConfigChange.CHANGED.getCode())) {
                directiveItemList.add(new DirectiveItem("TRADE_ACCOUNT_CONFIG_CHANGED", tradeAccountConfigChangedFlag.toString()));
            }
        }

        if (directivesRequest.getIsWorkInSummerTime() != null) {
            DAOService.getInstance().saveWorkInSummerTimeDirective(org.getIdOfOrg(), directivesRequest.getIsWorkInSummerTime());
        }

        Boolean disEditServicesInfoFlag = RuntimeContext.getInstance().getSmsService().ignoreNotifyFlags();
        directiveItemList.add(new DirectiveItem("DISABLE_EDIT_INFORMATION_SERVICES_FOR_CLIENTS", disEditServicesInfoFlag ? "1":"0"));

        Integer loginUniqControlPeriod = RuntimeContext.getInstance().getOptionValueInt(
                Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_LOGIN_REUSE);
        directiveItemList.add(new DirectiveItem("IS_LOGIN_UNIQ_CONTROL_PERIOD", loginUniqControlPeriod.toString()));

        Integer maxInactiveTime = RuntimeContext.getInstance().getOptionValueInt(
                Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_UNUSED_LOGIN_AFTER);
        directiveItemList.add(new DirectiveItem("IS_MAX_INACTIVITY_TIME", maxInactiveTime.toString()));

        Integer passwordChangePeriod = RuntimeContext.getInstance().getOptionValueInt(
                Option.OPTION_SECURITY_CLIENT_PERIOD_PASSWORD_CHANGE);
        directiveItemList.add(new DirectiveItem("IS_PASSWORD_CHANGE_PERIOD", passwordChangePeriod.toString()));

        Integer maxAuthFaultCount = RuntimeContext.getInstance().getOptionValueInt(
                Option.OPTION_SECURITY_CLIENT_MAX_AUTH_FAULT_COUNT);
        directiveItemList.add(new DirectiveItem("IS_MAX_AUTH_FAULT_COUNT", maxAuthFaultCount.toString()));

        Integer tmpBlockAccTime = RuntimeContext.getInstance().getOptionValueInt(
                Option.OPTION_SECURITY_CLIENT_TMP_BLOCK_ACC_TIME);
        directiveItemList.add(new DirectiveItem("IS_TMP_BLOCK_ACC_TIME", tmpBlockAccTime.toString()));

        Integer disableEmailEdit = RuntimeContext.getInstance().getOptionValueInt(
                Option.OPTION_DISABLE_EMAIL_EDIT);
        directiveItemList.add(new DirectiveItem("DISABLE_EMAIL_EDIT", disableEmailEdit.toString()));

        Boolean fullSync = org.getFullSyncParam();
        if(fullSync) {
            directiveItemList.add(new DirectiveItem("FullSync","1"));
            DAOService.getInstance().setFullSyncByOrg(org.getIdOfOrg(), false);
        }

        //OrgWritableRepository.getInstance().saveOrg(org);
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Directives");
        for (DirectiveItem directiveItem : this.directiveItemList) {
            element.appendChild(directiveItem.toElement(document));
        }
        return element;
    }

}
