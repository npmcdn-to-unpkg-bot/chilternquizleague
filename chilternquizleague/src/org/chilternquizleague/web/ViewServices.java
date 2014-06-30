package org.chilternquizleague.web;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.GlobalApplicationData;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.views.GlobalApplicationDataView;
import org.chilternquizleague.views.LeagueTableView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.Key;

/**
 * Servlet implementation class ViewServices
 */
public class ViewServices extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private ObjectMapper objectMapper;
	


	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		objectMapper = new ObjectMapper();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (request.getPathInfo().contains("globaldata")) {

			globalData(response);
		}
		
		else if (request.getPathInfo().contains("leaguetable")) {

			currentLeagueTable(request, response);
		}
	}
	
	private void globalData(HttpServletResponse resp) throws IOException {
		
		final GlobalApplicationData data = ofy().load().now(
				Key.create(GlobalApplicationData.class,
						AppStartListener.globalApplicationDataId));

		if (data != null) {
			objectMapper.writeValue(resp.getWriter(),
					new GlobalApplicationDataView(data));
		}
	}
	
	private void currentLeagueTable(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

		final Long seasonId = Long.parseLong(req.getParameter("id"));

		final Season season = ofy().load().now(
				Key.create(Season.class, seasonId));

		if (season != null) {
			objectMapper.writeValue(resp.getWriter(), new LeagueTableView(
					season));

		}
	}

}
