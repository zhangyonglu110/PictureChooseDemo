package com.zyl.myview.picture.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zyl.myview.zrecycleview.base.BaseRecycleAdapter
import com.zyl.myview.zrecycleview.base.BaseViewHolder
import com.zyl.myview.zrecycleview.widget.ZRecycleView
import com.zyl.picturechoose.demo.R

/**
 * Created by zhangyonglu on 2018/4/10.
 */
class PictureListActivity: AppCompatActivity() {
    var pictureRecycleView:ZRecycleView?=null
    var childList:ArrayList<String>?=null
    var adapter:BaseRecycleAdapter<String>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pictureRecycleView= ZRecycleView(this)
        setContentView(pictureRecycleView)

        pictureRecycleView!!.setLayoutManager(GridLayoutManager(this,3))
        childList=intent.getStringArrayListExtra("data")
        Log.i("sss","childlist size---------------->"+childList!!.size)
        adapter=object:BaseRecycleAdapter<String>(this,childList, R.layout.layout_picture_list_recycle_item){
            override fun getViewHolder(p0: View?): BaseViewHolder<String> {
                return object:BaseViewHolder<String>(p0!!){
                    override fun setdata(s: String?) {
                         var imgView=itemView as ImageView
                       Glide.with(this@PictureListActivity).load(s).into(imgView)
                    }
                }
            }
        }
        pictureRecycleView!!.setAdapter(adapter)

    }
}