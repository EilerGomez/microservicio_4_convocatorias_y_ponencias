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
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtTestUtil {

    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generarToken(Long idUsuario, String rol) {
        return Jwts.builder()
                .setClaims(Map.of("id", idUsuario, "rol", rol))
                .setSubject(String.valueOf(idUsuario))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24h
                .signWith(KEY)
                .compact();
    }
}