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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    private var isVideo: Boolean = false

    companion object {
        private const val REQUEST_CODE_MEDIA = 101
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

        binding.btnCloseCreate.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
            }
            val chooser = Intent.createChooser(intent, "Pilih Gambar atau Video")
            startActivityForResult(chooser, REQUEST_CODE_MEDIA)
        }

        binding.btnPost.setOnClickListener {
            submitPost()
        }
    }

    private suspend fun uploadImageToCloudinary(context: Context, uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            val requestFile = bytes?.toRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData(
                "file",
                "image.jpg",
                requestFile!!
            )

            val uploadPreset = "ensiplant_preset".toRequestBody("text/plain".toMediaTypeOrNull())
            val response = RetrofitClient.instance.uploadImage(filePart, uploadPreset)

            if (response.isSuccessful) {
                response.body()?.secure_url
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun submitPost() {
        val caption = binding.etCaption.text.toString().trim()
        val mediaUri = selectedMediaUri

        if (caption.isEmpty()) {
            Toast.makeText(requireContext(), "Caption tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        lifecycleScope.launch {
            try {
                val userDoc = FirebaseFirestore.getInstance().collection("users").document(uid).get().await()
                val username = userDoc.getString("username") ?: "Anonymous"
                val avatarUrl = userDoc.getString("avatar") ?: ""

                // Upload media jika ada
                val mediaUrl = if (mediaUri != null) {
                    uploadImageToCloudinary(requireContext(), mediaUri)
                } else {
                    null
                }

                val postId = UUID.randomUUID().toString()
                val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                val newPost = Post(
                    id = postId,
                    uid = uid,
                    username = username,
                    avatar = avatarUrl,
                    postDate = currentDateTime,
                    postImageUrl = mediaUrl, // boleh null
                    caption = caption,
                    likeCount = 0,
                    commentCount = 0,
                    isVideo = false
                )

                val result = PostRepository().createPost(newPost)
                if (result) {
                    Toast.makeText(requireContext(), "Post berhasil dikirim!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Gagal kirim post", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_MEDIA && resultCode == Activity.RESULT_OK) {
            selectedMediaUri = data?.data
            selectedMediaUri?.let { uri ->
                val mimeType = requireContext().contentResolver.getType(uri)
                isVideo = mimeType?.startsWith("video/") == true

                if (mimeType?.startsWith("image/") == true) {
                    binding.ivAddImage.setImageURI(uri)
                } else if (mimeType?.startsWith("video/") == true) {
                    binding.ivAddImage.setImageResource(R.drawable.add_image)
                }
            }
        }
    }
}
