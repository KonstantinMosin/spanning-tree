package io;

import graph.*;
import gui.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;


public class JsonGraphIO  {
    Gson gson;
    
    public JsonGraphIO() {
        gson = new Gson();
    };
    

    public Graph import_graph(String path) throws FileNotFoundException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            Type type = new TypeToken<HashMap<String, HashMap<String, Integer>>>(){}.getType();
            HashMap<String, HashMap<String, Integer>> json = gson.fromJson(bufferedReader, type);
            Graph graph = new Graph();
            
            for (Map.Entry<String, HashMap<String, Integer>> vertex : json.entrySet()) {
                if(!graph.searchVertex(vertex.getKey())) {
                    graph.addVertex(vertex.getKey());
                }
                for (Map.Entry<String, Integer> adj : vertex.getValue().entrySet()) {
                    if(!graph.searchVertex(adj.getKey())) {
                        graph.addVertex(adj.getKey());
                    }
                    graph.addEdge(vertex.getKey(), adj.getKey(), adj.getValue());
                }
            }
            
            graph.print();
            return graph;
        }
        catch (FileNotFoundException e) {
            new Log().error("File import " + path);
            e.printStackTrace(); 
            return null;
        }
    }
    

    public void export_graph(Graph graph, String path) {
        try {
            System.out.println("save ");
            graph.print();
            Writer writer = new FileWriter(path);
            gson.toJson(graph.hash_map(), writer);
            writer.flush();
            writer.close();
        }
        catch(IOException e) {
            e.printStackTrace();
            new Log().error("File export " + path);
        }
    }
}
