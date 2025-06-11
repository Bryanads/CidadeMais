// dao/ProblemaDAO.java
package dao;

import modelo.Cidadao;
import modelo.Localizacao;
import modelo.Problema;
import modelo.Comentario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ProblemaDAO implements BaseDAO {

    private Connection connection;

    public ProblemaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void salvar(Object objeto) {
        if (!(objeto instanceof Problema)) {
            System.out.println("Objeto não é um Problema válido.");
            return;
        }
        Problema problema = (Problema) objeto;
        String sql = "INSERT INTO problemas (id, titulo, descricao, classificacao, status, data_reportado, id_usuario_reportante) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, problema.getIdProblema());
            preparedStatement.setString(2, problema.getTitulo());
            preparedStatement.setString(3, problema.getDescricao());
            preparedStatement.setString(4, problema.getClassificacao());
            preparedStatement.setString(5, problema.getStatus());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(problema.getData()));
            preparedStatement.setInt(7, problema.getReportante().getId());
            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    problema.setIdProblema(rs.getInt(1));
                }
            }
            System.out.println("Problema salvo com sucesso: " + problema.getTitulo());

            // Salvar a localização do problema (assume que LocalizacaoDAO cuidará disso)
            LocalizacaoDAO localizacaoDAO = new LocalizacaoDAO(connection);
            problema.getLocalizacao().setId(problema.getIdProblema()); // Vincular ID do problema ao ID da localização
            localizacaoDAO.salvar(problema.getLocalizacao());

        } catch (SQLException e) {
            System.err.println("Erro ao salvar problema: " + e.getMessage());
        }
    }


    @Override
    public Object buscarPorId(int id) {
        String sql = "SELECT p.id, p.titulo, p.descricao, p.classificacao, p.status, p.data_reportado, " +
                "u.id AS reportante_id, u.nome AS reportante_nome " +
                "FROM problemas p JOIN usuarios u ON p.id_usuario_reportante = u.id " +
                "WHERE p.id = ?";
        Problema problema = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int problemaId = resultSet.getInt("id");
                    String titulo = resultSet.getString("titulo");
                    String descricao = resultSet.getString("descricao");
                    String classificacao = resultSet.getString("classificacao");
                    String status = resultSet.getString("status");
                    LocalDateTime data = resultSet.getTimestamp("data_reportado").toLocalDateTime();
                    int reportanteId = resultSet.getInt("reportante_id");
                    String reportanteNome = resultSet.getString("reportante_nome");

                    Cidadao reportante = new Cidadao(reportanteId, reportanteNome);

                    // Buscar localização (Lazy Loading)
                    LocalizacaoDAO localizacaoDAO = new LocalizacaoDAO(connection);
                    Localizacao localizacao = (Localizacao) localizacaoDAO.buscarPorId(problemaId); // Busca pela FK id_problema

                    problema = new Problema(problemaId, titulo, descricao, localizacao, classificacao, status, reportante);
                    problema.setData(data); // Sobrescreve a data gerada no construtor
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar problema por ID: " + e.getMessage());
        }
        return problema;
    }

    @Override
    public ArrayList<Object> listarTodosLazyLoading() {
        ArrayList<Object> problemas = new ArrayList<>();
        String sql = "SELECT p.id, p.titulo, p.descricao, p.classificacao, p.status, p.data_reportado, " +
                "u.id AS reportante_id, u.nome AS reportante_nome " +
                "FROM problemas p JOIN usuarios u ON p.id_usuario_reportante = u.id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int problemaId = resultSet.getInt("id");
                String titulo = resultSet.getString("titulo");
                String descricao = resultSet.getString("descricao");
                String classificacao = resultSet.getString("classificacao");
                String status = resultSet.getString("status");
                LocalDateTime data = resultSet.getTimestamp("data_reportado").toLocalDateTime();
                int reportanteId = resultSet.getInt("reportante_id");
                String reportanteNome = resultSet.getString("reportante_nome");

                Cidadao reportante = new Cidadao(reportanteId, reportanteNome);
                // Localização e comentários serão carregados sob demanda
                problemas.add(new Problema(problemaId, titulo, descricao, null, classificacao, status, reportante));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar problemas (Lazy Loading): " + e.getMessage());
        }
        return problemas;
    }

    @Override
    public ArrayList<Object> listarTodosEagerLoading() {
        ArrayList<Object> problemas = new ArrayList<>();
        String sql = "SELECT p.id AS problema_id, p.titulo, p.descricao, p.classificacao, p.status, p.data_reportado, " +
                "ur.id AS reportante_id, ur.nome AS reportante_nome, " +
                "l.id AS localizacao_id, l.uf, l.cidade, l.bairro, l.rua, l.numero, l.latitude, l.longitude, " +
                "com.id AS comentario_id, com.texto AS comentario_texto, com.data_hora AS comentario_data_hora, " +
                "ua.id AS autor_comentario_id, ua.nome AS autor_comentario_nome, " +
                "con.id_usuario AS contribuinte_id, uc.nome AS contribuinte_nome " +
                "FROM problemas p " +
                "JOIN usuarios ur ON p.id_usuario_reportante = ur.id " +
                "LEFT JOIN localizacoes l ON p.id = l.id_problema " +
                "LEFT JOIN comentarios com ON p.id = com.id_problema " +
                "LEFT JOIN usuarios ua ON com.id_usuario_autor = ua.id " +
                "LEFT JOIN contribuicoes con ON p.id = con.id_problema " +
                "LEFT JOIN usuarios uc ON con.id_usuario = uc.id " +
                "ORDER BY p.id, com.id, con.id_usuario";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            Problema currentProblema = null;

            while (resultSet.next()) {
                int problemaId = resultSet.getInt("problema_id");

                if (currentProblema == null || currentProblema.getIdProblema() != problemaId) {
                    String titulo = resultSet.getString("titulo");
                    String descricao = resultSet.getString("descricao");
                    String classificacao = resultSet.getString("classificacao");
                    String status = resultSet.getString("status");
                    LocalDateTime data = resultSet.getTimestamp("data_reportado").toLocalDateTime();

                    int reportanteId = resultSet.getInt("reportante_id");
                    String reportanteNome = resultSet.getString("reportante_nome");
                    Cidadao reportante = new Cidadao(reportanteId, reportanteNome);

                    int localizacaoId = resultSet.getInt("localizacao_id");
                    Localizacao localizacao = null;
                    if (localizacaoId != 0) {
                        String uf = resultSet.getString("uf");
                        String cidade = resultSet.getString("cidade");
                        String bairro = resultSet.getString("bairro");
                        String rua = resultSet.getString("rua");
                        String numero = resultSet.getString("numero");
                        double latitude = resultSet.getDouble("latitude");
                        double longitude = resultSet.getDouble("longitude");
                        localizacao = new Localizacao(localizacaoId, uf, cidade, bairro, rua, numero, latitude, longitude);
                    }

                    currentProblema = new Problema(problemaId, titulo, descricao, localizacao, classificacao, status, reportante);
                    currentProblema.setData(data); // Garante que a data do banco é usada
                    problemas.add(currentProblema);
                }

                // Adicionar comentários
                int comentarioId = resultSet.getInt("comentario_id");
                if (comentarioId != 0) {
                    boolean comentarioJaAdicionado = false;
                    for (Comentario c : currentProblema.getComentarios()) {
                        if (c.getId() == comentarioId) {
                            comentarioJaAdicionado = true;
                            break;
                        }
                    }
                    if (!comentarioJaAdicionado) {
                        String comentarioTexto = resultSet.getString("comentario_texto");
                        LocalDateTime comentarioDataHora = resultSet.getTimestamp("comentario_data_hora").toLocalDateTime();
                        int autorComentarioId = resultSet.getInt("autor_comentario_id");
                        String autorComentarioNome = resultSet.getString("autor_comentario_nome");
                        Cidadao autorComentario = new Cidadao(autorComentarioId, autorComentarioNome);
                        Comentario comentario = new Comentario(comentarioId, comentarioTexto, autorComentario);
                        comentario.setDataHora(comentarioDataHora);
                        currentProblema.adicionarComentario(comentario);
                    }
                }

                // Adicionar contribuintes
                int contribuinteId = resultSet.getInt("contribuinte_id");
                if (contribuinteId != 0) {
                    boolean contribuinteJaAdicionado = false;
                    for (Cidadao c : currentProblema.getContribuintes()) {
                        if (c.getId() == contribuinteId) {
                            contribuinteJaAdicionado = true;
                            break;
                        }
                    }
                    if (!contribuinteJaAdicionado) {
                        String contribuinteNome = resultSet.getString("contribuinte_nome");
                        Cidadao contribuinte = new Cidadao(contribuinteId, contribuinteNome);
                        currentProblema.adicionarContribuinte(contribuinte);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar problemas (Eager Loading): " + e.getMessage());
        }
        return problemas;
    }


    @Override
    public void atualizar(Object objeto) {
        if (!(objeto instanceof Problema)) {
            System.out.println("Objeto não é um Problema válido.");
            return;
        }
        Problema problema = (Problema) objeto;
        String sql = "UPDATE problemas SET titulo = ?, descricao = ?, classificacao = ?, status = ?, data_reportado = ?, id_usuario_reportante = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, problema.getTitulo());
            preparedStatement.setString(2, problema.getDescricao());
            preparedStatement.setString(3, problema.getClassificacao());
            preparedStatement.setString(4, problema.getStatus());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(problema.getData()));
            preparedStatement.setInt(6, problema.getReportante().getId());
            preparedStatement.setInt(7, problema.getIdProblema());
            preparedStatement.executeUpdate();
            System.out.println("Problema atualizado com sucesso: " + problema.getTitulo());

            // Atualizar a localização
            LocalizacaoDAO localizacaoDAO = new LocalizacaoDAO(connection);
            localizacaoDAO.atualizar(problema.getLocalizacao());

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar problema: " + e.getMessage());
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM problemas WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Problema excluído com sucesso (ID: " + id + ")");
        } catch (SQLException e) {
            System.err.println("Erro ao excluir problema: " + e.getMessage());
        }
    }
}