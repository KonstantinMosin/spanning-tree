package graph;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayDeque;
import java.util.ArrayList;


public class Graph {

    private HashMap<String, HashMap <String, Integer>> adjacentVertices;
    private ArrayList<String[]> steps;
    private int stepIndex = 0;
    private int stepCount = 0;
    private boolean forward = true;

    public Graph() {
        adjacentVertices = new HashMap<String, HashMap<String, Integer>>();
        steps = new ArrayList<String[]>();
    }

    public boolean addVertex(String vertex) {
        adjacentVertices.put(vertex, new HashMap<String, Integer>());
        return true;
    }

    public boolean addEdge(String sourceVertex, String targetVertex) {
        return addEdge(sourceVertex, targetVertex, 0);
    }

    
    public HashMap<String, HashMap <String, Integer>> hash_map() {
        return adjacentVertices;
    }
    
    public boolean addEdge(String sourceVertex, String targetVertex, int weight) {
        if (!searchVertex(sourceVertex)) {
            throw new NumberFormatException("no match source vertex");
        }
        else if (!searchVertex(targetVertex)) {
            throw new NumberFormatException("no match target vertex");
        }
        else if (hasEdge(sourceVertex, targetVertex)) {
            throw new NumberFormatException("edge already exist");
        }

        adjacentVertices.get(sourceVertex).put(targetVertex, weight);
        adjacentVertices.get(targetVertex).put(sourceVertex, weight);

        return true;
    }

    public boolean addWeight(String sourceVertex, String targetVertex, int weight) {
        if (!searchVertex(sourceVertex)) {
            throw new NumberFormatException("no match source vertex");
        }
        else if (!searchVertex(targetVertex)) {
            throw new NumberFormatException("no match target vertex");
        }
        else if (!hasEdge(sourceVertex, targetVertex)) {
            throw new NumberFormatException("edge not exist");
        }

        adjacentVertices.get(sourceVertex).put(targetVertex, weight);
        adjacentVertices.get(targetVertex).put(sourceVertex, weight);


        return true;
    }

    public boolean removeAllVertices() {
        adjacentVertices.clear();

        return true;
    }

    public boolean removeAllEdges() {
        for (Map.Entry<String, HashMap<String, Integer>> entry : adjacentVertices.entrySet()) {
            adjacentVertices.get(entry.getKey()).clear();
        }

        return true;
    }

    public boolean removeVertex(String vertex) {
        if (!searchVertex(vertex)) {
            throw new NumberFormatException("missing vertex");
        }

        for (Map.Entry<String, HashMap<String, Integer>> entry : adjacentVertices.entrySet()) {
            String key = entry.getKey();
            if (adjacentVertices.get(key).containsKey(vertex)) {
                adjacentVertices.get(key).remove(vertex);
            }
        }
        
        adjacentVertices.remove(vertex);

        return true;
    }

    public boolean removeEdge(String sourceVertex, String targetVertex) {
        if (!searchVertex(sourceVertex)) {
            throw new NumberFormatException("no match source vertex");
        }
        else if (!searchVertex(targetVertex)) {
            throw new NumberFormatException("no match target vertex");
        }
        else if (!hasEdge(sourceVertex, targetVertex)) {
            throw new NumberFormatException("edge not exists");
        }
        
        adjacentVertices.get(sourceVertex).remove(targetVertex);
        adjacentVertices.get(targetVertex).remove(sourceVertex);
        
        return true;
    }

    public Integer getEdgeWeight(String sourceVertex, String targetVertex) {
        if (!searchVertex(sourceVertex)) {
            throw new NumberFormatException("no match source vertex");
        }
        else if (!searchVertex(targetVertex)) {
            throw new NumberFormatException("no match target vertex");
        }
        else if (!hasEdge(sourceVertex, targetVertex)) {
            throw new NumberFormatException("edge not exists");
        }

        return adjacentVertices.get(sourceVertex).get(targetVertex);
    }

    public ArrayList<String> vertices() {
        return new ArrayList<String>(adjacentVertices.keySet());
    }

    public HashMap<String, Integer> getVertexList(String vertex) {
        return adjacentVertices.get(vertex);
    }

    /** Boruvka's algorithm */
    public Graph getMST() {
        steps.clear();
        stepIndex = 0;
        stepCount = 0;
        
        Graph MST = new Graph();
        
        for (Map.Entry<String, HashMap<String, Integer>> entry : adjacentVertices.entrySet()) {
            MST.addVertex(entry.getKey());
        }
        
        HashMap<String, String[]> connectedComponents = getConnectedComponents(MST);

        while (connectedComponents.size() > 1 && getMinEdges(this, MST, connectedComponents) > 0) {
            connectedComponents = getConnectedComponents(MST);
        }

        return MST;
    }

    public boolean searchVertex(String vertex) {
        if (adjacentVertices.containsKey(vertex)) {
            return true;
        }
        return false;
    }

    public boolean hasEdge(String sourceVertex, String targetVertex) {
        if (!searchVertex(sourceVertex) || !searchVertex(targetVertex)) {
            return false;
        }
        else if (adjacentVertices.get(sourceVertex).containsKey(targetVertex) && adjacentVertices.get(targetVertex).containsKey(sourceVertex)) {
            return true;
        }
        return false;
    }

    /** BFS */
    private HashMap<String, String[]> getConnectedComponents(Graph graph) {
        HashMap<String, String[]> connectedComponents = new HashMap<String, String[]>();
        
        int index = 0;
        int size = graph.adjacentVertices.size();
        String[] visitedVertices = new String[size];
        String[] vertices = new String[0];

        ArrayDeque<String> queue = new ArrayDeque<String>();

        String component = new String();

        for (Map.Entry<String, HashMap<String, Integer>> entry : graph.adjacentVertices.entrySet()) {
            String vertex = entry.getKey();
            if (queue.isEmpty()) {
                component = new String(vertex);
            }
            if (!find(visitedVertices, index, vertex)) {
                visitedVertices[index++] = vertex;
                queue.add(vertex);
                vertices = new String[size];
                int verticesStringIndex = 0;
                while (!queue.isEmpty()) {
                    String vertexFromQueue = queue.pop();
                    vertices[verticesStringIndex++] = vertexFromQueue;
                    HashMap<String, Integer> value = graph.adjacentVertices.get(vertexFromQueue);
                    for (Map.Entry<String, Integer> v : value.entrySet()) {
                        String key = v.getKey();
                        if (!find(visitedVertices, index, key)) {
                            visitedVertices[index++] = key;
                            queue.add(key);
                        }
                    }
                }
                connectedComponents.put(component, vertices);
            }
        }

        return connectedComponents;
    }

    private boolean find(String[] array, int size, String e) {
        for (int i = 0; i < size; ++i) {
            if (e.equals(array[i])) {
                return true;
            }
        }
        return false;
    }

    private Integer getMinEdges(Graph graph, Graph MST, HashMap<String, String[]> connectedComponents) {
        Integer counter = 0;
        MST.print();
        
        for (Map.Entry<String, String[]> entry : connectedComponents.entrySet()) {
            System.out.print(entry.getKey() + ":");
            for (String it : entry.getValue()) {
                System.out.print(it + " ");
            }
        }
        System.out.println();

        for (Map.Entry<String, String[]> entry : connectedComponents.entrySet()) {
            String sourceVertex = null;
            String targetVertex = null;
            Integer weight = Integer.MAX_VALUE;
            for (String it : entry.getValue()) {
                if (it != null){
                    for (Map.Entry<String, Integer> value : graph.adjacentVertices.get(it).entrySet()) {
                        if (value.getValue() < weight && !find(connectedComponents.get(entry.getKey()), connectedComponents.get(entry.getKey()).length, value.getKey())) {
                            sourceVertex = it;
                            targetVertex = value.getKey();
                            weight = value.getValue();
                            System.out.println(sourceVertex + ":" + targetVertex + "," + weight);
                        }
                    }
                }
            }
            if (sourceVertex != null && targetVertex != null) {
                MST.adjacentVertices.get(sourceVertex).put(targetVertex, weight);
                MST.adjacentVertices.get(targetVertex).put(sourceVertex, weight);
                counter++;
                String[] edge = new String[2];
                edge[0] = sourceVertex;
                edge[1] = targetVertex;
                steps.add(edge);
                stepCount++;
            }
            sourceVertex = null;
            targetVertex = null;
            weight = Integer.MAX_VALUE;
        }

        return counter;
    }

    @Override
    public String toString() {
        // TODO
        return "";
    }

    public String[] nextStep() {
        if (stepIndex < stepCount - 1) {
            if (forward) {
                return steps.get(stepIndex++);
            }
            return steps.get(++stepIndex);
        }
        return steps.get(stepIndex);
    }

    public String[] prevStep() {
        if (stepIndex > 0){
            if (forward) {
                return steps.get(--stepIndex);
            }
            return steps.get(stepIndex--);
        } 
        return steps.get(stepIndex);
    }

    public String[] restoreSteps() {
        forward = false;
        stepIndex = 0;
        return steps.get(stepIndex);
    }

    public ArrayList<String[]> resultStep() {
        forward = true;
        stepIndex = stepCount - 1;
        return steps;
    }
    
    
    
    public void print() {
        System.out.println("Graph:");
        for (Map.Entry<String, HashMap<String, Integer>> entry : adjacentVertices.entrySet()) {
            String key = entry.getKey();
            HashMap<String, Integer> value = entry.getValue();
            System.out.print(key.toString() + ": ");
            for (Map.Entry<String, Integer> v : value.entrySet()) {
                System.out.print(v.getKey().toString() + "(" + v.getValue() + ") ");
            }
            System.out.println();
        }
    }
}