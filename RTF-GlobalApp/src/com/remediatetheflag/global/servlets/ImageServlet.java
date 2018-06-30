package com.remediatetheflag.global.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.remediatetheflag.global.messages.MessageGenerator;
import com.remediatetheflag.global.model.AvailableExerciseInfo;
import com.remediatetheflag.global.persistence.HibernatePersistenceFacade;
import com.remediatetheflag.global.utils.Constants;

public class ImageServlet extends HttpServlet {

	private static final long serialVersionUID = 7661212624813244214L;
	private HibernatePersistenceFacade hpc = new HibernatePersistenceFacade();
	private static Logger logger = LoggerFactory.getLogger(ImageServlet.class);

	protected void doGet(HttpServletRequest req, HttpServletResponse res) {
		try {
			handleRequest(req, res);
		}catch(Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void handleRequest(HttpServletRequest req, HttpServletResponse res) throws IOException,ServletException {

		String type = req.getParameter("type");
		String id = req.getParameter("id");

		if(type.equals(Constants.IMAGES_AVAILABLE_EXERCISES_INFO)) {
			AvailableExerciseInfo info = hpc.getAvailableExerciseInfo(Integer.valueOf(id));
			MessageGenerator.sendImageFileMessage(info.getImage(), res);
			return;
		}
		MessageGenerator.sendErrorMessage("NoResults", res);

	}
}
