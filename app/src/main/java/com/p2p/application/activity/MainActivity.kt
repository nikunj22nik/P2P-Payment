package com.p2p.application.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.p2p.application.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startDestination()
    }


    private fun startDestination() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frameContainerMain) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.main_graph)
//        navGraph.setStartDestination(R.id.loginFragment)
//        navGraph.setStartDestination(R.id.accountTypeFragment)
        navGraph.setStartDestination(R.id.QRFragment)
        navController.graph = navGraph
    }
}