package com.example.ensiplant.ui.ensiklopedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        // TODO: BE akan mengambil data 'plant' yang dikirim
        // dan menampilkannya di sini.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
