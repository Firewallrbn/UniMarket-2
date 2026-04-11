const BASE_URL = "http://localhost:8081";
const URL_VENTAS = `${BASE_URL}/api/ventas/crear`;
const URL_CATALOGO = `${BASE_URL}/api/productos/catalogo`;
const URL_AUTH = `${BASE_URL}/api/auth/token`;
const URL_RESILIENCE = `${BASE_URL}/api/resilience/status`;
const URL_HISTORIAL = `${BASE_URL}/api/ventas/historial`;

document.addEventListener("DOMContentLoaded", () => {
    // ─── UI Elements ───
    const btnCargarCatalogo = document.getElementById("btnCargarCatalogo");
    const listaCatalogo = document.getElementById("listaCatalogo");
    const catalogTime = document.getElementById("catalogTime");

    const btnSimularVenta = document.getElementById("btnSimularVenta");
    const userTypeSelect = document.getElementById("userTypeSelect");
    const totalVentasMetric = document.getElementById("totalVentasMetric");

    const btnObtenerToken = document.getElementById("btnObtenerToken");
    const inputUsername = document.getElementById("inputUsername");
    const authStatus = document.getElementById("authStatus");
    const tokenPreview = document.getElementById("tokenPreview");

    const btnCheckResilience = document.getElementById("btnCheckResilience");
    const resilienceStatus = document.getElementById("resilienceStatus");

    const toast = document.getElementById("toast");
    const toastMessage = document.getElementById("toastMessage");

    let totalAcumulado = 0;
    let jwtToken = null; // El token JWT almacenado en memoria

    // ─── Toast ───
    const showToast = (message, icon = "fa-circle-check") => {
        toastMessage.innerHTML = message;
        toast.querySelector(".toast-icon").innerHTML = `<i class="fa-solid ${icon}"></i>`;
        toast.classList.add("show");
        setTimeout(() => toast.classList.remove("show"), 3500);
    };

    // ─── Update Time ───
    const updateTime = () => {
        const now = new Date();
        catalogTime.textContent = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    };
    updateTime();

    // ─── Auth Status Update ───
    const updateAuthUI = () => {
        if (jwtToken) {
            authStatus.innerHTML = `<i class="fa-solid fa-lock-open"></i> Autenticado`;
            authStatus.className = "auth-badge auth-ok";
            tokenPreview.textContent = jwtToken.substring(0, 30) + "...";
            tokenPreview.style.display = "block";
            // Habilitar botones protegidos
            btnSimularVenta.disabled = false;
            btnSimularVenta.classList.remove("btn-disabled");
        } else {
            authStatus.innerHTML = `<i class="fa-solid fa-lock"></i> Sin autenticar`;
            authStatus.className = "auth-badge auth-none";
            tokenPreview.style.display = "none";
            // Deshabilitar botones protegidos
            btnSimularVenta.disabled = true;
            btnSimularVenta.classList.add("btn-disabled");
        }
    };
    updateAuthUI();

    // ─── Helper: Headers with JWT ───
    const authHeaders = () => {
        const headers = { "Content-Type": "application/json" };
        if (jwtToken) {
            headers["Authorization"] = `Bearer ${jwtToken}`;
        }
        return headers;
    };

    // ═══════════════════════════════════════════
    // 1. OBTENER TOKEN JWT
    // ═══════════════════════════════════════════
    btnObtenerToken.addEventListener("click", async () => {
        const username = inputUsername.value.trim() || "testUser";

        try {
            btnObtenerToken.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Generando...`;

            const response = await fetch(`${URL_AUTH}?username=${encodeURIComponent(username)}`);
            if (!response.ok) throw new Error("Error al generar token");

            const data = await response.json();
            jwtToken = data.jwt;

            updateAuthUI();
            showToast(`<strong>JWT Obtenido</strong><br>Usuario: ${data.usuario}`);

            console.log("%c[AUTH] Token JWT obtenido:", "color: #10b981; font-weight: bold;");
            console.log(jwtToken);

        } catch (error) {
            console.error("Error obteniendo token:", error);
            showToast("Error al obtener el token JWT.", "fa-circle-xmark");
        } finally {
            btnObtenerToken.innerHTML = `<i class="fa-solid fa-key"></i> Obtener JWT`;
        }
    });

    // ═══════════════════════════════════════════
    // 2. CARGAR CATÁLOGO (público, no necesita JWT)
    // ═══════════════════════════════════════════
    btnCargarCatalogo.addEventListener("click", async () => {
        try {
            btnCargarCatalogo.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Cargando...`;

            const response = await fetch(URL_CATALOGO);
            if (!response.ok) throw new Error("Network response was not ok");

            const data = await response.json();

            // Render items
            listaCatalogo.innerHTML = "";
            if (data.length === 0) {
                listaCatalogo.innerHTML = `<li class="empty-state">No products found</li>`;
            } else {
                data.forEach(prod => {
                    listaCatalogo.innerHTML += `
                        <li>
                            <span class="prod-name"><i class="fa-solid fa-box mr-2"></i> ${prod.name}</span>
                            <span class="prod-price">$${prod.price.toFixed(2)}</span>
                        </li>
                    `;
                });
                showToast(`Se cargaron ${data.length} productos del catalogo.`);
                updateTime();
            }

        } catch (error) {
            console.error("Error fetching catalog:", error);
            showToast("Error al cargar el catalogo de productos.", "fa-circle-xmark");
        } finally {
            btnCargarCatalogo.innerHTML = `<i class="fa-solid fa-plus"></i> Cargar Catalogo (Actualizar)`;
        }
    });

    // ═══════════════════════════════════════════
    // 3. SIMULAR VENTA (requiere JWT)
    // ═══════════════════════════════════════════
    btnSimularVenta.addEventListener("click", async () => {
        if (!jwtToken) {
            showToast("Primero debes obtener un token JWT.", "fa-triangle-exclamation");
            return;
        }

        const usuarioId = userTypeSelect.value;
        const montoBase = Math.floor(Math.random() * 500) + 100;

        const payload = {
            usuarioId: usuarioId,
            montoBase: montoBase
        };

        try {
            btnSimularVenta.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Procesando...`;

            const response = await fetch(URL_VENTAS, {
                method: "POST",
                headers: authHeaders(),
                body: JSON.stringify(payload)
            });

            // Si recibimos 401/403, el token es inválido o expiró
            if (response.status === 401 || response.status === 403) {
                jwtToken = null;
                updateAuthUI();
                showToast("Token JWT invalido o expirado. Genera uno nuevo.", "fa-triangle-exclamation");
                return;
            }

            const data = await response.json();

            console.log("%c--- RESPUESTA SOA DEL CLUSTER BACKEND ---", "color: #f26419; font-weight: bold; font-size: 14px;");
            if (data.debugTrace) {
                data.debugTrace.forEach(line => console.log("%c" + line, "color: #6e6e6e;"));
            }

            if (!response.ok) {
                showToast(data.error || "Error en la validacion de usuario.", "fa-triangle-exclamation");
            } else {
                // Success
                totalAcumulado += data.montoFinal;
                totalVentasMetric.textContent = `$${totalAcumulado.toFixed(2)}`;

                showToast(`
                    <strong>Venta exitosa</strong><br>
                    Usuario: ${data.tipoUsuario} (${data.usuarioId})<br>
                    Base: $${data.montoBase} | Comision: $${data.comision}
                `);
            }
        } catch (error) {
            console.error("Error creating sale:", error);
            showToast("Error de conexion con el servicio de ventas.", "fa-circle-xmark");
        } finally {
            btnSimularVenta.innerHTML = `<i class="fa-solid fa-cart-plus"></i> Simular Venta (SOA)`;
        }
    });

    // ═══════════════════════════════════════════
    // 4. VER ESTADO DEL CIRCUIT BREAKER (público)
    // ═══════════════════════════════════════════
    btnCheckResilience.addEventListener("click", async () => {
        try {
            btnCheckResilience.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Consultando...`;

            const response = await fetch(URL_RESILIENCE);
            if (!response.ok) throw new Error("Error al consultar resiliencia");

            const data = await response.json();

            // Renderizar info del circuit breaker
            let html = "";
            const cbs = data.circuitBreakers || {};
            for (const [name, info] of Object.entries(cbs)) {
                const stateClass = info.estado === "CLOSED" ? "cb-closed"
                    : info.estado === "OPEN" ? "cb-open"
                    : "cb-half-open";

                html += `
                    <div class="cb-card">
                        <div class="cb-header">
                            <span class="cb-name">${name}</span>
                            <span class="cb-state ${stateClass}">${info.estado}</span>
                        </div>
                        <div class="cb-metrics">
                            <div class="cb-metric">
                                <span class="cb-metric-label">Tasa Fallos</span>
                                <span class="cb-metric-value">${info.tasaFallos}</span>
                            </div>
                            <div class="cb-metric">
                                <span class="cb-metric-label">Exitosas</span>
                                <span class="cb-metric-value text-green">${info.llamadasExitosas}</span>
                            </div>
                            <div class="cb-metric">
                                <span class="cb-metric-label">Fallidas</span>
                                <span class="cb-metric-value text-red">${info.llamadasFallidas}</span>
                            </div>
                            <div class="cb-metric">
                                <span class="cb-metric-label">No Permitidas</span>
                                <span class="cb-metric-value">${info.llamadasNoPermitidas}</span>
                            </div>
                        </div>
                        <div class="cb-config">
                            <small>Ventana: ${info.configuracion.slidingWindowSize} | Umbral: ${info.configuracion.failureRateThreshold} | Espera: ${info.configuracion.waitDurationInOpenState}</small>
                        </div>
                    </div>
                `;
            }

            if (html === "") {
                html = `<p class="text-mut">No hay Circuit Breakers registrados aun. Realiza una venta primero.</p>`;
            }

            resilienceStatus.innerHTML = html;
            showToast("Estado del Circuit Breaker actualizado.");

        } catch (error) {
            console.error("Error consultando resiliencia:", error);
            resilienceStatus.innerHTML = `<p class="text-red">Error al conectar con el servicio.</p>`;
            showToast("Error al consultar el estado de resiliencia.", "fa-circle-xmark");
        } finally {
            btnCheckResilience.innerHTML = `<i class="fa-solid fa-heart-pulse"></i> Ver Estado Circuit Breaker`;
        }
    });
});
