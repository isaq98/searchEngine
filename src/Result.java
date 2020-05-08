
/**
 * A class that stores a single search result and implements the
 * Comparable interface to sort results
 *
 */
public class Result implements Comparable<Result> {
	
	private final String path;
	private int occurences;
	private final int totalWords;
	private double score;
	
	/**
	 * Constructor, creates a Result object to sort information within the index
	 * @param path - the relative path to the query word
	 * @param occurences - the number of times the query word appears within a file
	 * @param totalWords - the total word count of a given file
	 */
	public Result(String path, int occurences, int totalWords)
	{
		this.path = path;
		this.occurences = occurences;
		this.totalWords = totalWords;
		this.score = (double) this.occurences / (double) this.totalWords;
	}
	
	/**
	 * Getter method for the number of times a query word appears
	 * @return - its total number of appearances
	 */
	public int getOccurences()
	{
		return this.occurences;
	}
	
	/**
	 * Getter method for the score
	 * @return - the score corresponding to the search term
	 */
	public double getScore()
	{
		return this.score;
	}
	
	/**
	 * Getter method for the path
	 * @return - the path corresponding to the search
	 */
	public String getPath()
	{
		return this.path;
	}
	
	/**
	 * Simple method to update the word count and score of a given query
	 */
	public void update(int count)
	{
		this.occurences += count;
		this.score = (double) this.occurences / (double) this.totalWords;
	}
	
	/**
	 * Overriding compareTo method
	 */
	public int compareTo(Result other)
	{
		int returnVal = Double.compare(other.score, this.score);
		
		if(returnVal == 0)
		{
			returnVal = Integer.compare(other.occurences, this.occurences);
			if(returnVal == 0)
			{
				returnVal = this.path.compareTo(other.path);
			}
		}
		return returnVal;
	}	

}