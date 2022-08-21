import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import jdk.jfr.Timestamp;

public class GraphTest {
    private Graph graph;

    @BeforeEach
    public void setUp() throws Exception {
        graph = new Graph();

        graph.addVertex("a");
        graph.addVertex("b");
        graph.addVertex("c");
        graph.addVertex("d");
        graph.addVertex("e");
        graph.addVertex("f");
        graph.addVertex("g");
        graph.addVertex("h");
        graph.addVertex("i");

        graph.addEdge("a", "b", 2);
        graph.addEdge("a", "c", 15);
        graph.addEdge("a", "g", 7);
        graph.addEdge("b", "g", 4);
        graph.addEdge("c", "b", 13);
        graph.addEdge("c", "i", 5);
        graph.addEdge("c", "d", 22);
        graph.addEdge("d", "f", 5);
        graph.addEdge("d", "g", 9);
        graph.addEdge("e", "b", 2);
        graph.addEdge("e", "h", 13);
        graph.addEdge("f", "c", 7);
        graph.addEdge("f", "i", 17);
        graph.addEdge("g", "c", 13);
        graph.addEdge("h", "a", 8);
        graph.addEdge("h", "i", 9);
        graph.addEdge("i", "b", 11);
        graph.addEdge("i", "a", 1);
    }

    @Test
    public void testAddVertex() {
        assertTrue(graph.addVertex("j"), "Vertex should be add if such not exist");
    }

    @Test
    public void testAddEdge() {
        assertTrue(graph.addEdge("a", "d"), "Edge should be add if shuch not exist and vertices exist");
    }

    @Test
    public void testAddWeight() {
        assertTrue(graph.addWeight("a", "b", 10), "Weight should be add (change) if it positive and egde exists");
    }

    @Test
    public void testRemoveVertex() {
        assertTrue(graph.removeVertex("a"), "Vertex shold be remove if such exist");
    }

    @Test
    public void testRemoveEdge() {
        assertTrue(graph.removeEdge("a", "b"), "Edge should be remove if such exist");
    }
}
