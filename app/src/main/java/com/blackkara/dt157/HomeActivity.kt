package com.blackkara.dt157

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayoutScene, SearchFragment.newInstance())
        transaction.commit()
    }
}
