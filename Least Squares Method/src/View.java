import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.plot.XYPlot;


public class View {
    private JComboBox functionBasis;
    private JTextField yTextField;
    private JTextField xTextField;
    private JButton addButton;
    private JButton startButton;
    private JPanel mainPanel;
    private ChartOne Plot;
    private JScrollPane scrollPane;
    private JPanel pointsPanel;
    private List<PointPanel> pointPanelList = new ArrayList<>();

    private View() {
        //...Отрисовка графика по кнопке
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Plot.drawChart();
                //frame.pack();
                //JOptionPane.showMessageDialog(null, "График построен!");
            }
        });

        //...Возможность изменения и Инициализация степени полинома
        functionBasis.setSelectedIndex(1);
        functionBasis.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Plot.setModelBasis(functionBasis.getSelectedIndex());
            }
        });

        pointsPanel.setLayout(new BoxLayout(pointsPanel, BoxLayout.Y_AXIS));

        //...Добавление точки и соответствующей панели
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double chartX = Double.parseDouble(xTextField.getText());
                double chartY = Double.parseDouble(yTextField.getText());

                addPointPanel(chartX, chartY);
                Plot.addPoint(new MyPoint(chartX, chartY));
                Plot.drawChart();
            }
        });

        //////////////////////////////////////////////////////////////////////
        /*
            Отслеживает следующие действия пользователя:
            1. Добавление новой точки
            2. Выделение точки при наведении
            (3. и снятие выделения при снятии наведения)
         */
        Plot.getChartPanel().addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                /*
                ChartEntity ce = event.getEntity();
                if (ce instanceof XYItemEntity) {
                    XYItemEntity e = (XYItemEntity) ce;
                    XYDataset d = ((XYItemEntity) ce).getDataset();
                    int i = ((XYItemEntity) ce).getItem();
                    double chartpx = d.getXValue(0, i);
                    double chartpy = d.getYValue(0, i);
                    //JOptionPane.showMessageDialog(null, "X:" + chartpx + ", Y:" + chartpy, "ChartPX", 0);
                }
                */

                //...Получаем координаты клика относительно осей плоскости
                Point2D po = Plot.getChartPanel().translateScreenToJava2D(event.getTrigger().getPoint());
                Rectangle2D plotArea = Plot.getChartPanel().getScreenDataArea();
                XYPlot plot = (XYPlot) Plot.getChartPanel().getChart().getPlot();
                double chartX = plot.getDomainAxis().java2DToValue(po.getX(), plotArea, plot.getDomainAxisEdge());
                double chartY = plot.getRangeAxis().java2DToValue(po.getY(), plotArea, plot.getRangeAxisEdge());

                //...Округляем до тысячных
                chartX = new BigDecimal(chartX).setScale(3, RoundingMode.UP).doubleValue();
                chartY = new BigDecimal(chartY).setScale(3, RoundingMode.UP).doubleValue();

                //...Если клик был на координатной плоскости, добавляем точку, ее панель и перерисовываем график
                if ( chartX > Plot.getChartPanel().getChart().getXYPlot().getDomainAxis().getLowerBound()
                        && chartY > Plot.getChartPanel().getChart().getXYPlot().getRangeAxis().getLowerBound()) {
                    addPointPanel(chartX, chartY);
                    Plot.addPoint(new MyPoint(chartX, chartY));
                    Plot.drawChart();
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent arg0) {
                //...Получаем координаты текущего расположения курсора относительно осей
                Point2D po = Plot.getChartPanel().translateScreenToJava2D(arg0.getTrigger().getPoint());
                Rectangle2D plotArea = Plot.getChartPanel().getScreenDataArea();
                XYPlot plot = (XYPlot) Plot.getChartPanel().getChart().getPlot();
                double chartX = plot.getDomainAxis().java2DToValue(po.getX(), plotArea, plot.getDomainAxisEdge());
                double chartY = plot.getRangeAxis().java2DToValue(po.getY(), plotArea, plot.getRangeAxisEdge());

                //...Округляем до тысячных
                chartX = new BigDecimal(chartX).setScale(3, RoundingMode.UP).doubleValue();
                chartY = new BigDecimal(chartY).setScale(3, RoundingMode.UP).doubleValue();

                //...Длины осей
                double xLength = Plot.getChartPanel().getChart().getXYPlot().getDomainAxis().getRange().getLength();
                double yLength = Plot.getChartPanel().getChart().getXYPlot().getRangeAxis().getRange().getLength();

                //...Вычисляем погрешность при наведении в зависимости от масштаба графика
                double xEpsilon = xLength / 110;
                double yEpsilon = yLength / 110;

                //...Сверяем текущее расположение курсора со всеми точками
                boolean isFound = false;
                for(MyPoint i : Plot.getMyPoints()) {

                    //...Если курсор достаточно близок к точке, выделяем ее
                    if (Math.abs(chartX - i.getX()) < xEpsilon && Math.abs(chartY - i.getY()) < yEpsilon) {

                        //...Проверка на случай, когда несколько точки находятся слишком близко
                        if (Plot.isSelected()) {
                            int ind = Plot.getSelectedPointIndex();
                            PointPanel.deSelect(pointPanelList.get(ind).getPanel());
                            Plot.deSelectPoint();
                        }

                        //...Выделение
                        PointPanel.select(pointPanelList.get(Plot.getMyPoints().indexOf(i)).getPanel());
                        Plot.selectPoint(Plot.getMyPoints().indexOf(i));
                        isFound = true;
                    }
                }

                //Если курсор не наведен на точку, снимаем выделение
                if (!isFound && Plot.isSelected()) {
                    int ind = Plot.getSelectedPointIndex();
                    PointPanel.deSelect(pointPanelList.get(ind).getPanel());
                    Plot.deSelectPoint();
                }

            }
        });
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        //...Задание стиля
        initLookAndFeel();

        //...Создание фрейма
        JFrame frame = new JFrame("LeastSquaresMethodDemo");
        frame.setBounds(0, 0, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new View().mainPanel);

        //...Выводим окно
        frame.pack();
        frame.setVisible(true);
    }

    private static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Can't use the specified look and feel on this platform.");
        } catch (Exception e) {
            System.err.println("Couldn't get specified look and feel, for some reason.");
        }
    }

    private void addPointPanel(double x, double y) {
        PointPanel p = new PointPanel(x, y, pointPanelList, this);

        //...Добавляем в список
        pointPanelList.add(p);

        //...Добавляем на фрейм
        pointsPanel.add(p.getPanel());
        scrollPane.revalidate();
    }

    void destroyPoint(int i) {
        pointPanelList.get(i).destroyPanel(pointPanelList, i);
        Plot.deletePoint(i);
    }

    ChartOne getPlot() {
        return Plot;
    }
}


