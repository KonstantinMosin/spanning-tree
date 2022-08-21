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


class EdgeDialog extends JDialog implements ActionListener, PropertyChangeListener {
    private JOptionPane optionPane;
    JTextField weightField = new JTextField(3);
    JLabel error = new JLabel("   ");
    JPanel edge_panel = new JPanel();
    int exit_code = -1;
    int current_weight = 0;
    
    public EdgeDialog(Frame frame, int weight) {
        super(frame, true);
        setTitle("Edge");
        setSize(400,150);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        setLocation(x, y);

        this.setResizable(false);
        weightField.setText(String.valueOf(weight));
        Object[] options = {"Enter", "Delete"};
        error.setFont(new Font(error.getName(), Font.PLAIN, 18));
        error.setForeground(Color.red);
        edge_panel.add(new JLabel("weight: "));
        edge_panel.add(weightField);
        edge_panel.add(error);
        edge_panel.setLayout(new BoxLayout(edge_panel, BoxLayout.Y_AXIS));
        optionPane = new JOptionPane(edge_panel,
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
        System.out.println(e.getPropertyName());
        if(isVisible() && optionPane.getValue().equals("Enter")) {
            if(isNumeric(weightField.getText())) {
                int new_weight = Integer.parseInt(weightField.getText());
                if(new_weight < 0 || new_weight > 100) {
                      error.setText("weight not in [0 : 100]");
                      optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                      return;
                }
                else {
                    exit_code = 0;
                    clearAndHide();
                }
            }
            else {
                error.setText("empty/incorrect fields");
            }
        }
        else if(isVisible() && optionPane.getValue().equals("Delete")) {
            exit_code = 1;
            clearAndHide();
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
        catch(NumberFormatException e) {  
            new Log().warning("incorrect edge weight");
            return false;  
        }  
    }
        

        
    public void clearAndHide() {
        setVisible(false);
    }
    

    public int weight() {
        return Integer.parseInt(weightField.getText());
    }
    
    
    public int exitCode() {
        return exit_code;
    }
}
