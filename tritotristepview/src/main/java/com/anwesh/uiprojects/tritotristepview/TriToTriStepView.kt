package com.anwesh.uiprojects.tritotristepview

/**
 * Created by anweshmishra on 01/04/20.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.app.Activity

val nodes : Int = 5
val tri : Int = 2
val scGap : Float = 0.02f / (tri + 1)
val delay : Long = 20
val lineSizeFactor : Float = 3f
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")

fun Int.inverse() : Float = 1f / this
fun Float.divideScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.maxScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()


fun Canvas.drawTriOrLine(i : Int, x : Float, size : Float, gap : Float, scale : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sci : Float = sf.divideScale(i, 2 * tri - 1)
    save()
    translate(x, 0f)
    if (i % 2 == 0) {

        for (j in 0..1) {
            val scij : Float = sci.divideScale(j, 2)
            drawLine(j * gap / 2, j * -size, (gap / 2) * scij, -size * (1f - 2 * j) * scij, paint)
        }
    } else {
        drawLine(0f, 0f, gap * sci, 0f, paint)
    }
    restore()
}

fun Canvas.drawTriToTriStep(scale : Float, w : Float, size : Float, paint : Paint) {
    val gap : Float = w / ((tri - 1) * lineSizeFactor + (tri))
    var x : Float = 0f
    for (j in 0..tri) {
        drawTriOrLine(j, x, size, gap, scale, paint)
        x += (j % 2) * lineSizeFactor * gap
    }
}

fun Canvas.drawTTTNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(0f, gap * (i + 1))
    drawTriToTriStep(scale, w, size, paint)
    restore()
}

class TriToTriStepView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
}