package com.example.ensiplant.ui.forum

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ensiplant.R
import com.example.ensiplant.data.model.forum.Comment
import com.example.ensiplant.databinding.ItemCommentBinding

class CommentAdapter(private val onReplyClick: (Comment) -> Unit) : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(COMMENT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        if (comment != null) {
            holder.bind(comment)
        }
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            // Bind data ke view
            binding.tvCommenterUsername.text = comment.username
            binding.tvCommentText.text = comment.text
            binding.tvLikeCount.text = comment.likeCount.toString()

            // TODO: Format timestamp Long menjadi "12 Mar • 23:10"
            binding.tvCommentTimestamp.text = "• 1h ago" // Placeholder

            // TODO: Implementasikan Glide/Coil untuk memuat gambar dari URL
            // Glide.with(itemView.context).load(comment.userAvatarUrl).into(binding.ivCommenterAvatar)

            // Mengatur ikon like berdasarkan status
            updateLikeIcon(comment.isLikedByUser)

            // DIUBAH: Listener ini sekarang memanggil lambda dari constructor
            binding.tvReply.setOnClickListener {
                onReplyClick(comment)
            }

            binding.btnLike.setOnClickListener {
                // TODO: Panggil fungsi di ViewModel untuk handle like/unlike di database
                comment.isLikedByUser = !comment.isLikedByUser
                if (comment.isLikedByUser) {
                    comment.likeCount++
                } else {
                    comment.likeCount--
                }
                updateLikeIcon(comment.isLikedByUser)
                binding.tvLikeCount.text = comment.likeCount.toString()
            }
        }

        // Mengubah ikon like
        private fun updateLikeIcon(isLiked: Boolean) {
            if (isLiked) {
                binding.btnLike.setImageResource(R.drawable.ic_like_active)
            } else {
                binding.btnLike.setImageResource(R.drawable.ic_like_inactive)
            }
        }
    }

    companion object {
        private val COMMENT_COMPARATOR = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.id == newItem.id
            }
            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }
        }
    }
}