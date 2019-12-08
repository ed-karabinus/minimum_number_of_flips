import java.util.*;
import java.util.stream.*;
class Solution {
    class SearchMatrix {
        List<List<Integer>> matrix;
        int level;
        public SearchMatrix(List<List<Integer>> matrix, int level) {
            this.matrix = matrix;
            this.level = level;
        }
    }
    // examples and problem from https://leetcode.com/problems/minimum-number-of-flips-to-convert-binary-matrix-to-zero-matrix/
    public static void main(String[] args) {
        Solution sol = new Solution();
        System.out.println("TEST CASES");
        System.out.printf("%d should be %d\n", sol.minFlips(new int[][] {{1,1,1},{1,0,1},{0,0,0}}), 6);
        System.out.printf("%d should be %d\n", sol.minFlips(new int[][] {{0,0},{0,1}}), 3);
        System.out.printf("%d should be %d\n", sol.minFlips(new int[][] {{0}}), 0);
        System.out.printf("%d should be %d\n", sol.minFlips(new int[][] {{1,0,0},{1,0,0}}), -1);
    }
    
    public int minFlips(int[][] matrix) {
        Deque<SearchMatrix> matrixQueue = new ArrayDeque<>();
        Set<List<List<Integer>>> visitedMatrices = new HashSet<>();
        List<List<Integer>> initialMatrix = Arrays.stream(matrix)
            .map(row -> Arrays.stream(row).boxed().collect(Collectors.toList()))
            .collect(Collectors.toList());
        matrixQueue.offerFirst(new SearchMatrix(initialMatrix, 0));
        while (matrixQueue.peekLast() != null) {
            SearchMatrix currentSearchMatrix = matrixQueue.pollLast();
            if (visitedMatrices.contains(currentSearchMatrix.matrix)) {
                continue;
            }
            visitedMatrices.add(currentSearchMatrix.matrix);
            int rowIndex = 0;
            boolean isZeroMatrix = true;
            for (List<Integer> row : currentSearchMatrix.matrix) {
                int columnIndex = 0;
                while (columnIndex < row.size()) {
                    isZeroMatrix &= currentSearchMatrix.matrix.get(rowIndex).get(columnIndex) == 0;
                    if (getNeighbors(currentSearchMatrix.matrix, rowIndex, columnIndex)
                            .stream()
                            .anyMatch(neighbor -> currentSearchMatrix.matrix.get(neighbor.getKey()).get(neighbor.getValue()) != 0)) {
                        matrixQueue.offerFirst(new SearchMatrix(getMatrixCopyWithElementFlipped(currentSearchMatrix.matrix, rowIndex, columnIndex), currentSearchMatrix.level + 1));
                    }
                    columnIndex++;
                }
                rowIndex++;
            }
            if (isZeroMatrix) {
                return currentSearchMatrix.level;
            }
        }
        return -1;
    }

    private List<Map.Entry<Integer, Integer>> getNeighbors(List<List<Integer>> matrix, int rowIndex, int columnIndex) {
        List<Map.Entry<Integer, Integer>> neighbors = new ArrayList<>();
        neighbors.add(new AbstractMap.SimpleImmutableEntry<Integer, Integer>(rowIndex, columnIndex));
        if (rowIndex > 0) {
            neighbors.add(new AbstractMap.SimpleImmutableEntry<Integer, Integer>(rowIndex - 1, columnIndex));
        }
        if (columnIndex > 0) {
            neighbors.add(new AbstractMap.SimpleImmutableEntry<Integer, Integer>(rowIndex, columnIndex - 1));
        }
        if (rowIndex < matrix.size() - 1) {
            neighbors.add(new AbstractMap.SimpleImmutableEntry<Integer, Integer>(rowIndex + 1, columnIndex));
        }
        if (columnIndex < matrix.get(0).size() - 1) {
            neighbors.add(new AbstractMap.SimpleImmutableEntry<Integer, Integer>(rowIndex, columnIndex + 1));
        }
        return neighbors;
    }
    
    private List<List<Integer>> getMatrixCopyWithElementFlipped(List<List<Integer>> matrix, int rowIndex, int columnIndex) {
        List<List<Integer>> copy = matrix.stream().map(ArrayList::new).collect(Collectors.toList());
        for (Map.Entry<Integer, Integer> coordinates : getNeighbors(matrix, rowIndex, columnIndex)) {
            flipElement(copy, coordinates.getKey(), coordinates.getValue());
        }
        return copy;
    }
    
    private void flipElement(List<List<Integer>> matrix, int rowIndex, int columnIndex) {
        List<Integer> row = matrix.get(rowIndex);
        row.set(columnIndex, 1 - row.get(columnIndex));
    }
}