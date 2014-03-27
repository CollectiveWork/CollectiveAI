package ga.utils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Cristi on 3/27/14.
 */
public class Log {

    FileOutputStream fos = null;
    BufferedWriter bw = null;

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    Calendar cal = Calendar.getInstance();


    public Log(String name) {
        File file;
        if (name == null) {
            file = new File("src//ga//utils//logs//" + dateFormat.format(cal.getTime()));
        } else file = new File("src//ga//utils//logs//" + name);
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bw = new BufferedWriter(new OutputStreamWriter(fos));
        try {
            bw.write("Log created at " + dateFormat.format(cal.getTime()));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
         Log lg = new Log(null);
    }

    public void write(String msg) {

        try {
            bw.write("Log entry: " + dateFormat.format(cal.getTime()));
            bw.write(msg);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
