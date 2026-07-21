package exam.QuadTree;

public class BranchNode extends QuadTreeNode {
    QuadTreeNode NW;
    QuadTreeNode NE;
    QuadTreeNode SW;
    QuadTreeNode SE;

    BranchNode() {
    }

    BranchNode(QuadTreeNode NW, QuadTreeNode NE, QuadTreeNode SW, QuadTreeNode SE) {
        this.NW = NW;
        this.NE = NE;
        this.SW = SW;
        this.SE = SE;
    }
}
