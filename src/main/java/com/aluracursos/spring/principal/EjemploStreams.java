package com.aluracursos.spring.principal;

import java.util.Arrays;
import java.util.List;


public class EjemploStreams {
    public void muestraEjemplo(){
        List<String> nombres = Arrays.asList("Mellena", "Javier", "Oscar", "Alberto", "Mandy Masha", "Maria", "Vladimir");
        nombres.stream()
                .sorted()
                .filter(n->n.startsWith("M")) //intermediate operations
                .map(n->n.toUpperCase())
                .limit(6)
                .forEach(System.out::println); //final operations: .collect, .count

    }
}
