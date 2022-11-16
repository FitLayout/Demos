/**
 * RenderPlaywright.java
 *
 * Created on 16. 11. 2022, 13:37:47 by burgetr
 */
package cz.vutbr.fit.layout.demo.render;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import cz.vutbr.fit.layout.demo.OutputUtils;
import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.playwright.PlaywrightTreeProvider;

/**
 * Renders a page using the Chromium (playwright) rendering backend.
 * When running for the first time, the Chromium backend will be installed
 * automatically.
 * 
 * @author burgetr
 */
public class RenderPlaywright
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try {
            URL url = new URL("http://cssbox.sf.net");
            
            // setup the renderer
            var renderer = new PlaywrightTreeProvider(url, 1200, 800);
            renderer.setIncludeScreenshot(false); // we don't need screen shot in this demo
            renderer.setPersist(3);
            
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
        } catch (InterruptedException e)  {
            e.printStackTrace();
        }
    }

}
