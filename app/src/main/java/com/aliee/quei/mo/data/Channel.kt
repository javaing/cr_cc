package com.aliee.quei.mo.data

import com.aliee.quei.mo.application.ReaderApplication
import com.meituan.android.walle.WalleChannelReader

object Channel{
    var channelName : String
    var refId = "1"
    var linkId = "1"

    init {
        refId = WalleChannelReader.get(ReaderApplication.instance,"refId")?:""
        linkId = WalleChannelReader.get(ReaderApplication.instance,"linkId")?:""

        val channelInfo = WalleChannelReader.getChannelInfoMap(ReaderApplication.instance)

        var result = ""
        channelInfo?.mapKeys {
            result += "${it.key} = ${it.value};"
        }

        channelName = WalleChannelReader.getChannel(ReaderApplication.instance,"0")?:"0"
    }
}