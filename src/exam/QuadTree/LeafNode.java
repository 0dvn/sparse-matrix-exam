package exam.QuadTree;

public class LeafNode extends QuadTreeNode {
    int row;
    int col;
    double value;

    LeafNode(int row, int col, double value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }
}
