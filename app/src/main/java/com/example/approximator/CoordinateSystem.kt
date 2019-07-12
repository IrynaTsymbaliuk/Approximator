package com.example.approximator

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View

class CoordinateSystem(context: Context) : View(context) {

    private var viewWidth = 0
    private var viewHeight = 0
    private var padding = 0
    private var step = 0

    private val axisPaint = Paint()
    private val arrowPaint = Paint()
    private val axisDividersPaint = Paint()
    private val textPaint = Paint()
    private val pointStrokePaint = Paint()
    private val pointFillPaint = Paint()
    private val mnkPaint = Paint()

    private val path = Path()

    private val axisXSize = 7
    private val axisYSize = 10
    private var arrowSize = 0

    private var listOfPoints = ArrayList<Point>()

    private var selectedPointY = 0F
    private var selectedPointIndex = -1

    private var xList = DoubleArray(5)
    private var yList = DoubleArray(5)

    init {
        padding = convertDpToPixel(context, 32)

        axisPaint.style = Paint.Style.STROKE
        axisPaint.strokeWidth = (convertDpToPixel(context, 2)).toFloat()

        axisDividersPaint.style = Paint.Style.STROKE
        axisDividersPaint.strokeWidth = (convertDpToPixel(context, 1)).toFloat()

        textPaint.textSize = (convertDpToPixel(context, 12)).toFloat()

        pointStrokePaint.color = Color.parseColor("#6300a5")
        pointStrokePaint.style = Paint.Style.STROKE
        pointStrokePaint.strokeWidth = (convertDpToPixel(context, 2)).toFloat()

        pointFillPaint.color = Color.parseColor("#8500de")
        pointFillPaint.style = Paint.Style.FILL

        mnkPaint.color = Color.parseColor("#e6324b")
        mnkPaint.style = Paint.Style.STROKE
        mnkPaint.strokeWidth = (convertDpToPixel(context, 3)).toFloat()

        arrowSize = convertDpToPixel(context, 6)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        this.viewWidth = w
        this.viewHeight = h

        invalidate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        viewWidth = width
        viewHeight = height

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        viewHeight = height
        viewWidth = width

        initStep()

        drawCoordinateSystem(canvas)
    }

    private fun initStep(){

        step = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            (viewWidth - padding * 2) / (axisXSize + 1)
        } else {
            (viewHeight - padding * 2) / (axisYSize + 1)
        }
    }

    private fun drawCoordinateSystem(canvas: Canvas) {
        drawAxisX(canvas)
        drawAxisY(canvas)
        drawPoints(canvas)
        drawLeastSquaresCurve(canvas)
    }

    private fun drawAxisX(canvas: Canvas) {
        drawLineX(canvas)
        drawArrowAndNameOfAxisX(canvas)
        drawStepDividersX(canvas)
    }

    private fun drawLineX(canvas: Canvas) {
        val startX = padding
        val stopX = padding + step * (axisXSize + 1)
        val startY = padding + step * axisYSize
        val stopY = padding + step * axisYSize
        canvas.drawLine(startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), axisPaint)
    }

    private fun drawArrowAndNameOfAxisX(canvas: Canvas) {
        val x = (padding + step * (axisXSize + 1)).toFloat()
        val y = (padding + step * axisYSize).toFloat()
        path.reset()
        path.moveTo(x, y)
        path.lineTo(x - arrowSize, y - arrowSize)
        path.lineTo(x - arrowSize, y + arrowSize)
        path.lineTo(x, y)
        path.close()
        canvas.drawPath(path, arrowPaint)
        canvas.drawText("X", x, y + step / 2, textPaint)
    }

    private fun drawStepDividersX(canvas: Canvas) {
        val startY = padding + (step * (axisYSize - 0.25)).toFloat()
        val stopY = padding + (step * (axisYSize + 0.25)).toFloat()
        val textY = padding + (step * (axisYSize + 0.75)).toFloat()
        var x = (padding + step).toFloat()
        var number = 0
        while (number <= 6) {
            canvas.drawLine(x, startY, x, stopY, axisDividersPaint)
            canvas.drawText(number.toString(), x, textY, textPaint)
            x += step
            number += 1
        }
    }

    private fun drawAxisY(canvas: Canvas) {
        drawLineY(canvas)
        drawArrowAndNameOfAxisY(canvas)
        drawStepDividersY(canvas)
    }

    private fun drawLineY(canvas: Canvas) {
        val startX = (padding + step).toFloat()
        val stopX = (padding + step).toFloat()
        val startY = padding.toFloat()
        val stopY = (padding + step * (axisYSize + 1)).toFloat()
        canvas.drawLine(startX, startY, stopX, stopY, axisPaint)
    }

    private fun drawArrowAndNameOfAxisY(canvas: Canvas) {
        val x = (padding + step).toFloat()
        val y = padding.toFloat()
        path.reset()
        path.moveTo(x, y)
        path.lineTo(x - arrowSize, y + arrowSize)
        path.lineTo(x + arrowSize, y + arrowSize)
        path.lineTo(x, y)
        path.close()
        canvas.drawPath(path, arrowPaint)
        canvas.drawText("Y", x - step / 2, y, textPaint)
    }

    private fun drawStepDividersY(canvas: Canvas) {
        val y = (padding + step).toFloat()
        val x = (padding + step * 1.5).toFloat()
        val number = 10
        canvas.drawText(number.toString(), x, y, textPaint)
    }

    private fun drawPoints(canvas: Canvas) {

        if (listOfPoints.isEmpty()) {
            var i = 2
            while (i <= 6) {
                val point = Point()
                point.x = (padding + step * i)
                point.y = (padding + step * 10)
                listOfPoints.add(point)
                i++
            }
        }

        listOfPoints.forEach {
            xList[listOfPoints.indexOf(it)] = convertPixelsToX(it.x)
            yList[listOfPoints.indexOf(it)] = convertPixelsToY(it.y)

            val x = it.x
            val y = it.y
            val pointSize = convertDpToPixel(context, 6)

            canvas.rotate(45F, x.toFloat(), y.toFloat())

            canvas.drawRect(
                (x - pointSize).toFloat(), (y - pointSize).toFloat(),
                (x + pointSize).toFloat(), (y + pointSize).toFloat(), pointFillPaint
            )
            canvas.drawRect(
                (x - pointSize).toFloat(), (y - pointSize).toFloat(),
                (x + pointSize).toFloat(), (y + pointSize).toFloat(), pointStrokePaint
            )

            canvas.rotate(-45F, x.toFloat(), y.toFloat())
        }

    }

    private fun drawLeastSquaresCurve(canvas: Canvas) {

        val xCoordinateStart = padding + step
        var xCoordinate: Int = xCoordinateStart
        val xCoordinateStop = padding + (step * axisXSize + 1)
        var xVariable: Double

        var yCoordinate: Int
        val yCoordinateStart = convertYToPixels(PolynomialRegression(xList, yList, 3).predict(0.0))
        var yVariable: Double

        path.reset()
        path.moveTo(xCoordinateStart.toFloat(), yCoordinateStart.toFloat())

        while (xCoordinate <= xCoordinateStop) {

            xVariable = convertPixelsToX(xCoordinate)
            yVariable = PolynomialRegression(xList, yList, 3).predict(xVariable)
            yCoordinate = convertYToPixels(yVariable)

            path.lineTo(
                xCoordinate.toFloat(),
                yCoordinate.toFloat()
            )
            xCoordinate++
        }

        canvas.drawPath(path, mnkPaint)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val pressingActionRadius = step / 2

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                listOfPoints.forEach {
                    if ((kotlin.math.abs(it.x - event.x) < pressingActionRadius) && (kotlin.math.abs(it.y - event.y) < pressingActionRadius)) {
                        selectedPointIndex = listOfPoints.indexOf(it)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (selectedPointIndex != -1) {
                    selectedPointY = when {
                        event.y < padding -> padding.toFloat()
                        event.y > padding + step * axisYSize -> (padding + step * axisYSize).toFloat()
                        else -> event.y
                    }
                    listOfPoints[selectedPointIndex].y = selectedPointY.toInt()
                    yList[selectedPointIndex] = convertPixelsToY(selectedPointY.toInt())
                    invalidate()
                }
            }
        }
        return true
    }

    private fun convertDpToPixel(context: Context, dp: Int): Int {
        return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    private fun convertPixelsToY(py: Int): Double {
        return if (py == padding) {
            axisYSize.toDouble()
        } else {
            (axisYSize - (py - padding) /step.toDouble())
        }
    }

    private fun convertPixelsToX(px: Int): Double {
        return if (px == padding + step) {
            0.0
        } else {
            ((px - padding - step) / step.toDouble())
        }
    }

    private fun convertYToPixels(y: Double): Int {
        return (padding + step * axisYSize - step * y).toInt()
    }

}