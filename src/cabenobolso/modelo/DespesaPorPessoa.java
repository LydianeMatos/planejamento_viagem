package cabenobolso.modelo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DespesaPorPessoa extends Despesa {
    private static final long serialVersionUID = 1L;

    public DespesaPorPessoa(String descricao, double valorUnitario, List<Participante> participantes) {
        super(descricao, valorUnitario, participantes);
    }

    @Override
    public Map<Participante, Double> calcularRateio() {
        Map<Participante, Double> rateio = new HashMap<>();
        for (Participante p : getParticipantes()) {
            rateio.put(p, getValor()); // Valor unitário para cada participante
        }
        return rateio;
    }
}