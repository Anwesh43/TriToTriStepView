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
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()


fun Canvas.drawTriOrLine(i : Int, x : Float, size : Float, gap : Float, scale : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val sci : Float = sf.divideScale(i, 2 * tri - 1)
    save()
    translate(x, 0f)
    if (i % 2 == 0) {

        for (j in 0..1) {
            val scij : Float = sci.divideScale(j, 2)
            save()
            translate(j * gap / 2, j * -size)
            drawLine(0f, 0f, (gap / 2) * scij, -size * (1f - 2 * j) * scij, paint)
            restore()
        }
    } else {
        drawLine(0f, 0f, gap * sci * lineSizeFactor, 0f, paint)
    }
    restore()
}

fun Canvas.drawTriToTriStep(scale : Float, w : Float, size : Float, paint : Paint) {
    val gap : Float = w / ((tri - 1) * lineSizeFactor + (tri))
    var x : Float = 0f
    for (j in 0..tri) {
        drawTriOrLine(j, x, size, gap, scale, paint)
        if (j % 2 == 0) {
            x += gap
        } else {
            x += gap * lineSizeFactor
        }
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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class Animator(var view : View, var animated : Boolean = false) {


        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class TTTNode(var i : Int, val state : State = State()) {

        private var next : TTTNode? = null
        private var prev : TTTNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = TTTNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawTTTNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : TTTNode {
            var curr : TTTNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class TriToTriStep(var i : Int) {

        private var curr : TTTNode = TTTNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : TriToTriStepView) {

        private val animator : Animator = Animator(view)
        private val ttt : TriToTriStep = TriToTriStep(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            ttt.draw(canvas, paint)
            animator.animate {
                ttt.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            ttt.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : TriToTriStepView {
            val view : TriToTriStepView = TriToTriStepView(activity)
            activity.setContentView(view)
            return view
        }
    }
}