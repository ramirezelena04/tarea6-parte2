# Tarea 6 – Parte 2: DNIC (Servidor Java + Cliente Python)

## Protocolo
CONSULTA:
- CONSULTAR <cedula> -> DATOS <cedula>|<nombre>|<apellido>|<estado> | NOT_FOUND
- LISTAR -> LISTA <n> + n líneas <cedula>|<nombre>|<apellido>|<estado>
- ESTADO <cedula> -> ESTADO <vigente|vencido|tramite> | NOT_FOUND

MODIFICACIÓN:
- CREAR <cedula> <nombre>;<apellido>;<estado> -> OK | ERROR
- ACTUALIZAR <cedula> <nombre>;<apellido>;<estado> -> OK | NOT_FOUND
- ELIMINAR <cedula> -> OK | NOT_FOUND

QUIT -> OK

## Ejecutar
### Servidor (Java)
cd server-java
javac -d out src/MainServer.java
java -cp out MainServer

### Cliente (Python)
cd client-python
python3 client.py


##  Métodos implementados

###  Consultas (lectura)
1. **CONSULTAR** `<cedula>` → Devuelve los datos de un ciudadano.  
   Ejemplo: `CONSULTAR 6624465`  
   Respuesta: `DATOS 6624465|Elena|Ramirez|vigente`
2. **LISTAR** → Devuelve la lista de todos los ciudadanos registrados.  
   Respuesta:
   LISTA 3
123|Ana|Lopez|vigente
456|Luis|Gomez|tramite
6624465|Elena|Ramirez|tramite


3. **ESTADO** `<cedula>` → Indica si el documento está vigente, vencido o en trámite.  
Ejemplo: `ESTADO 6624465` → `ESTADO tramite`

---

###  Modificación (creación, actualización y eliminación)
1. **CREAR** `<cedula> <nombre>;<apellido>;<estado>`  
Crea un nuevo registro.  
Ejemplo: `CREAR 6624465 Elena;Ramirez;vigente` → `OK`

2. **ACTUALIZAR** `<cedula> <nombre>;<apellido>;<estado>`  
Modifica un ciudadano existente.  
Ejemplo: `ACTUALIZAR 6624465 Elena;Ramirez;tramite` → `OK`

3. **ELIMINAR** `<cedula>`  
Elimina un registro de la base de datos.  
Ejemplo: `ELIMINAR 6624465` → `OK`

##  Protocolo de comunicación (TCP)

- Los mensajes se envían como texto UTF-8 terminado en salto de línea (`\n`).
- El servidor responde con una línea de texto según el comando.
- El comando `QUIT` finaliza la sesión:  
