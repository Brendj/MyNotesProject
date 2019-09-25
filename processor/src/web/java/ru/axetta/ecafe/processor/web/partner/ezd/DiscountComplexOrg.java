/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import java.util.LinkedList;
import java.util.List;

public class DiscountComplexOrg {
   private Long idOrg;
   private List<DiscountComplexGroup> groups;

   public DiscountComplexOrg(){
      this.groups  = new LinkedList<DiscountComplexGroup>();
   }

   public List<DiscountComplexGroup> getGroups() {
      return groups;
   }

   public void setGroups(List<DiscountComplexGroup> groups) {
      this.groups = groups;
   }

   public Long getIdOrg() {
      return idOrg;
   }

   public void setIdOrg(Long idOrg) {
      this.idOrg = idOrg;
   }
}
