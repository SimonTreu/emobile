package de.dfki.gs.service.stats

import grails.transaction.Transactional
import org.jfree.chart.ChartRenderingInfo
import org.jfree.chart.ChartUtilities
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.CategoryAxis
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.entity.StandardEntityCollection
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset

import java.awt.Font

@Transactional
class GenerateStatsPictureService {

    public File createDataChartFileForStats(
                    stats,
                    List<String> successPartsToShow,
                    List<String> featuresToShow,
                    String fleetName,
                    String carTypeName
    ) {

        def m = pickRelevantDataRowsFromStats( stats, successPartsToShow, featuresToShow, fleetName, carTypeName )

        final BoxAndWhiskerCategoryDataset dataset = createDataSet( m, successPartsToShow, featuresToShow )


        final CategoryAxis xAxis = new CategoryAxis("Success Category");
        final NumberAxis yAxis = new NumberAxis("time in [ h ], distance in [ km ], energy in [ kWh ] ");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox( true );
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(
                "${fleetName} : ${carTypeName}",
                new Font("SansSerif", Font.BOLD, 14),
                plot,
                true
        );


        final ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection() );
        UUID uuid = UUID.randomUUID();
        final File file = new File( "/tmp/emobile-time-filling-stats-${uuid}.png" );
        ChartUtilities.saveChartAsPNG( file, chart, 1300, 700, info );

        return file;
    }

    private static BoxAndWhiskerCategoryDataset createDataSet( m, List<String> successParts, List<String> features ) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        for ( String successPart : successParts ) {

            for ( String feature : features ) {

                List<Long> valuez = new ArrayList<Long>()
                if ( feature.contains( "Time" ) ) {

                    m."${successPart}"."${feature}".each { Long l ->
                        valuez.add( Math.round( ( l / ( 60 * 60 ) ) ) )
                    }

                } else {

                    m."${successPart}"."${feature}".each { l ->
                        valuez.add( (Long) Math.round( l ) )
                    }

                }

                dataset.add( (List) valuez , "${feature}" , "${successPart}" )

            }


        }

        return dataset;

    }

    private Map pickRelevantDataRowsFromStats(
                    stats,
                    List<String> successPartsToShow,
                    List<String> featuresToShow,
                    String fleetName,
                    String carTypeName
    ) {

        def m = [ : ]

        for ( String successPart : successPartsToShow ) {

            m."${successPart}" = [ : ]

        }

        stats.fleets.each { a ->

            if ( a.name.equals( fleetName ) ) {

                a.carTypes.each { carType ->

                    if ( carType.name.equals( carTypeName ) ) {

                        if ( successPartsToShow.contains( "all" ) ) {

                            for ( String feature : featuresToShow ) {

                                m."all"."${feature}" = carType.stats.allCars."${feature}".valuez

                            }

                        }
                        if ( successPartsToShow.contains( "successful" ) ) {

                            for ( String feature : featuresToShow ) {

                                m."successful"."${feature}" = carType.stats.succeededCars."${feature}".valuez

                            }

                        }
                        if ( successPartsToShow.contains( "failed" ) ) {

                            for ( String feature : featuresToShow ) {

                                m."failed"."${feature}" = carType.stats.failedCars."${feature}".valuez

                            }

                        }

                    }

                }

            }

        }

        return m
    }

    public File createDataChartFileForFillingStationUsage( m ) {

        final BoxAndWhiskerCategoryDataset dataset = createTimeSampleFillingStationDataset( m );

        final CategoryAxis xAxis = new CategoryAxis("Filling station type");
        final NumberAxis yAxis = new NumberAxis("time in [ h ]");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox( true );
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(
                "Time Usage of Filling Stations",
                new Font("SansSerif", Font.BOLD, 14),
                plot,
                true
        );


        final ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection() );
        UUID uuid = UUID.randomUUID();
        final File file = new File( "/tmp/emobile-time-filling-stats-${uuid}.png" );
        ChartUtilities.saveChartAsPNG( file, chart, 1300, 700, info );

        return file;

    }

    public File createDataChartFileForTime( m ) {
        final BoxAndWhiskerCategoryDataset dataset = createTimeSampleDataset( m );

        final CategoryAxis xAxis = new CategoryAxis("Car Type");
        final NumberAxis yAxis = new NumberAxis("time in [ h ]");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(true);
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(
                "Time Usage for Experiment",
                new Font("SansSerif", Font.BOLD, 14),
                plot,
                true
        );


        final ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection() );
        UUID uuid = UUID.randomUUID();
        final File file = new File( "/tmp/emobile-time-stats-${uuid}.png" );
        ChartUtilities.saveChartAsPNG( file, chart, 1300, 700, info );

        return file;
    }

    public File createDataChartFileForDetourTimeDetails( m, Integer searchLimit, boolean withLoading ) {

        final BoxAndWhiskerCategoryDataset dataset = createDetourTimeDetailSampleDataset( m );

        final CategoryAxis xAxis = new CategoryAxis("Planned Distance Group [ km ]");

        final NumberAxis yAxis = new NumberAxis("detour time related to planned time in [ % ]");


        // yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox( true );
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        String loadingIncluded = "Loading Time is included";
        if ( !withLoading ) {
            loadingIncluded = "Loading Time is not included";
        }

        final JFreeChart chart = new JFreeChart(
                "Percentage Detour Time Grouped By Planned Distance Class\nWith Search Limit of: ${searchLimit} %\n${loadingIncluded}",
                // "Distance Details for Experiment: ${m.carType} \nSuccess-Rate: ${m.successRate} %\nFilling-Stations: ${m.fillingStations}\nFilling-Station-Type: ${m.fillingStationType}\nSearching started below of: ${m.relativeSearchLimit} %",
                new Font("SansSerif", Font.BOLD, 12),
                plot,
                true
        );


        final ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection() );
        UUID uuid = UUID.randomUUID();
        final File file = new File( "/tmp/emobile-detour-time-details-stats-${uuid}.png" );
        ChartUtilities.saveChartAsPNG( file, chart, 1300, 700, info );

        return file;

    }

    public File createDataChartFileForDetourDetails( m, Integer searchLimit ) {

        final BoxAndWhiskerCategoryDataset dataset = createDetourDetailSampleDataset( m );

        final CategoryAxis xAxis = new CategoryAxis("Planned Distance Group [ km ]");

        final NumberAxis yAxis = new NumberAxis("detour distance related to planned distance in [ % ]");


        // yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox( true );
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(
                "Percentage Detour Grouped By Planned Distance Class\nWith Search Limit of: ${searchLimit} %",
                // "Distance Details for Experiment: ${m.carType} \nSuccess-Rate: ${m.successRate} %\nFilling-Stations: ${m.fillingStations}\nFilling-Station-Type: ${m.fillingStationType}\nSearching started below of: ${m.relativeSearchLimit} %",
                new Font("SansSerif", Font.BOLD, 12),
                plot,
                true
        );


        final ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection() );
        UUID uuid = UUID.randomUUID();
        final File file = new File( "/tmp/emobile-detour-details-stats-${uuid}.png" );
        ChartUtilities.saveChartAsPNG( file, chart, 1300, 700, info );

        return file;

    }

    public File createDataChartFileForDistanceDetails( m, String timeDistanceOption ) {

        final BoxAndWhiskerCategoryDataset dataset = createDistanceDetailSampleDataset( m, timeDistanceOption );

        final CategoryAxis xAxis = new CategoryAxis("Planned Distance Group [ km ]");

        final NumberAxis yAxis
        if ( timeDistanceOption.equals( "distance" ) ) {
            yAxis= new NumberAxis("distance in [ km ]");
        } else {
            yAxis = new NumberAxis("time in [ h ]");
        }


        // yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox( true );
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart

        if ( timeDistanceOption.equals( "distance" ) ) {
            chart = new JFreeChart(
                    "Distance Details for Experiment: ${m.carType} \nSuccess-Rate: ${m.successRate} %\nFilling-Stations: ${m.fillingStations}\nFilling-Station-Type: ${m.fillingStationType}\nSearching started below of: ${m.relativeSearchLimit} %",
                    new Font("SansSerif", Font.BOLD, 12),
                    plot,
                    true
            );
        } else {
            chart = new JFreeChart(
                    "Time Details for Experiment: ${m.carType} \nSuccess-Rate: ${m.successRate} %\nFilling-Stations: ${m.fillingStations}\nFilling-Station-Type: ${m.fillingStationType}\nSearching started below of: ${m.relativeSearchLimit} %",
                    new Font("SansSerif", Font.BOLD, 12),
                    plot,
                    true
            );
        }



        final ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection() );
        UUID uuid = UUID.randomUUID();
        final File file = new File( "/tmp/emobile-${timeDistanceOption}-details-stats-${uuid}.png" );
        ChartUtilities.saveChartAsPNG( file, chart, 1300, 700, info );

        return file;

    }

    public File createDataChartFileForDistance( m ) {
        final BoxAndWhiskerCategoryDataset dataset = createDistanceSampleDataset( m );

        final CategoryAxis xAxis = new CategoryAxis("Car Type");
        final NumberAxis yAxis = new NumberAxis("distance in [ km ]");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox( true );
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(
                "Distances for Experiment",
                new Font("SansSerif", Font.BOLD, 14),
                plot,
                true
        );


        final ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection() );
        UUID uuid = UUID.randomUUID();
        final File file = new File( "/tmp/emobile-distance-stats-${uuid}.png" );
        ChartUtilities.saveChartAsPNG( file, chart, 1300, 700, info );

        return file;
    }


    public File createDataChartFile( m ) {

        final BoxAndWhiskerCategoryDataset dataset = createSampleDataset( m );

        final CategoryAxis xAxis = new CategoryAxis("Car Type");
        final NumberAxis yAxis = new NumberAxis("Value");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox( true );
        renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(
                "Box-and-Whisker Demo",
                new Font("SansSerif", Font.BOLD, 14),
                plot,
                true
        );


        final ChartRenderingInfo info = new ChartRenderingInfo( new StandardEntityCollection() );
        UUID uuid = UUID.randomUUID();
        final File file = new File( "/tmp/emobile-stats-${uuid}.png" );
        ChartUtilities.saveChartAsPNG( file, chart, 1300, 700, info );

        return file;

    }

    private static BoxAndWhiskerCategoryDataset createSampleDataset( m ) {

        // series per category
        final int seriesCount = 5

        // category = carType
        final int categoryCount = m.carTypes.size();

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        m.carTypes.each { carTypeGroup ->

            dataset.add( (List) carTypeGroup.kmDrivenList , "real distance driven" , (String) carTypeGroup.carType )
            dataset.add( (List) carTypeGroup.plannedDistanceList , "planned distance " , (String) carTypeGroup.carType )
            dataset.add( (List) carTypeGroup.realTimeUsedList , "real time used" , (String) carTypeGroup.carType )
            dataset.add( (List) carTypeGroup.plannedTimeUsedList , "planned distance driven" , (String) carTypeGroup.carType )
            dataset.add( (List) carTypeGroup.timeForLoadingList , "time for loading" , (String) carTypeGroup.carType )

        }

        return dataset;




    }

    private static BoxAndWhiskerCategoryDataset createTimeSampleFillingStationDataset( m ) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        m.fillingTypes.each { fillingType ->

            List<Long> realTimeUsed = new ArrayList<Long>()
            fillingType.timeInUseList.each { Long d ->
                realTimeUsed <<  Math.round( ( d / ( 60 * 60 ) ) )
            }

            dataset.add( (List) realTimeUsed , "time used" , "All" )

        }

        return dataset;

    }

    private static BoxAndWhiskerCategoryDataset createTimeSampleDataset( m ) {

        // series per category
        final int seriesCount = 5

        // category = carType
        final int categoryCount = m.carTypes.size();

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        m.carTypes.each { carTypeGroup ->

            List<Long> realTimeUsed = new ArrayList<Long>()
            carTypeGroup.realTimeUsedList.each { Long d ->
                realTimeUsed <<  Math.round( ( d / ( 60 * 60 ) ) )
            }
            List<Long> plannedTimeUsed = new ArrayList<Long>()
            carTypeGroup.plannedTimeUsedList.each { Long d ->
                plannedTimeUsed <<  Math.round( ( ( d / ( 60 * 60 ) ) ) )
            }
            List<Long> timeForLoading = new ArrayList<Long>()
            carTypeGroup.timeForLoadingList.each { Long d ->
                timeForLoading <<  Math.round( ( d / ( 60 * 60 ) ) )
            }

            // dataset.add( (List) carTypeGroup.realTimeUsedList , "real time used" , (String) carTypeGroup.carType )
            dataset.add( (List) realTimeUsed , "real time used" , (String) carTypeGroup.carType )
            dataset.add( (List) plannedTimeUsed , "planned time" , (String) carTypeGroup.carType )
            dataset.add( (List) timeForLoading , "loading time" , (String) carTypeGroup.carType )

        }

        return dataset;
    }

    private static BoxAndWhiskerCategoryDataset createDetourTimeDetailSampleDataset( m ) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        m.distanceClasses.each { distanceClass ->

            distanceClass.types.each { type ->

                type.counts.each { count ->

                    // log.error( "${count.count} : ${count.valueList}" )

                    if ( count.valueList.size > 0 ) {

                        dataset.add( ( List ) count.valueList, "${count.count} of type ${type.type}", ( String ) distanceClass.distanceClass )

                    }


                }



            }

        }


        return dataset;

    }

    private static BoxAndWhiskerCategoryDataset createDetourDetailSampleDataset( m ) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        m.distanceClasses.each { distanceClass ->

            distanceClass.types.each { type ->

                type.counts.each { count ->

                    // log.error( "${count.count} : ${count.valueList}" )

                    if ( count.valueList.size > 0 ) {

                        dataset.add( ( List ) count.valueList, "${count.count} of type ${type.type}", ( String ) distanceClass.distanceClass )

                    }


                }



            }

        }


        return dataset;

    }

    private static BoxAndWhiskerCategoryDataset createDistanceDetailSampleDataset( m, String timeDistanceOption ) {

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        m.details.each { detail ->

            if ( detail.targetReached > 0 ) {

                if ( timeDistanceOption.equals( "distance" ) ) {
                    dataset.add( ( List ) detail.realDistances, "real distances", ( String ) detail.plannedDistanceClass );
                    dataset.add( ( List ) detail.plannedDistanceList, "planned distances", ( String ) detail.plannedDistanceClass );
                    dataset.add( ( List ) detail.diffDistanceList, "distance diff", ( String ) detail.plannedDistanceClass );
                } else {

                    def realTimeList = []
                    def plannedTimeList = []
                    def timeForLoadingList = []
                    def diffRealPlannedTimeList = []

                    detail.realTimeList.each { realTimeList << it / ( 60*60 ) }
                    detail.plannedTimeList.each { plannedTimeList << it / ( 60*60 ) }
                    detail.timeForLoadingList.each { timeForLoadingList << it / ( 60*60 ) }
                    detail.diffRealPlannedTimeList.each { diffRealPlannedTimeList << it / ( 60*60 ) }

                    dataset.add( ( List ) realTimeList, "real time", ( String ) detail.plannedDistanceClass );
                    dataset.add( ( List ) plannedTimeList, "planned time", ( String ) detail.plannedDistanceClass );
                    dataset.add( ( List ) timeForLoadingList, "loading time", ( String ) detail.plannedDistanceClass );
                    dataset.add( ( List ) diffRealPlannedTimeList, "over time", ( String ) detail.plannedDistanceClass );
                }

            }

        }

        return dataset;
    }

    private static BoxAndWhiskerCategoryDataset createDistanceSampleDataset( m ) {

        // series per category
        final int seriesCount = 5

        // category = carType
        final int categoryCount = m.carTypes.size();

        final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

        m.carTypes.each { carTypeGroup ->

            dataset.add( (List) carTypeGroup.kmDrivenList , "real distance driven" , (String) carTypeGroup.carType )
            dataset.add( (List) carTypeGroup.plannedDistanceList , "planned distance " , (String) carTypeGroup.carType )

        }

        return dataset;




    }


}
