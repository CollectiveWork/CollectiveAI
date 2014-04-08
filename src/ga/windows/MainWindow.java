package ga.windows;

import javax.swing.*;
import java.awt.*;

/**
 * Created by AndreiMadalin on 4/8/14.
 */
public class MainWindow {

    JFrame frame = new JFrame();
    JPanel panel = new JPanel();
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
        output = new JTextArea((x/20),(y/20));
        frame.setVisible(true);
        frame.setSize(new Dimension(x,y));
        panel.setSize(new Dimension(x,y));

        frame.add(panel);
        panel.add(output);
        output.setFocusable(false);



    }
}
