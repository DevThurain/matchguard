package com.thurainx.matchguard.network

import android.util.Log
import com.thurainx.matchguard.data.vos.MatchVO
import com.thurainx.matchguard.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException

object JsoupCrawler {
    suspend fun getMatchListV1(onSuccess: (matchList: List<MatchVO>) -> Unit) {
        var matchList: ArrayList<MatchVO> = arrayListOf()
        coroutineScope {
            launch {
                withContext(Dispatchers.IO) {
                    val doc: Document = Jsoup.connect(ApiConstants.BASED_URL).get()

                    val data: Elements = doc.select(".panel-box div div")
                    Log.d("jsoup_data", data.size.toString())
                    Log.d(
                        "jsoup_data",
                        doc.select(".panel-box div div").attr("data-toggle-area-content")
                    )


                    val newsHeadlines: Elements = doc.select(".wikitable-striped")

                    Log.d("jsoup", newsHeadlines.text())
                    Log.d("jsoup_size", newsHeadlines.size.toString())

                    for (headline in newsHeadlines) {
                        val leftTeamName = headline.select("table tbody tr .team-left").text()
                        val leftTeamUrl =
                            headline.select("table tbody tr .team-left .team-template-team2-short .team-template-image-icon a img")
                                .attr("src")
                        val rightTeamName = headline.select("table tbody tr .team-right").text()
                        val rightTeamUrl =
                            headline.select("table tbody tr .team-right .team-template-team-short .team-template-image-icon a img")
                                .attr("src")

                        val unixTime =
                            headline.select("table tbody tr .match-filler .match-countdown span.timer-object-countdown-only")
                                .attr("data-timestamp").toString()
                        val score = headline.select("table tbody tr .versus").text()
                        val leagueName =
                            headline.select("table tbody tr .match-filler div div a").text()

                        matchList.add(
                            MatchVO(
                                id = unixTime.plus(leftTeamName).plus(rightTeamName),
                                leftTeamName = leftTeamName,
                                leftTeamImageUrl = leftTeamUrl,
                                rightTeamName = rightTeamName,
                                rightTeamImageUrl = rightTeamUrl,
                                unixTime = unixTime,
                                score = score,
                                leagueName = leagueName,
                                standardMatchDate = DateUtils().convertToStandardDate(unixTime)
                            )
                        )
                        Log.d(
                            "jsoup_headLine",
                            headline.select("table tbody tr .team-left").text()
                                .plus(" Vs ")
                                .plus(headline.select("table tbody tr .team-right").text())
                                .plus(" score-")
                                .plus(headline.select("table tbody tr .versus").text())
                                .plus(" time-")
                                .plus(
                                    headline.select("table tbody tr .match-filler .match-countdown span.timer-object-countdown-only")
                                        .attr("data-timestamp")
                                )
                                .plus(" league-")
                                .plus(
                                    headline.select("table tbody tr .match-filler div div a").text()
                                )
                                .plus(" ImagePath-")
                                .plus(
                                    headline.select("table tbody tr .team-left .team-template-team2-short .team-template-image-icon a img")
                                        .attr("src")
                                )

                        )


//                Log.d("jsoup_url",headline.absUrl("href"))
                    }
                    onSuccess(matchList)
                }

            }
        }
    }

    suspend fun getMatchListV2(onSuccess: (matchList: List<MatchVO>) -> Unit,onError: (String) -> Unit) {
        var matchList: ArrayList<MatchVO> = arrayListOf()
        coroutineScope {
            launch {
                withContext(Dispatchers.IO) {
                    try {
                        val doc: Document = Jsoup.connect(ApiConstants.BASED_URL).get()

//                    val data: Elements = doc.select(".panel-box div div")
//                    Log.d("jsoup_data", data.size.toString())
//                    Log.d("jsoup_data", doc.select(".panel-box div div").attr("data-toggle-area-content"))


                        val groups: Elements = doc.select(".panel-box div div")

                        Log.d("jsoup", groups.text())
                        Log.d("jsoup_size", groups.size.toString())

                        for (group in groups.take(2)) {
                            if (group.attr("data-toggle-area-content").toString() == "1") {
                                val teams: Elements = group.select(".wikitable")
                                for (team in teams) {
                                    val leftTeamName = team.select("tbody tr .team-left").text()
                                    val leftTeamUrl =
                                        team.select("tbody tr .team-left .team-template-team2-short .team-template-darkmode a img")
                                            .attr("src")
                                    val rightTeamName = team.select("tbody tr .team-right").text()
                                    val rightTeamUrl =
                                        team.select("tbody tr .team-right .team-template-team-short .team-template-darkmode a img")
                                            .attr("src")

                                    val unixTime =
                                        team.select("tbody tr .match-filler .match-countdown span.timer-object-countdown-only")
                                            .attr("data-timestamp").toString()
                                    val score = team.select("tbody tr .versus").text()
                                    val leagueName =
                                        team.select("tbody tr .match-filler div div a").text()

                                    matchList.add(
                                        MatchVO(
                                            id = unixTime.plus(leftTeamName).plus(rightTeamName),
                                            leftTeamName = leftTeamName,
                                            leftTeamImageUrl = leftTeamUrl,
                                            rightTeamName = rightTeamName,
                                            rightTeamImageUrl = rightTeamUrl,
                                            unixTime = unixTime,
                                            score = score,
                                            leagueName = leagueName,
                                            standardMatchDate = DateUtils().convertToStandardDate(unixTime)
                                        )
                                    )
                                    Log.d(
                                        "jsoup_team",
                                        group.select("table tbody tr .team-left").text()
                                            .plus(" Vs ")
                                            .plus(group.select("table tbody tr .team-right").text())
                                            .plus(" score-")
                                            .plus(group.select("table tbody tr .versus").text())
                                            .plus(" time-")
                                            .plus(
                                                group.select("table tbody tr .match-filler .match-countdown span.timer-object-countdown-only")
                                                    .attr("data-timestamp")
                                            )
                                            .plus(" league-")
                                            .plus(
                                                group.select("table tbody tr .match-filler div div a").text()
                                            )
                                            .plus(" ImagePath-")
                                            .plus(
                                                group.select("table tbody tr .team-left .team-template-team2-short .team-template-image-icon a img")
                                                    .attr("src")
                                            )
                                    )

                                }
                            }

//                        val leftTeamUrl =
//                            headline.select("table tbody tr .team-left .team-template-team2-short .team-template-image-icon a img")
//                                .attr("src")
//                        val rightTeamName = headline.select("table tbody tr .team-right").text()
//                        val rightTeamUrl =
//                            headline.select("table tbody tr .team-right .team-template-team-short .team-template-image-icon a img")
//                                .attr("src")
//
//                        val unixTime =
//                            headline.select("table tbody tr .match-filler .match-countdown span.timer-object-countdown-only")
//                                .attr("data-timestamp").toString()
//                        val score = headline.select("table tbody tr .versus").text()
//                        val leagueName =
//                            headline.select("table tbody tr .match-filler div div a").text()
//
//                        matchList.add(
//                            MatchVO(
//                                id = unixTime.plus(leftTeamName).plus(rightTeamName),
//                                leftTeamName = leftTeamName,
//                                leftTeamImageUrl = leftTeamUrl,
//                                rightTeamName = rightTeamName,
//                                rightTeamImageUrl = rightTeamUrl,
//                                unixTime = unixTime,
//                                score = score,
//                                leagueName = leagueName
//                            )
//                        )
//                        Log.d(
//                            "jsoup_headLine",
//                            headline.select("table tbody tr .team-left").text()
//                                .plus(" Vs ")
//                                .plus(headline.select("table tbody tr .team-right").text())
//                                .plus(" score-")
//                                .plus(headline.select("table tbody tr .versus").text())
//                                .plus(" time-")
//                                .plus(
//                                    headline.select("table tbody tr .match-filler .match-countdown span.timer-object-countdown-only")
//                                        .attr("data-timestamp")
//                                )
//                                .plus(" league-")
//                                .plus(
//                                    headline.select("table tbody tr .match-filler div div a").text()
//                                )
//                                .plus(" ImagePath-")
//                                .plus(
//                                    headline.select("table tbody tr .team-left .team-template-team2-short .team-template-image-icon a img")
//                                        .attr("src")
//                                )
//
//                        )


//                Log.d("jsoup_url",headline.absUrl("href"))
                        }
                        onSuccess(matchList)
                    }catch (e: Exception){
                        if(e is IOException){
                            onError("No Internet Connection")
                        }else{
                            onError(e.localizedMessage ?: "Server Error")
                        }
                    }

                }

            }
        }
    }

}