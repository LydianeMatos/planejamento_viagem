package cabenobolso.persistencia;

import cabenobolso.modelo.Planejamento;
import java.io.*;

public class PersistenciaArquivo implements IPersistencia {

    @Override
    public void salvar(Planejamento planejamento, String caminhoArquivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoArquivo))) {
            oos.writeObject(planejamento);
        }
    }

    @Override
    public Planejamento carregar(String caminhoArquivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminhoArquivo))) {
            return (Planejamento) ois.readObject();
        }
    }
}