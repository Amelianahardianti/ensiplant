package com.example.ensiplant.ui.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ensiplant.R
import com.example.ensiplant.data.model.forum.Post
import com.example.ensiplant.databinding.ItemForumPostBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.example.ensiplant.data.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// DIUBAH: Constructor sekarang menerima dua jenis click listener
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

                post.isLikedByUser = !post.isLikedByUser
                if (post.isLikedByUser) post.likeCount++ else post.likeCount--
                updateLikeIcon(post.isLikedByUser)
                binding.tvLikeCount.text = post.likeCount.toString()

                // INI yang penting: panggil suspend function di dalam coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    postRepository.toggleLike(post.id, userId)
                }
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