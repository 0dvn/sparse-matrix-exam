package exam;

public interface SparseMatrix {

    int getDimension();

    double[][] getNonZeroInRow(int row);

    void transposeInPlace();
}
