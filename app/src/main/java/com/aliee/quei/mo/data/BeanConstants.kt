package com.aliee.quei.mo.data

import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication

/**
 * Created by Administrator on 2018/4/25 0025.
 */
object BeanConstants{
    const val SEX_ALL = 0

    //语言
    const val LANG_CH = 1
    const val LANG_EN = 2
    const val LANG_ALL = 0


    //排序
    const val SORT_ASC = 0
    const val SORT_DESC = 1
    const val SORT_DEFAULT = 0

    //状态
    const val STATUS_ALL = 0 //所有
    const val STATUS_SERICAL = 1
    const val STATUS_FINISH = 2

    enum class RecommendPosition(
        var rid : String,
        var title : String
    ) {
        BANNER("17","banner"),
        HOT_RECOMMEND("18",ReaderApplication.instance.getString(R.string.position_editor_choice)),
        LATELY_UPDATE("19",ReaderApplication.instance.getString(R.string.position_latelay_update)),
        WEEK_POPULAR("20",ReaderApplication.instance.getString(R.string.position_week_trending)),
        FREE("21",ReaderApplication.instance.getString(R.string.free)),
        MOST_POPULAR("22",ReaderApplication.instance.getString(R.string.position_most_popular)),
        NEW("23",ReaderApplication.instance.getString(R.string.position_new_release)),
        RANK_FINISH("24",ReaderApplication.instance.getString(R.string.position_finished_ranking)),
        RANK_MALE("25",ReaderApplication.instance.getString(R.string.position_male_ranking)),
        RANK_FEMALE("26",ReaderApplication.instance.getString(R.string.position_female_ranking)),
        GUESS_LIKE("27",ReaderApplication.instance.getString(R.string.guess_like)),
        CHAPTER_END("38","章节末尾"),
        TWO_CHAPTER("39","每隔两章推荐"),
        WEEK_TOP10("45",ReaderApplication.instance.getString(R.string.position_week_top10)),
        OPEN_APP_RECOMMEND("61","打开APP推荐"),
        HOTRANK_NEWSKIN("62","热门推荐"),
        AD_FLOW_90("-111","信息流90高度");

        companion object {
            fun getByRid(rid: String) : RecommendPosition?{
                values().forEach {
                    if (it.rid == rid)return it
                }
                return null
            }

            fun getByTitle(title : String) : RecommendPosition?{
                values().forEach {
                    if (it.title == title)return it
                }
                return null
            }
        }
    }
}