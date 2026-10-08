package com.thekainchee.user.presentation.search.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentSearchBinding
import com.thekainchee.user.presentation.parlour.ParlourActivity
import com.thekainchee.user.presentation.search.adapter.SearchResultAdapter
import com.thekainchee.user.presentation.search.state.SearchState
import com.thekainchee.user.presentation.search.viewModel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import android.view.inputmethod.EditorInfo
import com.google.android.material.snackbar.Snackbar
import com.thekainchee.user.utils.NetworkUtils
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.thekainchee.user.presentation.dashboard.state.LocationUiState
import com.thekainchee.user.presentation.search.bottomSheet.FilterBottomSheet

import com.thekainchee.user.presentation.dashboard.viewModel.LocationViewModel
import com.thekainchee.user.presentation.search.model.SearchParlourUiModel

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by activityViewModels()
    private val locationViewModel: LocationViewModel by activityViewModels()
    private var currentLat: Double? = null
    private var currentLng: Double? = null
    private var resultCount = 0
    private lateinit var searchResultAdapter: SearchResultAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeSearchState()
        setupClickListeners()
        setupPagination()
        setupSearch()
        observeLocation()
    }
    private fun setupRecyclerView() {

        searchResultAdapter = SearchResultAdapter(
            parlours = emptyList()
        ) { parlour ->
            openParlour(parlour)
        }

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchResultAdapter
        }
    }
    private fun observeSearchState() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.searchState.collect { state ->

                when (state) {

                    SearchState.Idle -> {
                        binding.idleState.root.visibility = View.VISIBLE
                        binding.loadingState.root.visibility = View.GONE
                        binding.emptyState.root.visibility = View.GONE
                        binding.errorState.root.visibility = View.GONE
                        binding.rvSearchResults.visibility = View.GONE
                        binding.tvResultCount.visibility = View.GONE
                        binding.tvSortLabel.visibility = View.GONE
                        binding.tvSortValue.visibility = View.GONE
                        binding.ivSortArrow.visibility = View.GONE
                    }

                    SearchState.Loading -> {
                        resultCount = 0
                        binding.idleState.root.visibility = View.GONE
                        binding.loadingState.root.visibility = View.VISIBLE
                        binding.loadingState.root.startShimmer()
                        binding.emptyState.root.visibility = View.GONE
                        binding.errorState.root.visibility = View.GONE
                        binding.rvSearchResults.visibility = View.GONE
                        binding.tvResultCount.visibility = View.GONE
                        binding.tvSortLabel.visibility = View.GONE
                        binding.tvSortValue.visibility = View.GONE
                        binding.ivSortArrow.visibility = View.GONE
                    }

                    is SearchState.Success -> {
                        binding.idleState.root.visibility = View.GONE
                        binding.loadingState.root.hideShimmer()
                        binding.loadingState.root.visibility = View.GONE
                        binding.emptyState.root.visibility = View.GONE
                        binding.errorState.root.visibility = View.GONE
                        binding.rvSearchResults.visibility = View.VISIBLE
                        binding.tvResultCount.visibility = View.VISIBLE
                        binding.tvSortLabel.visibility = View.VISIBLE
                        binding.tvSortValue.visibility = View.VISIBLE
                        binding.ivSortArrow.visibility = View.VISIBLE
                        resultCount = state.parlours.size

                        binding.tvResultCount.text =
                            "$resultCount results found"
                        searchResultAdapter.updateData(
                            state.parlours
                        )
                    }

                    SearchState.Empty -> {
                        binding.idleState.root.visibility = View.GONE
                        binding.loadingState.root.hideShimmer()
                        binding.loadingState.root.visibility = View.GONE
                        binding.rvSearchResults.visibility = View.GONE
                        binding.emptyState.root.visibility = View.VISIBLE
                        binding.errorState.root.visibility = View.GONE
                        binding.tvResultCount.visibility = View.GONE
                        binding.tvSortLabel.visibility = View.GONE
                        binding.tvSortValue.visibility = View.GONE
                        binding.ivSortArrow.visibility = View.GONE
                    }

                    is SearchState.Error -> {
                        binding.idleState.root.visibility = View.GONE
                        binding.loadingState.root.hideShimmer()
                        binding.loadingState.root.visibility = View.GONE
                        binding.rvSearchResults.visibility = View.GONE
                        binding.emptyState.root.visibility = View.GONE
                        binding.errorState.root.visibility = View.VISIBLE
                        binding.tvResultCount.visibility = View.GONE
                        binding.tvSortLabel.visibility = View.GONE
                        binding.tvSortValue.visibility = View.GONE
                        binding.ivSortArrow.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun observeLocation() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                locationViewModel.location.collect { state ->

                    when (state) {

                        is LocationUiState.Success -> {

                            if(currentLat != state.address.latitude || currentLng != state.address.longitude){
                                viewModel.setLatLng(
                                    state.address.latitude,
                                    state.address.longitude
                                )
                                currentLat = state.address.latitude
                                currentLng = state.address.longitude
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
    private fun setupClickListeners() {

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.emptyState.btnClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.clearSearch()
        }
        binding.errorState.btnRetry.setOnClickListener {
            if(!NetworkUtils.isInternetAvailable(requireContext())){
                Snackbar.make(requireView(), "No internet connection", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.searchParlours(query)
            }
        }
        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text?.clear()
            viewModel.clearSearch()
        }
        binding.ivFilter.setOnClickListener {

            val query = binding.etSearch.text
                .toString()
                .trim()

            if (query.isEmpty()) {
                Snackbar.make(requireView(),"Please enter a search query first",Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FilterBottomSheet
                .newInstance(query)
                .show(
                    parentFragmentManager,
                    FilterBottomSheet.TAG
                )
        }
    }
    private fun openParlour(item: SearchParlourUiModel) {
        startActivity(
            Intent(requireContext(), ParlourActivity::class.java).apply {
                putExtra("parlourId", item.id)
                putExtra("distance", item.distance.toString())
            }
        )
    }

    private fun setupPagination() {

        binding.rvSearchResults.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {

                override fun onScrolled(
                    recyclerView: RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager =
                        recyclerView.layoutManager as LinearLayoutManager

                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem =
                        layoutManager.findLastVisibleItemPosition()

                    if (
                        dy > 0 &&
                        lastVisibleItem >= totalItemCount - 2
                    ) {
                        if(!NetworkUtils.isInternetAvailable(requireContext())){
                            Snackbar.make(requireView(),"No internet connection",Snackbar.LENGTH_SHORT).show()
                        }else {
                            viewModel.loadNextPage()
                        }
                    }
                }
            }
        )
    }

    private fun setupSearch() {

        binding.etSearch.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {

                    if (s.isNullOrEmpty()) {

                        binding.ivClearSearch.visibility = View.GONE
                        viewModel.clearSearch()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    binding.ivClearSearch.visibility = View.VISIBLE
                }
            }
        )

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = requireContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
                binding.etSearch.clearFocus()
                if(!NetworkUtils.isInternetAvailable(requireContext())){
                   Snackbar.make(requireView(),"No internet connection",Snackbar.LENGTH_SHORT).show()
               }else{
                   val query = binding.etSearch.text
                       .toString()
                       .trim()

                   if (query.isNotEmpty()) {
                       viewModel.searchParlours(query)
                   }
               }

                true
            } else {
                false
            }
        }
    }

    override fun onDestroyView() {
        viewModel.clearSearch()
        _binding = null
        super.onDestroyView()
    }
}