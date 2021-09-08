package com.example.android.loadingstatus

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoaderButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var progress = 0
    var bgColor = 0
    var progressColor = 0

    init {
        val typedArray : TypedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.LoaderButton,
                defStyleAttr,
                0
        )

        with(typedArray) {
            bgColor = getColor(
                    R.styleable.LoaderButton_buttonColor,
                    resources.getColor(R.color.colorPrimary)
            )
            progressColor = getColor(
                    R.styleable.LoaderButton_progressColor,
                    resources.getColor(R.color.colorPrimaryDark)
            )
        }

        typedArray.recycle()
    }

    private var valueAnimator = ValueAnimator.ofFloat()

    /*Style used for the button text once is clicked*/
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50f
        typeface = Typeface.create("", Typeface.NORMAL)
        color = Color.WHITE

    }

    /*Style used for the firts state of the button*/
    private val paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = bgColor
    }

    /*Style used for the the button during the animation*/
    private val paintButtonAnimation = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = progressColor

    }

    /*Style used for the Pacman Circle animation*/
    private val paintPacman = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.colorAccent)

    }

    /*Observable for setting up the current state of the button*/
    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Completed -> setBackgroundColor(Color.RED)
            ButtonState.Clicked -> {
                animator()
            }

            ButtonState.Loading -> {
                animator()
            }

        }
    }



    private fun animator() {

        valueAnimator = ValueAnimator.ofInt(0, 1080).apply {
            addUpdateListener {
                progress = animatedValue as Int
                invalidate()
            }
            duration = 3000
            start()
            postDelayed({
                updateButtonState(
                        ButtonState.Completed,
                        ButtonState.Loading.fieName
                )
                updateStatus(ButtonState.Loading.status )
            }, duration)
        }

    }

    fun updateButtonState(state: ButtonState, fileName: String) {
        buttonState = state
        buttonState.fieName = fileName

    }

    fun updateStatus(status: String) {
        ButtonState.Loading.status = status
    }

    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintButton)

        if (buttonState == ButtonState.Clicked || buttonState == ButtonState.Loading) {
            canvas?.drawRect(0f, 0f, progress.toFloat(), heightSize.toFloat(), paintButtonAnimation)
            canvas?.drawArc(
                    widthSize - 145f,
                    heightSize / 2 - 35f,
                    widthSize - 75f,
                    heightSize / 2 + 35f,
                    0F,
                    progress / 2f,
                    true,
                    paintPacman
            )
        }
        canvas?.drawText(buttonState.text, widthSize / 2f, heightSize / 2f + 18, paintText)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                View.MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}