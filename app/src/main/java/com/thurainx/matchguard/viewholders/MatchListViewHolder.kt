package com.thurainx.matchguard.viewholders

import com.thurainx.matchguard.delegate.NotiDelegate
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thurainx.matchguard.R
import com.thurainx.matchguard.adapters.TYPE_NORMAL
import com.thurainx.matchguard.adapters.TYPE_SUBSCRIBE
import com.thurainx.matchguard.data.vos.MatchVO
import com.thurainx.matchguard.network.ApiConstants
import com.thurainx.matchguard.utils.DateUtils
import kotlinx.android.synthetic.main.viewholder_match.view.*

class MatchListViewHolder(itemView: View, notiDelegate: NotiDelegate) :
    RecyclerView.ViewHolder(itemView) {
    var mMatch: MatchVO? = null

    init {
        itemView.ivNotification.setOnClickListener {
            mMatch?.let {
                notiDelegate.onTapNoti(it)
            }
        }
    }


    fun bind(match: MatchVO,type: String) {
        mMatch = match

        itemView.tvFirstTeamName.text = match.leftTeamName
        itemView.tvSecondTeamName.text = match.rightTeamName

        itemView.ivFirstTeam.setImageResource(R.drawable.ic_dota)
        itemView.ivSecondTeam.setImageResource(R.drawable.ic_dota)

        if(match.leftTeamImageUrl.toString().isNotEmpty())
        Glide.with(itemView.context)
            .load(ApiConstants.IMAGE_BASED_URL.plus(match.leftTeamImageUrl))
            .into(itemView.ivFirstTeam)

        if(match.rightTeamImageUrl.toString().isNotEmpty())
        Glide.with(itemView.context)
            .load(ApiConstants.IMAGE_BASED_URL.plus(match.rightTeamImageUrl))
            .into(itemView.ivSecondTeam)

        itemView.tvScore.text = match.score.toString()
        itemView.tvLeague.text = match.leagueName.toString()

        match.score?.let{ score ->
            if(score.contains(":")){
                itemView.tvTime.text = itemView.context.getString(R.string.lbl_live)
                itemView.tvTime.setTextColor(Color.GREEN)
                itemView.ivNotification.visibility = View.GONE
            }else{
                itemView.tvTime.text = DateUtils().convertToReadableTime(match.unixTime.toString())
                itemView.tvTime.setTextColor(itemView.context.getColor(R.color.grey_light))
                itemView.ivNotification.visibility = View.VISIBLE
            }
        }

        itemView.tvShowStatus.visibility = View.GONE



        when(type){
            TYPE_NORMAL -> bindNormalMatch(match)
            TYPE_SUBSCRIBE -> bindSubscribedMatch(match)
        }

    }

    private fun bindNormalMatch(match: MatchVO){
        itemView.ivNotification.setImageResource(R.drawable.ic_notification)

        if(match.subscribeNoti){
            itemView.ivNotification.setColorFilter(ContextCompat.getColor(itemView.context,R.color.orange_accent))
        }else{
            itemView.ivNotification.setColorFilter(ContextCompat.getColor(itemView.context,R.color.grey_light))
        }
    }

    private fun bindSubscribedMatch(match: MatchVO){
        itemView.ivNotification.setImageResource(R.drawable.ic_close)

        if(match.alreadyShow)
            itemView.tvShowStatus.visibility = View.VISIBLE
    }
}