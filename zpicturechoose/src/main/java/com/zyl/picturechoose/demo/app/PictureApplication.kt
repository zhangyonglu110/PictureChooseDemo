package com.zyl.myview.picture.app

import android.app.Application
import com.zyl.myview.picture.model.ImgInfor
import java.util.*

/**
 * Created by zhangyonglu on 2018/4/17.
 */
class PictureApplication:Application(){
//    var instance:PictureApplication?=null
var selectedPictureList= ArrayList<ImgInfor>()
    override fun onCreate() {
        super.onCreate()
        minstance=this
    }

    companion object {
        var minstance:PictureApplication?=null

        fun getInstance():PictureApplication{
            if(minstance==null) minstance=PictureApplication()
            return minstance!!

        }

    }

    fun getSelectedList():ArrayList<ImgInfor>{
        if(selectedPictureList==null) selectedPictureList= ArrayList<ImgInfor>()
        return selectedPictureList
    }



}