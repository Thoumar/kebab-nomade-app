package com.thoumar.kebabnomade.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.adapters.ViewPagerAdapter
import com.thoumar.kebabnomade.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val RC_SIGN_IN = 123
    }

    private val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build()
    )

    private var mViewPagerAdapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        loadFragment(HomeFragment())
        bottom_navigation.setOnNavigationItemSelectedListener(this)

        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        view_pager.adapter = mViewPagerAdapter

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> bottom_navigation.menu.findItem(R.id.navigation_home).isChecked = true
                    1 -> bottom_navigation.menu.findItem(R.id.navigation_map).isChecked = true
                    2 -> bottom_navigation.menu.findItem(R.id.navigation_profile).isChecked = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        if (FirebaseAuth.getInstance().currentUser == null) {
            showSignInOptions()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_home -> view_pager.currentItem = 0
            R.id.navigation_map -> view_pager.currentItem = 1
            R.id.navigation_profile -> view_pager.currentItem = 2
        }
        return true
    }

    private fun loadFragment(fragment: Fragment?): Boolean {

        if (fragment != null) {
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(R.id.container_frame_layout, fragment)
            ft.commit()
            return true
        }
        return false
    }

    private fun showSignInOptions() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.logo)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
            } else {
                // Sign in failed
                Toast.makeText(this, response?.error.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}