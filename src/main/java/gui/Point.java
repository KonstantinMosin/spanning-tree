package gui;
import graph.*;
import io.*;

public class Point {
    int x_c;
    int y_c;
    
    public Point(int x, int y) {
        this.x_c = x;
        this.y_c = y;
    }
    
    public int x() {
        return x_c;
    }
    
    public int y() {
        return y_c;
    }
}