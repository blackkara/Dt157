package com.blackkara.dt157.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blackkara.dt157.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tag = SearchFragment::class.java.simpleName
        if(supportFragmentManager.findFragmentByTag(tag) == null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayoutScene, SearchFragment(), tag)
            transaction.commit()
        }
    }
}
