package com.hfad.agendax.ui.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.hfad.agendax.R
import android.content.ActivityNotFoundException

import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar


class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = super.onCreateView(inflater, container, savedInstanceState)
        return view
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val feedBackPreference = findPreference<Preference>("feedback_preference")
        feedBackPreference?.setOnPreferenceClickListener(this)
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            "feedback_preference" -> {

                try {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("mailto:" + "ezioblaze999@gmail.com" + "?subject= App Feedback: " +
                        getString(R.string.app_name))
                    )
                    intent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback")
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Snackbar.make(view!!, "Unable to send feedback", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        return false
    }


}