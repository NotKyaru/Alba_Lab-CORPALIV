package com.corpaliv.albalab.data

// Datos de ejemplo 100% FICTICIOS.
// El caso prohíbe usar datos personales reales (RUT, correos, fotos, etc.).

val listaProgramas = listOf(
    Programa(
        "Formación para el empleo",
        "Capacitación en habilidades laborales para personas con discapacidad múltiple."
    ),
    Programa(
        "Producción y comercialización",
        "Elaboración y venta de productos artesanales con impacto social."
    ),
    Programa(
        "RSE y vinculación empresarial",
        "Iniciativas de responsabilidad social junto a empresas y donantes."
    ),
    Programa(
        "Escuela Especial Jan Van Dijk",
        "Educación especializada que impulsa la autonomía de cada persona."
    ),
    Programa(
        "Red Sociolaboral Alba Lab",
        "Red de apoyo para la inclusión social y laboral."
    )
)

val listaProductos = listOf(
    Producto("Velas artesanales", "Velas decorativas hechas a mano por la comunidad Alba Lab.", 4500),
    Producto("Jabones naturales", "Jabones ecológicos elaborados con ingredientes naturales.", 3200),
    Producto("Libretas recicladas", "Libretas hechas con materiales 100% reciclados.", 2800),
    Producto("Maceteros decorativos", "Maceteros de diseño para interiores y exteriores.", 6000)
)

val listaCasosExito = listOf(
    CasoExito(
        "Integración laboral",
        "Una persona se incorporó al programa de empleo y hoy participa en la producción de velas.",
        "120 personas formadas"
    ),
    CasoExito(
        "Nueva alianza empresarial",
        "Una empresa se sumó a la red de vinculación y apoya el programa de formación.",
        "15 alianzas activas"
    ),
    CasoExito(
        "Productos con impacto",
        "Los productos de Alba Lab llegaron a nuevas tiendas y ferias locales.",
        "800 productos vendidos"
    )
)

val listaModalidades = listOf(
    "Donación",
    "Alianza empresarial",
    "Voluntariado",
    "Compra de productos",
    "Capacitaciones"
)