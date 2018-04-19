package com.zyl.myview.picture.app

import android.Manifest
import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.RadioGroup
import com.zyl.myview.picture.R
import com.zyl.myview.picture.model.ImgInfor
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    val RESULT_PROMISSON=0x06
    var isCrop=false
    var chooseModel=PictureConstant.PICTURE_MODEL_SINGLE
    var themeColor= Color.BLUE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            checkPermmison()
        }

        btn_open_photo.setOnClickListener(View.OnClickListener {
          ImageSelecterBuilder(this).setIsCrop(isCrop)
                  .setThemeColor(themeColor)
                  .setImageModel(chooseModel)
                  .start()
        })
        rg_crop.setOnCheckedChangeListener(object:RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if(checkedId== R.id.rbt_one) isCrop=false else isCrop=true

            }
        })

        rg_choose_model.setOnCheckedChangeListener(object:RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if(checkedId== R.id.rbt_model_one) chooseModel=PictureConstant.PICTURE_MODEL_SINGLE
               else if(checkedId== R.id.rbt_model_two) chooseModel=PictureConstant.PICTURE_MODEL_MANY

            }
        })

        rg_color.setOnCheckedChangeListener(object:RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if(checkedId== R.id.rbt_color_one) themeColor=Color.RED
                else if(checkedId== R.id.rbt_color_two) themeColor=Color.GREEN
                else if(checkedId== R.id.rbt_color_three) themeColor=Color.BLUE

            }
        })

    }

    private fun checkPermmison() {
        var permissonList= arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        var permissonList2= ArrayList<String>()
        for(permisson in permissonList){
            var permissResult= ContextCompat.checkSelfPermission(this,permisson!!)
            if(permissResult<0){
                permissonList2.add(permisson)
            }
        }

        if(permissonList2.size>0) {
            var permissonArray = arrayOfNulls<String>(permissonList2.size)
            for((i,permisson) in permissonList2.withIndex()){
                permissonArray[i]=permisson
            }
            ActivityCompat.requestPermissions(this,permissonArray,RESULT_PROMISSON)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==Activity.RESULT_OK) {
                 if(data!!.getStringExtra(PictureConstant.PICTURE_CHOOSE_MODEL).equals(PictureConstant.PICTURE_MODEL_SINGLE)) {
                     var imgInfor = data!!.getSerializableExtra(PictureConstant.INTENT_RESULR_DATA) as ImgInfor
                     tv_result.setText(imgInfor.path)
                 }else{
                     var imgInforList = data!!.getSerializableExtra(PictureConstant.INTENT_RESULR_DATA) as ArrayList<ImgInfor>
                     var buffer=StringBuffer()
                     for(imgInfor in imgInforList){
                         buffer.append(imgInfor.path)
                         buffer.append("\n")

                     }
                     tv_result.setText(buffer.toString())
                 }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}
