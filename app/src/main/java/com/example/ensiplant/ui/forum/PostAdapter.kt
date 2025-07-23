package com.example.ensiplant.ui.forum

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ensiplant.R
import com.example.ensiplant.data.model.forum.Post
import com.example.ensiplant.databinding.ItemForumPostBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.example.ensiplant.data.repository.PostRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class PostAdapter(
    private val postRepository: PostRepository,
    private val onPostClick: (Post) -> Unit,
    private val onCommentIconClick: (Post) -> Unit
) : ListAdapter<Post, PostAdapter.PostViewHolder>(POST_COMPARATOR) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemForumPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)


    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        if (post != null) {
            holder.bind(post)
            // Listener untuk seluruh item view (navigasi biasa)
            holder.itemView.setOnClickListener {
                onPostClick(post)
            }
        }
    }

    inner class PostViewHolder(private val binding: ItemForumPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.tvUsername.text = post.username
            binding.tvPostDate.text = post.postDate
            binding.tvPostCaption.text = post.caption
            binding.tvLikeCount.text = post.likeCount.toString()
            binding.tvCommentCount.text = post.commentCount.toString()

            // Load avatar dari drawable (contoh: avatar4.jpg -> avatar4)
            val context = itemView.context
            val avatarName = post.avatar.substringBeforeLast(".") // misal "avatar4.jpg" -> "avatar4"
            val avatarResId = context.resources.getIdentifier(avatarName, "drawable", context.packageName)



            if (avatarResId != 0) {
                Glide.with(context)
                    .load(avatarResId)
                    .into(binding.ivUserAvatar)
            } else {
                // fallback kalau resource tidak ditemukan
                binding.ivUserAvatar.setImageResource(R.drawable.ic_profile)
            }

            // Load gambar post (Cloudinary)
            if (!post.postImageUrl.isNullOrEmpty()) {
                Glide.with(context)
                    .load(post.postImageUrl)
                    .placeholder(R.drawable.ic_profile)
                    .into(binding.ivPostImage)
            } else {
                // Optional: sembunyikan image view kalau tidak ada gambar
                binding.ivPostImage.visibility = View.GONE
            }


            updateLikeIcon(post.isLikedByUser)

            binding.btnLike.setOnClickListener {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
                val wasLiked = post.likes.contains(userId)

                // Update lokal list
                val updatedLikes = if (wasLiked) {
                    post.likes - userId
                } else {
                    post.likes + userId
                }

                val updatedPost = post.copy(
                    likes = updatedLikes,
                    isLikedByUser = !wasLiked,
                    likeCount = if (wasLiked) post.likeCount - 1 else post.likeCount + 1
                )

                // Replace post di list
                val currentList = currentList.toMutableList()
                currentList[adapterPosition] = updatedPost
                submitList(currentList)

                // Firestore update async
                CoroutineScope(Dispatchers.IO).launch {
                    postRepository.toggleLike(post.id, userId)
                }
            }

            binding.btnDeletePost.visibility = if (post.uid == FirebaseAuth.getInstance().currentUser?.uid) {
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.btnDeletePost.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post? This action cannot be undone.")
                    .setPositiveButton("Delete") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                FirebaseFirestore.getInstance()
                                    .collection("posts")
                                    .document(post.id)
                                    .delete()
                                    .await()

                                withContext(Dispatchers.Main) {
                                    Toast.makeText(itemView.context, "Post deleted successfully.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(itemView.context, "error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }





            binding.btnComment.setOnClickListener {
                onCommentIconClick(post)
            }

        }



        private fun updateLikeIcon(isLiked: Boolean) {
            if (isLiked) {
                binding.btnLike.setImageResource(R.drawable.ic_like_active)
            } else {
                binding.btnLike.setImageResource(R.drawable.ic_like_inactive)
            }
        }
    }

    companion object {
        private val POST_COMPARATOR = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
        }
    }
}