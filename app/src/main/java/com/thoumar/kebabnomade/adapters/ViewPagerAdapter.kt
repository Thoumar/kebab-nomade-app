package com.thoumar.kebabnomade.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.thoumar.kebabnomade.fragments.HomeFragment
import com.thoumar.kebabnomade.fragments.MapFragment
import com.thoumar.kebabnomade.fragments.ProfileFragment


class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return HomeFragment()
            1 -> return MapFragment()
            2 -> return ProfileFragment()
        }
        return HomeFragment()
    }

    override fun getCount(): Int {
        return 3
    }
}