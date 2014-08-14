package org.chilternquizleague.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class URLRewriteFilter implements Filter {

	private ServletContext context;

	public URLRewriteFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;

		final String pathInfo = request.getPathInfo();

		if (pathInfo != null && !pathInfo.startsWith("_ah")) {

			if (context.getRealPath(pathInfo) == null) {

				if (pathInfo != null && pathInfo.startsWith("/maintain")) {
					request.getRequestDispatcher("/maintain.html").forward(
							arg0, arg1);
				} else {
					request.getRequestDispatcher("/index.html").forward(arg0,
							arg1);
				}

				return;
			}

		}

		arg2.doFilter(arg0, arg1);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

		context = arg0.getServletContext();
	}

}
