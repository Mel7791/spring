package com.aluracursos.spring;

import com.aluracursos.spring.principal.Principal;
import com.aluracursos.spring.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Application implements CommandLineRunner {
	@Autowired
	private SerieRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repository);
		principal.muestraElMenu();
		//principal.muestraMenuAnterior();

//		EjemploStreams ejemploStreams = new EjemploStreams();
//		ejemploStreams.muestraEjemplo();
	}
}