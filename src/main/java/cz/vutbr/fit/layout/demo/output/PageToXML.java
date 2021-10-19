/**
 * PageToXML.java
 *
 * Created on 19. 10. 2021, 17:42:12 by burgetr
 */
package cz.vutbr.fit.layout.demo.output;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.SAXException;

import cz.vutbr.fit.layout.cssbox.CSSBoxTreeProvider;
import cz.vutbr.fit.layout.io.ArtifactStreamOutput;
import cz.vutbr.fit.layout.model.Page;

/**
 * Shows rendering a page and serializing the result in an XML file.
 * 
 * @author burgetr
 */
public class PageToXML
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try {
            URL url = new URL("http://cssbox.sf.net");
            
            // setup the renderer
            var renderer = new CSSBoxTreeProvider(url, 1200, 800);
            renderer.setIncludeScreenshot(false); // we don't need screen shot in this demo
            
            // perform page rendering
            Page page = renderer.getPage();
            
            // read page information
            System.out.println("Rendered page");
            System.out.println("Url: " + page.getSourceURL());
            System.out.println("Title: " + page.getTitle());
            System.out.println("Rendered size: " + page.getWidth() + " x " + page.getHeight() + " px");
            
            // output to xml
            OutputStream os = new FileOutputStream("page.xml");
            ArtifactStreamOutput.outputXML(page, os);
            os.close();
            System.out.println("Output written to page.xml");
            
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

}
