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
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val foreColor : Int = Color.parseColor("#3F51B5")
val backColor : Int = Color.parseColor("#BDBDBD")
