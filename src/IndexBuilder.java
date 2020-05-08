import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.List;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

/**
 * This class contains various methods for manipulating text.
 */
public class IndexBuilder {
	
	/**
	 * Reads the given path, cleans, parses, and stems each line of the file.
	 * After completing all the previous tasks, it adds the information into the inverted index.
	 * 
	 * @param path - the path to traverse
	 * @param index - the inverted index that we are building
	 */
	public static void readFile(Path path, InvertedIndex index) throws IOException
	{
		try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8))
		{
			String line;
			int i = 1;

			SnowballStemmer stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);
			
			while((line = reader.readLine()) != null)
			{
				
				String[] clean = textCleaner(line);
				
				for(String s : clean)
				{
					String trim = stemmer.stem(s.toLowerCase().trim()).toString();
					
					if(!trim.isEmpty())
					{
						index.add(trim, path.toString(), i);
						i++;
					}
				}
			}
		}
	}
	
	
	/**
	 * Accepts the paths stored in the FileTraversal class in order to read it
	 * into the inverted index through calling the readFile method.
	 * 
	 * @param path - the path to traverse
	 * @param traverser - FileTraversal object in order to retrieve the stored paths
	 * @param index - the inverted index we are building
	 * @throws IOException 
	 */
	public static void readPaths(Path path, InvertedIndex index) throws IOException
	{	
		List<Path> paths = TextFileTraverser.traverse(path);
		
		for(Path p : paths)
		{
			readFile(p, index);	
		}
	}
	
	/**
	 * Helper method to do the cleaning and normalizing of text
	 * @param line - the line of the file we are on that we want to clean/parse
	 * @return - an array containing the cleaned strings
	 */
	public static String[] textCleaner(String line)
	{	
		String cleaned = Normalizer.normalize(line, Normalizer.Form.NFD).replaceAll("(?U)[^\\p{Alpha}\\p{Space}]+", "");
		
		String[] clean = cleaned.split("(?U)\\p{Space}+");
		
		return clean;
	}
}