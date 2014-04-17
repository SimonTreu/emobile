package de.dfki.gs.controller

import org.apache.commons.io.FileUtils

class StatsController {


    def showStats() {

        String timeStatsPictureName = "/tmp/${params.timeFileUUID}"
        String distanceStatsPictureName = "/tmp/${params.distanceFileUUID}"
        String fillingStatsPictureName = "/tmp/${params.fillingFileUUID}"


        def m = [
                    timeFileUUID : timeStatsPictureName,
                    distanceFileUUID : distanceStatsPictureName,
                    fillingStationFileUUID : fillingStatsPictureName
        ]

        int carsCount = params.carsCount as int
        int countTargetReached = params.countTargetReached as int
        double simulationTime = params.simulationTime as double

        double percentageReached = ( countTargetReached / carsCount ) * 100

        m.carsCount = carsCount
        m.countTargetReached = countTargetReached
        m.percentageReached = percentageReached

        m.simulationTime = ( simulationTime / 3600 )

        render view: 'stats', model: m
    }

    def showStatsFile() {

        String statsPicturePath = params.fileUUID

        File file = new File( statsPicturePath )

        byte[] imageBytes = file.bytes

        response.contentType = 'image/png'
        response.contentLength = imageBytes.size()

        OutputStream out = response.outputStream
        out.write( imageBytes )

        out.close()

        FileUtils.deleteQuietly( file )

    }


}
