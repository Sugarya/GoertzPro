package com.sugary.goertzpro.widget.popup

import android.graphics.drawable.BitmapDrawable
import android.view.*
import android.widget.PopupWindow
import com.sugary.goertzpro.R
import java.lang.IllegalArgumentException

/**
 * Created by Ethan Ruan on 2018/12/29.
 * 底部弹窗
 */
class BottomPopupBuilder {

    init {

    }

    private lateinit var mPopupWindow: PopupWindow

    private val mProperty: BottomPopupProperty = BottomPopupProperty()

    var isOutsideTouchable: Boolean = true
        set(value) {
            field = value
            mProperty.isOutsideTouchable = value
        }

    var popupAlpha: Float = 1f
        set(value) {
            field = value
            mProperty.popupAlpha = value
        }

    var contentView: View? = null
        set(value) {
            field = value
            mProperty.contentView = value
        }

    var window: Window? = null
        set(value) {
            field = value
            mProperty.window = value
        }

    var newPopupEveryShow: Boolean = false

    fun createAndShow(parentView: View): PopupWindow {
        if(!newPopupEveryShow || (newPopupEveryShow && !this::mPopupWindow.isInitialized)){
            val contentViewFromProperty = mProperty.contentView
                    ?: throw IllegalArgumentException("Please setup the property of contentView")

            mPopupWindow = PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setBackgroundDrawable(BitmapDrawable())
                isFocusable = true
                isTouchable = true
                isOutsideTouchable = mProperty.isOutsideTouchable
                animationStyle = R.style.PopupWindowAnimation
                contentView = contentViewFromProperty

                setOnDismissListener {
                    setBackgroundAlpha(1f)
                }
            }
        }

        if (mPopupWindow.isShowing) {
            mPopupWindow.dismiss()
        }

        val popupAlphaFromProperty = mProperty.popupAlpha
        setBackgroundAlpha(popupAlphaFromProperty)
        mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0)

        return mPopupWindow
    }


    /**
     * 设置window背景透明度
     *
     * @param alpha
     */
    private fun setBackgroundAlpha(alpha: Float) {
        mProperty.window?.let {
            it.attributes.alpha = alpha
            it.attributes = it.attributes
        }
    }

    /**
     * 关闭弹窗
     */
    fun dismiss(){
        if(this::mPopupWindow.isInitialized){
            mPopupWindow.dismiss()
        }
    }

}

data class BottomPopupProperty(var isOutsideTouchable: Boolean = true, var contentView: View? = null, var window: Window? = null, var popupAlpha: Float = 1f)

