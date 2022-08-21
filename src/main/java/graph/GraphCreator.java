package graph;
import io.Log;
import java.util.ArrayList;
import java.util.Random;
public class GraphCreator {
    Graph graph;
    int num_vertices;
    int num_edges;
    int min_weight;
    int max_weight;
    int max_number_of_edges;


    public GraphCreator(int num_vertices, int num_edges, int min_weight, int max_weight) {
        this.num_vertices = num_vertices;
        this.num_edges = num_edges;
        this.min_weight = min_weight;
        this.max_weight = max_weight;
        this.max_number_of_edges = (num_vertices*(num_vertices-1))/2;
        if(num_edges > max_number_of_edges) {
            num_edges = max_number_of_edges;
        }
    }
    
    
    public Graph create() {
        int id = 0;
        graph = new Graph();
        ArrayList<String> vertices = new ArrayList<String>();

        for(int i=0; i<num_vertices; i++) {
            System.out.println("ADD VERTEX " + String.valueOf(id));
            graph.addVertex(String.valueOf(id));
            vertices.add(String.valueOf(id));
            id++;
        }
        
        Random rand = new Random();
    
        int current_number_of_edges = 0;
        
        while(current_number_of_edges != num_edges) {
            String src =  String.valueOf(rand.nextInt(vertices.size() - 0) + 0);
            String dst =  String.valueOf(rand.nextInt(vertices.size() - 0) + 0);
            if(src.equals(dst)) {
                continue;
            }
            
            int weight;
            if(max_weight - min_weight == 0) {
                weight = max_weight;
            }
            else {
                weight = rand.nextInt(max_weight - min_weight) + min_weight;
            }

            if(!graph.hasEdge(src, dst)) {
                try {
                    graph.addEdge(src, dst, weight);
                }
                catch(NumberFormatException e) {
                    new Log().warning("edge cannot be added ");
                }
                current_number_of_edges += 1;
            }
        }

        graph.print();
        
        return graph;
    }
}
