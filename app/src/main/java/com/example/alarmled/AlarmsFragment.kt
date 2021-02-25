package com.example.alarmled

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.pow


class AlarmsFragment : Fragment() {


    class Alarm(
        val id: String,
        val hour: Int,
        val minute: Int,
        var state: Boolean,
        var listDays: Int,
        val repeatMode: Int,
        val red: Int,
        val green: Int,
        val blue: Int
    ) {
        //val id : Int


        companion object {
            fun convertArrayBooleanToInt(list: BooleanArray): Int {
                var sum = 0.0
                for ((index, value) in list.withIndex()) {
                    if (value) sum += 2.0.pow(index.toDouble())
                }

                return sum.toInt()
            }

            fun convertIntToArrayBoolean(number: Int): BooleanArray {
                var list = booleanArrayOf(false, false, false, false, false, false, false)
                var numberTmp = number
                var tmp = 0
                var iterator = 0
                while (numberTmp >= 1) {
                    tmp = numberTmp % 2
                    numberTmp /= 2
                    if (tmp == 1) {
                        list[iterator] = true
                    }
                    iterator++
                }
                return list
            }
        }

    }


    companion object {
        var listAlarms: ArrayList<Alarm> = ArrayList<Alarm>()
        lateinit var adapter: CaptionedImagesAdapter
        lateinit var appContext: Context
        lateinit var fabId: FloatingActionButton

        fun queryData(): ArrayList<Alarm> {
            listAlarms.clear()
            val sql: String = "select * from ALARM ORDER BY HOUR ASC, MINUTE ASC "
            val tmp = ArrayList<Alarm>()
            val dataBaseHelper = AlarmsDataBaseHelper(appContext)
            try {
                val db = dataBaseHelper.readableDatabase
                val cursor: Cursor = db.rawQuery(sql, null)
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getString(0)
                        val hour = cursor.getInt(1)
                        val minute = cursor.getInt(2)
                        val state = cursor.getInt(3) > 0
                        val list = cursor.getInt(4)
                        val mode = cursor.getInt(5)
                        val red = cursor.getInt(6)
                        val green = cursor.getInt(7)
                        val blue = cursor.getInt(8)
                        listAlarms.add(Alarm(id, hour, minute, state, list, mode, red, green, blue))
                    } while (cursor.moveToNext())
                }
                cursor.close()
            } catch (e: SQLException) {
                val toast = Toast.makeText(
                    appContext, e.message,
                    Toast.LENGTH_LONG
                )
                toast.show()
            }

            return listAlarms
        }


        fun insertAlarm(alarm: Alarm) {
            AlarmsDataBaseHelper.insertAlarm(
                AlarmsDataBaseHelper(appContext).readableDatabase,
                alarm
            )
            queryData()
        }

        fun deleteAlarm(alarm: Alarm) {
            AlarmsDataBaseHelper.deleteAlarm(
                AlarmsDataBaseHelper(appContext).readableDatabase,
                alarm
            )
        }

        fun updateAlarm(alarm: Alarm) {
            AlarmsDataBaseHelper.updateAlarm(
                AlarmsDataBaseHelper(appContext).readableDatabase,
                alarm
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AlarmsFragment.appContext = context!!

        return inflater.inflate(R.layout.fragment_alarms, container, false)
    }


    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {

        val recycler: RecyclerView = view?.findViewById(R.id.recycler) as RecyclerView
        fabId = view?.findViewById<FloatingActionButton>(R.id.fab)!!
        fabId.setOnClickListener {
            AlarmDialog().show(fragmentManager!!, "aa")
        }
        adapter = CaptionedImagesAdapter(queryData())
        recycler.adapter = adapter

        val layoutManager = GridLayoutManager(activity, 1)

        recycler.layoutManager = layoutManager


        /* adapter.setListener(object : CaptionedImagesAdapter.Listener {
             override fun onClick(position: Int) {
                 val intent = Intent(getActivity(), AlarmDetailActivity::class.java)
                 intent.putExtra(AlarmDetailActivity.EXTRA_PIZZA_ID, position)
                 getActivity()?.startActivity(intent)
             }
         })

 */
    }
}