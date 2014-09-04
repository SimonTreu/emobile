package de.dfki.gs.service.datafetching

import de.dfki.gs.domain.CarSharingCars
import de.dfki.gs.domain.CarSharingOwners
import de.dfki.gs.domain.CarSharingTimeStatus
import groovy.json.JsonSlurper

/**
 * Created by anbu02 on 02.09.14.
 */

class DriveNowFetcherService {

    def fetchData( String urlDriveNowString ) {

        InputStream urlStreamDriveNow = null;
        URL urlDriveNow = null;
        println "DriveNow job finished at"
        try {
            urlDriveNow = new URL( urlDriveNowString );
            urlStreamDriveNow = urlDriveNow.openStream();
            BufferedReader readerDriveNow = new BufferedReader(new InputStreamReader(urlStreamDriveNow));
            JsonSlurper jsonSlurperDriveNow = new JsonSlurper();
            def resultDriveNow = jsonSlurperDriveNow.parse(readerDriveNow);
            resultDriveNow.'rec'.'vehicles'.'vehicles'.each { vehicleMap ->



                String fuelType =(String) vehicleMap.'fuelType'
                if (fuelType=="ELE") {
                    String vin = (String) vehicleMap.'vin'
                    String address  = (String) vehicleMap.'address'
                    String mileage  = (String) vehicleMap.'mileage'
                    String latitude = (String) vehicleMap.'position'.'latitude'
                    String longitude = (String) vehicleMap.'position'.'longitude'
                    String name = (String) vehicleMap.'licensePlate'
                    String fuel = (String) vehicleMap.'fuelState'

                    try {

                        CarSharingCars carSharing = CarSharingCars.findByVin( vin )
                        // System.out.println("DriveNow: "+carSharing)
                        if ( !carSharing ) {
                            // carSharing not exist, create new one

                            String owners = ( CarSharingOwners.DRIVENOW)

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
                                    address : address,
                                    mileage: mileage


                            )

                            if ( !status.save() ) {

                                log.error( "failed to save time status: ${status.errors}" )

                            } else {
                                log.debug( "saved: ${status.properties}" )
                            }

                        }

                    }catch (NumberFormatException nfe ) {

                        log.error( "cound't  convert String to number", nfe )

                    }

                }

                //println vehicleMap.'model'
            }

            /*
            Map jsonResultDriveNow = (Map) resultDriveNow;

            List vehicles = (List) jsonResultDriveNow


            for (int i=0; i<vehicles.size(); i++ ) {

                Map DriveNowCars = (Map) vehicles.get(i);

                String fuelType =(String) DriveNowCars.get("fuelType")

                if (fuelType=="ELE") {

                    Integer fuel = (Integer) DriveNowCars.get("fuel")
                    boolean charging = (Boolean) DriveNowCars.get("charging")

                    try {
                        CarSharingCars carSharing = CarSharingCars.findByVin( vin )
                        // System.out.println("DriveNow: "+carSharing)
                        if ( !carSharing ) {
                            // carSharing not exist, create new one

                            String owners = ( CarSharingOwners.DRIVENOW)

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
            */

        } catch (MalformedURLException mue) {
            log.error( "couldn't create url from ${urlDriveNowString}", mue )
        }   finally {
            if (urlStreamDriveNow != null) {
                urlStreamDriveNow.close();
            }
        }


    }
}
