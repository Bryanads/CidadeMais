package modelo;

public abstract class Usuario {
    private int id;
    private String nome;

    public Usuario(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setId(int id) {
        this.id = id;
    }



    // Método abstrato: cada tipo de usuário define seu tipo
    public abstract String getTipoUsuario();

    // Método abstrato: exibe um resumo do usuário
    public abstract void exibirResumo();

}