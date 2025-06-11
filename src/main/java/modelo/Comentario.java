package modelo;

import java.time.LocalDateTime;

public class Comentario implements Exibivel {
    private int id;
    private Cidadao autor;
    private String texto;
    private LocalDateTime dataHora;

    public Comentario(int id, String texto, Cidadao autor) {
        this.id = id;
        this.texto = texto;
        this.dataHora = LocalDateTime.now();
        this.autor = autor;
    }

    public int getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Cidadao getAutor() {
        return autor;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setAutor(Cidadao autor) {
        this.autor = autor;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public void exibirDetalhes() {
        System.out.println( "ID: " + id + ", " + autor.getNome() + " (" + dataHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "): " + texto);
    }
}