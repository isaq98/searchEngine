import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Class to build an inverted index from URLs rather than text files
 * @author sameerisaq
 *
 */
public class WebCrawler {
	
	private WorkQueue queue;
	private ThreadSafeInvertedIndex index;
	private final HashSet<String> links;
	private final ReadWriteLock lock;
	private int LIMIT;
	
	/**
	 * Constructor for the webcrawler
	 * @param index - the index we are building
	 * @param threads - the number of threads we are initializing the work queue with
	 */
	public WebCrawler(ThreadSafeInvertedIndex index, int threads)
	{
		this.index = index;
		this.LIMIT = 0;
		links = new HashSet<String>();
		lock = new ReadWriteLock();
		queue = new WorkQueue(threads);
	}
	
	/**
	 * Method that actually crawls the web for information
	 * @param url - the root url
	 * @param limit - the max number of urls we add to the index
	 */
	public void crawl(URL url, int limit)
	{
		LIMIT = limit;
	
		try
		{
			if(!links.contains(url.toString()) && !url.toString().contains("\0"))
			{
				links.add(url.toString());
				queue.execute(new CrawlWorker(url));
			}
		}
		finally
		{
			queue.finish();
			queue.shutdown();
		}
	}
	
	/**
	 * Private class that does the work of searching through URLs,
	 * fetching html, and building the index
	 *
	 */
	private class CrawlWorker implements Runnable
	{
		private final URL url;
		
		public CrawlWorker(URL url)
		{
			this.url = url;
		}
		
		@Override
		public void run()
		{
			String html = null;
			try 
			{
				html = HTMLFetcher.fetchHTML(url, 3);
			} 
			catch (IOException e) 
			{	
				System.err.println("There was an issue with ");
			}
			
			if(html == null)
			{
				return;
			}
			
			lock.lockReadWrite();
			try
			{
				ArrayList<URL> linksList = LinkParser.listLinks(url, html);
				
				for(URL link : linksList)
				{
					if(!links.contains(link.toString()))
					{
						if(links.size() >= LIMIT)
						{
							break;
						}
						
						links.add(link.toString());
						queue.execute(new CrawlWorker(link));
					}
				}
			}
			
			finally
			{
				lock.unlockReadWrite();
			}
			
			SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
			String cleaned = HTMLCleaner.stripHTML(html);
			String[] words = IndexBuilder.textCleaner(cleaned);
			
			lock.lockReadWrite();
			InvertedIndex local = new InvertedIndex();
			int occurence = 1;
			
			try
			{
				for(String word : words)
				{
					String trim = stemmer.stem(word.toLowerCase().trim()).toString();
						
					if(!trim.isEmpty())
					{
						local.add(trim, url.toString(), occurence);
						occurence++;
					}
				}
			}
			finally
			{
				lock.unlockReadWrite();
			}
			
			index.addIndexes(local);
		}
	}
}
