import socket

HOST = "127.0.0.1"
PORT = 5000

def send(sock, msg: str):
    sock.sendall((msg + "\n").encode("utf-8"))
    data = sock.recv(8192).decode("utf-8").rstrip()
    print(f"> {msg}")
    print(data)
    return data

def main():
    with socket.create_connection((HOST, PORT)) as s:
        print(f"[CLIENTE] Conectado a {HOST}:{PORT}")

        # ===== MODIFICACIÓN =====
        send(s, "CREAR 6624465 Elena;Ramirez;vigente")
        send(s, "ACTUALIZAR 6624465 Elena;Ramirez;tramite")

        # ===== CONSULTA =====
        send(s, "CONSULTAR 6624465")
        send(s, "ESTADO 6624465")
        send(s, "LISTAR")

        # ===== ELIMINACIÓN =====
        send(s, "ELIMINAR 6624465")
        send(s, "CONSULTAR 6624465")  # debería NOT_FOUND

        # Cierre
        send(s, "QUIT")

if __name__ == "__main__":
    main()