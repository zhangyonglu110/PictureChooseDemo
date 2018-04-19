package com.zyl.myview.picture.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.zyl.myview.picture.util.DensityUtil

/**
 * Created by zhangyonglu on 2018/4/11.
 */
class PopCircleView: View {
    var paint= Paint()
    constructor(context:Context):super(context){init(null)}
    constructor(context:Context,attr:AttributeSet):super(context,attr){init(attr)}

    override fun onDraw(canvas: Canvas?) {
        canvas!!.translate(width/2.toFloat(),height/2.toFloat())
        paint.style=Paint.Style.FILL
        canvas.drawCircle(0f,0f, DensityUtil.dip2px(context,6.5f).toFloat(),paint)

        paint.style=Paint.Style.STROKE
        paint.strokeWidth= DensityUtil.dip2px(context,2f).toFloat()

        canvas.drawCircle(0f,0f, DensityUtil.dip2px(context,10f).toFloat(),paint)

        super.onDraw(canvas)
    }



   fun init(attr:AttributeSet?){
          paint.isAntiAlias=true
          paint.color= Color.GREEN
   }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        var heightMode=MeasureSpec.getMode(heightMeasureSpec)
        var widthMode=MeasureSpec.getMode(widthMeasureSpec)
        var defHeightSize=MeasureSpec.getSize(heightMeasureSpec)
        var defWidthSize=MeasureSpec.getSize(widthMeasureSpec)

        var width=defWidthSize
        var height=defHeightSize
        if(heightMode==MeasureSpec.AT_MOST){
            height= DensityUtil.dip2px(context,12f)*2
        }
        if(widthMode==MeasureSpec.AT_MOST){
            width= DensityUtil.dip2px(context,12f)*2
        }

        setMeasuredDimension(width,height)

    }
}