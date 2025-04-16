package com.example.codexlibris;

import java.util.*;

public class DatosSimulados {

    public static Map<Integer, List<LibroReservado>> obtenerHistoriales() {
        Map<Integer, List<LibroReservado>> historicos = new HashMap<>();

        historicos.put(2, Arrays.asList( // usuari: geral_rivia,
                new LibroReservado("Dune", "Frank Herbert"),
                new LibroReservado("Fundación", "Isaac Asimov"),
                new LibroReservado("Neuromante", "William Gibson")
        ));

        historicos.put(3, Arrays.asList( // usuari: laia_miret
                new LibroReservado("Orgullo y prejuicio", "Jane Austen"),
                new LibroReservado("Cumbres borrascosas", "Emily Brontë"),
                new LibroReservado("Jane Eyre", "Charlotte Brontë")
        ));

        return historicos;
    }
}
