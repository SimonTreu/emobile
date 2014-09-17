package de.dfki.gs.service.datafetching

import de.dfki.gs.domain.ElectricStationTimeStatus
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Owners
import de.dfki.gs.domain.SocketTimeStatus
import de.dfki.gs.model.elements.FillingStationStatus
import grails.transaction.Transactional
import groovy.json.JsonSlurper
import groovyx.gpars.GParsPool
import org.codehaus.groovy.grails.web.json.JSONObject

import java.util.concurrent.atomic.AtomicInteger
import com.opensymphony.module.sitemesh.parser.HTMLPageParser

@Transactional
class RweFetcherService {

    // def grailsApplication

    def fetchData( String urlRweString ) {


        InputStream urlStreamRWE = null;
        // String urlRweString = grailsApplication.config.fillingStations.rweUrl;
        URL urlRWE = null;
        URL urlSpotId1 =null
        URL urlSpotId2 =null

        InputStream urlSpotStreamId1 = null;
        InputStream urlSpotStreamId2 = null;


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
                    String spotIds = (String) electricstations.get("spotIds")
                    /*
                    CODE FOR STORAGE SOCKET INFORMATION
                    String[] SpotId = spotIds.split(",");

                    String spotId1 = SpotId[0]
                    String spotId2 = SpotId[1].replaceAll(" ","")

                    urlSpotId1 = new URL( "http://www.rwe-mobility.com/emobilityfrontend/rs/chargingstations/$spotId1" );
                    urlSpotId2 = new URL( "http://www.rwe-mobility.com/emobilityfrontend/rs/chargingstations/$spotId2" );

                    def tagsoupParser = new org.ccil.cowan.tagsoup.Parser();
                    def slurper = new XmlSlurper(tagsoupParser)



                    def powerParser1 = slurper.parse("https://www.rwe-mobility.com/vm/service/VirtualMeterServlet?ladepunkt=$spotId1")
                    def idSpot1 = powerParser1.toString().replaceAll("ZählerZählerLadepunktStartLadepunktZählerstandUhrzeit","").substring(0,9)
                    def powerSpot1 = powerParser1.toString().replaceAll("ZählerZählerLadepunktStartLadepunktZählerstandUhrzeit","").substring(9).split("kWh")[0]
                    def timeSpot1 = powerParser1.toString().replaceAll("ZählerZählerLadepunktStartLadepunktZählerstandUhrzeit","").substring(9).split("kWh")[1]

                    def powerParser2 = slurper.parse("https://www.rwe-mobility.com/vm/service/VirtualMeterServlet?ladepunkt=$spotId2")
                    def idSpot2 = powerParser2.toString().replaceAll("ZählerZählerLadepunktStartLadepunktZählerstandUhrzeit","").substring(0,9)
                    def powerSpot2 = powerParser2.toString().replaceAll("ZählerZählerLadepunktStartLadepunktZählerstandUhrzeit","").substring(9).split("kWh")[0]
                    def timeSpot2 = powerParser2.toString().replaceAll("ZählerZählerLadepunktStartLadepunktZählerstandUhrzeit","").substring(9).split("kWh")[1]


                    urlSpotStreamId1 = urlSpotId1.openStream();
                    urlSpotStreamId2 = urlSpotId2.openStream();


                    BufferedReader readerSpotId1 = new BufferedReader(new InputStreamReader(urlSpotStreamId1));
                    BufferedReader readerSpotId2 = new BufferedReader(new InputStreamReader(urlSpotStreamId2));


                    JsonSlurper jsonSlurperSpotId = new JsonSlurper();
                    Object resultSpotId1 = jsonSlurperSpotId.parse(readerSpotId1);
                    Object resultSpotId2 = jsonSlurperSpotId.parse(readerSpotId2);


                    String spotsId1 = resultSpotId1."id"
                    String statusId1 = resultSpotId1."status"
                    String dateTime1 =  resultSpotId1."dateTime"

                    String spotsId2 = resultSpotId1."id"
                    String statusId2 = resultSpotId1."status"
                    String dateTime2 =  resultSpotId1."dateTime"
                    */

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
                                    type: GasolineStationType.AC_22_2KW.toString(),
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

                            /*
                            CODE FOR STORAGE SOCKET INFORMATION
                            SocketTimeStatus socket = new SocketTimeStatus(
                                    name: spotsId1,
                                    power:powerSpot1


                            )
                            if ( !socket.save() ) {

                                log.error( "failed to save time status: ${socket.errors}" )

                            } else {
                                log.debug( "saved: ${socket.properties}" )
                            }
                            */

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
