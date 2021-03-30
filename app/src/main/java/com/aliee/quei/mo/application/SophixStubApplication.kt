package com.aliee.quei.mo.application


import android.content.Context
import android.support.annotation.Keep
import android.support.multidex.MultiDex
import android.util.Log
import com.taobao.sophix.PatchStatus
import com.taobao.sophix.SophixApplication
import com.taobao.sophix.SophixEntry
import com.taobao.sophix.SophixManager
import com.taobao.sophix.listener.PatchLoadStatusListener


class SophixStubApplication : SophixApplication() {
    private val TAG = "SophixStubApplication"
    private val APP_ID = "30846840";
    private val APP_SECRET = "7888913f08de5f1a26ebcd3c9d272130";
    private val RSA_SECRET="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCHVcHvCxleVV6VJHvU5C4U+MO8t6K977tBjzkFojWpiATqKsl5RL/xmlfhS7XFuM1kM6TX42Fk7/wHlIF5YSLgf1vYVq9pFr67KombrpLmPHJnm7nuGMWuLDvS8FVWX/uyDTsNs0Z6rbHZIFcGZPF3csBqAovUAET89vieneS2BYUJ68M9bNp8e6z6Eir/hKrsKbhivx1q3YygKxBClkpPHqrWL6mrGXbs75F0v3QbHfBkDXTGfg39uAA0SlPLktvbJ70Rl7qeS3eREuB8F7a6rHbYjrQIe6I6gPlN3SGW3Y5DFB8SBJFgwjiCM0LMk4NO+yuVeFhz0hGXhLxvENfzAgMBAAECggEAT0URAiZbJqEUdIFZMxqwT0JizEA3x88KvNzaRo17pyBv2IVVW33EfAyumt1QzE5xp9aEIJwj6Q6UbhAzQlh+KEcREge8WO6kq1bAIXtAujD/xeY2IttJtrpcFDMHmT9BW0pao6y1hJgGyyohLzKjKRcC01VId9XskstKVwYe9+orlzHvP0ACGq9N41lC/MBhDIq9fPWdlqoiZHk1/8eSOqE4xfNroKOEM1jljvj2Y6+D4byL7bYijjIyl94U0tcjGWBQoW0o5uy/CntoS7JSBT8M+8EWFJZmUsIWcpU7caHDTFHZy8kNzXiGikyXN4iP/OYrm8oiMAZQ1TEn93Mm2QKBgQDASE7+79TiJEi9ZkPKQ+kgifg2T7GNMd1eiYplUZ/pEsKgmYg4B53iBu/2TJwknoEYTjwYpu8BdHNJ3/h3qliOJZS5Nl+F6tasFZ9j064IDwcGfERNlJo0WjQxAo01WtGnBUrdr4B8Z/I94TFllEBiuZB2IIgW6/yiSIFBlP8D7wKBgQC0LnxYodBTqgYhVHGXQ7l8QK1Bp5Ujkhe7+qJU8g8Z+huAiuEXx/er37Bi9WkeTkjHTEDd01G4QkCvg2eQB/4oLFM2op8ttGmIASj5/jDrFlCyAr1mdiUSnGM1aBqlAufg00aY2aaObEGH3VVgKdx9hMpHHO/lYNtwEnrW2HmYPQKBgB9JpNEG5XUGwm9PPKik9EI/MQxlW1bGTmcbqlFiPssGKnVHhkrXsnEc/97sCFxNDmSOFmlMgIeUE05qC1wtc8ttgg3BMznvRI8r1YvA3gihqVr8zYByNcVfCC8RYzjTVln/GneIHzgfEePPyOHmhbOKzl7qXDqem7UsZxvsyrIxAoGAKfGIULmaXV9+/dNCfgvt6dbEfjwco6Mmk0PM9ilDCrEGJe4uZ/LM3N29mcu3JK7kkkBV+U1wAIyrsi/Ms+Kob4Gh/V3t4B3XUymx+EAL8y3GqymoHWnzR1nQXmZVjYQXpmaIUJyaAR2CQ7ZqYXBdO3rl+5fo4GSjsHsczVanN6ECgYAGpRUyqvkZk40xlqSjflVKQZWyY5cB7i7cEbZZeHSNLSHAZ348rAVmgJd5VpzwXq2F1OI2ViiRlyNPvcxkk6RTmigPsWUA7Q1bbFUZ1/fSRPOCbv8exasau3ldhHNQ8QPROka54zV9uZulp93Fs2aBXjJ9F5hlBqyHuIE/774c6w=="
//    private val APP_ID = "30466133";
//    private val APP_SECRET = "159c0c0be8156d9edfeff62f12041915";
//    private val RSA_SECRET="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDBHNRNeFV9AaST0RHZZ02gvfDP1RWRedvOOITWr9axxRFikqPjbAkflgHzRccS2K7asnG1bDhiC6OIKcRdE6+F4g6hMHx0n3kjsTmc3IvkL0o3tQKYxyQV4vnn5N0XDfNW2MM56BxnDoI73KuUyoMg5mNtIsSMcn/zOg9POALbiLiVJr8HhnvaREXjZysrlVMiXPe0kqOxuujVUUivac5FqLRmyJIyh1gkJK+S2wS8aykk03MK0uT35LI2pv8ea3wT7f6KlOQivD8Avu/7yOehw+IgD4NgjHabi25kbgS7RYPl3wKm6JsbJfW8KcHmKh993cyaOmp9Wyq98zGv/ifHAgMBAAECggEBAL/4JcBheDJNeMqM0j6ZZ9z8FLC2NbupNO2616sHn9iWe+LOvWbt1d/1Yjjwy6Ybiy7SpyDf1Oj6gvVv98lDMYBZKYvpRD827NijbA9YWyRaMmBQa6VuiZvMR/vTUkmBz74su14MNHfqP2K8ykCeVF5XSM2L+3FWvYUP2Xo5xvVuKKj906EvFVGTDeltzR3x7cdBPrJdYEZ+QcGFOoKLKUYpKaoa+fM5J+A9tNtJUAhOIT4NBuZOHte/diiVItXij8j22EIKtEdldhom+BRduVVGVQF6x8sQeImmn9eYlirRQEca4Opal04+1L3KtBpGGf35+1pdlY/JjHKqiFh1CnkCgYEA66WCkIFaZEA4t0i1eLQ29bEd7WyGQUjKK01Q7j1vruNIhS7Q/hJ4e2Rgxe/myvFQ56k8c+/g70nM5H4+9JSPn0TwSTqbQDCNNw3Bc9vzGCQ3qwsMpEfyvMGRcMfrBkqgU/w1ol9fWln/CdrxB8vzWo18TaGS+U70UkRG2Lq4jQMCgYEA0crVDMclmknzGiYp0oQShhF6PdNb6P2E0SHa8+Al/+Vz5jJPYs9OryFn737Rbd82fp/WhjDTA8B/Uk3KkxM+Jrk0JGD9Zk7ndUCcwaqg0rBiZpcEnncNPuH15e5+f+HsAuI7/eaVLiAnZp4k4TRZEoeZHrrRBh0ssVRH1wHVNO0CgYBryTwMjy5idsG6jGZ9fKk6/rX6uM48JGhdIDNple50539vc/eij55Oip59S3uczn68SDvfqBSSP4aZ6WkwhJehOW5TgJws40hkq0UWksACBeb0tMwmhMZyMnQtSzSknxPw9oct60P5VvsCbgQcFd0NSXRXKhXJNaUp0BM75USvMwKBgALa++NcoopjD0f/1PnkikwA2OUWh124daJTD6PaHSpKeTVc4e+6WpvPnJaJ51fTfe5jRow1Beo7KIB6RpOR43qrcqY8G7bUxBm4c2m/ZMj5VZ2H2Kw/epKsiKhEgrxC2u9/HMnr5YPnNCIVbPMTv1KdPdxJNqcapo0TVJb8k2ZRAoGBAI5HoVhe8R+tghHF6oOu7Q/zHO59DgZtyOItBWQ7qH0EDFVjVtUSaPwt1CqKliCPt5fydYXySSdLnpIXPgntBoGJqunrRgBqYCnt7ndLd61d3zrGHPoCXDBUT/9bMGYxliokHJXP9pbXCq5JQUw6776n4kjXu1mILk/B/zR8L8Ht"

    // 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
    @Keep
    @SophixEntry(ReaderApplication::class)
    internal class RealApplicationStub


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //如果需要使用MultiDex，需要在此处调用。
        MultiDex.install(base);
        initSophix()
    }




    private fun initSophix() {
        var appVersion = "0.0.0"
        try {
            appVersion = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0).versionName
        } catch (e: Exception) {
        }
        val instance: SophixManager = SophixManager.getInstance()
        instance.setContext(this)
                .setAppVersion(appVersion)
                .setSecretMetaData(APP_ID, APP_SECRET, RSA_SECRET)
                .setEnableDebug(true)
                .setEnableFullLog()
                .setPatchLoadStatusStub(object : PatchLoadStatusListener {
                    override fun onLoad(mode: Int, code: Int, info: String?, handlePatchVersion: Int) {
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {

                            Log.d(TAG, "sophix load patch success!")
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 如果需要在后台重启，建议此处用SharePreference保存状态。

                            Log.d(TAG, "sophix preload patch success. restart app to make effect.")
                        }
                    }
                }).initialize()
    }
}