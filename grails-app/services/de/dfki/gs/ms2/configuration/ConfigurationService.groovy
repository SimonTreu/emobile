package de.dfki.gs.ms2.configuration

import de.dfki.gs.domain.simulation.Car
import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.FillingStation
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.FillingStationType
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.simulation.Route
import de.dfki.gs.domain.simulation.Simulation
import de.dfki.gs.domain.simulation.TrackEdge
import de.dfki.gs.domain.users.Company
import de.dfki.gs.domain.users.Person
import de.dfki.gs.domain.utils.Distribution
import de.dfki.gs.domain.utils.FleetStatus
import de.dfki.gs.domain.utils.GroupStatus
import grails.transaction.Transactional

@Transactional
class ConfigurationService {

    def routeService
    def statisticService

    /**
     * configuration can have some fleets
     * this method is used to remove one fleet from configuration
     *
     * @param configurationStubId
     * @param fleetId
     * @return
     */
    def removeFleetFromConfiguration( Long configurationStubId, Long fleetId ) {

        Configuration configuration = Configuration.get( configurationStubId )
        Fleet fleet = Fleet.get( fleetId )

        configuration.removeFromFleets( fleet )

        if ( !configuration.save( flush: true ) ) {

            log.error( "failed to remove fleet from configuarionStub: ${configuration.errors}" )

        }

        return
    }

    /**
     * configuration can have some electric groups
     * this method is used to remove one group from configuration
     *
     * @param configurationStubId
     * @param groupId
     * @return
     */
    def removeGroupFromConfiguration( Long configurationStubId, Long groupId ) {

        Configuration configuration = Configuration.get( configurationStubId )
        FillingStationGroup group = FillingStationGroup.get( groupId )

        configuration.removeFromFillingStationGroups( group )

        if ( !configuration.save( flush: true ) ) {

            log.error( "failed to remove group from configuarionStub: ${configuration.errors}" )

        }

        return
    }

    /**
     * configuration can have some fleets
     * this method is used to add one fleet to configuration
     *
     * @param configurationStubId
     * @param fleetId
     * @return
     */
    def addFleetToConfiguration( Long configurationStubId , Long fleetId) {

        Configuration configuration = Configuration.get( configurationStubId )
        Fleet fleet = Fleet.get( fleetId )

        configuration.addToFleets( fleet )

        if ( !configuration.save( flush: true ) ) {

            log.error( "failed to add fleet to configuarionStub: ${configuration.errors}" )

        }

        return
    }


    /**
     * configuration can have some electric groups
     * this method is used to add one group to configuration
     *
     * @param configurationStubId
     * @param groupId
     * @return
     */
    def addGroupToConfiguration( Long configurationStubId , Long groupId) {

        Configuration configuration = Configuration.get( configurationStubId )
        FillingStationGroup group = FillingStationGroup.get( groupId )

        configuration.addToFillingStationGroups( group )

        if ( !configuration.save( flush: true ) ) {

            log.error( "failed to add group to configuarionStub: ${configuration.errors}" )

        }

        return
    }


    /**
     * this method creates a configuration stub and persist it to db
     *
     *
     * @param person to find his company, which is then the owner of the configuration
     * @return
     */
    def createConfigurationStub( Person person ) {

        // getting the company
        Company company = Company.get( person.company.id )

        if ( !company ) {
            log.error( "no company found for user: ${person.username}" )
            return
        }

        Configuration configurationStub = new Configuration(
            company: company,
            stub: true
        )

        if ( !configurationStub.save( flush: true ) ) {
            log.error( "failed to save configurationStub: ${configurationStub.errors}" )
        }


        return configurationStub
    }

    /**
     * get all fleets added to a certain configuration stub
     *
     * @param configurationStubId
     * @return
     */
    def getAddedFleets( Long configurationStubId ) {

        Configuration stub = Configuration.get( configurationStubId )

        List<Fleet> addedFleets = new ArrayList<Fleet>()
        stub.fleets.each { Fleet fleet ->

            addedFleets.add( Fleet.get( fleet.id ) )

        }

        return addedFleets
    }

    /**
     * get all groups added to a certain configuration stub
     *
     * @param configurationStubId
     * @return
     */
    def getAddedGroups( Long configurationStubId ) {

        Configuration stub = Configuration.get( configurationStubId )

        List<FillingStationGroup> addedGroups = new ArrayList<FillingStationGroup>()
        stub.fillingStationGroups.each { FillingStationGroup group ->

            addedGroups.add( FillingStationGroup.get( group.id ) )

        }
        log.error("addedGroups---${addedGroups}")
        return addedGroups
    }

    /**
     * collect all available fillingStationGroups for currentUser's company
     *
     *
     * @param currentUser
     * @return
     */
    def getFillingStationGroupsForCompany( Person currentUser ) {

        // getting the company
        Company company = Company.get( currentUser.company.id )

        if ( !company ) {
            log.error( "no company found for user: ${currentUser.username}" )
            return
        }

        List<FillingStationGroup> fillingStationGroups = FillingStationGroup.findAllByCompany( company )

        return fillingStationGroups
    }

    /**
     * collect all available fleets for currentUser's company
     * remove those fleets which are added to configuration already, so no fleet is selectable twice
     *
     * @param currentUser
     * @return
     */
    def getFleetsForCompany( Person currentUser, Long configurationStubId ) {

        // getting the company
        Company company = Company.get( currentUser.company.id )

        if ( !company ) {
            log.error( "no company found for user: ${currentUser.username}" )
            return
        }

        List<Fleet> fleets = Fleet.findAllByCompany( company, [ sort: "dateCreated", order: "desc" ] )

        Configuration stub = Configuration.get( configurationStubId )
        List<Fleet> alreadyAddedFleets = new ArrayList<Fleet>()
        stub.fleets.each { Fleet fleet ->
            alreadyAddedFleets.add( Fleet.get( fleet.id ) )
        }

        fleets.removeAll( alreadyAddedFleets )

        return fleets
    }

    /**
     * collect all available filling station groups for currentUser's company
     * remove those groups which are added to configuration already, so no groups is selectable twice
     *
     * @param currentUser
     * @return
     */
    def getGroupsForCompany( Person currentUser, Long configurationStubId ) {

        // getting the company
        Company company = Company.get( currentUser.company.id )

        if ( !company ) {
            log.error( "no company found for user: ${currentUser.username}" )
            return
        }

        List<FillingStationGroup> fillingStationGroups = FillingStationGroup.findAllByCompany( company )

        Configuration stub = Configuration.get( configurationStubId )
        List<FillingStationGroup> alreadyAddedGroups = new ArrayList<FillingStationGroup>()
        stub.fillingStationGroups.each { FillingStationGroup group ->
            alreadyAddedGroups.add( FillingStationGroup.get( group.id ) )
        }

        fillingStationGroups.removeAll( alreadyAddedGroups )

        return fillingStationGroups
    }

    /**
     * update carType
     * the company remains!
     *
     * @param person
     * @param carTypeName
     * @param energyConsumption
     * @param maxEnergyLoad
     */
    def updateCarTypeForCompany( Person person, Long carTypeId, String carTypeName, Double energyConsumption, Double maxEnergyLoad ) {

        // getting the company
        Company company = Company.get( person.company.id )

        if ( !company ) {
            log.error( "no company found for user: ${person.username}" )
            return
        }

        CarType carTypeToUpdate = CarType.get( carTypeId )

        carTypeToUpdate.name = carTypeName
        carTypeToUpdate.energyConsumption = energyConsumption
        carTypeToUpdate.maxEnergyLoad = maxEnergyLoad

        if ( !carTypeToUpdate.save( flush: true ) ) {
            log.error( "failed to update carType with : errors: ${carTypeToUpdate.errors}" )
        }


        return carTypeToUpdate
    }

    /**
     * creates a new CarTyp
     *
     * @param currentUser to save carType for currentUser's company
     * @param carName
     * @param energyDemand
     * @param maxEnergyCapacity
     */
    def createCarTypeForCompany( Person currentUser, String carName, Double energyDemand, Double maxEnergyCapacity ) {

        // getting the company
        Company company = Company.get( currentUser.company.id )

        if ( !company ) {
            log.error( "no company found for user: ${currentUser.username}" )
            return
        }

        CarType carType = new CarType(
                                company:  company,
                                name: carName,
                                energyConsumption: energyDemand,
                                maxEnergyLoad: maxEnergyCapacity
                          )

        if ( !carType.validate() && carType.hasErrors() ) {
            log.error( "failed to vaildate new carType: ${carType.errors}" )
            return
        }

        if ( !carType.save( flush: true ) ) {
            log.error( "failed to save new carType: ${carType.errors}" )
            return
        }

        return carType
    }

    /**
     * this method adds 'count' Cars of type 'carType' to fleet with given fleetStubId
     * these cars are stub cars, i.e. there are no routes persisted for these cars
     *
     * @param fleetStubId
     * @param count
     * @param carTypeId
     * @return
     */
    def addCarsToFleet( Long fleetStubId, Integer count, Long carTypeId, String nameForFleet ) {

        Fleet fleetStub = Fleet.get( fleetStubId )

        CarType carType = CarType.get( carTypeId )

        count.times {

            Car car = new Car(
                carType: carType,
                name: "${carType.name} - No.${it}",
                routesConfigured: false
            )

            if ( !car.save( flush: true ) ) {
                log.error( "failed to save car: ${car.errors}" )
            } else {

                fleetStub.addToCars( car )

            }

        }

        fleetStub.name = nameForFleet

        if ( !fleetStub.save( flush: true ) ) {
            log.error( "failed to update fleet: ${fleetStub.errors}" )
        }

        log.error( "fleetStup saved" )
    }

    /**
     * this method adds 'count' Stations of type 'FillingStationType' to group with given groupStubId
     * these stations are stub stations, i.e. there are no routes persisted for these stations
     *
     * @param groupStubId
     * @param count
     * @param stationTypeId
     * @return
     */
    def addStationsToGroup( Long groupStubId, Integer count, Long stationTypeId, String nameForGroup ) {

        FillingStationGroup groupStub = FillingStationGroup.get( groupStubId )

        FillingStationType fillingStationType = FillingStationType.get( stationTypeId )

        count.times {

            FillingStation station = new FillingStation(
                    fillingStationType: fillingStationType,
                    name: "${fillingStationType.name} - No.${it}",
                    groupsConfigured: false
            )

            if ( !station.save( flush: true ) ) {
                log.error( "failed to save car: ${car.errors}" )
            } else {

                groupStub.addToFillingStations( station )

            }

        }

        groupStub.name = nameForGroup

        if ( !groupStub.save( flush: true ) ) {
            log.error( "failed to update group: ${groupStub.errors}" )
        }


    }

    /**
     * this method creates a fleet stub and persists it to db
     * this fleet belongs to a given person's company
     *
     * @param person
     * @param configurationStubId
     * @return
     */
    def createFleetStub( Person person, Long configurationStubId ) {

        Company company = Company.get( person.company.id )

        Configuration stub = Configuration.get( configurationStubId )

        String generatedFleetName = "Fleet No. ${Fleet.countByCompany( Company.get( person.company.id ) ) + 1}"

        Fleet fleetStub = new Fleet(
                                company: company,
                                name: generatedFleetName,
                                stub: true,
                                distribution: Distribution.NOT_ASSIGNED,
                                fleetStatus: FleetStatus.NOT_CONFIGURED,
                                routesConfigured: false
                            )

        if ( !fleetStub.save( flush: true ) ) {
            log.error( "failed to save fleet stub: ${fleetStub.errors}" )
            return null
        }


        return fleetStub

    }

    def getFleetRoutesOfConfiguration( Long configurationId ) {

        Configuration configuration = Configuration.get( configurationId )

        def fleets = []

        configuration.fleets.each { Fleet fleet ->

            def fleetModel = [:]

            fleet = Fleet.get( fleet.id )

            fleetModel.cars = []
            fleetModel.name = fleet.name

            fleet.cars.each { Car car ->

                car = Car.get( car.id )

                def carModel = [:]
                carModel.name = car.name
                carModel.route = []

                Route route = Route.get( car.route.id )

                route.edges.each { TrackEdge trackEdge ->

                    trackEdge = TrackEdge.get( trackEdge.id )

                    carModel.route << trackEdge

                }

                fleetModel.cars << carModel

            }

            fleets << fleetModel

        }

        return fleets
    }

    def getGroupStationsOfConfiguration( Long configurationId ) {

        Configuration configuration = Configuration.get( configurationId )

        def fillingStationGroups = []

        configuration.fillingStationGroups.each { FillingStationGroup fillingStationGroup ->

            def fillingStationModel = [:]

            fillingStationGroup = FillingStationGroup.get( fillingStationGroup.id )

            fillingStationModel.stations = []
            fillingStationModel.name = fillingStationGroup.name

            fillingStationGroup.fillingStations.each { FillingStation fillingStation ->

                fillingStation = FillingStation.get( fillingStation.id )

                def stationModel = [:]
                stationModel.name = fillingStation.name
                stationModel.power = fillingStation.fillingStationType.power
                stationModel.lat = fillingStation.lat
                stationModel.lon = fillingStation.lon
                //stationModel.stations = []

                fillingStationModel.stations << stationModel

            }

            fillingStationGroups << fillingStationModel

        }

        return fillingStationGroups
    }

    /**
     * this method creates a group stub and persists it to db
     * this group belongs to a given person's company
     *
     * @param person
     * @param configurationStubId
     * @return
     */
    def createGroupStub( Person person, Long configurationStubId ) {

        Company company = Company.get( person.company.id )

        Configuration stub = Configuration.get( configurationStubId )

        String generatedFleetName = "Group No. ${FillingStationGroup.countByCompany( Company.get( person.company.id ) ) + 1}"


        FillingStationGroup groupStub = new FillingStationGroup(
                company: company,
                name: generatedFleetName,
                groupStatus: GroupStatus.NOT_CONFIGURED,
                groupsConfigured: false,
                stub: true
        )

        if ( !groupStub.save( flush: true ) ) {
            log.error( "failed to save fleet stub: ${groupStub.errors}" )
            return null
        }

        return groupStub

    }

    /**
     * collect carTypes of the company and adds the default ones of dfki
     *
     * @param currentUser to fetch available carType of the company
     * @return list of available carTypes sbd. of company's persons created
     */
    def getCarTypesForCompany( Person currentUser ) {

        Company company = Company.get( currentUser.company.id )
        List<CarType> carTypes = CarType.findAllByCompany( company )

        if ( !company.name.equals( "dfki" ) ) {
            Company dfkiCompany = Company.findByName( "dfki" )

            carTypes.addAll( CarType.findAllByCompany( dfkiCompany ) )

        }

        return carTypes
    }

    /**
     * collect FillingStationtype of the company and adds the default ones of dfki
     *
     * @param currentUser to fetch available carType of the company
     * @return list of available carTypes sbd. of company's persons created
     */
    def getFillingStationTypesForCompany( Person currentUser ) {

        Company company = Company.get( currentUser.company.id )
        List<FillingStationType> fillingStationTypes = FillingStationType.findAllByCompany( company )

        if ( !company.name.equals( "dfki" ) ) {
            Company dfkiCompany = Company.findByName( "dfki" )

            fillingStationTypes.addAll( FillingStationType.findAllByCompany( dfkiCompany ) )

        }

        return fillingStationTypes
    }

    /**
     * collect electricStationType of the company and adds the default ones of dfki
     *
     * @param currentUser to fetch available electricStationType of the company
     * @return list of available electricStationType sbd. of company's persons created
     */
    def getElectricStationTypesForCompany( Person currentUser ) {

        Company company = Company.get( currentUser.company.id )
        List<FillingStationType> electricStationTypes = FillingStationType.findAllByCompany( company )

        if ( !company.name.equals( "dfki" ) ) {
            Company dfkiCompany = Company.findByName( "dfki" )

            electricStationTypes.addAll( FillingStationType.findAllByCompany( dfkiCompany ) )

        }

        return electricStationTypes
    }


    /**
     * update FillingStation
     * the company remains!
     *
     * @param person
     * @param fillingStationTypeId
     * @param fillingStationTypeName
     * @param power
     */
    def updateFillingStationTypeForCompany( Person person, Long fillingStationTypeId, String fillingStationTypeName, String power ) {

        // getting the company
        Company company = Company.get( person.company.id )

        if ( !company ) {
            log.error( "no company found for user: ${person.username}" )
            return
        }

        FillingStationType fillingStationTypeToUpdate = FillingStationType .get( fillingStationTypeId )

        fillingStationTypeToUpdate.name = fillingStationTypeName
        fillingStationTypeToUpdate.power = power

        try {

            Float powerValue = Float.parseFloat( power )
            fillingStationTypeToUpdate.fillingPortion = ( 1 / 3600 ) * powerValue

            if ( !fillingStationTypeToUpdate.save( flush: true ) ) {
                log.error( "failed to update carType with : errors: ${fillingStationTypeToUpdate.errors}" )
            }

        } catch ( NumberFormatException nfe ) {
            log.error( "failed to convert string to float: ", nfe )
        }


        return fillingStationTypeToUpdate
    }

    /**
     * creates a new FillingStationtype
     *
     * @param currentUser to save carType for currentUser's company
     * @param carName
     * @param energyDemand
     * @param maxEnergyCapacity
     */
    def createFillingStationTypeForCompany( Person person, String fillingStationTypeName, Double power  ) {

        // getting the company
        Company company = Company.get( person.company.id )

        if ( !company ) {
            log.error( "no company found for user: ${person.username}" )
            return
        }

        FillingStationType fillingStationType = new FillingStationType(
                company:  company,
                name: fillingStationTypeName,
                power: power,
                fillingPortion: ( 1 / 3600 ) * power
        )

        if ( !fillingStationType.validate() && fillingStationType.hasErrors() ) {
            log.error( "failed to vaildate new fillingStationType: ${fillingStationType.errors}" )
            return
        }

        if ( !fillingStationType.save( flush: true ) ) {
            log.error( "failed to save new fillingStationType: ${fillingStationType.errors}" )
            return
        }


        return fillingStationType
    }

    /**
     * collect FillingStationtype of the company and adds the default ones of dfki
     *
     * @param currentUser to fetch available carType of the company
     * @return list of available carTypes sbd. of company's persons created
     */
    def getFillingStationTypesForCompany( Person currentUser ) {

        Company company = Company.get( currentUser.company.id )
        List<FillingStationType> fillingStationTypes = FillingStationType.findAllByCompany( company )

        if ( !company.name.equals( "dfki" ) ) {
            Company dfkiCompany = Company.findByName( "dfki" )

            fillingStationTypes.addAll( FillingStationType.findAllByCompany( dfkiCompany ) )

        }

        return fillingStationTypes
    }


    def removeConfigurationStub( Long configurationStubId ) {

        Configuration configurationToRemove = Configuration.get( configurationStubId )

        configurationToRemove.delete( flush: true )

        if ( Configuration.get( configurationStubId ) == null ) {

            log.error( "configuration stub with id ${configurationStubId} is removed again" )

        } else {

            log.error( "something wrent deeply wrong during removing of configuration stub" )

        }

    }

    def saveFinishedConfigurationStub( Long configurationStubId, String name, String description ) {

        Configuration configurationStubToSave = Configuration.get( configurationStubId )
        configurationStubToSave.stub = false

        // creating routes for distribution, set all FleetStatus
        configurationStubToSave.fleets.each { Fleet fleet ->

            fleet = Fleet.get( fleet.id )

            if ( fleet.fleetStatus == FleetStatus.SCHEDULED_FOR_CONFIGURING ) {
                fleet = routeService.createRandomDistanceRoutesForFleet( fleet.id )
            }

        }


        configurationStubToSave.configurationName = name
        configurationStubToSave.configurationDescription = description


        if ( !configurationStubToSave.save( flush: true ) ) {
            log.error( "failed to save configuration: ${configurationStubToSave.errors}" )
        } else {

            log.error( "configuration with id ${configurationStubId} is now unStubbed!" )

        }


    }


    def getRecentlyEditedConfigurationsOfCompany( Person person ) {

        Company company = Company.get( person.company.id )

        List<Configuration> configurations = Configuration.findAllByCompany( company )

        return configurations
    }

    def getCarsFromFleetTypeOrdered( Long fleetId ) {

        def cars = []
        Fleet fleet = Fleet.get( fleetId )

        fleet.cars.each { Car car ->
            cars << Car.get( car.id )
        }

        def types = cars.groupBy { Car car -> car.carType }

        return types
    }

    def getFillingStationsFromGroupTypeOrdered (Long groupId) {

        def stations = []
        FillingStationGroup fillingStationGroup = FillingStationGroup.get( groupId )

        fillingStationGroup.fillingStations.each { FillingStation fillingStation ->
            stations << FillingStation.get( fillingStation.id )
        }

        def types = stations.groupBy { FillingStation fillingStation -> fillingStation.fillingStationType.name }

        return types
    }

    def getCarsFromFleet( Long fleetId ) {

        def cars = []

        Fleet fleet = Fleet.get( fleetId )

        fleet.cars.each { Car car ->
            cars << Car.get( car.id )
        }

        return cars
    }

    def setDistributionForFleet( Distribution distribution, Long fleetId, Integer fromKm, Integer toKm ) {

        Fleet fleet = Fleet.get( fleetId )

        fleet.distribution = distribution
        fleet.routesConfigured = false

        fleet.plannedFromKm = fromKm
        fleet.plannedToKm = toKm

        fleet.fleetStatus = FleetStatus.SCHEDULED_FOR_CONFIGURING

        if ( !fleet.save( flush: true ) ) {

            log.error( "failed to save fleet: ${fleet.errors}" )

        }


    }

    def updateNameOfFleet( String nameForFleet, Long fleetStubId ) {

        Fleet fleet = Fleet.get( fleetStubId )
        fleet.name = nameForFleet

        if ( !fleet.save( flush: true ) ) {

            log.error( "failed to update fleetName for fleet: ${fleet.errors}" )

        }


    }

    def getNameOfFleet( Long fleetId ) {

        Fleet fleet = Fleet.get( fleetId )

        return fleet.name
    }

}
