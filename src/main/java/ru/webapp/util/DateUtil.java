package ru.webapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

    public static final LocalDate NOW = LocalDate.of(3000, 1, 1);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/yyyy");

    public static LocalDate of(int year, Month month) {
        log.debug("Creating LocalDate with year={}, month={}", year, month);
        return LocalDate.of(year, month, 1);
    }

    public static String format(LocalDate date) {
        if (date == null) {
            log.warn("Attempted to format a null date");
            return "";
        }
        String formatted = date.equals(NOW) ? "Сейчас" : date.format(DATE_FORMATTER);
        log.debug("Formatted date {} to '{}'", date, formatted);
        return formatted;
    }

    public static LocalDate parse(String date) {
        if (HtmlUtil.isEmpty(date) || "Сейчас".equals(date)) {
            log.debug("Parsing 'Сейчас' or empty string as NOW");
            return NOW;
        }
        YearMonth yearMonth = YearMonth.parse(date, DATE_FORMATTER);
        LocalDate result = LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
        log.debug("Parsed date string '{}' to LocalDate {}", date, result);
        return result;
    }
}
