package cadastroserver;

import controller.*;
import java.net.*;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class CadastroServer {
    public static void main(String[] args) throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("CadastroServerPU");

        ProdutoJpaController ctrlProd = new ProdutoJpaController(emf);
        UsuarioJpaController ctrlUsu = new UsuarioJpaController(emf);
        MovimentoJpaController ctrlMov = new MovimentoJpaController(emf);
        PessoaJpaController ctrlPessoa = new PessoaJpaController(emf);

        ServerSocket server = new ServerSocket(4322);
        System.out.println("Servidor iniciado na porta 4322...");

        while (true) {
            Socket s1 = server.accept();
            new CadastroThreadV2(ctrlProd, ctrlUsu, ctrlMov, ctrlPessoa, s1).start();
        }
    }
}
