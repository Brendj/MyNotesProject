package ru.axetta.ecafe.processor.web.ui.guardianservice;

import java.util.Arrays;
import java.util.List;

public class CGGroupItem implements Comparable {
    private Long idOfClientGroup;
    public static final List<Long> GROUPS = Arrays.asList(1100000000L, 1100000010L, 1100000050L, 1100000020L,
            1100000030L, 1100000100L, 1100000110L);
    //Предопределённые группы в порядке убывания приоритета: 1 - пед. состав, 2 - администрация, 3 - другое, 4 - тех. персонал,
    // 5 - родители, 6 - родители других ОО, 7 - сотрудники других ОО

    public CGGroupItem(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof CGGroupItem)) {
            return 1;
        }
        CGGroupItem item = (CGGroupItem) o;
        int indexThis = GROUPS.indexOf(this.idOfClientGroup);
        int indexItem = GROUPS.indexOf(item.getIdOfClientGroup());
        if ((indexThis == -1) && (indexItem == -1)) return 0;
        if ((indexThis == -1) && (indexItem > -1)) return -1;
        if ((indexThis > -1) && (indexItem == -1)) return 1;

        return Integer.valueOf(indexThis).compareTo(indexItem);
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }
}
