package de.dfki.gs.domain

class SocketTimeStatus {

    String name
    String power
    Boolean status
    Date dateCreated
    Date lastUpdated

    static constraints = {

        name ( nullable: false, blank: false )
        power ( nullable: false, blank: false )
        status (nullable: true, blank: true )
    }
}
