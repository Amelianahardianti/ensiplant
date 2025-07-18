package com.example.ensiplant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class AvatarAdapter(
    private val avatars: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    inner class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avatar, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatarName = avatars[position]
        val resId = holder.itemView.context.resources.getIdentifier(
            avatarName, "drawable", holder.itemView.context.packageName
        )
        holder.imgAvatar.setImageResource(resId)

        holder.imgAvatar.setOnClickListener {
            onClick("$avatarName.jpg") // Kirim avatar yang dipilih
        }
    }

    override fun getItemCount() = avatars.size
}

