package com.zyl.picturechoose.demo.viewholder

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zyl.myview.zrecycleview.base.BaseViewHolder
import com.zyl.picturechoose.demo.R
import com.zyl.picturechoose.demo.activity.MultipleChooseActivity
import com.zyl.picturechoose.demo.activity.PictureChooseActivity
import com.zyl.picturechoose.demo.app.PictureConstant
import com.zyl.picturechoose.demo.model.ImgInfor

/**
 * Created by zhangyonglu on 2018/4/20.
 */
class PopListHolder:BaseViewHolder<String> {
    var mcontext:Context?=null
    var mmodel=""
    var mMap:HashMap<String, ArrayList<ImgInfor>>?=null
    var currentFileName=""
    constructor(view: View):super(view)
    constructor(view: View,map:HashMap<String, ArrayList<ImgInfor>>,model:String):super(view){
        this.mMap=map
        this.mcontext=itemView.context
        this.mmodel=model
}

    override fun setdata(s: String?) {
        if (!TextUtils.isEmpty(s)) {

            try {
                var photoImgView = itemView.findViewById<ImageView>(R.id.img_parent_def) as ImageView
                var checkedView = itemView.findViewById<View>(R.id.view_checked)
                var fileNametv = itemView.findViewById<TextView>(R.id.tv_parent_file_name)
                var fileSizetv = itemView.findViewById<TextView>(R.id.tv_parent_file_size)
                var childList = mMap!![s]
                if (s.equals("all")) fileNametv.setText(R.string.all_picture) else fileNametv.setText(s)
                if(mmodel.equals(PictureConstant.PICTURE_MODEL_SINGLE)) currentFileName=PictureChooseActivity.currentFileName else currentFileName=MultipleChooseActivity.currentFileName

                if (s.equals(currentFileName)) {
                        checkedView.visibility = View.VISIBLE

                } else {
                        checkedView.visibility = View.GONE
                }
                if (childList != null && childList.size > 0) {
                    fileSizetv.setText(childList.size.toString() + mcontext!!.getString(R.string.zhang))
                    Glide.with(itemView.context).load(childList[0].path).into(photoImgView)
                }
            } catch (e: Exception) {

            }

        }

    }
}