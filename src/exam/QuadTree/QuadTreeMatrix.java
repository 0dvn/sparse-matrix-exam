package exam.QuadTree;

import exam.SparseMatrix;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class QuadTreeMatrix implements SparseMatrix {

    private int dimension;
    private QuadTreeNode root;

    private QuadTreeMatrix() {
        this.root = null;
    }

    public static QuadTreeMatrix fromFile(String filePath) {
        QuadTreeMatrix matrix = new QuadTreeMatrix();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line == null) return matrix;

            String[] parts = line.trim().split("\\s+");
            matrix.dimension = Integer.parseInt(parts[0]);

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                parts = line.split("\\s+");
                int row = Integer.parseInt(parts[0]) - 1;
                int col = Integer.parseInt(parts[1]) - 1;
                double value = Double.parseDouble(parts[2]);

                if (value != 0) {
                    matrix.root = matrix.insert(matrix.root, row, col, value, 0, matrix.dimension - 1, 0, matrix.dimension - 1);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
        return matrix;
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public double[][] getNonZeroInRow(int row) {
        return getNonZeroInRowHelper(root, row, 0, dimension - 1, 0, dimension - 1);
    }

    private double[][] getNonZeroInRowHelper(QuadTreeNode node, int row,
                                              int topRow, int bottomRow,
                                              int topCol, int bottomCol) {
        if (node == null) return null;

        if (node instanceof LeafNode leaf) {
            if (leaf.row == row) {
                return new double[][]{{leaf.col, leaf.value}};
            }
            return null;
        }

        BranchNode branch = (BranchNode) node;
        int midRow = (topRow + bottomRow) / 2;
        int midCol = (topCol + bottomCol) / 2;

        double[][] result = null;

        if (row <= midRow) {
            result = combine(result, getNonZeroInRowHelper(branch.NW, row, topRow, midRow, topCol, midCol));
            result = combine(result, getNonZeroInRowHelper(branch.NE, row, topRow, midRow, midCol + 1, bottomCol));
        } else {
            result = combine(result, getNonZeroInRowHelper(branch.SW, row, midRow + 1, bottomRow, topCol, midCol));
            result = combine(result, getNonZeroInRowHelper(branch.SE, row, midRow + 1, bottomRow, midCol + 1, bottomCol));
        }

        return result;
    }

    private double[][] combine(double[][] a, double[][] b) {
        if (a == null) return b;
        if (b == null) return a;

        double[][] result = new double[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    @Override
    public void transposeInPlace() {
        root = transposeHelper(root, 0, dimension - 1, 0, dimension - 1);
    }

    private QuadTreeNode transposeHelper(QuadTreeNode node, int topRow, int bottomRow,
                                          int topCol, int bottomCol) {
        if (node == null) return null;

        if (node instanceof LeafNode leaf) {
            return new LeafNode(leaf.col, leaf.row, leaf.value);
        }

        BranchNode branch = (BranchNode) node;
        int midRow = (topRow + bottomRow) / 2;
        int midCol = (topCol + bottomCol) / 2;

        QuadTreeNode newNW = transposeHelper(branch.NW, topRow, midRow, topCol, midCol);
        QuadTreeNode newSE = transposeHelper(branch.SE, midRow + 1, bottomRow, midCol + 1, bottomCol);
        QuadTreeNode newNE = transposeHelper(branch.NE, topRow, midRow, midCol + 1, bottomCol);
        QuadTreeNode newSW = transposeHelper(branch.SW, midRow + 1, bottomRow, topCol, midCol);

        return new BranchNode(newNW, newSW, newNE, newSE);
    }

    private QuadTreeNode insert(QuadTreeNode node, int row, int col, double value,
                                 int topRow, int bottomRow, int topCol, int bottomCol) {
        if (node == null) {
            return new LeafNode(row, col, value);
        }

        if (node instanceof LeafNode leaf) {
            if (leaf.row == row && leaf.col == col) {
                if (value == 0) return null;
                leaf.value = value;
                return leaf;
            }

            BranchNode branch = new BranchNode();
            branch = insertLeaf(branch, leaf.row, leaf.col, leaf.value, topRow, bottomRow, topCol, bottomCol);
            branch = insertLeaf(branch, row, col, value, topRow, bottomRow, topCol, bottomCol);
            return branch;
        }

        BranchNode branch = (BranchNode) node;
        int midRow = (topRow + bottomRow) / 2;
        int midCol = (topCol + bottomCol) / 2;

        if (row <= midRow) {
            if (col <= midCol) {
                branch.NW = insert(branch.NW, row, col, value, topRow, midRow, topCol, midCol);
            } else {
                branch.NE = insert(branch.NE, row, col, value, topRow, midRow, midCol + 1, bottomCol);
            }
        } else {
            if (col <= midCol) {
                branch.SW = insert(branch.SW, row, col, value, midRow + 1, bottomRow, topCol, midCol);
            } else {
                branch.SE = insert(branch.SE, row, col, value, midRow + 1, bottomRow, midCol + 1, bottomCol);
            }
        }

        return branch;
    }

    private BranchNode insertLeaf(BranchNode branch, int row, int col, double value,
                                   int topRow, int bottomRow, int topCol, int bottomCol) {
        int midRow = (topRow + bottomRow) / 2;
        int midCol = (topCol + bottomCol) / 2;

        if (row <= midRow) {
            if (col <= midCol) {
                branch.NW = insert(branch.NW, row, col, value, topRow, midRow, topCol, midCol);
            } else {
                branch.NE = insert(branch.NE, row, col, value, topRow, midRow, midCol + 1, bottomCol);
            }
        } else {
            if (col <= midCol) {
                branch.SW = insert(branch.SW, row, col, value, midRow + 1, bottomRow, topCol, midCol);
            } else {
                branch.SE = insert(branch.SE, row, col, value, midRow + 1, bottomRow, midCol + 1, bottomCol);
            }
        }

        return branch;
    }
}
