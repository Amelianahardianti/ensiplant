package com.example.ensiplant.ui.ensiklopedia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ensiplant.data.model.Plant
import com.example.ensiplant.databinding.ItemPlantBinding
// import com.bumptech.glide.Glide // Nanti kita akan pakai ini untuk memuat gambar

class PlantAdapter : ListAdapter<Plant, PlantAdapter.PlantViewHolder>(PLANT_COMPARATOR) {

    // Fungsi dipanggil saat item dalam daftar diklik
    private var onItemClickCallback: ((Plant) -> Unit)? = null

    fun setOnItemClickCallback(callback: (Plant) -> Unit) {
        this.onItemClickCallback = callback
    }

    inner class PlantViewHolder(private val binding: ItemPlantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            // Menyesuaikan dengan properti baru di data class Plant
            binding.tvPlantName.text = plant.nama
            binding.tvPlantDescription.text = plant.latin // Nama latin sebagai deskripsi singkat
            binding.tvPlantMetadata.text = plant.timestamp

            // TODO: Nanti kita akan memuat gambar dari URL menggunakan Glide atau Coil
            // Glide.with(itemView.context)
            //     .load(plant.foto) // Menggunakan properti 'foto'
            //     .into(binding.ivPlantPhoto)

            // Menambahkan aksi klik pada seluruh item
            itemView.setOnClickListener {
                onItemClickCallback?.invoke(plant)
            }
        }
    }

    // Membuat ViewHolder baru saat RecyclerView membutuhkannya
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = ItemPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)
    }

    // Menghubungkan data (bind) dari posisi tertentu ke ViewHolder
    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = getItem(position)
        if (plant != null) {
            holder.bind(plant)
        }
    }

    companion object {
        // DiffUtil membantu RecyclerView mengetahui item mana yang berubah,
        // ditambahkan, atau dihapus, sehingga lebih efisien.
        private val PLANT_COMPARATOR = object : DiffUtil.ItemCallback<Plant>() {
            override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
                return oldItem == newItem
            }
        }
    }
}
