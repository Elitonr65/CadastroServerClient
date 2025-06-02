package cadastroserver;

import controller.*;
import model.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class CadastroThread extends Thread {

    private final ProdutoJpaController ctrl;
    private final UsuarioJpaController ctrlUsu;
    private final Socket s1;

    public CadastroThread(ProdutoJpaController ctrl, UsuarioJpaController ctrlUsu, Socket s1) {
        this.ctrl = ctrl;
        this.ctrlUsu = ctrlUsu;
        this.s1 = s1;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(s1.getInputStream()); ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream())) {

            String login = (String) in.readObject();
            String senha = (String) in.readObject();

            Usuario u = ctrlUsu.findUsuario(login, senha);
            if (u == null) {
                out.writeObject("Acesso negado");
                return;
            }
            out.writeObject("Usuario conectado com sucesso");

            while (true) {
                String comando = (String) in.readObject();
                if ("L".equals(comando)) {
                    List<Produto> produtos = ctrl.findProdutoEntities();
                    out.writeObject(produtos);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
        }
    }
}
