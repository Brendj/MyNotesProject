/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

/**
 * User: regal
 * Date: 11.03.15
 * Time: 18:49
 */
public class SettingsConfig {
    private Boolean ecafeAutopaymentBkEnabled;
    private Boolean cardsEditDisabled;

    public SettingsConfig() {
    }


    public boolean isEcafeAutopaymentBkEnabled() {
        if (ecafeAutopaymentBkEnabled == null){
            ecafeAutopaymentBkEnabled = Boolean.valueOf((String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.autopayment.bk.enabled"));
            if (ecafeAutopaymentBkEnabled == null){
                ecafeAutopaymentBkEnabled = false;
            }
        }
        return ecafeAutopaymentBkEnabled;
    }

    public void setEcafeAutopaymentBkEnabled(boolean ecafeAutopaymentBkEnabled) {
        this.ecafeAutopaymentBkEnabled = ecafeAutopaymentBkEnabled;
    }


    public boolean isCardsEditDisabled(){
        if (cardsEditDisabled == null){
            cardsEditDisabled = Boolean.valueOf((String) RuntimeContext.getInstance().getConfigProperties().get("ecafe.processor.cards.edit.disabled"));
            if (cardsEditDisabled == null){
                cardsEditDisabled = false;
            }
        }
        return cardsEditDisabled;
    }
}
