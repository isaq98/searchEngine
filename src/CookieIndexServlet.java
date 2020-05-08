import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import java.net.URLEncoder;

import java.util.Map;

@SuppressWarnings("serial")
public class CookieIndexServlet extends CookieBaseServlet 
{	
	private static final String TITLE = "Search";
	public static final String VISIT_DATE = "Visited";
	
	/**
	 * Method to print the last known visit time of the user
	 * 
	 * @param request
	 * @param response
	 *
	 * @throws IOException
	 * @throws ServletException
	 */
	public void printPrompt(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		
		Map<String, Cookie> cookies = getCookieMap(request);
		
		Cookie visitDate = cookies.get(VISIT_DATE);
		
		response.setContentType("text/html");
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
		out.printf("%n");
		
		out.printf("<h2>%n");
		if(visitDate == null)
		{
			visitDate = new Cookie(VISIT_DATE, "");
			
			out.printf("Welcome, thanks for stopping by.%n");
		}
		else
		{
			try
			{
				String rawValue = visitDate.getValue();
				String decoded = URLDecoder.decode(rawValue, StandardCharsets.UTF_8);
				String escaped = StringEscapeUtils.escapeHtml4(decoded);
				
				out.printf("Your last visit to this site was: %s", escaped);
			}
			catch(NullPointerException | IllegalArgumentException e)
			{
				visitDate = new Cookie(VISIT_DATE, "");
				
				out.printf("Your cookies seemed to have an issue.");
			}
		}
		
		
		String encoded = URLEncoder.encode(SearchServlet.getDate(), StandardCharsets.UTF_8);
		visitDate.setValue(encoded);
		
		if(request.getIntHeader("DNT") != 1)
		{
			response.addCookie(visitDate);
		}
		else
		{
			clearCookies(request, response);
			out.printf("Your visits will not be tracked.");
		}
		
		out.printf("	      </h2>%n");
		out.printf("	    </div>%n");
		out.printf("	  </div>%n");
		out.printf("	</section>%n");
		out.printf("%n");

	}
}