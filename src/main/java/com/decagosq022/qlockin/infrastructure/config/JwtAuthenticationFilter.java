package com.decagosq022.qlockin.infrastructure.config;

import com.decagosq022.qlockin.exceptions.ExpiredJwtException;
import com.decagosq022.qlockin.repository.JTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final JTokenRepository jTokenRepository;

    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        final String authHeader = request.getHeader("Authorization");
//        final String jwt;
//        final String userEmail;
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            jwt = authHeader.substring(7);
//            userEmail = jwtService.extractUsername(jwt);
//
//            //System.out.println("Extracted User Email: " + userEmail);
//
//            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//                boolean isTokenValid = jTokenRepository.findByToken(jwt)
//                        .map(t -> !t.isExpired() && !t.isRevoked())
//                        .orElse(false);
//
//
////                System.out.println("Let say you entered here: " + userEmail);
////
////                if (jwtService.isTokenValid(jwt, userDetails)) {
////                    System.out.println("Passed the first test: " + userEmail);
////                }
////
////                if(isTokenValid){
////                    System.out.println("Toke is Valid: " + userEmail);
////                }
//
//
//                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//
//                    System.out.println("User authenticated: " + authentication.getName());
//                }
//            }
//
//            filterChain.doFilter(request, response);
//        } catch (ExpiredJwtException e) {
//            // System.out.println("JWT Token expired for user: " + e.getClaims().getSubject());
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired");
//        } catch (IOException e) {
//            // System.out.println("IO Exception occurred: " + e.getMessage());
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is not valid");
//        }
//    }

   protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if(authHeader == null || !authHeader.startsWith("Bearer")){
            filterChain.doFilter(request, response);
            return;
        }
        try {
            jwt = authHeader.substring(7);

            userEmail = jwtService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                var isTokenValid = jTokenRepository.findByToken(jwt)
                        .map(t -> !t.isExpired() && !t.isRevoked())
                        .orElse(false);

                if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                }
            }
            filterChain.doFilter(request,response);
        } catch (ExpiredJwtException | ServletException e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired");
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is not valid");
        }
    }
}