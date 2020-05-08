import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;


@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {
	private static final String TITLE = "Search";
	private final ThreadSafeInvertedIndex index;
	private static ConcurrentLinkedQueue<String> history;
	
	/**
	 * Constructor for the servlet
	 * 
	 * @param index - a thread safe inverted index
	 */
	public SearchServlet(ThreadSafeInvertedIndex index) 
	{
		super();
		this.index = index; 
		history = new ConcurrentLinkedQueue<>();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		printForm(request, response);
		
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		printResultForm(request, response, index);

		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Method that returns long time
	 * 
	 * @return - A string of the date and time
	 */
	public static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
	
	/**
	 * Method that prints the search page of the servlet
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		PrintWriter out = response.getWriter();
		CookieIndexServlet obj = new CookieIndexServlet();
		
		obj.printPrompt(request, response);

		out.printf("			</div>%n");
		out.printf("%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Search</h2>%n");
		out.printf("%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("				<div class=\"field\">%n");
		out.printf("					<label class=\"label\">Query</label>%n");
		out.printf("					<div class=\"control has-icons-left\">%n");
		out.printf("						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Enter your search here.\">%n", "query");
		out.printf("						<span class=\"icon is-small is-left\">%n");
		out.printf("							<i class=\"fas fa-user\"></i>%n");
		out.printf("						</span>%n");
		out.printf("					</div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-search\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("						Search for Partial Matches!%n");
		out.printf("					</button>%n");
		out.printf("			  </div>%n");
		out.printf("			</form>%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	<footer class=\"footer\">%n");
		out.printf("	  <div class=\"content has-text-centered\">%n");
		out.printf("	    <p>%n");
		out.printf("	      This request was handled by thread %s on %s.%n", Thread.currentThread().getName(), getDate());
		out.printf("	    </p>%n");
		out.printf("	  </div>%n");
		out.printf("	</footer>%n");
		out.printf("</body>%n");
		out.printf("</html>%n");
	}
	
	/**
	 * Method that prints the result page of the search
	 * 
	 * @param request
	 * @param response
	 * @param index - the index we are printing
	 * @throws IOException
	 */
	private static void printResultForm(HttpServletRequest request, HttpServletResponse response, ThreadSafeInvertedIndex index) throws IOException
	{
		
		PrintWriter out = response.getWriter();
		
		TreeSet<String> searchWords = new TreeSet<>();
		String search = request.getParameter("query");
		search = search == null ? "" : search;
		search = StringEscapeUtils.escapeHtml4(search);
		
		out.printf("<!DOCTYPE html>%n");
		out.printf("<html>%n");
		out.printf("<head>%n");
		out.printf("	<meta charset=\"utf-8\">%n");
		out.printf("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
		out.printf("	<title>%s</title>%n", TITLE);
		out.printf("	<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.2/css/bulma.min.css\">%n");
		out.printf("	<script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>%n");
		out.printf("</head>%n");
		out.printf("%n");
		out.printf("<body>%n");
		out.printf("	<section class=\"hero is-primary is-bold\">%n");
		out.printf("	  <div class=\"hero-body\">%n");
		out.printf("	    <div class=\"container\">%n");
		out.printf("	      <h1 class=\"title\">%n");
		out.printf("	        Sedentary Search%n");
		out.printf("	      </h1>%n");
		out.printf("	      <h2 class=\"subtitle\">%n");
		out.printf("					<i class=\"fas fa-search\"></i>%n");
		out.printf("					&nbsp;Finds what you're looking for so quickly, you'd think your mother were a thread.%n");
		out.printf("	      </h2>%n");
		out.printf("	    </div>%n");
		out.printf("	  </div>%n");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Results</h2>%n");
		
		
		String formatted = String.format("%s<br><font size=\"-2\">[  at %s ]</font>", search,
				getDate());
		
		if(request.getIntHeader("DNT") != 1)
		{
			history.add(formatted);
		}
		
		if(history.size() > 4)
		{
			history.poll();
		}
		
		for(var var : TextParser.parse(search))
		{
			searchWords.add(var);
		}
		
		final long startTime = System.currentTimeMillis();
		ArrayList<Result> resultList = index.partialSearch(searchWords);

			
		if(resultList.isEmpty())
		{
			out.printf("<p>No results found</p>");
		}
		
		int counter = 1;
		for(var var : resultList)
		{
			out.printf("<p>%d.) ", counter, "</p>");
			out.printf("<a href=%s> %s", var.getPath(), var.getPath(), "</a>");
			out.printf("</a>");
			out.printf(" Score: [%f], Total Matches: [%d]%n", var.getScore(), var.getOccurences());
			counter++;
		}
		
		final long endTime = System.currentTimeMillis();
		final long total = endTime - startTime;
		
		out.printf("<h2>Total number of results: %d found in %ds</h2>%n", resultList.size(), total);
		out.printf("</div>%n");
		out.printf("</section>%n");
		
		promptQuery(request, response);
		
		out.printf("	<footer class=\"footer\">%n");
		out.printf("	  <div class=\"content has-text-centered\">%n");
		out.printf("	    <p>%n");
		out.printf("	      This request was handled by thread %s on %s.%n", Thread.currentThread().getName(), getDate());
		out.printf("	    </p>%n");
		out.printf("	  </div>%n");
		out.printf("	</footer>%n");
		out.printf("</body>%n");
		out.printf("</html>%n");
	}
	
	/**
	 * Method that prints a textbox for the user to enter their queries
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void promptQuery(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		
		out.printf("	<section class=\"section\">%n");
		out.printf("		<div class=\"container\">%n");
		out.printf("			<h2 class=\"title\">Search</h2>%n");
		out.printf("%n");
		out.printf("			<form method=\"%s\" action=\"%s\">%n", "POST", request.getServletPath());
		out.printf("				<div class=\"field\">%n");
		out.printf("					<label class=\"label\">Query</label>%n");
		out.printf("					<div class=\"control has-icons-left\">%n");
		out.printf("						<input class=\"input\" type=\"text\" name=\"%s\" placeholder=\"Enter your search here.\">%n", "query");
		out.printf("						<span class=\"icon is-small is-left\">%n");
		out.printf("							<i class=\"fas fa-user\"></i>%n");
		out.printf("						</span>%n");
		out.printf("					</div>%n");
		out.printf("				</div>%n");
		out.printf("%n");
		out.printf("				<div class=\"control\">%n");
		out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");
		out.printf("						<i class=\"fas fa-comment\"></i>%n");
		out.printf("						&nbsp;%n");
		out.printf("						Search!%n");
		out.printf("					</button>%n");
		out.printf("			  </div>%n");
		out.printf("			</form>%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("%n");
	}
	
	/**
	 * Class that displays search history
	 * @author sameerisaq
	 *
	 */
	public class HistoryServlet extends SearchServlet
	{
		
		/**
		 * Constructor for the servlet
		 * @param index - a thread safe inverted index
		 */
		public HistoryServlet(ThreadSafeInvertedIndex index) 
		{
			super(index);
		}

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
		{
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			printHistoryForm(request, response);
			response.setStatus(HttpServletResponse.SC_OK);
		}
		
		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
			
			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);
			history.clear();
			response.setStatus(HttpServletResponse.SC_OK);
		}
		
		/**
		 * Method that properly formats printing the history of the user
		 * 
		 * @param request
		 * @param response
		 * @throws IOException
		 */
		private void printHistoryForm(HttpServletRequest request, HttpServletResponse response) throws IOException
		{
			PrintWriter out = response.getWriter();
			
			out.printf("<!DOCTYPE html>%n");
			out.printf("<html>%n");
			out.printf("<head>%n");
			out.printf("	<meta charset=\"utf-8\">%n");
			out.printf("	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
			out.printf("	<title>%s</title>%n", TITLE);
			out.printf("	<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.2/css/bulma.min.css\">%n");
			out.printf("	<script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>%n");
			out.printf("</head>%n");
			out.printf("%n");
			out.printf("<body>%n");
			out.printf("	<section class=\"hero is-primary is-bold\">%n");
			out.printf("	  <div class=\"hero-body\">%n");
			out.printf("	    <div class=\"container\">%n");
			out.printf("	      <h1 class=\"title\">%n");
			out.printf("	        Sedentary Search%n");
			out.printf("	      </h1>%n");
			out.printf("	      <h2 class=\"subtitle\">%n");
			out.printf("					<i class=\"fas fa-search\"></i>%n");
			out.printf("					&nbsp;Finds what you're looking for so quickly, you'd think your mother were a thread. %n");
			out.printf("	      </h2>%n");
			out.printf("	    </div>%n");
			out.printf("	  </div>%n");
			out.printf("	</section>%n");
			out.printf("%n");
			out.printf("	<section class=\"section\">%n");
			out.printf("		<div class=\"container\">%n");
			out.printf("			<h2 class=\"title\">Search History</h2>%n");
			out.printf("%n");
			
			for(String search : history)
			{
				out.printf("<p>%s</p>%n%n", search);
			}

			out.printf("				<div class=\"control\">%n");
			out.printf("			    <button class=\"button is-primary\" type=\"submit\">%n");
			out.printf("						<i class=\"fas fa-search\"></i>%n");
			out.printf("						&nbsp;%n");
			out.printf("						Clear History%n");
			out.printf("					</button>%n");
			out.printf("			  </div>%n");
			out.printf("			</form>%n");
			out.printf("		</div>%n");
			out.printf("	</section>%n");
			out.printf("%n");
			

			out.printf("	<footer class=\"footer\">%n");
			out.printf("	  <div class=\"content has-text-centered\">%n");
			out.printf("	    <p>%n");
			out.printf("	      This request was handled by thread %s on %s.%n", Thread.currentThread().getName(), getDate());
			out.printf("	    </p>%n");
			out.printf("	  </div>%n");
			out.printf("	</footer>%n");
			out.printf("</body>%n");
			out.printf("</html>%n");
		}
	}
}