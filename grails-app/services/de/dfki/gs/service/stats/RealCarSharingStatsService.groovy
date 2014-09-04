package de.dfki.gs.service.stats

import de.dfki.gs.controller.MapViewController
import de.dfki.gs.domain.CarSharingCars
import de.dfki.gs.domain.GasolineStation
import grails.transaction.Transactional
import groovy.sql.Sql
import javax.sql.DataSource

/**
 * Created by anbu02 on 02.09.14.
 */

@Transactional
class RealCarSharingStatsService {
    /**
     * Service to show carsharing cars in real time
     * it connects with openLayersCarSharingMaps as view
     */


    def getUsages( ) {

        def stationList = []

        def m = [ : ]

        Map<String,List<Long>> carSharing = new HashMap<String, List<Long>>()
        List<CarSharingCars> carSharingList = CarSharingCars.findAll()

        carSharingList.each { CarSharingCars car ->

            String key = "${car.lat}-${car.lon}"
            List<Long> mappedStationIds = carSharing.get( key )

            if ( mappedStationIds == null ) {

                mappedStationIds = new ArrayList<Long>()

            }
            mappedStationIds.add( car.id )

            carSharing.put( key, mappedStationIds )
        }


        m.stationList = stationList

        return m
    }


}
