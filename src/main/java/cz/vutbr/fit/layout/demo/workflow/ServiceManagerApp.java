/**
 * ServiceManagerApp.java
 *
 * Created on 20. 10. 2021, 21:00:35 by burgetr
 */
package cz.vutbr.fit.layout.demo.workflow;

import java.util.Map;

import cz.vutbr.fit.layout.api.ArtifactService;
import cz.vutbr.fit.layout.api.ParametrizedOperation;
import cz.vutbr.fit.layout.api.ServiceManager;
import cz.vutbr.fit.layout.bcs.BCSProvider;
import cz.vutbr.fit.layout.cssbox.CSSBoxTreeProvider;
import cz.vutbr.fit.layout.model.Artifact;
import cz.vutbr.fit.layout.provider.VisualBoxTreeProvider;
import cz.vutbr.fit.layout.puppeteer.PuppeteerTreeProvider;
import cz.vutbr.fit.layout.rdf.RDFArtifactRepository;
import cz.vutbr.fit.layout.segm.BasicSegmProvider;
import cz.vutbr.fit.layout.vips.VipsProvider;

/**
 * Demonstrates the application of the ServiceManager for creating multiple artifacts.
 * This scenario is suitable when the page processing workflow should not be hardcoded
 * in Java code and it depends on some external configuration (service IDs,
 * input parametres, etc.) 
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
        // setup an RDF repository
        // in-memory storage with no persistence
        var repository = RDFArtifactRepository.createMemory(null);

        // create and configure the service manager
        ServiceManager manager = createServiceManager(repository);
        
        // call several services to create artifacts
        Artifact page = applyArtifactService(manager,
                "FitLayout.CSSBox", 
                Map.of("url", "http://cssbox.sf.net",
                        "width", 1200,
                        "height", 800),
                null);
        Artifact atree1 = applyArtifactService(manager,
                "FitLayout.BasicAreas", 
                Map.of("preserveAuxAreas", false),
                page);
        Artifact atree2 = applyArtifactService(manager,
                "FitLayout.VIPS", 
                Map.of("pDoC", 9),
                page);
        
        // store the artifacts
        manager.getArtifactRepository().addArtifact(page);
        manager.getArtifactRepository().addArtifact(atree1);
        manager.getArtifactRepository().addArtifact(atree2);
        
        // print the info about stored artifacts
        System.out.println("Stored artifacts:");
        for (Artifact a : manager.getArtifactRepository().getArtifactInfo())
        {
            System.out.println("IRI: " + a.getIri());
            System.out.println("  Parent IRI: " + a.getParentIri());
            System.out.println("  Type: " + a.getArtifactType());
            System.out.println("  Creator: " + a.getCreator() + " " + a.getCreatorParams());
            System.out.println();
        }

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
    
    /**
     * Configures and invokes a service for an input artifact.
     *  
     * @param manager the Service manager to be used
     * @param serviceId the ID of the service to be invoked
     * @param params A map of service input parametres (depending on the given service)
     * @param inputArtifact The input artifact to apply the service on (may be {@code null} for services that
     * do not use input artifacts, e.g. page rendering) 
     * @return The created output artifact.
     * @throws IllegalArgumentException when the service with the given ID is not available
     */
    public static Artifact applyArtifactService(ServiceManager manager, String serviceId, Map<String, Object> params, Artifact inputArtifact)
    {
        ParametrizedOperation op = manager.findParmetrizedService(serviceId);
        
        if (op == null)
            throw new IllegalArgumentException("No such service: " + serviceId);
        
        if (!(op instanceof ArtifactService))
            throw new IllegalArgumentException("Not an ArtifactService: " + serviceId);
        
        if (params != null)
            ServiceManager.setServiceParams(op, params);
        
        return ((ArtifactService) op).process(inputArtifact);
    }

}
