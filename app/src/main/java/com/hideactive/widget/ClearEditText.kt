package com.hideactive.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText
import com.hideactive.R
import com.module.library.util.EditTextWatcher

/**
 * 带清除按钮的输入框
 *
 * @author zhouchunjie
 * @date 2018/5/6
 */
class ClearEditText : EditText {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var mClearDrawable: Drawable? = null
    private var hasFocus: Boolean = false

    init {
        mClearDrawable = compoundDrawables[2]
        if (mClearDrawable == null) {
            mClearDrawable = ContextCompat.getDrawable(context, R.drawable.ic_clear)
        }
        mClearDrawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
        }

        setClearIconVisible(false)
        setOnFocusChangeListener { _, b ->
            this.hasFocus = b
            if (b) {
                setClearIconVisible(text.isNotEmpty())
            } else {
                setClearIconVisible(false)
            }
        }
        addTextChangedListener(object : EditTextWatcher() {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(hasFocus){
                    setClearIconVisible(text.isNotEmpty())
                }
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            if (compoundDrawables[2] != null) {
                val touchable = event.x > width - totalPaddingRight && event.x < width - paddingRight
                if (touchable) {
                    text = null
                }
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 设置清除按钮是否可见
     */
    private fun setClearIconVisible(visible: Boolean) {
        val right = if (visible) mClearDrawable else null
        setCompoundDrawables(compoundDrawables[0],
                compoundDrawables[1], right, compoundDrawables[3])
    }

    /**
     * 设置密码可见性
     */
    fun setPasswordVisible(visible: Boolean) {
        transformationMethod = if (!visible) {
            HideReturnsTransformationMethod.getInstance()
        } else {
            PasswordTransformationMethod.getInstance()
        }
        setSelection(text.length)
    }
}