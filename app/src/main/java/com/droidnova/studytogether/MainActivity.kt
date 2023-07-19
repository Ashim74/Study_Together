package com.droidnova.studytogether

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.droidnova.studytogether.database.ProgressDatabase
import com.droidnova.studytogether.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.rgMainOptions?.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == binding?.rbStudyPlanner?.id) {
               findNavController(R.id.nav_host_fragment).navigate(R.id.studyPlanner)
            } else if(checkedId == binding?.rbYourProgress?.id){
                findNavController(R.id.nav_host_fragment).navigate(R.id.yourProgress)
            }else{
                findNavController(R.id.nav_host_fragment).navigate(R.id.studyMaterial)
            }
        }

    }
}