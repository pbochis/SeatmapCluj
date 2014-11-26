import be.quodlibet.boxable.PdfCell;
import be.quodlibet.boxable.PdfRow;
import be.quodlibet.boxable.PdfTable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * @author: Paul Bochis, Catalysts GmbH
 */
public class SeatmapGenerator {

    public String EMPLOYEES_FILE_NAME;
    public String ROOMS_FILE_NAME;
    private String currentDir;

    private static final int NUMBER_OF_TALBES_IN_ROOM = 8;

    public SeatmapGenerator(String currentDir){
        this.currentDir = currentDir;
        EMPLOYEES_FILE_NAME = currentDir + "/employees.txt";
        ROOMS_FILE_NAME = currentDir + "/rooms.txt";
    }

    public List<String> readData(String filename, boolean sorted){
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            List<String> data = new ArrayList<String>();
            String entity;
            while ((entity = br.readLine()) != null)
                if (!entity.equals(""))
                    data.add(entity);
            br.close();
            if (sorted)
                Collections.sort(data);
            else
                Collections.shuffle(data);
            return data;
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }

    public void writeData(String filename, List<String> values){
        try {
            Collections.sort(values);
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            for (String s : values){
                bw.write(s);
                bw.newLine();
            }
            bw.close();
        }
        catch (Exception io){
            System.out.println(io);
        }
    }

    public void generateSeatmap(List<String> employees, List<String> rooms, Date date) throws NotEnoughRoomsException{
            Collections.shuffle(rooms);
            Collections.shuffle(employees);
            LinkedHashMap<String, List<String>> roomsWithTables = expandRooms(rooms);
            generateSeatMap(roomsWithTables, rooms, employees, date);
    }

    private void generateSeatMap(LinkedHashMap<String, List<String>> roomsWithTables, List<String> rooms, List<String> employees, Date date) throws NotEnoughRoomsException{
        int totalEmployees = employees.size();
        LinkedHashMap<String, String> assignedSeats = new LinkedHashMap<String, String>();
        Random rand = new Random();
        int i = 0;
        int randomSpot;
        int randomEmployee;
        while (employees.size() > 0) {
            String room = rooms.get(i);
            if(roomsWithTables.get(room).size() <= 0){
                throw new NotEnoughRoomsException(totalEmployees);
            }
            randomSpot = rand.nextInt(roomsWithTables.get(room).size());
            randomEmployee = rand.nextInt(employees.size());
            assignedSeats.put(employees.get(randomEmployee), roomsWithTables.get(room).get(randomSpot));
            employees.remove(randomEmployee);
            roomsWithTables.get(room).remove(randomSpot);
            i = i == rooms.size() - 1 ? 0 : i + 1;
        }
        generateSeatmapDocument(assignedSeats, date);
    }

    private LinkedHashMap<String, String> invertMap(LinkedHashMap<String, String> map){
        LinkedHashMap<String, String> newMap = new LinkedHashMap<String, String>();
        for (String key : map.keySet()){
            newMap.put(map.get(key), key);
        }
        return newMap;
    }

    private void generateSeatmapDocument(LinkedHashMap<String, String> seats, Date date) {
        float Margin = 10;
        seats = invertMap(seats);
        try {
            PDDocument doc = new PDDocument();
            PDPage page = addNewPage(doc);
            PDPageContentStream pageContentStream = new PDPageContentStream(doc, page);
            float tableWidth = page.findMediaBox().getWidth() - (2 * Margin);
            float top = page.findMediaBox().getHeight() - (2 * Margin);
            PdfTable table = new PdfTable((top - (1 * 20f)), Margin, page, pageContentStream);

            PdfRow headerrow = new PdfRow(25f);
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, 14);
            Date toDate = cal.getTime();

            PdfCell cell = new PdfCell(tableWidth, "Assigned seats starting from " + df.format(date) + " to " + df.format(toDate));
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFontSize(16);
            headerrow.addCell(cell);
            table.drawRow(headerrow);

            List<String> tables = new ArrayList<String>(seats.keySet());
            Collections.sort(tables);
            for (String seat : tables) {
                String employee = seats.get(seat);
                PdfRow row = new PdfRow(20f);
                row.addCell(new PdfCell(tableWidth / 2, employee));
                row.addCell(new PdfCell(tableWidth / 2, seat));
                table.drawRow(row);
            }
            table.endTable(tableWidth);
            pageContentStream.close();
            System.out.println(currentDir);
            doc.save(currentDir+ "\\Seatmap" + new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss").format(new Date()) + ".pdf");
        } catch (Exception io) {
            System.out.println(io);
        }


    }

    private PDPage addNewPage(PDDocument doc) {
        PDPage page = new PDPage();
        doc.addPage(page);
        return page;
    }

    private LinkedHashMap<String, List<String>> expandRooms(List<String> rooms) {
        LinkedHashMap<String, List<String>> roomsWithTables = new LinkedHashMap<String, List<String>>();
        for (String room : rooms) {
            List<String> tables = new ArrayList<String>();
            for (int i = 1; i <= NUMBER_OF_TALBES_IN_ROOM; i++) {
                tables.add(room + "." + i);
            }
            roomsWithTables.put(room, tables);
        }
        return roomsWithTables;
    }

}
