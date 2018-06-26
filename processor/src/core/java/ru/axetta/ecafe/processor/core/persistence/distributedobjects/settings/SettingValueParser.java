package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.13
 * Time: 16:41
 * To change this template use File | Settings | File Templates.
 */
public class SettingValueParser {
    private AbstractParserBySettingValue parserBySettingValue;

    public SettingValueParser(String value,SettingsIds type) throws Exception {
        String[] values = value.split(";");
        switch (type){
            case CashierCheckPrinter: parserBySettingValue = new CashierCheckPrinterSettingValue(values);break;
            case SalesReportPrinter: parserBySettingValue = new SalesReportPrinterSettingValue(values);break;
            case CardBalanceReportPrinter: parserBySettingValue = new CardBalanceReportPrinterSettingValue(values); break;
            case AutoPlanPaymentSetting: parserBySettingValue = new AutoPlanPaymentSettingSettingValue(values); break;
            case SubscriberFeeding: parserBySettingValue = new SubscriberFeedingSettingSettingValue(values); break;
            case ReplacingMissingBeneficiaries: parserBySettingValue = new ReplacingMissingBeneficiariesSettingSettingValue(values); break;
            case PreOrderFeeding: parserBySettingValue = new PreOrderFeedingSettingValue(values); break;
            case PreOrderAutopay: parserBySettingValue = new PreOrderAutopaySettingValue(values); break;
        }
    }

    public AbstractParserBySettingValue getParserBySettingValue() {
        return parserBySettingValue;
    }
}
