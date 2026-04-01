# UniMarket-2: Guía de Inicio

Este proyecto es una aplicación de microservicios diseñada para demostrar patrones de diseño (como Strategy), principios SOA y observabilidad distribuida (Tracing con Jaeger).

## 🚀 Requisitos Previos

- **Java 21** o superior.
- **Node.js** (para el frontend).
- **Docker Desktop** (para la infraestructura de monitoreo).
- **Maven** (incluido mediante `./mvnw`).

---

## 🛠️ Paso 1: Iniciar la Infraestructura (Docker)

Primero, debemos iniciar los servicios de soporte (Jaeger, Prometheus, Grafana).

```powershell
docker-compose up -d
```

### Servicios Disponibles:
- **Jaeger UI**: [http://localhost:16686](http://localhost:16686) (Para ver las trazas distribuidas).
- **Prometheus**: [http://localhost:9090](http://localhost:9090).
- **Grafana**: [http://localhost:3000](http://localhost:3000) (User: `admin` / Pass: `admin`).

---

## 🟢 Paso 2: Iniciar los Microservicios (Backend)

Debes ejecutar cada microservicio en una terminal separada.

### 1. Microservicio de Usuarios (Puerto 8080)
Este servicio maneja la información de los perfiles y validación de existencia.

```powershell
cd unimarket-usuarios
./mvnw spring-boot:run
```

### 2. Microservicio de Ventas (Puerto 8081)
Este servicio gestiona las ventas y aplica el **Pattern Strategy** según el tipo de usuario obtenido desde el servicio de usuarios.

```powershell
cd unimarket-ventas
# Si hay cambios en el código, compila primero:
./mvnw clean compile
# Ejecuta:
./mvnw spring-boot:run
```

---

## 💻 Paso 3: Iniciar el Frontend

El frontend simula la interacción del usuario final.

```powershell
cd unimarket-frontend
npm install
npm run dev
```
Accede a la URL indicada (usualmente [http://localhost:5173](http://localhost:5173)).

---

## 📜 Comandos Útiles de Mantenimiento

| Acción | Comando | Directorio |
| :--- | :--- | :--- |
| Detener Docker | `docker-compose down` | Raíz del proyecto |
| Reconstruir Ventas | `./mvnw clean package` | `unimarket-ventas` |
| Limpiar Node Modules | `rm -rf node_modules` | `unimarket-frontend` |
| Matar proceso en puerto (Windows) | `Stop-Process -Id (Get-NetTCPConnection -LocalPort <PUERTO>).OwningProcess -Force` | Cualquier terminal |

---

## 🔍 Observabilidad y Tracing (Jaeger)

Para verificar que el flujo de microservicios está funcionando correctamente (gráfico escalonado):
1. Inicia todos los servicios.
2. Realiza una venta desde el **Frontend**.
3. Ve a [Jaeger UI](http://localhost:16686).
4. Selecciona `unimarket-ventas` en la lista de servicios.
5. Haz clic en **Find Traces**.
6. Abre la traza correspondiente para ver la jerarquía de llamadas.
