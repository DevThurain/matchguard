package com.thurainx.matchguard.adapters

import com.thurainx.matchguard.delegate.NotiDelegate
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thurainx.matchguard.R
import com.thurainx.matchguard.data.vos.MatchVO
import com.thurainx.matchguard.viewholders.MatchListViewHolder
const val TYPE_NORMAL = "TYPE_NORMAL"
const val TYPE_SUBSCRIBE = "TYPE_SUBSCRIBE"
class MatchListAdapter(val notiDelegate: NotiDelegate, val type: String) : RecyclerView.Adapter<MatchListViewHolder>() {
    var mMatchList: List<MatchVO> = listOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_match,parent,false)
        return MatchListViewHolder(view, notiDelegate)
    }

    override fun onBindViewHolder(holder: MatchListViewHolder, position: Int) {
       if(mMatchList.isNotEmpty()){
           holder.bind(match = mMatchList[position], type = type)
       }
    }

    override fun getItemCount(): Int {
        return mMatchList.size
    }

    fun updateData(matchVOList: List<MatchVO>) {
        mMatchList = matchVOList
        notifyDataSetChanged()
    }}