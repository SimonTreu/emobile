package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company

class FillingStationType {

    Company company

    String name

    String power

    /**
     * filling portion per second, depends on power
     */
    float fillingPortion

    static constraints = {
    }

    def beforeUpdate() {

        if ( fillingPortion == null ) {

            try {
                fillingPortion = ( 1 / 3600 ) * Float.parseFloat( power )
            } catch ( NumberFormatException nfe ) {
                log.error( "failed to calc fillingPortion during Float parsing from String: ${power}", nfe )
            }

        }

    }

    def beforeSave() {

        if ( fillingPortion == null ) {

            try {
                fillingPortion = ( 1 / 3600 ) * Float.parseFloat( power )
            } catch ( NumberFormatException nfe ) {
                log.error( "failed to calc fillingPortion during Float parsing from String: ${power}", nfe )
            }

        }

    }

}
