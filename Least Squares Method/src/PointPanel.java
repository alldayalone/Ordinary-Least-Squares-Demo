import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

class PointPanel extends JPanel {
    private JTextField x, y;
    private JButton delete;

    PointPanel (Double xValue, Double yValue, List<PointPanel> pointPanelList, View view) {
        PointPanel thisPointPanel = this;

        x = new JTextField(xValue.toString());

        //...Изменение координаты X у существующей точки
        x.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                if(x.getText().length() > 0) {
                    int ind = pointPanelList.indexOf(thisPointPanel);
                    double xValue = Double.parseDouble(x.getText());
                    view.getPlot().getMyPoints().get(ind).setX(xValue);
                    view.getPlot().drawChart();
                }
            }
        });

        y = new JTextField(yValue.toString());

        //...Изменение координаты Y у существующей точки
        y.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                if (y.getText().length() > 0) {
                    int ind = pointPanelList.indexOf(thisPointPanel);
                    view.getPlot().getMyPoints().get(ind).setY(Double.parseDouble(y.getText()));
                    view.getPlot().drawChart();
                }
            }
        });

        delete = new JButton("delete");

        //...Удаляем точку
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.destroyPoint(pointPanelList.indexOf(thisPointPanel));
            }
        });

        setLayout(new GridLayout(1,3));
        setMaximumSize(new Dimension(300, 25));
        add(x);
        add(y);
        add(delete);

        //...Создаем слушатель для всех элементов панельки
        //...При наведении увеличиваемся в размерах и выделяем точку на графике
        MouseAdapter selectPointPanel = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);

                //...Выделение панельки
                select(thisPointPanel);

                //...Отрисовка точки
                view.getPlot().selectPoint(pointPanelList.indexOf(thisPointPanel));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (view.getPlot().isSelected()) {
                    deSelect(thisPointPanel);
                    view.getPlot().deSelectPoint();
                }
            }
        };

        //...Навешаем слушателей на все компоненты панели
        addSelectionListener(selectPointPanel);
    }

    private void addSelectionListener(MouseAdapter mouseAdapter) {
        addMouseListener(mouseAdapter);
        x.addMouseListener(mouseAdapter);
        y.addMouseListener(mouseAdapter);
        delete.addMouseListener(mouseAdapter);
    }

    void destroyPanel(List<PointPanel> pointPanelList, int i) {
        //...Удаляем себя с фрейма
        Container container = getParent();
        container.remove(this);

        //...Отрисовка
        container.validate();
        container.repaint();

        //...И из списка
        pointPanelList.remove(i);
    }


    static void select(JPanel panel) {
        panel.setMaximumSize(new Dimension(300, 51));
        panel.revalidate();
        panel.repaint();
    }

    static void deSelect(JPanel panel) {
        panel.setMaximumSize(new Dimension(300, 25));
        panel.revalidate();
        panel.repaint();
    }

    public JPanel getPanel() {
        return this;
    }
}
