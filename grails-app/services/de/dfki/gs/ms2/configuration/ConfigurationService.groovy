package de.dfki.gs.ms2.configuration

import de.dfki.gs.domain.simulation.CarType
import de.dfki.gs.domain.simulation.FillingStationGroup
import de.dfki.gs.domain.simulation.Fleet
import de.dfki.gs.domain.users.Company
import de.dfki.gs.domain.users.Person
import grails.transaction.Transactional

@Transactional
class ConfigurationService {

    /**
     * collect all available fillingStationGroups for currentUser's company
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
     * update
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
     *
     * @param currentUser to fetch available carType of the company
     * @return list of available carTypes sbd. of company's persons created
     */
    def getCarTypesForCompany( Person currentUser ) {

        Company company = Company.get( currentUser.company.id )
        List<CarType> carTypes = CarType.findAllByCompany( company )

        return carTypes
    }


}
