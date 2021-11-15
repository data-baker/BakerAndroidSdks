package com.baker.vpr.demo.base

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.baker.vpr.demo.comm.Constants
import java.lang.reflect.ParameterizedType

/**
 *
 *@author xujian
 *@date 2021/11/12
 */
open class BaseActivity<_ViewBinding : ViewBinding> : AppCompatActivity() {

    protected val TAG: String by lazy {
        this.javaClass.simpleName
    }
    protected val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences(Constants.sharedPreference_name, Context.MODE_PRIVATE)
    }


    protected val mBinding: _ViewBinding by lazy {
        //使用反射得到viewbinding的class
        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        method.invoke(null, layoutInflater) as _ViewBinding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
//        setSupportActionBar(mBinding.root.findViewById(R.id.toolbar))
    }
}