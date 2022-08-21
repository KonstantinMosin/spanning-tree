package gui;
import graph.*;
import io.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.beans.*; 
import java.awt.*;
import java.awt.event.*;


class GraphGenerationDialog extends JDialog implements ActionListener, PropertyChangeListener {
    private JOptionPane optionPane;
    JTextField verticesField = new JTextField(3);
    JTextField edgesField = new JTextField(3);
    JTextField minwField = new JTextField(3);
    JTextField maxwField = new JTextField(3);
    JLabel error = new JLabel("   ");
    JPanel random_panel = new JPanel();
    int exit_code = -1;
    
    
    public GraphGenerationDialog(Frame frame) {
        super(frame, true);
        setTitle("Random");
        setSize(400,250);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        setLocation(x, y);
        
        this.setResizable(false);
        
        verticesField.setText("6");
        edgesField.setText("15");
        minwField.setText("1"); 
        maxwField.setText("20");
        
        Object[] options = {"Enter", "Cancel"};

        error.setFont(new Font(error.getName(), Font.PLAIN, 18));
        error.setForeground(Color.red);
        
        random_panel.add(new JLabel("vertices:"));
        random_panel.add(verticesField);
        random_panel.add(new JLabel("edges:"));
        random_panel.add(edgesField);
        random_panel.add(new JLabel("min weight:"));
        random_panel.add(minwField);
        random_panel.add(new JLabel("max weight:"));
        random_panel.add(maxwField);
        random_panel.add(error);

        random_panel.setLayout(new BoxLayout(random_panel, BoxLayout.Y_AXIS));

        optionPane = new JOptionPane(random_panel,
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    null,
                                    options,
                                    "Enter");


        setContentPane(optionPane);
        optionPane.addPropertyChangeListener(this);
    }


    public void actionPerformed(ActionEvent e) {}


    public void propertyChange(PropertyChangeEvent e) {
        if(isVisible() && optionPane.getValue().equals("Enter")) {
            if(isNumeric(verticesField.getText()) &&
                    isNumeric(edgesField.getText()) &&
                    isNumeric(minwField.getText()) &&
                    isNumeric(maxwField.getText())) {
                  
                  int number_of_vertices = Integer.parseInt(verticesField.getText());
                  if(number_of_vertices < 0 || number_of_vertices > 500) {
                      error.setText("Vertices not in [0 : 500]");
                      optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                      return;
                  }
                  int number_of_edges = Integer.parseInt(edgesField.getText());
                  if(number_of_edges < 0 || number_of_edges > number_of_vertices*(number_of_vertices-1)/2) {
                      int v = number_of_vertices*(number_of_vertices-1)/2;
                      error.setText("Edges not in [0 : " + v + "]");
                      optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                      return;
                  }
                  int min_weight = Integer.parseInt(minwField.getText());
                  if(min_weight < 0 || min_weight > 100) {
                      error.setText("min weight not in [0 : 100]");
                      optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                      return;
                  }
                  int max_weight = Integer.parseInt(maxwField.getText());
                  if(max_weight < 0 || max_weight > 100) {
                      error.setText("max weight not in [0 : 100]");
                      optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                      return;
                  }
                  if(min_weight > max_weight) {
                      error.setText("min weight > max weight");
                      optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                      return;
                  }
                  exit_code = 0;
                  clearAndHide();
            }
            else {
                error.setText("empty/incorrect fields");
            }
        }
        else if(!optionPane.getValue().equals("uninitializedValue")) {
            exit_code = -1;
            clearAndHide();
        }

        optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
    }

        
    private static boolean isNumeric(String str) { 
        try {  
            Integer.parseInt(str);  
            return true;
        } 
        catch(NumberFormatException e){  
            new Log().warning("empty/incorrect fields in graph creator");
            return false;  
        }  
    }
        

    public void clearAndHide() {
        setVisible(false);
    }
    

    public int vertices() {
        return Integer.parseInt(verticesField.getText());
    }
    

    public int edges() {
        return Integer.parseInt(edgesField.getText());
    }
    

    public int min_weight() {
        return Integer.parseInt(minwField.getText());
    }
    

    public int max_weight() {
        return Integer.parseInt(maxwField.getText());
    }
    
    
    public int exitCode() {
        return exit_code;
    }
}
