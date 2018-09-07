package com.company;

import java.util.ArrayList;

public class Cell {
    int x, y;
    ArrayList<Cell> neighborCells = new ArrayList<>();
    boolean passedCells = false;
    Cell parent = null;
    boolean path = false;
    double travelled;
    double projectedDist;
    boolean wall = true;
    boolean way = true;

    Cell(int x, int y) {
        this(x, y, true);
    }

    Cell(int x, int y, boolean isWall) {
        this.x = x;
        this.y = y;
        this.wall = isWall;
    }

    void addNeighbor(Cell other) {
        if (!this.neighborCells.contains(other)) {
            this.neighborCells.add(other);
        }
        if (!other.neighborCells.contains(this)) {
            other.neighborCells.add(this);
        }
    }

    boolean isCellBelowNeighbor() {
        return this.neighborCells.contains(new Cell(this.x, this.y + 1));
    }

    boolean isCellRightNeighbor() {
        return this.neighborCells.contains(new Cell(this.x + 1, this.y));
    }

    @Override
    public String toString() {
        return String.format("Cell(%s, %s)", x, y);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Cell)) return false;
        Cell otherCell = (Cell) other;
        return (this.x == otherCell.x && this.y == otherCell.y);
    }
}
