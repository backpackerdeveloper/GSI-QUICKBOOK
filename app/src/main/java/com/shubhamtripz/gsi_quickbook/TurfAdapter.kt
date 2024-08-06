package com.shubhamtripz.gsi_quickbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shubhamtripz.gsi_quickbook.databinding.ItemTurfBinding

class TurfAdapter(
    private var turfs: List<Turf>,
    private val onBookButtonClick: (Turf) -> Unit
) : RecyclerView.Adapter<TurfAdapter.TurfViewHolder>() {

    inner class TurfViewHolder(private val binding: ItemTurfBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(turf: Turf) {
            binding.turf = turf
            binding.bookButton.setOnClickListener {
                onBookButtonClick(turf)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurfViewHolder {
        val binding = ItemTurfBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TurfViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TurfViewHolder, position: Int) {
        holder.bind(turfs[position])
    }

    override fun getItemCount(): Int = turfs.size

    fun updateTurfList(newTurfs: List<Turf>) {
        turfs = newTurfs
        notifyDataSetChanged()
    }
}
