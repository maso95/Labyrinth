package com.company;

public class Main {

    public static void main(String[] args) {
        Maze maze = new Maze(20);
        maze.solve();
        maze.draw();
    }
}
