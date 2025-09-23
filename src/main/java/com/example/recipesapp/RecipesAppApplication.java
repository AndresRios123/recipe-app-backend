package com.example.recipesapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * RecipesAppApplication
 * ----------------------
 * Clase principal de la aplicación.
 * 
 * - Anotación @SpringBootApplication: combina @Configuration, @EnableAutoConfiguration y @ComponentScan,
 *   lo que indica a Spring Boot que esta es la clase de arranque.
 *
 * - Contiene el método main, que es el punto de entrada de la aplicación.
 *   Al ejecutarlo, se inicia el servidor embebido (por defecto Tomcat),
 *   se configuran los beans y se levanta toda la aplicación.
 */
@SpringBootApplication
public class RecipesAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipesAppApplication.class, args);
    }

}
