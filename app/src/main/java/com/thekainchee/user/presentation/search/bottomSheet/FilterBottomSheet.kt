package com.thekainchee.user.presentation.search.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentFilterBottomSheetBinding
import com.thekainchee.user.presentation.search.viewModel.SearchViewModel
import com.thekainchee.user.utils.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheet : BottomSheetDialogFragment() {
    private var _binding: FragmentFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var currentType: String? = null
    private var currentMinRating: Double? = null
    private var searchQuery: String = ""
    private val PRIMARY_COLOR =
        android.graphics.Color.parseColor("#00695C")

    private val GRAY_COLOR =
        android.graphics.Color.parseColor("#E4E8EA")

    private val viewModel: SearchViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBottomSheetBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchQuery = arguments?.getString(ARG_QUERY).orEmpty()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentType = viewModel.getCurrentType()
        currentMinRating = viewModel.getCurrentMinRating()
        selectType(currentType)
        selectRating(currentMinRating)

        setOnClickListeners()
    }
    private fun setOnClickListeners(){
        binding.tvClearAll.setOnClickListener {

            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(requireView(),"No internet connection",Snackbar.LENGTH_SHORT).show()
            }else{
                selectType(null)
                selectRating(null)
                currentType = null
                currentMinRating = null
                viewModel.searchParlours(
                    query = searchQuery,
                    type = currentType,
                    minRating = currentMinRating
                )
                dismiss()
            }
        }
        binding.cardMens.setOnClickListener {
            selectType("MENS")
        }

        binding.cardBeauty.setOnClickListener {
            selectType("BEAUTY")
        }

        binding.cardUnisex.setOnClickListener {
            selectType("UNISEX")
        }

        binding.cardRating4.setOnClickListener {
            selectRating(4.0)
        }

        binding.cardRating3.setOnClickListener {
            selectRating(3.0)
        }

        binding.cardRatingAny.setOnClickListener {
            selectRating(null)
        }
        binding.btnApplyFilters.setOnClickListener {

            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(requireView(),"No internet connection",Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.searchParlours(
                query = searchQuery,
                type = currentType,
                minRating = currentMinRating
            )

            dismiss()
        }
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.cardAny.setOnClickListener {
            selectType(null)
        }
    }
    private fun selectType(type: String?) {

        currentType = type

        binding.cardMens.strokeColor =
            if (type == "MENS") PRIMARY_COLOR else GRAY_COLOR

        binding.cardMens.setCardBackgroundColor(
            if (type == "MENS")
                ContextCompat.getColor(requireContext(), R.color.lightPrimaryColor)
            else
                ContextCompat.getColor(requireContext(), R.color.white)
        )

        binding.cardBeauty.strokeColor =
            if (type == "BEAUTY") PRIMARY_COLOR else GRAY_COLOR

        binding.cardBeauty.setCardBackgroundColor(
            if (type == "BEAUTY")
                ContextCompat.getColor(requireContext(), R.color.lightPrimaryColor)
            else
                ContextCompat.getColor(requireContext(), R.color.white)
        )

        binding.cardUnisex.strokeColor =
            if (type == "UNISEX") PRIMARY_COLOR else GRAY_COLOR

        binding.cardUnisex.setCardBackgroundColor(
            if (type == "UNISEX")
                ContextCompat.getColor(requireContext(), R.color.lightPrimaryColor)
            else
                ContextCompat.getColor(requireContext(), R.color.white)
        )

        binding.cardAny.strokeColor =
            if (type == null) PRIMARY_COLOR else GRAY_COLOR

        binding.cardAny.setCardBackgroundColor(
            if (type == null)
                ContextCompat.getColor(requireContext(), R.color.lightPrimaryColor)
            else
                ContextCompat.getColor(requireContext(), R.color.white)
        )
    }

    private fun selectRating(rating: Double?) {

        currentMinRating = rating

        val selectedTextColor =
            ContextCompat.getColor(requireContext(), R.color.black)

        val unselectedTextColor =
            ContextCompat.getColor(requireContext(), R.color.gray)

        val selectedBackground =
            ContextCompat.getColor(requireContext(), R.color.lightPrimaryColor)

        val unselectedBackground =
            ContextCompat.getColor(requireContext(), R.color.white)

        // 4★+
        binding.cardRating4.strokeColor =
            if (rating == 4.0) PRIMARY_COLOR else GRAY_COLOR

        binding.cardRating4.setCardBackgroundColor(
            if (rating == 4.0) selectedBackground else unselectedBackground
        )

        binding.tvRating4.setTextColor(
            if (rating == 4.0) selectedTextColor else unselectedTextColor
        )

        // 3★+
        binding.cardRating3.strokeColor =
            if (rating == 3.0) PRIMARY_COLOR else GRAY_COLOR

        binding.cardRating3.setCardBackgroundColor(
            if (rating == 3.0) selectedBackground else unselectedBackground
        )

        binding.tvRating3.setTextColor(
            if (rating == 3.0) selectedTextColor else unselectedTextColor
        )

        // Any
        binding.cardRatingAny.strokeColor =
            if (rating == null) PRIMARY_COLOR else GRAY_COLOR

        binding.cardRatingAny.setCardBackgroundColor(
            if (rating == null) selectedBackground else unselectedBackground
        )

        binding.tvRatingAny.setTextColor(
            if (rating == null) selectedTextColor else unselectedTextColor
        )
    }
    companion object {
        const val TAG = "FilterBottomSheet"
        const val ARG_QUERY = "query"

        fun newInstance(query: String): FilterBottomSheet {
            return FilterBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_QUERY, query)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}