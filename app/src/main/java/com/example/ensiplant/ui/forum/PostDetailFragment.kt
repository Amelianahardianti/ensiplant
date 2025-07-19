package com.example.ensiplant.ui.forum

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ensiplant.R
import com.example.ensiplant.data.model.forum.Comment
import com.example.ensiplant.data.model.forum.Post
import com.example.ensiplant.databinding.FragmentPostDetailBinding

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private val commentAdapter by lazy {
        CommentAdapter { comment ->
            focusCommentInput()
            binding.etAddComment.setText("@${comment.username} ")
            binding.etAddComment.setSelection(binding.etAddComment.length())
        }
    }

    private val args: PostDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupCommentsRecyclerView()
        loadData()
        setupClickListeners()

        if (args.openKeyboard) {
            focusCommentInput()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupCommentsRecyclerView() {
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }
    }

    private fun loadData() {
        val postId = args.postId
        Toast.makeText(context, "Memuat post dengan ID: $postId", Toast.LENGTH_SHORT).show()

        // INI DUMMYYY
        val allPosts = listOf(
            Post("p1", "uid1", "nathaniaaa", "url", "18 July 2025", "url", "Look at my new plant!!", 12, 3, true),
            Post("p2", "uid1", "nathaniaaa", "url", "17 July 2025", "url", "My second plant here!", 25, 5),
            Post("p3", "uid2", "amelianah", "url", "19 July 2025", "url", "My first post here, hello!", 54, 27),
            Post("p4", "uid3", "gemini", "url", "16 July 2025", "url", "Just sharing my new garden setup.", 102, 15)
        )

        // Cari post yang sesuai dengan postId yang dikirim
        val currentPost = allPosts.find { it.id == postId }

        // Tampilkan data post ke layout 'included_post' jika ditemukan
        currentPost?.let { post ->
            val postBinding = binding.includedPost
            postBinding.tvUsername.text = post.username
            postBinding.tvPostDate.text = post.postDate
            postBinding.tvPostCaption.text = post.caption
            postBinding.tvLikeCount.text = post.likeCount.toString()
            postBinding.tvCommentCount.text = post.commentCount.toString()

            // click listener untuk tombol like di dalam postingan utama
            updateLikeIcon(post.isLikedByUser) // Set ikon awal
            postBinding.btnLike.setOnClickListener {
                post.isLikedByUser = !post.isLikedByUser
                if (post.isLikedByUser) post.likeCount++ else post.likeCount--
                updateLikeIcon(post.isLikedByUser)
                postBinding.tvLikeCount.text = post.likeCount.toString()
            }

            // 5. (PENTING) Matikan click listener untuk seluruh card agar tidak terjadi navigasi lagi
            postBinding.root.setOnClickListener(null)
        }


        // --- BAGIAN LAMA: MEMUAT KOMENTAR (TIDAK BERUBAH) ---
        val dummyComments = listOf(
            Comment("c1", postId, "uid3", "gemini", "url", "Awesome!!", 0L, likeCount = 23, isLikedByUser = true),
            Comment("c2", postId, "uid2", "amelianah", "url", "Nice plant!", 0L, likeCount = 7, isLikedByUser = false)
        )
        commentAdapter.submitList(dummyComments)
    }

    // mengupdate ikon like di postingan utama
    private fun updateLikeIcon(isLiked: Boolean) {
        val iconRes = if (isLiked) R.drawable.ic_like_active else R.drawable.ic_like_inactive
        binding.includedPost.btnLike.setImageResource(iconRes)
    }


    private fun setupClickListeners() {
        binding.etAddComment.setOnClickListener {
            focusCommentInput()
        }
    }

    private fun focusCommentInput() {
        binding.etAddComment.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.etAddComment, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}