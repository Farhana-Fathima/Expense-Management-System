package Expense.Management.config;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import Expense.Management.service.JwtService;
import Expense.Management.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            //  Check if token is blacklisted
            if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
                sendUnauthorizedResponse(response, "Token is blacklisted. Please log in again.");
                return;
            }

            //  Extract username from JWT
            final String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendUnauthorizedResponse(response, "Invalid or expired token.");
        }
    }

    //  Sends a proper JSON response when the token is invalid or expired
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        response.getWriter().flush();
    }
}









// @Component
// @RequiredArgsConstructor
// public class JwtAuthenticationFilter extends OncePerRequestFilter {
//     private final JwtService jwtService;
//     private final UserDetailsService userDetailsService;
//     private final TokenBlacklistService tokenBlacklistService;

//     @Override
//     protected void doFilterInternal(HttpServletRequest request,
//                                     HttpServletResponse response,
//                                     FilterChain filterChain) throws ServletException, IOException {
//         final String authHeader = request.getHeader("Authorization");

//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             filterChain.doFilter(request, response);
//             return;
//         }

//         final String jwt = authHeader.substring(7);

//         try {
//             //  Check if token is blacklisted
//             if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
//                 sendUnauthorizedResponse(response, "Token is blacklisted. Please log in again.");
//                 return;
//             }

//             //  Extract username from JWT
//             final String username = jwtService.extractUsername(jwt);

//             if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                 UserDetails userDetails = userDetailsService.loadUserByUsername(username);

//                 if (jwtService.isTokenValid(jwt, userDetails)) {
//                     UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                             userDetails,
//                             null,
//                             userDetails.getAuthorities()
//                     );
//                     authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                     SecurityContextHolder.getContext().setAuthentication(authToken);
//                 }
//             }

//             filterChain.doFilter(request, response);
//         } catch (Exception e) {
//             sendUnauthorizedResponse(response, "Invalid or expired token.");
//         }
//     }

//     //  Sends a proper JSON response when the token is invalid or expired
//     private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
//         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//         response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//         response.getWriter().write("{\"error\": \"" + message + "\"}");
//         response.getWriter().flush();
//     }
// }







