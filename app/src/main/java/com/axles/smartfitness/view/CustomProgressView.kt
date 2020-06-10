package com.axles.smartfitness.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import com.axles.smartfitness.R
import com.axles.smartfitness.extensions.toRadian
import kotlin.math.cos
import kotlin.math.sin


class CustomProgressView: View {
	companion object {
		fun convertDPtoPixel(context: Context, dp: Float): Float {
			val resources = context.resources
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
		}

		fun convertSPtoPixel(context: Context, sp: Float): Float {
			val resources = context.resources
			return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
		}
	}
	@ColorInt private var placeHolderColor: Int = -0xbebeb
	@ColorInt private var startColor: Int = -0xffad51
	@ColorInt private var endColor: Int = -0xffcd2c
	private var startAngle = 0f
	private var circleWidth = 0f
	private var max: Float = 100f
	private var progress: Float = 50f

	constructor(context: Context): this(context, null)
	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		if (attrs == null) { return }

		val properties = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomProgressView, 0, 0)
		placeHolderColor = properties.getColor(R.styleable.CustomProgressView_placeHolderColor, placeHolderColor)
		startColor = properties.getColor(R.styleable.CustomProgressView_startColor, startColor)
		endColor = properties.getColor(R.styleable.CustomProgressView_endColor, endColor)
		startAngle = properties.getFloat(R.styleable.CustomProgressView_startAngle, startAngle)
		circleWidth = properties.getDimensionPixelSize(R.styleable.CustomProgressView_circleWidth, circleWidth.toInt()).toFloat()
		max = properties.getFloat(R.styleable.CustomProgressView_max, max)
		progress = properties.getFloat(R.styleable.CustomProgressView_progress, progress)
	}

	private var centerX = 0f
	private var centerY = 0f
	private var radius = 0f

	fun setMax(max: Int) {
		this.max = max.toFloat()
		invalidate()
	}

	fun setProgress(progress: Float) {
		this.progress = progress
		invalidate()
	}

	fun reset() {
		setProgress(max)
	}

	override fun onDraw(canvas: Canvas?) {
		super.onDraw(canvas)

		if (canvas == null) { return }

		centerX = (width / 2).toFloat()
		centerY = (height / 2).toFloat()
		radius = (width / 2).toFloat() - circleWidth

		drawPlaceHolder(canvas)
		drawStartHeader(canvas)
		drawEndHeader(canvas)
		drawProgress(canvas)
	}

	private fun drawPlaceHolder(canvas: Canvas) {
		val radius = this.radius

		val paint = Paint()
		paint.isAntiAlias = true
		paint.strokeWidth = circleWidth
		paint.style = Paint.Style.STROKE
		paint.isDither = true
		paint.color = placeHolderColor

		canvas.drawCircle(centerX, centerY, radius, paint)
	}

	private fun drawStartHeader(canvas: Canvas) {
		val x = this.radius * cos(startAngle.toRadian()) + this.centerX
		val y = this.radius * sin(startAngle.toRadian()) + this.centerY

		val paint = Paint()
		paint.isAntiAlias = true
		paint.color = startColor

		canvas.drawCircle(x, y, circleWidth/2, paint)
	}

	private fun drawEndHeader(canvas: Canvas) {
		val angle = (progress / max) * 360f + startAngle

		val x = this.radius * cos(angle.toRadian()) + this.centerX
		val y = this.radius * sin(angle.toRadian()) + this.centerY

		val paint = Paint()
		paint.isAntiAlias = true
		paint.color = startColor

		canvas.drawCircle(x, y, circleWidth/2, paint)
	}

	private fun drawProgress(canvas: Canvas) {
		val angle = (progress / max) * 360f

		val sweepGradient = SweepGradient(centerX, centerY, intArrayOf(startColor, endColor, startColor), floatArrayOf(0f, angle/360f/2f, angle/360f))
		sweepGradient.apply {
			val gradientMatrix = Matrix()
			gradientMatrix.preRotate(startAngle, centerX, centerY)
			setLocalMatrix(gradientMatrix)
		}

		val paint = Paint()
		paint.isAntiAlias = true
		paint.strokeWidth = circleWidth
		paint.style = Paint.Style.STROKE
		paint.isDither = true
		paint.shader = sweepGradient

		val left = this.centerX - this.radius
		val right = left + this.radius * 2
		val top = this.centerY - this.radius
		val bottom = top + this.radius * 2

		canvas.drawArc(left, top, right, bottom, startAngle, angle, false, paint)
//		canvas.drawCircle(centerX, centerY, radius, paint)
	}
}