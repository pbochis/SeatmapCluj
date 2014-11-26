import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author: Paul Bochis, Catalysts GmbH
 */
public class Seatmap extends JFrame {
    private JTextArea textArea_employees;
    private JTextArea textArea_rooms;
    private JButton generateSeatmapButton;
    private JPanel panel;
    private JTextField startDate;
    private SeatmapGenerator generator;

    public Seatmap(String currentDir){
        generator = new SeatmapGenerator(currentDir);
        textArea_employees.setText(getDataForTextArea(generator.readData(generator.EMPLOYEES_FILE_NAME, true)));
        textArea_rooms.setText(getDataForTextArea(generator.readData(generator.ROOMS_FILE_NAME, false)));

        generateSeatmapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSeatmap();
            }
        });
        this.setContentPane(panel);
        this.pack();
    }

    public void generateSeatmap(){
        String date = startDate.getText();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date startingDate;
        try{
            startingDate = df.parse(date);
        }catch (ParseException ex){
            JFrame errorFrame = new JFrame("Error");
            errorFrame.add(new JLabel("Add a correct starting date. Format dd.MM.yyyy"));
            errorFrame.pack();
            errorFrame.setVisible(true);
            return;
        }

        String emp = normalizeBreaks(textArea_employees.getText());
        String roo = normalizeBreaks(textArea_rooms.getText());
        List<String> emps = cleanList(new ArrayList<String>(Arrays.asList(emp.split(System.getProperty("line.separator")))));
        List<String> rooms = cleanList(new ArrayList<String>(Arrays.asList(roo.split(System.getProperty("line.separator")))));
        generator.writeData(generator.EMPLOYEES_FILE_NAME, cleanList(emps));
        generator.writeData(generator.ROOMS_FILE_NAME, cleanList(rooms));
        try{
            generator.generateSeatmap(emps, rooms, startingDate);
        }
        catch (NotEnoughRoomsException ex){
            JFrame errorFrame = new JFrame("Error");
            errorFrame.add(new JLabel(ex.toString()));
            errorFrame.pack();
            errorFrame.setVisible(true);
        }
    }

    private List<String> cleanList(List<String> list){
        int i = 0;
        while (i < list.size()){
            if (list.get(i).equals(""))
                list.remove(i);
            else
                i++;
        }
        return list;
    }

    private String normalizeBreaks(String text){
        return text.replaceAll("\\n", System.getProperty("line.separator"));
    }

    private String getDataForTextArea(List<String> data) {
        String s="";
        for (String d : data) {
            s += d + System.getProperty("line.separator");
        }
        return s;
    }

}
