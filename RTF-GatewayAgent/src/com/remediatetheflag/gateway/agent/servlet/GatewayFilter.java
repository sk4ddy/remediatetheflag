package com.remediatetheflag.gateway.agent.servlet;

import java.io.IOException;
import java.util.Base64;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import com.remediatetheflag.gateway.agent.model.Constants;
import com.remediatetheflag.gateway.agent.utils.GatewayAgentConfig;

public class GatewayFilter implements Filter {

	protected static org.slf4j.Logger logger = LoggerFactory.getLogger(GatewayFilter.class);

	private String username = GatewayAgentConfig.getAgentUsername();
	private String password = GatewayAgentConfig.getAgentPassword();
	private String realm = "rtf-gateway-agent";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		String authHeader = request.getHeader(Constants.AUTHORIZATION_HEADER);
		if (authHeader != null) {
			StringTokenizer st = new StringTokenizer(authHeader);
			if (st.hasMoreTokens()) {
				String basic = st.nextToken();
				if (basic.equalsIgnoreCase(Constants.BASIC_AUTH_HEADER)) {
					try {
						String credentials = new String(Base64.getDecoder().decode(st.nextToken()), Constants.CHARSET);
						int p = credentials.indexOf(":");
						if (p != -1) {
							String _username = credentials.substring(0, p).trim();
							String _password = credentials.substring(p + 1).trim();

							if (!username.equals(_username) || !password.equals(_password)) {
								logger.warn("Wrong username/password supplied for user "+_username);
								sendAuthenticationFailedMessage(response);
								return;
							}
							
							filterChain.doFilter(servletRequest, servletResponse);
							return;
						}
					} catch (Exception e) {
						logger.error(e.getMessage());
						sendAuthenticationFailedMessage(response);
					}
				}
			}
		}
		sendAuthenticationFailedMessage(response);
	}

	@Override
	public void destroy() {
	}

	private void sendAuthenticationFailedMessage(HttpServletResponse response) throws IOException {
		response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
		response.sendError(401, "Unauthorized");
		logger.warn("Authorization denied for GatewayAgent");
	}

}