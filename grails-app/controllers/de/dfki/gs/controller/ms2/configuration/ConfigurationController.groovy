package de.dfki.gs.controller.ms2.configuration

import de.dfki.gs.controller.ms2.configuration.commands.AddCarsToFleedCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.AddFleetToConfigurationCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.AddGroupToConfigurationCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.AddFillingStationsToGroupCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.CreateGroupForConfigurationCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.CreateGroupForConfigurationViewCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.CreateCarTypeCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.CreateFillingStationTypeCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.CreateFleetForConfigurationCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.CreateFleetForConfigurationViewCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.CreateRouteSelectorCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.EditCarTypeCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.EditConfigurationStubCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.EditFillingStationCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.UpdateCarTypeCommandObject
import de.dfki.gs.controller.ms2.configuration.commands.UpdateFillingStationTypeCommandObject
import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.FillingStationType
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.users.Company
import de.dfki.gs.domain.users.Person
import de.dfki.gs.domain.utils.Distribution
import grails.plugin.springsecurity.SpringSecurityUtils


/**
 * this controller is to handle all user inputs for configurainf simulation configurations
 * intially config-object is persisted as stub and after finished editing persisted as
 * executable
 * a) manage cartypes
 * b) manage cars-stubs
 * c) manage fleets
 * d) manage fillingStations
 * e) manage groups of fillingStations
 *
 */
class ConfigurationController {

    def springSecurityService
    def configurationService




    /**
     * this part handles all about fillingStationType manageing
     *
     *
     */

    /**
     * show the FillingStationTypes available with edit button for each
     * show the add button
     *
     * @return
     */
    def showFillingStationTypes() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        // plug the model..
        def m = [ : ]

        m.fillingStationTypes = [ ]
        List<FillingStationType> fillingStationTypes = configurationService.getFillingStationTypesForCompany( person )
        fillingStationTypes.each { FillingStationType fillingStationType ->

            m.fillingStationTypes << fillingStationType
        }
        m.lightboxlink = g.createLink( controller: 'configuration', action: 'createFillingStationTypeView' )

        render view: "showFillingStationTypes", model: m
    }

    def editFillingStationType() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        UpdateFillingStationTypeCommandObject cmd = new UpdateFillingStationTypeCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to find fillingStationType for id. errors: ${cmd.errors}" )

        } else {

            FillingStationType updatedfillingStationType = configurationService.updateFillingStationTypeForCompany( person, cmd.fillingStationTypeId, cmd.fillingStationTypeName, cmd.power )

        }

        redirect( controller: 'configuration', action: 'showFillingStationTypes' )
    }

    def editFillingStationTypeView() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        EditFillingStationCommandObject cmd = new EditFillingStationCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to find fillingStationType for id. errors: ${cmd.errors}" )

        } else {

            def m = [ : ]

            FillingStationType fillingStationType = FillingStationType.get( cmd.fillingStationTypeId )
            m.fillingStationTypeName = fillingStationType.name
            m.power                  = fillingStationType.power
            m.fillingPortion         = fillingStationType.fillingPortion
            m.fillingStationTypeId   = fillingStationType.id

            render template: '/templates/configuration/fillingstationtype/editFillingStationType', model: m

        }

    }

    def createFillingStationTypeView() {

        render template: '/templates/configuration/fillingstationtype/createFillingStationType'

    }



    /**
     * show the cartypes available with edit button for each
     * show the add button
     *
     * @return
     */
    def showCarTypes() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        // plug the model..
        def m = [ : ]

        m.carTypes = [ ]
        List<CarType> carTypes = configurationService.getCarTypesForCompany( person )
        carTypes.each { CarType carType ->

            def carTypeModel = [ : ]
            carTypeModel.carType = carType
            carTypeModel.lightboxlink = g.createLink( controller: 'configuration', action: 'editCarType', params: [ carTypeId: carType.id ] )

            m.carTypes << carTypeModel
        }
        m.lightboxlink = g.createLink( controller: 'configuration', action: 'createCarTypeView' )
        // m.carTypes = configurationService.getCarTypesForCompany( person )


        render view: "showCarTypes", model: m
    }

    def removeFleetFromConfiguration() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        AddFleetToConfigurationCommandObject cmd = new AddFleetToConfigurationCommandObject()
        bindData( cmd, params )
        if ( !cmd.validate() && cmd.hasErrors() ) {
            log.error( "failed to vaildate AddFleetToConfigurationCommandObject to remove fleet: ${cmd.errors}" )
        } else {

            configurationService.removeFleetFromConfiguration( cmd.configurationStubId, cmd.fleetId )

        }
        redirect( controller: 'configuration', action: 'index', params: [ configurationStubId : params.configurationStubId ] )

    }

    def removeGroupFromConfiguration() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        AddGroupToConfigurationCommandObject cmd = new AddGroupToConfigurationCommandObject()
        bindData( cmd, params )
        if ( !cmd.validate() && cmd.hasErrors() ) {
            log.error( "failed to vaildate AddGroupToConfigurationCommandObject to remove group: ${cmd.errors}" )
        } else {

            configurationService.removeGroupFromConfiguration( cmd.configurationStubId, cmd.groupId )

        }
        redirect( controller: 'configuration', action: 'index', params: [ configurationStubId : params.configurationStubId ] )

    }


    def addExistentFleetToConfiguration() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        AddFleetToConfigurationCommandObject cmd = new AddFleetToConfigurationCommandObject()
        bindData( cmd, params )
        if ( !cmd.validate() && cmd.hasErrors() ) {
            log.error( "failed to vaildate AddFleetToConfigurationCommandObject: ${cmd.errors}" )
        } else {

            configurationService.addFleetToConfiguration( cmd.configurationStubId, cmd.fleetId )

        }


        redirect( controller: 'configuration', action: 'index', params: [ configurationStubId : params.configurationStubId ] )

    }



    def addExistentGroupToConfiguration() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        AddGroupToConfigurationCommandObject cmd = new AddGroupToConfigurationCommandObject()
        bindData( cmd, params )
        if ( !cmd.validate() && cmd.hasErrors() ) {
            log.error( "failed to vaildate AddGroupToConfigurationCommandObject: ${cmd.errors}" )
        } else {

            configurationService.addGroupToConfiguration( cmd.configurationStubId, cmd.groupId )

        }


        redirect( controller: 'configuration', action: 'index', params: [ configurationStubId : params.configurationStubId ] )

    }


    def index() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        EditConfigurationStubCommandObject cmd = new EditConfigurationStubCommandObject()
        bindData( cmd, params )
        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to validate configuration stub: ${cmd.errors}" )

        }

        Long configurationStubId = null

        if ( cmd.configurationStubId == null ) {
            configurationStubId = configurationService.createConfigurationStub( person ).id;
        } else {
            configurationStubId = cmd.configurationStubId
        }



        // plug the model..
        def m = [ : ]

        // to know what we are talking about
        m.configurationStubId = configurationStubId

        // fleets
        m.availableFleets = configurationService.getFleetsForCompany( person, configurationStubId )

        // fleets already added to configuration stub
        m.addedFleets = configurationService.getAddedFleets( configurationStubId )

        // filling station groups
        m.availableFillingStationGroups = configurationService.getGroupsForCompany( person, configurationStubId )

        // groups already added to configuration stub
        m.addedFillingStationGroups = configurationService.getAddedGroups( configurationStubId )

        render view: 'index', model: m
    }


    def editCarType() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        UpdateCarTypeCommandObject cmd = new UpdateCarTypeCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to find carType for id. errors: ${cmd.errors}" )

        } else {

            CarType updatedCarType = configurationService.updateCarTypeForCompany( person, cmd.carTypeId, cmd.carName, cmd.energyDemand, cmd.maxEnergyCapacity)

        }

        redirect( controller: 'configuration', action: 'showCarTypes' )
    }



    def editCarTypeView() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        EditCarTypeCommandObject cmd = new EditCarTypeCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to find carType for id. errors: ${cmd.errors}" )

        } else {

            def m = [ : ]

            CarType carType = CarType.get( cmd.carTypeId )
            m.carTypeName       = carType.name
            m.energyConsumption = carType.energyConsumption
            m.maxEnergyLoad     = carType.maxEnergyLoad
            m.carTypeId         = carType.id

            render template: '/templates/configuration/cartype/editCarType', model: m

        }

    }

    def createFleet() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )


        CreateFleetForConfigurationCommandObject cmd = new CreateFleetForConfigurationCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to vaildate CreateFleetForConfigurationCommandObject: ${cmd.errors}" )

        } else {

            // configurationService.createFleetForCompany(  )
            log.error( "hua!! ${cmd.configurationStubId}" )

        }


        redirect( controller: 'configuration', action: 'index', params: [ configurationStubId : params.configurationStubId ] )
    }

    def createGroup() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )


        CreateGroupForConfigurationCommandObject cmd = new CreateGroupForConfigurationCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to vaildate CreateGroupForConfigurationCommandObject: ${cmd.errors}" )

        } else {

            // configurationService.createFleetForCompany(  )
            log.error( "hua!! ${cmd.configurationStubId}" )

        }


        redirect( controller: 'configuration', action: 'index', params: [ configurationStubId : params.configurationStubId ] )
    }


    def createRouteSelectorView() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        CreateRouteSelectorCommandObject cmd = new CreateRouteSelectorCommandObject()

        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to validate : ${cmd.errors}" )

        } else {

            def m = [ : ]

            // put fleetId
            m.fleetId = cmd.fleetId

            // put in available Distributions
            m.distributions = Distribution.values()


            // put all cars from fleet
            m.cars = configurationService.getCarsFromFleet( cmd.fleetId )


            render template: '/templates/configuration/fleet/distribution', model: m

        }
    }


    def createFleetView() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        CreateFleetForConfigurationViewCommandObject cmd = new CreateFleetForConfigurationViewCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to find configurationStub for id. errors: ${cmd.errors}" )

        } else {

            def m = [ : ]

            Long fleetStubId = configurationService.createFleetStub( person, cmd.configurationStubId )?.id
            m.fleetStubId = fleetStubId

            m.configurationStubId = cmd.configurationStubId
            m.availableCarTypes = configurationService.getCarTypesForCompany( person )
            m.generatedName = Fleet.get( fleetStubId ).name

            render template: '/templates/configuration/fleet/createFleet', model: m

        }

    }

    def createGroupView() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )

        CreateGroupForConfigurationViewCommandObject cmd = new CreateGroupForConfigurationViewCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to find configurationStub for id. errors: ${cmd.errors}" )

        } else {

            def m = [ : ]

            Long groupStubId = configurationService.createGroupStub( person, cmd.configurationStubId )?.id
            m.groupStubId = groupStubId

            m.configurationStubId = cmd.configurationStubId
            m.availableFillingStationTypes = configurationService.getFillingStationTypesForCompany( person )

            m.generatedName = FillingStationGroup.get( groupStubId ).name

            render template: '/templates/configuration/group/createGroup', model: m

        }

    }


    def updateFleetOfConfiguration() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )


        AddCarsToFleedCommandObject cmd = new AddCarsToFleedCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to add cars to fleet: ${cmd.errors}" )

        } else {

            int count = 0
            if ( cmd.carCountList != null && cmd.carCountList.size() > 0 ) {
                count = cmd.carCountList.get( cmd.carCountList.size() - 1 )
            } else {
                count = cmd.carCount
            }

            configurationService.addCarsToFleet( cmd.fleetStubId, count, cmd.carTypeId, cmd.nameForFleet )

        }

        def m = [ : ]
        m.uuid = UUID.randomUUID()
        m.fleetStubId = cmd.fleetStubId
        m.configurationStubId = cmd.configurationStubId
        m.availableCarTypes = configurationService.getCarTypesForCompany( person )

        render template: "/templates/configuration/fleet/anotherCarRow", model: m
    }

    def updateGroupOfConfiguration() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {

            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        log.error( "params: ${params}" )


        AddFillingStationsToGroupCommandObject cmd = new AddFillingStationsToGroupCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to add filling stations to group: ${cmd.errors}" )

        } else {

            int count = 0
            if ( cmd.stationCountList != null && cmd.stationCountList.size() > 0 ) {
                count = cmd.stationCountList.get( cmd.stationCountList.size() - 1 )
            } else {
                count = cmd.stationCount
            }

            configurationService.addStationsToGroup( cmd.groupStubId, count, cmd.stationTypeId, cmd.nameForGroup )

        }

        def m = [ : ]
        m.uuid = UUID.randomUUID()
        m.groupStubId = cmd.groupStubId
        m.configurationStubId = cmd.configurationStubId
        m.availableFillingStationTypes = configurationService.getFillingStationTypesForCompany( person )

        render template: "/templates/configuration/group/anotherFillingStationRow", model: m
    }


    def createCarTypeView() {

        render template: '/templates/configuration/cartype/createCarType'

    }

    def createFillingStationType() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        def m = [ : ]

        CreateFillingStationTypeCommandObject cmd = new CreateFillingStationTypeCommandObject()
        bindData( cmd, params )

        FillingStationType fillingStationType = null
        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to validate user input for creating fillingStationType: ${cmd.errors}" )
            m.errors = cmd.errors
            m.cmd = cmd

        } else {

            fillingStationType = configurationService.createFillingStationTypeForCompany( person, cmd.fillingStationTypeName, cmd.power )
            m.fillingStationType = fillingStationType


        }

        redirect( controller: 'configuration', action: 'showFillingStationTypes' )

    }

    /**
     * creates a carType for a company
     */
    def createCarType() {

        log.error( "params: ${params}" )

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        def m = [ : ]

        CreateCarTypeCommandObject cmd = new CreateCarTypeCommandObject()
        bindData( cmd, params )

        CarType carType = null
        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "failed to validate user input for creating carType: ${cmd.errors}" )
            m.errors = cmd.errors
            m.cmd = cmd

        } else {

            carType = configurationService.createCarTypeForCompany( person, cmd.carName, cmd.energyDemand, cmd.maxEnergyCapacity )
            m.carType = carType


        }

        redirect( controller: 'configuration', action: 'showCarTypes' )
    }

    /**
     * list available carTypes for company
     */
    def listAvailableCarType() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        def m = [ : ]

        List<CarType> carTypes = configurationService.getCarTypesForCompany( person )

        m.carTypes = carTypes

        // TODO: render
    }


    def removeStubConfiguration() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        CreateFleetForConfigurationViewCommandObject cmd = new CreateFleetForConfigurationViewCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {
            log.error( "nothing to remove, no coniguration stub found for ${cmd.configurationStubId} : ${cmd.errors}" )
        } else {

            configurationService.removeConfigurationStub( cmd.configurationStubId )

        }


        redirect controller: 'front', action: 'init'
    }

    def saveFinishedConfiguration() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        CreateFleetForConfigurationViewCommandObject cmd = new CreateFleetForConfigurationViewCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {
            log.error( "nothing to save, no coniguration stub found for ${cmd.configurationStubId} : ${cmd.errors}" )
        } else {

            configurationService.saveFinishedConfigurationStub( cmd.configurationStubId )

        }

        redirect controller: 'front', action: 'init'
    }

    def showRecentlyEditedConfiguration() {

        Person person = (Person) springSecurityService.currentUser

        if ( !person ) {
            redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl
            return
        }

        def m = [ : ]
        m.configurations = [  ]

        List<Configuration> configurations = configurationService.getRecentlyEditedConfigurationsOfCompany( person )

        configurations.each { Configuration configuration ->

            def conf = [ : ]
            conf.configurationId = configuration.id


            m.configurations << conf
        }

        render view: 'recentConfigurations', model: m
    }

    def checkPerson() {

    }


}
