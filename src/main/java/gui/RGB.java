package gui;
import graph.*;
import io.*;

public class RGB {
    int r_c;
    int g_c;
    int b_c;
    
    public RGB(int r, int g, int b) {
        this.r_c = r;
        this.g_c = g;
        this.b_c = b;
    }
    
    public int r() {
        return r_c;
    }
    
    public int g() {
        return g_c;
    }
    
    public int b() {
        return b_c;
    }
}