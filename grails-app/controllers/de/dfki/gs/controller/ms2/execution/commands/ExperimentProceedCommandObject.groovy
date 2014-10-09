package de.dfki.gs.controller.ms2.execution.commands

import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.stats.ExperimentRunResult
import grails.validation.Validateable

/**
 * Created by glenn on 09.10.14.
 */
@Validateable
class ExperimentProceedCommandObject {

    Long configurationId
    Integer relativeSearchLimit

    Long experimentRunResultId

    static constraints = {

        configurationId(nullable: true, validator: { val, obj ->
            if (val && !Configuration.get(val)) return 'de.dfki.gs.controller.commands.SimulationCommand.not.exist'
        })

        relativeSearchLimit(nullable: false, min: 0, max: 100)


        experimentRunResultId(nullable: false, validator: { val, obj ->

            ExperimentRunResult result = ExperimentRunResult.get(val)

            if (result == null) {
                // TODO: check entry
                return 'de.dfki.gs.controller.commands.SimulationCommand.not.exist'
            }

        })

    }

}