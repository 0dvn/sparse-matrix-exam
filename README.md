# Sparse Matrix — QuadTree Implementation

## Overview

This project implements a sparse square matrix using a QuadTree data structure, with Laplace's determinant calculation and in-place transpose.

The solution reads a 50x50 matrix from `matrix.txt`, computes its determinant, transposes it in place, and verifies that the determinant remains unchanged.

## Files

| File | Purpose |
|------|---------|
| `src/exam/SparseMatrix.java` | Interface. Defines the contract any sparse matrix must follow. |
| `src/exam/DeterminantCalculator.java` | Laplace's algorithm. Works with any `SparseMatrix`. |
| `src/exam/Main.java` | Entry point. Loads matrix, computes determinant, transposes, verifies. |
| `src/exam/QuadTree/QuadTreeNode.java` | Abstract base class for tree nodes. |
| `src/exam/QuadTree/LeafNode.java` | Stores a single non-zero element: row, col, value. |
| `src/exam/QuadTree/BranchNode.java` | Stores 4 children: NW, NE, SW, SE. |
| `src/exam/QuadTree/QuadTreeMatrix.java` | QuadTree implementation of `SparseMatrix`. |

## Architecture

The determinant algorithm is **independent** of the matrix implementation. It only uses the `SparseMatrix` interface. This means you could swap the QuadTree for a Coordinate List or Row-Wise List and the determinant would still work.

The QuadTree is chosen because it is efficient for in-place transpose. The hierarchical structure allows recursive NE/SW swaps without creating new structures.

## Design Decisions

### QuadTree without stored bounds

Each node does not store its region bounds. Instead, bounds are passed as parameters during traversal. This makes transpose simpler as we only swap children and leaf coordinates, no bound recalculation needed.

### Row-wise cache for determinant

The QuadTree is good for transpose but not ideal for Laplace's algorithm. Laplace needs to scan entire rows repeatedly. So we convert the QuadTree to a row-wise cache **once** at the start. After that, the determinant works entirely on the cache. No more tree traversals.

### Bitmask for available rows and columns

Instead of creating new arrays for each minor matrix, we use two `long` bitmasks. One tracks which rows are still available, the other which columns. When we expand along a row and remove a column, we just clear the corresponding bits. This avoids copying data at every recursion level.

### Sorted row selection

Before starting the recursion, we sort the rows by their non-zero count. At each level we pick the sparsest available row. This minimizes the number of recursive branches. The sort is done once. The order is used as a heuristic throughout, even though column removals make the counts slightly stale over time. For very sparse matrices this is not a problem. And also, each recursion makes the impact of the stale counts smaller, so the overall effect is negligible compared to recalculating counts at every level.

### Sign calculation

The position of a row or column in the current minor is calculated by counting how many available rows/columns come before it. We use `Long.bitCount` for this, which is a hardware instruction and runs in O(1).

## Limitations

> **WARNING:** The determinant calculator uses `long` bitmasks to track available rows and columns. A `long` has 64 bits, so the maximum supported matrix size is **64x64**. Matrices larger than 64x64 will produce incorrect results.

## How to Build and Run

```
javac -d . src/exam/*.java src/exam/QuadTree/*.java
java exam.Main
```

## Expected Output

```
Determinant: -4.0
Transposed Determinant: -4.0
Verification: Determinants are equal
```
