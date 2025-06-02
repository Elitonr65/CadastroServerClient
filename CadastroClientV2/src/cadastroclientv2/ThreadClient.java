package cadastroclientv2;

import java.io.EOFException;
import java.io.IOException;
import model.Produto;
import javax.swing.*;
import java.io.ObjectInputStream;
import java.util.List;

public class ThreadClient extends Thread {

    private final ObjectInputStream entrada;
    private final JTextArea textArea;

    public ThreadClient(ObjectInputStream entrada, JTextArea textArea) {
        this.entrada = entrada;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object obj = entrada.readObject();

                SwingUtilities.invokeLater(() -> {
                    if (obj instanceof String s) {
                        textArea.append(">> " + s + "\n");
                    } else if (obj instanceof List<?> lista) {
                        for (Object item : lista) {
                            if (item instanceof Produto p) {
                                textArea.append(p.getNome() + ": " + p.getQuantidade() + "\n");
                            }
                        }
                    }
                });
            }
        } catch (EOFException e) {
            SwingUtilities.invokeLater(() -> textArea.append(">> Conexão encerrada pelo servidor (EOF).\n"));
        } catch (IOException | ClassNotFoundException e) {
            SwingUtilities.invokeLater(() -> textArea.append(">> Erro inesperado: " + e.getMessage() + "\n"));
        }
    }
}
