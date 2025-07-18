package com.example.ensiplant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AvatarPickerBottomSheet(
    private val onAvatarSelected: (String) -> Unit
) : BottomSheetDialogFragment() {

    private val avatarList = listOf(
        "avatar1", "avatar2", "avatar3",
        "avatar4", "avatar5", "avatar6"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_avatar_picker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvAvatarPicker)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = AvatarAdapter(avatarList) {
            onAvatarSelected(it)
            dismiss()
        }
    }
}
