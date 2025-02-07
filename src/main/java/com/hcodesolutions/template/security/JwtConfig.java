package com.hcodesolutions.template.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@Component
public class JwtConfig implements AuthenticationEntryPoint,Serializable{

    @Serial
    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
    	System.out.println("Called to JWT Config------");
    	String jsonErrorMessage = "{\"error\": \"JWT token expired\"}";
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, jsonErrorMessage);
    }

}
