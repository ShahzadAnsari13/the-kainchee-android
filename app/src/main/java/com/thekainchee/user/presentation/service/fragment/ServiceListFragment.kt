package com.thekainchee.user.presentation.service.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.thekainchee.user.R
import com.thekainchee.user.databinding.FragmentServiceListBinding
import com.thekainchee.user.presentation.parlour.ParlourActivity
import com.thekainchee.user.presentation.service.adapter.ServiceAdapter
import com.thekainchee.user.presentation.service.model.ServiceUiModel
import com.thekainchee.user.presentation.service.state.ServiceListState
import com.thekainchee.user.presentation.service.viewModel.ServiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServiceListFragment : Fragment() {

    private var _binding : FragmentServiceListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ServiceAdapter
    private val serviceViewModel : ServiceViewModel by viewModels()
    private val navArgs : ServiceListFragmentArgs by navArgs()
    private var parlourId : String? = null
    private var categoryId : String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentServiceListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as ParlourActivity)
            .setToolbarTitle(navArgs.categoryName +" Services" ?: "Services")
        parlourId = navArgs.parlourId
        categoryId = navArgs.categoryId
        parlourId?.let { parlourId ->
            categoryId?.let { categoryId ->
                serviceViewModel.getServicesByCategory(parlourId,categoryId)
            }
        }

        adapter = ServiceAdapter {item ->
            Toast.makeText(requireContext(), "Added ${item.name}", Toast.LENGTH_SHORT).show()
        }
        binding.rvServices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ServiceListFragment.adapter

        }

        val dummyList = listOf(
            ServiceUiModel("1", "Hair Cut", 199.0, 30, "Basic haircut", true),
            ServiceUiModel("2", "Beard Trim", 99.0, 15, "Clean beard", true),
            ServiceUiModel("3", "Facial", 499.0, 60, "Glow facial", false)
        )
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                launch {
                    serviceViewModel.serviceListState.collect{ state ->
                        when(state){
                            is ServiceListState.Idle -> {

                            }
                            is ServiceListState.Loading -> {
                                binding.shimmer.visibility = View.VISIBLE
                                binding.contentLayout.visibility = View.GONE
                                binding.errorLayout.visibility = View.GONE
                                binding.shimmer.startShimmer()
                            }
                            is ServiceListState.Success -> {
                                binding.shimmer.stopShimmer()
                                binding.shimmer.visibility = View.GONE
                                binding.contentLayout.visibility = View.VISIBLE
                                binding.errorLayout.visibility = View.GONE
                                adapter.submitList(state.data)
                            }
                            is ServiceListState.Empty -> {
                                binding.shimmer.stopShimmer()
                                binding.shimmer.visibility = View.GONE
                                binding.errorLayout.visibility = View.VISIBLE
                                binding.contentLayout.visibility = View.GONE
                                binding.tvEmptyTitle.text = "No Services Found"
                                binding.tvEmptySubtitle.text = "This category has no services yet."
                            }
                            is ServiceListState.Error -> {
                                binding.shimmer.stopShimmer()
                                binding.shimmer.visibility = View.GONE
                                binding.errorLayout.visibility = View.VISIBLE
                                binding.contentLayout.visibility = View.GONE
                                binding.tvEmptyTitle.text = "Something went wrong"
                                binding.tvEmptySubtitle.text = state.message
                            }
                        }
                    }
                }
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
