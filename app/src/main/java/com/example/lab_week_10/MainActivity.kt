package com.example.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.viewmodels.TotalViewModel

class MainActivity : AppCompatActivity() {

    // Inisialisasi Database
    private val db by lazy { prepareDatabase() }

    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    // ID konstan karena kita hanya simpan 1 baris data untuk contoh ini
    companion object {
        const val ID: Long = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load data dari database saat aplikasi mulai
        initializeValueFromDatabase()

        prepareViewModel()
    }

    // Update database saat aplikasi dipause (home button / close)
    override fun onPause() {
        super.onPause()
        // Simpan nilai total terakhir ke database
        viewModel.total.value?.let { currentValue ->
            db.totalDao().update(Total(ID, currentValue))
        }
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java,
            "total-database"
        ).allowMainThreadQueries().build()
        // allowMainThreadQueries dipake untuk penyederhanaan di lab ini
    }

    private fun initializeValueFromDatabase() {
        // Cek apakah data dengan ID 1 sudah ada?
        val totalList = db.totalDao().getTotal(ID)
        if (totalList.isEmpty()) {
            // Jika kosong, buat data baru mulai dari 0
            db.totalDao().insert(Total(id = ID, total = 0))
        } else {
            // Jika ada, ambil nilainya dan masukkan ke ViewModel
            viewModel.setTotal(totalList.first().total)
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text = getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) { total ->
            updateText(total)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }
}