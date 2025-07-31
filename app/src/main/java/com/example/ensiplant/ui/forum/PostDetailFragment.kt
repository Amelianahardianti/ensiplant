package com.example.ensiplant.ui.forum

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ensiplant.R
import com.example.ensiplant.data.model.forum.Comment
import com.example.ensiplant.data.model.forum.Post
import com.example.ensiplant.databinding.FragmentPostDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDetailFragment : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private var replyToCommentId: String? = null


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

        // DIUBAH: Semua pekerjaan berat ditunda menggunakan view.post
        view.post {
            setupToolbar()
            setupCommentsRecyclerView()
            setupClickListeners()
            loadData()
            fixMissingAvatarsInComments()

            if (args.openKeyboard) {
                focusCommentInput()
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                FirebaseFirestore.getInstance().collection("users").document(uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val avatarName = doc.getString("avatar") ?: "avatar1"
                        val avatarResId = resources.getIdentifier(avatarName, "drawable", requireContext().packageName)

                        Glide.with(requireContext())
                            .load(avatarResId)
                            .placeholder(R.drawable.ic_profile)
                            .into(binding.ivMyAvatar)
                    }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupCommentsRecyclerView() {
        binding.rvComments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
    }



    private fun loadData() {
        val postId = args.postId
        val db = FirebaseFirestore.getInstance()

        lifecycleScope.launch {
            try {
                val postSnapshot = db.collection("posts").document(postId).get().await()
                val post = postSnapshot.toObject(Post::class.java)

                if (post != null) {
                    showPostDetail(post)
                    loadComments(postId)
                } else {
                    Toast.makeText(context, "Post tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal memuat post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPostDetail(post: Post) {
        val postBinding = binding.includedPost
        postBinding.tvUsername.text = post.username
        postBinding.tvPostDate.text = post.postDate
        postBinding.tvPostCaption.text = post.caption
        postBinding.tvLikeCount.text = post.likeCount.toString()
        postBinding.tvCommentCount.text = post.commentCount.toString()

        val avatarResId = getAvatarResId(post.avatar)
        Glide.with(requireContext())
            .load(avatarResId)
            .placeholder(R.drawable.ic_profile)
            .into(postBinding.ivUserAvatar)

        if (!post.postImageUrl.isNullOrEmpty()) {
            postBinding.ivPostImage.visibility = View.VISIBLE
            Glide.with(requireContext())
                .load(post.postImageUrl)
                .into(postBinding.ivPostImage)
        } else {
            postBinding.ivPostImage.visibility = View.GONE
        }

        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        val isLikedByCurrentUser = currentUid != null && post.likes.contains(currentUid)
        post.isLikedByUser = isLikedByCurrentUser

        updateLikeIcon(isLikedByCurrentUser)

        postBinding.btnLike.setOnClickListener {
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val db = FirebaseFirestore.getInstance()
            val postRef = db.collection("posts").document(args.postId)

            val wasLiked = post.likes.contains(currentUid)
            val updatedLikes = if (wasLiked) post.likes - currentUid else post.likes + currentUid
            post.likes = updatedLikes
            post.isLikedByUser = !wasLiked
            post.likeCount += if (!wasLiked) 1 else -1

            // Update UI
            updateLikeIcon(post.isLikedByUser)
            postBinding.tvLikeCount.text = post.likeCount.toString()

            // Update Firestore
            val likesUpdate = if (post.isLikedByUser) {
                FieldValue.arrayUnion(currentUid)
            } else {
                FieldValue.arrayRemove(currentUid)
            }

            postRef.update(
                mapOf(
                    "likes" to likesUpdate,
                    "likeCount" to post.likeCount
                )
            ).addOnFailureListener {
                Toast.makeText(context, "Gagal update like", Toast.LENGTH_SHORT).show()
            }
        }


        postBinding.root.setOnClickListener(null)
    }


    private fun sendComment() {
        val commentText = binding.etAddComment.text.toString().trim()
        if (commentText.isEmpty()) return

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(context, "Kamu belum login", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                val username = document.getString("username") ?: "Anonim"
                val avatar = document.getString("avatar") ?: "avatar1"
                val commentMap = mapOf(
                    "postId" to args.postId,
                    "uid" to currentUser.uid,
                    "username" to username,
                    "avatar" to avatar,
                    "text" to commentText,
                    "timestamp" to System.currentTimeMillis(),
                    "parentCommentId" to replyToCommentId,
                    "isEdited" to false,
                    "likeCount" to 0,
                    "isLikedByUser" to false
                )
                db.collection("comments")
                    .add(commentMap)
                    .addOnSuccessListener {
                        val postRef = db.collection("posts").document(args.postId)
                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(postRef)
                            val currentCount = snapshot.getLong("commentCount") ?: 0
                            transaction.update(postRef, "commentCount", currentCount + 1)
                        }
                        binding.etAddComment.setText("")
                        focusCommentInput()
                        lifecycleScope.launch {
                            loadComments(args.postId)
                        }
                        replyToCommentId = null
                        binding.etAddComment.hint = "Tambahkan komentar..."
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Gagal menyimpan komentar", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadComments(postId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("comments")
            .whereEqualTo("postId", postId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val allComments = snapshot.documents.mapNotNull { it.toObject(Comment::class.java) }
                val structuredComments = buildThreadedList(allComments)
                commentAdapter.submitList(structuredComments)
            }
    }

    private fun buildThreadedList(comments: List<Comment>): List<Comment> {
        val commentMap = comments.associateBy { it.id }
        val groupedReplies = comments.groupBy { it.parentCommentId }
        val result = mutableListOf<Comment>()
        fun addThread(comment: Comment) {
            result.add(comment)
            groupedReplies[comment.id]?.forEach { reply ->
                addThread(reply)
            }
        }
        comments.filter { it.parentCommentId.isNullOrEmpty() }.forEach {
            addThread(it)
        }
        return result
    }

    private fun fixMissingAvatarsInComments() {
        val db = FirebaseFirestore.getInstance()
        db.collection("comments")
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    val avatar = doc.getString("avatar")
                    if (avatar.isNullOrBlank()) {
                        doc.reference.update("avatar", "avatar1")
                            .addOnSuccessListener {
                                Log.d("FixAvatar", "Updated avatar for comment: ${doc.id}")
                            }
                            .addOnFailureListener {
                                Log.e("FixAvatar", "Failed to update comment: ${doc.id}", it)
                            }
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FixAvatar", "Failed to fetch comments", it)
            }
    }

    private fun updateLikeIcon(isLiked: Boolean) {
        val iconRes = if (isLiked) R.drawable.ic_like_active else R.drawable.ic_like_inactive
        binding.includedPost.btnLike.setImageResource(iconRes)
    }

    private fun setupClickListeners() {
        binding.etAddComment.setOnClickListener {
            focusCommentInput()
        }
        binding.btnSendComment.setOnClickListener {
            sendComment()
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

    private fun getAvatarResId(avatarName: String?): Int {
        return when (avatarName) {
            "avatar1" -> R.drawable.avatar1
            "avatar2" -> R.drawable.avatar2
            "avatar3" -> R.drawable.avatar3
            "avatar4" -> R.drawable.avatar4
            "avatar5" -> R.drawable.avatar5
            "avatar6" -> R.drawable.avatar6
            else -> R.drawable.ic_profile
        }
    }
}