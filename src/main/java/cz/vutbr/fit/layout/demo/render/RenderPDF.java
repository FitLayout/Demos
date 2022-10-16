/**
 * RenderPDF.java
 *
 * Created on 16. 10. 2022, 10:18:13 by burgetr
 */
package cz.vutbr.fit.layout.demo.render;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.SAXException;

import cz.vutbr.fit.layout.demo.OutputUtils;
import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.pdf.PDFBoxTreeProvider;

/**
 * Renders a PDF document using the PDF rendering backend.
 * 
 * @author burgetr
 */
public class RenderPDF
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Usage: RenderPDF <filename>|<url>");
            System.exit(1);
        }
        String urlString = args[0];
        if (!urlString.contains("://"))
            urlString = "file://" + urlString;
        
        try {
            URL url = new URL(urlString);
            
            // setup the renderer: we render all pages with the zoom 1.5
            var renderer = new PDFBoxTreeProvider(url, true, false, 1.5f, 0, Integer.MAX_VALUE);
            
            // perform page rendering
            Page page = renderer.getPage();
            
            // read page information
            System.out.println("Rendered page");
            System.out.println("Url: " + page.getSourceURL());
            System.out.println("Title: " + page.getTitle());
            System.out.println("Rendered size: " + page.getWidth() + " x " + page.getHeight() + " px");
            
            // print boxes
            System.out.println("Text boxes:");
            OutputUtils.printTextBoxes(page.getRoot());
            
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

}
