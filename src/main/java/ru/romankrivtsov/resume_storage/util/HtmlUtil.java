package ru.romankrivtsov.resume_storage.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.romankrivtsov.resume_storage.model.Organization;

public class HtmlUtil {
    private static final Logger log = LoggerFactory.getLogger(HtmlUtil.class);

    public static boolean isEmpty(String str) {
        boolean result = str == null || str.trim().isEmpty();
        log.debug("Checked if string is empty: '{}', result: {}", str, result);
        return result;
    }

    public static String formatDates(Organization.Position position) {
        String formatted = DateUtil.format(position.getStartDate()) + " - " + DateUtil.format(position.getEndDate());
        log.debug("Formatted dates for position '{}': {}", position.getTitle(), formatted);
        return formatted;
    }
}
