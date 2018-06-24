package com.remediatetheflag.gateway.extras;
import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet({"/cservlet"})
public class CServlet extends HttpServlet {

	private static Logger logger = LoggerFactory.getLogger(CServlet.class);
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException	{
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException	{
		String user = request.getParameter(Constants.USER);
		String token = request.getParameter(Constants.TOKEN);

		String msg = "{\"authToken\":\"" + token + "\",\"username\":\"" + user + "\",\"dataSource\":\"mysql\",\"availableDataSources\":[\"mysql\"]}";
		String encodedMsg = URLEncoder.encode(msg, Constants.CHARSET);

		Cookie cookie = getCookie(request, Constants.GUAC_AUTH_COOKIE);
		if (cookie != null)	{
			cookie.setValue(encodedMsg);
			response.addCookie(cookie);
		}
		else	{
			Cookie guacCookie = new Cookie(Constants.GUAC_AUTH_COOKIE, encodedMsg);
			response.addCookie(guacCookie);
		}
		logger.debug("Handled request for guac temp user "+user);
		response.sendRedirect(Constants.RTF_PATH);
	}

	private static Cookie getCookie(HttpServletRequest request, String name) {
		if (request.getCookies() != null){
			Cookie[] arrayOfCookie;
			int j = (arrayOfCookie = request.getCookies()).length;
			for (int i = 0; i < j; i++)	{
				Cookie cookie = arrayOfCookie[i];
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		}
		return null;
	}
}