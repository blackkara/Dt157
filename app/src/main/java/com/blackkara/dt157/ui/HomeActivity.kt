package com.blackkara.dt157.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blackkara.dt157.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val fragment = SearchFragment()
        transaction.replace(R.id.frameLayoutScene, fragment)
        transaction.commit()
    }
}
