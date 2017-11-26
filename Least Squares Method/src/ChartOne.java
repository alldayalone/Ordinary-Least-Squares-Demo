import org.jfree.data.xy.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ChartOne extends JPanel {
    private ArrayList<MyPoint> myPoints         = new ArrayList<>();
    private XYSeriesCollection seriesCollection = new XYSeriesCollection();
    private JFreeChart chart                    = createChart(seriesCollection);
    private ChartPanel chartPanel               = new ChartPanel(chart);

    private Model model = new Model();

    private int selectedPointIndex = -1;

    public ChartOne() {
        super();

        //...Размещаем график на фрейме
        add(chartPanel);
    }

    //...Генерируем графики, отрисовываем, помещаем на панель
    void drawChart() {
        seriesCollection = createSeriesCollection();

        chart = createChart(seriesCollection);

        chartPanel.setChart(chart);
    }

    //...Генерация точек
    private XYSeriesCollection createSeriesCollection() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        model.approximate(myPoints);

        XYSeries selected = new XYSeries("Selected");
        if (isSelected()) {
            selected.add(myPoints.get(selectedPointIndex).getX(), myPoints.get(selectedPointIndex).getY());
        }

        XYSeries experiment = new XYSeries("Experiment");
        for (MyPoint i : myPoints)
            experiment.add(i.getX(), i.getY());

        XYSeries approximation = new XYSeries("Approximation");
        for(double i = getMinimumX() - 2; i < getMaximumX() + 2; i += 0.5) {
            double y = model.getFunction(i);
            approximation.add(i, y);
        }

        xySeriesCollection.addSeries(selected);
        xySeriesCollection.addSeries(approximation);
        xySeriesCollection.addSeries(experiment);

        return xySeriesCollection;
    }

    //...Рисуем графики
    private JFreeChart createChart(XYSeriesCollection xySeriesCollection) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "PlotDemo",
                "X",
                "Y",
                xySeriesCollection,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        chart.setBackgroundPaint(Color.white);

        //...Устанавливаем правила для серий
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(2, false);
        renderer.setSeriesShapesVisible(1, false);
        renderer.setSeriesPaint(0, Color.orange);
        renderer.setSeriesPaint(1, Color.red);
        renderer.setSeriesPaint(2, Color.blue);

        XYPlot plot = chart.getXYPlot();
        plot.setOutlinePaint(Color.cyan);
        plot.setRenderer(renderer);

        return chart;
    }

    void addPoint(MyPoint p) {
        myPoints.add(p);
    }

    void deletePoint(int i) {
        myPoints.remove(i);

        //...При удалении панельки она сразу пропадает, события "мышь покинула панель" не произойдет, так что
        //...делаем это вручную
        deSelectPoint();
    }

    void selectPoint(int i) {
        selectedPointIndex = i;
        drawChart();
    }

    void deSelectPoint() {
        selectedPointIndex = -1;
        drawChart();
    }

    void setModelBasis(int basis) {
        model.setBasis(basis);
    }

    boolean isSelected() {
        return selectedPointIndex > -1;
    }

    int getSelectedPointIndex() {
        return selectedPointIndex;
    }

    ChartPanel getChartPanel() {
        return chartPanel;
    }

    ArrayList<MyPoint> getMyPoints() {
        return myPoints;
    }

    private double getMinimumX() {
        double ret = myPoints.get(0).getX();
        for(MyPoint i : myPoints) {
            if( i.getX() < ret) ret = i.getX();
        }
        return ret;
    }

    private double getMaximumX() {
        double ret = myPoints.get(0).getX();
        for(MyPoint i : myPoints) {
            if( i.getX() > ret) ret = i.getX();
        }
        return ret;
    }
}

