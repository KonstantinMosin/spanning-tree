package gui;
import graph.*;
import io.*;
public class Edge {
    VertexInfo v1;
    VertexInfo v2;
    int e_weight;
    RGB e_color;
    int e_thickness = 1;
    
    public Edge(VertexInfo v1, VertexInfo v2, int weight) {
        this.v1 = v1;
        this.v2 = v2;
        this.e_weight = weight;
        this.e_color = new RGB(0,0,0);
    }
    
    public int weight() {
        return e_weight;
    }
    
    public VertexInfo left() {
        return v1;
    }
    
    public VertexInfo right() {
        return v2;
    }
    
    public boolean equals(Edge e) {
        return (e.left().equals(v1) && e.right().equals(v2)) || (e.left().equals(v2) && e.right().equals(v1));
    }
    
    public RGB color() {
        return e_color;
    }
    
    public void setColor(RGB p) {
        e_color = p;
    }
    
    public void setThickness(int t) {
        e_thickness = t;
    }
    
    public int thickness() {
        return e_thickness;
    }
    
    public void setWeight(int weight) {
        e_weight = weight;
    }
}