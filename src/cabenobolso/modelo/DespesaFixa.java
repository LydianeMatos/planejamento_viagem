package cabenobolso.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DespesaFixa extends Despesa {
    private static final long serialVersionUID = 1L;

    public DespesaFixa(String descricao, double valor, List<Participante> participantes) {
        super(descricao, valor, participantes);
    }

    @Override
    public Map<Participante, Double> calcularRateio() {
        Map<Participante, Double> rateio = new HashMap<>();
        
        // Cria uma cópia modificável da lista para poder ordenar por código sem erro
        List<Participante> lista = new ArrayList<>(getParticipantes());
        
        // Ordena por código em ordem crescente para distribuição dos centavos
        Collections.sort(lista);

        long valorTotalCentavos = Math.round(getValor() * 100);
        int qtd = lista.size();

        long valorBaseCentavos = valorTotalCentavos / qtd;
        long restoCentavos = valorTotalCentavos % qtd;

        for (int i = 0; i < qtd; i++) {
            long valorParticipante = valorBaseCentavos + (i < restoCentavos ? 1 : 0);
            rateio.put(lista.get(i), valorParticipante / 100.0);
        }

        return rateio;
    }
}