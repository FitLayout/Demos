/**
 * BatchRender.java
 *
 * Created on 29. 1. 2022, 20:46:40 by burgetr
 */
package cz.vutbr.fit.layout.demo.batch;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.rdf4j.model.IRI;

import cz.vutbr.fit.layout.model.Page;
import cz.vutbr.fit.layout.puppeteer.PuppeteerTreeProvider;
import cz.vutbr.fit.layout.rdf.RDFArtifactRepository;

/**
 * 
 * @author burgetr
 */
public class BatchRender
{
    private static final int NUM_THREADS = 8;

    public void executeBatch(String listFile, String storagePath) throws IOException
    {
        var repo = RDFArtifactRepository.createNative(storagePath);
        var tasks = createTasks(listFile, repo);
        runTasks(tasks);
        repo.disconnect();
    }

    private List<RenderTask> createTasks(String listFile, RDFArtifactRepository repo)
            throws IOException
    {
        Path filePath = Path.of(listFile);
        List<String> urls = Files.readAllLines(filePath);
        List<RenderTask> tasks = new LinkedList<>();
        for (String urlString : urls)
            tasks.add(new RenderTask(this, repo, urlString));
        return tasks;
    }
    
    private void runTasks(List<RenderTask> tasks)
            throws IOException
    {
        ExecutorService exec = Executors.newFixedThreadPool(NUM_THREADS);
        try
        {
            List<Future<IRI>> results = exec.invokeAll(tasks);
            
            for (Future<IRI> ft : results)
            {
                ft.get();
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            exec.shutdown();
        }
    }
    
    public void taskFinished(RenderTask task)
    {
        System.out.println(task.getUrlString() + " --> " + task.getResultIri());
    }
    
    
    // ====================================================================
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.err.println("Usage: BatchRender <url_list_file> <storage_path>");
            System.err.println("The list file must contain a list of URLs to render; each URL on a separate line.");
            System.exit(1);
        }

        String listFile = args[0];
        String storagePath = args[1];
        
        try
        {
            BatchRender renderer = new BatchRender();
            renderer.executeBatch(listFile, storagePath);
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // ====================================================================
    
    static class RenderTask implements Callable<IRI>
    {
        private BatchRender parent;
        private RDFArtifactRepository repo;
        private String urlString;
        private IRI resultIri;
        
        public RenderTask(BatchRender parent, RDFArtifactRepository repo, String urlString)
        {
            this.parent = parent;
            this.repo = repo;
            this.urlString = urlString;
        }

        public String getUrlString()
        {
            return urlString;
        }

        public IRI getResultIri()
        {
            return resultIri;
        }

        @Override
        public IRI call() throws Exception
        {
            try {
                URL url = new URL(urlString);
                
                var renderer = new PuppeteerTreeProvider(url, 1200, 800);
                renderer.setIncludeScreenshot(true);
                renderer.setPersist(3);
                
                Page page = renderer.getPage();
                repo.addArtifact(page);
                resultIri = page.getIri();
                
                parent.taskFinished(this);
                return resultIri;
                
            } catch (MalformedURLException e) {
                System.err.println(e.getMessage());
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

    }
    
}
