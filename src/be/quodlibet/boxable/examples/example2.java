/*
 Quodlibet.be
 */
package be.quodlibet.boxable.examples;

import be.quodlibet.boxable.PdfCell;
import be.quodlibet.boxable.PdfRow;
import be.quodlibet.boxable.PdfTable;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;


public class example2
{
 public static void main(String[] args)
    {
        try
        {
            //Set margins
            float Margin = 10;
            List<String[]> facts = getFacts();
            //A list of bookmarks of all the tables
            List<PDOutlineItem> bookmarks = new ArrayList();
            //Initialize Document
            PDDocument doc = new PDDocument();
            PDPage page = addNewPage(doc);
            PDPageContentStream pageContentStream = new PDPageContentStream(doc, page);
            //Initialize table
            float tableWidth = page.findMediaBox().getWidth()-(2*Margin);
            float top = page.findMediaBox().getHeight()-(2*Margin);
            PdfTable table  = new PdfTable( (top - (1* 20f)),Margin, page,  pageContentStream);
            //Create Header row
            PdfRow headerrow = new PdfRow(15f);
            PdfCell cell = new PdfCell(tableWidth,"Awesome Facts About Belgium");
            cell.setFont(PDType1Font.HELVETICA_BOLD);
            cell.setFillColor(Color.BLACK);cell.setTextColor(Color.WHITE);
            headerrow.addCell(cell);
            table.drawRow(headerrow);
            //Create 2 column row
            PdfRow row = new PdfRow(15f);
            cell = new PdfCell((tableWidth/4) * 3 ,"Source:");
            cell.setFont(PDType1Font.HELVETICA);
            row.addCell(cell);
            cell = new PdfCell((tableWidth/4),"http://www.factsofbelgium.com/");
            cell.setFont(PDType1Font.HELVETICA_OBLIQUE);
            row.addCell(cell);
            table.drawRow(row);
            //Create Fact header row
            PdfRow factHeaderrow = new PdfRow(15f);
            cell = new PdfCell((tableWidth/3) * 2 ,"Fact");
            cell.setFont(PDType1Font.HELVETICA);cell.setFontSize(6);
            cell.setFillColor(Color.LIGHT_GRAY);
            factHeaderrow.addCell(cell);
            cell = new PdfCell((tableWidth/3),"Tags");
            cell.setFillColor(Color.LIGHT_GRAY);
            cell.setFont(PDType1Font.HELVETICA_OBLIQUE);cell.setFontSize(6);
            factHeaderrow.addCell(cell);
            table.drawRow(factHeaderrow);

            //Add multiple rows with random facts about Belgium
            int bookmarkid = 0;
            for(String[] fact : facts)
            {

                row = new PdfRow(10f);
                cell = new PdfCell((tableWidth/3)*2 ,fact[0]+ " " + fact[0]+ " " + fact[0]);
                cell.setFont(PDType1Font.HELVETICA);cell.setFontSize(6);
                row.addCell(cell);
                //Create a bookmark for each record
                PDOutlineItem outlineItem = new PDOutlineItem();
                outlineItem.setTitle((++bookmarkid ) + ") " + fact[0]);
                row.setBookmark( outlineItem);

                for(int i = 1; i< fact.length; i++)
                {
                    cell = new PdfCell(((tableWidth/9)) ,fact[i]);
                    cell.setFont(PDType1Font.HELVETICA_OBLIQUE);cell.setFontSize(6);
                    //Set colors
                    if(fact[i].contains("beer"))cell.setFillColor(Color.yellow);
                    if(fact[i].contains("champion"))cell.setTextColor(Color.GREEN);
                    row.addCell(cell);
                    
                }
                table.drawRow(row);
                //Start a new page if needed
                if(table.isEndOfPage() )
                {
                    pageContentStream.close();
                    //Start new table on new page
                    page = addNewPage(doc);
                    pageContentStream = new PDPageContentStream(doc, page);
                    //Get all bookmarks of previous table
                    bookmarks.addAll(table.getBookmarks());

                    table = new PdfTable( (top - (1* 20f)),Margin, page,  pageContentStream);

                    //redraw all headers on each page
                    table.drawRow(headerrow);
                    table.drawRow(factHeaderrow);
                }

            }
            table.endTable(tableWidth);
            //Get all bookmarks of previous table
            bookmarks.addAll(table.getBookmarks());
            //Close Stream and save pdf
            pageContentStream.close();
            //Create document outline
            PDDocumentOutline outline =  new PDDocumentOutline();
            for(PDOutlineItem bm : bookmarks)
            {
                outline.appendChild(bm);
            }
            doc.getDocumentCatalog().setDocumentOutline(outline);
            //Save the document
            doc.save("Boxable_example2.pdf");

        }
        catch (IOException io)
        {
            System.out.println(io.getMessage());
        }
        catch (COSVisitorException io){
            System.out.println(io.getMessage());
        }
    }
    private static PDPage addNewPage(PDDocument doc)
    {
        PDPage page = new PDPage();
        doc.addPage(page);
        return page;
    }
    private static List<String[]> getFacts()
    {
        List<String[]> facts = new ArrayList();
        facts.add(new String[]{"Oil Painting was invented by the Belgian van Eyck brothers","art","inventions","science"});
        facts.add(new String[]{"The Belgian Adolphe Sax invented the Saxophone","inventions","music",""});
        facts.add(new String[]{"11 sites in Belgium are on the UNESCO World Heritage List","art","history",""});
        facts.add(new String[]{"Belgium was the second country in the world to legalize same-sex marriage","politics","",""});
        facts.add(new String[]{"In the seventies, schools served light beer during lunch","beer","health",""});
        facts.add(new String[]{"Belgium has the sixth fastest domestic internet connection in the world","science","technology",""});
        facts.add(new String[]{"Belgium hosts the World's Largest Sand Sculpture Festival","art","festivals","world championship"});
        facts.add(new String[]{"Belgium has compulsary education between the ages of 6 and 18","education","",""});
        facts.add(new String[]{"Belgium also has more comic makers per square kilometer than any other country in the world","art","social","world championship"});
        facts.add(new String[]{"Belgium has one of the lowest proportion of McDonald's restaurants per inhabitant in the developed world","food","health",""});
        facts.add(new String[]{"Belgium has approximately 178 beer breweries","beer","food",""});
        facts.add(new String[]{"Gotye was born in Bruges, Belgium","music","celebrities",""});
        facts.add(new String[]{"The Belgian Coast Tram is the longest tram line in the world","technology","world championship",""});
        facts.add(new String[]{"Stefan Everts is the only motocross racer with 10 World Championship titles.","celebrities","sports","world champions"});
        facts.add(new String[]{"Tintin was conceived by Belgian artist Hergé","art","celebrities","inventions"});
        facts.add(new String[]{"Brussels Airport is the world's biggest selling point of chocolate","food","world champions",""});
        facts.add(new String[]{"Tomorrowland is the biggest electronic dance music festival in the world","festivals","music","world champion"});
        facts.add(new String[]{"French Fries are actually from Belgium","food","inventions",""});
        facts.add(new String[]{"Herman Van Rompy is the first full-time president of the European Council","politics","",""});
        facts.add(new String[]{"Belgians are the fourth most money saving people in the world","economy","social",""});
        facts.add(new String[]{"The Belgian highway system is the only man-made structure visible from the moon at night","technology","world champions",""});
        facts.add(new String[]{"Andreas Vesalius, the founder of modern human anatomy, is from Belgium","celebrities","education","history"});
        facts.add(new String[]{"Napoleon was defeated in Waterloo, Belgium","celebrities","history","politicians"});
        facts.add(new String[]{"The first natural color picture in National Geographic was of a flower garden in Gent, Belgium in 1914","art","history","science"});
        facts.add(new String[]{"Rock Werchter is the Best Festival in the World","festivals","music","world champions"});

        //Make the table a bit bigger
        facts.addAll(facts);facts.addAll(facts);facts.addAll(facts);


        return facts;
    }
}
