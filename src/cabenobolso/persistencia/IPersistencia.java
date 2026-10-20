package cabenobolso.persistencia;

import cabenobolso.modelo.Planejamento;
import java.io.IOException;

public interface IPersistencia {
    void salvar(Planejamento planejamento, String caminhoArquivo) throws IOException;
    Planejamento carregar(String caminhoArquivo) throws IOException, ClassNotFoundException;
}