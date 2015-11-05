
package com.my.diff;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.diffkit.common.annot.NotThreadSafe;
import org.diffkit.diff.engine.DKColumnDiff;
import org.diffkit.diff.engine.DKColumnDiffRow;
import org.diffkit.diff.engine.DKContext;
import org.diffkit.diff.engine.DKDiff;
import org.diffkit.diff.engine.DKRowDiff;
import org.diffkit.diff.engine.DKSide;
import org.diffkit.diff.sns.DKDiffFormatter;


@NotThreadSafe
public class MyDKFormatter implements DKDiffFormatter {

   private static final MyDKFormatter INSTANCE = new MyDKFormatter();
   private DKColumnDiffRow _runningRow;

   public static MyDKFormatter getInstance() {
      return INSTANCE;
   }

   public String format(DKDiff diff_, DKContext context_) {
      if (diff_ == null)
         return null;

      switch (diff_.getKind()) {
      case ROW_DIFF:
         return this.formatRowDiff((DKRowDiff) diff_, context_);
      case COLUMN_DIFF:
         return this.formatColumnDiff((DKColumnDiff) diff_, context_);

      default:
         throw new IllegalArgumentException(String.format("unrecognized kind->%s",
            diff_.getKind()));
      }
   }

   private String formatRowDiff(DKRowDiff diff_, DKContext context_) {
      StringBuilder builder = new StringBuilder();
      DKSide side = diff_.getSide();
      builder.append((side == DKSide.LEFT ? "L" : "R"));
      builder.append(getDisplayValue(diff_.getRowDisplayValues()) + " ");
      builder.append("\n");
      return builder.toString();
   }
   
   private String getDisplayValue(OrderedMap values) {
	   OrderedMap newMap = new ListOrderedMap();
	   MapIterator itr = values.mapIterator();
	   while(itr.hasNext()) {
		   Object key = itr.next();
		   Object value = itr.getValue();
		   newMap.put(key, value + "##!");
	   }

	   return newMap.toString();
   }

   private String formatColumnDiff(DKColumnDiff diff_, DKContext context_) {
      StringBuilder builder = new StringBuilder();
      DKColumnDiffRow row = diff_.getRow();
      if (row != _runningRow) {
         _runningRow = row;
         builder.append("D" + getDisplayValue(row.getRowDisplayValues()) + "\n");
      }
      /*builder.append(diff_.getColumnName() + " => ");
      builder.append(diff_.getLhs() + " | ");
      builder.append(diff_.getRhs() + "");*/
      return builder.toString();
   }
}
