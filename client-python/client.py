import socket

HOST = "127.0.0.1"
PORT = 5000

def send(sock, msg: str):
    """Envía un comando al servidor y muestra la respuesta."""
    sock.sendall((msg + "\n").encode("utf-8"))
    data = sock.recv(8192).decode("utf-8").rstrip()
    print(data)
    print("-" * 60)
    return data

def main():
    with socket.create_connection((HOST, PORT)) as s:
        print(f"[CLIENTE] Conectado a {HOST}:{PORT}")
        print(s.recv(8192).decode("utf-8"))  # Mensaje de bienvenida

        while True:
            print("\n--- MENÚ ---")
            print("1. CREAR ciudadano")
            print("2. ACTUALIZAR ciudadano")
            print("3. CONSULTAR ciudadano")
            print("4. ESTADO ciudadano")
            print("5. LISTAR todos")
            print("6. ELIMINAR ciudadano")
            print("7. SALIR")
            opcion = input("Seleccione opción: ").strip()

            if opcion == "1":
                ced = input("Cédula: ").strip()
                nom = input("Nombre: ").strip()
                ape = input("Apellido: ").strip()
                est = input("Estado (vigente/vencido/tramite): ").strip()
                send(s, f"CREAR {ced} {nom};{ape};{est}")

            elif opcion == "2":
                ced = input("Cédula a actualizar: ").strip()
                nom = input("Nuevo nombre: ").strip()
                ape = input("Nuevo apellido: ").strip()
                est = input("Nuevo estado (vigente/vencido/tramite): ").strip()
                send(s, f"ACTUALIZAR {ced} {nom};{ape};{est}")

            elif opcion == "3":
                ced = input("Cédula a consultar: ").strip()
                send(s, f"CONSULTAR {ced}")

            elif opcion == "4":
                ced = input("Cédula para ver estado: ").strip()
                send(s, f"ESTADO {ced}")

            elif opcion == "5":
                send(s, "LISTAR")

            elif opcion == "6":
                ced = input("Cédula a eliminar: ").strip()
                send(s, f"ELIMINAR {ced}")

            elif opcion == "7":
                send(s, "QUIT")
                break

            else:
                print("Opción inválida, intente de nuevo.")

if __name__ == "__main__":
    main()
