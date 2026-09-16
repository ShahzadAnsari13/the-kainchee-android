package com.thekainchee.user.presentation.booking.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
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
import com.thekainchee.user.presentation.booking.state.BookingEvent
import com.thekainchee.user.presentation.booking.state.CreateBookingState
import com.thekainchee.user.presentation.booking.state.SlotState
import com.thekainchee.user.presentation.booking.state.StaffState
import com.thekainchee.user.presentation.booking.viewModel.BookingViewModel
import com.thekainchee.user.presentation.common.extensions.hide
import com.thekainchee.user.presentation.common.extensions.show
import com.thekainchee.user.presentation.common.state.StateViewData
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
        val firstService = bookingPreviewData.services.firstOrNull() ?: return
        binding.tvServiceTitle.text ="${firstService.name} + ${bookingPreviewData.totalServices-1}"
        binding.tvServiceInfo.text = "⏱ ${formatDuration(bookingPreviewData.totalDuration)} • ₹${bookingPreviewData.totalPrice}"
        Glide.with(this@BookingSlotFragment)
            .load(firstService.image)
            .placeholder(R.drawable.ic_oops)
            .into(binding.imgService)
        hideBottomStrip()
        setupRecyclerViews()
        observeUiStates()
        setupSocketListeners()
        serviceViewModel.loadSelectedServices(bookingPreviewData.parlourId)


        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            showNoInternetState{
                if (!NetworkUtils.isInternetAvailable(requireContext())) {
                    Snackbar.make(
                        binding.root,
                        "No Internet Connection",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    binding.stateView.hide()
                    viewModel.getParlourStaffs(bookingPreviewData.parlourId)
                }
            }
        }else{
            viewModel.getParlourStaffs(bookingPreviewData.parlourId)
        }

        binding.btnContinue.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(binding.root,"Check Internet Connection", Snackbar.LENGTH_SHORT).show()
            }else{
                if (serviceIds.isNullOrEmpty()) {
                    return@setOnClickListener
                }
                when(viewModel.createBookingState.value){

                    is CreateBookingState.Success -> {

                        val bottomSheet =
                            PaymentMethodBottomSheet()
                        bottomSheet.onPaymentSuccess = { bookingId ->

                            findNavController().navigate(
                                BookingSlotFragmentDirections
                                    .actionBookingSlotFragmentToBookingSuccessFragment(
                                        bookingId
                                    )
                            )
                        }

                        bottomSheet.show(
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

    }

    private fun setupRecyclerViews(){

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
    }
    private fun observeUiStates() {
        observeStaff()
        observeSlots()
        observeSelectedServices()
        observeCreateBooking()
        observeBookingEvents()
    }
    private fun observeStaff() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.staffState.collect {state->
                    when(state){
                        is StaffState.Idle -> {

                        }
                        is StaffState.Loading -> {
                            showStaffLoading()
                        }
                        is StaffState.Success -> {
                            hideStaffLoading()
                            binding.rvStaff.isVisible = true
                            binding.mainContent.isVisible = true
                            bookingStaffAdapter.submitList(state.data)
                            binding.layoutDateContainer.isVisible = true
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
                            hideStaffLoading()
                            binding.layoutStaffContainer.isVisible = false
                            Toast.makeText(
                                requireContext(),
                                "No staff available at the moment.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is StaffState.Error -> {
                            hideStaffLoading()
                            binding.layoutStaffContainer.isVisible = false
                            binding.mainContent.isVisible = false
                            showStaffLoadError {
                                withInternet {
                                    binding.stateView.hide()
                                    viewModel.getParlourStaffs(bookingPreviewData.parlourId)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun observeSlots() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.slotState.collect {state->
                    when(state){
                        is SlotState.Idle -> {

                        }
                        is SlotState.Loading -> {
                            showSlotLoading()
                        }
                        is SlotState.Success -> {
                            hideSlotLoading()
                            binding.rvSlots.isVisible = true
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
                            hideSlotLoading()
                            binding.rvSlots.isVisible = false
                            binding.layoutSlotContainer.isVisible = false
                            Toast.makeText(
                                requireContext(),
                                "No slots available for the selected date.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is SlotState.Error -> {
                            hideSlotLoading()
                            binding.rvSlots.isVisible = false
                            binding.layoutSlotContainer.isVisible = false
                            binding.mainContent.isVisible = false
                            showSlotLoadError {
                                withInternet {
                                    binding.stateView.hide()
                                    viewModel.getStaffSlots(
                                        staffId = selectedStaff?.id.orEmpty(),
                                        parlourId = bookingPreviewData.parlourId,
                                        date = selectedDate?.fullDate.orEmpty()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun observeSelectedServices() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                serviceViewModel.selectedServiceIds.collect{selectedIds->
                    serviceIds = selectedIds
                }
            }
        }
    }
    private fun observeCreateBooking() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
    private fun observeBookingEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookingEvent.collect { event ->
                    when(event){
                        is BookingEvent.OpenPaymentSheet -> {
                            val paymentSummary = PaymentSummary(
                                bookingId = event.booking.bookingId,
                                staffName = selectedStaff?.name.orEmpty(),
                                dateTime = "${selectedDate?.day} • ${selectedDate?.fullDate} • ${bookingSlot}",
                                amount = bookingPreviewData.totalPrice.toString()
                            )
                            val bottomSheet =
                                PaymentMethodBottomSheet.newInstance(
                                    paymentSummary
                                )

                            bottomSheet.onPaymentSuccess = { bookingId ->

                                findNavController().navigate(
                                    BookingSlotFragmentDirections
                                        .actionBookingSlotFragmentToBookingSuccessFragment(
                                            bookingId
                                        )
                                )
                            }

                            bottomSheet.show(
                                parentFragmentManager,
                                "PaymentMethodBottomSheet"
                            )

                        }
                    }
                }
            }
        }
    }
    private fun setupSocketListeners() {
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
        }
    }
    private fun showStaffLoadError(
        onRetry: () -> Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_oops,
                title = "Unable to Load Staff",
                subtitle = "We couldn't load the available staff. Please try again.",
                primaryButtonText = "Retry",
                onPrimaryClick = onRetry
            )
        )
    }
    private fun showSlotLoadError(
        onRetry: () -> Unit
    ) {
        binding.stateView.show(
            StateViewData(
                image = R.drawable.ic_oops,
                title = "Unable to Load Slots",
                subtitle = "We couldn't load the available time slots. Please try again.",
                primaryButtonText = "Retry",
                onPrimaryClick = onRetry
            )
        )
    }

    private fun withInternet(
        onConnected: () -> Unit
    ) {
        if (!NetworkUtils.isInternetAvailable(requireContext())) {
            showNoInternetState {
                if (!NetworkUtils.isInternetAvailable(requireContext())) {
                    Snackbar.make(
                        binding.root,
                        "No Internet Connection",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    binding.stateView.hide()
                    onConnected()
                }
            }
        } else {
            onConnected()
        }
    }
    private fun showNoInternetState(
        retryText: String = "Retry",
        onRetry: () -> Unit
    ){
        binding.stateView.show(
            StateViewData(
                image = R.drawable.no_internet,
                title = "No Internet Connection",
                subtitle = "Please check your internet connection and try again.",
                primaryButtonText = retryText,
                onPrimaryClick = onRetry
            )
        )
    }
    private fun showStaffLoading() {
        binding.mainContent.isVisible = true
        binding.layoutStaffContainer.isVisible = true
        binding.rvStaff.isVisible = false

        binding.shimmerStaffLayout.isVisible = true
        binding.shimmerStaffLayout.startShimmer()
    }

    private fun hideStaffLoading() {
        binding.shimmerStaffLayout.stopShimmer()
        binding.shimmerStaffLayout.isVisible = false
    }
    private fun showSlotLoading() {
        binding.layoutSlotContainer.isVisible = true
        binding.rvSlots.isVisible = false

        binding.shimmerSlotLayout.isVisible = true
        binding.shimmerSlotLayout.startShimmer()
    }

    private fun hideSlotLoading() {
        binding.shimmerSlotLayout.stopShimmer()
        binding.shimmerSlotLayout.isVisible = false
    }
    private fun showBottomStrip() {

        binding.cardBottomAction.apply {
            if (visibility == View.VISIBLE) return
            isVisible = true
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
                binding.cardBottomAction.isVisible = false
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
    private fun formatDuration(totalMinutes: Int): String {

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
    override fun onDestroyView() {
        super.onDestroyView()
        leaveStaffSlotsJoin()
        SocketManager.getSocket()?.off("slot_locked")
        SocketManager.getSocket()?.off("slot_unlocked")
        _binding =null
    }
}