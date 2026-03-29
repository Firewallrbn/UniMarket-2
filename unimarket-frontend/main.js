const URL_VENTAS = "http://localhost:8081/api/ventas/crear";
const URL_CATALOGO = "http://localhost:8081/api/productos/catalogo";

document.addEventListener("DOMContentLoaded", () => {
    // UI Elements
    const btnCargarCatalogo = document.getElementById("btnCargarCatalogo");
    const listaCatalogo = document.getElementById("listaCatalogo");
    const catalogTime = document.getElementById("catalogTime");

    const btnSimularVenta = document.getElementById("btnSimularVenta");
    const userTypeSelect = document.getElementById("userTypeSelect");
    const totalVentasMetric = document.getElementById("totalVentasMetric");
    
    const toast = document.getElementById("toast");
    const toastMessage = document.getElementById("toastMessage");

    let totalAcumulado = 0;

    // Toast function
    const showToast = (message, icon = "fa-circle-check") => {
        toastMessage.innerHTML = message;
        toast.querySelector(".toast-icon").innerHTML = `<i class="fa-solid ${icon}"></i>`;
        toast.classList.add("show");
        setTimeout(() => toast.classList.remove("show"), 3500);
    };

    // Update time
    const updateTime = () => {
        const now = new Date();
        catalogTime.textContent = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    };
    updateTime();

    // Load Catalog (GET)
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
                showToast(`Se cargaron ${data.length} productos del catálogo.`);
                updateTime();
            }

        } catch (error) {
            console.error("Error fetching catalog:", error);
            showToast("Error al cargar el catálogo de productos.", "fa-circle-xmark");
        } finally {
            btnCargarCatalogo.innerHTML = `<i class="fa-solid fa-plus"></i> Cargar Catálogo (Actualizar)`;
        }
    });

    // Simulate Sale (POST)
    btnSimularVenta.addEventListener("click", async () => {
        const usuarioId = userTypeSelect.value;
        const montoBase = Math.floor(Math.random() * 500) + 100; // Random amount between 100 and 600
        
        const payload = {
            usuarioId: usuarioId,
            montoBase: montoBase
        };

        try {
            btnSimularVenta.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Procesando...`;
            
            const response = await fetch(URL_VENTAS, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            const data = await response.json();
            
            console.log("%c--- RESPUESTA SOA DEL CLUSTER BACKEND ---", "color: #f26419; font-weight: bold; font-size: 14px;");
            if (data.debugTrace) {
                data.debugTrace.forEach(line => console.log("%c" + line, "color: #6e6e6e;"));
            }

            if (!response.ok) {
                showToast(data.error || "Error en la validación de usuario.", "fa-triangle-exclamation");
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
            // It could fail because CORS wasn't applied or service is down
            showToast("Error de conexión con el servicio de ventas.", "fa-circle-xmark");
        } finally {
            btnSimularVenta.innerHTML = `<i class="fa-solid fa-cart-plus"></i> Simular Venta (SOA)`;
        }
    });
});
