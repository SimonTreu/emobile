package de.dfki.gs.controller.commands

import grails.validation.Validateable

/**
 * Created by anbu02 on 23.05.14.
 */
@Validateable
class CheckDateCommand {

    Integer fromDate_minute
    Integer fromDate_hour
    Integer fromDate_day
    Integer fromDate_month
    Integer fromDate_year

    Integer toDate_minute
    Integer toDate_hour
    Integer toDate_day
    Integer toDate_month
    Integer toDate_year

    static constraints = {
        fromDate_minute( nullable: false, inList:   0..59  )
        fromDate_hour ( nullable: false, inList:    0..23  )
        fromDate_day ( nullable: false, inList:     0..31 )
        fromDate_month ( nullable: false, inList:   0..11 )
        fromDate_year ( nullable: false, inList:    2014..2016 )
        toDate_minute ( nullable: false, inList:    0..59  )
        toDate_hour ( nullable: false, inList:      0..23 )
        toDate_day ( nullable: false, inList:       0..31 )
        toDate_month ( nullable: false, inList:     0..23 )
        toDate_year ( nullable: false, inList:      2014..2016 )
    }


}
