package com.example.ensiplant.ui.forum

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ensiplant.R
import com.example.ensiplant.data.model.forum.Post
import com.example.ensiplant.databinding.FragmentForumBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ForumFragment : Fragment() {

    private var _binding: FragmentForumBinding? = null
    private val binding get() = _binding!!

    private val postAdapter by lazy {
        PostAdapter(
            onPostClick = { post ->
                // Navigasi biasa saat item diklik
                val action = ForumFragmentDirections.actionForumFragmentToPostDetailFragment(post.id)
                findNavController().navigate(action)
            },
            onCommentIconClick = { post ->
                // Navigasi saat ikon komentar diklik
                // TODO: Anda perlu cara untuk mengirim sinyal "buka keyboard" ke PostDetailFragment.
                // Cara paling mudah adalah dengan menambahkan argumen baru di nav_graph.
                val action = ForumFragmentDirections.actionForumFragmentToPostDetailFragment(post.id)
                findNavController().navigate(action)
                // Untuk sementara, kita navigasi biasa dulu.
            }
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

        binding.appBarLayout.setExpanded(true, false)

        setupRecyclerView()
        loadForumPosts()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        binding.rvForumPosts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    private fun loadForumPosts() {
        // Dummy data
        val dummyFypPosts = listOf(
            Post("p3", "uid2", "amelianah", "url", "19 July 2025", "url", "My first post here, hello!", 54, 27),
            Post("p1", "uid1", "nathaniaaa", "url", "18 July 2025", "url", "Look at my new plant!!", 12, 3, true),
            Post("p2", "uid1", "nathaniaaa", "url", "17 July 2025", "url", "My second plant here!", 25, 5),
            Post("p4", "uid3", "gemini", "url", "16 July 2025", "url", "Just sharing my new garden setup.", 102, 15)
        )
        postAdapter.submitList(dummyFypPosts)
    }

    private fun setupClickListeners() {
        binding.fabAddPostForum.setOnClickListener {
            findNavController().navigate(R.id.action_forumFragment_to_createPostFragment)
        }

        binding.includedToolbar.ivToolbarAvatar.setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.selectedItemId = R.id.profileFragment
        }

        binding.includedToolbar.btnToolbarSearch.setOnClickListener {
            binding.appBarLayout.setExpanded(true, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvForumPosts.adapter = null
        _binding = null
    }
}