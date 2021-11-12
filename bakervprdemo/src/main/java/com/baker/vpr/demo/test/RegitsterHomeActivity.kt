package com.baker.vpr.demo.test

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.baker.vpr.demo.R
import com.baker.vpr.demo.base.BaseActivity
import com.baker.vpr.demo.databinding.ActivityRegisterHomeBinding

class RegitsterHomeActivity : BaseActivity<ActivityRegisterHomeBinding>() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setSupportActionBar(mBinding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_base)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        mBinding.toolbar.title="声纹注册"

//        mBinding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_base)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}