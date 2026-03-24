package com.example.dailysessiontracker_clc

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var myDb: DatabaseHelper
    private lateinit var editName: EditText
    private lateinit var btnSave: Button
    private lateinit var btnExport: Button
    private lateinit var tvCount: TextView
    private lateinit var recyclerView: RecyclerView

    private var adapter: UserAdapter? = null
    private var owner: String? = null // Persists the name after the first entry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
        }
        
        // --- 1. System UI Setup (Status Bar & Toolbar) ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black_card)
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // --- 2. Initialize Views ---
        myDb = DatabaseHelper(this)
        editName = findViewById(R.id.etName)
        btnSave = findViewById(R.id.btnSave)
        btnExport = findViewById(R.id.btnExport)
        tvCount = findViewById(R.id.tvCount)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initial UI Refresh
        refreshUI()

        // --- 3. Save Logic ---
        btnSave.setOnClickListener {
            val inputName = editName.text.toString().trim()

            // If owner isn't set yet, use the input
            if (owner == null && inputName.isNotEmpty()) {
                owner = inputName
            }

            if (owner != null) {
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val isInserted = myDb.insertData(owner, formattedDate)
                if (isInserted) {
                    editName.text.clear()
                    editName.visibility = View.GONE // Hide input after first save

                    refreshUI()
                    Toast.makeText(this, "Attendance Recorded", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a name first", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 4. Export JSON Logic ---
        btnExport.setOnClickListener {
            val dataList = myDb.getAllData()
            if (dataList.isEmpty()) {
                Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show()
            } else {
                val gson = GsonBuilder().setPrettyPrinting().create()
                val json = gson.toJson(dataList)
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())

                // Use 'owner' for filename, fallback to "Guest" if null
                val fileName = "Aikido_Record_${owner ?: "Guest"}_$timestamp"
                downloadStringAsFile(this, json, fileName)
            }
        }
    }

    private fun refreshUI() {
        val data = myDb.getAllData()
        adapter = UserAdapter(this, data, myDb) { refreshUI() }
        recyclerView.adapter = adapter

        // Update count text with owner name if available
        val count = myDb.recordCount
        tvCount.text = if (owner != null) {
            "Total Sessions for $owner: $count"
        } else {
            "Total Sessions Attended: $count"
        }
    }

    // --- 5. Downloader Logic (Supports All APIs) ---
    fun downloadStringAsFile(context: Context, content: String, fileName: String) {
        val fullFileName = "$fileName.txt"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fullFileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                try {
                    context.contentResolver.openOutputStream(it)?.use { out ->
                        out.write(content.toByteArray())
                    }
                    Toast.makeText(context, "Saved to Downloads", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Export Failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            try {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fullFileName)
                FileOutputStream(file).use { it.write(content.toByteArray()) }
                Toast.makeText(context, "Saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Storage Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}