package com.thoumar.kebabnomade.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        Glide

        v.btn_menu.setOnClickListener {

        }

        v.btn_sign_out.setOnClickListener {
            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener {
                    // ...
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    activity?.finish()
                }
        }

        v.btn_delete_account.setOnClickListener {
            val balloon = createBalloon(requireContext()) {
                setArrowSize(10)
                setWidthRatio(1.0f)
                setHeight(65)
                setArrowPosition(0.7f)
                setCornerRadius(4f)
                setAlpha(0.9f)
                setText("Are you sure you want to delete your account ?")
                setIconDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_profile))
                setBackgroundColorResource(R.color.colorPrimary)
                setBalloonAnimation(BalloonAnimation.FADE)
                setLifecycleOwner(lifecycleOwner)
            }

//            AuthUI.getInstance()
//                .delete(requireContext())
//                .addOnCompleteListener {
//                    // ...
//
//                }
        }

        return v
    }

    val providers = arrayListOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
        AuthUI.IdpConfig.GoogleBuilder().build(),
        AuthUI.IdpConfig.FacebookBuilder().build()
    )


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

}