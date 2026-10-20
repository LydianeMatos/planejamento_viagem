package cabenobolso.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class Despesa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String descricao;
    private double valor;
    private List<Participante> participantes;

    public Despesa(String descricao, double valor, List<Participante> participantes) {
        setDescricao(descricao);
        setValor(valor);
        setParticipantes(participantes);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição da despesa não pode ser vazia.");
        }
        this.descricao = descricao.trim();
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor da despesa deve ser maior que zero.");
        }
        this.valor = valor;
    }

    public List<Participante> getParticipantes() {
        return Collections.unmodifiableList(participantes);
    }

    public void setParticipantes(List<Participante> participantes) {
        if (participantes == null || participantes.isEmpty()) {
            throw new IllegalArgumentException("A despesa precisa ter pelo menos um participante.");
        }
        // Evita duplicatas mantendo uma lista limpa
        List<Participante> listaUnica = new ArrayList<>();
        for (Participante p : participantes) {
            if (!listaUnica.contains(p)) {
                listaUnica.add(p);
            }
        }
        this.participantes = listaUnica;
    }

    // Método polimórfico obrigatório
    public abstract Map<Participante, Double> calcularRateio();
}