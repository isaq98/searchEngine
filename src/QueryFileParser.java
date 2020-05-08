import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * Class that parses query files and calls
 * the corresponding search method. Also overrides interface class.
 */
public class QueryFileParser implements QueryParserHelper
{
	
	private final TreeMap<String, ArrayList<Result>> results;
	private final InvertedIndex index;
	
	/**
	 * Constructor, handles the queryfile that is passed into the program
	 * to prepare it so we can populate the inverted index
	 * 
	 * @param index - the inverted index that we are building
	 */
	public QueryFileParser(InvertedIndex index)
	{
		this.results = new TreeMap<>();
		this.index = index;
	}
	
	/**
	 * Method that parses queries by reading the file line by line and calling the appropriate exact/partial search method
	 * 
	 * @param path - The input path
	 * @param exact - Boolean value determining whether we are using partial or exact search
	 * 		
	 * @throws IOException
	 */
	public void parseQuery(Path path, boolean exactSearch) throws IOException
	{
		try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);)
		{
	
			String line;
			SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
			
			while((line = reader.readLine()) != null)
			{	
				TreeSet<String> queryWords = new TreeSet<>();
				
				for(String word : TextParser.parse(line))
				{
					queryWords.add(stemmer.stem(word).toString());
				}
				
				if(queryWords.isEmpty())
				{
					continue;
				}
				
				line = String.join(" ", queryWords);
				
				if(results.containsKey(line))
				{
					continue;
				}
				
				ArrayList<Result> resultMember = exactSearch ? index.exactSearch(queryWords) : index.partialSearch(queryWords);
				results.put(line, resultMember);
			}
		}
	}
	
	/**
	 * Method to output search results in JSON format
	 * 
	 * @param path - The file path we are writing from
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException
	{
		try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
		{
			JSONWriter.toSearchFormat(results, path);
		}
	}
}