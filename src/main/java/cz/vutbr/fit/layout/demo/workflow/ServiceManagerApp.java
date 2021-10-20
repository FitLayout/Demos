/**
 * ServiceManagerApp.java
 *
 * Created on 20. 10. 2021, 21:00:35 by burgetr
 */
package cz.vutbr.fit.layout.demo.workflow;

import java.util.Map;

import cz.vutbr.fit.layout.api.ServiceManager;
import cz.vutbr.fit.layout.bcs.BCSProvider;
import cz.vutbr.fit.layout.cssbox.CSSBoxTreeProvider;
import cz.vutbr.fit.layout.provider.VisualBoxTreeProvider;
import cz.vutbr.fit.layout.puppeteer.PuppeteerTreeProvider;
import cz.vutbr.fit.layout.rdf.RDFArtifactRepository;
import cz.vutbr.fit.layout.segm.BasicSegmProvider;
import cz.vutbr.fit.layout.vips.VipsProvider;

/**
 * Demonstrates the application of the ServiceManager for creating multiple artifacts.
 *  
 * @author burgetr
 */
public class ServiceManagerApp
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        var task1 = Map.of("id", "FitLayout.CSSBox",
                           "params", Map.of("url", "http://cssbox.sf.net",
                                            "width", 1200,
                                            "height", 800));
        
        // setup an RDF repository
        // in-memory storage with no persistence
        var repository = RDFArtifactRepository.createMemory(null);

        // create and configure the service manager
        ServiceManager manager = createServiceManager(repository);
        
        // call several services to create artifacts
        

    }
    
    
    /**
     * Creates and configures a FitLayout ServiceManager instance that encapsulates several artifact
     * providers and a repository.
     * 
     * @param repo the artifact repository to be used by the service manager or {@code null} when
     * no repository should be configured.
     * @return the created ServiceManager instance
     */
    public static ServiceManager createServiceManager(RDFArtifactRepository repo)
    {
        //initialize the services
        ServiceManager sm = ServiceManager.create();
        
        //renderers
        sm.addArtifactService(new CSSBoxTreeProvider());
        sm.addArtifactService(new PuppeteerTreeProvider());
        
        //visual box tree construction
        sm.addArtifactService(new VisualBoxTreeProvider());
        
        //segmentation
        sm.addArtifactService(new BasicSegmProvider());
        sm.addArtifactService(new VipsProvider());
        sm.addArtifactService(new BCSProvider());
        
        //use RDF storage as the artifact repository
        if (repo != null)
            sm.setArtifactRepository(repo);
        return sm;
    }


}
