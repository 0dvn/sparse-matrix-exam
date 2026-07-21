package exam;

import exam.QuadTree.QuadTreeMatrix;

public class Main {

    public static void main(String[] args) {
        QuadTreeMatrix matrix = QuadTreeMatrix.fromFile("matrix.txt");

        double determinant = DeterminantCalculator.calculate(matrix);
        System.out.println("Determinant: " + determinant);

        matrix.transposeInPlace();
        double transposedDeterminant = DeterminantCalculator.calculate(matrix);
        System.out.println("Transposed Determinant: " + transposedDeterminant);

        if (Math.abs(determinant - transposedDeterminant) < 1e-10) {
            System.out.println("Verification: Determinants are equal");
        } else {
            System.out.println("Verification: Determinants differ");
        }
    }
}
