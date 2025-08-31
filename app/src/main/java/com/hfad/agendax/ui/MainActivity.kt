package com.hfad.agendax.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController

import com.google.android.gms.ads.MobileAds
import com.hfad.agendax.R
import com.hfad.agendax.databinding.ActivityMainBinding
import com.hfad.agendax.widget.TaskListWidgetProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adSetup()
        initNavigation()

    }



    private fun adSetup(){
        MobileAds.initialize(this)
    }



    private fun initNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(binding.mainNavHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        setSupportActionBar(binding.mainToolbar)

        // Customize toolbar and bottom nav bar for each screen
        navController.addOnDestinationChangedListener{_, destination,_ ->
            if(R.id.home == destination.id){
                binding.mainToolbar.visibility = View.GONE
            } else{
                binding.mainToolbar.visibility = View.VISIBLE
            }

        }

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Allows NavigationUI to support proper up navigation or the drawer layout
        // drawer menu, depending on the situation
        return findNavController(binding.mainNavHostFragment.id).navigateUp(appBarConfiguration)
    }

    override fun onPause() {
        TaskListWidgetProvider.sendRefreshBroadcast(this)
        super.onPause()
    }

}