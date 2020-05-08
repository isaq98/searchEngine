import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Set;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collection;

public class InvertedIndex {
	
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final TreeMap<String, Integer> locations;

	/**
	 * Initializes the index as a TreeMap
	 */
	public InvertedIndex()
	{
		this.index = new TreeMap<>();
		this.locations = new TreeMap<>();
	}
	
	/**
	 * Method to add a given word, path, and location of occurrences into the inverted index
	 * 
	 * @param word - The word we're storing
	 * @param path - The file path we are iterating through
	 * @param occurences - The locations of where the words appear in the file
	 * @return - true if the index is changed as a result of a call to this function
	 */
	public boolean add(String word, String path, int occurrences)
	{		
		index.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		index.get(word).putIfAbsent(path,  new TreeSet<>());
		
		boolean success = index.get(word).get(path).add(occurrences);
		
		if(success)
		{
			locations.put(path, locations.getOrDefault(path, 0) + 1);
		}

		return success;
 	}
	
	/**
	 * Method to add all information in one given inverted index to another
	 * 
	 * @param other - The index we are taking information from
	 */
	public void addIndexes(InvertedIndex other)
	{
		for(String word : other.index.keySet())
		{
			if(!this.index.containsKey(word))
			{
				this.index.put(word, other.index.get(word));
			}
			else
			{
				for(String relativePath : other.index.get(word).keySet())
				{
					if(!this.index.get(word).containsKey(relativePath))
					{
						this.index.get(word).put(relativePath, other.index.get(word).get(relativePath));
					}
					else
					{
						this.index.get(word).get(relativePath).addAll(other.index.get(word).get(relativePath));
					}
				}
			}
		}
	
		for(String key : other.locations.keySet())
		{
			if(!this.locations.containsKey(key))
			{
				this.locations.put(key, other.locations.get(key));
			}
			else
			{
				this.locations.put(key, this.locations.get(key) + other.locations.get(key));
			}
		}
	}
	
	/**
	 * Method that searches for exact searches between query words and words within the inverted index
	 * 
	 * @param queryWords - The words from the query file we are looking for
	 * @return - An array list of type results
	 */
	public ArrayList<Result> exactSearch(Collection<String> queryWords)
	{
		HashMap<String, Result> resultsMap = new HashMap<>();
		ArrayList<Result> resultsList = new ArrayList<>();
		
		for(String words : queryWords)
		{
			if(index.containsKey(words))
			{
				searchHelper(words, resultsList, resultsMap);
			}
		}
		
		Collections.sort(resultsList);
		return resultsList;
	}
	
	/**
	 * Method that searches for partial searches between query words and words within the inverted index
	 * 
	 * @param queryWords - A line of words from the query file we are looking for
	 * @return - An array list of type results
	 * @throws IOException
	 */
	public ArrayList<Result> partialSearch(Collection<String> queryWords)
	{
		HashMap<String, Result> resultsMap = new HashMap<>();
		ArrayList<Result> resultsList = new ArrayList<>();
		
		for(String stem : queryWords)
		{
			for(String word : index.tailMap(stem).keySet())
			{
				if(word.startsWith(stem))
				{
					searchHelper(word, resultsList, resultsMap);
				}
				
				else
				{
					break;
				}
			}
		}
		
		Collections.sort(resultsList);
		return resultsList;
	}
	
	/**
	 * A helper method for searching. Validates the situation we are facing and stores the 
	 * information accordingly
	 * @param searchTerm - The query word we are looking for
	 * @param resultsList - The arraylist of type result
	 * @param resultsMap - A map which maps paths to their result
	 */
	private void searchHelper(String searchTerm, ArrayList<Result> resultsList, HashMap<String, Result> resultsMap)
	{
		for(String path : index.get(searchTerm).keySet())
		{
			TreeSet<Integer> positions = index.get(searchTerm).get(path);
			int occurences = positions.size();
			
			if(resultsMap.containsKey(path))
			{
				resultsMap.get(path).update(occurences);
			}
			
			else
			{
				resultsMap.put(path, new Result(path, occurences, locations.get(path)));
				resultsList.add(resultsMap.get(path));
			}
		}
	}
	
	/**
	 * Method to output an inverted index in JSON format
	 * 
	 * @param path - The file path we are writing from
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException {
		try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
		{
			JSONWriter.indexNestedObject(index, writer, 0);
		}
	}
	
	/**
	 * Method to output a -locations inverted index object in JSON format
	 * 
	 * @param path - The path we are writing from
	 * @throws IOException
	 */
	public void fileInfoToJSON(Path path) throws IOException
	{
		try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
		{
			JSONWriter.fileInfoObject(locations, writer, 0);
		}
	}
	
	
	/**
	 * Method to return an unmodifiable Set containing the 
	 * index keys
	 * 
	 * @return - the unmodifiable set
	 */
	public Set<String> getWords()
	{
		return Collections.unmodifiableSet(index.keySet());
	}
	
	/**
	 * Method to return an unmodifiable Map containing the file locations
	 * 
	 * @param word - the initial key we're using
	 * @return - the unmodifiable map
	 */
	public Set<String> getLocations(String word)
	{
		if(index.containsKey(word))
		{
			return Collections.unmodifiableSet(index.get(word).keySet());
		}
		else
		{
			return Collections.emptySet();
		}
	}
	
	public Set<Integer> getPositions(String word, String location)
	{
		if(index.containsKey(word) && index.get(word).containsKey(location))
		{
			return Collections.unmodifiableSet(index.get(word).get(location));
		}
		else
		{
			return Collections.emptySet();
		}
	}
	
	/**
	 * Boolean method to check if the inverted index contains a given word
	 * 
	 * @param word - the word we are searching for 
	 * @return true if it exists in the index
	 */
	public boolean containsWord(String word)
	{
		return index.containsKey(word);
	}
	
	/**
	 * Boolean method to check if the inverted index contains a given path
	 * @param word - the word we're looking for within the path
	 * @param path - the path itself
	 * @return true if it exists in the index
	 */
	public boolean containsPath(String word, String path)
	{
		if(containsWord(word))
		{
			return index.get(word).containsKey(path);
		}

		else
		{
			return false;
		}
			
	}
	
	/**
	 * Boolean method to check if the index contains the given key,
	 * path and position
	 * @param word - the word we are searching for
	 * @param path - the path itself
	 * @param position - its location within the file
	 * @return true if it exists in the index
	 */
	public boolean containsLocation(String word, String path, int position)
	{
		if(containsPath(word, path))
		{
			return index.get(word).get(path).contains(position);
		}
		
		else
		{
			return false;
		}
			
	}
	
	/**
	 * Method to return the size of the inverted index
	 * @return number of entries in the index
	 */
	public int words()
	{
		return index.size();
	}
	
	/**
	 * Method to return the total number of positions of the word
	 * within a file
	 * 
	 * @param word - the key we are using
	 * @param locations - the path we are searching
	 * @return - the total number of key appearances
	 */
	public int positions(String word, String locations)
	{
		if(containsPath(word, locations))
		{
			return index.get(word).get(locations).size();
		}
		
		else
		{
			return 0;
		}
	}
	
	/**
	 * Method to return the locations stored in the location data structure
	 * 
	 * @return - an unmodifiable set of all relative paths
	 */
	public Set<String> getLocationPaths()
	{
		return Collections.unmodifiableSet(locations.keySet());
	}
	
	/**
	 * Method to return the number of words in each path in the location data structure
	 * 
	 * @param key - the relative path we're accessing
	 * @return - the total number of words within it
	 */
	public int getLocationSize(String key)
	{
		return locations.get(key);
	}
	
	/**
	 * Overwriting default toString method
	 */
	public String toString()
	{
		return this.index.toString();
	}
	
}