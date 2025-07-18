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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.core.widget.doOnTextChanged

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


        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            plantAdapter.filter(text.toString())
        }



        setupRecyclerView()
        loadPlantsFromFirestore()
        setupItemClick()
    }

    private fun setupRecyclerView() {
        // Mengatur RecyclerView
        binding.rvPlants.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = plantAdapter
        }
    }

    private fun loadPlantsFromFirestore() {
        val db = Firebase.firestore

        db.collection("plants")
            .get()
            .addOnSuccessListener { result ->
                val plantList = result.map { document ->
                    Plant(
                        id = document.id,
                        nama = document.getString("nama") ?: "",
                        latin = document.getString("latin") ?: "",
                        timestamp = document.getString("timestamp") ?: "",
                        foto = document.getString("foto") ?: "",
                        deskripsi = document.getString("deskripsi") ?: ""
                    )
                }
                plantAdapter.submitList(plantList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Gagal memuat data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
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
