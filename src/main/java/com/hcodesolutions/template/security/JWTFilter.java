package com.hcodesolutions.template.security;

import com.hcodesolutions.template.service.AuthService;
import com.hcodesolutions.template.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter{

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthService userDetailsService;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
                final String authHeader = request.getHeader("Authorization");
                final String jwt;
                final String userName;
                if (authHeader == null || !authHeader.startsWith("Bearer")) {
                    filterChain.doFilter(request, response);
                    return;
                }
                jwt = authHeader.substring(7);
                userName = jwtUtil.extractUsername(jwt);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (userName != null && authentication == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        
                    if (jwtUtil.isTokenValid(jwt, userDetails)) {
                        String userId = jwtUtil.extractUsername(jwt);
                        
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("userId", userId);
                    	UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken.setDetails(map);
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        
                    }else{
                        throw new RuntimeException("Authentication Invalid");
                    }
                }
                filterChain.doFilter(request, response);
    }
    
 
}
