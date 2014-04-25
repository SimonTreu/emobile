package de.dfki.gs.service.datafetching

import de.dfki.gs.domain.ElectricStationTimeStatus
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Owners
import de.dfki.gs.model.elements.FillingStationStatus
import grails.transaction.Transactional
import groovy.json.JsonSlurper

@Transactional
class RweFetcherService {

    // def grailsApplication

    def fetchData( String urlRweString ) {


        InputStream urlStreamRWE = null;
        // String urlRweString = grailsApplication.config.fillingStations.rweUrl;
        URL urlRWE = null;

        try {

            urlRWE = new URL( urlRweString );
            //RWE JSON
            urlStreamRWE = urlRWE.openStream();
            BufferedReader readerRWE = new BufferedReader(new InputStreamReader(urlStreamRWE));
            JsonSlurper jsonSlurperRWE = new JsonSlurper();
            Object resultRWE = jsonSlurperRWE.parse(readerRWE);
            Map jsonResultRWE = (Map) resultRWE;
            List chargingstations = (List) jsonResultRWE.get("chargingstations");


            for (int i=0; i<chargingstations.size(); i++ ) {
                Map electricstations = (Map) chargingstations.get(i);
                String city =(String) electricstations.get("city")
                if (city=="Berlin") {
                    String id  = (String) electricstations.get("id")
                    String brand = (String) electricstations.get("brand")
                    String latitude = (String) electricstations.get("latitude")
                    String longitude = (String) electricstations.get("longitude")
                    String street = (String) electricstations.get("street")
                    String house_number = (String) electricstations.get("house_number")
                    boolean ac = (Boolean) electricstations.get("ac")
                    boolean dc = (Boolean) electricstations.get("dc")
                    boolean free = (Boolean) electricstations.get("free")
                    boolean occupied = (Boolean) electricstations.get("occupied")


                    try {
                        GasolineStation station = GasolineStation.findByOwnerId( id )
                       // System.out.println("RWE: "+station)
                        if ( !station ) {
                            // station not exist, create new one

                            String owners = ( Owners.values()*.toString() ).find{ it.equals( brand ) }

                            if ( !owners ) {
                                owners = Owners.undefined
                            }

                            station = new GasolineStation(
                                    ownerName: owners,
                                    ownerId: id ,
                                    lat: Double.parseDouble( latitude ),
                                    lon: Double.parseDouble( longitude ),
                                    streetName: street,
                                    houseNumber: house_number,
                                    type: GasolineStationType.AC_22KW.toString(),
                                    fillingPortion: 0.006167
                            )

                            if ( !station.save() ) {
                                log.error( "failed to save new gasolineStation: ${station.errors}" )
                            }

                        }

                        if ( station ) {

                            FillingStationStatus stationStatus = FillingStationStatus.IN_USE
                            if ( free ) {
                                stationStatus = FillingStationStatus.FREE
                            }

                            ElectricStationTimeStatus status = new ElectricStationTimeStatus(
                                    station: station,
                                    fillingStationStatus: stationStatus
                            )

                            if ( !status.save() ) {

                                log.error( "failed to save time status: ${status.errors}" )

                            } else {
                                log.debug( "saved: ${status.properties}" )
                            }

                        }


                    } catch ( NumberFormatException nfe ) {

                        log.error( "cound't  convert String to number", nfe )

                    }


                }
            }

        } catch ( MalformedURLException mue ) {

            log.error( "couldn't create url from ${urlRweString}", mue )

        } finally {
            if (urlStreamRWE != null) {
                urlStreamRWE.close();
            }
        }


    }


}
