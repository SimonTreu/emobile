package de.dfki.gs.domain.simulation

import de.dfki.gs.domain.users.Company

class FillingStationType {

    Company company

    String power

    /**
     * filling portion per second, depends on power
     */
    float fillingPortion

    static constraints = {
    }
}
