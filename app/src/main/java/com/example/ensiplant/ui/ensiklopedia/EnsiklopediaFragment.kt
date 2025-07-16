package com.example.ensiplant.ui.ensiklopedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ensiplant.data.model.Plant
import com.example.ensiplant.databinding.FragmentEnsiklopediaBinding

class EnsiklopediaFragment : Fragment() {

    private var _binding: FragmentEnsiklopediaBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi adapter untuk RecyclerView
    private val plantAdapter by lazy { PlantAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEnsiklopediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadDummyData()
        setupItemClick()
    }

    private fun setupRecyclerView() {
        // Mengatur RecyclerView
        binding.rvPlants.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = plantAdapter
        }
    }

    private fun loadDummyData() {
        // TODO (Untuk BE): Ganti fungsi ini dengan logika untuk mengambil data dari file JSON.
        // DATA INI DUMMYYYY
        val dummyPlantList = listOf(
            Plant("1", "Zebra Plant", "Calathea zebrina", "12 Mar • 5 min", "url_gambar_1"),
            Plant("2", "Bayam", "Amaranthus", "12 Mar • 3 min", "url_gambar_2"),
            Plant("3", "Jagung", "Zea mays", "12 Mar • 3 min", "url_gambar_3"),
            Plant("4", "Lidah Mertua", "Sansevieria trifasciata", "12 Mar • 3 min", "url_gambar_4"),
            Plant("5", "Monstera", "Monstera deliciosa", "11 Mar • 4 min", "url_gambar_5")
        )

        // Mengirim data dummy ke adapter
        plantAdapter.submitList(dummyPlantList)
    }

    private fun setupItemClick() {
        // Menambahkan aksi saat salah satu item tanaman di klik
        plantAdapter.setOnItemClickCallback { plant ->
            // Action untuk pindah ke halaman detail sambil mengirim data plant
            val action = EnsiklopediaFragmentDirections.actionEnsiklopediaFragmentToPlantDetailFragment(plant)
            // Menjalankan navigasi
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
