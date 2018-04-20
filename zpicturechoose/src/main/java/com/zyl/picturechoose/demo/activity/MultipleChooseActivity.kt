package com.zyl.picturechoose.demo.activity

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.*
import com.zyl.myview.zrecycleview.base.BaseRecycleAdapter
import com.zyl.myview.zrecycleview.base.BaseViewHolder
import com.zyl.myview.zrecycleview.util.DensityUtil
import com.zyl.myview.zrecycleview.util.ZItemDecoration
import com.zyl.myview.zrecycleview.widget.ZRecycleView
import com.zyl.picturechoose.demo.R
import com.zyl.picturechoose.demo.app.PictureConstant
import com.zyl.picturechoose.demo.model.ImgInfor
import com.zyl.picturechoose.demo.util.SelectImgCachUtil
import com.zyl.picturechoose.demo.viewholder.PictureHolder
import com.zyl.picturechoose.demo.viewholder.PopListHolder
import kotlinx.android.synthetic.main.activity_multiple_choose.*
import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MultipleChooseActivity : AppCompatActivity() {
    var simpleDateFormat: SimpleDateFormat?=null
    var parentNameList= ArrayList<String>()
    var dataList= ArrayList<ImgInfor>()
    var allImgList= ArrayList<ImgInfor>()
    var map= HashMap<String, ArrayList<ImgInfor>>()
    var adapter: BaseRecycleAdapter<ImgInfor>?=null
    var popAdapter: BaseRecycleAdapter<String>?=null
    var popWindow: PopupWindow?=null
    var popView: View?=null
    var popRecycleView: ZRecycleView?=null
    var selectedImgInforList=ArrayList<ImgInfor>()
    var themeColor:Int=0
    var selectedNameList=ArrayList<String>()

    companion object {
        var currentFileName=""

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple_choose)
        initdata()
        Thread(Runnable {
            var cursor = contentResolver!!.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
            initPhotoData(cursor)
            runOnUiThread(Runnable {
                initRecycleView()
                initListPopWindow()
                initEvent()
            })
        }).start()

    }

    private fun initdata() {
        if(intent!=null){
            themeColor=intent.getIntExtra(PictureConstant.INTENT_THEME_COLOR, resources.getColor(R.color.colorPrimary))
        }
        simpleDateFormat=SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
        layout_title.setBackgroundColor(themeColor)
        layout_parent_type.setBackgroundColor(themeColor)
        btn_confirm.setText(getString(R.string.confirm)+"(0/9)")
    }

    private fun initListPopWindow() {
        popView= LayoutInflater.from(this).inflate(R.layout.layout_picture_list_pop,null)
        popView!!.background.alpha=200
        popRecycleView=popView!!.findViewById<ZRecycleView>(R.id.zrecycleview)
        popRecycleView!!.setLayoutManager(LinearLayoutManager(this))
        popRecycleView!!.setZItemDecoration(ZItemDecoration(this, LinearLayoutManager.VERTICAL, DensityUtil.dip2px(this, 1f), resources.getColor(R.color.gray_color)))
        popAdapter=object: BaseRecycleAdapter<String>(this,parentNameList, R.layout.layout_pop_list_recycle_item){
            override fun getViewHolder(p0: View?): BaseViewHolder<String> {
               return  PopListHolder(p0!!,map,PictureConstant.PICTURE_MODEL_MANY)
            }
        }

        popRecycleView!!.setAdapter(popAdapter)

         //获取主布局宽高  设置popwindow高度

        var viewTreeObserver=layout_picture_choose_main.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener (object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                popWindow= PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, layout_picture_choose_main.measuredHeight - DensityUtil.dip2px(this@MultipleChooseActivity, 50f) * 2)
                popWindow!!.animationStyle= R.style.popwindow_style
                popWindow!!.isFocusable=true
                popWindow!!.update()
                layout_picture_choose_main.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        } )
    }



    private fun initEvent() {
        popAdapter!!.setOnItemClickListener(object: BaseRecycleAdapter.OnZRecycleViewItemClickListener {
            override fun onItemClick(p0: Int) {
                var itemFileName=parentNameList.get(p0)
                if(!itemFileName.equals(currentFileName)){
                    var list=map[itemFileName]
                    if(adapter!=null){
                        adapter!!.clear()
                        adapter!!.addAll(list)
                        popAdapter!!.notifyDataSetChanged()
                    }
                    currentFileName=itemFileName
                    if(currentFileName.equals("all"))tv_photo_file_type.setText(R.string.all_picture) else  tv_photo_file_type.setText(currentFileName)

                }
                popWindow!!.dismiss()
            }

        })


        tv_photo_file_type.setOnClickListener(View.OnClickListener {
               if(!popWindow!!.isShowing){
                   popWindow!!.showAsDropDown(layout_title,0,0)
               }else{
                   popWindow!!.dismiss()
               }
        })

        layout_title_back.setOnClickListener(View.OnClickListener {
            finish()
        })

        btn_confirm.setOnClickListener(View.OnClickListener {
            var selectedImgInforList= SelectImgCachUtil.getSelectedList()
            if(selectedImgInforList.size<=0){
                Toast.makeText(this, R.string.please_choose_picture, Toast.LENGTH_SHORT).show()
                return@OnClickListener

            }
            if(selectedImgInforList.size>PictureConstant.MAX_PICTURE_SELECT_NUM){
                Toast.makeText(this, R.string.most_choose_nine_picture, Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            var intent= Intent()
            intent.putExtra(PictureConstant.INTENT_RESULR_DATA, selectedImgInforList)
            intent.putExtra(PictureConstant.PICTURE_CHOOSE_MODEL,PictureConstant.PICTURE_MODEL_MANY)
            setResult(Activity.RESULT_OK, intent)
            finish()

        })
    }



    private fun initRecycleView() {

         //默认显示所有图片

        setDefListShow()
        zrecycleview.setLayoutManager(GridLayoutManager(this, 4))
        zrecycleview.setZItemDecoration(ZItemDecoration(this, LinearLayoutManager.VERTICAL, DensityUtil.dip2px(this, 5f), Color.WHITE))
        adapter=object: BaseRecycleAdapter<ImgInfor>(this,dataList, R.layout.layout_multiple_picture_list_recycle_item) {
            override fun getViewHolder(p0: View?): BaseViewHolder<ImgInfor> {
                return PictureHolder(p0!!,selectedImgInforList,btn_confirm,selectedNameList)
            }
        }

        zrecycleview.setAdapter(adapter)


    }

    private fun setDefListShow() {
        var defFileName=parentNameList[0]
        var defChildList=map[defFileName]
        currentFileName=defFileName
        if(defChildList!=null&&defChildList.size>0) dataList.addAll(defChildList!!)
        if(defFileName.equals("all"))tv_photo_file_type.setText(R.string.all_picture) else tv_photo_file_type.setText(defFileName)

    }


      //初始化数据  转化为map 集合

    private fun initPhotoData(cursor: Cursor) {
        try {
            while (cursor.moveToNext()) {
                var imgPathColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                var imgNameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                var imgdesColumn = cursor.getColumnIndex(MediaStore.Images.Media.DESCRIPTION)
                var imgdateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                var imgdateColumn2 = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
                var imgdateColumn3 = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
                var path = cursor.getString(imgPathColumn)
                var imgname = cursor.getString(imgNameColumn)
                var imgdes = cursor.getString(imgdesColumn)
                var addDate = cursor.getString(imgdateColumn)
                var modifieDate = cursor.getString(imgdateColumn2)
                var takenDate= cursor.getString(imgdateColumn3)
                addDate = simpleDateFormat!!.format(Date(addDate.toLong()))
                modifieDate = simpleDateFormat!!.format(Date(modifieDate.toLong()))
                takenDate = simpleDateFormat!!.format(Date(takenDate.toLong()))

                 //验证是否存在

                var file = File(path)
                if(!file.exists()){
                    continue
                }

                 //获取所在文件路径和文件名

                var parentpath = file.parentFile.path
                var parentName = file.parentFile.name
                var imgInfor = ImgInfor()

                if(!TextUtils.isEmpty(imgname))    imgInfor.name=imgname
                if(!TextUtils.isEmpty(imgdes))    imgInfor.imgdes=imgdes
                if(!TextUtils.isEmpty(path))    imgInfor.path=path
                if(!TextUtils.isEmpty(parentName))  imgInfor.parentname=parentName
                if(!TextUtils.isEmpty(parentpath)) imgInfor.parentpath=parentpath
                if(!TextUtils.isEmpty(addDate))imgInfor.adddate=addDate
                if(!TextUtils.isEmpty(modifieDate))imgInfor.modifiedate=modifieDate
                if(!TextUtils.isEmpty(takenDate)) imgInfor.takendate=takenDate

                //数据分类

                if (!TextUtils.isEmpty(parentName)) {
                    if (!parentNameList.contains(parentName)) parentNameList.add(parentName)
                    var list: ArrayList<ImgInfor>? = null
                    if (map[parentName] != null) {
                        list = map[parentName]!!
                        if (!list.contains(imgInfor)) list.add(imgInfor)
                    } else {
                        list = ArrayList()
                        list.add(imgInfor)
                        map[parentName] = list
                    }
                }

                 //所有图片

                allImgList.add(imgInfor)
                map.put("all",allImgList)


            }
        }catch (e:Exception){
            runOnUiThread(Runnable {
                Toast.makeText(this, "load picture fail", Toast.LENGTH_SHORT).show()
            })
        }finally {
            cursor.close()
        }

        //添加所有图片类别

        parentNameList.add(0,"all")

        //图片按日期降序排列
        if(parentNameList.size>1) {
            for (parentName in parentNameList) {
                Collections.reverse(map[parentName])
            }
        }
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        if(popWindow!=null&&popWindow!!.isShowing)popWindow!!.dismiss()
        super.onBackPressed()
    }

    override fun onStop() {
       //activity不可见时 清空缓存数据
        SelectImgCachUtil.getSelectedList().clear()
        super.onStop()
    }

}