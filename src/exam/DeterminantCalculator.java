package exam;

public class DeterminantCalculator {

    private double[][][] cache;
    private int[] sortedRows;
    private long availableRows;
    private long availableCols;
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
                this.availableRows = 0;
                this.availableCols = 0;
                return;
            }
        }

        this.sortedRows = new int[n];
        for (int i = 0; i < n; i++) sortedRows[i] = i;
        sortRowsByNonZeroCount(sortedRows, cache);

        this.availableRows = (1L << n) - 1;
        this.availableCols = (1L << n) - 1;
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
            int row = Long.numberOfTrailingZeros(availableRows);
            int col = Long.numberOfTrailingZeros(availableCols);
            if (cache[row] != null) {
                for (double[] element : cache[row]) {
                    if ((int) element[0] == col) return element[1];
                }
            }
            return 0;
        }

        int pointer = 0;
        while ((availableRows & (1L << sortedRows[pointer])) == 0) {
            pointer++;
        }

        int bestRow = sortedRows[pointer];
        long savedRows = availableRows;
        long savedCols = availableCols;
        int savedN = n;

        availableRows &= ~(1L << bestRow);
        n--;

        int posI = Long.bitCount(savedRows & ((1L << bestRow) - 1));

        double det = 0;
        for (double[] element : cache[bestRow]) {
            int col = (int) element[0];
            double value = element[1];

            if ((savedCols & (1L << col)) != 0) {
                int posJ = Long.bitCount(savedCols & ((1L << col) - 1));
                int sign = ((posI + posJ) % 2 == 0) ? 1 : -1;

                availableCols = savedCols & ~(1L << col);

                det += sign * value * detRecursive();
            }
        }

        availableRows = savedRows;
        availableCols = savedCols;
        n = savedN;

        return det;
    }
}
