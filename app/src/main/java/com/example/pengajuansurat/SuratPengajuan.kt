package com.example.pengajuansurat

data class SuratPengajuan(
    val id: Long = 0,
    val nama: String,
    val nim: String,
    val jenisSurat: String,
    val keperluan: String
)
