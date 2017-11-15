package ga.chrom_web.player.multiplayer.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View


class CircleView : View {

    private lateinit var paint: Paint

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        paint = Paint()
    }

    public fun setCircleColor(color: Int) {
        paint.color = color;
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), paint)
    }
}
