package com.example.ensiplant.ui.forum

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ensiplant.R
import com.example.ensiplant.data.model.forum.Post
import com.example.ensiplant.data.repository.PostRepository
import com.example.ensiplant.databinding.FragmentForumBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.math.abs

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!
    private val postRepository = PostRepository()
    private var allPosts: List<Post> = emptyList()

    private val postAdapter by lazy {
        PostAdapter(
            onPostClick = { post ->
                val action = ForumFragmentDirections.actionForumFragmentToPostDetailFragment(post.id)
                findNavController().navigate(action)
            },
            onCommentIconClick = { post ->
                val action = ForumFragmentDirections.actionForumFragmentToPostDetailFragment(
                    postId = post.id,
                    openKeyboard = true
                )
                findNavController().navigate(action)
            },
            postRepository = postRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DIUBAH: Semua setup ditunda agar animasi lebih mulus
        view.post {
            setupRecyclerView()
            loadForumPostsFromFirestore()
            setupClickListeners()
            loadCurrentUserAvatar()
            setupAppBarListener()

            binding.etSearchForum.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val query = s.toString().trim()
                    val filteredList = if (query.isNotEmpty()) {
                        allPosts.filter { it.caption.contains(query, ignoreCase = true) }
                    } else {
                        allPosts
                    }
                    postAdapter.submitList(filteredList)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun setupAppBarListener() {
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                binding.includedToolbar.ivToolbarAvatar.visibility = View.VISIBLE
            } else {
                binding.includedToolbar.ivToolbarAvatar.visibility = View.INVISIBLE
            }
        })
    }

    private fun loadCurrentUserAvatar() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val avatarName = document.getString("avatar") ?: "avatar1"
                        val avatarResId = resources.getIdentifier(
                            avatarName,
                            "drawable",
                            requireContext().packageName
                        )
                        Glide.with(this)
                            .load(avatarResId)
                            .placeholder(R.drawable.ic_profile)
                            .circleCrop()
                            .into(binding.includedToolbar.ivToolbarAvatar)
                    }
                }
                .addOnFailureListener {
                    binding.includedToolbar.ivToolbarAvatar.setImageResource(R.drawable.ic_profile)
                }
        }
    }

    private fun setupRecyclerView() {
        binding.rvForumPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    private fun loadForumPostsFromFirestore() {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid

        FirebaseFirestore.getInstance()
            .collection("posts")
            .orderBy("postDate", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(
                        id = doc.id,
                        likes = (doc["likes"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                        isLikedByUser = currentUid != null && (doc["likes"] as? List<*>)?.contains(currentUid) == true
                    )
                }

                allPosts = posts
                postAdapter.submitList(posts)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal memuat post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }





    private fun setupClickListeners() {
        binding.fabAddPostForum.setOnClickListener {
            findNavController().navigate(R.id.action_forumFragment_to_createPostFragment)
        }

        binding.includedToolbar.ivToolbarAvatar.setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.selectedItemId =
                R.id.profileFragment
        }

        binding.includedToolbar.btnToolbarSearch.setOnClickListener {
            binding.appBarLayout.setExpanded(true, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}