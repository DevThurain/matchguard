package com.thurainx.matchguard.adapters

import com.thurainx.matchguard.delegate.DateDelegate
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thurainx.matchguard.R
import com.thurainx.matchguard.data.vos.DateVO
import com.thurainx.matchguard.viewholders.DateListViewHolder

class DateListAdapter(val dateDelegate: DateDelegate) : RecyclerView.Adapter<DateListViewHolder>() {
    var mDateList: List<DateVO> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_date,parent,false)
        return DateListViewHolder(view, dateDelegate)
    }

    override fun onBindViewHolder(holder: DateListViewHolder, position: Int) {
       if(mDateList.isNotEmpty()){
           holder.bind(date = mDateList[position])
       }
    }

    override fun getItemCount(): Int {
        return mDateList.size
    }

    fun updateData(dayVOList: List<DateVO>) {
        mDateList = dayVOList
        notifyDataSetChanged()
    }}