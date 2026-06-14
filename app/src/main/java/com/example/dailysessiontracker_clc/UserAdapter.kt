package com.example.dailysessiontracker_clc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class UserAdapter(
    private val context: AppCompatActivity,
    private val userList: MutableList<MutableMap<String?, String?>>,
    private val db: DatabaseHelper,
    private val getCurrentName: () -> String,
    private val onUpdateCallback: () -> Unit
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]

        // --- Format Date (Clean UI Format) ---
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val rawDate = user["date"]
        val displayDate = try {
            val parsed = inputFormat.parse(rawDate ?: "")
            if (parsed != null) outputFormat.format(parsed) else rawDate
        } catch (e: Exception) {
            rawDate
        }

        holder.dateText.text = "${position + 1}. $displayDate"

        // --- Paid Status ---
        holder.cbPaid.setOnCheckedChangeListener(null)
        holder.cbPaid.isChecked = user["paid"] == "1"
        holder.cbPaid.setOnCheckedChangeListener { _, isChecked ->
            val id = user["id"]
            if (id != null) {
                db.updatePaidStatus(id, isChecked)
                user["paid"] = if (isChecked) "1" else "0"
            }
        }

        // --- Edit Button Click (Material Date Picker) ---
        holder.btnEdit.setOnClickListener { view ->

            // Button click animation ✨
            view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100)
                .withEndAction {
                    view.animate().scaleX(1f).scaleY(1f).duration = 100
                }.start()

            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Training Date")
                .setTheme(R.style.MaterialCalendarTheme) // your custom theme
                .build()

            picker.show(context.supportFragmentManager, "DATE_PICKER")

            picker.addOnPositiveButtonClickListener { selection ->

                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = sdf.format(Date(selection))

                val id = user["id"]
                if (id != null) {
                    val currentName = getCurrentName()
                    db.updateData(id, currentName, formattedDate)
                    onUpdateCallback()
                }
            }
        }
    }

    override fun getItemCount(): Int = userList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.txtDate)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditItem)
        val cbPaid: CheckBox = itemView.findViewById(R.id.cbPaid)
    }
}