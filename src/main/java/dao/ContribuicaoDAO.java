// dao/ContribuicaoDAO.java
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ContribuicaoDAO implements BaseDAO {

    private Connection connection;

    public ContribuicaoDAO(Connection connection) {
        this.connection = connection;
    }

    // Este método é específico para salvar a relação N:N
    public void salvar(int[] ids) {
        if (ids == null || ids.length != 2) {
            System.out.println("IDs inválidos para a contribuição. Esperado [idUsuario, idProblema].");
            return;
        }
        int idUsuario = ids[0];
        int idProblema = ids[1];

        String sql = "INSERT INTO contribuicoes (id_usuario, id_problema, data_contribuicao) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idUsuario);
            preparedStatement.setInt(2, idProblema);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
            System.out.println("Contribuição salva com sucesso: Usuário ID " + idUsuario + " -> Problema ID " + idProblema);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.out.println("Usuário ID " + idUsuario + " já contribuiu para o Problema ID " + idProblema);
            } else {
                System.err.println("Erro ao salvar contribuição: " + e.getMessage());
            }
        }
    }

    @Override
    public void salvar(Object objeto) {
        System.out.println("Método salvar(Object) não é implementado para ContribuicaoDAO. Use salvar(int[] ids).");
    }

    @Override
    public Object buscarPorId(int id) {
        System.out.println("Buscar por ID único não é aplicável para ContribuicaoDAO. Use métodos específicos para a chave composta.");
        return null;
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        System.out.println("Listar todos (Lazy Loading) não é comum para ContribuicaoDAO isoladamente.");
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Object> listarTodosEagerLoading() {
        System.out.println("Listar todos (Eager Loading) não é comum para ContribuicaoDAO isoladamente.");
        return new ArrayList<>();
    }

    @Override
    public void atualizar(Object objeto) {
        System.out.println("Atualizar não é comum para ContribuicaoDAO. Exclua e reinsira se necessário.");
    }

    // Excluir uma contribuição específica
    public void excluir(int idUsuario, int idProblema) {
        String sql = "DELETE FROM contribuicoes WHERE id_usuario = ? AND id_problema = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idUsuario);
            preparedStatement.setInt(2, idProblema);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Contribuição excluída com sucesso: Usuário ID " + idUsuario + ", Problema ID " + idProblema);
            } else {
                System.out.println("Nenhuma contribuição encontrada para exclusão: Usuário ID " + idUsuario + ", Problema ID " + idProblema);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir contribuição: " + e.getMessage());
        }
    }

    @Override
    public void excluir(int id) {
        System.out.println("Excluir por ID único não é aplicável para ContribuicaoDAO. Use excluir(int idUsuario, int idProblema).");
    }
}