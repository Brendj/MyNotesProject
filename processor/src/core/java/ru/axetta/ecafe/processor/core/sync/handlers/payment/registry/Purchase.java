package ru.axetta.ecafe.processor.core.sync.handlers.payment.registry;

import ru.axetta.ecafe.processor.core.persistence.MenuDetail;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import static ru.axetta.ecafe.processor.core.utils.XMLUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.10.13
 * Time: 16:00
 * To change this template use File | Settings | File Templates.
 */
public class Purchase {

    private final long discount;
    private final long socDiscount;
    private final long idOfOrderDetail;
    private final String name;
    private final long qty;
    private final long rPrice;
    private final String rootMenu;
    private final String menuOutput;
    private final int type;
    private final String menuGroup;
    private final int menuOrigin;
    private final String itemCode;
    private final String guidOfGoods;
    private final Long idOfRule;
    private final Long idOfMenu;
    private final String manufacturer;
    private final String guidPreOrderDetail;
    private final Integer fRation;

    public static Purchase build(Node purchaseNode, SyncRequest.MenuGroups menuGroups) throws Exception {
        NamedNodeMap namedNodeMap = purchaseNode.getAttributes();
        long discount = getLongValue(namedNodeMap, "Discount");
        Long socDiscount = null;
        if (namedNodeMap.getNamedItem("SocDiscount") != null) {
            socDiscount = getLongValue(namedNodeMap, "SocDiscount");
        }
        long idOfOrderDetail = getLongValue(namedNodeMap, "IdOfOrderDetails");
        String name = StringUtils.substring(namedNodeMap.getNamedItem("Name").getTextContent(), 0, 90);
        long qty = getLongValue(namedNodeMap, "Qty");
        long rPrice = getLongValue(namedNodeMap, "rPrice");
        String rootMenu = getStringValueNullSafe(namedNodeMap, "RootMenu");
        String menuOutput = getStringValueNullSafe(namedNodeMap, "O");

        String itemCode = getStringValueNullSafe(namedNodeMap, "ItemCode");
        if(itemCode != null){
                            /* если значение не пусто то урежем лишнее и запишем данные только в 32 символа */
            itemCode = StringUtils.substring(itemCode.trim(),0,32);
        }
        if (menuOutput == null) {
            menuOutput = "";
        }

        int type = 0; // свободный выбор
        String typeStr = getStringValueNullSafe(namedNodeMap, "T");
        if (typeStr != null) {
            type = Integer.parseInt(typeStr);
        }
        int menuOrigin = 0; // собственное
        String menuOriginStr = getStringValueNullSafe(namedNodeMap, "MenuOrigin");
        if (menuOriginStr != null) {
            menuOrigin = Integer.parseInt(menuOriginStr);
        }
        String menuGroup = null;
        String refIdOfMenuGroupStr = getStringValueNullSafe(namedNodeMap, "refIdOfMG");
        if (refIdOfMenuGroupStr != null) {
            long refIdOfMenuGroup = Long.parseLong(refIdOfMenuGroupStr);
            menuGroup = menuGroups.findMenuGroup(refIdOfMenuGroup);
        }
        if (menuGroup == null) {
            menuGroup = MenuDetail.DEFAULT_GROUP_NAME;
        }
        String guidOfGoods = getStringValueNullSafe(namedNodeMap, "GoodsGuid");

        Long idOfRule = null; // собственное
        String idOfRuleStr = getStringValueNullSafe(namedNodeMap, "IdOfRule");
        if (idOfRuleStr != null) {
            idOfRule = Long.parseLong(idOfRuleStr);
        }

        Long idOfMenu = null;
        String idOfMenuStr = getStringValueNullSafe(namedNodeMap, "IdOfMenu");
        if (idOfMenuStr != null) {
            idOfMenu = Long.parseLong(idOfMenuStr);
        }

        String manufacturer = getStringValueNullSafe(namedNodeMap, "Manufacturer");

        String guidPreOrderDetail = getStringValueNullSafe(namedNodeMap, "GuidPreOrderDetail");
        Integer fRation = getIntegerValueNullSafe(namedNodeMap, "FRation");

        return new Purchase(discount, socDiscount, idOfOrderDetail, name, qty, rPrice, rootMenu,
                menuOutput, type, menuGroup, menuOrigin, itemCode, guidOfGoods, idOfRule, idOfMenu, manufacturer, guidPreOrderDetail, fRation);
    }

    public Purchase(long discount, long socDiscount, long idOfOrderDetail, String name, long qty,
            long rPrice, String rootMenu, String menuOutput, int type, String menuGroup, int menuOrigin,
            String itemCode, String guidOfGoods, Long idOfRule, Long idOfMenu, String manufacturer, String guidPreOrderDetail, Integer fRation) {
        this.discount = discount;
        this.socDiscount = socDiscount;
        this.idOfOrderDetail = idOfOrderDetail;
        this.name = name;
        this.qty = qty;
        this.rPrice = rPrice;
        this.rootMenu = rootMenu;
        this.menuOutput = menuOutput;
        this.type = type;
        this.menuGroup = menuGroup;
        this.menuOrigin = menuOrigin;
        this.itemCode = itemCode;
        this.guidOfGoods = guidOfGoods;
        this.idOfRule = idOfRule;
        this.idOfMenu = idOfMenu;
        this.manufacturer = manufacturer;
        this.guidPreOrderDetail = guidPreOrderDetail;
        this.fRation = fRation;
    }

    public Long getDiscount() {
        return discount;
    }

    public Long getSocDiscount() {
        return socDiscount;
    }

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public String getName() {
        return name;
    }

    public Long getQty() {
        return qty;
    }

    public Long getrPrice() {
        return rPrice;
    }

    public String getRootMenu() {
        return rootMenu;
    }

    public String getMenuOutput() {
        return menuOutput;
    }

    public Integer getType() {
        return type;
    }

    public String getMenuGroup() {
        return menuGroup;
    }

    public Integer getMenuOrigin() {
        return menuOrigin;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getGuidOfGoods() {
        return guidOfGoods;
    }

    public Long getIdOfRule() {
        return idOfRule;
    }

    public Long getIdOfMenu() {
        return idOfMenu;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "discount=" + discount +
                ", socDiscount=" + socDiscount +
                ", idOfOrderDetail=" + idOfOrderDetail +
                ", name='" + name + '\'' +
                ", qty=" + qty +
                ", rPrice=" + rPrice +
                ", rootMenu='" + rootMenu + '\'' +
                ", menuOutput='" + menuOutput + '\'' +
                ", type=" + type +
                ", menuGroup='" + menuGroup + '\'' +
                ", menuOrigin=" + menuOrigin +
                ", itemCode='" + itemCode + '\'' +
                ", guidOfGoods='" + guidOfGoods + '\'' +
                ", idOfRule=" + idOfRule +
                ", idOfMenu=" + idOfMenu +
                ", manufacturer=" + manufacturer +
                '}';
    }

    public String getGuidPreOrderDetail() {
        return guidPreOrderDetail;
    }

    public Integer getfRation() {
        return fRation;
    }
}
