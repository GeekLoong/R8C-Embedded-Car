package net.kuisec.r8c

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import net.kuisec.r8c.Utils.ThemeUtil
import net.kuisec.r8c.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    //定时器
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        setContentView(binding.root)
    }

    //初始化函数
    private fun initData() {
        ThemeUtil.setDarkTheme(this)
        //加载布局
        binding = ActivitySplashBinding.inflate(layoutInflater)
        //执行3s，每搁1s执行一次
        timer = Timers(3000, 1000)
        timer.start()
        binding.jump.setOnClickListener { jump() }
    }

    //跳转函数
    private fun jump() {
        //时长结束跳转
        val intent = Intent(application, MainActivity::class.java)
        startActivity(intent)
        finish()
        timer.cancel()
    }

    //定时器类
    internal inner class Timers
    /**
     * 构造函数
     * @param millisInFuture 总定时时长
     * @param countDownInterval 间隔执行时长
     */
        (millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        /**
         * 间隔执行函数
         * @param l 达到间隔时执行
         */
        @SuppressLint("SetTextI18n")
        override fun onTick(l: Long) {
            val s = (l / 1000 + 1).toString()
            binding.jump.text = s + "s 跳过"
        }

        //时长结束函数
        override fun onFinish() {
            jump()
        }
    }
}