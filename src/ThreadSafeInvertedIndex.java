import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


public class ThreadSafeInvertedIndex extends InvertedIndex {
	
	private final ReadWriteLock lock;
	
	/**
	 * Creating a thread safe inverted index & initializing our ReadWriteLock
	 */
	public ThreadSafeInvertedIndex()
	{
		super();
		lock = new ReadWriteLock();
	}
	
	/**
	 * Thread safe method to add a given word, path, and location of occurrences into the inverted index
	 * 
	 * @param word - The word we're storing
	 * @param path - The file path we are iterating through
	 * @param occurences - The locations of where the words appear in the file
	 * @return - true if the index is changed as a result of a call to this function
	 */
	@Override
	public boolean add(String word, String path, int occurrences)
	{
		lock.lockReadWrite();
		try
		{
			return super.add(word, path, occurrences);
		}
		finally 
		{
			lock.unlockReadWrite();
		}
	}
	
	/**
	 * Method to add all information in one given inverted index to another
	 * 
	 * @param other - The index we are taking information from
	 */
	@Override
	public void addIndexes(InvertedIndex other)
	{
		lock.lockReadWrite();
		try
		{
			super.addIndexes(other);
		}
		finally
		{
			lock.unlockReadWrite();
		}
	}
	
	/**
	 * Thread safe method that searches for exact searches between query words and words within the inverted index
	 * 
	 * @param queryWords - The words from the query file we are looking for
	 * @return - An array list of type result
	 */
	@Override
	public ArrayList<Result> exactSearch(Collection<String> queryWords)
	{
		lock.lockReadOnly();
		try
		{
			return super.exactSearch(queryWords);
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe method that searches for partial searches between query words and words within the inverted index
	 * 
	 * @param queryWords - A line of words from the query file we are looking for
	 * @return - An array list of type result
	 * @throws IOException
	 */
	@Override
	public ArrayList<Result> partialSearch(Collection<String> queryWords)
	{
		lock.lockReadOnly();
		try
		{
			return super.partialSearch(queryWords);
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe method to output an inverted index in JSON format
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
			super.toJSON(path);
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe method to output a -locations inverted index object in JSON format
	 * 
	 * @param path - The path we are writing from
	 * @throws IOException
	 */
	@Override
	public void fileInfoToJSON(Path path) throws IOException
	{
		lock.lockReadOnly();
		try
		{
			super.fileInfoToJSON(path);
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe method to return an unmodifiable Set containing the 
	 * index keys
	 * 
	 * @return - the unmodifiable set
	 */
	@Override
	public Set<String> getWords()
	{
		lock.lockReadOnly();
		try
		{
			return super.getWords();
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe method to return an unmodifiable Map containing the file locations
	 * 
	 * @param word - the initial key we're using
	 * @return - the unmodifiable map
	 */
	@Override
	public Set<String> getLocations(String word)
	{
		lock.lockReadOnly();
		try
		{
			return super.getLocations(word);
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe boolean method to check if the inverted index contains a given word
	 * 
	 * @param word - the word we are searching for 
	 * @return true if it exists in the index
	 */
	@Override
	public boolean containsWord(String word)
	{
		lock.lockReadOnly();
		try
		{
			return super.containsWord(word);
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe boolean method to check if the inverted index contains a given path
	 * @param word - the word we're looking for within the path
	 * @param path - the path itself
	 * @return true if it exists in the index
	 */
	@Override
	public boolean containsPath(String word, String path)
	{
		lock.lockReadOnly();
		try
		{
			return super.containsPath(word, path);
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe boolean method to check if the index contains the given key,
	 * path and position
	 * @param word - the word we are searching for
	 * @param path - the path itself
	 * @param position - its location within the file
	 * @return true if it exists in the index
	 */
	@Override
	public boolean containsLocation(String word, String path, int position)
	{
		lock.lockReadOnly();
		try
		{
			return super.containsLocation(word, path, position);
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe method to return the size of the inverted index
	 * @return number of entries in the index
	 */
	@Override
	public int words()
	{
		lock.lockReadOnly();
		try
		{
			return super.words();
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe method to return the total number of positions of the word
	 * within a file
	 * 
	 * @param word - the key we are using
	 * @param locations - the path we are searching
	 * @return - the total number of key appearances
	 */
	@Override
	public int positions(String word, String locations)
	{
		lock.lockReadOnly();
		try
		{
			return super.positions(word, locations);
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe method to return the number of words in each path in the location data structure
	 * 
	 * @param key - the relative path we're accessing
	 * @return - the total number of words within it
	 */
	@Override
	public int getLocationSize(String key)
	{
		lock.lockReadOnly();
		try
		{
			return super.getLocationSize(key);
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	/**
	 * Thread safe method to return the locations stored in the location data structure
	 * 
	 * @return - an unmodifiable set of all relative paths
	 */
	@Override
	public Set<String> getLocationPaths()
	{
		lock.lockReadOnly();
		try
		{
			return super.getLocationPaths();
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public String toString()
	{
		lock.lockReadOnly();
		try
		{
			return super.toString();
		}
		finally
		{
			lock.unlockReadOnly();
		}
	}
}