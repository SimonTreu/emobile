package de.dfki.gs.ms2.configuration

import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.FillingStationType
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.users.Company
import de.dfki.gs.domain.users.Person
import grails.transaction.Transactional

@Transactional
class ConfigurationService {

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
     *
     * @param currentUser
     * @return
     */
    def getFleetsForCompany( Person currentUser ) {

        // getting the company
        Company company = Company.get( currentUser.company.id )

        if ( !company ) {
            log.error( "no company found for user: ${currentUser.username}" )
            return
        }

        List<Fleet> fleets = Fleet.findAllByCompany( company )

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

}
