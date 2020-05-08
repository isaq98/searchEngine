import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONWriter {
	
	/**
	 * Writes several tab <code>\t</code> symbols using the provided
	 * {@link Writer}.
	 *
	 * @param times  the number of times to write the tab symbol
	 * @param writer the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 *
	 * @param element the element to quote
	 * @param writer  the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asArray(TreeSet<Integer> elements, Path path)
			throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers
	 * using the provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer,
			int level) throws IOException {

		//Handling condition for empty TreeSet
		if(elements.size() <= 0)
		{
			writer.write("[");
			writer.write(System.lineSeparator());
			indent(level, writer);
			writer.write("]");
			return;
		}
		
		//Completing method with code we were provided in class
		writer.write("[");
		writer.write(System.lineSeparator());
		
		for(Integer element : elements.headSet(elements.last()))
		{
			indent(level + 1, writer);
			writer.write(element.toString());
			writer.write(',');
			writer.write(System.lineSeparator());
		}
		
		indent(level + 1, writer);
		writer.write(elements.last().toString());
		writer.write(System.lineSeparator());
		
		indent(level, writer);
		writer.write("]");

	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path)
			throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the map of elements as a pretty JSON object using the provided
	 * {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer,
			int level) throws IOException {
		
		//Handling condition for empty TreeMap
		if(elements.size() <= 0)
		{
			writer.write("{");
			writer.write(System.lineSeparator());
			writer.write("}");
			return;
		}
		
		//Writing a brace to indicate an object rather than an array
		writer.write("{");
		writer.write(System.lineSeparator());
		
		//Iterate through the keys of the TreeMap
		for(Map.Entry<String, Integer> entry : elements.headMap(elements.lastKey()).entrySet())
		{
			//Format appropriately
			indent(level + 1, writer);
			quote(entry.getKey(), writer); 
			writer.write(": " + entry.getValue());
			writer.write(",");
			writer.write(System.lineSeparator());
		}
		
		//Conditions to handle writing the last element without a comma
		indent(level + 1, writer);
		quote(elements.lastKey(), writer);
		writer.write(": " + elements.get(elements.lastKey()));
		writer.write(System.lineSeparator());
		
		writer.write("}");

	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements,
			Path path) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements,
			Writer writer, int level) throws IOException {
		
		//Handling condition for empty TreeMap
		if(elements.size() <= 0)
		{
			writer.write("{");
			writer.write(System.lineSeparator());
			writer.write("}");
			return;
		}
		
		//Writing brace to indicate object rather than an array
		writer.write("{");
		writer.write(System.lineSeparator());
		
		//Iterate through the keys of the TreeMap
		for(Map.Entry<String, TreeSet<Integer>> entry : elements.headMap(elements.lastKey()).entrySet())
		{
			//Format appropriately
			indent(level + 1, writer);
			quote(entry.getKey().toString(), writer);
			writer.write(": ");
			asArray(entry.getValue(), writer, level + 1);
			writer.write(",");
			writer.write(System.lineSeparator());
		}
		
		//Conditions to handle writing the last element without a comma
		indent(level + 1, writer);
		quote(elements.lastKey().toString(), writer);
		writer.write(": ");
		asArray(elements.get(elements.lastKey()), writer, level + 1);
		writer.write(System.lineSeparator());
		
		indent(level, writer);
		writer.write("}");

	}
	
	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param fileInformation the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #fileInfoObject(TreeMap, Writer, int)
	 */
	public static String fileInfoObject(TreeMap<String, Integer> fileInformation)
	{
		try
		{
			StringWriter writer = new StringWriter();
			fileInfoObject(fileInformation, writer, 0);
			return writer.toString();
		}
		catch(IOException e)
		{
			return null;
		}
	}
	
	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param fileInformation the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #fileInfoObject(TreeMap, Writer, int)
	 */
	public static void fileInfoObject(TreeMap<String, Integer> fileInformation, Path path) throws IOException
	{
		try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
		{
			fileInfoObject(fileInformation, writer, 0);
		}
	}
	
	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param fileInformation the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 */
	public static void fileInfoObject(TreeMap<String, Integer> fileInformation, Writer writer, int level) throws IOException
	{
		if(fileInformation.size() <= 0)
		{
			writer.write("{");
			writer.write(System.lineSeparator());
			writer.write("}");
			return;
		}
		
		writer.write("{");
		writer.write(System.lineSeparator());
		
		int i = 1;
		//Iterate through the keys of the TreeMap
		for(String path : fileInformation.keySet())
		{
			if(i == fileInformation.size())
			{
				break;
			}
			
			indent(level + 1, writer);
			quote(path, writer);
			
			writer.write(": " + fileInformation.get(path) + ",");
			writer.write(System.lineSeparator());
			i++;
		}
		
		//Conditions to handle writing the last element without a comma
		indent(level + 1, writer);
		quote(fileInformation.lastKey(), writer);
		writer.write(": " + fileInformation.get(fileInformation.lastKey()));
		writer.write(System.lineSeparator());
		writer.write("}");
	}
	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param index the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #indexNestedObject(TreeMap, Writer, int)
	 */
	public static String indexNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index)
	{
		try
		{
			StringWriter writer = new StringWriter();
			indexNestedObject(index, writer, 0);
			return writer.toString();
		}
		catch(IOException e)
		{
			return null;
		}
	}
	
	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param index the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void indexNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, Path path)
			throws IOException
	{
		try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
		{
			indexNestedObject(index, writer, 0);
		}
	}
	
	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param index the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void indexNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> index, 
			Writer writer, int level) throws IOException
	{
		//If it's empty
		if(index.size() <= 0)
		{
			writer.write("{");
			writer.write(System.lineSeparator());
			writer.write("}");
			return;
		}
		
		writer.write("{");
		writer.write(System.lineSeparator());
		
		//Iterate through the keys of the TreeMap
		for(Map.Entry<String, TreeMap<String, TreeSet<Integer>>> entry : index.headMap(index.lastKey()).entrySet())
		{
			//Format appropriately
			indent(level + 1, writer);
			quote(entry.getKey(), writer);
			writer.write(": ");
			asNestedObject(entry.getValue(), writer, level + 1);
			writer.write(",");
			writer.write(System.lineSeparator());
		}
		
		//Handle the last case so that way it doesn't print extra punctuation
		indent(level + 1, writer);
		quote(index.lastKey(), writer);
		writer.write(": ");
		asNestedObject(index.get(index.lastKey()), writer, level + 1);
		writer.write(System.lineSeparator());
		writer.write("}");
		
	}
	
	/**
	 * helper method for JSON writer
	 * 
	 * @param writer - The writer to use
	 * @param elements - The data we're writing
	 * @throws IOException
	 */
	private static void asResultsArray(BufferedWriter writer, ArrayList<Result> elements) throws IOException
	{
		int num = 0;
		DecimalFormat FORMATTER = new DecimalFormat("0.000000");
		indent(2, writer);
		quote("results", writer);
		writer.write(": [\n");
		
		for (Result searchResult : elements)
		{
			indent(3, writer);
			writer.write("{\n");
			indent(4, writer);
			quote("where", writer);
			writer.write(": ");
			quote(searchResult.getPath(), writer);
			writer.write(",\n");
			indent(4, writer);
			quote("count", writer);
			writer.write(": " + searchResult.getOccurences() + ",\n");
			indent(4, writer);
			quote("score", writer);
			writer.write(": " + FORMATTER.format(searchResult.getScore()) + "\n");
			indent(3, writer);
			writer.write("}");
			
			int searchCount = elements.size();
			while (num < searchCount-1)
			{
				writer.write(",\n");
				num++;
				break;
			}
		}
		
		if (elements.size() == 0)
		{
			indent(2, writer);
			writer.write("]");
		}
		else
		{
			writer.write("\n");
			indent(2, writer);
			writer.write("]");
			writer.flush();
		}
	}
	
	/**
	 * Prints into JSON format given the raw data structure and path
	 * 
	 * @param elements - The data we're writing
	 * @param path - The path we're writing to
	 * 	
	 * @throws IOException
	 */
	public static void toSearchFormat(TreeMap<String, ArrayList<Result>> elements, Path path) throws IOException
	{
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8))
		{
			writer.write("[");
			
			int count = 0;
			int numOfQueries = elements.size();
			
			for (String q : elements.keySet())
			{	
				writer.write("\n");
				indent(1, writer);
				writer.write("{\n");
				indent(2, writer);
				quote("queries", writer);
				writer.write(": ");
				quote(q, writer);
				writer.write(",\n");
				JSONWriter.asResultsArray(writer, elements.get(q));
				
				writer.write("\n");
				indent(1, writer);
				writer.write("}");

				while (count < numOfQueries-1)
				{
					writer.write(",");
					count++;
					break;
				}
			}
			writer.write("\n]");
		}
	}
}