import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;


public class ThreadedQueryFileParser implements QueryParserHelper
{
	private final ThreadSafeInvertedIndex index;
	private final TreeMap<String, ArrayList<Result>> map;
	private final int threads;
	private final ReadWriteLock lock;

	/**
	 * Constructor for ThreadedQueryFileParser
	 * 
	 * @param index - the thread safe index we are adding information to
	 * @param threads - the number of threads we are prompting our workqueue with
	 */
	public ThreadedQueryFileParser(ThreadSafeInvertedIndex index, int threads)
	{
		this.index = index;
		this.threads = threads;
		map = new TreeMap<>();
		lock = new ReadWriteLock();
	}

	/**
	 * Method that parses queries by reading the file line by line and calling the appropriate exact/partial search method
	 * 
	 * @param path - The input path
	 * @param exact - Boolean value determining whether we are using partial or exact search
	 * 		
	 * @throws IOException
	 */
	@Override
	public void parseQuery(Path path, boolean exactSearch) throws IOException
	{
		WorkQueue queue = new WorkQueue(threads);
		
		try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);)
		{
			String line;
			
			while((line = reader.readLine()) != null)
			{
				queue.execute(new QueryTask(line, exactSearch));
			}
		}
		finally 
		{
			queue.finish();
			queue.shutdown();
		}
	}

	/**
	 * Method to output search results in JSON format
	 * 
	 * @param path - The file path we are writing from
	 * @throws IOException
	 */
	@Override
	public void toJSON(Path path) throws IOException 
	{
		lock.lockReadOnly();
		try
		{
			JSONWriter.toSearchFormat(map, path);
		}
		finally
		{
			lock.unlockReadOnly();
		}
		
	}

	/**
	 * A private class that creates a task for reading through files and calling the search methods
	 * accordingly
	 */
	private class QueryTask implements Runnable
	{
		private String line;
		private boolean exactSearch;
		
		public QueryTask(String line, boolean exactSearch) 
		{
			this.line = line;
			this.exactSearch = exactSearch;
		}
		
		@Override
		public void run()
		{
			SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
			TreeSet<String> queryWords = new TreeSet<>();
					
			for(String word : TextParser.parse(line))
			{
				queryWords.add(stemmer.stem(word).toString());
			}
					
			if(queryWords.isEmpty())
			{
				return;
			}
					
			line = String.join(" ", queryWords);
			
			lock.lockReadOnly();
			
			try
			{
				if(map.containsKey(line))
				{
					return;
				}
			}
			finally
			{
				lock.unlockReadOnly();
			}
			
			ArrayList<Result> resultMember = exactSearch ? index.exactSearch(queryWords) : index.partialSearch(queryWords);

			lock.lockReadWrite();
			try
			{
				map.put(line, resultMember);
			}
			finally
			{
				lock.unlockReadWrite();
			}	
		}
	}
}