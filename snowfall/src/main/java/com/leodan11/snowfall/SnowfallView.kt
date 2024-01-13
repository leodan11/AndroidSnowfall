package com.leodan11.snowfall

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.HandlerThread
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

class SnowfallView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val snowflakesNum: Int
    private var snowflakeImage: Bitmap?
    private var snowflakeImages: MutableList<Bitmap> = mutableListOf()
    private val snowflakeAlphaMin: Int
    private val snowflakeAlphaMax: Int
    private val snowflakeAngleMax: Int
    private val snowflakeSizeMinInPx: Int
    private val snowflakeSizeMaxInPx: Int
    private val snowflakeSpeedMin: Int
    private val snowflakeSpeedMax: Int
    private val snowflakesFadingEnabled: Boolean
    private val snowflakesAlreadyFalling: Boolean
    private val snowflakesMultipleImages: Boolean

    private lateinit var updateSnowflakesThread: UpdateSnowflakesThread
    private var snowflakes: Array<Snowflake>? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SnowfallView)
        try {
            snowflakesNum = a.getInt(R.styleable.SnowfallView_snowflakesNum, DEFAULT_SNOWFLAKES_NUM)
            snowflakeImage = a.getDrawable(R.styleable.SnowfallView_snowflakeImage)?.toBitmap()
            snowflakeAlphaMin =
                a.getInt(R.styleable.SnowfallView_snowflakeAlphaMin, DEFAULT_SNOWFLAKE_ALPHA_MIN)
            snowflakeAlphaMax =
                a.getInt(R.styleable.SnowfallView_snowflakeAlphaMax, DEFAULT_SNOWFLAKE_ALPHA_MAX)
            snowflakeAngleMax =
                a.getInt(R.styleable.SnowfallView_snowflakeAngleMax, DEFAULT_SNOWFLAKE_ANGLE_MAX)
            snowflakeSizeMinInPx = a.getDimensionPixelSize(
                R.styleable.SnowfallView_snowflakeSizeMin,
                dpToPx(DEFAULT_SNOWFLAKE_SIZE_MIN_IN_DP)
            )
            snowflakeSizeMaxInPx = a.getDimensionPixelSize(
                R.styleable.SnowfallView_snowflakeSizeMax,
                dpToPx(DEFAULT_SNOWFLAKE_SIZE_MAX_IN_DP)
            )
            snowflakeSpeedMin =
                a.getInt(R.styleable.SnowfallView_snowflakeSpeedMin, DEFAULT_SNOWFLAKE_SPEED_MIN)
            snowflakeSpeedMax =
                a.getInt(R.styleable.SnowfallView_snowflakeSpeedMax, DEFAULT_SNOWFLAKE_SPEED_MAX)
            snowflakesFadingEnabled = a.getBoolean(
                R.styleable.SnowfallView_snowflakesFadingEnabled,
                DEFAULT_SNOWFLAKES_FADING_ENABLED
            )
            snowflakesAlreadyFalling = a.getBoolean(
                R.styleable.SnowfallView_snowflakesAlreadyFalling,
                DEFAULT_SNOWFLAKES_ALREADY_FALLING
            )
            snowflakesMultipleImages = a.getBoolean(
                R.styleable.SnowfallView_snowflakesMultipleImages,
                DEFAULT_SNOWFLAKES_MULTIPLE_IMAGES
            )

            setLayerType(LAYER_TYPE_HARDWARE, null)

        } finally {
            a.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateSnowflakesThread = UpdateSnowflakesThread()
    }

    override fun onDetachedFromWindow() {
        updateSnowflakesThread.quit()
        super.onDetachedFromWindow()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        snowflakes = createSnowflakes()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView === this && visibility == GONE) {
            snowflakes?.forEach { it.reset() }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isInEditMode) {
            return
        }

        var haveAtLeastOneVisibleSnowflake = false

        val localSnowflakes = snowflakes
        if (localSnowflakes != null) {
            for (snowflake in localSnowflakes) {
                if (snowflake.isStillFalling()) {
                    haveAtLeastOneVisibleSnowflake = true
                    snowflake.draw(canvas)
                }
            }
        }

        if (haveAtLeastOneVisibleSnowflake) {
            updateSnowflakes()
        } else {
            visibility = GONE
        }

        val fallingSnowflakes = snowflakes?.filter { it.isStillFalling() }
        if (fallingSnowflakes?.isNotEmpty() == true) {
            fallingSnowflakes.forEach { it.draw(canvas) }
            updateSnowflakes()
        } else {
            visibility = GONE
        }
    }

    /**
     * Sets a drawable as the content of this SnowfallView.
     *
     * @param drawable – the [Drawable] to set the content
     */
    fun setSnowflakeImageDrawable(drawable: Drawable) {
        snowflakeImage = drawable.toBitmap()
    }

    /**
     * Sets a list drawables as the content of this SnowfallView.
     *
     * @param drawables – the list [Drawable] to set the content
     */
    fun setSnowflakeImageDrawables(drawables: List<Drawable>) {
        drawables.forEach {
            snowflakeImages.add(it.toBitmap())
        }
    }

    /**
     * Sets a Bitmap as the content of this SnowfallView.
     *
     * @param bitmap – the [Bitmap] to set the content
     *
     */
    fun setSnowflakeImageBitmap(bitmap: Bitmap) {
        snowflakeImage = bitmap
    }

    /**
     * Sets a list Bitmap as the content of this SnowfallView.
     *
     * @param bitmaps – the list [Bitmap] to set the content
     *
     */
    fun setSnowflakeImageBitmaps(bitmaps: List<Bitmap>) {
        bitmaps.forEach {
            snowflakeImages.add(it)
        }
    }

    /**
     * Sets a resource as the content of this SnowfallView.
     * This does Bitmap reading and decoding on the UI thread, which can cause a latency hiccup. If that's a concern, consider using [setSnowflakeImageDrawable] or [setSnowflakeImageBitmap] and [android.graphics.BitmapFactory] instead.
     *
     * @param resId – the resource identifier of the drawable
     *
     */
    fun setSnowflakeResource(@DrawableRes resId: Int) {
        snowflakeImage = ContextCompat.getDrawable(context!!, resId)?.toBitmap()
    }

    /**
     * Sets a list resources as the content of this SnowfallView.
     * This does Bitmap reading and decoding on the UI thread, which can cause a latency hiccup. If that's a concern, consider using [setSnowflakeImageDrawables] or [setSnowflakeImageBitmaps] and [android.graphics.BitmapFactory] instead.
     *
     * @param resIds – the resource identifier of the drawable
     *
     */
    fun setSnowflakeResources(@DrawableRes resIds: List<Int>) {
        resIds.forEach {
            snowflakeImages.add(ContextCompat.getDrawable(context!!, it)?.toBitmap()!!)
        }
    }

    fun stopFalling() {
        snowflakes?.forEach { it.shouldRecycleFalling = false }
    }

    fun restartFalling() {
        snowflakes?.forEach { it.shouldRecycleFalling = true }
    }

    private fun createSnowflakes(): Array<Snowflake> {
        val randomizer = Randomizer()

        return when (snowflakesMultipleImages && snowflakeImages.isNotEmpty()) {
            true -> {
                Array(snowflakesNum) {
                    val snowflakeParams = Snowflake.Params(
                        parentWidth = width,
                        parentHeight = height,
                        image = snowflakeImages.random(),
                        alphaMin = snowflakeAlphaMin,
                        alphaMax = snowflakeAlphaMax,
                        angleMax = snowflakeAngleMax,
                        sizeMinInPx = snowflakeSizeMinInPx,
                        sizeMaxInPx = snowflakeSizeMaxInPx,
                        speedMin = snowflakeSpeedMin,
                        speedMax = snowflakeSpeedMax,
                        fadingEnabled = snowflakesFadingEnabled,
                        alreadyFalling = snowflakesAlreadyFalling
                    )
                    Snowflake(randomizer, snowflakeParams)
                }
            }

            else -> {
                val snowflakeParams = Snowflake.Params(
                    parentWidth = width,
                    parentHeight = height,
                    image = snowflakeImage,
                    alphaMin = snowflakeAlphaMin,
                    alphaMax = snowflakeAlphaMax,
                    angleMax = snowflakeAngleMax,
                    sizeMinInPx = snowflakeSizeMinInPx,
                    sizeMaxInPx = snowflakeSizeMaxInPx,
                    speedMin = snowflakeSpeedMin,
                    speedMax = snowflakeSpeedMax,
                    fadingEnabled = snowflakesFadingEnabled,
                    alreadyFalling = snowflakesAlreadyFalling
                )
                Array(snowflakesNum) { Snowflake(randomizer, snowflakeParams) }
            }
        }
    }

    private fun updateSnowflakes() {
        updateSnowflakesThread.handler.post {
            var haveAtLeastOneVisibleSnowflake = false

            val localSnowflakes = snowflakes ?: return@post

            for (snowflake in localSnowflakes) {
                if (snowflake.isStillFalling()) {
                    haveAtLeastOneVisibleSnowflake = true
                    snowflake.update()
                }
            }

            if (haveAtLeastOneVisibleSnowflake) {
                postInvalidateOnAnimation()
            }
        }
    }

    private class UpdateSnowflakesThread : HandlerThread("SnowflakesComputations") {
        val handler: Handler

        init {
            start()
            handler = Handler(looper)
        }
    }

    companion object {
        private const val DEFAULT_SNOWFLAKES_NUM = 200
        private const val DEFAULT_SNOWFLAKE_ALPHA_MIN = 150
        private const val DEFAULT_SNOWFLAKE_ALPHA_MAX = 250
        private const val DEFAULT_SNOWFLAKE_ANGLE_MAX = 10
        private const val DEFAULT_SNOWFLAKE_SIZE_MIN_IN_DP = 2
        private const val DEFAULT_SNOWFLAKE_SIZE_MAX_IN_DP = 8
        private const val DEFAULT_SNOWFLAKE_SPEED_MIN = 2
        private const val DEFAULT_SNOWFLAKE_SPEED_MAX = 8
        private const val DEFAULT_SNOWFLAKES_FADING_ENABLED = false
        private const val DEFAULT_SNOWFLAKES_ALREADY_FALLING = false
        private const val DEFAULT_SNOWFLAKES_MULTIPLE_IMAGES = false
    }

}