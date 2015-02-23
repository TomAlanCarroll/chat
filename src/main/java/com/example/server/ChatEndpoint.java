package com.example.server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.http.HttpStatus;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class ChatEndpoint extends HttpServlet {
    private static final long serialVersionUID = 8032611514671727168L;

    private static final Jedis jedis = new Jedis("localhost");

    Logger logger = Logger.getLogger("");

    /**
     * GET method for chat requests
     * @param req The well-formed HttpServletRequest
     * @param resp The well-formed HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONArray toReturn = new JSONArray();
        JSONParser parser = new JSONParser();

        if (req.getParameter("id") != null && req.getParameter("id").trim().length() > 0) {
            try {
                String value = jedis.get(req.getParameter("id"));
                JSONObject parsed = (JSONObject) parser.parse(value);
                toReturn.add(parsed);
            } catch (Exception e) {
                logger.warning("Unrecognized data in Redis");
            }
        } else {
            // Query for all values
            Set<String> keys = jedis.keys("*");

            for (String key : keys) {
                try {
                    String value = jedis.get(key);
                    JSONObject parsed = (JSONObject) parser.parse(value);
                    toReturn.add(parsed);
                } catch (Exception e) {
                    logger.warning("Unrecognized data in Redis at ID " + key);
                }
            }
        }

        resp.getWriter().write(toReturn.toString());
        resp.setStatus(HttpStatus.SC_OK);
    }

    /**
     * POST method for Chat; This will dump the incoming request JSON into Redis
     * @param req The well-formed HttpServletRequest containing a parseable JSON object
     * @param resp The well-formed HttpServletResponse
     * @throws ServletException If the incoming JSON is not parseable,
     * there was a problem reading the request, or jedis couldn't save.
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            StringBuffer stringBuffer = new StringBuffer();
            String line = null, key = UUID.randomUUID().toString();
            JSONObject jsonObject = null;

            BufferedReader bufferedReader = req.getReader();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            try {
                jsonObject = (JSONObject)(new JSONParser()).parse(stringBuffer.toString());
            } catch (Exception e) {
                throw new ServletException("Unable to parse incoming JSON String.");
            }

            if (jsonObject.containsKey("id")) {
                key = jsonObject.get("id").toString();
            }

            jedis.set(key, jsonObject.toString());

            resp.setStatus(HttpStatus.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }
}
