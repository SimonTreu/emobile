package de.dfki.gs.controller.ms2.stats

import de.dfki.gs.controller.ms2.stats.commands.ExperimentResultCommandObject
import de.dfki.gs.controller.ms2.stats.commands.ShowStationStatsCommandObject
import de.dfki.gs.controller.ms2.stats.commands.ShowStationsCommandObject
import de.dfki.gs.controller.ms2.stats.commands.ShowStatsCommandObject
import org.apache.commons.io.FileUtils

class StatisticsController {

    def statisticService
    def generateStatsPictureService

    /**
     * FIXED: after sim run, javascript never get back to controller,
     * also, no chance to get simResultId.
     * better to show results on config side. !!
     *
     * @return
     */
    def showStats() {

        ExperimentResultCommandObject cmd = new ExperimentResultCommandObject();
        bindData( cmd, params )

        def m = [ : ]

        if ( !cmd.validate() && cmd.hasErrors() ) {

            log.error( "sth deeply went wrong: ${cmd.errors}" )

        }

        def stats = statisticService.generateStatisticMapForExperiment( cmd.experimentRunResultId )

        m.stats = stats

        m.experimentRunResultId = cmd.experimentRunResultId

        render view: 'showStats', model: m

    }

    def showPicture() {

        // log.error( "params: ${params}" )

        ShowStatsCommandObject cmd = new ShowStatsCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {
            log.error( "failed to show stats: ${cmd.errors}" )
        }


        List<String> successPartsToShow = new ArrayList<String>()
        List<String> featuresToShow = new ArrayList<String>()

        // now grap the "on"-checksboxes -> "plannedTime"...
        for ( Object key : params.keySet() ) {

            String value = (String) params.get( key )
            if ( value.equals( "on" ) ) {

                if ( ((String) key).endsWith( "all" ) ) {
                    if ( !successPartsToShow.contains( "all" ) ) {
                        successPartsToShow.add( "all" )
                    }
                } else if ( ((String) key).endsWith( "successful" ) ) {
                    if ( !successPartsToShow.contains( "successful" ) ) {
                        successPartsToShow.add( "successful" )
                    }
                } else if ( ((String) key).endsWith( "failed" ) ) {
                    if ( !successPartsToShow.contains( "failed" ) ) {
                        successPartsToShow.add( "failed" )
                    }
                } else {

                    String[] featureNames = ((String)key).split( "\\:\\:" )
                    if ( featureNames && featureNames.length > 0 ) {

                        String feature = featureNames[ featureNames.length - 1 ];
                        if ( !featuresToShow.contains( feature ) ) {
                            featuresToShow.add( feature )
                        }

                    }

                }

            }

        }

        // create stats for features and for success parts matrix
        def stats = statisticService.generateStatisticMapForExperiment( cmd.experimentRunResultId )



        File pictureFile = generateStatsPictureService.createDataChartFileForStats(
                                stats,
                                successPartsToShow,
                                featuresToShow,
                                cmd.fleetName,
                                cmd.carTypeName
        );

        byte[] imageBytes = pictureFile.bytes

        response.contentType = 'image/png'
        response.contentLength = imageBytes.size()

        OutputStream out = response.outputStream
        out.write( imageBytes )

        out.close()

        FileUtils.deleteQuietly( pictureFile )
    }


    def showStationPicture() {

        // log.error( "params: ${params}" )

        ShowStationStatsCommandObject cmd = new ShowStationStatsCommandObject()
        bindData( cmd, params )

        if ( !cmd.validate() && cmd.hasErrors() ) {
            log.error( "failed to show stats: ${cmd.errors}" )
        }


        List<String> successPartsToShow = new ArrayList<String>()
        List<String> featuresToShow = new ArrayList<String>()

        // now grap the "on"-checksboxes -> "timInUse"...
        for ( Object key : params.keySet() ) {

            String value = (String) params.get( key )
            if ( value.equals( "on" ) ) {

                if ( ((String) key).endsWith( "all" ) ) {
                    if ( !successPartsToShow.contains( "all" ) ) {
                        successPartsToShow.add( "all" )
                    }
                } else if ( ((String) key).endsWith( "successful" ) ) {
                    if ( !successPartsToShow.contains( "successful" ) ) {
                        successPartsToShow.add( "successful" )
                    }
                } else if ( ((String) key).endsWith( "failed" ) ) {
                    if ( !successPartsToShow.contains( "failed" ) ) {
                        successPartsToShow.add( "failed" )
                    }
                } else {

                    String[] featureNames = ((String)key).split( "\\:\\:" )
                    if ( featureNames && featureNames.length > 0 ) {

                        String feature = featureNames[ featureNames.length - 1 ];
                        if ( !featuresToShow.contains( feature ) ) {
                            featuresToShow.add( feature )
                        }

                    }

                }

            }

        }

        // create stats for features and for success parts matrix
        def stats = statisticService.generateStatisticMapForExperiment( cmd.experimentRunResultId )


        File pictureFile = generateStatsPictureService.createDataChartFileForStationStats(
                stats,
                successPartsToShow,
                featuresToShow,
                cmd.groupName,
                cmd.stationTypeName
        );

        byte[] imageBytes = pictureFile.bytes

        response.contentType = 'image/png'
        response.contentLength = imageBytes.size()

        OutputStream out = response.outputStream
        out.write( imageBytes )

        out.close()

        FileUtils.deleteQuietly( pictureFile )
    }



    def showDetailsPicture() {

        log.error( "params: ${params}" )

        String timeDistanceOption = ( params.time != null )?"time":"distance"

        def m = experimentStatsService.createStatsForDetailPicture( params.experimentResultId as Long, params.carType )

        // File file = generateStatsPictureService
        File distanceFile = generateStatsPictureService.createDataChartFileForDistanceDetails( m, timeDistanceOption  );

        byte[] imageBytes = distanceFile.bytes

        response.contentType = 'image/png'
        response.contentLength = imageBytes.size()

        OutputStream out = response.outputStream
        out.write( imageBytes )

        out.close()

        FileUtils.deleteQuietly( distanceFile )

    }

    def showStationsOnMap() {

        ShowStationsCommandObject cmd = new ShowStationsCommandObject()
        bindData( cmd, params )


        if ( !cmd.validate() && cmd.hasErrors() ) {
            log.error( "failed -- ${cmd.errors}" )
        } else {
            def m = [ : ]

            List<String> successCategoriesToShow = new ArrayList<String>()
            for ( Object key : params.keySet() ) {

                String value = (String) params.get( key )
                if ( value.equals( "on" ) ) {

                    if ( ((String) key).endsWith( "all" ) ) {
                        if ( !successCategoriesToShow.contains( "all" ) ) {
                            successCategoriesToShow.add( "all" )
                        }
                    } else if ( ((String) key).endsWith( "successful" ) ) {
                        if ( !successCategoriesToShow.contains( "successful" ) ) {
                            successCategoriesToShow.add( "successful" )
                        }
                    } else if ( ((String) key).endsWith( "failed" ) ) {
                        if ( !successCategoriesToShow.contains( "failed" ) ) {
                            successCategoriesToShow.add( "failed" )
                        }
                    }

                }

            }

            m.fillingStationGroups = statisticService.getStationsForMap( cmd.experimentRunResultId, successCategoriesToShow )
            // configurationService.getGroupStationsOfConfiguration( cmd.configurationStubId )
            render template: '/templates/configuration/stations/showStationsOnMap', model: m
        }

    }

}
