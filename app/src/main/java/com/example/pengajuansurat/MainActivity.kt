package com.example.pengajuansurat

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var databaseHelper: SuratDatabaseHelper
    private lateinit var namaInput: EditText
    private lateinit var nimInput: EditText
    private lateinit var jenisSpinner: Spinner
    private lateinit var keperluanInput: EditText
    private lateinit var simpanButton: Button
    private lateinit var hapusButton: Button
    private lateinit var batalButton: Button
    private lateinit var daftarSuratView: ListView
    private lateinit var kosongText: TextView

    private val suratList = mutableListOf<SuratPengajuan>()
    private var selectedSurat: SuratPengajuan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        databaseHelper = SuratDatabaseHelper(this)
        bindViews()
        setupJenisSurat()
        setupActions()
        tampilkanData()
    }

    private fun bindViews() {
        namaInput = findViewById(R.id.inputNama)
        nimInput = findViewById(R.id.inputNim)
        jenisSpinner = findViewById(R.id.spinnerJenisSurat)
        keperluanInput = findViewById(R.id.inputKeperluan)
        simpanButton = findViewById(R.id.buttonSimpan)
        hapusButton = findViewById(R.id.buttonHapus)
        batalButton = findViewById(R.id.buttonBatal)
        daftarSuratView = findViewById(R.id.listSurat)
        kosongText = findViewById(R.id.textKosong)
    }

    private fun setupJenisSurat() {
        val jenisSurat = listOf("Surat Magang", "Surat Cuti", "Surat Riset")
        jenisSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            jenisSurat
        )
    }

    private fun setupActions() {
        simpanButton.setOnClickListener { tambahSurat() }
        hapusButton.setOnClickListener { hapusSurat() }
        batalButton.setOnClickListener { resetForm() }

        daftarSuratView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                pilihSurat(suratList[position])
            }
    }

    private fun tambahSurat() {
        val surat = ambilInput() ?: return
        val idBaru = databaseHelper.tambahSurat(surat)

        if (idBaru > 0) {
            tampilkanPesan("Pengajuan berhasil disimpan")
            resetForm()
            tampilkanData()
        } else {
            tampilkanPesan("Pengajuan gagal disimpan")
        }
    }

    private fun hapusSurat() {
        val suratDipilih = selectedSurat
        if (suratDipilih == null) {
            tampilkanPesan("Pilih data yang ingin dihapus")
            return
        }

        val jumlahHapus = databaseHelper.hapusSurat(suratDipilih.id)
        if (jumlahHapus > 0) {
            tampilkanPesan("Pengajuan berhasil dihapus")
            resetForm()
            tampilkanData()
        } else {
            tampilkanPesan("Pengajuan gagal dihapus")
        }
    }

    private fun ambilInput(): SuratPengajuan? {
        val nama = namaInput.text.toString().trim()
        val nim = nimInput.text.toString().trim()
        val keperluan = keperluanInput.text.toString().trim()
        val jenisSurat = jenisSpinner.selectedItem.toString()

        when {
            nama.isEmpty() -> {
                namaInput.error = "Nama wajib diisi"
                return null
            }
            nim.isEmpty() -> {
                nimInput.error = "NIM wajib diisi"
                return null
            }
            keperluan.isEmpty() -> {
                keperluanInput.error = "Keperluan wajib diisi"
                return null
            }
        }

        return SuratPengajuan(
            nama = nama,
            nim = nim,
            jenisSurat = jenisSurat,
            keperluan = keperluan
        )
    }

    private fun tampilkanData() {
        suratList.clear()
        suratList.addAll(databaseHelper.semuaSurat())

        daftarSuratView.adapter = object : ArrayAdapter<SuratPengajuan>(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            suratList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val surat = getItem(position)
                val judul = view.findViewById<TextView>(android.R.id.text1)
                val detail = view.findViewById<TextView>(android.R.id.text2)

                judul.text = "${surat?.jenisSurat} - ${surat?.nama}"
                detail.text = "NIM ${surat?.nim} | ${surat?.keperluan}"

                return view
            }
        }

        kosongText.visibility = if (suratList.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun pilihSurat(surat: SuratPengajuan) {
        selectedSurat = surat
        namaInput.setText(surat.nama)
        nimInput.setText(surat.nim)
        keperluanInput.setText(surat.keperluan)

        val indexJenis = (0 until jenisSpinner.count).firstOrNull {
            jenisSpinner.getItemAtPosition(it) == surat.jenisSurat
        } ?: 0
        jenisSpinner.setSelection(indexJenis)

        hapusButton.isEnabled = true
    }

    private fun resetForm() {
        selectedSurat = null
        namaInput.text.clear()
        nimInput.text.clear()
        keperluanInput.text.clear()
        jenisSpinner.setSelection(0)
        simpanButton.isEnabled = true
        hapusButton.isEnabled = false
        namaInput.requestFocus()
    }

    private fun tampilkanPesan(pesan: String) {
        Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
    }
}
