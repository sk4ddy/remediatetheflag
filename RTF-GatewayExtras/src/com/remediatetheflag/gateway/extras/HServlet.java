package com.remediatetheflag.gateway.extras;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet({"/hservlet"})
public class HServlet extends HttpServlet{

	private static Logger logger = LoggerFactory.getLogger(HServlet.class);

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType(Constants.CONTENT_TYPE_PLAIN);
		PrintWriter out = response.getWriter();
		out.print(Constants.JSON_VALUE_OK);
		out.close();
		logger.debug("Handled healtcheck request from "+request.getRemoteAddr());
	}
}