import socket

HOST = "127.0.0.1"
PORT = 5000

def send(sock, msg: str):
    """Envía un comando al servidor y muestra la respuesta."""
    sock.sendall((msg + "\n").encode("utf-8"))
    data = sock.recv(8192).decode("utf-8").rstrip()
    print(f"> {msg}")
    print(data)
    print("-" * 60)
    return data

def main():
    with socket.create_connection((HOST, PORT)) as s:
        print(f"[CLIENTE] Conectado a {HOST}:{PORT}")
        print(s.recv(8192).decode("utf-8"))  # Mensaje de bienvenida

        # =================== MÉTODO DE ESCRITURA ===================
        print("\n=== PRUEBAS DE ESCRITURA ===")
        send(s, "CREAR 6624465 Elena;Ramirez;vigente")
        send(s, "ACTUALIZAR 6624465 Elena;Ramirez;tramite")
        send(s, "CREAR 5648043 Elena;Ramirez;tramite")

        # =================== MÉTODO DE LECTURA =====================
        print("\n=== PRUEBAS DE LECTURA ===")
        send(s, "CONSULTAR 6624465")
        send(s, "ESTADO 6624465")
        send(s, "LISTAR")
        send(s, "CONSULTAR 123")
        send(s, "ESTADO 123")
        send(s, "LISTAR")


        # =================== ELIMINACIÓN ===========================
        print("\n=== ELIMINACIÓN ===")
        send(s, "ELIMINAR 5648043")
        send(s, "CONSULTAR 5648043")  

        # =================== SALIR ================================
        send(s, "QUIT")

if __name__ == "__main__":
    main()
