package com.p2p.application.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.p2p.application.R
import com.p2p.application.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        selectedType = sessionManager.getLoginType().orEmpty()
        startDestination()
    }
    private fun startDestination() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frameContainerMain) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.main_graph)
        if (sessionManager.getIsLogin()?:false){
            navGraph.setStartDestination(R.id.userWelcomeFragment)
        }else{
            navGraph.setStartDestination(R.id.QRFragment)
        }
        navController.graph = navGraph
    }

}