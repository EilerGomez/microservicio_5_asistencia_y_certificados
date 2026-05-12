/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.seguridad;

/**
 *
 * @author eiler
 */

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FiltroAutenticacionGatewayTest {

    enum Rol { ADMIN_CONGRESO, ADMIN_SISTEMA, PARTICIPANTE, USER }

    @InjectMocks private FiltroAutenticacionGateway filtro;
    @Mock private HttpServletRequest  request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain         filterChain;

    private static final String JWT_SECRET =
            "VGhpc0lzQVN1cGVyU2VjdXJlSldUU2VjcmV0S2V5Rm9yRGV2TXVzdEJlQXRMZWFzdDMyQ2hhcnM=";

    private Key signingKey;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(filtro, "jwtSecret", JWT_SECRET);
        byte[] keyBytes = Base64.getDecoder().decode(JWT_SECRET);
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void conHeadersValidosEstableceAutenticacion() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-User-Id")).thenReturn("42");
        when(request.getHeader("X-User-Role")).thenReturn(Rol.ADMIN_CONGRESO.name());

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals("42", auth.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void conHeadersValidosAsignaRolAdminSistema() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-User-Role")).thenReturn(Rol.ADMIN_SISTEMA.name());

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Rol.ADMIN_SISTEMA.name())));
    }

    @Test
    void conRolParticipanteAsignaRolParticipante() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-User-Id")).thenReturn("99");
        when(request.getHeader("X-User-Role")).thenReturn(Rol.PARTICIPANTE.name());

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_" + Rol.PARTICIPANTE.name())));
        assertEquals("99", auth.getPrincipal());
    }

    @Test
    void sinHeadersNoEstableceAutenticacion() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-User-Role")).thenReturn(null);

        filtro.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void soloUserIdSinRoleNoEstableceAutenticacion() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-User-Id")).thenReturn("42");
        when(request.getHeader("X-User-Role")).thenReturn(null);

        filtro.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void soloRoleSinUserIdNoEstableceAutenticacion() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-User-Id")).thenReturn(null);
        when(request.getHeader("X-User-Role")).thenReturn(Rol.ADMIN_CONGRESO.name());

        filtro.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void rolTienePrefijoRoleAgregado() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getHeader("X-User-Id")).thenReturn("1");
        when(request.getHeader("X-User-Role")).thenReturn(Rol.ADMIN_CONGRESO.name());

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String autoridad = auth.getAuthorities().iterator().next().getAuthority();
        assertTrue(autoridad.startsWith("ROLE_"));
        assertEquals("ROLE_" + Rol.ADMIN_CONGRESO.name(), autoridad);
    }


    @Test
    void conJwtValidoEstableceAutenticacion() throws Exception {
        String token = generarToken(1L, Rol.ADMIN_CONGRESO, 86400000L);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.isAuthenticated());
        assertEquals("1", auth.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void conJwtValidoAsignaRolCorrectamente() throws Exception {
        String token = generarToken(42L, Rol.ADMIN_SISTEMA, 86400000L);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        filtro.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority()
                        .equals("ROLE_" + Rol.ADMIN_SISTEMA.name())));
    }

    @Test
    void conJwtExpiradoNoEstableceAutenticacionYContinuaCadena() throws Exception {
        String token = generarToken(1L, Rol.ADMIN_CONGRESO, -1L);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        filtro.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void conJwtInvalidoNoEstableceAutenticacionYContinuaCadena() throws Exception {
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer token.invalido.xxx");

        filtro.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void conJwtValidoSiempreContinuaCadena() throws Exception {
        String token = generarToken(1L, Rol.USER, 86400000L);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        filtro.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    private String generarToken(Long uid, Rol rol, long expiracionMs) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid",  uid);
        claims.put("role", rol.name());
        claims.put("type", "ACCESS");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("test@test.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiracionMs))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
