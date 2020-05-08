import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class Driver {

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		//Initialize all needed variables
		ArgumentMap map = new ArgumentMap();
		InvertedIndex invertedIndex = null;
		QueryParserHelper queryParserHelper = null;
		ThreadSafeInvertedIndex threadSafeInvertedIndex = null;
		int threads, limit, port;
		
		map.parse(args);
		
		try
		{
			port = Integer.parseInt(map.getString("-port"));
			if(port <= 0)
			{
				port = 8080;
			}
		}
		catch(NumberFormatException e)
		{
			port = 8080;
		}
		
		try
		{
			threads = Integer.parseInt(map.getString("-threads"));
			if(threads <= 0)
			{
				threads = 5;
			}
		}
		catch(NumberFormatException e)
		{
			threads = 5;
		}
		
		try
		{
			limit = Integer.parseInt(map.getString("-limit"));
			if(limit <= 0)
			{
				limit = 50;
			}
		}
		catch(NumberFormatException e)
		{
			limit = 50;
		}
		
		if(map.hasFlag("-threads"))
		{
			
			threadSafeInvertedIndex = new ThreadSafeInvertedIndex();
			invertedIndex = threadSafeInvertedIndex;
			queryParserHelper = new ThreadedQueryFileParser(threadSafeInvertedIndex, threads);
			
			if(map.hasFlag("-path") && map.getPath("-path") != null)
			{
				try
				{
					ThreadSafeIndexBuilder.buildHelper(map.getPath("-path"), threadSafeInvertedIndex, threads);
				}
				catch(IOException e)
				{
					System.err.println("Unable to build from path: " + map.getPath("-path").toString());
				}
			}
		}
		
		else
		{
			invertedIndex = new InvertedIndex();
			queryParserHelper = new QueryFileParser(invertedIndex);
			
			if(map.hasFlag("-path"))
			{
				Path path = map.getPath("-path");
				
				if(path != null)
				{
					try
					{
						IndexBuilder.readPaths(path, invertedIndex);
					}
					catch(IOException e)
					{
						System.err.println("Unable to build the index from path: " + path.toString());
					}
				}
				else
				{
					System.out.println("The path you are trying to traverse is null");
				}
			}
		}
		
		if(map.hasFlag("-url") && map.getString("-url") != null && !map.getString("-url").contains("\0"))
		{
			threadSafeInvertedIndex = new ThreadSafeInvertedIndex();
			invertedIndex = threadSafeInvertedIndex;
			WebCrawler crawler = new WebCrawler(threadSafeInvertedIndex, threads);
			queryParserHelper = new QueryFileParser(invertedIndex);
			
			try
			{
				URL urlFlag = new URL(map.getString("-url"));
				crawler.crawl(urlFlag, limit);
			}
			catch(MalformedURLException e)
			{
				System.out.println("unable to read URL");
			}
			
		}
		
		if(map.hasFlag("-port"))
		{
			Server server = new Server(port);
			ServletHandler handler = new ServletHandler();
			SearchServlet servlet = new SearchServlet(threadSafeInvertedIndex);
			SearchServlet.HistoryServlet historyServlet = servlet.new HistoryServlet(threadSafeInvertedIndex);
			
			handler.addServletWithMapping(new ServletHolder(servlet), "");
			handler.addServletWithMapping(CookieIndexServlet.class, "/");
			handler.addServletWithMapping(CookieConfigServlet.class, "/config");
			handler.addServletWithMapping(new ServletHolder(new DisplayServlet(threadSafeInvertedIndex)), "/display");
			handler.addServletWithMapping(new ServletHolder(new LocationServlet(threadSafeInvertedIndex)), "/locations");
			handler.addServletWithMapping(new ServletHolder(new CrawlServlet(threadSafeInvertedIndex, limit, threads)), "/crawl");
			handler.addServletWithMapping(new ServletHolder(new ExactServlet(threadSafeInvertedIndex)), "/exact");
			handler.addServletWithMapping(new ServletHolder(historyServlet), "/history");
			server.setHandler(handler);
			server.start();
			server.join();
		}
		
		if(map.hasFlag("-index"))
		{
			Path defaultPath = map.getPath("-index", Paths.get("index.json"));
					
			try
			{
				invertedIndex.toJSON(defaultPath);
			}
			catch(IOException e1)
			{
				System.err.println("Unable to write the index to path: " + defaultPath.toString());
			}
		}
		
		if(map.hasFlag("-locations"))
		{
			try
			{
				invertedIndex.fileInfoToJSON(map.getPath("-locations"));
			}
			catch(IOException e2)
			{
				System.err.println("Unable to write to path: " + map.getPath("-path").toString());
			}
		}
		
		if(map.hasFlag("-search"))
		{
			Path queryFile = map.getPath("-search");
			
			try
			{
				queryParserHelper.parseQuery(queryFile, map.hasFlag("-exact"));
			}
			catch(IOException e3)
			{
				System.err.println("Unable to read from path: " + queryFile.toString());
			}
		}	
		
		if(map.hasFlag("-results"))
		{
			Path defaultPath = map.getPath("-results", Paths.get("results.json"));
			
			try
			{
				queryParserHelper.toJSON(defaultPath);
			}
			catch(IOException e5)
			{
				System.err.println("Unable to write to path: " + defaultPath.toString());
			}
		}
	}
}