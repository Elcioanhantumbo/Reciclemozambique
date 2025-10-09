package com.example.reciclemozambique.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PointsStore {
    private const val PREF = "points_store"
    private const val KEY_TOTAL = "total_points"
    private const val KEY_HISTORY = "history_json"

    data class HistoryItem(
        val title: String,
        val date: String,
        val points: Int
    )

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun totalPoints(ctx: Context): Int =
        prefs(ctx).getInt(KEY_TOTAL, 0)

    fun history(ctx: Context): List<HistoryItem> {
        val json = prefs(ctx).getString(KEY_HISTORY, "[]") ?: "[]"
        val arr = JSONArray(json)
        val out = ArrayList<HistoryItem>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            out.add(
                HistoryItem(
                    title = o.optString("title"),
                    date = o.optString("date"),
                    points = o.optInt("points")
                )
            )
        }
        return out
    }

    /** Credita pontos (usado pelo botão “Resgatar”/claim). */
    fun addPoints(ctx: Context, points: Int, title: String) {
        if (points <= 0) return
        val newTotal = totalPoints(ctx) + points
        val list = history(ctx).toMutableList()
        val date = now()
        list.add(0, HistoryItem(title = title, date = date, points = points))
        save(ctx, newTotal, list)
    }

    /** Debita pontos se houver saldo suficiente (ex.: resgate de benefício real). */
    fun tryRedeem(ctx: Context, cost: Int, reason: String): Boolean {
        val current = totalPoints(ctx)
        if (cost <= 0 || current < cost) return false
        val list = history(ctx).toMutableList()
        val date = now()
        list.add(0, HistoryItem(title = reason, date = date, points = -cost))
        save(ctx, current - cost, list)
        return true
    }

    private fun save(ctx: Context, total: Int, list: List<HistoryItem>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject()
                    .put("title", it.title)
                    .put("date", it.date)
                    .put("points", it.points)
            )
        }
        prefs(ctx).edit()
            .putInt(KEY_TOTAL, total)
            .putString(KEY_HISTORY, arr.toString())
            .apply()
    }

    private fun now(): String =
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
}
