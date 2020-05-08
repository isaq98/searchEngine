import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;

@SuppressWarnings("serial")
public class CookieBaseServlet extends HttpServlet 
{	
	/**
	 * Method that places cookies into a map
	 * 
	 * @param request
	 * @return - a map of cookies
	 */
	public Map<String, Cookie> getCookieMap(HttpServletRequest request)
	{
		HashMap<String, Cookie> map = new HashMap<>();
		
		Cookie[] cookies = request.getCookies();
		
		if(cookies != null)
		{
			for(Cookie cookie : cookies)
			{
				map.put(cookie.getName(), cookie);
			}
		}
		
		return map;
	}
	
	/**
	 * Method to clear cookies
	 * 
	 * @param request
	 * @param response
	 */
	public void clearCookies(HttpServletRequest request, HttpServletResponse response) 
	{
		Cookie[] cookies = request.getCookies();
		
		if(cookies != null)
		{
			for(Cookie cookie : cookies)
			{
				cookie.setValue(null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}
}