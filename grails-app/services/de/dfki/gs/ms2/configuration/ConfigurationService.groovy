package de.dfki.gs.ms2.configuration

import de.dfki.gs.domain.simulation.Car
import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.Configuration
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.FillingStationType
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.simulation.Simulation
import de.dfki.gs.domain.users.Company
import de.dfki.gs.domain.users.Person
import grails.transaction.Transactional

@Transactional
class ConfigurationService {

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

        List<Fleet> fleets = Fleet.findAllByCompany( company )

        Configuration stub = Configuration.get( configurationStubId )
        List<Fleet> alreadyAddedFleets = new ArrayList<Fleet>()
        stub.fleets.each { Fleet fleet ->
            alreadyAddedFleets.add( Fleet.get( fleet.id ) )
        }

        fleets.removeAll( alreadyAddedFleets )

        return fleets
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
    def addCarsToFleet( Long fleetStubId, Integer count, Long carTypeId ) {

        Fleet fleetStub = Fleet.get( fleetStubId )

        CarType carType = CarType.get( carTypeId )

        count.times {

            Car car = new Car(
                carType: carType,
                name: "${carType.name} - No.${it}"
            )

            if ( !car.save( flush: true ) ) {
                log.error( "failed to save car: ${car.errors}" )
            } else {

                fleetStub.addToCars( car )

            }

        }

        if ( !fleetStub.save( flush: true ) ) {
            log.error( "failed to update fleet: ${fleetStub.errors}" )
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

        Fleet fleetStub = new Fleet(
                                company: company,
                                name: "fleetStub-${UUID.randomUUID().toString().substring(0,5)}",
                                stub: true
                            )

        if ( !fleetStub.save( flush: true ) ) {
            log.error( "failed to save fleet stub: ${fleetStub.errors}" )
            return null
        }

        return fleetStub

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
    def createFillingStationTypeForCompany( Person person, String fillingStationTypeName, String power  ) {

        // getting the company
        Company company = Company.get( person.company.id )

        if ( !company ) {
            log.error( "no company found for user: ${person.username}" )
            return
        }

        FillingStationType fillingStationType = null

        try {
            Float powerValue = Float.parseFloat( power )

            fillingStationType = new FillingStationType(
                    company:  company,
                    name: fillingStationTypeName,
                    power: power,
                    fillingPortion: ( 1 / 3600 ) * powerValue
            )

            if ( !fillingStationType.validate() && fillingStationType.hasErrors() ) {
                log.error( "failed to vaildate new fillingStationType: ${fillingStationType.errors}" )
                return
            }

            if ( !fillingStationType.save( flush: true ) ) {
                log.error( "failed to save new fillingStationType: ${fillingStationType.errors}" )
                return
            }
        } catch ( NumberFormatException nfe ) {

            log.error( "failed to save fillingStationType: ", nfe )

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

    def saveFinishedConfigurationStub( Long configurationStubId ) {

        Configuration configurationStubToSave = Configuration.get( configurationStubId )
        configurationStubToSave.stub = false

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

}
