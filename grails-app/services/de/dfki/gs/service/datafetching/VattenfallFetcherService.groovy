package de.dfki.gs.service.datafetching

import de.dfki.gs.domain.ElectricStationTimeStatus
import de.dfki.gs.domain.GasolineStation
import de.dfki.gs.domain.GasolineStationType
import de.dfki.gs.domain.Owners
import de.dfki.gs.model.elements.FillingStationStatus
import grails.transaction.Transactional
import groovy.json.JsonSlurper
import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.LogFactory

/**
 * Created by anbu02 on 24.04.14.
 */
@Transactional
class VattenfallFetcherService {

    def fetchData( String urlJavaScriptVattenfallString , String urlJsonVattenfallString ) {


        //final String JAVASCRIPT_VATTENFALL = "http://ladestationen.cosmotools.de/google_map/js/locations_ber.js";
        //final String JSON_VATENFALL = "http://ladestationen.cosmotools.de/google_map/status.php"
       // def log = LogFactory.getLog( Vate.class )

        InputStream urlStreamJavaScriptVattenfall = null;
        InputStream urlStreamJsonVattenfall = null;

        URL urlJsonVattenfall = new URL(urlJsonVattenfallString);
        URL urlJavaScriptVattenfall= new URL(urlJavaScriptVattenfallString);

        // first check filling stations from js -> db? save
        //Java Script
        urlStreamJavaScriptVattenfall = urlJavaScriptVattenfall.openStream();
        BufferedReader readerJavaScriptVattenfall = new BufferedReader(new InputStreamReader(urlStreamJavaScriptVattenfall));

        String str = readerJavaScriptVattenfall.readLine().toString();
        String[] strParts = str.split("locationMapMarkers =");
        String part1 = strParts[1];
        String part2 = part1.replaceAll(";", "")
        def javaScriptSlurperVattenfall = new JsonSlurper().parseText(part2);

        for (int i=0; i<javaScriptSlurperVattenfall.name.size(); i++){
            String idName = ((String)javaScriptSlurperVattenfall.name.get( i ) ).replace( ".", "" );
            String latitude = (String) javaScriptSlurperVattenfall.'Geo Lat'.get(i)
            String longitude = (String) javaScriptSlurperVattenfall.'Geo Lng'.get(i)



            GasolineStation station = GasolineStation.findByOwnerId(  idName.trim()  )

            if ( !station ) {

                    station = new GasolineStation(
                            ownerName: Owners.VATTENFALL.toString(),
                            ownerId: idName.trim(),
                            lat: Double.parseDouble( latitude),
                            lon: Double.parseDouble( longitude ),
                            streetName: javaScriptSlurperVattenfall.'Strasse + Nr'.get(i),
                            type: GasolineStationType.AC_3_7KW.toString(),
                            fillingPortion: 0.002056
                    )

                    if ( !station.save( flush: true ) ) {
                        log.error( "failed to save new gasolineStation: ${station.errors}" )
                    }
            }

        }

        // second: iterate through jason



        try {

            //Json
            urlStreamJsonVattenfall = urlJsonVattenfall.openStream();
            BufferedReader readerJsonVattenfall = new BufferedReader(new InputStreamReader(urlStreamJsonVattenfall));
            JsonSlurper jsonSlurperVattenfall = new JsonSlurper();
            Object resultJsonVattenfall = jsonSlurperVattenfall.parse(readerJsonVattenfall);
            Map <String, ArrayList> json = (Map) resultJsonVattenfall;

            //Java Script
            urlStreamJavaScriptVattenfall = urlJavaScriptVattenfall.openStream();


            for ( Map.Entry<String, ArrayList> entry : json.entrySet()) {

                String key = entry.getKey();

                GasolineStation station = GasolineStation.findByOwnerId( key )

                if ( station ) {

                    ArrayList tab = entry.getValue();
                    boolean  stat = tab.find { String s -> StringUtils.containsIgnoreCase( s, "FREE" ) }

                    FillingStationStatus stationStatus1 = FillingStationStatus.IN_USE

                    if ( stat ) {
                        stationStatus1 = FillingStationStatus.FREE
                    }

                    ElectricStationTimeStatus status1 = new ElectricStationTimeStatus(
                            station: station,
                            fillingStationStatus: stationStatus1
                    )

                    if ( !status1.save() ) {

                        log.error( "failed to save time status: ${status1.errors}" )

                    } else {
                        log.debug( "saved: ${status1.properties}" )
                    }


                }
            }

        } catch ( NumberFormatException nfe ) {

            log.error( "cound't  convert String to number", nfe )



    } catch ( MalformedURLException mue ) {

            log.error( "couldn't create url from ${urlJavaScriptVattenfallString} ${urlJsonVattenfallString}", mue )

        } finally {
            if ((urlStreamJsonVattenfall != null) || (urlStreamJavaScriptVattenfall!= null)) {
                urlStreamJsonVattenfall.close();
                urlStreamJavaScriptVattenfall.close();

            }
        }


    }
}
