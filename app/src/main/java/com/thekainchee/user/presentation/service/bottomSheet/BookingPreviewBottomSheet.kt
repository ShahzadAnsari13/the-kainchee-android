package com.thekainchee.user.presentation.service.bottomSheet

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.databinding.BottomSheetBookingPreviewBinding
import com.thekainchee.user.presentation.service.adapter.BookingPreviewAdapter
import com.thekainchee.user.presentation.service.fragment.ServiceListFragment
import com.thekainchee.user.presentation.service.model.BookingPreviewUiModel
import com.thekainchee.user.presentation.service.viewModel.ServiceViewModel
import kotlin.getValue

class BookingPreviewBottomSheet : BottomSheetDialogFragment() {
    private var onChangesDone : (() -> Unit)? = null
    companion object {

        private const val BOOKING_PREVIEW = "BOOKING_PREVIEW"

        fun newInstance(
            data: BookingPreviewUiModel,
            onChangesDone : (() -> Unit)? = null
        ): BookingPreviewBottomSheet {

            val fragment = BookingPreviewBottomSheet()
            fragment.onChangesDone = onChangesDone
            val bundle = Bundle().apply {
                putParcelable(BOOKING_PREVIEW, data)
            }

            fragment.arguments = bundle

            return fragment
        }
    }
    private var _binding: BottomSheetBookingPreviewBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BookingPreviewAdapter
    private var bookingPreviewData: BookingPreviewUiModel? = null

    private var totalPrice: Int? = null
    private var  totalDuration: Int? = null
    private var totalServices: Int? = null
    private var hasChanges = false
    private val serviceViewModel : ServiceViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bookingPreviewData =
            arguments?.getParcelable(BOOKING_PREVIEW)
        _binding = BottomSheetBookingPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        totalPrice = bookingPreviewData?.totalPrice
        totalServices = bookingPreviewData?.totalServices
        totalDuration = bookingPreviewData?.totalDuration

        updateSummaryUi()

        adapter = BookingPreviewAdapter(
            onRemoveClick = { item ->

                bookingPreviewData?.parlourId?.let {parlourId->
                    serviceViewModel.removeService(parlourId,item.id);
                    val updatedList =
                        adapter.currentList.toMutableList()

                    updatedList.remove(item)
                    if(updatedList.isEmpty()){
                        dismiss()
                        return@BookingPreviewAdapter
                    }
                    adapter.submitList(updatedList)
                    totalPrice = totalPrice?.minus(item.price)
                    totalDuration = totalDuration?.minus(item.duration)
                    totalServices =  (totalServices ?: 1) - 1

                    updateSummaryUi()
                    hasChanges = true
                }
                    ?: Snackbar.make(binding.root,"Something went wrong",Snackbar.LENGTH_SHORT).show()


            }
        )
        binding.rvSelectedServices.apply {

            layoutManager =
                LinearLayoutManager(requireContext())

            adapter =
                this@BookingPreviewBottomSheet.adapter
        }

        adapter.submitList(
            bookingPreviewData?.services ?: emptyList()
        )
    }
    fun formatDuration(totalMinutes: Int): String {

        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {

            hours > 0 && minutes > 0 ->
                "${hours}h ${minutes}m"

            hours > 0 ->
                "${hours}h"

            else ->
                "${minutes}m"
        }
    }

    private fun updateSummaryUi(){

        binding.tvTotalPrice.text =
            "₹${totalPrice}"

        binding.tvSummary.text =
            totalDuration?.let {
                "• ${formatDuration(it)} • ${totalServices} Services"
            }
                ?: "• ${totalServices} Services"
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if(hasChanges){
            onChangesDone?.invoke()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}