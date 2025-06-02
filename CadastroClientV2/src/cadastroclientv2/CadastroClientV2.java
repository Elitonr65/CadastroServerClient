package cadastroclientv2;

import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CadastroClientV2 {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("localhost", 4322)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            SaidaFrame tela = new SaidaFrame();
            tela.setVisible(true);
            
            ThreadClient th = new ThreadClient(in, tela.texto);
            th.start();
            
            out.writeObject("op1");
            out.writeObject("op1");
            
            while (true) {
                System.out.println("\n L - Listar | X - Finalizar | E – Entrada | S – Saida");
                String comando = teclado.readLine();
                out.writeObject(comando);
                
                if ("X".equals(comando)) break;
                
                if ("E".equals(comando) || "S".equals(comando)) {
                    System.out.print("Id da Pessoa: ");
                    out.writeObject(teclado.readLine());
                    System.out.print("Id do Produto: ");
                    out.writeObject(teclado.readLine());
                    System.out.print("Quantidade: ");
                    out.writeObject(teclado.readLine());
                    System.out.print("Valor Unitario: ");
                    out.writeObject(teclado.readLine());
                }
            }
        }
    }
}
