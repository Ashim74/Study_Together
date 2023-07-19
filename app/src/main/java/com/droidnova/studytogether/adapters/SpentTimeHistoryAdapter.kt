package com.droidnova.studytogether.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.droidnova.studytogether.database.ProgressEntity
import com.droidnova.studytogether.databinding.ItemSpentTimeHistoryBinding

open class SpentTimeHistoryAdapter(private val context: Context,
                                    private val historyList:ArrayList<ProgressEntity>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(ItemSpentTimeHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var item = historyList[position]
        if (holder is MyViewHolder){
            holder.date.text = item.id.toString()
            holder.spentTime.text = item.timeSpentDaily
        }
    }

    class MyViewHolder(private val binding: ItemSpentTimeHistoryBinding):RecyclerView.ViewHolder(binding.root){
         var date = binding.tvDateHistory
         var spentTime = binding.tvSpentTimeHistory
    }
}