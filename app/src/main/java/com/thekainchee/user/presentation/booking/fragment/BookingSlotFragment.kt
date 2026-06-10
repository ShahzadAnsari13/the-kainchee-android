package com.thekainchee.user.presentation.booking.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentBookingSlotBinding
import com.thekainchee.user.presentation.booking.adapter.BookingDateAdapter
import com.thekainchee.user.presentation.booking.adapter.BookingSlotAdapter
import com.thekainchee.user.presentation.booking.adapter.BookingStaffAdapter
import com.thekainchee.user.presentation.payment.bottomSheet.PaymentMethodBottomSheet
import com.thekainchee.user.presentation.booking.model.CreateBookingParams
import com.thekainchee.user.presentation.booking.model.DateUiModel
import com.thekainchee.user.presentation.booking.model.PaymentSummary
import com.thekainchee.user.presentation.booking.model.SlotUiModel
import com.thekainchee.user.presentation.booking.model.StaffUiModel
import com.thekainchee.user.presentation.booking.state.CreateBookingState
import com.thekainchee.user.presentation.booking.state.SlotState
import com.thekainchee.user.presentation.booking.state.StaffState
import com.thekainchee.user.presentation.booking.viewModel.BookingViewModel
import com.thekainchee.user.presentation.service.viewModel.ServiceViewModel
import com.thekainchee.user.utils.NetworkUtils
import com.thekainchee.user.utils.socket.SocketManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import kotlin.getValue

@AndroidEntryPoint
class BookingSlotFragment : Fragment() {

    private val navArgs : BookingSlotFragmentArgs by navArgs()

    private val viewModel : BookingViewModel by viewModels()
    private val bookingPreviewData by lazy {
        navArgs.services
    }
    private lateinit var bookingStaffAdapter: BookingStaffAdapter
    private lateinit var bookingSlotAdapter: BookingSlotAdapter
    private lateinit var bookingDateAdapter: BookingDateAdapter
    private var _binding : FragmentBookingSlotBinding? = null
    private val binding get() = _binding!!
    private val today = LocalDate.now()
    private var selectedStaff: StaffUiModel? = null

    private var selectedDate: DateUiModel? = null
    private val dates = mutableListOf<DateUiModel>()
    private var currentSlots = mutableListOf<SlotUiModel>()
    private var joinedStaffId: String? = null
    private var joinedDate: String? = null
    private val  serviceViewModel : ServiceViewModel by viewModels()
    private var bookingSlot : String? =null
    private var serviceIds : List<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingSlotBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        for (i in 0..6) {

            val date = today.plusDays(i.toLong())

            dates.add(
                DateUiModel(
                    day = date.dayOfWeek.name.take(3),
                    date = date.dayOfMonth.toString(),
                    fullDate = date.toString()
                )
            )
        }
        bookingStaffAdapter = BookingStaffAdapter {staff ->
            selectedStaff = staff
            hideBottomStrip()
            leaveStaffSlotsJoin()
            viewModel.resetCreateBookingState()
            selectedDate?.let {date->

                if(!NetworkUtils.isInternetAvailable(requireContext())){
                    Snackbar.make(binding.root,"Check Internet Connection", Snackbar.LENGTH_SHORT).show()
                }else{
                    viewModel.getStaffSlots(
                        staffId = staff.id, parlourId = bookingPreviewData.parlourId, date = date.fullDate
                    )
                }

            }
        }
        bookingSlotAdapter = BookingSlotAdapter{slot->
            selectedStaff?.let { staff->
                selectedDate?.let { date->
                    val isToday = date.fullDate == LocalDate.now().toString()
                    val isTomorrow = date.fullDate == LocalDate.now().plusDays(1).toString()
                    bookingSlot = slot.time
                    if(isToday){
                        binding.tvSelectedAppointment.text = "${staff.name} • Today • ${slot.time}"
                    }else if(isTomorrow){
                        binding.tvSelectedAppointment.text = "${staff.name} • Tomorrow • ${slot.time}"
                    }else{
                        binding.tvSelectedAppointment.text = "${staff.name} • ${date.day} ${date.date} • ${slot.time}"
                    }
                }
            }
            Log.d("slot","selected")
            showBottomStrip()
            viewModel.resetCreateBookingState()

        }
        bookingDateAdapter = BookingDateAdapter { date ->
            selectedDate = date
            hideBottomStrip()
            leaveStaffSlotsJoin()
            viewModel.resetCreateBookingState()
            selectedStaff?.let {staff->
                if(!NetworkUtils.isInternetAvailable(requireContext())){
                    Snackbar.make(binding.root,"Check Internet Connection", Snackbar.LENGTH_SHORT).show()
                }else{
                    viewModel.getStaffSlots(
                        staffId = staff.id, parlourId = bookingPreviewData.parlourId, date = date.fullDate
                    )
                }
            }


        }
        bookingDateAdapter.submitList(dates)
        hideBottomStrip()
        serviceViewModel.loadSelectedServices(bookingPreviewData.parlourId)
        binding.rvStaff.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false

            )

            adapter = bookingStaffAdapter
        }

        binding.rvSlots.apply {
            layoutManager = GridLayoutManager(requireContext(),3)

            adapter = bookingSlotAdapter
        }
        binding.rvDates.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false

            )

            adapter = bookingDateAdapter
        }

        val firstService = bookingPreviewData.services.firstOrNull() ?: return
        binding.tvServiceTitle.text ="${firstService.name} + ${bookingPreviewData.totalServices-1}"
        binding.tvServiceInfo.text = "⏱ ${bookingPreviewData.totalDuration} min • ₹${bookingPreviewData.totalPrice}"
        Glide.with(this@BookingSlotFragment)
            .load(firstService.image)
            .placeholder(R.drawable.ic_oops)
            .into(binding.imgService)

        if(!NetworkUtils.isInternetAvailable(requireContext())){
            binding.layoutNoInternet.visibility = View.VISIBLE
            binding.mainContent.visibility = View.GONE
        }else{
            viewModel.getParlourStaffs(bookingPreviewData.parlourId)
        }

        binding.btnTryAgain.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.layoutNoInternet.visibility = View.VISIBLE
                binding.mainContent.visibility = View.GONE
            }else{
                viewModel.getParlourStaffs(bookingPreviewData.parlourId)
            }
        }

        binding.btnRetry.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                binding.layoutNoInternet.visibility = View.VISIBLE
                binding.mainContent.visibility = View.GONE
            }else{
                viewModel.getParlourStaffs(bookingPreviewData.parlourId)
            }

        }

        binding.btnContinue.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(binding.root,"Check Internet Connection", Snackbar.LENGTH_SHORT).show()
            }else{
                if (serviceIds.isNullOrEmpty()) {
                    return@setOnClickListener
                }
                when(val state = viewModel.createBookingState.value){

                    is CreateBookingState.Success -> {

                        PaymentMethodBottomSheet().show(
                            parentFragmentManager,
                            "PaymentMethodBottomSheet"
                        )
                        Toast.makeText(requireContext(),"Booking Created Start Payment",Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        selectedStaff?.let {staff->
                            selectedDate?.let { date ->
                                serviceIds?.let { serviceIds->
                                    bookingSlot?.let { slot->
                                        viewModel.createBooking(CreateBookingParams(parlourId = bookingPreviewData.parlourId,staffId = staff.id, bookingDate = date.fullDate, serviceIds = serviceIds,slotStartTime = slot))
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        SocketManager.getSocket()?.on("slot_locked") { args ->
            val data = args[0] as org.json.JSONObject

            val staffId = data.getString("staffId")
            val bookingDate = data.getString("bookingDate")
            val slotStartTime = data.getString("slotStartTime")
            val slotEndTime = data.getString("slotEndTime")

            val startMinutes = toMinutes(slotStartTime)
            val endMinutes = toMinutes(slotEndTime)

            if (
                staffId != selectedStaff?.id ||
                bookingDate != selectedDate?.fullDate
            ) {
                return@on
            }
            requireActivity().runOnUiThread {

                currentSlots.removeAll { slot ->

                    val slotMinutes = toMinutes(slot.time)

                    slotMinutes >= startMinutes &&
                            slotMinutes < endMinutes
                }

                bookingSlotAdapter.submitSlots(
                    currentSlots.toList()
                )
            }

        }

        SocketManager.getSocket()?.on("slot_unlocked") { args ->
            val data = args[0] as org.json.JSONObject

            val staffId = data.getString("staffId")
            val bookingDate = data.getString("bookingDate")
            if (
                staffId != selectedStaff?.id ||
                bookingDate != selectedDate?.fullDate
            ) {
                return@on
            }
            selectedStaff?.let {staff->
                if(!NetworkUtils.isInternetAvailable(requireContext())){
                    Snackbar.make(binding.root,"Check Internet Connection", Snackbar.LENGTH_SHORT).show()
                }else{
                    selectedDate?.let { date ->
                        viewModel.getStaffSlots(
                            staffId = staff.id,
                            parlourId = bookingPreviewData.parlourId,
                            date = date.fullDate
                        )
                    }
                }
            }
            Log.d("SOCKET", "UNLOCKED = $data")
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    viewModel.staffState.collect {state->
                        when(state){
                            is StaffState.Idle -> {

                            }
                            is StaffState.Loading -> {
                                binding.layoutNoInternet.visibility = View.GONE
                                binding.mainContent.visibility = View.VISIBLE
                                binding.layoutStaffContainer.visibility = View.VISIBLE
                                binding.rvStaff.visibility = View.GONE
                                binding.shimmerStaffLayout.visibility = View.VISIBLE
                                binding.shimmerStaffLayout.startShimmer()
                            }
                            is StaffState.Success -> {
                                binding.shimmerStaffLayout.stopShimmer()
                                binding.shimmerStaffLayout.visibility = View.GONE
                                binding.rvStaff.visibility = View.VISIBLE
                                bookingStaffAdapter.submitList(state.data)
                                binding.layoutDateContainer.visibility = View.VISIBLE
                                val firstStaff = state.data.firstOrNull()
                                val firstDate = dates.firstOrNull()
                                firstStaff?.let {staff->
                                    firstDate?.let {date->
                                        viewModel.getStaffSlots(
                                            staffId = staff.id, parlourId = bookingPreviewData.parlourId, date = date.fullDate
                                        )
                                        selectedStaff = staff
                                        selectedDate = date
                                    }

                                }

                            }
                            is StaffState.Empty -> {
                                binding.shimmerStaffLayout.stopShimmer()
                                binding.shimmerStaffLayout.visibility = View.GONE
                                binding.layoutStaffContainer.visibility = View.GONE
                            }
                            is StaffState.Error -> {
                                binding.shimmerStaffLayout.stopShimmer()
                                binding.shimmerStaffLayout.visibility = View.GONE
                                binding.layoutStaffContainer.visibility = View.GONE
                                binding.mainContent.visibility = View.GONE
                                binding.errorLayout.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                launch {
                    viewModel.slotState.collect {state->
                        when(state){
                            is SlotState.Idle -> {

                            }
                            is SlotState.Loading -> {
                                Log.d("slot","loading")
                                binding.layoutSlotContainer.visibility = View.VISIBLE
                                binding.rvSlots.visibility = View.GONE
                                binding.shimmerSlotLayout.visibility = View.VISIBLE
                                binding.shimmerSlotLayout.startShimmer()
                            }
                            is SlotState.Success -> {
                                Log.d("slot","success")
                                binding.shimmerSlotLayout.stopShimmer()
                                binding.shimmerSlotLayout.visibility = View.GONE
                                binding.rvSlots.visibility = View.VISIBLE
                                currentSlots.clear()
                                currentSlots.addAll(state.data)
                                bookingSlotAdapter.submitSlots(currentSlots.toList())
                                SocketManager.getSocket()?.emit(
                                    "join_staff_slots",
                                    org.json.JSONObject().apply {
                                        put("staffId", selectedStaff?.id)
                                        put("bookingDate", selectedDate?.fullDate)
                                    }
                                )
                                joinedStaffId = selectedStaff?.id
                                joinedDate = selectedDate?.fullDate
                            }
                            is SlotState.Empty -> {
                                Log.d("slot","empty")
                                binding.shimmerSlotLayout.stopShimmer()
                                binding.shimmerSlotLayout.visibility = View.GONE
                                binding.rvSlots.visibility = View.GONE
                                binding.layoutSlotContainer.visibility = View.GONE
                            }
                            is SlotState.Error -> {
                                Log.d("slot","error")
                                binding.shimmerSlotLayout.stopShimmer()
                                binding.shimmerSlotLayout.visibility = View.GONE
                                binding.rvSlots.visibility = View.GONE
                                binding.layoutSlotContainer.visibility = View.GONE
                            }
                        }
                    }
                }
                launch{
                    serviceViewModel.selectedServiceIds.collect{selectedIds->
                        serviceIds = selectedIds
                    }
                }

                launch {
                    viewModel.createBookingState.collect { state->
                        when(state){
                            is CreateBookingState.Idle -> {

                            }
                            is CreateBookingState.Loading -> {
                                binding.btnContinue.text = ""
                                binding.btnContinue.isEnabled = false
                                binding.progressStatusCheck.visibility = View.VISIBLE
                            }
                            is CreateBookingState.Success -> {
                                    //open payment page here
                                    binding.progressStatusCheck.visibility = View.GONE
                                    binding.btnContinue.text = "Continue to Payment"
                                    binding.btnContinue.isEnabled = true
                                val paymentSummary = PaymentSummary(
                                    bookingId = state.booking.bookingId,
                                    staffName = selectedStaff?.name.orEmpty(),
                                    dateTime = "${selectedDate?.day} • ${selectedDate?.fullDate} • ${bookingSlot}",
                                    amount = bookingPreviewData.totalPrice.toString()
                                )
                                PaymentMethodBottomSheet.newInstance(paymentSummary)
                                    .show(
                                        parentFragmentManager,
                                        "PaymentMethodBottomSheet"
                                    )
                            }
                            is CreateBookingState.Error -> {
                                binding.progressStatusCheck.visibility = View.GONE
                                binding.btnContinue.text = "Continue to Payment"
                                binding.btnContinue.isEnabled = true
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showBottomStrip() {

        binding.cardBottomAction.apply {

            Log.d("strip","show called")
            if (visibility == View.VISIBLE) return
            Log.d("strip","visible = ${visibility}")
            visibility = View.VISIBLE
            alpha = 0f
            translationX =300f

            animate()
                .translationX(0f)
                .alpha(1f)
                .setDuration(350)
                .setInterpolator(OvershootInterpolator())
                .start()
        }
    }
    private fun hideBottomStrip() {

        binding.cardBottomAction.animate()
            .translationX(300f)
            .alpha(0f)
            .setDuration(250)
            .withEndAction {
                binding.cardBottomAction.visibility = View.GONE
            }
            .start()
    }

    private fun leaveStaffSlotsJoin(){
        joinedStaffId?.let { staffId ->
            joinedDate?.let { bookingDate ->

                SocketManager.getSocket()?.emit(
                    "leave_staff_slots",
                    JSONObject().apply {
                        put("staffId", staffId)
                        put("bookingDate", bookingDate)
                    }
                )
            }
        }
        joinedStaffId = null
        joinedDate = null
    }
    private fun toMinutes(time: String): Int {
        val parts = time.split(":")
        return parts[0].toInt() * 60 + parts[1].toInt()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        leaveStaffSlotsJoin()
        SocketManager.getSocket()?.off("slot_locked")
        SocketManager.getSocket()?.off("slot_unlocked")
        _binding =null
    }
}