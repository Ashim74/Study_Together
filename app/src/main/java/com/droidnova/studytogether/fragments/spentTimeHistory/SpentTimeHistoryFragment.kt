package com.droidnova.studytogether.fragments.spentTimeHistory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidnova.studytogether.adapters.SpentTimeHistoryAdapter
import com.droidnova.studytogether.database.ProgressDatabase
import com.droidnova.studytogether.database.ProgressEntity
import com.droidnova.studytogether.databinding.FragmentSpentTimeHistoryBinding
import kotlinx.coroutines.launch


class SpentTimeHistoryFragment : Fragment() {
    private var historyList = ArrayList<ProgressEntity>()
    private lateinit var binding: FragmentSpentTimeHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpentTimeHistoryBinding.inflate(layoutInflater,container,false)


        val databaseDao = ProgressDatabase.getDatabase(requireContext()).progressDao()
        lifecycleScope.launch {
            databaseDao.getProgress().collect{list->
                historyList.clear() // Clear the existing data
                historyList.addAll(list)
                Log.e("myTag list",list.size.toString())
                setUpHistoryRecyclerView()
            }
        }

        Log.e("myTag list",historyList.toString())


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setUpHistoryRecyclerView(){
        val historyAdapter = SpentTimeHistoryAdapter(requireContext(),historyList)
        binding.rvSpentTimeHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSpentTimeHistory.adapter = historyAdapter

    }


}