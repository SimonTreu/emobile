package de.dfki.gs.domain.utils

/**
 * Created by glenn on 11.07.14.
 */
public enum Distribution {

    NORMAL_DISTRIBUTION( "Normal Distribution" ),
    EQUAL_DISTRIBUTION( "Equal Distribution" ),
    SELF_MADE_ROUTES( "Self Configured Routes" ),
    NOT_ASSIGNED( "Not Assigned" )

    final String value

    Distribution(String value) {
        this.value = value
    }

    String toString() {
        value
    }

    String getKey() {
        name()
    }

}