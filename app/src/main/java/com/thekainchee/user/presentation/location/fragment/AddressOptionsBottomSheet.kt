package com.thekainchee.user.presentation.location.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentAddressOptionsBottomSheetBinding
import com.thekainchee.user.databinding.FragmentLocationListBinding

class AddressOptionsBottomSheet(private val onEditClick: () -> Unit,private val onDeleteClick: () -> Unit,private val onSetDefaultClick:()->Unit)
    : BottomSheetDialogFragment() {
    private var _binding: FragmentAddressOptionsBottomSheetBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddressOptionsBottomSheetBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvEdit.setOnClickListener {
            onEditClick()
            dismiss()
        }
        binding.tvDelete.setOnClickListener {
            onDeleteClick()
            dismiss()
        }
        binding.tvDefault.setOnClickListener {
            onSetDefaultClick()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}