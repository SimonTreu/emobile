package de.dfki.gs.controller.ms2.stats

import de.dfki.gs.controller.ms2.stats.commands.ExperimentResultCommandObject

class StatisticsController {

    def statisticService

    /**
     * TODO: after sim run, javascript never get back to controller,
     * also, no chance to get simResultId.
     * better to show results on config side. !!
     *
     * @return
     */
    def showStats() {

        ExperimentResultCommandObject cmd = new ExperimentResultCommandObject();
        bindData( cmd, params )

        def m = [ : ]

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "sth deeply went wrong: ${cmd.errors}" )

        }

        def stats = statisticService.generateStatisticMapForExperiment( cmd.experimentRunResultId )

        m.stats = stats

        render view: 'showStats', model: m

    }

    def showPicture() {

        log.error( "params: ${params}" )

    }
}
