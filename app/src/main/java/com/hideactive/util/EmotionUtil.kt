package com.hideactive.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.widget.TextView
import com.hideactive.R
import java.util.regex.Pattern


/**
 * Emotion工具类
 *
 * @author zhouchunjie
 * @date 2018/5/10
 */
object EmotionUtil {

    private const val REGEX_EMOTION = "\\\\ue[a-z0-9]{3}"

    val emotions = listOf(
            Emotion("\\ue001", R.mipmap.ue001),
            Emotion("\\ue002", R.mipmap.ue002),
            Emotion("\\ue003", R.mipmap.ue003),
            Emotion("\\ue004", R.mipmap.ue004),
            Emotion("\\ue005", R.mipmap.ue005),
            Emotion("\\ue006", R.mipmap.ue006),
            Emotion("\\ue007", R.mipmap.ue007),
            Emotion("\\ue008", R.mipmap.ue008),
            Emotion("\\ue009", R.mipmap.ue009),
            Emotion("\\ue010", R.mipmap.ue010),
            Emotion("\\ue011", R.mipmap.ue011),
            Emotion("\\ue012", R.mipmap.ue012),
            Emotion("\\ue013", R.mipmap.ue013),
            Emotion("\\ue014", R.mipmap.ue014),
            Emotion("\\ue015", R.mipmap.ue015),
            Emotion("\\ue016", R.mipmap.ue016),
            Emotion("\\ue017", R.mipmap.ue017),
            Emotion("\\ue018", R.mipmap.ue018),
            Emotion("\\ue019", R.mipmap.ue019),
            Emotion("\\ue020", R.mipmap.ue020),
            Emotion("\\ue021", R.mipmap.ue021),
            Emotion("\\ue022", R.mipmap.ue022),
            Emotion("\\ue023", R.mipmap.ue023),
            Emotion("\\ue024", R.mipmap.ue024),
            Emotion("\\ue025", R.mipmap.ue025),
            Emotion("\\ue026", R.mipmap.ue026),
            Emotion("\\ue027", R.mipmap.ue027),
            Emotion("\\ue028", R.mipmap.ue028),
            Emotion("\\ue029", R.mipmap.ue029),
            Emotion("\\ue030", R.mipmap.ue030),
            Emotion("\\ue031", R.mipmap.ue031),
            Emotion("\\ue032", R.mipmap.ue032),
            Emotion("\\ue033", R.mipmap.ue033),
            Emotion("\\ue034", R.mipmap.ue034),
            Emotion("\\ue035", R.mipmap.ue035),
            Emotion("\\ue036", R.mipmap.ue036),
            Emotion("\\ue037", R.mipmap.ue037),
            Emotion("\\ue038", R.mipmap.ue038),
            Emotion("\\ue039", R.mipmap.ue039),
            Emotion("\\ue040", R.mipmap.ue040),
            Emotion("\\ue041", R.mipmap.ue041),
            Emotion("\\ue042", R.mipmap.ue042),
            Emotion("\\ue043", R.mipmap.ue043),
            Emotion("\\ue044", R.mipmap.ue044),
            Emotion("\\ue045", R.mipmap.ue045),
            Emotion("\\ue046", R.mipmap.ue046),
            Emotion("\\ue047", R.mipmap.ue047),
            Emotion("\\ue048", R.mipmap.ue048),
            Emotion("\\ue049", R.mipmap.ue049),
            Emotion("\\ue050", R.mipmap.ue050),
            Emotion("\\ue051", R.mipmap.ue051),
            Emotion("\\ue052", R.mipmap.ue052),
            Emotion("\\ue053", R.mipmap.ue053),
            Emotion("\\ue054", R.mipmap.ue054),
            Emotion("\\ue055", R.mipmap.ue055),
            Emotion("\\ue056", R.mipmap.ue056),
            Emotion("\\ue057", R.mipmap.ue057),
            Emotion("\\ue058", R.mipmap.ue058),
            Emotion("\\ue059", R.mipmap.ue059),
            Emotion("\\ue060", R.mipmap.ue060),
            Emotion("\\ue061", R.mipmap.ue061),
            Emotion("\\ue062", R.mipmap.ue062),
            Emotion("\\ue063", R.mipmap.ue063),
            Emotion("\\ue064", R.mipmap.ue064),
            Emotion("\\ue065", R.mipmap.ue065),
            Emotion("\\ue066", R.mipmap.ue066),
            Emotion("\\ue067", R.mipmap.ue067),
            Emotion("\\ue068", R.mipmap.ue068),
            Emotion("\\ue069", R.mipmap.ue069),
            Emotion("\\ue070", R.mipmap.ue070),
            Emotion("\\ue071", R.mipmap.ue071),
            Emotion("\\ue072", R.mipmap.ue072),
            Emotion("\\ue073", R.mipmap.ue073),
            Emotion("\\ue074", R.mipmap.ue074),
            Emotion("\\ue075", R.mipmap.ue075),
            Emotion("\\ue076", R.mipmap.ue076),
            Emotion("\\ue077", R.mipmap.ue077),
            Emotion("\\ue078", R.mipmap.ue078),
            Emotion("\\ue079", R.mipmap.ue079),
            Emotion("\\ue080", R.mipmap.ue080),
            Emotion("\\ue081", R.mipmap.ue081),
            Emotion("\\ue082", R.mipmap.ue082),
            Emotion("\\ue083", R.mipmap.ue083),
            Emotion("\\ue084", R.mipmap.ue084),
            Emotion("\\ue085", R.mipmap.ue085),
            Emotion("\\ue086", R.mipmap.ue086),
            Emotion("\\ue087", R.mipmap.ue087),
            Emotion("\\ue088", R.mipmap.ue088),
            Emotion("\\ue089", R.mipmap.ue089),
            Emotion("\\ue090", R.mipmap.ue090),
            Emotion("\\ue091", R.mipmap.ue091),
            Emotion("\\ue092", R.mipmap.ue092)
    )

    /**
     * 普通文本转Emotion文本
     */
    fun format(textView: TextView, text: CharSequence?): CharSequence? {
        if (text == null || text.isEmpty()) return text
        try {
            val spannableString = SpannableString(text)
            var start = 0
            val pattern = Pattern.compile(REGEX_EMOTION, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                val faceText = matcher.group()
                val imageSpan = CenterImageSpan(textView.context, getResIdByTag(faceText))
                val startIndex = text.indexOf(faceText, start)
                val endIndex = startIndex + faceText.length
                if (startIndex >= 0)
                    spannableString.setSpan(imageSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                start = endIndex - 1
            }
            return spannableString
        } catch (e: Exception) {
            return text
        }
    }

    fun getResIdByTag(tag: String): Int {
        emotions.forEach {
            if (it.tag == tag) return it.resId
        }
        return R.drawable.ic_emotion
    }

    /**
     * 表情对象
     */
    data class Emotion(val tag: String, val resId: Int)

    /**
     * 居中显示Span
     */
    class CenterImageSpan(
            context: Context,
            resourceId: Int,
            verticalAlignment: Int = ImageSpan.ALIGN_BASELINE
    ) : ImageSpan(context, resourceId, verticalAlignment) {

        override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int,
                             fontMetricsInt: Paint.FontMetricsInt?): Int {
            val drawable = drawable
            val rect = drawable.bounds
            if (fontMetricsInt != null) {
                val fmPaint = paint.fontMetricsInt
                val fontHeight = fmPaint.descent - fmPaint.ascent
                val drHeight = rect.bottom - rect.top
                val centerY = fmPaint.ascent + fontHeight / 2

                fontMetricsInt.ascent = centerY - drHeight / 2
                fontMetricsInt.top = fontMetricsInt.ascent
                fontMetricsInt.bottom = centerY + drHeight / 2
                fontMetricsInt.descent = fontMetricsInt.bottom
            }
            return rect.right
        }

        override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int,
                          bottom: Int, paint: Paint) {
            drawable?.let {
                canvas.save()
                val fmPaint = paint.fontMetricsInt
                val fontHeight = fmPaint.descent - fmPaint.ascent
                val centerY = y + fmPaint.descent - fontHeight / 2
                val transY = centerY - (it.bounds.bottom - it.bounds.top) / 2
                canvas.translate(x, transY.toFloat())
                it.draw(canvas)
                canvas.restore()
            }
        }
    }
}