package de.dfki.gs.utils;

/**
 * @author: glenn
 * @since: 08.01.14
 */
public class Calculater {

    public static final double R = 6372.8; // In kilometers

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));

        return R * c;
    }


    public static void main( String [] argv ) {

        double hua = Calculater.haversine( 13.34434, 52.52513 , 13.34393, 52.52408 );

        System.err.println( hua );
    }

}
