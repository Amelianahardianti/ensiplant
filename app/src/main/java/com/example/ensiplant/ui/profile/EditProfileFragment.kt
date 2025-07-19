package com.example.ensiplant.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ensiplant.AvatarPickerBottomSheet
import com.example.ensiplant.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    private var selectedAvatar: String = "avatar_1.jpg" // default avatar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel.loadUserData()

        profileViewModel.userData.observe(viewLifecycleOwner) { user ->
            binding.etEditUsername.setText(user.username)
            binding.etEditEmail.setText(user.email)
            binding.etEditCity.setText(user.location)

            selectedAvatar = user.avatar ?: "avatar_1.jpg"
            val avatarName = selectedAvatar.substringBefore(".")
            val resId = resources.getIdentifier(
                avatarName,
                "drawable",
                requireContext().packageName
            )
            binding.ivEditProfilePicture.setImageResource(resId)
        }

        binding.btnCloseEdit.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivEditAvatar.setOnClickListener {
            val picker = AvatarPickerBottomSheet { avatarName ->
                selectedAvatar = avatarName.substringBefore(".").replace("_", "") // FIXED ✅
                val resId = resources.getIdentifier(
                    selectedAvatar,
                    "drawable",
                    requireContext().packageName
                )
                binding.ivEditProfilePicture.setImageResource(resId)
            }
            picker.show(parentFragmentManager, "AvatarPicker")
        }


        binding.btnUpdateProfile.setOnClickListener {
            val newUsername = binding.etEditUsername.text.toString().trim()
            val newLocation = binding.etEditCity.text.toString().trim()

            if (newUsername.isEmpty() || newLocation.isEmpty()) {
                Toast.makeText(requireContext(), "Isi semua field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            profileViewModel.updateUserProfile(newUsername, newLocation, selectedAvatar) { success ->
                if (success) {
                    Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
