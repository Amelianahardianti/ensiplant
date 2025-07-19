package com.example.ensiplant.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ensiplant.R
import com.example.ensiplant.data.model.forum.Post
import com.example.ensiplant.databinding.FragmentProfileBinding
import com.example.ensiplant.auth.LoginActivity
import com.example.ensiplant.ui.forum.PostAdapter
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Menggunakan PostAdapter yang sudah diperbaiki dengan dua click listener
    private val postAdapter by lazy {
        PostAdapter(
            onPostClick = { post ->
                val action = ProfileFragmentDirections.actionProfileFragmentToPostDetailFragment(
                    postId = post.id,
                    openKeyboard = false
                )
                findNavController().navigate(action)
            },
            onCommentIconClick = { post ->
                val action = ProfileFragmentDirections.actionProfileFragmentToPostDetailFragment(
                    postId = post.id,
                    openKeyboard = true
                )
                findNavController().navigate(action)
            }
        )
    }

    private lateinit var firebaseAuth: FirebaseAuth
    // Menggunakan ViewModel untuk mengambil data
    private val profileViewModel: ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        // Pengecekan penting: jika user belum login, arahkan ke LoginActivity
        if (firebaseAuth.currentUser == null) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
            return // Hentikan eksekusi lebih lanjut
        }

        // Memulai proses memuat data
        profileViewModel.loadUserData()

        // Setup tampilan
        setupRecyclerView()
        loadProfileData()
        loadUserPosts()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        binding.rvUserPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    private fun loadProfileData() {
        // Mengamati perubahan data dari ViewModel
        profileViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.tvProfileUsername.text = user.username
            binding.tvProfileEmail.text = user.email
            binding.tvLocation.text = user.location ?: "Belum diisi"

            // Logika untuk menampilkan avatar (contoh)
            val avatarName = user.avatar ?: "avatar_default"
            val resId = resources.getIdentifier(
                avatarName.substringBefore("."),
                "drawable",
                requireContext().packageName
            )
            binding.ivProfilePicture.setImageResource(resId)
        }
    }

    private fun loadUserPosts() {
        // TODO (Untuk BE): Panggil fungsi di ViewModel untuk mengambil postingan user ini
        val dummyPosts = listOf(
            Post("p1", "uid1", "nathaniaaa", "url", "17 July 2025", "url", "Look at my new plant!!", 12, 3, true),
            Post("p2", "uid1", "nathaniaaa", "url", "16 July 2025", "url", "My second plant here!", 25, 5)
        )
        postAdapter.submitList(dummyPosts)
    }

    private fun setupClickListeners() {
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_createPostFragment)
        }

        binding.btnLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        firebaseAuth.signOut()
        val intent = Intent(activity, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}