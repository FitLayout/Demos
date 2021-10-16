/**
 * SegmVIPS.java
 *
 * Created on 16. 10. 2021, 14:06:58 by burgetr
 */
package cz.vutbr.fit.layout.demo.segmentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.SAXException;

import cz.vutbr.fit.layout.cssbox.CSSBoxTreeProvider;
import cz.vutbr.fit.layout.io.XMLOutputOperator;
import cz.vutbr.fit.layout.model.AreaTree;
import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.vips.VipsProvider;

/**
 * This demo renders a page and performs page segmentation using the VIPS algorithm.
 * The resulting area tree is then serialized in XML.
 * 
 * @author burgetr
 */
public class SegmVIPS
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try {
            URL url = new URL("http://cssbox.sf.net");
            
            // setup the CSSBox renderer
            var renderer = new CSSBoxTreeProvider(url, 1200, 800);
            renderer.setIncludeScreenshot(false); // we don't need screen shot in this demo
            
            // perform page rendering
            Page page = renderer.getPage();
            
            // read page information
            System.out.println("Rendered page");
            System.out.println("Url: " + page.getSourceURL());
            System.out.println("Title: " + page.getTitle());
            System.out.println("Rendered size: " + page.getWidth() + " x " + page.getHeight() + " px");
            
            // setup the VIPS segmentation provider
            var vips = new VipsProvider();
            vips.setPDoC(9); // the preferred degree of coherence
            
            // perform segmentation; produces an area tree
            AreaTree atree = vips.createAreaTree(page);
            
            // serialize the area tree to XML
            try (PrintWriter out = new PrintWriter(System.out)) {
                XMLOutputOperator xmlOut = new XMLOutputOperator();
                xmlOut.setProduceHeader(false);
                xmlOut.dumpTo(atree, out);
            }
            
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }

}
