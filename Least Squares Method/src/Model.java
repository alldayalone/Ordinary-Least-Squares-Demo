import java.util.ArrayList;

class Model {
    private double[] function;
    private int basis = 2;

    void approximate (ArrayList<MyPoint> points) {
        approximate(convertToArray(points));
    }

    private double[][] convertToArray(ArrayList<MyPoint> dots) {
        double xyTable[][] = new double[dots.size()][2];
        for(int i = 0; i < dots.size(); i++) {
            xyTable[i][0] = dots.get(i).getX();
            xyTable[i][1] = dots.get(i).getY();
        }
        return xyTable;
    }

    private void approximate (double[][] xyTable) {
        function = Gauss(getMatrix(xyTable));
    }

    private double[][] getMatrix(double[][] xyTable) {
        double matrix[][] = new double[basis][basis + 1];
        for (int i = 0; i < basis; i++)
            for (int j = 0; j < basis + 1; j++)
                matrix[i][j] = 0;

        for (int i = 0; i < basis; i++) {
            for (int j = 0; j < basis; j++) {
                double sumA = 0, sumB = 0;
                for (int k = 0; k < xyTable.length; k++) {
                    sumA += Math.pow(xyTable[k][0], i) * Math.pow(xyTable[k][0], j);
                    sumB += xyTable[k][1] * Math.pow(xyTable[k][0], i);
                }
                matrix[i][j] = sumA;
                matrix[i][basis] = sumB;
            }
        }

        return matrix;
    }

    private double[] Gauss(double[][] matrix) {
        //Прямой ход
        for(int k = 0; k < matrix.length; k++) {
            for (int i = k; i < matrix.length; i++) {
                double elem = matrix[i][k];
                for (int j = k; j < matrix[i].length; j++)
                    matrix[i][j] /= elem;
            }
            for (int i = k + 1; i < matrix.length; i++)
                for (int j = k; j < matrix[i].length; j++)
                    matrix[i][j] -= matrix[k][j];
        }
        //Обратный ход
        for (int i = matrix.length - 1; i > 0; i--) {
            double xi = matrix[i][matrix[i].length-1];
            for (int k = i - 1; k >= 0; k--) {
                matrix[k][matrix[k].length - 1] -= xi * matrix[k][i];
                matrix[k][i] = 0;
            }
        }
        double[] ret = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++)
            ret[i] = matrix[i][matrix[i].length - 1];
        return ret;
    }

    double getFunction (double x) {
        double ret = .0;
        for(int i = 0; i < function.length; i++)
            ret += Math.pow(x, i) * function[i];
        return ret;
    }

    void setBasis(int x) {
        basis = x + 1;
    }
}

