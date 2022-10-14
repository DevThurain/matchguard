package com.thurainx.matchguard.viewholders

import com.thurainx.matchguard.delegate.DateDelegate
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thurainx.matchguard.R
import com.thurainx.matchguard.data.vos.DateVO
import kotlinx.android.synthetic.main.viewholder_date.view.*

class DateListViewHolder(itemView: View, delegate: DateDelegate) : RecyclerView.ViewHolder(itemView) {
    var mDate: DateVO? = null
    init {
        itemView.setOnClickListener {
            mDate?.let {
                delegate.onTapDate(it)
            }
        }
    }


    fun bind(date: DateVO){
        itemView.tvDate.text = date.formatDate
        mDate = date
        if(date.isSelected){
            itemView.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemView.context.resources.getDimension(
                R.dimen.text_regular_2x))
            itemView.tvDate.setTextColor(ContextCompat.getColor(itemView.context,R.color.orange_accent))
        }else{
            itemView.tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemView.context.resources.getDimension(R.dimen.text_regular_2x))
            itemView.tvDate.setTextColor(ContextCompat.getColor(itemView.context,R.color.grey_light))
        }
    }
}