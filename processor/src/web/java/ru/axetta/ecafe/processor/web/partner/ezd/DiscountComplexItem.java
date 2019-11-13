/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import java.util.LinkedList;
import java.util.List;

public class DiscountComplexItem {
   private String date;
   private List<ComplexesItem> complexeslist;

   public DiscountComplexItem(){
      this.complexeslist  = new LinkedList<ComplexesItem>();
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public List<ComplexesItem> getComplexeslist() {
      return complexeslist;
   }

   public void setComplexeslist(List<ComplexesItem> complexeslist) {
      this.complexeslist = complexeslist;
   }
}
