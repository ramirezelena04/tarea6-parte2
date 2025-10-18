import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

public class MainServer {

    // Modelo simple
    static class Ciudadano {
        final String cedula;
        String nombre;
        String apellido;
        String estado; // vigente | vencido | tramite

        Ciudadano(String cedula, String nombre, String apellido, String estado) {
            this.cedula = cedula;
            this.nombre = nombre;
            this.apellido = apellido;
            this.estado = estado;
        }
        String asLine() { return String.join("|", cedula, nombre, apellido, estado); }
    }

    // “Base de datos” en memoria segura para hilos
    private static final ConcurrentHashMap<String, Ciudadano> DB = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 5000;
        System.out.println("[SERVIDOR] DNIC TCP escuchando en puerto " + port + " ...");
        precargar(); // datos demo opcionales

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("[SERVIDOR] Cliente conectado: " + client.getRemoteSocketAddress());
                new Thread(new ClientHandler(client)).start();
            }
        } catch (IOException e) {
            System.err.println("[SERVIDOR] Error: " + e.getMessage());
        }
    }

    static void precargar() {
        DB.put("123", new Ciudadano("123", "Ana", "Lopez", "vigente"));
        DB.put("456", new Ciudadano("456", "Luis", "Gomez", "tramite"));
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        ClientHandler(Socket s) { this.socket = s; }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))
            ) {
                String line;
                while ((line = in.readLine()) != null) {
                    String resp = handle(line);
                    out.write(resp);
                    out.write("\n");
                    out.flush();
                    if ("QUIT".equalsIgnoreCase(line.trim())) break;
                }
            } catch (IOException e) {
                System.err.println("[SERVIDOR] Cliente desconectado: " + e.getMessage());
            } finally {
                try { socket.close(); } catch (IOException ignore) {}
            }
        }

        private String handle(String line) {
            if (line == null) return "ERROR empty";
            String t = line.trim();
            if (t.isEmpty()) return "ERROR empty";

            String cmd = t.split("\\s+", 2)[0].toUpperCase();
            String rest = t.length() > cmd.length() ? t.substring(cmd.length()).trim() : "";

            switch (cmd) {
                case "CONSULTAR": {
                    if (rest.isEmpty()) return "ERROR Usage: CONSULTAR <cedula>";
                    Ciudadano c = DB.get(rest);
                    return (c == null) ? "NOT_FOUND" : "DATOS " + c.asLine();
                }
                case "LISTAR": {
                    List<Ciudadano> list = new ArrayList<>(DB.values());
                    StringBuilder sb = new StringBuilder();
                    sb.append("LISTA ").append(list.size()).append("\n");
                    for (Ciudadano c : list) sb.append(c.asLine()).append("\n");
                    return sb.toString().trim();
                }
                case "ESTADO": {
                    if (rest.isEmpty()) return "ERROR Usage: ESTADO <cedula>";
                    Ciudadano c = DB.get(rest);
                    return (c == null) ? "NOT_FOUND" : "ESTADO " + c.estado;
                }
                case "CREAR": {
                    // CREAR <cedula> <nombre>;<apellido>;<estado>
                    String[] p = rest.split("\\s+", 2);
                    if (p.length < 2) return "ERROR Usage: CREAR <cedula> <nombre>;<apellido>;<estado>";
                    String ced = p[0];
                    String[] campos = p[1].split(";", 3);
                    if (campos.length < 3) return "ERROR Usage: CREAR <cedula> <nombre>;<apellido>;<estado>";
                    if (DB.containsKey(ced)) return "ERROR Ya existe";
                    DB.put(ced, new Ciudadano(ced, campos[0], campos[1], campos[2]));
                    return "OK";
                }
                case "ACTUALIZAR": {
                    // ACTUALIZAR <cedula> <nombre>;<apellido>;<estado>
                    String[] p = rest.split("\\s+", 2);
                    if (p.length < 2) return "ERROR Usage: ACTUALIZAR <cedula> <nombre>;<apellido>;<estado>";
                    String ced = p[0];
                    String[] campos = p[1].split(";", 3);
                    if (campos.length < 3) return "ERROR Usage: ACTUALIZAR <cedula> <nombre>;<apellido>;<estado>";
                    Ciudadano c = DB.get(ced);
                    if (c == null) return "NOT_FOUND";
                    c.nombre = campos[0];
                    c.apellido = campos[1];
                    c.estado = campos[2];
                    return "OK";
                }
                case "ELIMINAR": {
                    if (rest.isEmpty()) return "ERROR Usage: ELIMINAR <cedula>";
                    return (DB.remove(rest) == null) ? "NOT_FOUND" : "OK";
                }
                case "QUIT":
                    return "OK";
                default:
                    return "ERROR Unknown command";
            }
        }
    }
}
