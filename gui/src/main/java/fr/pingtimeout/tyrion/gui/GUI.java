package fr.pingtimeout.tyrion.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StackedXYBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {


    public GUI() {
//        ChartPanel chartPanel = new ChartPanel(createTaskChart());
        ChartPanel chartPanel = new ChartPanel(createStackedXYChart());

        this.add(chartPanel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }


    private static TableXYDataset createTableDataset() {
        DefaultTableXYDataset defaulttablexydataset = new DefaultTableXYDataset();
        XYSeries xyseries = new XYSeries("Series 1", true, false);
        xyseries.add(1, 5);
        xyseries.add(2, 15.5);
        xyseries.add(3, 9.5);
//        xyseries.add(4, 7.5);
        defaulttablexydataset.addSeries(xyseries);
        XYSeries xyseries1 = new XYSeries("Series 2", true, false);
        xyseries1.add(1, 5);
        xyseries1.add(2, 15.5);
        xyseries1.add(3, 9.5);
        xyseries1.add(4, 3.5);
        defaulttablexydataset.addSeries(xyseries1);
        return defaulttablexydataset;
    }

    private static JFreeChart createStackedXYChart() {
        TableXYDataset tablexydataset = createTableDataset();

        NumberAxis thread = new NumberAxis("Thread");
        thread.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        NumberAxis millis = new NumberAxis("Millis");
        StackedXYBarRenderer stackedxybarrenderer = new StackedXYBarRenderer(0.1);
        stackedxybarrenderer.setDrawBarOutline(false);
        XYPlot xyplot = new XYPlot(tablexydataset, thread, millis, stackedxybarrenderer);
        JFreeChart jfreechart = new JFreeChart("Stacked XY Bar Chart Demo 1", xyplot);

        ChartUtilities.applyCurrentTheme(jfreechart);
        return jfreechart;
    }

    private static JFreeChart createTaskChart() {
        XYTaskDataset intervalxydataset = new XYTaskDataset(createTasks());
        JFreeChart jfreechart = ChartFactory.createXYBarChart("XYTaskDatasetDemo1", "Resource", false, "Timing", intervalxydataset, PlotOrientation.HORIZONTAL, true, false, false);
        jfreechart.setBackgroundPaint(Color.white);
        XYPlot xyplot = (XYPlot) jfreechart.getPlot();
        xyplot.setRangePannable(true);
        SymbolAxis symbolaxis = new SymbolAxis("Series", new String[]{
                "Team A", "Team B", "Team C", "Team D"
        });
        symbolaxis.setGridBandsVisible(false);
        xyplot.setDomainAxis(symbolaxis);
        XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot.getRenderer();

        xyplot.setRangeAxis(new DateAxis("Timing"));

        xybarrenderer.setUseYInterval(true);
        xybarrenderer.setSeriesPaint(0, Color.BLACK);
        xybarrenderer.setSeriesPaint(1, Color.MAGENTA);
        ChartUtilities.applyCurrentTheme(jfreechart);
        return jfreechart;
    }

    private static TaskSeriesCollection createTasks() {
        TaskSeriesCollection taskseriescollection = new TaskSeriesCollection();
        TaskSeries taskseries = new TaskSeries("Team A");
        taskseries.add(new Task("T1a", new Hour(11, new Day())));
        taskseries.add(new Task("T1b", new Hour(14, new Day())));
        taskseries.add(new Task("T1c", new Hour(16, new Day())));
        TaskSeries taskseries1 = new TaskSeries("Team B");
        taskseries1.add(new Task("T2a", new Hour(13, new Day())));
        taskseries1.add(new Task("T2b", new Hour(19, new Day())));
        taskseries1.add(new Task("T2c", new Hour(21, new Day())));
        TaskSeries taskseries2 = new TaskSeries("Team C");
        taskseries2.add(new Task("T3a", new Hour(13, new Day())));
        taskseries2.add(new Task("T3b", new Hour(19, new Day())));
        taskseries2.add(new Task("T3c", new Hour(21, new Day())));
        TaskSeries taskseries3 = new TaskSeries("Team D");
        taskseries3.add(new Task("T4a", new Day()));
        taskseriescollection.add(taskseries);
        taskseriescollection.add(taskseries1);
        taskseriescollection.add(taskseries2);
        taskseriescollection.add(taskseries3);
        return taskseriescollection;
    }

    public static void main(String[] args) {
        new GUI();
    }
}