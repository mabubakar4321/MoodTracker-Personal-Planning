package com.bakar.moodtracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakar.moodtracker.data.MoodEntity
import com.bakar.moodtracker.databinding.ItemMoodBinding
import com.bakar.moodtracker.util.MoodUtils
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(
    private val moods: List<MoodEntity>
) : RecyclerView.Adapter<MoodAdapter.VH>() {

    inner class VH(val binding: ItemMoodBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val mood = moods[position]

        val date = SimpleDateFormat(
            "dd MMM yyyy",
            Locale.getDefault()
        ).format(Date(mood.date))

        holder.binding.tvMood.text =
            "${mood.emotionType} (${mood.timeOfDay}) → ${MoodUtils.getMoodText(mood.moodValue)}"

        // ✅ SAFE NULL HANDLING (THIS FIXES YOUR ERROR)
        holder.binding.tvNote.text =
            mood.note?.takeIf { it.isNotBlank() } ?: "No note"

        holder.binding.tvDate.text = date
    }

    override fun getItemCount(): Int = moods.size
}
