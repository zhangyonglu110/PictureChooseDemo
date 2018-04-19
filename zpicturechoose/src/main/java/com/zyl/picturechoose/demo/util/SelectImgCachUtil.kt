package com.zyl.picturechoose.demo.util

import com.zyl.picturechoose.demo.model.ImgInfor


/**
 * Created by zhangyonglu on 2018/4/18.
 */
class SelectImgCachUtil {

    companion object {
        var selectedPictureList=ArrayList<ImgInfor>()
        fun getSelectedList():ArrayList<ImgInfor>{
            if(selectedPictureList==null) selectedPictureList= ArrayList<ImgInfor>()
            return selectedPictureList
        }

    }
}