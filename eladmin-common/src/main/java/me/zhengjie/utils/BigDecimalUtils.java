package me.zhengjie.utils;

import java.math.BigDecimal;

public class BigDecimalUtils {
    public static final BigDecimal ONEHUNDRED = BigDecimal.TEN.multiply(BigDecimal.TEN);
    public static final BigDecimal ONETHOUSAND = BigDecimal.TEN.multiply(ONEHUNDRED);

    public static void main(String[] args) {
        System.out.println(gt(new BigDecimal("1"),new BigDecimal("2")));
        System.out.println(le(new BigDecimal("2"),new BigDecimal("2")));
        System.out.println(eq(new BigDecimal("2"),new BigDecimal("2")));
    }
    public static boolean gt(BigDecimal bigDecimal1,BigDecimal bigDecimal2){
        if (bigDecimal1==null || bigDecimal2==null)
            return false;
        return bigDecimal1.compareTo(bigDecimal2)==1;
    }

    public static boolean lt(BigDecimal bigDecimal1,BigDecimal bigDecimal2){
        if (bigDecimal1==null || bigDecimal2==null)
            return false;
        return bigDecimal1.compareTo(bigDecimal2)==-1;
    }

    public static boolean eq(BigDecimal bigDecimal1,BigDecimal bigDecimal2){
        if (bigDecimal1==null || bigDecimal2==null)
            return false;
        return bigDecimal1.compareTo(bigDecimal2)==0;
    }

    public static boolean ge(BigDecimal bigDecimal1,BigDecimal bigDecimal2){
        if (bigDecimal1==null || bigDecimal2==null)
            return false;
        return gt(bigDecimal1,bigDecimal2)||eq(bigDecimal1,bigDecimal2);
    }

    public static boolean le(BigDecimal bigDecimal1,BigDecimal bigDecimal2){
        if (bigDecimal1==null || bigDecimal2==null)
            return false;
        return lt(bigDecimal1,bigDecimal2)||eq(bigDecimal1,bigDecimal2);
    }

    public static boolean nq(BigDecimal bigDecimal1,BigDecimal bigDecimal2){
        return !eq(bigDecimal1,bigDecimal2);
    }
}
