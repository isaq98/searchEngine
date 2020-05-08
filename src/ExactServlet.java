import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

@SuppressWarnings("serial")
public class ExactServlet extends HttpServlet 
{
	private static final String TITLE = "Search";
	private final ThreadSafeInvertedIndex index;
	
	/**
	 * Constructor for the servlet
	 * @param index - thread safe inverted index
	 */
	public ExactServlet(ThreadSafeInvertedIndex index) 
	{
		super();
		this.index = index; 
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		printForm(request, response);

		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		printResultForm(request, response, index);
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * Method that properly prints the search page for the user
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private static void printForm(HttpServletRequest request, HttpServletResponse response) throws IOException
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
		out.printf("						Search for Exact Matches!%n");
		out.printf("					</button>%n");
		out.printf("			  </div>%n");
		out.printf("			</form>%n");
		out.printf("		</div>%n");
		out.printf("	</section>%n");
		out.printf("%n");
		out.printf("	<footer class=\"footer\">%n");
		out.printf("	  <div class=\"content has-text-centered\">%n");
		out.printf("	    <p>%n");
		out.printf("	      This request was handled by thread %s on %s.%n", Thread.currentThread().getName(), SearchServlet.getDate());
		out.printf("	    </p>%n");
		out.printf("	  </div>%n");
		out.printf("	</footer>%n");
		out.printf("</body>%n");
		out.printf("</html>%n");
	}
	
	/**
	 * Method that properly prints the search results
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
		out.printf("	        Ya Boy's Search Engine%n");
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
		
		
			for(var var : TextParser.parse(search))
			{
				searchWords.add(var);
			}
			
			final long startTime = System.currentTimeMillis();
			ArrayList<Result> resultList = index.exactSearch(searchWords);
			
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
		
		SearchServlet.promptQuery(request, response);
		
		out.printf("	<footer class=\"footer\">%n");
		out.printf("	  <div class=\"content has-text-centered\">%n");
		out.printf("	    <p>%n");
		out.printf("	      This request was handled by thread %s on %s.%n", Thread.currentThread().getName(), SearchServlet.getDate());
		out.printf("	    </p>%n");
		out.printf("	  </div>%n");
		out.printf("	</footer>%n");
		out.printf("</body>%n");
		out.printf("</html>%n");
		
	}

}