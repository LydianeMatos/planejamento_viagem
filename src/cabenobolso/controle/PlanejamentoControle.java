package cabenobolso.controle;

import cabenobolso.modelo.*;
import cabenobolso.persistencia.*;
import java.util.List;
import java.util.Map;

public class PlanejamentoControle {

    private Planejamento planejamento;
    private final IPersistencia persistencia;

    public PlanejamentoControle() {
        this.planejamento = new Planejamento();
        this.persistencia = new PersistenciaArquivo();
    }

    public Planejamento getPlanejamento() {
        return planejamento;
    }

    // --- Gestão de Participantes ---
    public void adicionarParticipante(int codigo, String nome, double orcamento) {
        Participante p = new Participante(codigo, nome, orcamento);
        planejamento.adicionarParticipante(p);
    }

    public void atualizarParticipante(Participante p, String novoNome, double novoOrcamento) {
        p.setNome(novoNome);
        p.setOrcamento(novoOrcamento);
    }

    public void removerParticipante(Participante p) {
        planejamento.removerParticipante(p);
    }

    public List<Participante> getParticipantes() {
        return planejamento.getParticipantes();
    }

    // --- Gestão de Despesas ---
    public void adicionarDespesaFixa(String descricao, double valor, List<Participante> participantes) {
        DespesaFixa df = new DespesaFixa(descricao, valor, participantes);
        planejamento.adicionarDespesa(df);
    }

    public void adicionarDespesaPorPessoa(String descricao, double valorUnitario, List<Participante> participantes) {
        DespesaPorPessoa dp = new DespesaPorPessoa(descricao, valorUnitario, participantes);
        planejamento.adicionarDespesa(dp);
    }

    public void removerDespesa(Despesa d) {
        planejamento.removerDespesa(d);
    }

    public List<Despesa> getDespesas() {
        return planejamento.getDespesas();
    }

    // --- Cálculos e Resumo ---
    public double getCustoTotal() {
        return planejamento.calcularCustoTotal();
    }

    public Map<Participante, Double> getTotaisIndividuais() {
        return planejamento.calcularTotaisIndividuais();
    }

    // --- Persistência de Dados ---
    public void salvar(String caminhoArquivo) throws Exception {
        persistencia.salvar(planejamento, caminhoArquivo);
    }

    public void carregar(String caminhoArquivo) throws Exception {
        this.planejamento = persistencia.carregar(caminhoArquivo);
    }

    public void novoPlanejamento() {
        this.planejamento = new Planejamento();
    }
}