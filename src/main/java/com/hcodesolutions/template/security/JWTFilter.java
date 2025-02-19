package com.hcodesolutions.template.security;

import com.hcodesolutions.template.service.AuthService;
import com.hcodesolutions.template.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter{
    private static Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthService userDetailsService;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        String email = null;
        String jwtToken = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);
            email = jwtUtil.extractUsername(jwtToken);
            System.out.println(email);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                if (!jwtUtil.isTokenValid(jwtToken, userDetails)) {
                    logger.error("Token is invalid or expired for user {}", email);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Token is invalid or expired");
                    return;
                }

                List<String> requiredRoles = jwtUtil.extractRoles(authorizationHeader); // Extract roles from JWT
                boolean hasRequiredRole = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority) // Get user's roles
                        .anyMatch(requiredRoles::contains);  // Check if any of the required roles match
                if (!hasRequiredRole) {
                    logger.error("Access Denied for user {}: Missing one of the required roles {}", email, requiredRoles);
                    throw new AccessDeniedException("Access Denied: You do not have the required role(s) to access this resource.");
                }

                // Convert the required roles into GrantedAuthority objects
                List<GrantedAuthority> grantedAuthorities = requiredRoles.stream()
                        .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role)) // Explicitly cast
                        .collect(Collectors.toList());

                // Create and set the Authentication object with updated roles
                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, grantedAuthorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                logger.info("User {} authenticated successfully with role {}", email, requiredRoles);
            } catch (Exception e) {
                logger.error("Authentication failed for user {}: {}", email, e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid token or user details");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
    
 
}
