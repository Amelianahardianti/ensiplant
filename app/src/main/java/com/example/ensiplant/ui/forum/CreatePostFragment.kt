package com.example.ensiplant.ui.forum

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ensiplant.R
import com.example.ensiplant.data.model.forum.Post
import com.example.ensiplant.data.repository.PostRepository
import com.example.ensiplant.databinding.FragmentCreatePostBinding
import com.example.ensiplant.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private var selectedMediaUri: Uri? = null

    private val mediaLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedMediaUri = data?.data
            selectedMediaUri?.let { uri ->
                binding.layoutPlaceholder.visibility = View.GONE
                binding.ivPreviewImage.visibility = View.VISIBLE
                binding.ivPreviewImage.setImageURI(uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnCloseCreate.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.layoutPlaceholder.setOnClickListener {
            // REVISI: Intent hanya untuk memilih gambar
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            val chooser = Intent.createChooser(intent, "Pilih Gambar")
            mediaLauncher.launch(chooser)
        }
        binding.btnPost.setOnClickListener {
            submitPost()
        }
    }

    private suspend fun uploadImageToCloudinary(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                val requestFile = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
                val uploadPreset = "ensiplant_preset".toRequestBody("text/plain".toMediaTypeOrNull())

                val response = RetrofitClient.instance.uploadImage(filePart, uploadPreset)

                if (response.isSuccessful) {
                    response.body()?.secure_url
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun submitPost() {
        val caption = binding.etCaption.text.toString().trim()
        val mediaUri = selectedMediaUri
        val user = FirebaseAuth.getInstance().currentUser

        if (caption.isEmpty()) {
            Toast.makeText(requireContext(), "Caption tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }
        if (user == null) {
            Toast.makeText(requireContext(), "Anda harus login untuk membuat post", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                val postResult = withContext(Dispatchers.IO) {
                    val userDoc = FirebaseFirestore.getInstance().collection("users").document(user.uid).get().await()
                    val username = userDoc.getString("username") ?: "Anonymous"
                    val avatarUrl = userDoc.getString("avatar") ?: ""

                    val mediaUrl = if (mediaUri != null) {
                        uploadImageToCloudinary(requireContext(), mediaUri)
                    } else {
                        null
                    }

                    val postId = UUID.randomUUID().toString()
                    val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                    // REVISI: isVideo di-set false secara permanen
                    val newPost = Post(
                        id = postId, uid = user.uid, username = username, avatar = avatarUrl,
                        postDate = currentDateTime, postImageUrl = mediaUrl, caption = caption,
                        likeCount = 0, commentCount = 0, isVideo = false
                    )
                    PostRepository().createPost(newPost)
                }

                if (postResult) {
                    Toast.makeText(requireContext(), "Post berhasil dikirim!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Gagal kirim post", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBarCreate.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnPost.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}