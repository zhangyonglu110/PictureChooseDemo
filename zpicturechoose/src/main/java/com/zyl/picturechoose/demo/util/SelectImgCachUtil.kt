package com.zyl.myview.picture.util

import com.zyl.myview.picture.model.ImgInfor

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