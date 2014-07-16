package de.dfki.gs.jobs.stats

class VattenfallJob {

    def vattenfallFetcherService

    static triggers = {
        simple name:'VattenfallJobTest', startDelay:1000,repeatInterval: ( 15 *  60 * 1000 )
    }
    def group = "VattenfallGroup"

    def execute() {

        vattenfallFetcherService.fetchData("http://ladestationen.cosmotools.de/google_map/js/locations_ber.js", "http://ladestationen.cosmotools.de/google_map/status.php" )

        println "Vattenfall job finished at ${new Date()}"
    }
}
