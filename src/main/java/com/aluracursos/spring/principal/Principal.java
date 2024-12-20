package com.aluracursos.spring.principal;

import ch.qos.logback.core.encoder.JsonEscapeUtil;
import com.aluracursos.spring.model.*;
import com.aluracursos.spring.repository.SerieRepository;
import com.aluracursos.spring.service.ConsumoAPI;
import com.aluracursos.spring.service.ConvierteDatos;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import static org.apache.logging.log4j.ThreadContext.peek;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    //uso de API
    private ConsumoAPI consumoApi = new ConsumoAPI();
    //Constantes de URL
    private final String URL_BASE = "http://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=df671dd";
    //conversor de datos
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSerie = new ArrayList<>();
    private  SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }


    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    \n**************************
                        MENU
                    1 - Buscar Series
                    2 - Buscar Episodios
                    3 - Mostrar Series buscadas
                    4 - Buscar series por titulo
                    5 - Buscar top 5 series
                    6 - Buscar series por categoria
                    7 - Filtrar series por cantidad de temporadas y su evaluacion
                    8 - Buscar Episodios por Nombre
                    9 - Top 5 episodios por serie
                    
                    0 - Salir
                    ***************************
                    """;

            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    buscarSeriesPorNumeroTEmporadasYEvaluacion();
                    break;
                case 8:
                    buscarEpisodioPorNombre();
                    break;
                case 9:
                    buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Serrando la aplicacion...");
                    break;
                default:
                    System.out.println("Opcion no valida");
            }
        }
    }

    private DatosSerie getDatosSerie() {
        System.out.println("\n*********************************************************");
        System.out.println("\nEscribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        String url = URL_BASE + nombreSerie.replace(" ", "+") + API_KEY;
        var json = consumoApi.obtenerDatos(url);
        System.out.println((String) json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("\n*********************************************************");
        System.out.println("\nEscribe nombre de serie que deseas ver por episodio");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s->s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if(serie.isPresent()){
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalDeTemporadas(); i++) {

                String url2 = URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&Season=" + i + API_KEY;
                var json = consumoApi.obtenerDatos(url2);
                DatosTemporadas datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporadas);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d->d.episodios().stream()
                            .map(e->new Episodio(d.numeroTemporada(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }
    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie series = new Serie(datos);
        repositorio.save(series);
       // datosSerie.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }

    private void buscarSeriesPorTitulo(){
        System.out.println("\n*********************************************************");
        System.out.println("\nEscribe nombre de serie por titulo de la lista");
        var nombreSerie = teclado.nextLine();
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if(serieBuscada.isPresent()){
            System.out.println("\n*********************************************************");
            System.out.println("\nLa serie buscada es: " +serieBuscada.get());
        }else{
            System.out.println("\n*********************************************************");
            System.out.println("\nSerie no encontrada");
        }
    }

    private void buscarTop5Series(){
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s->
                System.out.println("\nSerie: "  +s.getTitulo() + ", Evaluacion: " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCategoria(){
        System.out.println("\n*********************************************************");
        System.out.println("\nEscribe el genero/categoria de la serie que deseas buscar");
        var genero = teclado.nextLine();
        var category = Category.fromEspanol(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(category);
        System.out.println("\n*********************************************************");
        System.out.println("\nLas series de la categoria: " +genero);
        seriesPorCategoria.forEach((System.out::println));

    }

    private void buscarSeriesPorNumeroTEmporadasYEvaluacion(){
        System.out.println("\n*********************************************************");
        System.out.println("\nEscribe cantidad de temporadas de la serie que deseas buscar: ");
        int totalDeTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("\nEscribe el valor minima de evaluacion: ");
        double evaluacion = teclado.nextDouble();
        teclado.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadasYEvaluacion(totalDeTemporadas, evaluacion);
        System.out.println("**** Series filtradas ****");
        filtroSeries.forEach(s-> System.out.println(s.getTitulo() + " con evaluacion: " + s.getEvaluacion()));
    }

    private void buscarEpisodioPorNombre(){
        System.out.println("Escribe el titulo del episodio que desea buscar");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodioPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e->
                System.out.printf("Serie: %s - Temporada: %s - Episodio: %s - Evaluacion: %s",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));
    }

    private void buscarTop5Episodios(){
        buscarSeriesPorTitulo();
        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios= repositorio.top5Episodios(serie);
            topEpisodios.forEach(e->
                    System.out.printf("Serie: %s - Temporada: %s - Numero de Episodio: %s - Titulo: %s - Evaluacion: %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getEvaluacion()));
        }

    }
}



//codigo anterior
    //interaccion con usuario
/*    public void muestraMenuAnterior() {
        System.out.println("\nEscribe el nombre de la serie que desea buscar");
        var nombreSerie = teclado.nextLine();
//Busca datos generales de cualquier serie
        String url = URL_BASE + nombreSerie.replace(" ", "+") + API_KEY;
        var json = consumoApi.obtenerDatos(url);
        System.out.println(json);
//El conversor de JSON to Java
        var datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos);

//Busca los datos de todas las temporadas
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i <= datos.totalDeTemporadas(); i++) {
            String url2 = URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + API_KEY;
            json = consumoApi.obtenerDatos(url2);
            var datosTemporadas = conversor.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporadas);
        }//imprimimos el resultado de loop
        System.out.println("\nINFORMACION DETALLADA POR TEMPORADA:");
        temporadas.forEach(System.out::println);


        //mostrar solo titulos de los episodios para todas las temporadas
        //primero llamamos todos los episodios de cada temporada:
//        for (int i = 0; i < datos.totalDeTemporadas(); i++) {
//            List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            //aqui llamamos titulo de cada episodio:
//            for (int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println("El nombre del episodio: " + episodiosTemporada.get(j).titulo());
//            }
//        }
        System.out.println("\nLISTA DE TEMPORADAS CON LOS TITULOS DE EPISODIOS");
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println("Temporada:" + t.numeroTemporada() + " Episodio: " + e.titulo())));//es la version con lambda

        //Hacer una lista de temporadas con episodios con stream y lambda
        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        //top 5 episodios
        System.out.println("\nTOP 5 EPISODIOS:");
        datosEpisodios.stream()
                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("Primer filtro (N/A) " + e))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
                .peek(e -> System.out.println("Segundo filtro (M>m)" + e))
                .map(e -> e.titulo().toUpperCase())
                .peek(e -> System.out.println("Tercer filtro mayusculas (m>M)" + e))
                .limit(5)
                .forEach(System.out::println);

        //Convirtiendo los datos a datos Episodio con sus Temporadas
        System.out.println("\nLISTA PRINCIPAL DE TEMPORADAS CON EPISODIOS:");
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numeroTemporada(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        //Escoger por fecha de lanzaiento
        System.out.println("\nIntroduce desde que fecha desea ver el episodio");
        var fecha = teclado.nextInt();
        teclado.nextLine();

        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //latino

        episodios.stream()
                .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
                .forEach(e -> System.out.println(
                        String.format("Temporada " + e.getTemporada() +
                                ", Episodio " + e.getNumeroEpisodio() +
                                ", Titulo: " + e.getTitulo() +
                                ", Fecha de lanzamiento " + e.getFechaDeLanzamiento().format(dtf))));

        //Busca de episodio por un pedazo de titulo
        System.out.println("Escribe la palabra clave del titulo que deseas ver: ");
        var pedazoTitulo = teclado.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase())) //para q coincida todo ponemos en mayuscula
                .findFirst();
        if (episodioBuscado.isPresent()) {
            System.out.println("Episodio encontrado");
            System.out.println("Los datos son: " + episodioBuscado.get().getTitulo());
        } else {
            System.out.println("Episodio no se encontro");
        }

        //Evaluaciones a base de temporadas
        Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));
        System.out.println("\nEVALUACIONES POR TEMPORADA: " + evaluacionesPorTemporada);

        //Media de Evaluaciones
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getEvaluacion() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println("Media de evaluaciones: " + est.getAverage());
        System.out.println("Mejor Evaluado: " + est.getMax());
        System.out.println("Peor evaluado: " + est.getMin());
    }*/




