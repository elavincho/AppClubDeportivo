package com.elavincho.appclubdeportivo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


// Clase para administrar la BBDD
// Tenemos que pasar 4 argumentos, el contexto(this), el nombre de la BBDD, factory(hace referencia al cursor) y la version de la BBDD

class DBHelper(context: Context) : SQLiteOpenHelper(context, "Club.db", null, 3) {

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
    }

    // Funcion para actualizar la BBDD, solo se ejecuta cuando cambiamos la version
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Elimina la tabla
        db.execSQL("DROP TABLE IF EXISTS socios")
        // Vuelve a crear la tabla
        onCreate(db)
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

    // Método para verificar la estructura de la tabla
    fun verificarEstructuraTabla() {
        val db = this.readableDatabase
        val cursor = db.rawQuery("PRAGMA table_info(socios)", null)

        val columnas = ArrayList<String>()
        if (cursor.moveToFirst()) {
            do {
                val nombreColumna = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val tipoColumna = cursor.getString(cursor.getColumnIndexOrThrow("type"))
                columnas.add("$nombreColumna ($tipoColumna)")
                android.util.Log.d("DB_STRUCTURE", "Columna: $nombreColumna - Tipo: $tipoColumna")
            } while (cursor.moveToNext())
        } else {
            android.util.Log.d("DB_STRUCTURE", "La tabla socios no existe")
        }
        cursor.close()
        db.close()

        android.util.Log.d("DB_STRUCTURE", "Total columnas: ${columnas.size}")
    }




}




