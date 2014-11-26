import be.quodlibet.boxable.PdfCell;
import be.quodlibet.boxable.PdfRow;
import be.quodlibet.boxable.PdfTable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {



    public static void main(String[] args) {
        Seatmap seatmap = new Seatmap(System.getProperty("user.dir"));
        seatmap.show();
        seatmap.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
