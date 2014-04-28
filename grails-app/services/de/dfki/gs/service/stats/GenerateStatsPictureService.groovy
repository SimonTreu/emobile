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

    public File createDataChartFileForFillingStationUsage( m ) {

        final BoxAndWhiskerCategoryDataset dataset = createTimeSampleFillingStationDataset( m );

        final CategoryAxis xAxis = new CategoryAxis("Filling station type");
        final NumberAxis yAxis = new NumberAxis("time in [ h ]");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
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
        renderer.setFillBox(false);
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

    public File createDataChartFileForDistance( m ) {
        final BoxAndWhiskerCategoryDataset dataset = createDistanceSampleDataset( m );

        final CategoryAxis xAxis = new CategoryAxis("Car Type");
        final NumberAxis yAxis = new NumberAxis("distance in [ km ]");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
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
        renderer.setFillBox(false);
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