package de.dfki.gs.service.datafetching

import de.dfki.gs.domain.CarSharingCars
import de.dfki.gs.domain.CarSharingOwners
import de.dfki.gs.domain.CarSharingTimeStatus
import groovy.json.JsonSlurper

/**
 * Created by anbu02 on 02.09.14.
 */
class Car2GoFetcherService {

    def fetchData( String urlCar2GoString ) {

        InputStream urlStreamCar2Go = null;
        URL urlCar2Go = null;
        println "Car2Go job finished at"
        try {
            urlCar2Go = new URL( urlCar2GoString );
            urlStreamCar2Go = urlCar2Go.openStream();
            BufferedReader readerCar2Go = new BufferedReader(new InputStreamReader(urlStreamCar2Go));
            JsonSlurper jsonSlurperCar2Go = new JsonSlurper();
            Object resultCar2Go = jsonSlurperCar2Go.parse(readerCar2Go);
            Map jsonResultCar2Go = (Map) resultCar2Go;
            List placemarks = (List) jsonResultCar2Go.get("placemarks");

            for (int i=0; i<placemarks.size(); i++ ) {

                Map car2goCars = (Map) placemarks.get(i);

                String engineType =(String) car2goCars.get("engineType")

                if (engineType=="ED") {

                    String address  = (String) car2goCars.get("address")
                    String name = (String) car2goCars.get("name")
                    String vin = (String) car2goCars.get("vin")
                    Integer fuel = (Integer) car2goCars.get("fuel")
                    boolean charging = (Boolean) car2goCars.get("charging")
                    String latitude = (String) car2goCars.get("coordinates")[1]
                    String longitude = (String) car2goCars.get("coordinates")[0]

                    try {
                        CarSharingCars carSharing = CarSharingCars.findByVin( vin )
                        // System.out.println("car2Go: "+carSharing)
                        if ( !carSharing ) {
                            // carSharing not exist, create new one

                            String owners = ( CarSharingOwners.CAR2GO)

                            if ( !owners ) {
                                owners = CarSharingOwners.undefined
                            }

                            carSharing = new CarSharingCars(

                                    vin : vin,
                                    name: name,
                                    ownerName: owners

                            )

                            if ( !carSharing.save() ) {
                                log.error( "failed to save new carSharing: ${carSharing.errors}" )
                            }

                        }

                        if ( carSharing ) {

                            CarSharingTimeStatus status = new CarSharingTimeStatus(
                                    carSharing: carSharing,
                                    fuel: fuel,
                                    lat: Double.parseDouble( latitude ),
                                    lon: Double.parseDouble( longitude ),
                                    address : address


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

        } catch (MalformedURLException mue) {
            log.error( "couldn't create url from ${urlCar2GoString}", mue )
        }   finally {
            if (urlStreamCar2Go != null) {
                urlStreamCar2Go.close();
            }
        }


    }
}
