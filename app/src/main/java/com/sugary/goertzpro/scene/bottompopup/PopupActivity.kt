package com.sugary.goertzpro.scene.bottompopup

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.sugary.goertzpro.R
import com.sugary.goertzpro.widget.popup.BottomPopupBuilder
import kotlinx.android.synthetic.main.activity_popup.*

/**
 * Created by Ethan Ruan 2018/12/28
 * 底部弹窗实现Demo
 */
class PopupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup)

        tvBottom.setOnClickListener {
            BottomPopupBuilder().apply {
                isOutsideTouchable = false
                contentView = generatePopupContentView()
                popupAlpha = 0.5f
                window = getWindow()
            }.createAndShow(rootPopupLayout)
        }
    }


    private fun generatePopupContentView(): View{
        val contentView =  LayoutInflater.from(this).inflate(R.layout.popup_bottom_dialog, null,false)
        val tv1 = contentView.findViewById<TextView>(R.id.tvPopupBottom)
        tv1.setOnClickListener {
            Toast.makeText(this, "点击响应", Toast.LENGTH_SHORT).show()
        }
        return contentView
    }
}
