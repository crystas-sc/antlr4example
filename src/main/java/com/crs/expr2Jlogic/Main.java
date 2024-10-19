package com.crs.expr2Jlogic;


/**
 * @author Crystas
 */
class Main {
   public static void main(String[] args) {
      System.out.println(AntlrGenericExpressionUtil.convertToJSONLogic("age > 15 and maxQty < min(473,allowedQty) and city in (\"Paris\",\"London\") "));

   }
}