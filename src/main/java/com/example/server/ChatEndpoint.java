package com.example.server;

import org.apache.http.HttpStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class ChatEndpoint extends HttpServlet {
    private static final long serialVersionUID = 8032611514671727168L;

    Logger logger = Logger.getLogger("");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            Map<String, String[]> parameters = req.getParameterMap();

            for (String key : parameters.keySet()) {
                logger.info(key + ":" + parameters.get(key));
            }

            resp.setStatus(HttpStatus.SC_OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

    }

}
