/**
 * PageToRDF.java
 *
 * Created on 19. 10. 2021, 17:42:12 by burgetr
 */
package cz.vutbr.fit.layout.demo.rdf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.rdf4j.model.Model;
import org.xml.sax.SAXException;

import cz.vutbr.fit.layout.cssbox.CSSBoxTreeProvider;
import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.rdf.BoxModelBuilder;
import cz.vutbr.fit.layout.rdf.DefaultIRIFactory;
import cz.vutbr.fit.layout.rdf.IRIFactory;
import cz.vutbr.fit.layout.rdf.Serialization;

/**
 * Shows rendering a page and serializing the result in RDF format (turtle serialization).
 * 
 * @author burgetr
 */
public class PageToRDF
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
            
            // assign an IRI to the created page
            IRIFactory iriFactory = new DefaultIRIFactory();
            page.setIri(iriFactory.createArtifactIri(1));
            
            // output to RDF in Turtle serialization
            OutputStream os = new FileOutputStream("page.ttl");
            BoxModelBuilder builder = new BoxModelBuilder(iriFactory);
            Model graph = builder.createGraph(page);
            Serialization.modelToStream(graph, os, Serialization.TURTLE);
            os.close();
            System.out.println("Output written to page.ttl");
            
        } catch (MalformedURLException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

}
