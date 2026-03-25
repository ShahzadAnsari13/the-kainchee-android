package com.thekainchee.user.presentation.dashboard.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.thekainchee.user.presentation.dashboard.home.tabs.fragment.AllParlourFragment
import com.thekainchee.user.presentation.dashboard.home.tabs.fragment.BeautyParlourFragment
import com.thekainchee.user.presentation.dashboard.home.tabs.fragment.MensParlourFragment
import com.thekainchee.user.presentation.dashboard.home.tabs.fragment.UnisexParlourFragment

class HomeTabsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment){
    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> return AllParlourFragment()
            1 -> return MensParlourFragment()
            2 -> return BeautyParlourFragment()
            3 -> return UnisexParlourFragment()
            else -> return AllParlourFragment()
        }
    }

    override fun getItemCount(): Int {
        return 4
    }

}