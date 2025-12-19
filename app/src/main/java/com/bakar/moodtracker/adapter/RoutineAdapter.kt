package com.bakar.moodtracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bakar.moodtracker.data.RoutineEntity
import com.bakar.moodtracker.databinding.ItemRoutineBinding

class RoutineAdapter(
    private val onClick: (RoutineEntity) -> Unit,
    private val onToggleDone: (RoutineEntity, Boolean) -> Unit,
    private val onLongPressDelete: (RoutineEntity) -> Unit
) : RecyclerView.Adapter<RoutineAdapter.VH>() {

    private val items = mutableListOf<RoutineEntity>()

    fun submitList(list: List<RoutineEntity>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val binding: ItemRoutineBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemRoutineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val routine = items[position]

        holder.binding.tvTitle.text = routine.title

        holder.binding.cbDone.setOnCheckedChangeListener(null)
        holder.binding.cbDone.isChecked = routine.isCompleted

        holder.binding.cbDone.setOnCheckedChangeListener { _, isChecked ->
            onToggleDone(routine, isChecked)
        }

        holder.binding.root.setOnClickListener {
            onClick(routine) // ✅ edit
        }

        holder.binding.root.setOnLongClickListener {
            onLongPressDelete(routine) // ✅ delete
            true
        }
    }

    override fun getItemCount(): Int = items.size
}
