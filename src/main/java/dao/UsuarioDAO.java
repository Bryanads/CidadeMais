package dao;

import modelo.Cidadao;
import modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO implements BaseDAO {
    private Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Usuario)) {
            System.out.println("Objeto não é um Usuário válido.");
            return;
        }
        Usuario usuario = (Usuario) objeto;
        String sql = "INSERT INTO usuarios (id, nome, tipo) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, usuario.getId());
            preparedStatement.setString(2, usuario.getNome());
            preparedStatement.setString(3, usuario.getTipoUsuario());
            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
            System.out.println("Usuário salvo com sucesso: " + usuario.getNome());
        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    @Override
    public Object buscarPorId(int id) {
        String sql = "SELECT id, nome, tipo FROM usuarios WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String nome = resultSet.getString("nome");
                    String tipo = resultSet.getString("tipo");

                    // Como Usuario é abstrato, criamos uma instância de Cidadao
                    // No futuro, você pode adicionar outros tipos de usuário e fazer a lógica adequada
                    if (tipo.equals("Cidadão")) {
                        return new Cidadao(id, nome);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        ArrayList<Object> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, tipo FROM usuarios";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String tipo = resultSet.getString("tipo");

                if (tipo.equals("Cidadão")) {
                    usuarios.add(new Cidadao(id, nome));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários (Lazy Loading): " + e.getMessage());
        }
        return usuarios;
    }

    @Override
    public ArrayList<Object> listarTodosEagerLoading() {
        // Para simplificar, vamos usar o mesmo comportamento do Lazy Loading
        return listarTodosLazyLoading();
    }

    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Usuario)) {
            System.out.println("Objeto não é um Usuário válido.");
            return;
        }
        Usuario usuario = (Usuario) objeto;
        String sql = "UPDATE usuarios SET nome = ?, tipo = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, usuario.getNome());
            preparedStatement.setString(2, usuario.getTipoUsuario());
            preparedStatement.setInt(3, usuario.getId());
            preparedStatement.executeUpdate();
            System.out.println("Usuário atualizado com sucesso: " + usuario.getNome());
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Usuário excluído com sucesso (ID: " + id + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao excluir usuário: " + e.getMessage());
        }
    }
}