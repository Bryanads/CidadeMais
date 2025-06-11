// dao/ComentarioDAO.java
package dao;

import modelo.Comentario;
import modelo.Cidadao; // Para o autor do comentário

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ComentarioDAO implements BaseDAO {

    private Connection connection;

    public ComentarioDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Comentario)) {
            System.out.println("Objeto não é um Comentário válido.");
            return;
        }
        System.err.println("Método salvar(Object) em ComentarioDAO requer o ID do problema. Use salvar(Comentario, int idProblema).");
    }

    public void salvar(Comentario comentario, int idProblema) {
        String sql = "INSERT INTO comentarios (texto, data_hora, id_usuario_autor, id_problema) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, comentario.getTexto());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(comentario.getDataHora()));
            preparedStatement.setInt(3, comentario.getAutor().getId());
            preparedStatement.setInt(4, idProblema);
            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    comentario.setId(rs.getInt(1));
                }
            }
            System.out.println("Comentário salvo com sucesso por " + comentario.getAutor().getNome() + " para o problema ID: " + idProblema);
        } catch (SQLException e) {
            System.err.println("Erro ao salvar comentário: " + e.getMessage());
        }
    }

    @Override
    public Object buscarPorId(int id) {
        String sql = "SELECT com.id, com.texto, com.data_hora, " +
                "u.id AS autor_id, u.nome AS autor_nome, com.id_problema " +
                "FROM comentarios com JOIN usuarios u ON com.id_usuario_autor = u.id WHERE com.id = ?";
        Comentario comentario = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int comentarioId = resultSet.getInt("id");
                    String texto = resultSet.getString("texto");
                    LocalDateTime dataHora = resultSet.getTimestamp("data_hora").toLocalDateTime();
                    int autorId = resultSet.getInt("autor_id");
                    String autorNome = resultSet.getString("autor_nome");

                    Cidadao autor = new Cidadao(autorId, autorNome);
                    comentario = new Comentario(comentarioId, texto, autor);
                    comentario.setDataHora(dataHora);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar comentário por ID: " + e.getMessage());
        }
        return comentario;
    }

    public ArrayList<Comentario> buscarPorIdProblema(int idProblema) {
        ArrayList<Comentario> comentarios = new ArrayList<>();
        String sql = "SELECT com.id, com.texto, com.data_hora, " +
                "u.id AS autor_id, u.nome AS autor_nome " +
                "FROM comentarios com JOIN usuarios u ON com.id_usuario_autor = u.id WHERE com.id_problema = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idProblema);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int comentarioId = resultSet.getInt("id");
                    String texto = resultSet.getString("texto");
                    LocalDateTime dataHora = resultSet.getTimestamp("data_hora").toLocalDateTime();
                    int autorId = resultSet.getInt("autor_id");
                    String autorNome = resultSet.getString("autor_nome");

                    Cidadao autor = new Cidadao(autorId, autorNome);
                    Comentario comentario = new Comentario(comentarioId, texto, autor);
                    comentario.setDataHora(dataHora);
                    comentarios.add(comentario);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar comentários por ID do problema: " + e.getMessage());
        }
        return comentarios;
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        ArrayList<Object> comentarios = new ArrayList<>();
        String sql = "SELECT com.id, com.texto, com.data_hora, " +
                "u.id AS autor_id, u.nome AS autor_nome " +
                "FROM comentarios com JOIN usuarios u ON com.id_usuario_autor = u.id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int comentarioId = resultSet.getInt("id");
                String texto = resultSet.getString("texto");
                LocalDateTime dataHora = resultSet.getTimestamp("data_hora").toLocalDateTime();
                int autorId = resultSet.getInt("autor_id");
                String autorNome = resultSet.getString("autor_nome");

                Cidadao autor = new Cidadao(autorId, autorNome);
                Comentario comentario = new Comentario(comentarioId, texto, autor);
                comentario.setDataHora(dataHora);
                comentarios.add(comentario);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar comentários (Lazy Loading): " + e.getMessage());
        }
        return comentarios;
    }

    @Override
    public ArrayList<Object> listarTodosEagerLoading() {
        return listarTodosLazyLoading();
    }

    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Comentario)) {
            System.out.println("Objeto não é um Comentário válido.");
            return;
        }
        Comentario comentario = (Comentario) objeto;
        String sql = "UPDATE comentarios SET texto = ?, data_hora = ?, id_usuario_autor = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, comentario.getTexto());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(comentario.getDataHora()));
            preparedStatement.setInt(3, comentario.getAutor().getId());
            preparedStatement.setInt(4, comentario.getId());
            preparedStatement.executeUpdate();
            System.out.println("Comentário atualizado com sucesso (ID: " + comentario.getId() + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar comentário: " + e.getMessage());
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM comentarios WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Comentário excluído com sucesso (ID: " + id + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao excluir comentário: " + e.getMessage());
        }
    }
}