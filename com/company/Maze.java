package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Maze {

    private int height, width;
    private int gridHeight, gridWight;
    private char[][] grid;
    private Cell[][] cells;
    private Random random = new Random();

    public Maze(int aDimension) {
        this(aDimension, aDimension);
    }

    public Maze(int height, int width) {
        this.height = height;
        this.width = width;
        gridHeight = height * 4 + 1;
        gridWight = width * 2 + 1;
        grid = new char[gridHeight][gridWight];
        cellCreator();
        generateMaze();
    }

    private void cellCreator() {
        cells = new Cell[height][width];
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                cells[x][y] = new Cell(x, y, false);
            }
        }
    }

    private void generateMaze() {
        generateMaze(0, 0);
    }

    private void generateMaze(int x, int y) {
        generateMaze(getCell(x, y)); // generate from Cell
    }

    private void generateMaze(Cell startAt) {
        if (startAt == null) return;
        startAt.way = false;
        ArrayList<Cell> cells = new ArrayList<>();
        cells.add(startAt);

        while (!cells.isEmpty()) {
            Cell cell;
            if (random.nextInt(10) == 0)
                cell = cells.remove(random.nextInt(cells.size()));
            else cell = cells.remove(cells.size() - 1);
            ArrayList<Cell> neighbors = new ArrayList<>();
            Cell[] potentialNeighbors = new Cell[]{
                    getCell(cell.x + 1, cell.y),
                    getCell(cell.x, cell.y + 1),
                    getCell(cell.x - 1, cell.y),
                    getCell(cell.x, cell.y - 1)
            };
            for (Cell other : potentialNeighbors) {
                if (other == null || other.wall || !other.way) continue;
                neighbors.add(other);
            }
            if (neighbors.isEmpty()) continue;
            Cell selected = neighbors.get(random.nextInt(neighbors.size()));
            selected.way = false;
            cell.addNeighbor(selected);
            cells.add(cell);
            cells.add(selected);
        }
    }

    public Cell getCell(int x, int y) {
        try {
            return cells[x][y];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public void solve() {
        this.solve(0, 0, height - 1, width - 1);
    }

    public void solve(int startX, int startY, int endX, int endY) {
        for (Cell[] cellSet : this.cells) {
            for (Cell cell : cellSet) {
                cell.parent = null;
                cell.passedCells = false;
                cell.path = false;
                cell.travelled = 0;
                cell.projectedDist = -1;
            }
        }

        ArrayList<Cell> openCells = new ArrayList<>();
        Cell endCell = getCell(endX, endY);
        if (endCell == null) return;
        {
            Cell start = getCell(startX, startY);
            if (start == null) return;
            start.projectedDist = getProjectedDistance(start, 0, endCell);
            start.passedCells = true;
            openCells.add(start);
        }
        boolean solving = true;
        while (solving) {
            if (openCells.isEmpty()) return;
            Collections.sort(openCells, new Comparator<Cell>() {
                @Override
                public int compare(Cell cell1, Cell cell2) {
                    double diff = cell1.projectedDist - cell2.projectedDist;
                    if (diff > 0) return 1;
                    else if (diff < 0) return -1;
                    else return 0;
                }
            });
            Cell current = openCells.remove(0);
            if (current == endCell) break;
            for (Cell neighbor : current.neighborCells) {
                double projDist = getProjectedDistance(neighbor,
                        current.travelled + 1, endCell);
                if (!neighbor.passedCells || projDist < neighbor.projectedDist) {
                    neighbor.parent = current;
                    neighbor.passedCells = true;
                    neighbor.projectedDist = projDist;
                    neighbor.travelled = current.travelled + 1;
                    if (!openCells.contains(neighbor))
                        openCells.add(neighbor);
                }
            }
        }
        Cell backtracking = endCell;
        backtracking.path = true;
        while (backtracking.parent != null) {
            backtracking = backtracking.parent;
            backtracking.path = true;
        }
    }

    public double getProjectedDistance(Cell current, double travelled, Cell end) {
        return travelled + Math.abs(current.x - end.x) +
                Math.abs(current.y - current.x);
    }

    public void showGrid() {
        char backChar = ' ', wallChar = 'X', cellChar = ' ', pathChar = '*';
        for (int x = 0; x < gridHeight; x++) {
            for (int y = 0; y < gridWight; y++) {
                grid[x][y] = backChar;
            }
        }
        for (int x = 0; x < gridHeight; x++) {
            for (int y = 0; y < gridWight; y++) {
                if (x % 4 == 0 || y % 2 == 0)
                    grid[x][y] = wallChar;
            }
        }
        for (int x = 0; x < height; x++) {
            for (int y = 0; y < width; y++) {
                Cell current = getCell(x, y);
                int gridX = x * 4 + 2, gridY = y * 2 + 1;
                if (current.path) {
                    grid[gridX][gridY] = pathChar;
                    if (current.isCellBelowNeighbor())
                        if (getCell(x, y + 1).path) {
                            grid[gridX][gridY + 1] = pathChar;
                            grid[gridX + 1][gridY + 1] = backChar;
                            grid[gridX - 1][gridY + 1] = backChar;
                        } else {
                            grid[gridX][gridY + 1] = cellChar;
                            grid[gridX + 1][gridY + 1] = backChar;
                            grid[gridX - 1][gridY + 1] = backChar;
                        }
                    if (current.isCellRightNeighbor())
                        if (getCell(x + 1, y).path) {
                            grid[gridX + 2][gridY] = pathChar;
                            grid[gridX + 1][gridY] = pathChar;
                            grid[gridX + 3][gridY] = pathChar;
                        } else {
                            grid[gridX + 2][gridY] = cellChar;
                            grid[gridX + 1][gridY] = cellChar;
                            grid[gridX + 3][gridY] = cellChar;
                        }
                } else {
                    grid[gridX][gridY] = cellChar;
                    if (current.isCellBelowNeighbor()) {
                        grid[gridX][gridY + 1] = cellChar;
                        grid[gridX + 1][gridY + 1] = backChar;
                        grid[gridX - 1][gridY + 1] = backChar;
                    }
                    if (current.isCellRightNeighbor()) {
                        grid[gridX + 2][gridY] = cellChar;
                        grid[gridX + 1][gridY] = cellChar;
                        grid[gridX + 3][gridY] = cellChar;
                    }
                }
            }
        }
    }

    public void draw() {
        System.out.print(this);
    }

    @Override
    public String toString() {
        showGrid();
        String output = "";
        for (int y = 0; y < gridWight; y++) {
            for (int x = 0; x < gridHeight; x++) {
                output += grid[x][y];
            }
            output += "\n";
        }
        return output;
    }

}