package com.example.ensiplant.ui.forum

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ensiplant.R
import com.example.ensiplant.data.model.forum.Comment
import com.example.ensiplant.databinding.ItemCommentBinding
import java.text.SimpleDateFormat
import java.util.*

class CommentAdapter(
    private val onReplyClick: (Comment) -> Unit
) : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(COMMENT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            // 1. Set username, text, like count
            binding.tvCommenterUsername.text = comment.username
            binding.tvCommentText.text = comment.text
            binding.tvLikeCount.text = comment.likeCount.toString()

            // 2. Format timestamp
            val sdf = SimpleDateFormat("dd MMM • HH:mm", Locale("id", "ID"))
            binding.tvCommentTimestamp.text = sdf.format(Date(comment.timestamp))

            // 3. Load avatar preset
            val avatarResId = when (comment.avatar) {
                "avatar1" -> R.drawable.avatar1
                "avatar2" -> R.drawable.avatar2
                "avatar3" -> R.drawable.avatar3
                "avatar4" -> R.drawable.avatar4
                "avatar5" -> R.drawable.avatar5
                "avatar6" -> R.drawable.avatar6
                else -> R.drawable.ic_profile
            }

            Glide.with(binding.root.context)
                .load(avatarResId)
                .placeholder(R.drawable.ic_profile)
                .into(binding.ivCommenterAvatar)

            // 4. Indent if it's a reply (has parentId)
            val layoutParams = binding.root.layoutParams as MarginLayoutParams
            layoutParams.marginStart = if (!comment.parentCommentId.isNullOrEmpty()) 64 else 0
            binding.root.layoutParams = layoutParams

            // 5. Like icon & click
            updateLikeIcon(comment.isLikedByUser)

            binding.btnLike.setOnClickListener {
                comment.isLikedByUser = !comment.isLikedByUser
                comment.likeCount += if (comment.isLikedByUser) 1 else -1
                updateLikeIcon(comment.isLikedByUser)
                binding.tvLikeCount.text = comment.likeCount.toString()
            }

            // 6. Reply
            binding.tvReply.setOnClickListener {
                onReplyClick(comment)
            }
        }

        private fun updateLikeIcon(isLiked: Boolean) {
            val res = if (isLiked) R.drawable.ic_like_active else R.drawable.ic_like_inactive
            binding.btnLike.setImageResource(res)
        }
    }

    companion object {
        private val COMMENT_COMPARATOR = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Comment, newItem: Comment) = oldItem == newItem
        }
    }
}
