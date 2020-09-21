package com.mobile.isecurity.util

class iStopwatch : Thread() {
    private var startTime: Long = 0
    private var started = false
    fun startThread() {
        startTime = System.currentTimeMillis()
        started = true
        start()
    }

    override fun run() {
        while (started) {
            // empty code since currentTimeMillis increases by itself
        }
    }

    val getTime: IntArray
        get() {
            val milliTime = System.currentTimeMillis() - startTime
            val out = intArrayOf(0, 0, 0, 0)
            out[0] = (milliTime / 3600000).toInt()
            out[1] = (milliTime / 60000).toInt() % 60
            out[2] = (milliTime / 1000).toInt() % 60
            out[3] = milliTime.toInt() % 1000
            return out
        }

    fun stopThread() {
        started = false
    }
}