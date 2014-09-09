package de.dfki.gs.jobs.carsharing

import de.dfki.gs.service.datafetching.MulticityFetcherService
/**
 * Created by anbu02 on 02.09.14.
 */
class MulticityJob {

    def multicityFetcherService

    static triggers = {
        simple name:'MulticityJobTest', startDelay:0,repeatInterval: ( 5 * 60 * 1000 )
    }
    def group = "MulticityGroup"

    def execute() {

        multicityFetcherService.fetchData( "https://kunden.multicity-carsharing.de/kundenbuchung/hal2ajax_process.php?zoom=10&centerLng=13.382322739257802&centerLat=52.50734843957503&searchmode=buchanfrage&with_staedte=true&buchungsanfrage=N&lat=52.50734843957503&lng=13.382322739257802&instant_access=J&open_end=J&objectname=multicitymarker&ignore_virtual_stations=J&ajxmod=hal2map&callee=getMarker" )

        println "MulticityNow job finished at ${new Date()}"
    }
}
