package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.orgsettingstypes.AutoPlanPaymentSettingType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.13
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
public class AutoPlanPaymentSettingSettingValue extends AbstractParserBySettingValue{

    private boolean offOnFlag; //булевое значение ввкл выкл
    private Date payTime; //  0:00 - время автооплаты
    private int porog;//100 - порог срабатывания (от 0 до 100)
    private static SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("HH:mm");


    public AutoPlanPaymentSettingSettingValue(String[] values) throws ParseException {
        super(values);
    }

    @Override
    protected void parse(String[] values) throws ParseException {
        this.offOnFlag = values[0].equals("1");
        this.payTime = dateOnlyFormat.parse(values[1]);
        this.porog = Integer.parseInt(values[2]);
    }

    @Override
    public String build() {
        return (offOnFlag?1:0)+";"+dateOnlyFormat.format(payTime)+";"+porog+";";
    }

    @Override
    public boolean check() {
        return true;
    }

    @Override
    protected Integer gettypeByIndex(Integer index) {
        return AutoPlanPaymentSettingType.getGlobalIdByECafeSettingValueIndex(index);
    }

    public boolean isOffOnFlag() {
        return offOnFlag;
    }

    public void setOffOnFlag(boolean offOnFlag) {
        this.offOnFlag = offOnFlag;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public int getPorog() {
        return porog;
    }

    public void setPorog(int porog) {
        this.porog = porog;
    }
}

