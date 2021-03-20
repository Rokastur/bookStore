package com.dematic.bookStore.services;

import java.util.Comparator;

public class TotalPriceComparator implements Comparator<String> {

    @Override
    public int compare(String str1, String str2) {

        String toCompare1 = str1.substring(str1.lastIndexOf('/')+1);
        String toCompare2 = str2.substring(str2.lastIndexOf('/')+1);

        return Double.valueOf(toCompare1).compareTo(Double.valueOf(toCompare2));
    }


}
