package modelo;

import java.util.*;

public class Cidadao extends Usuario {
    private List<Problema> problemasReportados;
    private List<Problema> problemasContribuidos;
    private Map<Integer, Problema> problemasReportadosPorId;

    public Cidadao(int id, String nome) {
        super(id, nome);
        this.problemasReportados = new ArrayList<>();
        this.problemasContribuidos = new ArrayList<>();
        this.problemasReportadosPorId = new HashMap<>();
    }

    public List<Problema> getProblemasReportados() {
        return problemasReportados;
    }

    public void adicionarProblemaReportado(Problema problema) {
        if (problema != null && !problemasReportados.contains(problema)) {
            problemasReportados.add(problema);
            problemasReportadosPorId.put(problema.getIdProblema(), problema); // Adiciona ao Map
        }
    }

    public boolean removerProblemaReportado(int id) {
        Problema problema = problemasReportadosPorId.remove(id);
        if (problema != null) {
            problemasReportados.remove(problema);
            return true;
        }
        return false;
    }

    public List<Problema> getProblemasContribuidos() {
        return problemasContribuidos;
    }

    public void adicionarProblemaContribuido(Problema problema) {
        if (problema != null && !problemasContribuidos.contains(problema)) {
            problemasContribuidos.add(problema);
        }
    }

    @Override
    public String getTipoUsuario() {
        return "Cidadão";
    }

    @Override
    public void exibirResumo() {
        System.out.println("Cidadão: " + getNome() + " (ID: " + getId() + ")");
        System.out.println("Problemas reportados: " + problemasReportados.size());
        System.out.println("Problemas contribuídos: " + problemasContribuidos.size());
    }



}