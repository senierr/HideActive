package com.hideactive.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.ImageView

/**
 *
 * @author zhouchunjie
 * @date 2018/5/2
 */
class CircularAnim(
        var sPerfectMills: Long = 0L,
        var sFullActivityPerfectMills: Long = 0L,
        var sColorOrImageRes: Int = 0
) {

    val PERFECT_MILLS = 618L
    val MINI_RADIUS = 0

    private fun getPerfectMills(): Long {
        return if (sPerfectMills != 0L) sPerfectMills else PERFECT_MILLS
    }

    private fun getFullActivityMills(): Long {
        return if (sFullActivityPerfectMills != 0L) sFullActivityPerfectMills else PERFECT_MILLS
    }

    private fun getColorOrImageRes(): Int {
        return if (sColorOrImageRes != 0) sColorOrImageRes else android.R.color.white
    }

    interface OnAnimationEndListener {
        fun onAnimationEnd()
    }

    interface OnAnimatorDeployListener {
        fun deployAnimator(animator: Animator)
    }

    @SuppressLint("NewApi")
    inner class VisibleBuilder(private val mAnimView: View, private val isShow: Boolean) {
        private var mTriggerView: View? = null

        private var mStartRadius: Float? = null
        private var mEndRadius: Float? = null

        private var mDurationMills = getPerfectMills()

        private var mOnAnimatorDeployListener: OnAnimatorDeployListener? = null

        private var mOnAnimationEndListener: OnAnimationEndListener? = null

        init {
            if (isShow) {
                mStartRadius = MINI_RADIUS + 0f
            } else {
                mEndRadius = MINI_RADIUS + 0f
            }
        }

        fun triggerView(triggerView: View): VisibleBuilder {
            mTriggerView = triggerView
            return this
        }

        fun startRadius(startRadius: Float): VisibleBuilder {
            mStartRadius = startRadius
            return this
        }

        fun endRadius(endRadius: Float): VisibleBuilder {
            mEndRadius = endRadius
            return this
        }

        fun duration(durationMills: Long): VisibleBuilder {
            mDurationMills = durationMills
            return this
        }

        fun deployAnimator(onAnimatorDeployListener: OnAnimatorDeployListener): VisibleBuilder {
            mOnAnimatorDeployListener = onAnimatorDeployListener
            return this
        }

        fun go(onAnimationEndListener: OnAnimationEndListener? = null) {
            mOnAnimationEndListener = onAnimationEndListener

            // 版本判断
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                doOnEnd()
                return
            }

            val rippleCX: Int
            val rippleCY: Int
            val maxRadius: Int
            if (mTriggerView != null) {
                val tvLocation = IntArray(2)
                mTriggerView!!.getLocationInWindow(tvLocation)
                val tvCX = tvLocation[0] + mTriggerView!!.getWidth() / 2
                val tvCY = tvLocation[1] + mTriggerView!!.getHeight() / 2

                val avLocation = IntArray(2)
                mAnimView.getLocationInWindow(avLocation)
                val avLX = avLocation[0]
                val avTY = avLocation[1]

                var triggerX = Math.max(avLX, tvCX)
                triggerX = Math.min(triggerX, avLX + mAnimView.getWidth())

                var triggerY = Math.max(avTY, tvCY)
                triggerY = Math.min(triggerY, avTY + mAnimView.getHeight())

                // 以上全为绝对坐标

                val avW = mAnimView.getWidth()
                val avH = mAnimView.getHeight()

                rippleCX = triggerX - avLX
                rippleCY = triggerY - avTY

                // 计算水波中心点至 @mAnimView 边界的最大距离
                val maxW = Math.max(rippleCX, avW - rippleCX)
                val maxH = Math.max(rippleCY, avH - rippleCY)
                maxRadius = Math.sqrt((maxW * maxW + maxH * maxH).toDouble()).toInt() + 1
            } else {
                rippleCX = (mAnimView.getLeft() + mAnimView.getRight()) / 2
                rippleCY = (mAnimView.getTop() + mAnimView.getBottom()) / 2

                val w = mAnimView.getWidth()
                val h = mAnimView.getHeight()

                // 勾股定理 & 进一法
                maxRadius = Math.sqrt((w * w + h * h).toDouble()).toInt() + 1
            }

            if (isShow && mEndRadius == null)
                mEndRadius = maxRadius + 0f
            else if (!isShow && mStartRadius == null)
                mStartRadius = maxRadius + 0f

            try {
                val anim = ViewAnimationUtils.createCircularReveal(
                        mAnimView, rippleCX, rippleCY, mStartRadius!!, mEndRadius!!)

                mAnimView.visibility = View.VISIBLE
                anim.duration = mDurationMills

                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        doOnEnd()
                    }
                })
                if (mOnAnimatorDeployListener != null)
                    mOnAnimatorDeployListener!!.deployAnimator(anim)
                anim.start()
            } catch (e: Exception) {
                e.printStackTrace()
                doOnEnd()
            }
        }

        private fun doOnEnd() {
            if (isShow)
                mAnimView.visibility = View.VISIBLE
            else
                mAnimView.visibility = View.INVISIBLE

            mOnAnimationEndListener?.onAnimationEnd()
        }
    }

    @SuppressLint("NewApi")
    inner class FullActivityBuilder(private val mActivity: Activity, private val mTriggerView: View) {
        private var mStartRadius = MINI_RADIUS.toFloat()
        private var mColorOrImageRes = getColorOrImageRes()
        private var mDurationMills: Long? = null
        private var mStartAnimatorDeployListener: OnAnimatorDeployListener? = null
        private var mReturnAnimatorDeployListener: OnAnimatorDeployListener? = null
        private var mOnAnimationEndListener: OnAnimationEndListener? = null
        private var mEnterAnim = android.R.anim.fade_in
        private var mExitAnim = android.R.anim.fade_out

        fun startRadius(startRadius: Float): FullActivityBuilder {
            mStartRadius = startRadius
            return this
        }

        fun colorOrImageRes(colorOrImageRes: Int): FullActivityBuilder {
            mColorOrImageRes = colorOrImageRes
            return this
        }

        fun duration(durationMills: Long): FullActivityBuilder {
            mDurationMills = durationMills
            return this
        }

        fun overridePendingTransition(enterAnim: Int, exitAnim: Int): FullActivityBuilder {
            mEnterAnim = enterAnim
            mExitAnim = exitAnim
            return this
        }

        fun deployStartAnimator(onAnimatorDeployListener: OnAnimatorDeployListener): FullActivityBuilder {
            mStartAnimatorDeployListener = onAnimatorDeployListener
            return this
        }

        fun deployReturnAnimator(onAnimatorDeployListener: OnAnimatorDeployListener): FullActivityBuilder {
            mReturnAnimatorDeployListener = onAnimatorDeployListener
            return this
        }

        fun go(onAnimationEndListener: OnAnimationEndListener) {
            mOnAnimationEndListener = onAnimationEndListener
            // 版本判断,小于5.0则无动画.
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
                doOnEnd()
                return
            }

            val location = IntArray(2)
            mTriggerView.getLocationInWindow(location)
            val cx = location[0] + mTriggerView.width / 2
            val cy = location[1] + mTriggerView.height / 2
            val view = ImageView(mActivity)
            view.scaleType = ImageView.ScaleType.CENTER_CROP
            view.setImageResource(mColorOrImageRes)
            val decorView = mActivity.window.decorView as ViewGroup
            val w = decorView.width
            val h = decorView.height
            decorView.addView(view, w, h)

            // 计算中心点至view边界的最大距离
            val maxW = Math.max(cx, w - cx)
            val maxH = Math.max(cy, h - cy)
            val finalRadius = Math.sqrt((maxW * maxW + maxH * maxH).toDouble()).toInt() + 1

            try {
                val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, mStartRadius, finalRadius.toFloat())

                val maxRadius = Math.sqrt((w * w + h * h).toDouble()).toInt() + 1
                // 若未设置时长，则以PERFECT_MILLS为基准根据水波扩散的距离来计算实际时间
                if (mDurationMills == null) {
                    // 算出实际边距与最大边距的比率
                    val rate = 1.0 * finalRadius / maxRadius
                    // 为了让用户便于感触到水波，速度应随最大边距的变小而越慢，扩散时间应随最大边距的变小而变小，因此比率应在 @rate 与 1 之间。
                    mDurationMills = (getFullActivityMills() * Math.sqrt(rate)).toLong()
                }
                val finalDuration = mDurationMills!!
                // 由于thisActivity.startActivity()会有所停顿，所以进入的水波动画应比退出的水波动画时间短才能保持视觉上的一致。
                anim.duration = (finalDuration * 0.9).toLong()
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)

                        doOnEnd()

                        mActivity.overridePendingTransition(mEnterAnim, mExitAnim)

                        // 默认显示返回至当前Activity的动画.
                        mTriggerView.postDelayed(Runnable {
                            if (mActivity.isFinishing)
                                return@Runnable
                            try {
                                val returnAnim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
                                        finalRadius.toFloat(), mStartRadius)
                                returnAnim.duration = finalDuration
                                returnAnim.addListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator) {
                                        super.onAnimationEnd(animation)
                                        try {
                                            decorView.removeView(view)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }
                                })
                                if (mReturnAnimatorDeployListener != null)
                                    mReturnAnimatorDeployListener!!.deployAnimator(returnAnim)
                                returnAnim.start()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                try {
                                    decorView.removeView(view)
                                } catch (e1: Exception) {
                                    e1.printStackTrace()
                                }

                            }
                        }, 1000)
                    }
                })
                if (mStartAnimatorDeployListener != null)
                    mStartAnimatorDeployListener!!.deployAnimator(anim)
                anim.start()
            } catch (e: Exception) {
                e.printStackTrace()
                doOnEnd()
            }

        }

        private fun doOnEnd() {
            mOnAnimationEndListener?.onAnimationEnd()
        }
    }

    /* 上面为实现逻辑，下面为外部调用方法 */

    /* 伸展并显示@animView */
    fun show(animView: View): VisibleBuilder {
        return VisibleBuilder(animView, true)
    }

    /* 收缩并隐藏@animView */
    fun hide(animView: View): VisibleBuilder {
        return VisibleBuilder(animView, false)
    }

    /* 以@triggerView 为触发点铺满整个@activity */
    fun fullActivity(activity: Activity, triggerView: View): FullActivityBuilder {
        return FullActivityBuilder(activity, triggerView)
    }
}