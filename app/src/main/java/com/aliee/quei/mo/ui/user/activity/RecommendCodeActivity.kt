package com.aliee.quei.mo.ui.user.activity

import androidx.lifecycle.Observer
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.user.vm.BillVModel
import com.aliee.quei.mo.utils.Contents
import com.aliee.quei.mo.utils.QRCodeEncoder
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.disable
import com.aliee.quei.mo.utils.extention.enable
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import kotlinx.android.synthetic.main.activity_recommend_id.*
import kotlinx.android.synthetic.main.layout_common_list.*


@Route(path = Path.PATH_USER_RECOMMEND)
class RecommendCodeActivity : BaseActivity(), TextWatcher {

    private val VM = BillVModel()
    private val launchVModel = LaunchVModel()
    override fun getLayoutId() = R.layout.activity_recommend_id
    private var recommendeCode: String = ""
    private var closeWindow: Boolean = false

    override fun afterTextChanged(s: Editable?) {
        recommendeCode = editRecommendid.text.toString()
        if (recommendeCode.isEmpty()) {
            btnInput.disable()
        } else {
            btnInput.enable()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun initData() {
        VM.getBonusId(this, recommendeCode)
    }

    override fun initView() {
        initEditText()
        initClick()
        initVM()
//        initRecyclerView()
//        initRefresh()
    }

    private fun initClick() {
        btnInput.click {
            val RecommId = editRecommendid.text.toString()

            VM.getBonusId(this, recommendeCode)
            closeWindow = true
        }
        titleBack.click {
            finish()
        }

    }

    private fun initRecyclerView() {

    }

    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            VM.getList(this)

        }
        refreshLayout.setOnLoadMoreListener {
            VM.loadMore(this)
        }
    }

    private fun initVM() {
        VM.bonusIDLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    Log.d("RecommendCodeActivity", "RecommendCodeData:" + it.data)
                    val bean = it.data
                    val parentUid = bean!!.parentUid
                    val errMsg = bean!!.errmsg
                    val uid = bean!!.uid
                    val isTemp = bean.isTemp

                    if (closeWindow) {
                        finish()
                        closeWindow = false
                    }
                    if (parentUid < 0) {
                        toast(errMsg)
                    }

                    if (parentUid == 0) {
                        if (!errMsg.isNullOrBlank()) {
                            toast(errMsg)
                        }

                        if (isTemp == 0) {
                            font01.visibility = View.VISIBLE
                            tv_is_temp.visibility = View.GONE
                            ll_qrcode.visibility = View.VISIBLE
                            recommendCode_my.visibility = View.VISIBLE
                            recommendCode_my.text = "$uid"
                            val shareUrl = bean.shareUrl
                            if (!shareUrl.isNullOrBlank()) {
                                //Find screen size
                                val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                                val display = manager.defaultDisplay
                                val point = Point()
                                display.getSize(point)
                                val width: Int = point.x
                                val height: Int = point.y
                                var smallerDimension = if (width < height) width else height

                                smallerDimension = smallerDimension * 3 / 6
                                Log.d("tag","smallerDimension:$smallerDimension")
                                //Encode with a QR Code image
                                val qrCodeEncoder = QRCodeEncoder(shareUrl,
                                        null,
                                        Contents.Type.TEXT,
                                        BarcodeFormat.QR_CODE.toString(),
                                        smallerDimension)
                                try {
                                    val bitmap: Bitmap = qrCodeEncoder.encodeAsBitmap()
                                     ivCode.setImageBitmap(bitmap)
                                    Log.d("bitmap","bitmap.width:${bitmap.width}")
                                    Log.d("bitmap","bitmap.height:${bitmap.height}")
                                } catch (e: WriterException) {
                                    e.printStackTrace()
                                }
                                btnCopyLink.visibility = View.VISIBLE
                                btnCopyLink.click {
                                    copyText(recommendCode_my, shareUrl)
                                }
                            }
                        }else{
                            tv_is_temp.visibility = View.VISIBLE
                            ll_qrcode.visibility = View.GONE
                            Log.d("tag","临时用户")
                        }
                    } else {
                        if (!errMsg.isNullOrBlank()) {
                            toast(errMsg)
                        }

                        if (isTemp == 0) {
                            editRecommendid.hint = "推广返利给 " + parentUid.toString()
                            editRecommendid.clearFocus()
                            editRecommendid.disable()
                            btnInput.visibility = View.INVISIBLE
                            recommendCode_my.visibility = View.VISIBLE
                            recommendCode_my.text = "我的分享码 " + uid
                            tv_is_temp.visibility = View.GONE
                            ll_qrcode.visibility = View.VISIBLE
                            font01.visibility = View.VISIBLE

                            val shareUrl = bean.shareUrl
                            if (!shareUrl.isNullOrBlank()) {

                                //Find screen size
                                val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                                val display = manager.defaultDisplay
                                val point = Point()
                                display.getSize(point)
                                val width: Int = point.x
                                val height: Int = point.y
                                var smallerDimension = if (width < height) width else height
                                smallerDimension = smallerDimension * 3 / 6

                                //Encode with a QR Code image
                                val qrCodeEncoder = QRCodeEncoder(shareUrl,
                                        null,
                                        Contents.Type.TEXT,
                                        BarcodeFormat.QR_CODE.toString(),
                                        smallerDimension)
                                try {
                                    val bitmap: Bitmap = qrCodeEncoder.encodeAsBitmap()
                                       ivCode.setImageBitmap(bitmap)
                                } catch (e: WriterException) {
                                    e.printStackTrace()
                                }
                                btnCopyLink.visibility = View.VISIBLE
                                btnCopyLink.click {
                                    copyText(recommendCode_my, shareUrl)
                                }
                            }
                        }else{
                            tv_is_temp.visibility = View.VISIBLE
                            ll_qrcode.visibility = View.GONE
                        }

                    }

                }
                Status.Start -> {

                }
                Status.Complete -> {

                }

                Status.TokenError->{
                    launchVModel.registerToken(this)
                }
            }
        })

        launchVModel.registerTokenLiveData.observe(this, Observer {
            when(it?.status){
                Status.Success->{
                    initData()
                }
            }
        })
    }

    private fun initEditText() {
        editRecommendid.addTextChangedListener(this)

    }

    fun copyText(view: View, shareUrl: String) {
        var myClipboard: ClipboardManager? = null
        var myClip: ClipData? = null
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?;
        myClip = ClipData.newPlainText("text", shareUrl);
        myClipboard?.setPrimaryClip(myClip);

        Toast.makeText(this, "复制分享链接成功", Toast.LENGTH_SHORT).show();
    }

    override fun getPageName() = ""
}