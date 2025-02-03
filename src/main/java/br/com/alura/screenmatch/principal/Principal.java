package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private List<Serie> series = new ArrayList<>();


    private SerieRepository repositorio;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {

        var opcao = -1;

        while (opcao != 0){

            var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3-  Listar Series Buscadas
                4 - Buscar Série por Título
                5 - Buscar Séries por Ator
                6 - Top 5 Séries
                0 - Sair                                 
                """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                    case 5:
                    buscarSeriePorAtor();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }

    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator para a busca: ");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Series em que o " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliacao " + s.getAvaliacao() ));
        System.out.println(seriesEncontradas);
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        //dadosSeries.add(dados);
        Serie serie = new Serie(dados);

        repositorio.save(serie);

        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        System.out.println(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        // DadosSerie dadosSerie = getDadosSerie();

        listarSeriesBuscadas();

        System.out.println("Escolha a serie pelo nome");

        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()) {

            List<DadosTemporada> temporadas = new ArrayList<>();

            var serieEncontrada = serie.get();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios =  temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(),e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }
        else{
            System.out.println("Series não encontradas");
        }
    }



    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

  /*  private void listarSeriesBuscadas() {
        series = repositorio.findAll(); // Isso busca as séries, mas os episódios não são carregados
        series.forEach(serie -> {
            Serie serieComEpisodios = repositorio.findByIdWithEpisodes(serie.getId()).orElse(serie);
            System.out.println(serieComEpisodios);
        });
    }*/

    private void buscarSeriePorTitulo() {
        System.out.println("Digite parte do nome da série para busca:");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> seriesEncontradas = repositorio.findByTituloContainingIgnoreCase(nomeSerie);


        if (seriesEncontradas.isEmpty()) {
            System.out.println("Nenhuma série encontrada com esse nome.");
        } else {
           /* seriesEncontradas.forEach(serie -> {
                System.out.println("Série: " + serie.getTitulo());
                System.out.println("Gênero: " + serie.getGenero());
                System.out.println("Avaliação: " + serie.getAvaliacao());
                System.out.println("Total de Temporadas: " + serie.getTotalTemporadas());

                if (serie.getEpisodios() != null && !serie.getEpisodios().isEmpty()) {
                    System.out.println("Episódios:");
                    serie.getEpisodios().forEach(ep ->
                            System.out.println("Temporada " + ep.getTemporada() + " - Episódio " + ep.getNumeroEpisodio() + ": " + ep.getTitulo())
                    );
                } else {
                    System.out.println("Nenhum episódio encontrado.");
                }

                System.out.println("--------------------------");
            })*/;
        }
    }



}