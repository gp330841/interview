package codingInInterview.companyWise.greyOrange;

/**
 * Implementation of the "Number of Islands" problem.
 * Given an m x n 2D binary grid which represents a map of '1's (land) and '0's (water),
 * returns the number of islands. An island is surrounded by water and is formed by
 * connecting adjacent lands horizontally or vertically.
 */
public class NumberOfIslands {

    /**
     * Calculates the number of islands in the given grid.
     * Time Complexity: O(M * N) where M is rows and N is columns.
     * Space Complexity: O(M * N) in the worst case due to recursion stack.
     */
    public static int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int count = 0;
        int rows = grid.length;
        int cols = grid[0].length;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    dfs(grid, r, c);
                }
            }
        }

        return count;
    }

    private static void dfs(char[][] grid, int r, int c) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Boundary conditions and check if the current cell is water or already visited
        if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] == '0') {
            return;
        }

        // Mark the current land cell as visited (converting '1' to '0')
        grid[r][c] = '0';

        // Traverse in all 4 cardinal directions (up, down, left, right)
        dfs(grid, r - 1, c); // Up
        dfs(grid, r + 1, c); // Down
        dfs(grid, r, c - 1); // Left
        dfs(grid, r, c + 1); // Right
    }

    public static void main(String[] args) {
        System.out.println("--- Testing NumberOfIslands ---");

        char[][] grid1 = {
            {'1', '1', '1', '1', '0'},
            {'1', '1', '0', '1', '0'},
            {'1', '1', '0', '0', '0'},
            {'0', '0', '0', '0', '0'}
        };

        char[][] grid2 = {
            {'1', '1', '0', '0', '0'},
            {'1', '1', '0', '0', '0'},
            {'0', '0', '1', '0', '0'},
            {'0', '0', '0', '1', '1'}
        };

        System.out.println("Grid 1 Expected Islands: 1, Actual: " + numIslands(grid1));
        System.out.println("Grid 2 Expected Islands: 3, Actual: " + numIslands(grid2));
    }
}
