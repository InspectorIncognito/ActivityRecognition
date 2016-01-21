package api.activity.activityrecognition.utils;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;

import api.activity.activityrecognition.R;

/**
 * Created by brahim on 11-01-16.
 */
public class Functions {
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }

    public static String getActivityStringInSpanish(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle_ES);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle_ES);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot_ES);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running_ES);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still_ES);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting_ES);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown_ES);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking_ES);
            default:
                return resources.getString(R.string.unidentifiable_activity_ES, detectedActivityType);
        }
    }

    public static int getActivityCode(Context context, String detectedActivityType) {
        Resources resources = context.getResources();
        if(detectedActivityType.equals(resources.getString(R.string.in_vehicle)))
            return DetectedActivity.IN_VEHICLE;
        if(detectedActivityType.equals(resources.getString(R.string.on_bicycle)))
            return DetectedActivity.ON_BICYCLE;
        if(detectedActivityType.equals(resources.getString(R.string.on_foot)))
            return DetectedActivity.ON_FOOT;
        if(detectedActivityType.equals(resources.getString(R.string.running)))
            return DetectedActivity.RUNNING;
        if(detectedActivityType.equals(resources.getString(R.string.still)))
            return DetectedActivity.STILL;
        if(detectedActivityType.equals(resources.getString(R.string.tilting)))
            return DetectedActivity.TILTING;
        if(detectedActivityType.equals(resources.getString(R.string.unknown)))
            return DetectedActivity.UNKNOWN;
        if(detectedActivityType.equals(resources.getString(R.string.walking)))
            return DetectedActivity.WALKING;

        return -1;
    }

    public static double getVincentyDistance(double lat1, double lon1, double lat2, double lon2){
        double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563;
        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
        double cosSqAlpha;
        double sinSigma;
        double cos2SigmaM;
        double cosSigma;
        double sigma;

        double lambda = L, lambdaP, iterLimit = 100;
        do {
            double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt(	(cosU2 * sinLambda)
                            * (cosU2 * sinLambda)
                            + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                            * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
            );
            if (sinSigma == 0) {
                return 0;
            }

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = 	L + (1 - C) * f * sinAlpha
                    * 	(sigma + C * sinSigma
                    * 	(cos2SigmaM + C * cosSigma
                    * 	(-1 + 2 * cos2SigmaM * cos2SigmaM)
            )
            );

        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0) {
            return 0;
        }

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384
                * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma =
                B * sinSigma
                        * (cos2SigmaM + B / 4
                        * (cosSigma
                        * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                        * (-3 + 4 * sinSigma * sinSigma)
                        * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

        double s = b * A * (sigma - deltaSigma);

        return s;
    }

    static final double _eQuatorialEarthRadius = 6378.1370D;
    static final double _d2r = (Math.PI / 180D);

    public static int haversineInM(double lat1, double long1, double lat2, double long2) {
        return (int) (1000D * haversineInKM(lat1, long1, lat2, long2));
    }

    public static double haversineInKM(double lat1, double long1, double lat2, double long2) {
        double dlong = (long2 - long1) * _d2r;
        double dlat = (lat2 - lat1) * _d2r;
        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * _d2r) * Math.cos(lat2 * _d2r)
                * Math.pow(Math.sin(dlong / 2D), 2D);
        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
        double d = _eQuatorialEarthRadius * c;

        return d;
    }
}
