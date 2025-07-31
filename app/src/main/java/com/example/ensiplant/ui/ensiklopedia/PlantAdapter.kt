package com.example.ensiplant.ui.ensiklopedia

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ensiplant.data.model.Plant
import com.example.ensiplant.databinding.ItemPlantBinding
// import com.bumptech.glide.Glide // Nanti kita akan pakai ini untuk memuat gambar

class PlantAdapter : ListAdapter<Plant, PlantAdapter.PlantViewHolder>(PLANT_COMPARATOR) {


    private var originalList: List<Plant> = emptyList()

    override fun submitList(list: List<Plant>?) {
        super.submitList(list)
        originalList = list ?: emptyList()
    }

    fun filter(keyword: String) {
        val filtered = if (keyword.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.nama.contains(keyword, ignoreCase = true)
            }
        }
        super.submitList(filtered)
    }

    // Fungsi dipanggil saat item dalam daftar diklik
    private var onItemClickCallback: ((Plant) -> Unit)? = null


    fun setOnItemClickCallback(callback: (Plant) -> Unit) {
        this.onItemClickCallback = callback
    }

    inner class PlantViewHolder(private val binding: ItemPlantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            // Set nama dan nama latin tanaman
            binding.tvPlantName.text = plant.nama
            binding.tvPlantDescription.text = plant.latin

            // Cek dan tampilkan timestamp jika tidak null
            binding.tvPlantMetadata.text = plant.timestamp ?: ""

            Log.d("PlantAdapter", "Image URL: ${plant.foto}")

            // Load gambar dari URL dengan Glide
            Glide.with(binding.root.context)
                .load(plant.foto)
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(android.R.drawable.stat_notify_error)
                .into(binding.ivPlantphoto)

            // Aksi ketika item diklik
            binding.root.setOnClickListener {
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
