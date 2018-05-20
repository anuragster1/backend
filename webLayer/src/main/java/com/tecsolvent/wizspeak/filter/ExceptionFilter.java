package com.tecsolvent.wizspeak.filter;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author sunil.kata
 * @since 16/01/16
 */

public class ExceptionFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			httpServletResponse.setContentType("application/json");
			if (e.getCause() instanceof Exception) {
				httpServletResponse.getWriter().println(e.getCause().toString());
			}
		}
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

}
