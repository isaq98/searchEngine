import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Class to multithread building an inverted index
 * @author sameerisaq
 *
 */
public class ThreadSafeIndexBuilder
{
	/**
	 * Initializes workqueue and calls readPathHelper to build index
	 * 
	 * @param path - the path we are reading from
	 * @param index - the index we are building
	 * @param threads - the number of threads to initialize the workqueue with
	 * @throws IOException
	 */
	public static void buildHelper(Path path, ThreadSafeInvertedIndex index, int threads) throws IOException
	{
		WorkQueue queue = new WorkQueue(threads);
		try
		{
			readPathHelper(path, index, queue);
		}
		finally
		{
			queue.finish();
			queue.shutdown();
		}
	}

	/**
	 * Recursively traverses through the given path and creates workers based on 
	 * whether or not we find a new file
	 * 
	 * @param path - the path we are reading from
	 * @param index - the index we are building
	 * @param queue - the workqueue
	 * @throws IOException
	 */
	private static void readPathHelper(Path path, ThreadSafeInvertedIndex index, WorkQueue queue) throws IOException
	{	
		if(Files.isDirectory(path))
		{
			try(DirectoryStream<Path> listing = Files.newDirectoryStream(path))
			{
				for(Path paths : listing)
				{
					readPathHelper(paths, index, queue);
				}
			}
		}
		
		else if(path.toString().toLowerCase().endsWith(".txt") || path.toString().toLowerCase().endsWith(".text"))
		{
			queue.execute(new IndexTask(path, index));
		}
	}

	/**
	 * 
	 * Nested worker class that builds the index
	 *
	 */
	private static class IndexTask implements Runnable
	{
		private final Path path;
		private final ThreadSafeInvertedIndex index;

		public IndexTask(Path path, ThreadSafeInvertedIndex index)
		{
			this.path = path;
			this.index = index;
		}
		
		@Override
		public void run()
		{
			try
			{
				InvertedIndex local = new InvertedIndex();
				IndexBuilder.readFile(path, local);
				index.addIndexes(local);
			}
			catch(IOException e)
			{
				System.err.println("There was an error when building the multithreading index: " + e);
			}
		}
	}
}