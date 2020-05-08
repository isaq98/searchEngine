import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * This class recursively lists files and adds the discovered paths to a list
 */

public class TextFileTraverser {
	
	
	/**
	 * Outputs the name of the file or subdirectory, with proper indentation to
	 * help indicate the hierarchy. If a subdirectory is encountered, will
	 * recursively list all the files in that subdirectory.
	 *
	 * The recursive version of this method is private. Users of this class will
	 * have to use the public version (see below).
	 *
	 * 
	 * @param path   to retrieve the listing, assumes a directory and not a file
	 *               is passed
	 * @throws IOException
	 */
	public static List<Path> traverse(Path path) throws IOException 
	{
		List<Path> paths = new ArrayList<>();
		traverse(path, paths);
		return paths;
	}
	
	/**
	 * Private version of the method above. Accepts a path and
	 * a list to which the method adds to recursively in order to build
	 * the index.
	 * 
	 * @param path  to retrieve the listing, assumes a directory and not a file
	 * @param paths the list that we are adding everything to
	 */
	private static void traverse(Path path, List<Path> paths) throws IOException
	{
		String name = path.getFileName().toString().toLowerCase();
		
		if (Files.isDirectory(path)) 
		{
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) 
			{
				for (Path file : listing) 
				{
					traverse(file, paths);
				}
			} 
		}
		
		else if(name.endsWith(".txt") || name.endsWith(".text"))
		{
			paths.add(path);
		}
	}
}
