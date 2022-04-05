package ru.axetta.ecafe.processor.core.card;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CardBlockPeriodConfig {
    public static final String PARAM_BASE = "ecafe.processor.card.registration.block.period";
    private final List<BlockPeriodCardTypes> list;
    private static final Logger logger = LoggerFactory.getLogger(CardBlockPeriodConfig.class);

    /*
    Параметры в конфигурации:
    ecafe.processor.card.registration.block.period.0=0,1,2,4,5,6:90 (сначала типы карт, после двоеточия количество дней)
    ecafe.processor.card.registration.block.period.1=3,7,8,9,10,11:180
    * */
    public CardBlockPeriodConfig(Properties properties) {
        this.list = new ArrayList<>();
        try {
            for (int n = 0; ; ++n) {
                String nameParam = PARAM_BASE + "." + n;
                if (!properties.containsKey(nameParam)) break;
                String param = properties.getProperty(nameParam);
                String[] arr = param.split(":");
                BlockPeriodCardTypes blockPeriodCardTypes = new BlockPeriodCardTypes()
                        .cardTypes(arr[0])
                        .period(arr[1]);
                list.add(blockPeriodCardTypes);
            }
        } catch (Exception e) {
            logger.error("Error in init CardBlockPeriodConfig: ", e);
        }
    }

    public List<BlockPeriodCardTypes> getList() {
        return list;
    }

    public static class BlockPeriodCardTypes {
        private List<Integer> cardTypes;
        private Integer period;

        public BlockPeriodCardTypes cardTypes(String cardTypes) {
            String[] arr = cardTypes.split(",");
            List<Integer> list = new ArrayList<>();
            for (String s : arr) {
                list.add(Integer.valueOf(s));
            }
            this.cardTypes = list;
            return this;
        }

        public BlockPeriodCardTypes period(String period) {
            this.period = Integer.valueOf(period);
            return this;
        }

        public List<Integer> getCardTypes() {
            return cardTypes;
        }

        public Integer getPeriod() {
            return period;
        }
    }
}
