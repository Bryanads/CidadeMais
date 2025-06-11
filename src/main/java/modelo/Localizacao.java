package modelo;

public class Localizacao {
    private int id;
    private String uf;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private double latitude;
    private double longitude;


    public Localizacao(int id, String uf, String cidade, String bairro, String rua, String numero, double latitude, double longitude) {
        this.id = id;
        this.uf = uf;
        this.cidade = cidade;
        this.bairro = bairro;
        this.rua = rua;
        this.numero = numero;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return uf + ", " + cidade + " - " + bairro + ", " + rua + ", " + numero +
                " - Latitude: " + latitude + ", Longitude: " + longitude;
    }

    public int getId() {
        return id;
    }

    public String getUf() {
        return uf;
    }

    public String getCidade() {
        return cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public String getRua() {
        return rua;
    }

    public String getNumero() {
        return numero;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
