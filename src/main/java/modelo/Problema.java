package modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.*;

public class Problema implements Exibivel {
    private int idProblema;
    private String titulo;
    private String descricao;
    private Localizacao localizacao;
    private String classificacao;
    private String status;
    private LocalDateTime data;
    private Cidadao reportante;
    private List<Comentario> comentarios;
    private List<Cidadao> contribuintes;

    public Problema(int idProblema, String titulo, String descricao, Localizacao localizacao, String classificacao, String status, Cidadao reportante) {
        this.idProblema = idProblema;
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.classificacao = classificacao;
        this.status = status;
        this.data = LocalDateTime.now();
        this.comentarios = new ArrayList<>();
        this.contribuintes = new ArrayList<>();
        this.reportante = reportante;
        if (reportante != null) {
            reportante.adicionarProblemaReportado(this);
        }
    }

    public int getIdProblema() {
        return idProblema;
    }


    public void adicionarContribuinte(Cidadao cidadao) {
        if (cidadao != null && !contribuintes.contains(cidadao)) {
            contribuintes.add(cidadao);
            cidadao.adicionarProblemaContribuido(this);
            System.out.println(cidadao.getNome() + " contribuiu para o problema '" + this.titulo + "'.");
        }
    }

    public void adicionarComentario(Comentario comentario) {
        if (comentario != null) {
            this.comentarios.add(comentario);
            System.out.println((comentario.getAutor()).getNome() + " comentou : " + this.titulo + "'.");
        }
    }

    @Override
    public void exibirDetalhes() {
        System.out.println("ID: " + idProblema);
        System.out.println("Título: " + titulo);
        System.out.println("Descrição: " + descricao);
        System.out.println("Localização: " + localizacao.toString());
        System.out.println("Classificação: " + classificacao);
        System.out.println("Status: " + status);
        System.out.println("Reportado por: " + reportante.getNome() + " (ID: " + reportante.getId() + ")");
        System.out.println("Registrado em: " + data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        System.out.println("Contribuições: " + contribuintes.size());
        System.out.print("Contribuintes: ");
        if (contribuintes.isEmpty()) {
            System.out.println("Nenhum");
        } else {
            for (int i = 0; i < contribuintes.size(); i++) {
                Cidadao c = contribuintes.get(i);
                System.out.print(c.getNome() + " (" + c.getId() + ")");
                if (i < contribuintes.size() - 1) System.out.print(", ");
            }
            System.out.println();
        }
        System.out.println("Comentários (" + comentarios.size() + "):");
        if (comentarios.isEmpty()) {
            System.out.println("  Nenhum comentário ainda.");
        } else {
            for (Comentario c : comentarios) {
                c.exibirDetalhes();
            }
        }
    }


    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public String getClassificacao() {
        return classificacao;
    }

    public LocalDateTime getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public Cidadao getReportante() {
        return reportante;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public List<Cidadao> getContribuintes() {
        return contribuintes;
    }

    public void setIdProblema(int idProblema) {
        this.idProblema = idProblema;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public void setClassificacao(String classificacao) {
        this.classificacao = classificacao;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public void setReportante(Cidadao reportante) {
        this.reportante = reportante;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public void setContribuintes(List<Cidadao> contribuintes) {
        this.contribuintes = contribuintes;
    }
}