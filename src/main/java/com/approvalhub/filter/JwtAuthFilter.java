package com.approvalhub.filter;

import com.approvalhub.service.JwtService;
import com.approvalhub.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Récupère le header Authorization
        final String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Header Authorization reçu: " + authorizationHeader);

        // Vérifie la présence du préfixe Bearer
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Extrait le token (sans le préfixe "Bearer ")
        final String jwt  = authorizationHeader.substring(7);

        try {
            // Extrait le username du token
            final String username = jwtService.extractUsername(jwt);

            // Vérifie que l'utilisateur n'est pas déjà authentifié
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Charge les détails utilisateur depuis la base
                UserDetails  userDetails = customUserDetailsService.loadUserByUsername(username);

                // Valide le token (signature + expiration + correspondance user)
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Crée l'objet d'authentification
                    UsernamePasswordAuthenticationToken authenticationtoken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Ajoute les détails de la requête
                    authenticationtoken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Définit l'authentification dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authenticationtoken);
                }
            }
        } catch (ExpiredJwtException e) {
            // Token expiré : l'utilisateur devra se réauthentifier
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expiré");
            return;
        } catch (JwtException e) {
            // Token invalide : signature incorrect ou formal malformé
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token invalide");
            return;
        }

        //continue la chaîne de filtre
        filterChain.doFilter(request, response);

    }
}
