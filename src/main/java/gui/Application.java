package gui;
import graph.*;
import io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import java.io.File;



public class Application {
    Graph graph;
    GraphVisualizer view;
    JFrame frame;
    
    //Save/load
    JsonGraphIO json_io = new JsonGraphIO();
    JFileChooser fc = new JFileChooser();
    
    //Edge panel
    JTextField weightField = new JTextField(3);
    JPanel edge_panel = new JPanel();
    Object[] options = { "Ok", "Delete" };

    //Save/Load Graph
    JButton save_btn = new JButton("Save");
    JButton load_btn = new JButton("Load");
    
    //Buttons
    JButton random_btn = new JButton("Random");
    JButton run_btn = new JButton("Run");
    JButton next_step_btn = new JButton("Next");  
    JButton prev_step_btn = new JButton("Prev");  
    JButton restart_step_btn = new JButton("Restart");   
    JButton result_btn = new JButton("Result");     
    
    EdgeChangedListener edge_changed = new EdgeChangedListener() {
        public int myEventOccurred(int current_weight) {
            EdgeDialog dialog = new EdgeDialog(frame, current_weight);
            dialog.setVisible(true);
            int result = dialog.exitCode();
            if(result == 0) {
                return dialog.weight();
            }
            if(result == 1) {
                return -2;
            }
            return -1;
        };
    };

    //Cards
    JPanel edit_card = new JPanel();
    JPanel run_card = new JPanel();
    JPanel cards;
    JComboBox combo_box;
    String[] combo_box_items = {"edit", "run"};
    
    boolean events = true;
    GridBagConstraints c = new GridBagConstraints();
    
    
    public Application() {
        frame = new JFrame("Summer practice");  
        graph = new Graph();
        view = new GraphVisualizer(graph);
        view.addMyEventListener(edge_changed);
        
        frame.setSize(1024,720);  
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;
        frame.setLocation(x,y);
        
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        //Json files only
        FileNameExtensionFilter filter = new FileNameExtensionFilter(null,"json");
        fc.setFileFilter(filter);
        ///////////////////////////
        createUI();
        initListeners();
        frame.setVisible(true);  
    }
    
    
    private void createUI() {
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridheight = 1;
        c.gridwidth = 1;
        
        //Cards run/edit
        cards = new JPanel(new CardLayout());
        edit_card.setLayout(new GridBagLayout());
        edit_card.add(save_btn, c);
        c.gridx = 1;
        edit_card.add(load_btn, c);
        c.gridx = 2;
        edit_card.add(random_btn, c);
        
        run_card.setLayout(new GridBagLayout());
        c.gridy = 1;
        c.gridx = 0;
        run_card.add(restart_step_btn, c);
        c.gridx = 1;
        run_card.add(prev_step_btn, c);
        c.gridx = 2;
        run_card.add(next_step_btn, c);
        c.gridx = 3;
        run_card.add(result_btn, c);
         
        //Edge delete/change panel
        edge_panel.add(new JLabel("weight:"));
        edge_panel.add(weightField);
        
        cards.add(edit_card, "edit");
        cards.add(run_card, "run");
        combo_box = new JComboBox(combo_box_items);
        
        frame.setLayout(new GridBagLayout());
        
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.1;
        c.weighty = 0.001;
        c.gridheight = 1;
        c.gridwidth = 1;
        frame.add(combo_box, c);
        
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.001;
        c.gridheight = 1;
        c.gridwidth = 1;
        frame.add(cards, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0.9;
        c.gridheight = 1;
        c.gridwidth = 2;
        frame.add(view, c);
        
        cards.setFocusable(false);
        combo_box.setFocusable(false);
        save_btn.setFocusable(false);
        load_btn.setFocusable(false);
        run_btn.setFocusable(false);
        random_btn.setFocusable(false);
        result_btn.setFocusable(false);
        next_step_btn.setFocusable(false);
        prev_step_btn.setFocusable(false);
        restart_step_btn.setFocusable(false);
    }


    private void initListeners() {
        //Listeners---------------------------------------------------
        combo_box.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                CardLayout cl = (CardLayout)(cards.getLayout());
                cl.show(cards, (String)evt.getItem());
                String s = (String)evt.getItem();
                if(s.equals("edit") && !events) {
                    
                    events = true;
                    view.enableEvents(events);
                    view.setDefaultStyle();
                }
                else if(s.equals("run") && events) {
                    graph.getMST();
                    System.out.print("MST");
                    graph.print();
                    events = false;
                    view.enableEvents(events);
                }
                view.repaint();

            }
        });
        
        
        
        next_step_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String[] step = graph.nextStep();
                view.highlightEdge(step[0], step[1], new RGB(0,255,0), 5);
                view.highlightEdge(step[1], step[0], new RGB(0,255,0), 5);
                
            }
        
        });
        
        
        
        prev_step_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String[] step = graph.prevStep();
                view.highlightEdge(step[0], step[1], new RGB(0,0,0), 1);
                view.highlightEdge(step[1], step[0], new RGB(0,0,0), 1);
                
            }
        
        });
        
        
        
        load_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fc.getSelectedFile();
                      try {
                          graph = json_io.import_graph(selectedFile.getAbsolutePath());
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                        return;
                    }
                }
                else {
                    return;
                }
                
                  view = new GraphVisualizer(graph);
                  view.addMyEventListener(edge_changed);
                  frame.getContentPane().remove(2);
                  c.gridx = 0;
                  c.gridy = 1;
                  c.weightx = 1;
                  c.weighty = 1;
                  c.gridheight = 1;
                  c.gridwidth = 4;
                  frame.getContentPane().add(view, c, 2);
                  frame.revalidate();
                  frame.repaint();
            }
        
        });
        
        
        
        save_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fc.getSelectedFile();
                    String file_path = fileToSave.getAbsolutePath().concat(".json");
                    json_io.export_graph(graph, file_path);
                }	
            }
        });
        
        
        
        
        random_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                
                GraphGenerationDialog dialog = new GraphGenerationDialog(frame);
                dialog.setVisible(true);
                
                if(dialog.exitCode() == 0) {
                      GraphCreator creator = new GraphCreator(dialog.vertices(), dialog.edges(), dialog.min_weight(), dialog.max_weight());
                      graph = creator.create();
                      view = new GraphVisualizer(graph);
                      view.addMyEventListener(edge_changed);
                      frame.getContentPane().remove(2);
                      c.gridx = 0;
                      c.gridy = 1;
                      c.weightx = 1;
                      c.weighty = 1;
                      c.gridheight = 1;
                      c.gridwidth = 4;
                      frame.getContentPane().add(view, c, 2);
                      frame.revalidate();
                      frame.repaint();
                }
            }
        });

        
        
        
        result_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String[]> steps = graph.resultStep();
                for(String[] step : steps) {
                    view.highlightEdge(step[0], step[1], new RGB(0,255,0), 5);
                    view.highlightEdge(step[1], step[0], new RGB(0,255,0), 5);
                }
            }
        
        });
        
        
        restart_step_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String[]> steps = graph.resultStep();
                for(String[] step : steps) {
                    view.highlightEdge(step[0], step[1], new RGB(0,0,0), 1);
                    view.highlightEdge(step[1], step[0], new RGB(0,0,0), 1);
                }
                graph.getMST();
            }
        });
    }
    
}
