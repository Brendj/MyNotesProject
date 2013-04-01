package ru.axetta.ecafe.processor.core.daoservices.client.items;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 27.03.13
 * Time: 13:26
 * To change this template use File | Settings | File Templates.
 */
public class DayInfo {

    private Long priceType0;
    private Long priceType1;

    public void addDayInfo(DayInfo dayInfo){
        this.priceType0 +=dayInfo.getPriceType0();
        if(this.priceType1<0 && dayInfo.getRealPriceType1()>=0){
            this.priceType1 =dayInfo.getRealPriceType1();
        } else if(dayInfo.getRealPriceType1()>=0){
            this.priceType1 += dayInfo.getRealPriceType1();
        }
    }

    public DayInfo() {
        this.priceType0 = 0L;
        this.priceType1 = -1L;
    }

    public Long getPriceType0() {
        return priceType0;
    }

    public void setPriceType0(Long priceType0) {
        this.priceType0 = priceType0;
    }

    public Long getRealPriceType1(){
        return priceType1;
    }

    public Long getPriceType1() {
        return (priceType1<0?0:priceType1);
    }

    public void setPriceType1(Long priceType1) {
        this.priceType1 = priceType1;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{priceType0=").append(priceType0);
        sb.append(", priceType1=").append(getPriceType1());
        sb.append('}');
        return sb.toString();
    }
}
