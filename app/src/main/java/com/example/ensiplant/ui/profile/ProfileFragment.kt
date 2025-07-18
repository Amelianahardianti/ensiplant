package com.example.ensiplant.ui.profile


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ensiplant.AvatarPickerBottomSheet
import com.example.ensiplant.R
import com.example.ensiplant.data.model.Forum.Post
import com.example.ensiplant.databinding.FragmentProfileBinding
import com.example.ensiplant.ui.forum.PostAdapter
import com.google.firebase.auth.FirebaseAuth
import com.example.ensiplant.LoginActivity


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val postAdapter by lazy { PostAdapter() }
    private lateinit var firebaseAuth: FirebaseAuth
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

        profileViewModel.loadUserData()

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            return
        }

        setupRecyclerView()
        loadProfileData()
        loadUserPosts()
        setupClickListeners()

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }



    private fun setupRecyclerView() {
        binding.rvUserPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    private fun loadProfileData() {
        profileViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.tvProfileUsername.text = user.username
            binding.tvProfileEmail.text = user.email
            binding.tvLocation.text = user.location ?: "Belum diisi"

            val avatarName = user.avatar ?: "avatar_default"
            val resId = resources.getIdentifier(
                avatarName.substringBefore("."), // Misal: avatar_1 dari "avatar_1.jpg"
                "drawable",
                requireContext().packageName
            )
            binding.ivProfilePicture.setImageResource(resId)
        }
    }



    private fun loadUserPosts() {
        // TODO (Untuk BE): Panggil fungsi di ViewModel untuk mengambil daftar postingan
        // yang dibuat oleh user ini dari Firestore.
        val dummyPosts = listOf(
            Post("p1", "uid1", "nathaniaaa", "url", "17 July 2025", "url", "Look at my new plant!!", 12, 3, true),
            Post("p2", "uid1", "nathaniaaa", "url", "16 July 2025", "url", "My second plant here!", 25, 5)
        )
        postAdapter.submitList(dummyPosts)



    }

    private fun setupClickListeners(){
        binding.btnEditProfile.setOnClickListener {
            // Navigasi ke halaman Edit Profile
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        binding.fabAddPost.setOnClickListener {
            // Navigasi ke halaman Buat Postingan Baru
            findNavController().navigate(R.id.action_profileFragment_to_createPostFragment)
        }

        binding.btnLogout.setOnClickListener {
            performLogout()
        }
    }

    // Proses logout
    private fun performLogout() {
        // TODO BE: Proses sign out dari Firebase Authentication
        firebaseAuth.signOut()

        // Arahkan pengguna kembali ke LoginActivity
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