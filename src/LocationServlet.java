import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class LocationServlet extends HttpServlet {
	
	private static final String TITLE = "Search";
	private final ThreadSafeInvertedIndex index;
	
	/**
	 * Constructor for the servlet
	 * @param index - a thread safe inverted index
	 */
	public LocationServlet(ThreadSafeInvertedIndex index) 
	{
		super();
		this.index = index; 
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		printForm(request, response, index);

		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	/**
	 * Method that properly formats printing the locations of the index
	 * 
	 * @param request
	 * @param response
	 * @param index - the index we are printing
	 * @throws IOException
	 */
	private static void printForm(HttpServletRequest request, HttpServletResponse response, ThreadSafeInvertedIndex index) throws IOException
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
		out.printf("			<h2 class=\"title\">Display Inverted Index Locations</h2>%n");
		out.printf("%n");
		
		if(index.words() == 0)
		{
			out.printf("<p> The index is empty </p>");
		}
		
		else
		{
			int counter = 1;
			for(var var : index.getLocationPaths())
			{
				out.printf("<p>%d. <a href=%s>%s: ", counter, var, var, "</a></p>%n");
				out.printf("</a>");
				out.printf("%d words", index.getLocationSize(var));
				counter++;
			}
		}
		out.printf("			</div>%n");
		out.printf("%n");
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
}