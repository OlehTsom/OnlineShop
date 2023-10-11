package com.example.onlineshop.util

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

class ConfigManager {
    fun loadConfig(context: Context): Config {
        val gson = Gson()
        val inputStream = context.assets.open("config.json")
        val reader = InputStreamReader(inputStream)
        return gson.fromJson(reader, Config::class.java)
    }
}

data class Config(val google_request_id_token: String)