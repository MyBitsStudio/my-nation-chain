package core.utils;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtilities {

    public static @NotNull String getTimeFormatted() {
        Date getDate = new Date();
        String timeFormat = "M/d/yy hh:mma";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        return "[" + sdf.format(getDate) + "] --- ";
    }

    public static @NotNull String getTimeUnformatted() {
        Date getDate = new Date();
        String timeFormat = "M/d/yy hh:mma";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        return sdf.format(getDate);
    }
}
