package com.clerami.intermediate.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.clerami.intermediate.R

class MyButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var txtColorDisabled: Int = 0
    private var txtColorEnabled: Int = 0
    private var enabledBackground: Drawable
    private var disabledBackground: Drawable
    private var textLogin: String
    private var textRegister: String

    init {
        txtColorDisabled = ContextCompat.getColor(context, android.R.color.black)
        txtColorEnabled = ContextCompat.getColor(context, android.R.color.white)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disabledBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable


        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MyButton)
        textLogin = attributes.getString(R.styleable.MyButton_textLogin)
            ?: context.getString(R.string.login_button_text_enabled)
        textRegister = attributes.getString(R.styleable.MyButton_textRegister)
            ?: context.getString(R.string.register_button_text_enabled)
        attributes.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        background = if (isEnabled) enabledBackground else disabledBackground
        setTextColor(if (isEnabled) txtColorEnabled else txtColorDisabled)


        text = if (isEnabled) {
            if (tag == "register") textRegister else textLogin
        } else {
            context.getString(R.string.default_button_text_disabled)
        }

        textSize = 12f
        gravity = Gravity.CENTER
    }
}
