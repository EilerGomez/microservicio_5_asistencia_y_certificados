/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.microservicio_5_asistencias_certificados.utils;

/**
 *
 * @author eiler
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokensDevPrinterTest {

    @InjectMocks
    private TokensDevPrinter printer;

    private static final String JWT_SECRET =
            "VGhpc0lzQVN1cGVyU2VjdXJlSldUU2VjcmV0S2V5Rm9yRGV2TXVzdEJlQXRMZWFzdDMyQ2hhcnM=";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(printer, "jwtSecret", JWT_SECRET);
    }

    @Test
    void runEjecutaSinExcepcion() {
        assertDoesNotThrow(() -> printer.run());
    }

    @Test
    void runGeneraTokensYImprime() throws Exception {
        // Captura stdout para verificar que se imprime algo
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.PrintStream original = System.out;
        System.setOut(new java.io.PrintStream(out));

        printer.run();

        System.setOut(original);

        String salida = out.toString();

        assertTrue(salida.contains("TOKENS DE DESARROLLO"));
        assertTrue(salida.contains("ADMIN_SISTEMA"));
        assertTrue(salida.contains("ADMIN_CONGRESO"));
        assertTrue(salida.contains("PARTICIPANTE"));
        assertTrue(salida.contains("uid: 1"));
        assertTrue(salida.contains("uid: 2"));
        assertTrue(salida.contains("uid: 42"));
    }

    @Test
    void runGeneraTresTokensJwtDistintos() throws Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.PrintStream original = System.out;
        System.setOut(new java.io.PrintStream(out));

        printer.run();

        System.setOut(original);

        String salida = out.toString();
        long cantidadTokens = java.util.Arrays.stream(salida.split("\n"))
                .filter(linea -> linea.trim().startsWith("eyJ"))
                .count();

        assertEquals(3, cantidadTokens);
    }
}