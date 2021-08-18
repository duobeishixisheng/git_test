package tech.tosee.app.utils

import android.app.Activity
import android.content.Context
import java.text.NumberFormat

/**
 * 获取上下行速度的工具类
 */
class GetUpAndDownloadSpeedHelper {
    private val repeatTask = RepeatTask()
    lateinit var getUpAndDownloadSpeed : GetUpAndDownloadSpeed
    /**
     * @param isGetTheAppSpeed 是否根据Uid 获取该应用的上下行速度，若为false，则获取整机的上下行速度
     * @param intervalTime 间隔时间：即多久发送一次
     */
    fun startUpAndDownloadSpeedRepeatTask(context: Context,
                                               activity : Activity,
                                               isGetTheAppSpeed : Boolean = true,
                                               intervalTime : Long = 2000,
                                               callback: UpAndDownloadSpeedCallback){
        var upSpeed : Float = 0.00f
        var downSpeed : Float = 0.00f
        getUpAndDownloadSpeed = GetUpAndDownloadSpeed()
        repeatTask.repeatTask(intervalTime,0,callback = object :RepeatTask.TaskCallback{
            override fun revice() {
                if(isGetTheAppSpeed){
                    upSpeed = getUpAndDownloadSpeed.getUpSpeedByUid(context.applicationInfo.uid)
                    downSpeed = getUpAndDownloadSpeed.getDownloadSpeedByUid(context.applicationInfo.uid)
                }else{
                    upSpeed = getUpAndDownloadSpeed.getTotalUpSpeed()
                    downSpeed = getUpAndDownloadSpeed.getTotalDownloadSpeed()
                }
                //在主线程中发送，方便调用者直接使用
                activity.runOnUiThread {
                    callback.receiveSpeed(upSpeed,downSpeed)
                }
            }
        })
    }


    //将float型的数据转换为String型，并自带单位
    //defaultPointAmount:保留小数的位数，默认为2位
    private val numberFormat = NumberFormat.getInstance()
    fun speedFloatToString(speed : Float, defaultPointAmount: Int = 2) : String{
        numberFormat.maximumFractionDigits = defaultPointAmount
        numberFormat.minimumFractionDigits = defaultPointAmount
        numberFormat.isGroupingUsed = false         //是否以此格式分组

        var speedString : String = ""

        if(speed >= 1024){
            speedString = numberFormat.format(speed / 1024.0)
            return speedString+"MB/s"
        }else{
            speedString = numberFormat.format(speed)
            return speedString+"KB/s"
        }
    }

    fun cancelUpAndDownloadSpeedTask(){
        repeatTask.cancelTask()
    }

    interface UpAndDownloadSpeedCallback{
        fun receiveSpeed(upSpeed : Float,downloadSpeed : Float)
    }

}