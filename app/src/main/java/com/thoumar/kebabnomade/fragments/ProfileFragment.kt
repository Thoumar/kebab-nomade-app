package com.thoumar.kebabnomade.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.thoumar.kebabnomade.R
import com.thoumar.kebabnomade.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_profile, container, false)

        Glide.with(requireContext())
            .load(FirebaseAuth.getInstance().currentUser?.photoUrl)
            .circleCrop()
            .into(v.image_view_profile_picture)

        v.btn_sign_out.setOnClickListener {
            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    activity?.finish()
                }
        }

        v.btn_delete_account.setOnClickListener {

            val balloon = Balloon.Builder(requireContext())
                .setLayout(R.layout.dialog_delete_account)
                .setWidthRatio(0.8f)
                .setHeight(280)
                .setCornerRadius(4f)
                .setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorBackgroundNormal
                    )
                )
                .setBalloonAnimation(BalloonAnimation.CIRCULAR)
                .setLifecycleOwner(this)
                .build()

            balloon.show(v.profile_view_container)

            val cancelBtn: Button = balloon.getContentView().findViewById(R.id.button_cancel)
            val confirmBtn: Button = balloon.getContentView().findViewById(R.id.button_confirm)

            cancelBtn.setOnClickListener { balloon.dismiss() }

            confirmBtn.setOnClickListener {
                AuthUI.getInstance()
                    .delete(requireContext())
                    .addOnCompleteListener {
                        // ...
                    }
            }
        }
        return v
    }
}