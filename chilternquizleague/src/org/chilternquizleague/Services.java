/**
 * 
 */
package org.chilternquizleague;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.LeagueTable;

import com.google.api.client.json.gson.GsonFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * @author gb106507
 *
 */
@SuppressWarnings("serial")
public class Services extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if(req.getPathInfo().contains("leaguetable/current")){
			
			Objectify ofy = ObjectifyService.ofy();

			final List<LeagueTable> tables = ofy.load().type(LeagueTable.class).filter("endYear >=", Calendar.getInstance().get(Calendar.YEAR)).list();
			
			if(!tables.isEmpty()){			
				resp.getOutputStream().write(GsonFactory.getDefaultInstance().toByteArray(tables.iterator().next()));
			}
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
	}

}
