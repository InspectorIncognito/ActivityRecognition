package api.activity.activityrecognition.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Hardcoded values used in this app.
 */
public final class Constants {

    private Constants() {
    }

    public static final String TAB = "\t";
    public static final String NEWLINE = "\n";
    public static final String HYPHEN = "-";
    public static final String COMMA = ",";


    public static final String BROADCAST_LOCATION_UPDATE = "UPDATE_LOCATION_CHECK";
    public static final String BROADCAST_ACTIVITY_UPDATE = "UPDATE_ACTIVITY_CHECK";
    //public static final String BROADCAST_ACTIVITY_UPDATE = "UPDATE_ACTIVITY_TEXTVIEW";
    public static final String SECOND_SINGULAR_STRING = "segundo";
    public static final String SECOND_PLURAL_STRING = "segundos";
    public static final String MINUTE_SINGULAR_STRING = "minuto";
    public static final String MINUTE_PLURAL_STRING = "minutos";

    public static final int SEND_TO_SERVER_INTERVAL = 30 * 60 * 1000; //30 minutes in milliseconds

    /* value used by the LocationServices API to indicate the desired request timeout for location
    change updates */
    public static final int LOCATION_REQUESTS_INTERVAL = 5000;

    /* value used by the LocationServices API to indicate the maximum request timeout at which
    this app can handle location change updates*/
    public static final int LOCATION_REQUESTS_FASTEST_INTERVAL = 5000;

    /* approximate speed in meters per second of Transantiago buses,
    converted from an 14 km/hr average speed */
    public static final int AVERAGE_BUS_SPEED = 4;

    /* minimum confidence value for an activity change to be considered the current activity */
    public static final int MIN_CONFIDENCE = 75;

    /* minimum amount of times the same activity has to be measured in a row in order for it to be
     considered the current activity*/
    public static final int MIN_ACTIVITY_REPETITIONS = 2;

    /* minimum amount of times the same speed has to be measured in a row in order for it to be
    considered the current speed */
    public static final int MIN_SPEED_REPETITIONS = 3;

    /* amount of time to wait for an AsyncTask to finish, if required */
    public static final long TASK_WAITING_TIMEOUT = 2000;

    /* minimum amount of time to wait before considering previous activity change/speed
    measurements not valid for the vehicle detection */
    public static final long MAX_DELAY = 30000;

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
