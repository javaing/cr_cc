package com.aliee.quei.mo.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object MD5{
    //写一个md5加密的方法
    fun md5(plainText: String): String {
        //定义一个字节数组
        var secretBytes: ByteArray? = null
        try {
            // 生成一个MD5加密计算摘要
            val md = MessageDigest.getInstance("MD5")
            //对字符串进行加密
            md.update(plainText.toByteArray())
            //获得加密后的数据
            secretBytes = md.digest()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("没有md5这个算法！")
        }

        //将加密后的数据转换为16进制数字
        var md5code = BigInteger(1, secretBytes).toString(16)// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        for (i in 0 until 32 - md5code.length) {
            md5code = "0$md5code"
        }
        return md5code
    }
}