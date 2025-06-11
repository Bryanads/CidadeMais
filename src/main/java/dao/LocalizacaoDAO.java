// dao/LocalizacaoDAO.java
package dao;

import modelo.Localizacao;

import java.sql.*;
import java.util.ArrayList;

public class LocalizacaoDAO implements BaseDAO {

    private Connection connection;

    public LocalizacaoDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Localizacao)) {
            System.out.println("Objeto não é uma Localização válida.");
            return;
        }
        Localizacao localizacao = (Localizacao) objeto;
        // A tabela localizacoes tem id_problema como UNIQUE.
        // O id da localização pode ser AUTO_INCREMENT, então não passamos ele no INSERT se for 0.
        String sql = "INSERT INTO localizacoes (uf, cidade, bairro, rua, numero, latitude, longitude, id_problema) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, localizacao.getUf());
            preparedStatement.setString(2, localizacao.getCidade());
            preparedStatement.setString(3, localizacao.getBairro());
            preparedStatement.setString(4, localizacao.getRua());
            preparedStatement.setString(5, localizacao.getNumero());
            preparedStatement.setDouble(6, localizacao.getLatitude());
            preparedStatement.setDouble(7, localizacao.getLongitude());
            // No seu schema, id_problema está na tabela de localizações.
            // Para poder salvar uma localização, você precisa associá-la a um problema existente.
            // No seu main, você cria a Localizacao antes do Problema. Isso é um problema.
            // A Localização deve ser criada *com* ou *depois* do Problema para obter o id_problema.
            // Por enquanto, vamos assumir que você passará o id do problema para a localização.
            // Ou, como no seu `Main.java`, você pode criar a Localizacao e depois associá-la ao Problema,
            // e então salvar a Localizacao. Isso implica que o id_problema na tabela `localizacoes`
            // será o ID do problema ao qual ela pertence.
            // Se `id_problema` na tabela `localizacoes` é uma FK e UNIQUE, então uma localização
            // pertence a um e apenas um problema.
            // A forma como você constrói Localizacao (sem idProblema) sugere que ela é um objeto independente.
            // Porém, o schema exige `id_problema` na `localizacoes`.
            // Para o exemplo, vamos assumir que o `id` da Localizacao *será* o `id_problema` do Problema associado,
            // ou você precisará adicionar um campo `idProblema` na classe `Localizacao` e no construtor.
            // Dada a sua `Main.java`, onde `locBuraco` é criado *antes* de `problema1` ter um ID,
            // vamos precisar de uma forma de vincular isso.

            // Por enquanto, vamos salvar a localização *sem* o id_problema e exigir que o ProblemaDAO
            // atualize o id_problema na tabela localizacoes *após* o problema ser salvo.
            // OU: A localização deve ter o id do problema.
            // No seu schema: `id_problema` INT NOT NULL UNIQUE em `localizacoes`.
            // Isso significa que o ID da localização é o mesmo ID do problema ao qual ela está associada.
            // Portanto, `localizacao.getId()` deve ser o `idProblema`.
            preparedStatement.setInt(8, localizacao.getId()); // O id da localização será o id do problema
            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    localizacao.setId(rs.getInt(1)); // Se o id da localização for auto_increment
                }
            }
            System.out.println("Localização salva com sucesso para o problema ID: " + localizacao.getId());
        } catch (SQLException e) {
            System.err.println("Erro ao salvar localização: " + e.getMessage());
        }
    }


    @Override
    public Object buscarPorId(int id) {
        String sql = "SELECT id, uf, cidade, bairro, rua, numero, latitude, longitude, id_problema FROM localizacoes WHERE id_problema = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id); // Buscar pelo id_problema
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int locId = resultSet.getInt("id"); // O ID da localização em si
                    String uf = resultSet.getString("uf");
                    String cidade = resultSet.getString("cidade");
                    String bairro = resultSet.getString("bairro");
                    String rua = resultSet.getString("rua");
                    String numero = resultSet.getString("numero");
                    double latitude = resultSet.getDouble("latitude");
                    double longitude = resultSet.getDouble("longitude");
                    return new Localizacao(locId, uf, cidade, bairro, rua, numero, latitude, longitude);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar localização por ID do problema: " + e.getMessage());
        }
        return null;
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        ArrayList<Object> localizacoes = new ArrayList<>();
        String sql = "SELECT id, uf, cidade, bairro, rua, numero, latitude, longitude, id_problema FROM localizacoes";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String uf = resultSet.getString("uf");
                String cidade = resultSet.getString("cidade");
                String bairro = resultSet.getString("bairro");
                String rua = resultSet.getString("rua");
                String numero = resultSet.getString("numero");
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                localizacoes.add(new Localizacao(id, uf, cidade, bairro, rua, numero, latitude, longitude));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar localizações (Lazy Loading): " + e.getMessage());
        }
        return localizacoes;
    }

    @Override
    public ArrayList<Object> listarTodosEagerLoading() {
        // Para Localização, Eager Loading não faz muito sentido a menos que você queira buscar o Problema associado.
        // Como Localizacao tem um relacionamento 1:1 com Problema (id_problema UNIQUE),
        // uma consulta que traga a Localizacao já tem os "detalhes" dela.
        // Se quiséssemos o Problema completo, a query seria mais complexa.
        return listarTodosLazyLoading(); // Para este caso, são equivalentes.
    }

    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Localizacao)) {
            System.out.println("Objeto não é uma Localização válida.");
            return;
        }
        Localizacao localizacao = (Localizacao) objeto;
        String sql = "UPDATE localizacoes SET uf = ?, cidade = ?, bairro = ?, rua = ?, numero = ?, latitude = ?, longitude = ? WHERE id_problema = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, localizacao.getUf());
            preparedStatement.setString(2, localizacao.getCidade());
            preparedStatement.setString(3, localizacao.getBairro());
            preparedStatement.setString(4, localizacao.getRua());
            preparedStatement.setString(5, localizacao.getNumero());
            preparedStatement.setDouble(6, localizacao.getLatitude());
            preparedStatement.setDouble(7, localizacao.getLongitude());
            preparedStatement.setInt(8, localizacao.getId()); // Usa o ID da localização, que é o ID do problema
            preparedStatement.executeUpdate();
            System.out.println("Localização atualizada com sucesso para o problema ID: " + localizacao.getId());
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar localização: " + e.getMessage());
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM localizacoes WHERE id_problema = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id); // Excluir pela FK id_problema
            preparedStatement.executeUpdate();
            System.out.println("Localização excluída com sucesso (ID do Problema: " + id + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao excluir localização: " + e.getMessage());
        }
    }
}