package de.dfki.gs.utils

/**
 * Created by glenn on 27.05.14.
 */
class TimeCalculator {

    public static String readableTime( BigDecimal timeSeconds, boolean showSeconds = true ) {

        String res = "00:00:00";

        if ( timeSeconds > 0 ) {

            def seconds = timeSeconds as BigInteger

            def secD = ( seconds.remainder( 60g ) ) as BigInteger
            seconds = seconds - secD
            def minutes = ( seconds / 60 ) as BigInteger
            def minD = minutes.remainder( 60g )
            minutes = minutes - minD
            def hours = ( minutes / 60 ) as BigInteger
            def hoursD = hours.remainder( 60g )

            def secDisplay = secD<10?"0${secD}":secD
            def minDisplay = minD<10?"0${minD}":minD

            res = "${hoursD}:${minDisplay}:${secDisplay}"

        }

        if ( !showSeconds ) {
            res = res.substring( 0, res.length() - 3 )
        }

        return res
    }


}
