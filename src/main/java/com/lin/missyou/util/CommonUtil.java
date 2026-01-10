package com.lin.missyou.util;

import com.lin.missyou.bo.PageCounter;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class CommonUtil {
    /**
     * 1.分页工具
     */
    public static PageCounter convertToPageParameter(Integer start, Integer count){
        //需要返回两个数据, Java是面向对象的, 只能用一个类来包装这两个属性
        int pageNum = start/count;//页码
        PageCounter pageCounter = PageCounter.builder()
                .page(pageNum)
                .count(count)
                .build();
        return pageCounter;
    }

    /**
     * 2. 比较时间, 验证是否过期
     */
    public static Boolean isInTimeLine(Date date, Date startDate, Date endDate){
        Long time = date.getTime();
        Long startTime = startDate.getTime();
        Long endTime = endDate.getTime();
        if(time > startTime && time < endTime){
            return true;
        }
        return false;
    }

    /**
     * 3. 时间计算
     */
    public static Calendar addSomeSeconds(Calendar calendar, int seconds){
        calendar.add(Calendar.SECOND, seconds);
        return calendar;
    }

    public static String timestamp10(){
        Long timestamp13 = Calendar.getInstance().getTimeInMillis();
        String timestamp13Str = timestamp13.toString();
        return timestamp13Str.substring(0, timestamp13Str.length() - 3);
    }


    //period 单位：秒
    public static Boolean isOutOfDate(Date startTime, Long period) {
        Long now = Calendar.getInstance().getTimeInMillis();
        Long startTimeStamp = startTime.getTime();
        Long periodMillSecond = period * 1000;
        if (now > (startTimeStamp + periodMillSecond)) {
            return true;
        }
        return false;
    }

    public static Boolean isOutOfDate(Date expiredTime) {
        Long now = Calendar.getInstance().getTimeInMillis();
        Long expiredTimeStamp = expiredTime.getTime();
        if(now > expiredTimeStamp){
            return true;
        }
        return false;
    }

    public static String yuanToFenPlainString(BigDecimal p){
        p = p.multiply(new BigDecimal("100"));
        return CommonUtil.toPlain(p);
    }

    public static String toPlain(BigDecimal p){
        return p.stripTrailingZeros().toPlainString();
    }
}
