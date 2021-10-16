/**
 * RenderCSSBox.java
 *
 * Created on 15. 10. 2021, 20:11:59 by burgetr
 */
package cz.vutbr.fit.layout.demo.render;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.SAXException;

import cz.vutbr.fit.layout.cssbox.CSSBoxTreeProvider;
import cz.vutbr.fit.layout.demo.OutputUtils;
import cz.vutbr.fit.layout.model.Page;

/**
 * Renders a page using the CSSBox rendering backend.
 * 
 * @author burgetr
 */
public class RenderCSSBox
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
