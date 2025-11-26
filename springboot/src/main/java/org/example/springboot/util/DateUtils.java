package org.example.springboot.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    /**
     * 计算上个月的第一天。
     * @return 上个月第一天的LocalDate对象。
     */
    public static LocalDate getLastMonthFirstDay() {
        LocalDate today = LocalDate.now(); // 获取当前日期
        int currentMonth = today.getMonthValue(); // 获取当前月份
        int currentYear = today.getYear(); // 获取当前年份

        // 如果当前月份是1月，则上个月是去年的12月
        if (currentMonth == 1) {
            return LocalDate.of(currentYear - 1, 12, 1);
        } else {
            // 否则，上个月的第一天就是当前年加上上个月的月份减1，日期为1
            return LocalDate.of(currentYear, currentMonth - 1, 1);
        }
    }

    /**
     * 格式化LocalDateTime为字符串
     * @param dateTime 要格式化的LocalDateTime对象
     * @return 格式化后的字符串，格式为 yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    /**
     * 格式化LocalDate为字符串
     * @param date 要格式化的LocalDate对象
     * @return 格式化后的字符串，格式为 yyyy-MM-dd
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}
