/**
 * PDFLayoutAnalyzer.java
 *
 * Created on 16. 10. 2022, 10:35:37 by burgetr
 */
package cz.vutbr.fit.layout.demo.layout;

import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.SAXException;

import cz.vutbr.fit.layout.model.Area;
import cz.vutbr.fit.layout.model.AreaTree;
import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.pdf.PDFBoxTreeProvider;
import cz.vutbr.fit.layout.segm.BasicSegmProvider;
import cz.vutbr.fit.layout.segm.op.FindLineOperator;
import cz.vutbr.fit.layout.segm.op.SortByPositionOperator;

/**
 * Renders a PDF document and performs basic layout analysis:
 * <ul>
 * <li>Sorting boxes by their coordinates
 * <li>Joining boxes that form a single line
 * <li>Finding significant text flow changes - new lines and horizontal spaces to separate data fields.
 * </ul>
 * 
 * @author burgetr
 */
public class PDFLayoutAnalyzer
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Usage: PDFLayoutAnaylzer <filename>|<url>");
            System.exit(1);
        }
        String urlString = args[0];
        if (!urlString.contains("://"))
            urlString = "file://" + urlString;
        
        try {
            URL url = new URL(urlString);
            
            // setup the renderer: we render all pages with the zoom 1.5
            var renderer = new PDFBoxTreeProvider(url, true, false, 1.5f, 0, Integer.MAX_VALUE);
            
            // 1. perform page rendering
            Page page = renderer.getPage();
            
            // 2. identify basic visual areas
            var segm = new BasicSegmProvider(true);
            AreaTree atree = segm.createAreaTree(page);
            
            // 3. area tree postprocessing using the built-in FitLayout operators
            // a. sort areas by their coordinates
            var sortOp = new SortByPositionOperator();
            sortOp.apply(atree);
            
            // b. detect lines
            var linesOp = new FindLineOperator(true, false, 0.9f);
            linesOp.apply(atree);
            
            // 4. format leaf areas to stdout
            var out = new AreaOutput(System.out);
            printAreas(atree.getRoot(), out);
            
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the leaf areas of the given subtree.
     * 
     * @param root Subtree root
     * @param out The output formatter used for printing the areas.
     */
    public static void printAreas(Area root, AreaOutput out)
    {
        if (root.isLeaf())
        {
            out.printArea(root);
        }
        else
        {
            for (var child : root.getChildren())
            {
                printAreas(child, out);
            }
        }
    }
    
    /**
     * A simple output formatter that detects changes in the X and Y coordinates of the areas
     * and tries to resemble it using field and line separators. 
     * 
     * @author burgetr
     */
    public static class AreaOutput
    {
        private static final String FIELD_SEPARATOR = " | ";
        
        private PrintStream out;
        private int lastX = 0;
        private int lastY = 0;

        public AreaOutput(PrintStream out)
        {
            this.out = out;
        }
        
        public void printArea(Area a)
        {
            var bounds = a.getBounds();
            var em = a.getTextStyle().getFontSize();
            // end the line when the Y coordinate changes significantly 
            var difY = bounds.getY1() - lastY;
            if (difY > 0.25f * bounds.getHeight())
            {
                out.println();
                lastX = 0;
            }
            // print the field separator when the X coordinate changes significantly
            var difX = bounds.getX1() - lastX;
            if (difX > 1.0f * em)
            {
                out.print(FIELD_SEPARATOR);
            }
            // print space when the X coordinate changes a bit
            else if (difX > 0.3f * em)
            {
                out.print(" ");
            }
            // print the contents
            if (a.getTextStyle().getFontWeight() > 0.75f)
                out.print("*" + a.getText() + "*"); // "bold" text
            else
                out.print(a.getText()); // "normal" text
            
            lastX = bounds.getX2();
            lastY = bounds.getY1();
        }
        
    }

}
