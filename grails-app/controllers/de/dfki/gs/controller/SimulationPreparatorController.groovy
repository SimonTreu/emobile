package de.dfki.gs.controller

import de.dfki.gs.controller.commands.AddCarTypeAndRouteCommand
import de.dfki.gs.controller.commands.AddFillingStationCommand
import de.dfki.gs.controller.commands.CreateSimulationCommand
import de.dfki.gs.controller.commands.CreateSimulationOnlyCommand
import de.dfki.gs.controller.commands.DeleteFillingStationCommand
import de.dfki.gs.controller.commands.SelectSimulationCommand
import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Simulation
import de.dfki.gs.domain.SimulationRoute

class SimulationPreparatorController {

    def simulationCollectDataService
    def simulationDataService

    def routeService

    def index() {

        log.error( "params: ${params}" )

        def m = [ : ]
        List<Simulation> simulations = Simulation.findAll()

        m.simulations = simulations;

        if ( params.viewOnly == "true" ) {
            m.viewOnly = true
        }

        render view: 'index', model: m
    }


    def createSimulation() {

        CreateSimulationOnlyCommand cmd = new CreateSimulationOnlyCommand();
        bindData( cmd, params )

        def m = [ : ]

        Simulation simulation = null

        if ( cmd.validate() && !cmd.hasErrors() ) {

            simulation = new Simulation( name: cmd.simulationName )

            if ( !simulation.save( flush: true ) ) {
                log.error( "failed to save simulation ${cmd.simulationName}" )
                redirect( action: 'index' )
            }

            m.simulationId = simulation.id
        }

        if ( simulation != null ) {
            redirect( action: 'editSimulation', params: [ selectedSimulationId: "${simulation.id}" ] )
        } else {
            redirect( action: 'index' )
        }

    }

    def deleteGasolineStations() {

        DeleteFillingStationCommand cmd = new DeleteFillingStationCommand()
        bindData( cmd, params )

        if ( cmd.validate() && !cmd.hasErrors() ) {

            simulationDataService.deleteAllFillingStationsOfSimulation( cmd.simulationId, cmd.fillingType )

        }

        def m = simulationDataService.collectModelForEditSimulation( cmd.simulationId )
        m.cmd = cmd

        render view: 'editSimulation', model: m
    }

    def addFillingStationInstances() {

        AddFillingStationCommand cmd = new AddFillingStationCommand()
        bindData( cmd, params )

        if ( cmd.validate() && !cmd.hasErrors() ) {

            simulationDataService.addNFillingStationInstancesToSimulation( cmd.simulationId, cmd.fillingType, cmd.count )
        }

        def m = simulationDataService.collectModelForEditSimulation( cmd.simulationId )
        m.cmd = cmd

        render view: 'editSimulation', model: m
    }

    def addCarTypeInstances() {

        AddCarTypeAndRouteCommand cmd = new AddCarTypeAndRouteCommand()
        bindData( cmd, params )

        if ( cmd.validate() && !cmd.hasErrors() ) {
            // do the fun stuff
            // TODO: try to do it async?
            // simulationDataService.addNCarAndRoutesToSimulation( cmd.simulationId, cmd.carTypeId, cmd.count, cmd.targets )
            simulationDataService.addNCarAndRoutesWithFixedKmToSimulation( cmd.count, cmd.simulationId, cmd.linearDistance, cmd.carTypeId )

        }

        def m = simulationDataService.collectModelForEditSimulation( cmd.simulationId )
        m.cmd = cmd


        render view: 'editSimulation', model: m

    }

    def editSimulation() {

        SelectSimulationCommand cmd = new SelectSimulationCommand();
        bindData( cmd, params )

        if ( cmd.validate() && !cmd.hasErrors() ) {
            // go ahead

            def m = simulationDataService.collectModelForEditSimulation( cmd.selectedSimulationId )

            render view: 'editSimulation', model: m
        } else {
            redirect( action: 'index' )
        }

    }


}
