package de.dfki.gs.service.stats
import de.dfki.gs.controller.MapViewController
import de.dfki.gs.domain.CarType
import de.dfki.gs.domain.ElectricStationTimeStatus
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.model.elements.FillingStationStatus
import grails.transaction.Transactional
import groovy.sql.Sql

import javax.sql.DataSource

@Transactional
class RealStationsStatsService {

    /**
     * Service to show gasoline stations in real time
     * it connects with openLayersMapsStatistik as view
     */

    def getUsages() {
        return getUsages( null, null );
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    def getUsages( Date from, Date to ) {
        def stationList = []
        if ( from == null ) {

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set( Calendar.YEAR,  2014);
            calendar.set( Calendar.MONTH, 3 );
            calendar.set( Calendar.DAY_OF_MONTH, 1 );

            from = calendar.getTime()

        }
        if ( to == null ) {
            to = new Date()
        }

        // timeDelta in hours
        long timeDelta = ( to.time - from.time ) / ( 1000 * 60 * 60 );

        def m = [ : ]

        Map<String,List<Long>> fillingStations = new HashMap<String, List<Long>>()
        List<GasolineStation> gasolineStationList = GasolineStation.findAllByOwnerIdNotIsNull()

        // to keep things short for testing, to be skipped
        //gasolineStationList = gasolineStationList.subList(0,5 )

        gasolineStationList.each { GasolineStation station ->

            String key = "${station.lat}-${station.lon}"
            List<Long> mappedStationIds = fillingStations.get( key )

            if ( mappedStationIds == null ) {

                mappedStationIds = new ArrayList<Long>()

            }
            mappedStationIds.add( station.id )

            fillingStations.put( key, mappedStationIds )
        }

        fillingStations.each { Map.Entry<String,List<Long>> mapEntry ->

            List<Long> currentStationIds = mapEntry.value
            List<GasolineStation> currentStations = GasolineStation.findAllByIdInList( currentStationIds )

            def localMap = [:]
            localMap.lat = currentStations.get( 0 ).lat
            localMap.lon = currentStations.get( 0 ).lon
            localMap.streetName = currentStations.get( 0 ).streetName
            if ( currentStations.get(0).houseNumber == null) {
                localMap.houseNumber = ''

            } else
                localMap.houseNumber = currentStations.get(0).houseNumber
            localMap.type = currentStations.get(0).type
            if (currentStations.get(0).ownerName == 'undefined'){
                currentStations.get(0).ownerName = 'RWE'
                localMap.ownerName = currentStations.get(0).ownerName
            } else {
                localMap.ownerName = currentStations.get(0).ownerName
            }

            List<ElectricStationTimeStatus> statusList = ElectricStationTimeStatus.findAllByStationInListAndDateCreatedBetween( currentStations, from, to );

            int statusListSize = statusList.size()
            int countFree = statusList.count { it.fillingStationStatus.equals( FillingStationStatus.FREE.toString() ) }

            double freePercentage = 0;
            double inUsePercentage = 100;
            double scale = 0;
            double scaleGroup = 0;
            String color = '#B3B7B6';

            if ( statusListSize > 0 ) {
                freePercentage = Math.round( ( countFree / statusListSize ) * 100 );
                inUsePercentage = 100 - freePercentage;
            }

            scale = inUsePercentage / 100
            if  ((scale <= 0.0) || (scale <=0.2)) {
                scale = scale + 0.3
            } else {
                scale = scale;
            }

            localMap.inUsePercentage = inUsePercentage;
            localMap.freePercentage = freePercentage;
            localMap.scale = scale;
            localMap.color = color;
            stationList << localMap
        }

        m.stationList = stationList
        m.legendGroups = createLegend( m )

        return m
    }

    /**
     * for legend printing
     * for each station, check in which group inUsePercentage is
     * and count per group
     * set name and color fpr view
     *
     * @param m
     * @return
     */
    def createLegend( m ) {

        def legendGroups = [
               '0-10' :  0..10,
               '11-20' : 11..20,
               '21-30' : 21..30,
               '31-40' : 31..40,
               '41-50' : 41..50,
               '51-60' : 51..60,
               '61-70' : 61..70,
               '71-80' : 71..80,
               '81-90' : 81..90,
               '91-100' : 91..100
        ];

        def legendGroupsModel = []

        legendGroups.each { it ->

            def local = [:]

            local.count = m.stationList.count { sLit ->
                ( Math.round( sLit.inUsePercentage ) as Integer ) in it.value
            }
            local.name = it.key
            legendGroupsModel << local
        }
     return legendGroupsModel
    }
}
