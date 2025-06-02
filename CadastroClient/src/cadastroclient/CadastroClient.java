package cadastroclient;

import java.io.*;
import java.net.*;
import java.util.List;
import model.Produto;

public class CadastroClient {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("localhost", 4322)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject("op1"); // login
            out.writeObject("op1"); // senha

            // ✅ Aguarda resposta do servidor após o login
            Object respostaLogin = in.readObject();
            if (respostaLogin instanceof String msg) {
                if (msg.equals("Acesso negado")) {
                    System.out.println("Acesso negado");
                    return; // encerra se não for autorizado
                } else {
                    System.out.println(msg); // exibe "Usuario conectado com sucesso"
                }
            }

            out.writeObject("L"); // comando para listar produtos

            Object resposta = in.readObject();

            if (resposta instanceof List<?> lista) {
                for (Object obj : lista) {
                    if (obj instanceof Produto p) {
                        System.out.println(p.getNome());
                    }
                }
            } else {
                System.out.println(resposta);
            }
        }
    }
}
