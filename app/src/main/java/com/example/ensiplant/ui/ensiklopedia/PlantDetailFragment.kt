package com.example.ensiplant.ui.ensiklopedia

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ensiplant.databinding.FragmentPlantDetailBinding

class PlantDetailFragment : Fragment() {

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        val plant = PlantDetailFragmentArgs.fromBundle(requireArguments()).plant

// Set data ke UI
        binding.tvDetailPlantName.text = plant.nama
        binding.tvDetailLatinName.text = plant.latin
        binding.tvDetailMetadata.text = plant.timestamp ?: ""
        Log.d("DEBUG_PLANT_DETAIL", "Deskripsi: ${plant.deskripsi}")
        binding.tvDetailDescription.text = plant.deskripsi

        Log.d("DEBUG_PLANT_DETAIL", "Plant: $plant")



        Glide.with(requireContext())
            .load(plant.foto)
            .into(binding.ivDetailPlantPhoto)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
