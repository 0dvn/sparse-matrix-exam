package exam;

import java.util.BitSet;

public class DeterminantCalculator {

    private double[][][] cache;
    private int[] sortedRows;
    private BitSet availableRows;
    private BitSet availableCols;
    private int n;

    public static double calculate(SparseMatrix matrix) {
        DeterminantCalculator calc = new DeterminantCalculator(matrix);
        return calc.detRecursive();
    }

    private DeterminantCalculator(SparseMatrix matrix) {
        this.n = matrix.getDimension();
        this.cache = new double[n][][];
        for (int i = 0; i < n; i++) {
            cache[i] = matrix.getNonZeroInRow(i);
            if (cache[i] == null) {
                this.availableRows = new BitSet(n);
                this.availableCols = new BitSet(n);
                return;
            }
        }

        this.sortedRows = new int[n];
        for (int i = 0; i < n; i++) sortedRows[i] = i;
        sortRowsByNonZeroCount(sortedRows, cache);

        this.availableRows = new BitSet(n);
        this.availableRows.set(0, n);
        this.availableCols = new BitSet(n);
        this.availableCols.set(0, n);
    }

    private static void sortRowsByNonZeroCount(int[] arr, double[][][] cache) {
        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int keyLen = cache[key].length;
            int j = i - 1;
            while (j >= 0 && cache[arr[j]].length > keyLen) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private double detRecursive() {
        if (n == 1) {
            int row = availableRows.nextSetBit(0);
            int col = availableCols.nextSetBit(0);
            if (cache[row] != null) {
                for (double[] element : cache[row]) {
                    if ((int) element[0] == col) return element[1];
                }
            }
            return 0;
        }

        int pointer = 0;
        while (!availableRows.get(sortedRows[pointer])) {
            pointer++;
        }

        int bestRow = sortedRows[pointer];
        BitSet savedRows = (BitSet) availableRows.clone();
        BitSet savedCols = (BitSet) availableCols.clone();
        int savedN = n;

        availableRows.clear(bestRow);
        n--;

        int posI = savedRows.get(0, bestRow).cardinality();

        double det = 0;
        for (double[] element : cache[bestRow]) {
            int col = (int) element[0];
            double value = element[1];

            if (savedCols.get(col)) {
                int posJ = savedCols.get(0, col).cardinality();
                int sign = ((posI + posJ) % 2 == 0) ? 1 : -1;

                BitSet newCols = (BitSet) savedCols.clone();
                newCols.clear(col);
                availableCols = newCols;

                det += sign * value * detRecursive();
            }
        }

        availableRows = savedRows;
        availableCols = savedCols;
        n = savedN;

        return det;
    }
}
