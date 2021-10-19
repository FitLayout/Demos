/**
 * RDFArtifactStorage.java
 *
 * Created on 19. 10. 2021, 20:09:14 by burgetr
 */
package cz.vutbr.fit.layout.demo.rdf;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.xml.sax.SAXException;

import cz.vutbr.fit.layout.cssbox.CSSBoxTreeProvider;
import cz.vutbr.fit.layout.model.AreaTree;
import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.rdf.RDFArtifactRepository;
import cz.vutbr.fit.layout.vips.VipsProvider;

/**
 * Shows rendering a page, creating a visual area tree and storing both artifacts in a RDF artifact
 * repository. By default, an in-memory storage is used. See commented options in the code for using
 * a native (local filesystem) or remote storage over HTTP.
 * 
 * Next, a SPARQL query is executed in order to get all boxes in the page, their font sizes and
 * text contents.
 *  
 * @author burgetr
 */
public class RDFStorageSPARQL
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        try {
            URL url = new URL("http://cssbox.sf.net");
            
            // setup an RDF repository
            // in-memory storage with no persistence
            var repository = RDFArtifactRepository.createMemory(null);
            
            // -or- a persistent native storage in local filesystem
            //var repository = RDFArtifactRepository.createNative("/tmp/storage");
            
            // -or- an RDF4J storage over HTTP
            //var repository = RDFArtifactRepository.createHTTP("http://localhost:8080/rdf4j", "fitlayout");
            
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
            
            // store the page in the repository (includes assigning a new IRI to the page)
            repository.addArtifact(page);
            System.out.println("Page IRI: " + page.getIri());
            
            // setup the VIPS segmentation provider
            var vips = new VipsProvider();
            vips.setPDoC(9); // the preferred degree of coherence
            
            // perform segmentation; produces an area tree
            AreaTree atree = vips.createAreaTree(page);
            
            // store the area tree in the repository (includes assigning a new IRI to the area tree)
            repository.addArtifact(atree);
            System.out.println("Area tree IRI: " + atree.getIri());
            
            // the SPARQL query - get all boxes, their font size and text
            String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                    + "PREFIX box: <http://fitlayout.github.io/ontology/render.owl#>\n"
                    + "\n"
                    + "SELECT ?box ?fsize ?text WHERE {  \n"
                    + "  ?box rdf:type box:Box . \n"
                    + "  ?box box:fontSize ?fsize .\n"
                    + "  ?box box:text ?text\n"
                    + "}";
            
            // execute the query and print the results using the RDF4J API
            // see https://rdf4j.org/documentation/programming/repository/#querying-a-repository
            try (RepositoryConnection conn = repository.getStorage().getConnection())
            {
                TupleQuery tupleQuery = conn.prepareTupleQuery(queryString);
                try (TupleQueryResult result = tupleQuery.evaluate())
                {
                    while (result.hasNext())
                    { // iterate over the result
                        BindingSet bindingSet = result.next();
                        IRI box = (IRI) bindingSet.getValue("box");
                        Literal fsize = (Literal) bindingSet.getValue("fsize");
                        Literal text = (Literal) bindingSet.getValue("text");
                        
                        // print a result line
                        System.out.println("<" + box.stringValue() + ">\t" 
                                + fsize.floatValue() + " px\t" 
                                + "'" + text.stringValue() + "'");
                    }
                }
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
