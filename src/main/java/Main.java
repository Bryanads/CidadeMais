// Main.java
import bd.ConnectionFactory;
import dao.*;

import modelo.Cidadao;
import modelo.Localizacao;
import modelo.Problema;
import modelo.Comentario;

import java.sql.Connection;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        ConnectionFactory fabrica = new ConnectionFactory();
        Connection connection = fabrica.recuperaConexao();


        // Inicializar DAOs
        UsuarioDAO usuarioDAO = new UsuarioDAO(connection);
        LocalizacaoDAO localizacaoDAO = new LocalizacaoDAO(connection);
        ProblemaDAO problemaDAO = new ProblemaDAO(connection);
        ComentarioDAO comentarioDAO = new ComentarioDAO(connection);
        ContribuicaoDAO contribuicaoDAO = new ContribuicaoDAO(connection);

        System.out.println("-------------------------------------------------------------");


        // 1. Criar e salvar usuários (no caso, cidadãos)
        Cidadao joao = new Cidadao(1, "João Silva");
        Cidadao maria = new Cidadao(2, "Maria Oliveira");
        Cidadao pedro = new Cidadao(3, "Pedro Souza");

        usuarioDAO.salvar(joao);
        usuarioDAO.salvar(maria);
        usuarioDAO.salvar(pedro);
        System.out.println("-------------------------------------------------------------");

        // 2. Criar e salvar problemas (Localização será salva dentro do ProblemaDAO)
        // Note que o ID da Localizacao deve corresponder ao ID do Problema no seu schema
        Localizacao locBuraco = new Localizacao(1, "SP", "São Paulo", "Centro", "Rua das Flores", "123", -23.550520, -46.633308);
        Problema problema1 = new Problema(1, "Buraco Perigoso na Rua das Flores",
                "Há um buraco enorme próximo ao número 123, causando risco a carros e pedestres.",
                locBuraco, "Buraco", "Aberto", joao);
        problemaDAO.salvar(problema1); // Salva o problema e sua localização associada

        System.out.println("-------------------------------------------------------------");

        Localizacao locPoste = new Localizacao(2, "RJ", "Rio de Janeiro", "Bairro Alegre", "Avenida do Sol", "45", -22.906847, -43.172896);
        Problema problema2 = new Problema(2, "Poste Sem Luz na Avenida do Sol",
                "O poste em frente ao número 45 está com a luz queimada há 3 dias, deixando a rua escura e perigosa.",
                locPoste, "Poste sem luz", "Aberto", maria);
        problemaDAO.salvar(problema2); // Salva o problema e sua localização associada

        System.out.println("-------------------------------------------------------------");

        // 3. Contribuições (usando os IDs de Cidadão e Problema)
        problema1.adicionarContribuinte(joao);
        problema1.adicionarContribuinte(maria);
        problema1.adicionarContribuinte(pedro);
        problema2.adicionarContribuinte(joao);


        contribuicaoDAO.salvar(new int[]{joao.getId(), problema1.getIdProblema()});
        contribuicaoDAO.salvar(new int[]{maria.getId(), problema1.getIdProblema()});
        contribuicaoDAO.salvar(new int[]{pedro.getId(), problema1.getIdProblema()});
        contribuicaoDAO.salvar(new int[]{joao.getId(), problema2.getIdProblema()});

        System.out.println("-------------------------------------------------------------");

        // 4. Comentários (associando ao problema por ID)
        Comentario comentario1_1 = new Comentario(1, "Concordo, quase caí de bicicleta aqui!", pedro);
        comentarioDAO.salvar(comentario1_1, problema1.getIdProblema());

        Comentario comentario1_2 = new Comentario(2, "A prefeitura precisa agir rápido!", maria);
        comentarioDAO.salvar(comentario1_2, problema1.getIdProblema());

        Comentario comentario2_1 = new Comentario(3, "Realmente, está muito escuro à noite.", joao);
        comentarioDAO.salvar(comentario2_1, problema2.getIdProblema());

        System.out.println("-------------------------------------------------------------");


        System.out.println("##############################################################################");



        System.out.println("\n--- Listando todos os problemas com Eager Loading ---");
        ArrayList<Object> problemas = problemaDAO.listarTodosEagerLoading();
        for (Object obj : problemas) {
            Problema p = (Problema) obj;
            p.exibirDetalhes();
            System.out.println("\n---------------------------------------------\n");
        }

        System.out.println("\n--- Testando busca de um problema por ID ---");
        Problema problemaBuscado = (Problema) problemaDAO.buscarPorId(problema1.getIdProblema());
        if (problemaBuscado != null) {
            System.out.println("Problema buscado por ID:");
            problemaBuscado.exibirDetalhes();
        } else {
            System.out.println("Problema não encontrado.");
        }

        System.out.println("\n--- Testando atualização de um problema ---");
        problema1.setStatus("Resolvido");
        problema1.setDescricao("O buraco foi tapado e a rua está segura agora.");
        problemaDAO.atualizar(problema1);

        System.out.println("\n--- Listando problemas após atualização ---");
        problemas = problemaDAO.listarTodosEagerLoading();
        for (Object obj : problemas) {
            Problema p = (Problema) obj;
            p.exibirDetalhes();
            System.out.println("\n---------------------------------------------\n");
        }

        System.out.println("\n--- Testando exclusão de um problema (e seus comentários, localizações e contribuições associadas) ---");
        problemaDAO.excluir(problema2.getIdProblema());

        System.out.println("\n--- Listando problemas após exclusão ---");
        problemas = problemaDAO.listarTodosEagerLoading();
        if (problemas.isEmpty()) {
            System.out.println("Nenhum problema restante.");
        } else {
            for (Object obj : problemas) {
                Problema p = (Problema) obj;
                p.exibirDetalhes();
                System.out.println("\n---------------------------------------------\n");
            }
        }

    }
}
