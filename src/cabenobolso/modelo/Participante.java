package cabenobolso.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Participante implements Serializable, Comparable<Participante> {
    private static final long serialVersionUID = 1L;

    private int codigo;
    private String nome;
    private double orcamento;

    public Participante(int codigo, String nome, double orcamento) {
        setCodigo(codigo);
        setNome(nome);
        setOrcamento(orcamento);
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        if (codigo <= 0) {
            throw new IllegalArgumentException("O código deve ser um número positivo.");
        }
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do participante não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public double getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(double orcamento) {
        if (orcamento < 0) {
            throw new IllegalArgumentException("O orçamento não pode ser negativo.");
        }
        this.orcamento = orcamento;
    }

    @Override
    public int compareTo(Participante outro) {
        return Integer.compare(this.codigo, outro.codigo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participante that = (Participante) o;
        return codigo == that.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return nome + " (Cód: " + codigo + ")";
    }
}