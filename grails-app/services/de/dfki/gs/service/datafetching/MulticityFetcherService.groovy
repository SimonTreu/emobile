package de.dfki.gs.service.datafetching

import groovyx.gpars.GParsPool
import java.util.concurrent.atomic.AtomicInteger

import com.opensymphony.module.sitemesh.parser.HTMLPageParser
import de.dfki.gs.domain.CarSharingCars
import de.dfki.gs.domain.CarSharingOwners
import de.dfki.gs.domain.CarSharingTimeStatus
import groovy.json.JsonSlurper
import groovy.util.XmlParser;

/**
 * Created by anbu02 on 02.09.14.
 */

class MulticityFetcherService {

    def fetchData( String urlMulticityString ) {

        InputStream urlStreamMulticity = null;
        URL urlMulticity = null;
        try {
            urlMulticity = new URL( urlMulticityString );
            urlStreamMulticity = urlMulticity.openStream();
            BufferedReader readerMulticity = new BufferedReader(new InputStreamReader(urlStreamMulticity));
            JsonSlurper jsonSlurperMulticity = new JsonSlurper();
            def resultMulticity = jsonSlurperMulticity.parse(readerMulticity);
            resultMulticity.'marker'.each { markerMap ->

                String latitude = (String) markerMap.'lat'
                String longitude = (String) markerMap.'lng'
                String id = (String) markerMap.'hal2option'.'id'
                String name = (String) markerMap.'hal2option'.'tooltip'
                String name1 = name.replaceAll("Citroën&nbsp;C-Zero&nbsp;&nbsp;","");
                //'CitroënC-Zero(B-MC2439)'
                String name2 = name1.replaceAll("&nbsp;",'');

                def tagsoupParser = new org.ccil.cowan.tagsoup.Parser();
                def slurper = new XmlSlurper(tagsoupParser)
                def htmlParser = slurper.parse("https://kunden.multicity-carsharing.de/kundenbuchung/hal2ajax_process.php?infoConfig%5BinfoTyp%5D=HM_AUTO_INFO&infoConfig%5BobjectId%5D=$id&infoConfig%5Bobjecttyp%5D=carpos&ajxmod=hal2map&callee=markerinfo")

                def list3 = htmlParser.children().children().children()[2]
                String address = list3
                String newAdress = address.replaceAll("Standort:", "")

                def list5 = htmlParser.children().children().children().children().children().children()
                String fuel = list5
                String fuelStr = fuel.replaceAll(" %","")


                    try {

                        CarSharingCars carSharing = CarSharingCars.get( Long.parseLong( id ) )
                        // System.out.println("Multicity: "+carSharing)
                        if ( !carSharing ) {
                            // carSharing not exist, create new one

                            String owners = ( CarSharingOwners.MULTICITY)

                            if ( !owners ) {
                                owners = CarSharingOwners.undefined
                            }

                            carSharing = new CarSharingCars(

                                    name: name2,
                                    ownerName: owners

                            )

                            if ( !carSharing.save() ) {
                                log.error( "failed to save new carSharing: ${carSharing.errors}" )
                            }

                        }

                        if ( carSharing ) {

                            CarSharingTimeStatus status = new CarSharingTimeStatus(
                                    carSharing: carSharing,
                                    address: newAdress,
                                    fuel: fuelStr,
                                    lat: Double.parseDouble( latitude ),
                                    lon: Double.parseDouble( longitude )
                            )

                            if ( !status.save() ) {

                                log.error( "failed to save time status: ${status.errors}" )

                            } else {
                                log.debug( "saved: ${status.properties}" )
                            }

                        }

                    } catch (NumberFormatException nfe ) {

                        log.error( "cound't  convert String to number", nfe )

                    }

                //println vehicleMap.'model'
            }

        } catch (MalformedURLException mue) {
            log.error( "couldn't create url from ${urlMulticityString}", mue )
        }   finally {
            if (urlStreamMulticity != null) {
                urlStreamMulticity.close();
            }
        }


    }
}

