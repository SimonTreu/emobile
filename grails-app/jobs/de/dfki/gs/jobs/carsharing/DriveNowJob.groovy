package de.dfki.gs.jobs.carsharing

/**
 * Created by anbu02 on 02.09.14.
 */
class DriveNowJob {

    def driveNowFetcherService

    static triggers = {
        simple name:'DriveNowJobTest', startDelay:0,repeatInterval: ( 1 * 60 * 1000 )
    }
    def group = "DriveNowGroup"

    def execute() {

        driveNowFetcherService.fetchData( "https://www.drive-now.com/php/metropolis/json.vehicle_filter?cit=6099" )

        println "DriveNow job finished at ${new Date()}"
    }

}
