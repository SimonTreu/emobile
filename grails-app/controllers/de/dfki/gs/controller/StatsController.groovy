package de.dfki.gs.controller

import org.apache.commons.io.FileUtils

class StatsController {


    def showStats() {

        String timeStatsPictureName = "/tmp/${params.timeFileUUID}"
        String distanceStatsPictureName = "/tmp/${params.distanceFileUUID}"


        def m = [ timeFileUUID : timeStatsPictureName, distanceFileUUID : distanceStatsPictureName ]


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
