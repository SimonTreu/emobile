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
                String nameNew = name.replaceAll("Citroën&nbsp;C-Zero&nbsp;&nbsp;","").replaceAll("&nbsp;","").replaceAll("'","").replaceAll("\\(" ,"").replaceAll("\\)" ,"");


                def tagsoupParser = new org.ccil.cowan.tagsoup.Parser();
                def slurper = new XmlSlurper(tagsoupParser)
                def htmlParser = slurper.parse("https://kunden.multicity-carsharing.de/kundenbuchung/hal2ajax_process.php?infoConfig%5BinfoTyp%5D=HM_AUTO_INFO&infoConfig%5BobjectId%5D=$id&infoConfig%5Bobjecttyp%5D=carpos&ajxmod=hal2map&callee=markerinfo")

                def address = htmlParser.children().children().children()[2].toString().replaceAll("Standort:","")
                def fuel = htmlParser.children().children().children().children().children().children().toString().replaceAll(" %","")

                    try {

                        CarSharingCars carSharing = CarSharingCars.findByName(  nameNew )
                        // System.out.println("Multicity: "+carSharing)
                        if ( !carSharing ) {
                            // carSharing not exist, create new one

                            String owners = ( CarSharingOwners.MULTICITY)

                            if ( !owners ) {
                                owners = CarSharingOwners.undefined
                            }

                            carSharing = new CarSharingCars(

                                    name: nameNew,
                                    ownerName: owners

                            )

                            if ( !carSharing.save() ) {
                                log.error( "failed to save new carSharing: ${carSharing.errors}" )
                            }

                        }

                        if ( carSharing ) {

                            CarSharingTimeStatus status = new CarSharingTimeStatus(
                                    carSharing: carSharing,
                                    address: address,
                                    fuel: fuel,
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

