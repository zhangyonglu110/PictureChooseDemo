package com.zyl.picturechoose.demo.viewholder

import android.text.TextUtils
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.zyl.myview.zrecycleview.base.BaseViewHolder
import com.zyl.picturechoose.demo.R
import com.zyl.picturechoose.demo.model.ImgInfor
import com.zyl.picturechoose.demo.util.SelectImgCachUtil

/**
 * Created by zhangyonglu on 2018/4/16.
 */
class PictureHolder : BaseViewHolder<ImgInfor> {
    var imgView: ImageView? = null
    var ckBox: CheckBox? = null
    var textView: Button? = null
    var seletedList: ArrayList<ImgInfor>? = null
    var seletedNameList: ArrayList<String>? = null

    constructor(view: View) : super(view) {
        imgView = itemView.findViewById<ImageView>(R.id.img_choose)
        ckBox = itemView.findViewById<CheckBox>(R.id.cb_choose)


    }

    constructor(view: View, list: ArrayList<ImgInfor>, textView: Button, slist: ArrayList<String>) : super(view) {
        imgView = itemView.findViewById<ImageView>(R.id.img_choose)
        ckBox = itemView.findViewById<CheckBox>(R.id.cb_choose)
        this.textView = textView
        this.seletedList = SelectImgCachUtil.getSelectedList()
        this.seletedNameList = slist

    }

    override fun setdata(s: ImgInfor?) {
        if (!TextUtils.isEmpty(s!!.path)) {
            Glide.with(mitenView.context).load(s!!.path).into(imgView)
        }

        if (seletedList!!.contains(s!!)) {
            ckBox!!.post(Runnable {
                ckBox!!.isChecked = true
            })

        } else {
            ckBox!!.post(Runnable {
                ckBox!!.isChecked = false
            })
        }

        ckBox!!.setOnClickListener(View.OnClickListener {
            if (seletedList!!.size > 9) {
                ckBox!!.isChecked = false
                Toast.makeText(itemView.context, R.string.most_choose_nine_picture, Toast.LENGTH_SHORT).show()
            }
        })

        ckBox!!.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    if (!seletedList!!.contains(s!!)) {
                        seletedList!!.add(s!!)
                    }
                } else {
                    if (seletedList!!.contains(s!!)) seletedList!!.remove(s!!)
                }
                textView!!.setText(itemView.context.getString(R.string.confirm) + "(" + seletedList!!.size + "/9" + ")")
            }
        })
    }
}