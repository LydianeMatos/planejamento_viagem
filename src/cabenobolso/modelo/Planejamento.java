package cabenobolso.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Planejamento implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Participante> participantes = new ArrayList<>();
    private List<Despesa> despesas = new ArrayList<>();

    public void adicionarParticipante(Participante p) {
        for (Participante existente : participantes) {
            if (existente.getCodigo() == p.getCodigo()) {
                throw new IllegalArgumentException("Já existe um participante com o código " + p.getCodigo());
            }
        }
        participantes.add(p);
    }

    public void removerParticipante(Participante p) {
        for (Despesa d : despesas) {
            if (d.getParticipantes().contains(p)) {
                throw new IllegalStateException("Não é possível remover o participante enquanto houver despesas vinculadas a ele.");
            }
        }
        participantes.remove(p);
    }

    public void adicionarDespesa(Despesa d) {
        despesas.add(d);
    }

    public void removerDespesa(Despesa d) {
        despesas.remove(d);
    }

    public double calcularCustoTotal() {
        double total = 0.0;
        for (Despesa d : despesas) {
            if (d instanceof DespesaFixa) {
                total += d.getValor();
            } else if (d instanceof DespesaPorPessoa) {
                total += d.getValor() * d.getParticipantes().size();
            }
        }
        return total;
    }

    public Map<Participante, Double> calcularTotaisIndividuais() {
        Map<Participante, Double> totais = new HashMap<>();
        for (Participante p : participantes) {
            totais.put(p, 0.0);
        }

        for (Despesa d : despesas) {
            Map<Participante, Double> rateio = d.calcularRateio();
            for (Map.Entry<Participante, Double> entry : rateio.entrySet()) {
                Participante p = entry.getKey();
                double valor = entry.getValue();
                totais.put(p, totais.getOrDefault(p, 0.0) + valor);
            }
        }
        return totais;
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }

    public List<Despesa> getDespesas() {
        return despesas;
    }
}