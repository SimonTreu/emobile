package de.dfki.gs.jobs.carsharing

/**
 * Created by anbu02 on 02.09.14.
 */
class Car2GoJob {

    def car2GoFetcherService

    static triggers = {
        simple name:'Car2GoJobTest', startDelay:0,repeatInterval: ( 5 * 60 * 1000 )
    }
    def group = "Car2GoGroup"

    def execute() {

        car2GoFetcherService.fetchData( "https://www.car2go.com/api/v2.1/vehicles?loc=berlin&oauth_consumer_key=eFahrung&format=json" )

        println "Car2Go job finished at ${new Date()}"
    }


}
