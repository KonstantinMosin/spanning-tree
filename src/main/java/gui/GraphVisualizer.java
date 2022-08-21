package gui;
import graph.*;
import io.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import java.util.EventListener;
import java.util.EventObject;
import javax.swing.event.EventListenerList;


interface EdgeChangedListener extends EventListener {
    public int myEventOccurred(int current_weight);
}


class GraphVisualizer extends JComponent implements MouseMotionListener, MouseListener, KeyListener  {  
    int vertex_id = 0;
    int radius = 50;
    int count = 0;
    boolean antialiasingEnabled = false;

    Point mouse_last = null;
    Point mouse_press_coord = null;
    Point last_press_vertex_pos = null;
    
    VertexInfo lastHoveredVertex = null;	
    VertexInfo lastSelectedVertex = null;
    Edge lastHoveredEdge = null;
    
    ArrayList<VertexInfo> vertices;
    ArrayList<Edge> edges;
    ArrayList<Edge> highlightEdges; 
    boolean left_mouse_clicked = false;

    enum DrawState {None, Hover, Select, EdgeHover, EdgeSelect, Drag};
    DrawState currentState = DrawState.None;
    
    Graph graph;
    
    long drag_time = 0;
    

    protected EventListenerList listenerList = new EventListenerList();
    

    public void addMyEventListener(EdgeChangedListener listener) {
        listenerList.add(EdgeChangedListener.class, listener);
    }


    public void removeMyEventListener(EdgeChangedListener listener) {
        listenerList.remove(EdgeChangedListener.class, listener);
    }
    
    
    public GraphVisualizer(Graph graph) {
        vertices = new ArrayList<VertexInfo>();
        edges = new ArrayList<Edge>(); 
        
        highlightEdges = new ArrayList<Edge>(); 
        this.graph = graph;
        
        enableEvents(true);
        setFocusable(true);

        initGraph();
    
        new Log().info("Create graph");
        new Log().info("Number of edges " + edges.size());
        new Log().info("Number of vertices " + vertices.size());
    
        this.setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.BLACK));
        repaint();
        
        vertex_id = vertices.size();
    }

    
    public void setDefaultStyle() {
        for(int i=0; i<edges.size(); i++) {
            edges.get(i).setColor(new RGB(0,0,0));
            edges.get(i).setThickness(1);
        }
    }


    //debug
    public void highlightEdge(String s1, String s2, RGB color, int thickness) {
        for(int i=0; i<edges.size(); i++) {
            Edge e = edges.get(i);
            if((e.left().label().equals(s1) && e.right().label().equals(s2)) || 
               (e.left().label().equals(s2) && e.right().label().equals(s1))) {
                edges.get(i).setColor(color);
                edges.get(i).setThickness(thickness);
                highlightEdges.add(edges.get(i));
                this.repaint();
                break;
                
            }
        }
    }
    
    
    public void enableEvents(boolean enabled) {
        if(enabled) {
            addMouseMotionListener(this);
            addMouseListener(this);
            addKeyListener(this);
            antialiasingEnabled = false;
        }
        else {
            removeMouseMotionListener(this);
            removeMouseListener(this);
            removeKeyListener(this);
            antialiasingEnabled = true;
        }
    }
    
    
    private void initGraph() {
        for(String vertex: graph.vertices()) {
            VertexInfo v1 = getVertexByLabel(vertex);

            if(v1 == null) {
                v1 = new VertexInfo(getRandomPoint());
                v1.setLabel(vertex);
                addVertex(v1);
            }
            
            HashMap<String, Integer> adjacent = graph.getVertexList(vertex);
            for (Map.Entry<String, Integer> adj : adjacent.entrySet()) {
                VertexInfo v2 = getVertexByLabel(adj.getKey());
                if(v2 == null) {
                    v2 = new VertexInfo(getRandomPoint());
                    v2.setLabel(adj.getKey());
                    addVertex(v2);
                }
                Edge edge = new Edge(v1, v2, adj.getValue());
                if(v1.hasEdge(edge) || v2.hasEdge(edge)) {
                    continue;
                }
                createEdge(v1, v2, adj.getValue());
            }
        }
    }
    

    private Point getRandomPoint() {
        Random random = new Random();
        int rand_x = random.nextInt(950 - 10) + 10;
        int rand_y = random.nextInt(500 - 10) + 10;
        return new Point(rand_x, rand_y);
    }
    
    
    public void clear() {
        vertex_id = 0;
        mouse_last = null;
        mouse_press_coord = null;
        last_press_vertex_pos = null;
        lastHoveredVertex = null;	
        lastSelectedVertex = null;
        lastHoveredEdge = null;
        vertices = new ArrayList<VertexInfo>();
        edges = new ArrayList<Edge>();
        left_mouse_clicked = false;
        currentState = DrawState.None;
        graph = new Graph();
        repaint();
    }
    
    
    void addVertex(VertexInfo v) {
        vertices.add(v);
    }
    

    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        if(antialiasingEnabled) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        Font f = new Font("serif", Font.PLAIN, 15);
        g2d.setFont(f);
        FontMetrics fm = g2d.getFontMetrics();
        
        HashSet<String> visited = new HashSet<String>(); 
        for(int i=0; i<vertices.size(); i++) {
            VertexInfo v = vertices.get(i);
            int red = v.color().r();
            int green = v.color().g();
            int blue = v.color().b();
            int x = v.coord().x();
            int y = v.coord().y();
            
            if(lastSelectedVertex != null) {
                Point p1 = lastSelectedVertex.coord();
                Point p2 = mouse_last;
                g2d.drawLine(p1.x()+radius/2, p1.y()+radius/2, p2.x(), p2.y());
            }
            

            for(int j=0; j<v.edges().size(); j++) {
                VertexInfo v1 = v.edges().get(j).left();
                VertexInfo v2 = v.edges().get(j).right();
                
                if(visited.contains(v1.label()) || visited.contains(v2.label())) {
                    continue;
                }
                
                String text = String.valueOf(v.edges().get(j).weight());
                Point p1 = v1.coord();
                Point p2 = v2.coord();
                
                g2d.setStroke(new BasicStroke(v.edges().get(j).thickness()));
                g2d.setColor(new Color(v.edges().get(j).color().r(),v.edges().get(j).color().g(), v.edges().get(j).color().b()));
                g2d.drawLine(p1.x()+radius/2, p1.y()+radius/2, p2.x()+radius/2, p2.y()+radius/2);
                g2d.setColor(new Color(0,0,255));
                g2d.drawString(text, (p1.x()+p2.x()+radius)/2 - fm.stringWidth(text)/2, (p1.y()+p2.y()+radius)/2+fm.getHeight()/2-fm.getAscent()/4);
                g2d.setColor(new Color(0,0,0));
            }
            
            String text = v.label();
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(new Color(255,255,255));
            g2d.fillOval(v.coord().x(), v.coord().y(), radius, radius);
            g2d.setColor(new Color(red,green,blue));
            g2d.drawOval(v.coord().x(), v.coord().y(), radius, radius);
            g2d.drawString(text, x+radius/2 - fm.stringWidth(text)/2, y+radius/2+fm.getHeight()/2-fm.getAscent()/4);
            
            visited.add(v.label());
        }
    }
    

    private void hover(int index) {
        if(lastHoveredVertex != null) {
            lastHoveredVertex.setColor(new RGB(0,0,0));
        }
        currentState = DrawState.Hover;
        lastHoveredVertex = vertices.get(index);
        lastHoveredVertex.setColor(new RGB(255,100,0));
        VertexInfo c = vertices.remove(index);
        vertices.add(c);
        Point p = lastHoveredVertex.coord();
        last_press_vertex_pos = new Point(p.x(), p.y());
    }
    
    
    private void unhover() {
        lastHoveredVertex.setColor(new RGB(0,0,0));
        lastHoveredVertex = null;
        currentState = DrawState.None;
    }
    
    
    private void select(Point e) {
        mouse_last = new Point(e.x(), e.y());
        lastSelectedVertex = lastHoveredVertex;
        lastSelectedVertex.setColor(new RGB(255,255,0));
        currentState = DrawState.Select;
    }
    

    private void unselect() {
        lastSelectedVertex.setColor(new RGB(0,0,0));
        lastSelectedVertex = null;
        currentState = DrawState.None;
    }
    
    
    private int getVertexIndexAt(Point p) {
        for(int i=vertices.size()-1; i>=0; i--) {
            VertexInfo v = vertices.get(i);
            int mouse_x = p.x();
            int mouse_y = p.y();
            boolean x_ok = mouse_x >= v.coord().x() && mouse_x <= v.coord().x()+radius;
            boolean y_ok = mouse_y >= v.coord().y() && mouse_y <= v.coord().y()+radius;
            if(x_ok && y_ok) {
                return i;
            }
        }
        return -1;
    }
    
    
    private VertexInfo getVertexByLabel(String name) {
        
        for(int i=vertices.size()-1; i>=0; i--) {
            VertexInfo v = vertices.get(i);
            if(v.label().equals( name)) {
                return v;
            }
        }
        return null;
    }
    

    private int getEdgeIndexAt(Point p) {
        for(int i=edges.size()-1; i>=0; i--) {
            Edge v = edges.get(i);
            int mouse_x = p.x();
            int mouse_y = p.y();
              
            VertexInfo min_vertex = (v.left().coord().x() <= v.right().coord().x())? v.left() : v.right();
            VertexInfo max_vertex = (v.left().coord().x() > v.right().coord().x())? v.left() : v.right();
            
            int x0 = Math.min(v.left().coord().x(),v.right().coord().x())+radius/2;
            int y0 = Math.min(v.left().coord().y(),v.right().coord().y())+radius/2;
            int w = Math.abs(max_vertex.coord().x() - min_vertex.coord().x());
            int h = Math.abs( max_vertex.coord().y() - min_vertex.coord().y());
            
            boolean x_ok = mouse_x >= x0 && mouse_x <= x0+w;	       
            boolean line_ok = false;
            int t = 10;
            if(w <= 5) {
                if(mouse_x > x0-t && mouse_x < x0+t && mouse_y >= y0 && mouse_y < y0+h) {
                    line_ok = true;
                }
            }
            else {
                x0 = min_vertex.coord().x()+radius/2;
                y0 = min_vertex.coord().y()+radius/2;
                int x1 = max_vertex.coord().x()+radius/2;
                int y1 = max_vertex.coord().y()+radius/2;
                float a = (float)(y0-y1)/(x1-x0);
                float b = (float)(x0*y1 - x1*y0)/(x1-x0);
                float r = -a*mouse_x - b;
                x_ok = mouse_x >= x0 && mouse_x <= x0+w;
                line_ok = (mouse_y > r-t && mouse_y < r+t)&&x_ok;	
            }

            if(line_ok) {
                return i;
            }
        }
        return -1;
    }
    
    
    private void createVertex(Point e) {
        VertexInfo new_vertex = new VertexInfo(new Point(e.x()-radius/2, e.y()-radius/2));
        new_vertex.setLabel(String.valueOf(vertex_id));
        vertex_id++;
        vertices.add(new_vertex);
        
        graph.addVertex(new_vertex.label());
        
        new Log().info("Create vertex " + new_vertex.label());
        
        graph.print();
    }
    
    
    private void createEdge(VertexInfo v1, VertexInfo v2, int weight) {
        Edge edge = new Edge(v1, v2, weight);
        if(v1.hasEdge(edge) || v2.hasEdge(edge)) {
            throw new NumberFormatException("edge exists");
        }

        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i+2) {
              if (listeners[i] == EdgeChangedListener.class) {
                int res = ((EdgeChangedListener) listeners[i+1]).myEventOccurred(weight);
                if(res < 0) {
                    throw new NumberFormatException("edge weight < 0");
                }
                edge.setWeight(res);
              }
         }
        
        edges.add(edge);
        
        v1.addEdge(edge);
        v2.addEdge(edge);
    
        try {
            graph.addEdge(v1.label(), v2.label(), weight);
        }
        catch(NumberFormatException e) {
            new Log().warning("edge cannot be added ");
        }
        graph.print();
        

        new Log().info("Create edge " + v1.label() + " " + v2.label());
    }
    

    public void keyTyped(KeyEvent e) {}

    
    public void keyPressed(KeyEvent e) {
        if(currentState == DrawState.Select) {
            if(e.getKeyChar() == KeyEvent.VK_DELETE) {
                for(int i=0; i<lastSelectedVertex.edges().size(); i++) {
                    Edge edge = lastSelectedVertex.edges().get(i);
                        edge.left().removeEdge(edge);
                        edge.right().removeEdge(edge);
                        
                        graph.removeEdge(edge.left().label(), edge.right().label());
                        i-=1;
                }
                vertices.remove(lastSelectedVertex);
                graph.removeVertex(lastSelectedVertex.label());
                unselect();
                repaint();
                
                graph.print();
            }
        }
        
    }


    public void keyReleased(KeyEvent e) {
        
    }
    
    
    @Override
    public void mousePressed(MouseEvent e) {
        drag_time = System.currentTimeMillis();
        left_mouse_clicked = e.getButton() == MouseEvent.BUTTON1;
        boolean left_mouse = e.getButton() == MouseEvent.BUTTON1;
        mouse_press_coord = new Point(e.getPoint().x, e.getPoint().y);

        if(currentState == DrawState.None && left_mouse) {
            //Add VertexInfo
            createVertex(new Point(e.getX(), e.getY()));
            hover(vertices.size()-1);
            repaint();
        }
        else if(currentState == DrawState.Hover && e.getClickCount() == 2 && left_mouse) {
            if(lastHoveredVertex != null) {
                select(new Point(e.getX(), e.getY()));
                repaint();
            }
        }
        else if(currentState == DrawState.Select && left_mouse) {
            int index = getVertexIndexAt(new Point(e.getX(), e.getY()));
            if(index == -1) {
                //Click on empty space
                unselect();
                repaint();
            }
            if(index != -1) {
                //Click on VertexInfo
                if(vertices.get(index).equals(lastSelectedVertex)) {
                    // if VertexInfo == selected_vertex: unselect() + hover()
                    unselect();
                    hover(index);
                    repaint();
                }	
                else {
                    //Add edge
                    try {
                        createEdge(lastSelectedVertex, vertices.get(index),  new Random().nextInt(100)+1);
                        unselect();
                        hover(index);
                        repaint();
                    }
                    catch(NumberFormatException er) {
                        System.out.println("CATCH");
                        unselect();
                        repaint();
                    }
                }
            }
            
        }
        else if(currentState == DrawState.EdgeHover && e.getClickCount() == 2 && left_mouse) {

            int weight = lastHoveredEdge.weight();
            Object[] listeners = listenerList.getListenerList();
            for (int i = 0; i < listeners.length; i = i+2) {
                if (listeners[i] == EdgeChangedListener.class) {
                    int res = ((EdgeChangedListener) listeners[i+1]).myEventOccurred(weight);
                    if(res >= 0) {
                        lastHoveredEdge.setWeight(res);
                        graph.addWeight(lastHoveredEdge.left().label(), lastHoveredEdge.right().label(), res);
                        repaint();
                    }
                    else if(res == -2) {
                        graph.removeEdge(lastHoveredEdge.left().label(), lastHoveredEdge.right().label());
                        edges.remove(lastHoveredEdge);
                        lastHoveredEdge.left().removeEdge(lastHoveredEdge);
                        lastHoveredEdge.right().removeEdge(lastHoveredEdge);
                        currentState = DrawState.None;
                        repaint();
//		    			lastHoveredEdge = null;
                    }
                    System.out.println("RESULT " + res);
                 }
            }
        }

    }
    
    
    @Override
    public void mouseClicked(MouseEvent e) {}
    

    @Override
    public void mouseEntered(MouseEvent e) {}
    

    @Override
    public void mouseExited(MouseEvent e) {}
    

    @Override
    public void mouseReleased(MouseEvent e) {
        left_mouse_clicked = false;
        drag_time = 0;
    }
    

    @Override
    public void mouseDragged(MouseEvent e) {
        if(currentState == DrawState.Hover && left_mouse_clicked) {	
            int dx = e.getX() - mouse_press_coord.x();
            int dy = e.getY() - mouse_press_coord.y();
            Point p = last_press_vertex_pos;
            lastHoveredVertex.setCoord(new Point(p.x()+dx, p.y()+dy));
            
            if((System.currentTimeMillis()-drag_time) > 1000/60) {
                this.repaint();
                drag_time = System.currentTimeMillis();
            }
        }
    }
    

    @Override
    public void mouseMoved(MouseEvent e) {

        if(currentState == DrawState.None) {
            int index = getVertexIndexAt(new Point(e.getX(), e.getY()));
            if(index != -1) {
                hover(index);
                repaint();
            }
            else {
                index = getEdgeIndexAt(new Point(e.getX(), e.getY()));
                if(index != -1) {
                    edges.get(index).setColor(new RGB(0,255,0));
                    lastHoveredEdge = edges.get(index);
                    currentState = DrawState.EdgeHover;
                    repaint();
                }			
            }
            
        }
        else if(currentState == DrawState.Hover) {
            
            
            int index = getVertexIndexAt(new Point(e.getX(), e.getY()));
            if(index == -1) {
                unhover();
                repaint();
            }
            else {
                if(vertices.get(index).equals(lastHoveredVertex)) {
                
                    return;
                }
                hover(index);
                repaint();
            }
        
        }
        else if(currentState == DrawState.Select) {

        
            
            mouse_last = new Point(e.getX(), e.getY());
            int index = getVertexIndexAt(new Point(e.getX(), e.getY()));
            if(index != -1) {
                if(vertices.get(index).equals(lastSelectedVertex)) {
                
                    return;
                }
                if(!lastHoveredVertex.equals(lastSelectedVertex)) {
                    lastHoveredVertex.setColor(new RGB(0,0,0));
                }
                VertexInfo c = vertices.remove(index);
                vertices.add(c);
                lastHoveredVertex = c;
                lastHoveredVertex.setColor(new RGB(255,100,0));
                repaint();
            }
            else {

                if(!lastHoveredVertex.equals(lastSelectedVertex)) {
                    lastHoveredVertex.setColor(new RGB(0,0,0));
                }
                repaint();
            }

            
        }
        else if(currentState == DrawState.EdgeHover) {
        
            int index = getVertexIndexAt(new Point(e.getX(), e.getY()));
            if(index != -1) {
                lastHoveredEdge.setColor(new RGB(0,0,0));
                lastHoveredEdge = null;
                hover(index);
                repaint();
        
                return;
            }
            index = getEdgeIndexAt(new Point(e.getX(), e.getY()));
            if(index == -1) {
                lastHoveredEdge.setColor(new RGB(0,0,0));
                lastHoveredEdge = null;
                currentState = DrawState.None;
                repaint();
            }
            else {
                if(edges.get(index).equals(lastHoveredEdge)) {
    
                    return;
                }
                lastHoveredEdge.setColor(new RGB(0,0,0));
                
                lastHoveredEdge = edges.get(index);
                lastHoveredEdge.setColor(new RGB(0,255,0));
                currentState = DrawState.EdgeHover;
                repaint();
            }

        }
        

    }
    
}  