package com.p2p.application.activity

import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.p2p.application.R
import com.p2p.application.di.SessionEventBus
import com.p2p.application.util.AppConstant
import com.p2p.application.util.AppConstant.Companion.SESSION_ERROR
import com.p2p.application.util.LoadingUtils.Companion.showSessionDialog
import com.p2p.application.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private var isDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sessionManager = SessionManager(this)
        selectedType = sessionManager.getLoginType().orEmpty()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frameContainerMain) as NavHostFragment
        val navController = navHostFragment.navController
        checkDeveloperOption(navController)
        observeSessionExpiration(navController)
    }

    private fun checkDeveloperOption(navController: NavController) {
//        val adbEnabled = Settings.Global.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0)
        val adbEnabled = 0
        startDestination(navController,adbEnabled)
    }

    private fun observeSessionExpiration(navController: NavController) {
        lifecycleScope.launch {
            SessionEventBus.sessionExpiredFlow.collectLatest {
                if (!isDialogShown) {
                    isDialogShown = true
                    showSessionDialog(this@MainActivity,SESSION_ERROR,navController)
                }
            }
        }
    }

    private fun startDestination(navController: NavController, adbEnabled: Int) {
        val navGraph = navController.navInflater.inflate(R.navigation.main_graph)
        if (adbEnabled ==1){
            navGraph.setStartDestination(R.id.developerFragment)
        }else{
            if (sessionManager.getIsLogin()?:false){
                if (selectedType.equals(AppConstant.USER,true) || selectedType.equals(AppConstant.AGENT,true)|| selectedType.equals(AppConstant.MASTER_AGENT,true)) {
                    if (sessionManager.getIsPin()){
                        navGraph.setStartDestination(R.id.userWelcomeFragment)
                    }else{
                        navGraph.setStartDestination(R.id.secretCodeFragment)
                    }
                }else{
                    navGraph.setStartDestination(R.id.userWelcomeFragment)
                }
            }else{
                navGraph.setStartDestination(R.id.accountTypeFragment)
            }
        }

        navController.graph = navGraph
    }


}