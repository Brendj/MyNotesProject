/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import java.util.LinkedList;
import java.util.List;

public class DiscountComplexGroup {
   private String groupName;
   private List<DiscountComplexItem> days;

   public DiscountComplexGroup(){
      this.days  = new LinkedList<DiscountComplexItem>();
   }

   public List<DiscountComplexItem> getDays() {
      return days;
   }

   public void setDays(List<DiscountComplexItem> days) {
      this.days = days;
   }

   public String getGroupName() {
      return groupName;
   }

   public void setGroupName(String groupName) {
      this.groupName = groupName;
   }
}
