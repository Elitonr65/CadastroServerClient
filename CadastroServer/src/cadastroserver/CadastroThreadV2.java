package cadastroserver;

import controller.*;
import model.*;
import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CadastroThreadV2 extends Thread {

    private final ProdutoJpaController ctrlProd;
    private final UsuarioJpaController ctrlUsu;
    private final MovimentoJpaController ctrlMov;
    private final PessoaJpaController ctrlPessoa;
    private final Socket s1;

    public CadastroThreadV2(
            ProdutoJpaController ctrlProd,
            UsuarioJpaController ctrlUsu,
            MovimentoJpaController ctrlMov,
            PessoaJpaController ctrlPessoa,
            Socket s1
    ) {
        this.ctrlProd = ctrlProd;
        this.ctrlUsu = ctrlUsu;
        this.ctrlMov = ctrlMov;
        this.ctrlPessoa = ctrlPessoa;
        this.s1 = s1;
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream()); ObjectInputStream in = new ObjectInputStream(s1.getInputStream())) {

            String login = (String) in.readObject();
            String senha = (String) in.readObject();

            Usuario u = ctrlUsu.findUsuario(login, senha);
            if (u == null) {
                out.writeObject("Acesso negado");
                s1.close(); 
                return;
            }

            out.writeObject("Usuario conectado com sucesso");

            OUTER:
            while (true) {
                String comando = (String) in.readObject();
                if (comando != null) {
                    switch (comando) {
                        case "L" -> {
                            List<Produto> produtos = ctrlProd.findProdutoEntities();
                            out.writeObject(produtos);
                        }
                        case "E", "S" -> {
                            int idPessoa = Integer.parseInt((String) in.readObject());
                            int idProduto = Integer.parseInt((String) in.readObject());
                            int quantidade = Integer.parseInt((String) in.readObject());
                            float valorUnitario = Float.parseFloat((String) in.readObject());

                            Movimento mov = new Movimento();
                            mov.setTipo(comando.charAt(0));
                            mov.setIdPessoa(ctrlPessoa.findPessoa(idPessoa));
                            mov.setIdProduto(ctrlProd.findProduto(idProduto));
                            mov.setIdUsuario(u);
                            mov.setQuantidade(quantidade);
                            mov.setValorUnitario(BigDecimal.valueOf(valorUnitario));
                            ctrlMov.create(mov);

                            Produto p = ctrlProd.findProduto(idProduto);
                            int novaQuantidade = comando.equals("E")
                                    ? p.getQuantidade() + quantidade
                                    : p.getQuantidade() - quantidade;
                            p.setQuantidade(novaQuantidade);
                            ctrlProd.edit(p);

                            out.writeObject("Movimento registrado com sucesso");
                        }
                        case "X" -> {
                            out.writeObject(">> Conexão encerrada pelo servidor.");
                            break OUTER;
                        }
                        default -> {
                            out.writeObject(">> Comando inválido recebido: " + comando);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream());
                out.writeObject(">> Conexão encerrada pelo servidor.");
            } catch (IOException ex) {
                Logger.getLogger(CadastroThreadV2.class.getName()).log(Level.SEVERE, "Erro ao tentar notificar o cliente", ex);
            }
        }
    }
}
