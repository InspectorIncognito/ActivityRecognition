package api.activity.activityrecognition.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {
    }

    public static final String TAB = "\t";
    public static final String HYPHEN = "-";
    public static final String COMMA = ",";

    public static final String BROADCAST_ACTIVITY_UPDATE = "UPDATE_ACTIVITY_TEXTVIEW";
    public static final String SECOND_SINGULAR_STRING = "segundo";
    public static final String SECOND_PLURAL_STRING = "segundos";
    public static final String MINUTE_SINGULAR_STRING = "minuto";
    public static final String MINUTE_PLURAL_STRING = "minutos";

    public static final int SEND_TO_SERVER_INTERVAL = 30 * 60 * 1000; //30 minutes in milliseconds
    public static final int LOCATION_REQUESTS_INTERVAL = 10000;
    public static final int LOCATION_REQUESTS_FASTEST_INTERVAL = 5000;

    public static final ArrayList<Integer> MEASUREMENT_INTERVALS_MILLIS = new ArrayList<>(Arrays.asList(
            0,
            1000,
            5000,
            10000,
            15000,
            30000,
            60000,
            90000
    ));
}
