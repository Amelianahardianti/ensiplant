package com.example.ensiplant.ui.forum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ensiplant.databinding.FragmentCreatePostBinding

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tombol close
        binding.btnCloseCreate.setOnClickListener {
            // Perintah untuk kembali ke halaman sebelumnya
            findNavController().popBackStack()
        }

        // TODO: Tambahkan aksi klik pada ImageView untuk memilih gambar dari galeri
        // binding.ivAddImage.setOnClickListener { ... }

        // TODO (Untuk BE): Tambahkan aksi klik pada tombol "Post" untuk mengirim data
        // binding.btnPost.setOnClickListener { ... }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
