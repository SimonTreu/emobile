package de.dfki.gs.jobs.stats



class RWEJob {

    def rweFetcherService

    static triggers = {
        simple name:'RWEJobTest', startDelay:0,repeatInterval: ( 15 * 60 * 1000 )
    }
    def group = "RWEGroup"

    def execute() {

        rweFetcherService.fetchData( "http://www.rwe-mobility.com/emobilityfrontend/rs/chargingstations" )

        println "Rwe job finished at ${new Date()}"
    }
}
