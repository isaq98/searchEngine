import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface class for ThreadedQueryParser
 * @author sameerisaq
 *
 */
public interface QueryParserHelper 
{
	
	/**
	 * Method that parses queries by reading the file line by line and calling the appropriate exact/partial search method
	 * 
	 * @param path - The input path
	 * @param exact - Boolean value determining whether we are using partial or exact search
	 * 		
	 * @throws IOException
	 */
	public void parseQuery(Path path, boolean exactSearch) throws IOException;
	
	/**
	 * Method to output search results in JSON format
	 * 
	 * @param path - The file path we are writing from
	 * @throws IOException
	 */
	public void toJSON(Path path) throws IOException;
}
