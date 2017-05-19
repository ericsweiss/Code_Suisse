import java.awt.Color;
import java.awt.BasicStroke;
import java.io.File;
import java.io.IOException;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class XYLineChart extends ApplicationFrame {

    public XYLineChart(String applicationTitle, String chartTitle, String[] symbols, Date start, Date end) {
        super(applicationTitle);
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Timestamp",
                "Price",
                createDataset(symbols, start, end),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1100, 800));
        final XYPlot plot = xylineChart.getXYPlot();

        int width = 900;
        int height = 600;
        File priceMovementChart = new File("Price_Movements.jpeg");
        try {
            ChartUtilities.saveChartAsJPEG(priceMovementChart, xylineChart, width, height);
        } catch(IOException e) {
            System.err.print("Error in saving jpeg");
        }


        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(3.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);



    }

    private XYDataset createDataset(String[] symbols, Date start, Date end) {

        final XYSeriesCollection dataset = new XYSeriesCollection();

        for(int i = 0; i < symbols.length; i++) {
            final XYSeries met = new XYSeries(symbols[i]);
            Company company = Main.getCompanyHashMap().get(symbols[i]);

            Transaction iteratorA = company.lookupTransaction(start);
            Transaction lastA = company.lookupTransaction(end);

            if(iteratorA != null) {
                int initialIndex = company.getDateIndex().get(iteratorA.getTimestamp().getDate());
                int index = initialIndex;
                while (iteratorA.getTimestamp().compareTo(lastA.getTimestamp()) <= 0) {
                    met.add(iteratorA.getTimestamp().getDate(), iteratorA.getPrice());
                    index++;
                    iteratorA = company.getTransactionList().get(index);
                }
            }

            dataset.addSeries(met);
        }

        return dataset;
    }
}
