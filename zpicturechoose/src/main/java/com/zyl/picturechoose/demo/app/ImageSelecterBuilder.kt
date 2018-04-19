package com.zyl.myview.picture.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.zyl.picturechoose.demo.activity.PictureChooseActivity
import com.zyl.picturechoose.demo.R
import com.zyl.picturechoose.demo.activity.MultipleChooseActivity
import com.zyl.picturechoose.demo.app.PictureConstant

/**
 * Created by zhangyonglu on 2018/4/13.
 */
class ImageSelecterBuilder {


    var miscrop = false
    var mcount = 1
    var mcolor = R.color.colorPrimary
    var mcurrentModel = PictureConstant.PICTURE_MODEL_SINGLE
    var mIntent: Intent? = null
    var mContext: Context? = null

    fun setThemeColor(color: Int): ImageSelecterBuilder {
        this.mcolor = color
        return this

    }

    fun setIsCrop(iscrop: Boolean): ImageSelecterBuilder {
        this.miscrop = iscrop
        return this

    }

    fun setImageCount(count: Int): ImageSelecterBuilder {
        this.mcount = count
        return this

    }

    fun setImageModel(model: String): ImageSelecterBuilder {
        this.mcurrentModel = model
        return this

    }

    fun start() {
        if (mcurrentModel.equals(PictureConstant.PICTURE_MODEL_SINGLE)) {
            startSingle()
        } else {
            startMany()
        }
    }

    constructor(context: Context) {
        mContext = context


    }


    fun startSingle() {
        mIntent = Intent(mContext!!.applicationContext, PictureChooseActivity::class.java)
        mIntent!!.putExtra(PictureConstant.INTENT_THEME_COLOR, mcolor)
        mIntent!!.putExtra(PictureConstant.INTENT_IS_CROP, miscrop)
        (mContext as Activity).startActivityForResult(mIntent, PictureConstant.REQUEST_CHOOSE_PHOTO)
    }


    fun startMany() {
        mIntent = Intent(mContext!!.applicationContext, MultipleChooseActivity::class.java)
        mIntent!!.putExtra(PictureConstant.INTENT_THEME_COLOR, mcolor)
        mIntent!!.putExtra(PictureConstant.INTENT_IMAGE_COUNT, mcount)
        (mContext as Activity).startActivityForResult(mIntent, PictureConstant.REQUEST_CHOOSE_PHOTO)
    }
}