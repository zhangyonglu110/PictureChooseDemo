package com.zyl.picturechoose.demo.activity

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.zyl.myview.zrecycleview.base.BaseRecycleAdapter
import com.zyl.myview.zrecycleview.base.BaseViewHolder
import com.zyl.myview.zrecycleview.util.DensityUtil
import com.zyl.myview.zrecycleview.util.ZItemDecoration
import com.zyl.myview.zrecycleview.widget.ZRecycleView
import com.zyl.picturechoose.demo.R
import com.zyl.picturechoose.demo.R.id.*
import com.zyl.picturechoose.demo.activity.MultipleChooseActivity.Companion.currentFileName
import com.zyl.picturechoose.demo.app.PictureConstant
import com.zyl.picturechoose.demo.model.ImgInfor
import com.zyl.picturechoose.demo.viewholder.PopListHolder
import kotlinx.android.synthetic.main.activity_picture_choose.*
import java.io.File
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class PictureChooseActivity : AppCompatActivity() {
    var simpleDateFormat: SimpleDateFormat? = null
    //父类文件名集合
    var parentNameList = ArrayList<String>()
    var dataList = ArrayList<ImgInfor>()
    var allImgList = ArrayList<ImgInfor>()
    //存放所有图片路径集合
    var map = HashMap<String, ArrayList<ImgInfor>>()
    var adapter: BaseRecycleAdapter<ImgInfor>? = null
    var popAdapter: BaseRecycleAdapter<String>? = null
    var popWindow: PopupWindow? = null
    var popView: View? = null
    var popRecycleView: ZRecycleView? = null
    val REQUEST_CARMERA = 0x11
    var headerView: View? = null
    //图片选择存放文件
    var photoFile: File? = null
    var selectPath = ""
    var iscrop = false
    var REQUEST_CUT_PHOTO = 0x08
    //图片裁剪存放文件
    var cropFile: File? = null
    var cropUri: Uri? = null
    var selectedImgInfor: ImgInfor? = null
    var cropFileName = ""
    var photoFileNmae = ""
    var themeColor: Int = 0

    companion object {
        var currentFileName = ""

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_choose)
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

   fun initdata(){
       if (intent != null) {
           iscrop = intent.getBooleanExtra(PictureConstant.INTENT_IS_CROP, false)
           themeColor = intent.getIntExtra(PictureConstant.INTENT_THEME_COLOR, R.color.colorPrimary)
       }
       layout_title.setBackgroundColor(themeColor)
       layout_parent_type.setBackgroundColor(themeColor)
       simpleDateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")

   }

    private fun initListPopWindow() {
        popView = LayoutInflater.from(this).inflate(R.layout.layout_picture_list_pop, null)
        popView!!.background.alpha = 200
        popRecycleView = popView!!.findViewById<ZRecycleView>(R.id.zrecycleview)
        popRecycleView!!.setLayoutManager(LinearLayoutManager(this))
        popRecycleView!!.setZItemDecoration(ZItemDecoration(this, LinearLayoutManager.VERTICAL, DensityUtil.dip2px(this, 1f), resources.getColor(R.color.gray_color)))
        popAdapter = object : BaseRecycleAdapter<String>(this, parentNameList, R.layout.layout_pop_list_recycle_item) {
            override fun getViewHolder(p0: View?): BaseViewHolder<String> {
                return PopListHolder(p0!!, map, PictureConstant.PICTURE_MODEL_SINGLE)
            }
        }
        popRecycleView!!.setAdapter(popAdapter)


        //获取主布局宽高  设置popwindow高度
        var viewTreeObserver = layout_picture_choose_main.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                popWindow = PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, layout_picture_choose_main.measuredHeight - DensityUtil.dip2px(this@PictureChooseActivity, 50f) * 2)
                popWindow!!.animationStyle = R.style.popwindow_style
                popWindow!!.isFocusable = true
                popWindow!!.update()
                layout_picture_choose_main.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }

        })
    }


    fun startCropCamra(uri: Uri) {
        var file = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        cropFileName = "crop_crop_photo_" + simpleDateFormat!!.format(Date())
        var suffix = ".jpg"
        cropFile = File.createTempFile(cropFileName, suffix, file)
        var intent = Intent()
        intent.action = "com.android.camera.action.CROP"
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("scale", true)
        intent.putExtra("crop", "true")
        intent.putExtra("return-data", false)

        //设置裁剪的宽高
        intent.putExtra("outputX", DensityUtil.dip2px(this, 200f)) //200dp
        intent.putExtra("outputY", DensityUtil.dip2px(this, 200f))
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        cropUri = Uri.fromFile(cropFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri)
        startActivityForResult(intent, REQUEST_CUT_PHOTO)
    }

    private fun initEvent() {
        adapter!!.setOnItemClickListener(object : BaseRecycleAdapter.OnZRecycleViewItemClickListener {
            override fun onItemClick(p0: Int) {
                if (adapter!!.headerview != null) {
                    if (p0 != 0) {
                        selectedImgInfor = dataList.get(p0 - 1)
                        selectPath = selectedImgInfor!!.path
                    }
                } else {
                    selectedImgInfor = dataList.get(p0)
                    selectPath = selectedImgInfor!!.path
                }

                var intent = Intent()
                if (iscrop) {
                    var file = File(selectPath)
                    var uri = FileProvider.getUriForFile(this@PictureChooseActivity, PictureConstant.AUTHOR_NAME, file)
                    startCropCamra(uri)

                } else {
                    intent.putExtra(PictureConstant.INTENT_RESULR_DATA, selectedImgInfor)
                    intent.putExtra(PictureConstant.PICTURE_CHOOSE_MODEL, PictureConstant.PICTURE_MODEL_SINGLE)

                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

            }
        })

        headerView!!.setOnClickListener(View.OnClickListener {
            //文件存在包名下的文件  应用卸载即删除
            var cachfile = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            photoFileNmae = "myphoto_carmra_" + simpleDateFormat!!.format(Date())
            photoFile = File.createTempFile(photoFileNmae, ".jpg", cachfile)

            var uri: Uri? = null
            var carmeraInent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //7.0 打开相机
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, PictureConstant.AUTHOR_NAME, photoFile)
                carmeraInent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                carmeraInent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                uri = Uri.fromFile(photoFile)
                carmeraInent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

            }
            startActivityForResult(carmeraInent, REQUEST_CARMERA)
        })

        popAdapter!!.setOnItemClickListener(object : BaseRecycleAdapter.OnZRecycleViewItemClickListener {
            override fun onItemClick(p0: Int) {
                var itemFileName = parentNameList.get(p0)
                if (!itemFileName.equals(currentFileName)) {
                    var list = map[itemFileName]
                    if (adapter != null) {
                        adapter!!.clear()
                        adapter!!.addAll(list)
                        popAdapter!!.notifyDataSetChanged()
                    }
                    currentFileName = itemFileName


                    //是否显示拍照
                    isshowCarmera()
                    if (currentFileName.equals("all")) tv_photo_file_type.setText(R.string.all_picture) else tv_photo_file_type.setText(currentFileName)

                }
                popWindow!!.dismiss()
            }

        })


        tv_photo_file_type.setOnClickListener(View.OnClickListener {
            if (!popWindow!!.isShowing) {
                popWindow!!.showAsDropDown(layout_title, 0, 0)
            } else {
                popWindow!!.dismiss()
            }
        })

        layout_title_back.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

    private fun isshowCarmera() {
        if (currentFileName.equals("all")) {
            if (adapter!!.headerview == null) {
                var headerParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                headerParams.leftMargin = DensityUtil.dip2px(this, 5f)
                adapter!!.addHeader(headerView, headerParams)
            }
        } else {
            if (adapter!!.headerview != null) {
                adapter!!.removeHeader(headerView)
            }
        }
    }

    private fun initRecycleView() {

        //默认显示所有图片
        setDefListShow()
        zrecycleview.setLayoutManager(GridLayoutManager(this, 4))
        zrecycleview.setZItemDecoration(ZItemDecoration(this, LinearLayoutManager.VERTICAL, DensityUtil.dip2px(this, 5f), Color.WHITE))
        adapter = object : BaseRecycleAdapter<ImgInfor>(this, dataList, R.layout.layout_picture_list_recycle_item) {
            override fun getViewHolder(p0: View?): BaseViewHolder<ImgInfor> {
                return object : BaseViewHolder<ImgInfor>(p0) {
                    override fun setdata(s: ImgInfor?) {
                        try {
                            if (!TextUtils.isEmpty(s!!.path)) {
                                var imgView = itemView as ImageView
                                Glide.with(this@PictureChooseActivity).load(s!!.path).into(imgView)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }

        //添加拍照
        headerView = LayoutInflater.from(this).inflate(R.layout.layout_picture_list_recycle_header, null)
        headerView!!.findViewById<ImageView>(R.id.img_header).setImageResource(R.mipmap.icon_carmera)
        var headerParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
        headerParams.leftMargin = DensityUtil.dip2px(this, 5f)
        adapter!!.addHeader(headerView, headerParams)
        zrecycleview.setAdapter(adapter)


    }

    private fun setDefListShow() {
        var defFileName = parentNameList[0]
        var defChildList = map[defFileName]
        currentFileName = defFileName
        if (defChildList != null && defChildList.size > 0) dataList.addAll(defChildList!!)
        if (defFileName.equals("all")) tv_photo_file_type.setText(R.string.all_picture) else tv_photo_file_type.setText(defFileName)

    }

    /**
     * 初始化数据  转化为map 集合
     */
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
                var takenDate = cursor.getString(imgdateColumn3)

                addDate = simpleDateFormat!!.format(Date(addDate.toLong()))
                modifieDate = simpleDateFormat!!.format(Date(modifieDate.toLong()))
                takenDate = simpleDateFormat!!.format(Date(takenDate.toLong()))

                //验证是否存在
                var file = File(path)
                if (!file.exists()) {
                    continue
                }

                //获取所在文件路径和文件名
                var parentpath = file.parentFile.path
                var parentName = file.parentFile.name
                var imgInfor = ImgInfor()
                if (!TextUtils.isEmpty(imgname)) imgInfor.name = imgname
                if (!TextUtils.isEmpty(path)) imgInfor.path = path
                if (!TextUtils.isEmpty(imgdes)) imgInfor.imgdes = imgdes
                if (!TextUtils.isEmpty(parentName)) imgInfor.parentname = parentName
                if (!TextUtils.isEmpty(parentpath)) imgInfor.parentpath = parentpath
                if (!TextUtils.isEmpty(addDate)) imgInfor.adddate = addDate
                if (!TextUtils.isEmpty(modifieDate)) imgInfor.modifiedate = modifieDate
                if (!TextUtils.isEmpty(takenDate)) imgInfor.takendate = takenDate

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
                map.put("all", allImgList)


            }
        } catch (e: Exception) {
            runOnUiThread(Runnable {
                Toast.makeText(this, "load picture fail", Toast.LENGTH_SHORT).show()
            })
        } finally {
            cursor.close()
        }

        //添加所有图片类别
        parentNameList.add(0, "all")


        //图片按日期降序排列
        if (parentNameList.size > 1) {
            for (parentName in parentNameList) {
                Collections.reverse(map[parentName])
            }
        }
    }

    fun saveDataToImgInfor(path: String, name: String): ImgInfor {
        var imgInfor = ImgInfor()
        if (!TextUtils.isEmpty(path)) imgInfor.path = path

        //验证是否存在
        var file = File(path)
        if (!file.exists()) {
            return imgInfor
        }

        //获取所在文件路径和文件名
        var parentpath = file.parentFile.path
        var parentName = file.parentFile.name
        if (!TextUtils.isEmpty(parentName)) imgInfor.parentname = parentName
        if (!TextUtils.isEmpty(parentpath)) imgInfor.parentpath = parentpath
        if (!TextUtils.isEmpty(name)) imgInfor.name = name
        if (!TextUtils.isEmpty(file.lastModified().toString())) imgInfor.modifiedate = file.lastModified().toString()
        return imgInfor

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CARMERA) {
                //照相不裁剪
                if (!iscrop) {
                    var intent = Intent()
                    intent.putExtra(PictureConstant.INTENT_RESULR_DATA, saveDataToImgInfor(photoFile!!.absolutePath, photoFileNmae))
                    intent.putExtra(PictureConstant.PICTURE_CHOOSE_MODEL, PictureConstant.PICTURE_MODEL_SINGLE)
                    setResult(RESULT_OK, intent)
                    finish()

                    //照相裁剪
                } else {
                    var uri: Uri? = null
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //7.0处理
                        uri = FileProvider.getUriForFile(this, PictureConstant.AUTHOR_NAME, photoFile)
                    } else {
                        uri = Uri.fromFile(photoFile)
                    }
                    startCropCamra(uri)
                }
               //选择图片裁剪
            } else if (requestCode == REQUEST_CUT_PHOTO) {
                var intent = Intent()
                intent.putExtra(PictureConstant.INTENT_RESULR_DATA, saveDataToImgInfor(cropFile!!.absolutePath, cropFileName))
                intent.putExtra(PictureConstant.PICTURE_CHOOSE_MODEL, PictureConstant.PICTURE_MODEL_SINGLE)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        if (popWindow != null && popWindow!!.isShowing) popWindow!!.dismiss()
        super.onBackPressed()
    }


}