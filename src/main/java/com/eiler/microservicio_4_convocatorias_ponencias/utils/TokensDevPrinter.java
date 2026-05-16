/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eiler.microservicio_4_convocatorias_ponencias.utils;

/**
 *
 * @author eiler
 */


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Profile("dev") // solo en dev
public class TokensDevPrinter implements CommandLineRunner {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public void run(String... args) {

        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String tokenAdminSistema  = generarToken(1L,  "ADMIN_SISTEMA",  "admin.sistema@dev.com",  key);
        String tokenAdminCongreso = generarToken(2L,  "ADMIN_CONGRESO", "admin.congreso@dev.com", key);
        String tokenParticipante  = generarToken(42L, "PARTICIPANTE",   "participante@dev.com",   key);

        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║            TOKENS DE DESARROLLO — SOLO EN DEV            ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║       en Postman → Header: Authorization: Bearer <token> ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  ADMIN_SISTEMA  (uid: 1)                                 ║");
        System.out.println("╟──────────────────────────────────────────────────────────╢");
        System.out.println(tokenAdminSistema);
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  ADMIN_CONGRESO (uid: 2)                                 ║");
        System.out.println("╟──────────────────────────────────────────────────────────╢");
        System.out.println(tokenAdminCongreso);
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  PARTICIPANTE   (uid: 42)                                ║");
        System.out.println("╟──────────────────────────────────────────────────────────╢");
        System.out.println(tokenParticipante);
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }

    private String generarToken(Long uid, String role, String email, Key key) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid",  uid);
        claims.put("role", role);
        claims.put("type", "ACCESS");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000L)) // 24h
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
