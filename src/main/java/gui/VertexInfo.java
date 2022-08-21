package gui;
import java.util.ArrayList;
import graph.*;
import io.*;

public class VertexInfo {
    Point coordinate;
    RGB v_color;
    String v_label;
    ArrayList<Edge> v_edges;
    
    public VertexInfo(Point p) {
        this.coordinate = p;
        this.v_label = " ";
        v_color = new RGB(0,0,0);
        v_edges = new ArrayList<Edge>();
    }
        

    public ArrayList<Edge> edges() {
        return v_edges;
    }
    

    public void addEdge(Edge e) {
        v_edges.add(e);
    }
    

    boolean hasEdge(Edge e) {
        for(int i=0; i<v_edges.size(); i++) {
            if(e.equals(v_edges.get(i))) {
                return true;
            }
        }
        return false;
    }
    

    public void removeEdge(Edge e) {
        for(int i=0; i<v_edges.size(); i++) {
            if(e.equals(v_edges.get(i))) {
                v_edges.remove(i);
                return;
            }
        }	
    }
    

    public void setLabel(String new_label) {
        v_label = new_label;
    }
    

    public String label() {
        return v_label;
    }
    

    public void setCoord(Point p) {
        coordinate = p;
    }
    

    public Point coord() {
        return coordinate;
    }
    

    public RGB color() {
        return v_color;
    }
    

    public void setColor(RGB p) {
        v_color = p;
    }
    
    
    public boolean equals(VertexInfo v) {
        if(coordinate.x() == v.coord().x() &&
           coordinate.y() == v.coord().y() &&
           v_label == v.label()) {
            return true;
        }
        return false;
    }
}
