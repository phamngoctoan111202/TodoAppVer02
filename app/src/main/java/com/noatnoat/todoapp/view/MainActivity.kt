package com.noatnoat.todoapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import com.noatnoat.todoapp.R

import com.noatnoat.todoapp.view.fragment.HomeFragment
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    @SuppressLint("NotifyDataSetChanged")
    @OptIn(UnstableApi::class) override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }
}