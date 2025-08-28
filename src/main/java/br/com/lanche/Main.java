package br.com.lanche;

import br.com.lanche.applications.LancheApplication;
import br.com.lanche.facades.LancheFacade;
import br.com.lanche.interfaces.LancheRepository;
import br.com.lanche.models.Lanche;
import br.com.lanche.repositories.LancheRepositoryFirebase;
import br.com.lanche.repositories.LancheRepositoryImpl;
import br.com.lanche.services.LancheService;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static LancheRepository lancheRepositoryImpl;
    private static LancheService lancheService;
    private static LancheApplication lancheApplication;
    private static LancheFacade lancheFacade;
    private static Scanner scanner;

    public static void injetarDependencias() {
        lancheRepositoryImpl = new LancheRepositoryFirebase();
        lancheService = new LancheService();
        lancheApplication = new LancheApplication(lancheRepositoryImpl, lancheService);
        lancheFacade = new LancheFacade(lancheApplication);
        scanner = new Scanner(System.in);
    }

    public static void exibirMenu() {
        System.out.println("1 - Listar Produtos");
        System.out.println("2 - Cadastrar Produto");
        System.out.println("3 - Editar Produto");
        System.out.println("4 - Excluir Produto");
        System.out.println("5 - Vender");
        System.out.println("0 - Sair do sistema");
    }


    public static int solicitaOpcaoMenu() {
        System.out.println("Informe a opção escoliha: ");
        return scanner.nextInt();
    }

    public static void listarLanches() throws IOException {
        System.out.println("Lista de Produtos:\n(ID -- Nome -- Preço)\n");
        lancheFacade.buscarTodos().forEach(l -> {
            System.out.println(l);
        });
    }

    public static void cadastrarLanche() throws IOException {
        System.out.println("ID do produto: ");
        int id = scanner.nextInt();

        System.out.println("Nome do produto: ");
        scanner.nextLine();
        String nome = scanner.nextLine();

        System.out.println("Valor do produto: ");
        double preco = scanner.nextFloat();

        String caminhoImagem;

        do {
            System.out.print("Digite o caminho completo da imagem (ex: C:\\pasta\\hamburguer.jpg): ");
            scanner.nextLine();
            caminhoImagem = scanner.nextLine();

            if (!new File(caminhoImagem).exists()) {
                System.out.println("Arquivo não encontrado! Digite novamente.");
            }
        } while (!new File(caminhoImagem).exists());

        Lanche lanche = new Lanche(id, nome, preco, null);

        // Salvar imagem com ID
        lancheService.salvarImagem(lanche, caminhoImagem);

        lancheFacade.adicionar(lanche);
    }


    public static void atualizarLanche() throws IOException {
        System.out.println("ID do produto que será editado: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Novo Nome do produto: ");
        String nome = scanner.nextLine();

        System.out.println("Novo Preco do produto: ");
        double preco = scanner.nextDouble();
        scanner.nextLine();

        System.out.println("Novo Caminho Imagem: ");
        String caminhoImagem = scanner.nextLine();

        Lanche lanche = new Lanche(0, nome, preco, caminhoImagem);

        lancheFacade.atualizar(id, lanche);
    }

    public static void excluirLanche() throws IOException {
        System.out.println("ID do produto que será excluído: ");
        int id = scanner.nextInt();

        lancheFacade.excluir(id);
    }

    public static void venderLanche() throws IOException {
        System.out.println("ID do produto que será vendido: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Quantidade que será vendida: ");
        int quantidade = scanner.nextInt();
        scanner.nextLine();

        Lanche lanche = lancheFacade.buscarPorId(id);

        double total = lancheFacade.calcularTotal(lanche, quantidade);
        System.out.println("Total do lanche: " + total);
    }

    public static void excluirLanche() throws IOException {
        System.out.print("Digite o ID do produto a excluir: ");
        int id = scanner.nextInt();

        Lanche lanche = lancheFacade.buscarPorId(id);
        if (lanche != null) {
            // Exclui imagem
            lancheService.excluirImagem(lanche);

            // Exclui do repositório
            lancheFacade.remover(id);

            System.out.println("Lanche excluído com sucesso!");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    public static void editarLanche() throws IOException {
        System.out.print("Digite o ID do produto a editar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Lanche lanche = lancheFacade.buscarPorId(id);
        if (lanche == null) {
            System.out.println("Produto não encontrado!");
            return;
        }

        System.out.println("Novo nome (atual: " + lanche.getNome() + "): ");
        String nome = scanner.nextLine();
        if (!nome.isEmpty()) lanche.setNome(nome);

        System.out.println("Novo preço (atual: " + lanche.getPreco() + "): ");
        double preco = scanner.nextDouble();
        lanche.setPreco(preco);

        scanner.nextLine();
        System.out.print("Deseja alterar a imagem? (s/n): ");
        String alterar = scanner.nextLine();

        if (alterar.equalsIgnoreCase("s")) {
            // Exclui imagem antiga
            lancheService.excluirImagem(lanche);

            // Solicita nova imagem
            String caminhoImagem;
            do {
                System.out.print("Digite o caminho completo da nova imagem: ");
                caminhoImagem = scanner.nextLine();

                if (!new File(caminhoImagem).exists()) {
                    System.out.println("Arquivo não encontrado! Digite novamente.");
                }
            } while (!new File(caminhoImagem).exists());

            // Salva a nova
            lancheService.salvarImagem(lanche, caminhoImagem);
        }

        lancheFacade.atualizar(lanche);
        System.out.println("Produto atualizado com sucesso!");
    }

    public static void iniciarSistema() throws IOException {
        int opcaoMenu = -1;

        do {
            exibirMenu();

            opcaoMenu = solicitaOpcaoMenu();

            switch (opcaoMenu) {
                case 1:
                    listarLanches();
                    break;
                case 2:
                    cadastrarLanche();
                    break;
                case 3:
                    editarLanche();
                    break;
                case 4:
                    excluirLanche();
                    break;
                case 5:
                    venderLanche();
                    break;
                default:
                    break;
            }
        } while (opcaoMenu != 0);
    }

    public static void main(String[] args) throws IOException {
        injetarDependencias();
        iniciarSistema();
    }
}