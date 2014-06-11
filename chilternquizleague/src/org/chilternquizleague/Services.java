/**
 * 
 */
package org.chilternquizleague;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;

/**
 * @author gb106507
 *
 */
@SuppressWarnings("serial")
public class Services extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if(req.getPathInfo().contains("table")){
			
			UrlFetchTransport transport = new UrlFetchTransport();
			
			Storage storage = new Storage(transport, new GsonFactory(), null);
			
			storage.objects().insert("test", new StorageObject());
	
			
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
