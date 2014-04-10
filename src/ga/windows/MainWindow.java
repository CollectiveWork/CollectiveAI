package ga.windows;

import ga.GeneticAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Created by AndreiMadalin on 4/8/14.
 */
public class MainWindow {

    JFrame frame = new JFrame();
    public DrawPanel draw_panel;
    public JTextArea output;

    public MainWindow(String title,int x,int y){
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        frame.setTitle(title);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setSize(new Dimension(x,y));

        draw_panel = new DrawPanel();
        frame.add(draw_panel);


//        draw_panel.setSize(new Dimension(x,y));
//        frame.add(draw_panel);

//        output = new JTextArea((x/20),(y/20));
//        panel.add(output);
//        output.setFocusable(false);





    }
}
