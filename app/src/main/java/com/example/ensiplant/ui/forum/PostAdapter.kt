package com.example.ensiplant.ui.forum

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ensiplant.R
import com.example.ensiplant.data.model.Forum.Post
import com.example.ensiplant.databinding.ItemForumPostBinding
// import com.bumptech.glide.Glide // Nanti kita akan pakai ini untuk memuat gambar

class PostAdapter : ListAdapter<Post, PostAdapter.PostViewHolder>(POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemForumPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        if (post != null) {
            holder.bind(post)
        }
    }

    inner class PostViewHolder(private val binding: ItemForumPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.tvUsername.text = post.username
            binding.tvPostDate.text = post.postDate
            binding.tvPostCaption.text = post.caption
            binding.tvLikeCount.text = post.likeCount.toString()
            binding.tvCommentCount.text = post.commentCount.toString()

            // TODO (Untuk BE): Implementasikan Glide/Coil untuk memuat gambar dari URL
            // Glide.with(itemView.context).load(post.userAvatarUrl).into(binding.ivUserAvatar)
            // Glide.with(itemView.context).load(post.postImageUrl).into(binding.ivPostImage)

            // Mengatur ikon like berdasarkan status
            updateLikeIcon(post.isLikedByUser)

            // Aksi saat tombol like diklik
            binding.btnLike.setOnClickListener {
                // TODO (Untuk BE): Panggil fungsi di ViewModel untuk handle like/unlike.
                // ViewModel akan mengubah data di Firestore dan mengupdate LiveData.
                // Untuk sementara, kita hanya ubah status lokal untuk demo.
                post.isLikedByUser = !post.isLikedByUser
                if (post.isLikedByUser) post.likeCount++ else post.likeCount--

                updateLikeIcon(post.isLikedByUser)
                binding.tvLikeCount.text = post.likeCount.toString()
            }
        }

        private fun updateLikeIcon(isLiked: Boolean) {
            if (isLiked) {
                binding.btnLike.setImageResource(R.drawable.ic_like_active) // Hati merah
            } else {
                binding.btnLike.setImageResource(R.drawable.ic_like_inactive) // Hati kosong
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