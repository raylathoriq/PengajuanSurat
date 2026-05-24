package com.example.pengajuansurat

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SuratDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_SURAT (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAMA TEXT NOT NULL,
                $COLUMN_NIM TEXT NOT NULL,
                $COLUMN_JENIS_SURAT TEXT NOT NULL,
                $COLUMN_KEPERLUAN TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SURAT")
        onCreate(db)
    }

    fun tambahSurat(surat: SuratPengajuan): Long {
        val values = surat.toContentValues()
        return writableDatabase.insert(TABLE_SURAT, null, values)
    }

    fun semuaSurat(): List<SuratPengajuan> {
        val data = mutableListOf<SuratPengajuan>()
        val cursor = readableDatabase.query(
            TABLE_SURAT,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_ID DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                data.add(
                    SuratPengajuan(
                        id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                        nama = it.getString(it.getColumnIndexOrThrow(COLUMN_NAMA)),
                        nim = it.getString(it.getColumnIndexOrThrow(COLUMN_NIM)),
                        jenisSurat = it.getString(it.getColumnIndexOrThrow(COLUMN_JENIS_SURAT)),
                        keperluan = it.getString(it.getColumnIndexOrThrow(COLUMN_KEPERLUAN))
                    )
                )
            }
        }

        return data
    }

    fun hapusSurat(id: Long): Int {
        return writableDatabase.delete(
            TABLE_SURAT,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        )
    }

    private fun SuratPengajuan.toContentValues(): ContentValues {
        return ContentValues().apply {
            put(COLUMN_NAMA, nama)
            put(COLUMN_NIM, nim)
            put(COLUMN_JENIS_SURAT, jenisSurat)
            put(COLUMN_KEPERLUAN, keperluan)
        }
    }

    companion object {
        private const val DATABASE_NAME = "pengajuan_surat.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_SURAT = "surat"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAMA = "nama"
        private const val COLUMN_NIM = "nim"
        private const val COLUMN_JENIS_SURAT = "jenis_surat"
        private const val COLUMN_KEPERLUAN = "keperluan"
    }
}
