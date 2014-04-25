package de.dfki.gs.utils;

/**
 * @author: glenn
 * @since: 08.01.14
 */
public class Calculater {

    public static final double R = 6372.8; // In kilometers

    /**
     *
     *
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return distnace in [ km ]
     */
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

        // berlin: lon: 13, lat: 52
        double hua = Calculater.haversine(  13.2698116 ,52.6051089,  13.273795, 52.5999223);

        // 1, 0, 0.00915836, 52.4792848, 13.6184218, 22079, 0.2747508, 30, 1, Hegelstraße, 52.4773083, 13.6159872, start

        double hua2 = Calculater.haversine( 13.6184218 ,52.4792848 ,13.6159872 , 52.4773083);

        System.err.println( hua2 );
    }

}
