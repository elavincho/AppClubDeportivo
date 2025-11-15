package com.elavincho.appclubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Clase para administrar la BBDD
// Tenemos que pasar 4 argumentos, el contexto(this), el nombre de la BBDD, factory(hace referencia al cursor) y la version de la BBDD

class DBHelper(context: Context) : SQLiteOpenHelper(context, "Club.db", null, 10) {

    // Función para crear la base de datos
    override fun onCreate(db: SQLiteDatabase) {
        android.util.Log.d("DB_CREATE", "Creando tabla socios...")

        db.execSQL(
            "CREATE TABLE socios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tipo TEXT, " +
                    "tipo_socio TEXT, " +
                    "nombre TEXT NOT NULL, " +
                    "apellido TEXT, " +
                    "tipo_doc TEXT, " +
                    "nro_doc TEXT, " +
                    "fecha_nac TEXT, " +
                    "telefono TEXT, " +
                    "mail TEXT, " +
                    "direccion TEXT)"
        )

        android.util.Log.d("DB_CREATE", "Tabla socios creada exitosamente")

        db.execSQL(
            "CREATE TABLE aptos_fisicos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "socio_id INTEGER NOT NULL, " +
                    "fecha_vencimiento TEXT NOT NULL, " +
                    "medico_nombre TEXT NOT NULL, " +
                    "medico_apellido TEXT NOT NULL, " +
                    "medico_matricula TEXT NOT NULL, " +
                    "es_apto INTEGER NOT NULL, " +  // 1 = Sí, 0 = No
                    "fecha_creacion TEXT NOT NULL, " +
                    "FOREIGN KEY (socio_id) REFERENCES socios(id) ON DELETE CASCADE)"
        )

        android.util.Log.d("DB_CREATE", "Tablas creadas exitosamente")

        db.execSQL(
            "CREATE TABLE comprobantes_pago (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "fecha TEXT NOT NULL, " +
                    "numero_socio TEXT NOT NULL, " +
                    "nombre TEXT NOT NULL, " +
                    "apellido TEXT NOT NULL, " +
                    "actividad TEXT NOT NULL, " +
                    "fecha_vencimiento TEXT NOT NULL, " +
                    "metodo_pago TEXT NOT NULL, " +
                    "cuota TEXT NOT NULL, " +
                    "importe TEXT NOT NULL, " +
                    "fecha_creacion TEXT NOT NULL)"
        )

        android.util.Log.d("DB_CREATE", "Todas las tablas creadas exitosamente")

        // Tabla de usuarios para login
        db.execSQL(
            "CREATE TABLE usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "rol TEXT DEFAULT 'admin', " +
                    "fecha_creacion TEXT NOT NULL)"
        )

        // Insertar usuario por defecto
        crearUsuarioPorDefecto(db)
    }

    // Funcion para actualizar la BBDD, solo se ejecuta cuando cambiamos la version
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        android.util.Log.d("DB_UPGRADE", "Actualizando BD de v$oldVersion a v$newVersion")
        // Elimina la tabla
        db.execSQL("DROP TABLE IF EXISTS socios")
        db.execSQL("DROP TABLE IF EXISTS aptos_fisicos")
        db.execSQL("DROP TABLE IF EXISTS comprobantes_pago")
        db.execSQL("DROP TABLE IF EXISTS usuarios")

        // Vuelve a crear la tabla
        onCreate(db)
    }

    // Método para crear usuario por defecto
    private fun crearUsuarioPorDefecto(db: SQLiteDatabase) {
        try {
            val values = ContentValues().apply {
                put("username", "admin")
                put("password", "1234") // En producción, esto debería estar encriptado
                put("rol", "admin")
                put("fecha_creacion", System.currentTimeMillis().toString())
            }
            db.insert("usuarios", null, values)
            android.util.Log.d("DB_USUARIOS", "Usuario por defecto creado")
        } catch (e: Exception) {
            android.util.Log.e("DB_USUARIOS", "Error creando usuario por defecto: ${e.message}")
        }
    }

    // Método para verificar credenciales de usuario
    fun verificarUsuario(username: String, password: String): Boolean {
        val db = this.readableDatabase
        var usuarioValido = false

        try {
            // Primero verifica si la tabla existe
            val cursorCheck = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='usuarios'",
                null
            )
            val tablaExiste = cursorCheck.count > 0
            cursorCheck.close()

            if (!tablaExiste) {
                android.util.Log.e("LOGIN", "La tabla 'usuarios' no existe")
                return false
            }

            val cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE username = ? AND password = ?",
                arrayOf(username, password)
            )

            usuarioValido = cursor.count > 0
            if (usuarioValido) {
                android.util.Log.d("LOGIN", "Usuario válido: $username")
            } else {
                android.util.Log.d("LOGIN", "Credenciales inválidas para: $username")
            }

            cursor.close()
        } catch (e: Exception) {
            android.util.Log.e("LOGIN", "Error verificando usuario: ${e.message}", e)
        } finally {
            db.close()
        }

        return usuarioValido
    }

    // Método para insertar un nuevo socio
    fun agregarSocio(
        tipo: String,
        tipoSocio: String,
        nombre: String,
        apellido: String,
        tipoDoc: String,
        nroDoc: String,
        fechaNac: String,
        telefono: String,
        mail: String,
        direccion: String
    ): Long {
        val db = this.writableDatabase
        var resultado: Long = -1

        try {
            val values = ContentValues().apply {
                put("tipo", tipo)
                put("tipo_socio", tipoSocio)
                put("nombre", nombre)
                put("apellido", apellido)
                put("tipo_doc", tipoDoc)
                put("nro_doc", nroDoc)
                put("fecha_nac", fechaNac)
                put("telefono", telefono)
                put("mail", mail)
                put("direccion", direccion)
            }

            resultado = db.insert("socios", null, values)

            // Log para debug
            android.util.Log.d("DB_DEBUG", "Insert result: $resultado")
            android.util.Log.d("DB_DEBUG", "Values: $values")

        } catch (e: Exception) {
            android.util.Log.e("DB_ERROR", "Error inserting socio: ${e.message}", e)
            e.printStackTrace()
        } finally {
            db.close()
        }
        return resultado
    }

    // Método para obtener todos los socios
    fun obtenerTodosLosSocios(): ArrayList<Socio> {
        val listaSocios = ArrayList<Socio>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM socios", null)

        try {
            if (cursor.moveToFirst()) {
                do {
                    val socio = Socio(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        tipoSocio = cursor.getString(cursor.getColumnIndexOrThrow("tipo_socio")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        tipoDoc = cursor.getString(cursor.getColumnIndexOrThrow("tipo_doc")),
                        nroDoc = cursor.getString(cursor.getColumnIndexOrThrow("nro_doc")),
                        fechaNac = cursor.getString(cursor.getColumnIndexOrThrow("fecha_nac")),
                        telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        mail = cursor.getString(cursor.getColumnIndexOrThrow("mail")),
                        direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion"))
                    )
                    listaSocios.add(socio)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }
        return listaSocios
    }

    // Método para insertar un apto físico
    fun agregarAptoFisico(
        socioId: Int,
        fechaVencimiento: String,
        medicoNombre: String,
        medicoApellido: String,
        medicoMatricula: String,
        esApto: Boolean
    ): Long {
        val db = this.writableDatabase
        var resultado: Long = -1

        try {
            val values = ContentValues().apply {
                put("socio_id", socioId)
                put("fecha_vencimiento", fechaVencimiento)
                put("medico_nombre", medicoNombre)
                put("medico_apellido", medicoApellido)
                put("medico_matricula", medicoMatricula)
                put("es_apto", if (esApto) 1 else 0)
                put("fecha_creacion", System.currentTimeMillis().toString())
            }

            resultado = db.insert("aptos_fisicos", null, values)

            android.util.Log.d("DB_DEBUG", "Insert apto físico result: $resultado")

        } catch (e: Exception) {
            android.util.Log.e("DB_ERROR", "Error inserting apto físico: ${e.message}", e)
            e.printStackTrace()
        } finally {
            db.close()
        }
        return resultado
    }

    // Método para obtener el último apto físico de un socio
    fun obtenerUltimoAptoFisico(socioId: Int): AptoFisico? {
        val db = this.readableDatabase
        var apto: AptoFisico? = null

        val cursor = db.rawQuery(
            "SELECT * FROM aptos_fisicos WHERE socio_id = ? ORDER BY fecha_creacion DESC LIMIT 1",
            arrayOf(socioId.toString())
        )

        try {
            if (cursor.moveToFirst()) {
                apto = AptoFisico(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    socioId = cursor.getInt(cursor.getColumnIndexOrThrow("socio_id")),
                    fechaVencimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento")),
                    medicoNombre = cursor.getString(cursor.getColumnIndexOrThrow("medico_nombre")),
                    medicoApellido = cursor.getString(cursor.getColumnIndexOrThrow("medico_apellido")),
                    medicoMatricula = cursor.getString(cursor.getColumnIndexOrThrow("medico_matricula")),
                    esApto = cursor.getInt(cursor.getColumnIndexOrThrow("es_apto")) == 1,
                    fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion"))
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }
        return apto
    }

    // Método para insertar un nuevo comprobante
    fun agregarComprobante(comprobante: ComprobantePago): Long {
        val db = this.writableDatabase
        var resultado: Long = -1

        try {
            val values = ContentValues().apply {
                put("fecha", comprobante.fecha)
                put("numero_socio", comprobante.numeroSocio)
                put("nombre", comprobante.nombre)
                put("apellido", comprobante.apellido)
                put("actividad", comprobante.actividad)
                put("fecha_vencimiento", comprobante.fechaVencimiento)
                put("metodo_pago", comprobante.metodoPago)
                put("cuota", comprobante.cuota)
                put("importe", comprobante.importe)
                put("fecha_creacion", System.currentTimeMillis().toString())
            }

            resultado = db.insert("comprobantes_pago", null, values)

            android.util.Log.d("DB_DEBUG", "Insert comprobante result: $resultado")
            android.util.Log.d("DB_DEBUG", "Comprobante values: $values")

        } catch (e: Exception) {
            android.util.Log.e("DB_ERROR", "Error inserting comprobante: ${e.message}", e)
            e.printStackTrace()
        } finally {
            db.close()
        }
        return resultado
    }

    // Método para obtener todos los comprobantes
    fun obtenerTodosLosComprobantes(): ArrayList<ComprobantePago> {
        val listaComprobantes = ArrayList<ComprobantePago>()
        val db = this.readableDatabase
        val cursor =
            db.rawQuery("SELECT * FROM comprobantes_pago ORDER BY fecha_creacion DESC", null)

        try {
            if (cursor.moveToFirst()) {
                do {
                    val comprobante = ComprobantePago(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                        numeroSocio = cursor.getString(cursor.getColumnIndexOrThrow("numero_socio")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        actividad = cursor.getString(cursor.getColumnIndexOrThrow("actividad")),
                        fechaVencimiento = cursor.getString(cursor.getColumnIndexOrThrow("fecha_vencimiento")),
                        metodoPago = cursor.getString(cursor.getColumnIndexOrThrow("metodo_pago")),
                        cuota = cursor.getString(cursor.getColumnIndexOrThrow("cuota")),
                        importe = cursor.getString(cursor.getColumnIndexOrThrow("importe")),
                        fechaCreacion = Date(
                            cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion"))
                                .toLong()
                        )
                    )
                    listaComprobantes.add(comprobante)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }
        return listaComprobantes
    }

    // Método para obtener la última actividad de un socio desde los comprobantes
    fun obtenerUltimaActividadSocio(socioId: Int): String {
        val db = this.readableDatabase
        var ultimaActividad = "Sin actividad registrada"

        val cursor = db.rawQuery(
            "SELECT actividad FROM comprobantes_pago WHERE numero_socio = ? ORDER BY fecha_creacion DESC LIMIT 1",
            arrayOf(socioId.toString())
        )

        try {
            if (cursor.moveToFirst()) {
                ultimaActividad = cursor.getString(cursor.getColumnIndexOrThrow("actividad"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }
        return ultimaActividad
    }

    // Método específico para obtener socios con vencimientos hoy
    fun obtenerSociosConVencimientoHoy(): List<Socio> {
        val db = this.readableDatabase
        val socios = mutableListOf<Socio>()

        val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        android.util.Log.d("DB_DEBUG", "Buscando socios con vencimiento: $fechaHoy")

        val query = """
    SELECT DISTINCT s.* 
    FROM socios s
    INNER JOIN comprobantes_pago cp ON s.id = cp.numero_socio
    WHERE cp.fecha_vencimiento = ? 
    AND s.tipo_socio = 'Socio'
    AND cp.cuota LIKE '%Mensual%'
    ORDER BY s.apellido, s.nombre
        """.trimIndent()

        android.util.Log.d("DB_DEBUG", "Query: $query")

        val cursor = db.rawQuery(query, arrayOf(fechaHoy))

        try {
            android.util.Log.d("DB_DEBUG", "Cursor count: ${cursor.count}")

            if (cursor.moveToFirst()) {
                do {
                    val socio = Socio(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        tipoSocio = cursor.getString(cursor.getColumnIndexOrThrow("tipo_socio")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                        tipoDoc = cursor.getString(cursor.getColumnIndexOrThrow("tipo_doc")),
                        nroDoc = cursor.getString(cursor.getColumnIndexOrThrow("nro_doc")),
                        fechaNac = cursor.getString(cursor.getColumnIndexOrThrow("fecha_nac")),
                        telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        mail = cursor.getString(cursor.getColumnIndexOrThrow("mail")),
                        direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion"))
                    )
                    socios.add(socio)
                    android.util.Log.d(
                        "DB_DEBUG",
                        "Socio agregado: ${socio.nombre} ${socio.apellido}"
                    )
                } while (cursor.moveToNext())
            } else {
                android.util.Log.d("DB_DEBUG", "No se encontraron socios en el cursor")
            }
        } catch (e: Exception) {
            android.util.Log.e(
                "DB_DEBUG",
                "Error en obtenerSociosConVencimientoHoy: ${e.message}",
                e
            )
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }

        android.util.Log.d("DB_DEBUG", "Total socios retornados: ${socios.size}")
        return socios
    }

    /* *********************************************************** */
    /* Datos de pruaba para que siempre haya socios en vencimientos hoy*/

    // Método para crear los socios de prueba
    private fun crearSociosDePrueba(db: SQLiteDatabase, fechaHoy: String) {
        // Socio 1: Juan Pérez
        val valuesSocio1 = ContentValues().apply {
            put("tipo", "Persona")
            put("tipo_socio", "Socio")
            put("nombre", "Juan")
            put("apellido", "Pérez")
            put("tipo_doc", "DNI")
            put("nro_doc", "12345678")
            put("fecha_nac", "15/05/1985")
            put("telefono", "1111111111")
            put("mail", "juan.perez@email.com")
            put("direccion", "5° Avenina 123")
        }

        val idSocio1 = db.insert("socios", null, valuesSocio1)
        android.util.Log.d("DB_DEBUG", "Socio 1 creado con ID: $idSocio1")

        // Socio 2: María Gómez
        val valuesSocio2 = ContentValues().apply {
            put("tipo", "Persona")
            put("tipo_socio", "Socio")
            put("nombre", "María")
            put("apellido", "Gómez")
            put("tipo_doc", "DNI")
            put("nro_doc", "87654321")
            put("fecha_nac", "20/08/1990")
            put("telefono", "2222222222")
            put("mail", "maria.gomez@email.com")
            put("direccion", "Avenida Siempre Viva 456")
        }

        val idSocio2 = db.insert("socios", null, valuesSocio2)
        android.util.Log.d("DB_DEBUG", "Socio 2 creado con ID: $idSocio2")

        // Crear comprobantes para ambos socios con vencimiento hoy
        if (idSocio1 != -1L) {
            crearComprobanteParaSocio(db, idSocio1.toInt(), "Juan", "Pérez", fechaHoy)
            android.util.Log.d("DB_DEBUG", "Comprobante creado para Socio 1")
        }

        if (idSocio2 != -1L) {
            crearComprobanteParaSocio(db, idSocio2.toInt(), "María", "Gómez", fechaHoy)
            android.util.Log.d("DB_DEBUG", "Comprobante creado para Socio 2")
        }
    }

    // Método para inicializar/actualizar los socios de prueba con vencimiento hoy
    fun inicializarSociosConVencimientoHoy() {
        val db = this.writableDatabase

        try {
            // Obtener fecha actual en formato dd/MM/yyyy
            val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            android.util.Log.d("DB_DEBUG", "Inicializando socios con vencimiento hoy: $fechaHoy")

            // Verificar si ya existen nuestros socios de prueba
            val sociosExistentes = verificarSociosDePrueba(db)  // Pasar la conexión como parámetro
            android.util.Log.d(
                "DB_DEBUG",
                "Socios existentes encontrados: ${sociosExistentes.size}"
            )

            if (sociosExistentes.size < 2) {
                // Crear los socios de prueba si no existen
                android.util.Log.d("DB_DEBUG", "Creando nuevos socios de prueba...")
                crearSociosDePrueba(db, fechaHoy)
            } else {
                // Actualizar los vencimientos de los socios existentes
                android.util.Log.d("DB_DEBUG", "Actualizando vencimientos de socios existentes...")
                actualizarVencimientosSociosDePrueba(db, sociosExistentes, fechaHoy)
            }

            android.util.Log.d(
                "DB_DEBUG",
                "Socios con vencimiento hoy inicializados/actualizados exitosamente"
            )

        } catch (e: Exception) {
            android.util.Log.e(
                "DB_ERROR",
                "Error inicializando socios con vencimiento hoy: ${e.message}",
                e
            )
        } finally {
            db.close()  // Cerrar la conexión SOLO aquí
        }
    }

    // Método modificado para aceptar la conexión como parámetro
    private fun verificarSociosDePrueba(db: SQLiteDatabase): List<Int> {
        val idsSocios = mutableListOf<Int>()

        try {
            val cursor = db.rawQuery(
                "SELECT id FROM socios WHERE nombre IN ('Juan', 'María') AND apellido IN ('Pérez', 'Gómez')",
                null
            )

            if (cursor.moveToFirst()) {
                do {
                    idsSocios.add(cursor.getInt(cursor.getColumnIndexOrThrow("id")))
                    android.util.Log.d(
                        "DB_DEBUG",
                        "Socio de prueba encontrado: ID ${
                            cursor.getInt(
                                cursor.getColumnIndexOrThrow("id")
                            )
                        }"
                    )
                } while (cursor.moveToNext())
            } else {
                android.util.Log.d("DB_DEBUG", "No se encontraron socios de prueba")
            }

            cursor.close()
        } catch (e: Exception) {
            android.util.Log.e("DB_ERROR", "Error verificando socios de prueba: ${e.message}")
        }
        // NO cerrar la base de datos aquí
        return idsSocios
    }

    // Método para verificar si existen los socios de prueba
    private fun verificarSociosDePrueba(): List<Int> {
        val db = this.readableDatabase
        val idsSocios = mutableListOf<Int>()

        try {
            val cursor = db.rawQuery(
                "SELECT id FROM socios WHERE nombre IN ('Juan', 'María') AND apellido IN ('Pérez', 'Gómez')",
                null
            )

            if (cursor.moveToFirst()) {
                do {
                    idsSocios.add(cursor.getInt(cursor.getColumnIndexOrThrow("id")))
                } while (cursor.moveToNext())
            }

            cursor.close()
        } catch (e: Exception) {
            android.util.Log.e("DB_ERROR", "Error verificando socios de prueba: ${e.message}")
        }

        return idsSocios
    }

    // Método para actualizar vencimientos de socios existentes
    private fun actualizarVencimientosSociosDePrueba(
        db: SQLiteDatabase,
        idsSocios: List<Int>,
        fechaHoy: String
    ) {
        // Primero eliminar comprobantes existentes de estos socios
        for (idSocio in idsSocios) {
            db.delete("comprobantes_pago", "numero_socio = ?", arrayOf(idSocio.toString()))
        }

        // Luego crear nuevos comprobantes con vencimiento hoy
        for (idSocio in idsSocios) {
            // Obtener nombre y apellido del socio
            val cursor = db.rawQuery(
                "SELECT nombre, apellido FROM socios WHERE id = ?",
                arrayOf(idSocio.toString())
            )

            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
                crearComprobanteParaSocio(db, idSocio, nombre, apellido, fechaHoy)
            }

            cursor.close()
        }
    }

    // Método auxiliar para crear comprobante para un socio
    private fun crearComprobanteParaSocio(
        db: SQLiteDatabase,
        idSocio: Int,
        nombre: String,
        apellido: String,
        fechaVencimiento: String
    ) {
        val valuesComprobante = ContentValues().apply {
            put("fecha", SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
            put("numero_socio", idSocio.toString())
            put("nombre", nombre)
            put("apellido", apellido)
            put("actividad", "Socio Prueba")
            put("fecha_vencimiento", fechaVencimiento)
            put("metodo_pago", "Efectivo")
            put("cuota", "Cuota Mensual - Socio")
            put("importe", "35000.00")
            put("fecha_creacion", System.currentTimeMillis().toString())
        }

        db.insert("comprobantes_pago", null, valuesComprobante)
    }

    // Método simplificado para obtener socios con vencimiento hoy
    fun obtenerSociosConVencimientoHoySimplificado(): List<Socio> {
        val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        android.util.Log.d(
            "DB_DEBUG",
            "=== INICIANDO obtenerSociosConVencimientoHoySimplificado ==="
        )
        android.util.Log.d("DB_DEBUG", "Fecha hoy: $fechaHoy")

        // Primero, asegurarnos de que los socios de prueba estén actualizados
        inicializarSociosConVencimientoHoy()

        // Luego obtener los socios con vencimiento hoy
        val socios = obtenerSociosConVencimientoHoy()
        android.util.Log.d(
            "DB_DEBUG",
            "=== FINALIZANDO obtenerSociosConVencimientoHoySimplificado ==="
        )
        android.util.Log.d("DB_DEBUG", "Total socios con vencimiento hoy: ${socios.size}")

        return socios
    }

    // Método de prueba para verificar que los socios se crearon correctamente
    fun verificarDatosDePrueba() {
        val db = this.readableDatabase

        try {
            // Verificar socios
            val cursorSocios = db.rawQuery("SELECT * FROM socios", null)
            android.util.Log.d("DB_DEBUG", "Total socios en BD: ${cursorSocios.count}")
            if (cursorSocios.moveToFirst()) {
                do {
                    val id = cursorSocios.getInt(cursorSocios.getColumnIndexOrThrow("id"))
                    val nombre =
                        cursorSocios.getString(cursorSocios.getColumnIndexOrThrow("nombre"))
                    val apellido =
                        cursorSocios.getString(cursorSocios.getColumnIndexOrThrow("apellido"))
                    android.util.Log.d("DB_DEBUG", "Socio: ID=$id, Nombre=$nombre $apellido")
                } while (cursorSocios.moveToNext())
            }
            cursorSocios.close()

            // Verificar comprobantes
            val fechaHoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val cursorComprobantes = db.rawQuery(
                "SELECT * FROM comprobantes_pago WHERE fecha_vencimiento = ?",
                arrayOf(fechaHoy)
            )
            android.util.Log.d(
                "DB_DEBUG",
                "Comprobantes con vencimiento hoy: ${cursorComprobantes.count}"
            )
            if (cursorComprobantes.moveToFirst()) {
                do {
                    val numeroSocio =
                        cursorComprobantes.getString(cursorComprobantes.getColumnIndexOrThrow("numero_socio"))
                    val nombre =
                        cursorComprobantes.getString(cursorComprobantes.getColumnIndexOrThrow("nombre"))
                    val apellido =
                        cursorComprobantes.getString(cursorComprobantes.getColumnIndexOrThrow("apellido"))
                    val fechaVenc =
                        cursorComprobantes.getString(cursorComprobantes.getColumnIndexOrThrow("fecha_vencimiento"))
                    android.util.Log.d(
                        "DB_DEBUG",
                        "Comprobante: Socio=$numeroSocio, $nombre $apellido, Vence: $fechaVenc"
                    )
                } while (cursorComprobantes.moveToNext())
            }
            cursorComprobantes.close()

        } catch (e: Exception) {
            android.util.Log.e("DB_ERROR", "Error verificando datos de prueba: ${e.message}")
        } finally {
            db.close()
        }
    }

}




