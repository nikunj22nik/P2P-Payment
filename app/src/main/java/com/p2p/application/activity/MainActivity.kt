package com.p2p.application.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.Gson
import com.p2p.application.R
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.model.countrymodel.Data
import com.p2p.application.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            navGraph.setStartDestination(R.id.accountTypeFragment)
        }
        navController.graph = navGraph
    }

    fun countryListApi(onResult: (MutableList<Data>) -> Unit) {
        // Show the loading indicator
//        BaseApplication.showMe(this)
        // Launch the coroutine to perform the API call
//        lifecycleScope.launch {
//            mealRoutineViewModel.userPreferencesApi { networkResult ->
//                // Dismiss the loading indicator
//                BaseApplication.dismissMe()
//                val mealRoutineList = mutableListOf<MealRoutineModelData>()
//                // Return the result through the callback
//                onResult(mealRoutineList)
//            }
//        }
    }
}