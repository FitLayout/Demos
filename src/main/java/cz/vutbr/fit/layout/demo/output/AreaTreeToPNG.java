/**
 * AreaTreeToPNG.java
 *
 * Created on 19. 10. 2021, 17:47:42 by burgetr
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
import cz.vutbr.fit.layout.model.AreaTree;
import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.vips.VipsProvider;

/**
 * Shows rendering a page, performing page semgnetation and storing the result as a PNG image.
 * 
 * @author burgetr
 */
public class AreaTreeToPNG
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
            renderer.setIncludeScreenshot(true); // we do need a screen shot
            
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
            
            // output to PNG (screen shot)
            OutputStream os = new FileOutputStream("areas.png");
            ArtifactStreamOutput.outputPNG(atree, page, os);
            os.close();
            System.out.println("Output written to areas.png (page screen shot)");
            
            // output to PNG (internal model)
            OutputStream osi = new FileOutputStream("areas_model.png");
            ArtifactStreamOutput.outputPNGi(atree, page, osi);
            osi.close();
            System.out.println("Output written to areas_model.png (internal model of the page)");
            
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

}
