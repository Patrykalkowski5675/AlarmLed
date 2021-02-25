package com.example.alarmled


import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import kotlinx.android.synthetic.main.fragment_dialog_alarm.*
import java.util.*


class AlarmDialog : DialogFragment() {
    companion object {
        private const val id_ = "ID"
        private const val hour = "HOUR"
        private const val minute = "MINUTE"
        private const val listDays = "LIST_DAYS"
        private const val repeatMode = "REPEAT_MODE"
        private const val red = "RED"
        private const val green = "GREEN"
        private const val blue = "BLUE"

        @RequiresApi(Build.VERSION_CODES.N)
        fun CountDiff(H1: Int, M1: Int, H2: Int, M2: Int): String {
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            val date1: Date = simpleDateFormat.parse("${H1}:${M1}")

            val date2: Date = simpleDateFormat.parse("${H2}:${M2}")

            var diff: Long

            if (date2 > date1) diff = (date2.time - date1.time) % 86_400_000
            else diff = (date2.time - date1.time + 86_400_000) % 86_400_000


            val diffHours: Long = diff / 3_600_000
            diff %= 3_600_000

            val diffMinutes: Long = diff / 60_000
            diff %= 60_000

            val str = StringBuilder("Alarm za ")

            val array: IntArray =
                intArrayOf(2, 3, 4, 22, 23, 24, 32, 33, 34, 42, 43, 44, 52, 53, 54)

            if (diffHours.toInt() != 0) {
                if (diffHours.toInt() == 1) str.append("godzinę ")
                else if (diffHours.toInt() in array) str.append("${diffHours} godziny ")
                else str.append("${diffHours} godzin ")
            }

            if (diffHours.toInt() != 0 && diffMinutes.toInt() != 0) str.append("i ")

            if (diffMinutes.toInt() != 0) {
                if (diffMinutes.toInt() == 1) str.append("minutę")
                else if (diffMinutes.toInt() in array) str.append("${diffMinutes} minuty")
                else str.append("${diffMinutes} minut")
            }
            if (diffHours.toInt() == 0 && diffMinutes.toInt() == 0) str.append("24 godziny")

            return str.toString()
        }
    }

    private var idFromBudle: String? = null
    private var hourFromBudle: Int? = null
    private var minuteFromBudle: Int? = null
    private var ListFromBudle: BooleanArray? = null
    private var repeatModeFromBudle: Int? = null
    private var redFromBudle: Int? = null
    private var greenFromBudle: Int? = null
    private var blueFromBudle: Int? = null

    private var choosenRed = 0
    private var choosenGreen = 200
    private var choosenBlue = 255


    @RequiresApi(Build.VERSION_CODES.N)
    var c: Calendar = Calendar.getInstance()

    @RequiresApi(Build.VERSION_CODES.N)
    var actualH = c.get(android.icu.util.Calendar.HOUR)

    @RequiresApi(Build.VERSION_CODES.N)
    var actualM = c.get(android.icu.util.Calendar.MINUTE)

    val btListState = booleanArrayOf(false, false, false, false, false, false, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            idFromBudle = arguments?.getString(id_.toString())!!
            hourFromBudle = arguments?.getInt(hour)!!
            minuteFromBudle = arguments?.getInt(minute)!!
            ListFromBudle =
                AlarmsFragment.Alarm.convertIntToArrayBoolean(arguments?.getInt(listDays)!!)
            repeatModeFromBudle = arguments?.getInt(repeatMode)
            redFromBudle = arguments?.getInt(red)!!
            greenFromBudle = arguments?.getInt(green)!!
            blueFromBudle = arguments?.getInt(blue)!!
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_dialog_alarm, container, false)

        if (dialog != null && dialog?.window != null) {
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
            // dialog?.window?.getAttributes()?.windowAnimations = R.style.MyAnimation_Window;
        }
        initViews(view)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        timePicker.setIs24HourView(true)

        if (hourFromBudle != null) {
            timePicker.hour =
                hourFromBudle!!
            timePicker.minute =
                minuteFromBudle!!
            repeatSpinner.setSelection(repeatModeFromBudle!!)

        } else {
            c = Calendar.getInstance()
            c.add(Calendar.MINUTE, 1)
            timePicker.hour = c.get(Calendar.HOUR)
            timePicker.minute = c.get(Calendar.MINUTE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun initViews(view: View) {

        InitDaysButtons(view)

        val btCP = view.findViewById<Button>(R.id.buttonColorPicker)

        if (redFromBudle != null) {
            val color: Int =
                (redFromBudle!! and 0xff shl 16) or (greenFromBudle!! and 0xff shl 8) or (blueFromBudle!! and 0xff)
            btCP.background.setColorFilter(
                Color.parseColor(
                    String.format(
                        "#%06X",
                        0xFFFFFF and color
                    )
                ), PorterDuff.Mode.SRC_ATOP
            )
        }

        btCP.setOnClickListener {
            val cp: ColorPicker
            cp = ColorPicker(
                activity,
                redFromBudle ?: choosenRed,
                greenFromBudle ?: choosenGreen,
                blueFromBudle ?: choosenBlue,

            )
            cp.window?.setBackgroundDrawableResource(R.color.black)
            cp.show()
            cp.enableAutoClose()
            cp.setCallback { color ->

                choosenRed = Color.red(color)
                choosenGreen = Color.green(color)
                choosenBlue = Color.blue(color)

                val red = Color.red(color)
                val green = Color.green(color)
                val blue = Color.blue(color)

                btCP.background.setColorFilter(
                    Color.parseColor(
                        String.format(
                            "#%06X",
                            0xFFFFFF and color
                        )
                    ), PorterDuff.Mode.SRC_ATOP
                )
                /* RequestAsync(getApplicationContext()).execute(
                                 "2", Integer.toString(Color.red(color)), Integer.toString(
                                     Color.green(color)
                                 ), Integer.toString(Color.blue(color))
                             )*/
                // If the auto-dismiss option is not enable (disabled as default) you have to manually dimiss the dialog
                // cp.dismiss();
            }

        }


        val cancelBT = view.findViewById(R.id.cancelBT) as ImageButton
        cancelBT.setOnClickListener {
            dismiss()
        }

        val doneBT = view.findViewById(R.id.doneBT) as ImageButton
        doneBT.setOnClickListener {
            val repeatSpinner = view.findViewById<Spinner>(R.id.repeatSpinner)
            val mode = repeatSpinner.selectedItemPosition
            if (btListState.count { it } == 0) {
                val toast = Toast.makeText(
                    context, "Select days",
                    Toast.LENGTH_LONG
                )
                toast.show()
                return@setOnClickListener
            }


            if (idFromBudle != null) {
                AlarmsFragment.updateAlarm(
                    AlarmsFragment.Alarm(
                        idFromBudle!!,
                        timePicker.hour,
                        timePicker.minute,
                        true,
                        AlarmsFragment.Alarm.convertArrayBooleanToInt(btListState),
                        mode,
                        choosenRed,
                        choosenGreen,
                        choosenBlue
                    )
                )
            } else {
                AlarmsFragment.insertAlarm(
                    AlarmsFragment.Alarm(
                        "-1",
                        timePicker.hour,
                        timePicker.minute,
                        true,
                        AlarmsFragment.Alarm.convertArrayBooleanToInt(btListState),
                        mode,
                        choosenRed,
                        choosenGreen,
                        choosenBlue
                    )
                )
            }

            AlarmsFragment.queryData()
            AlarmsFragment.adapter.notifyDataSetChanged()

            val toast = Toast.makeText(
                context, CountDiff(actualH, actualM, timePicker.hour, timePicker.minute),
                Toast.LENGTH_LONG
            )
            toast.show()
            dismiss()
        }


        val viewTimePicker = view.findViewById<TimePicker>(R.id.timePicker)
        viewTimePicker.currentHour

        viewTimePicker.setOnTimeChangedListener { timePicker: TimePicker, i: Int, i1: Int ->
            val diffTX = view.findViewById<TextView>(R.id.diffText)
            diffTX.text = CountDiff(actualH, actualM, i, i1)
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun InitDaysButtons(view: View) {

        val btList = arrayListOf<Button>()

        btList.add(view.findViewById(R.id.dayBT1) as Button)
        btList.add(view.findViewById(R.id.dayBT2) as Button)
        btList.add(view.findViewById(R.id.dayBT3) as Button)
        btList.add(view.findViewById(R.id.dayBT4) as Button)
        btList.add(view.findViewById(R.id.dayBT5) as Button)
        btList.add(view.findViewById(R.id.dayBT6) as Button)
        btList.add(view.findViewById(R.id.dayBT7) as Button)

        for (i in btList) i.setOnClickListener(::CheckDay)

        val c = Calendar.getInstance()
        val wDay = c.get(android.icu.util.Calendar.DAY_OF_WEEK)

        if (ListFromBudle != null) {
            for ((index, value) in this.ListFromBudle!!.withIndex()) {
                if (value) {
                    btListState[index] = true
                    btList[index].setBackgroundResource(R.drawable.ripple_effect_pink)
                    btList[index].setTextColor(resources.getColor(R.color.white))
                }
            }
        } else {
            var which = 0
            when (wDay) {
                Calendar.MONDAY -> which = 0
                Calendar.TUESDAY -> which = 1
                Calendar.WEDNESDAY -> which = 2
                Calendar.THURSDAY -> which = 3
                Calendar.FRIDAY -> which = 4
                Calendar.SATURDAY -> which = 5
                Calendar.SUNDAY -> which = 6
            }

            btListState[which] = true
            btList[which].setBackgroundResource(R.drawable.ripple_effect_pink)
            btList[which].setTextColor(resources.getColor(R.color.white))
        }
    }

    fun CheckDay(view: View) {
        when (view.id) {
            R.id.dayBT1 -> ChangeStyle(view, 0)
            R.id.dayBT2 -> ChangeStyle(view, 1)
            R.id.dayBT3 -> ChangeStyle(view, 2)
            R.id.dayBT4 -> ChangeStyle(view, 3)
            R.id.dayBT5 -> ChangeStyle(view, 4)
            R.id.dayBT6 -> ChangeStyle(view, 5)
            R.id.dayBT7 -> ChangeStyle(view, 6)
        }
    }

    fun ChangeStyle(view: View, number: Int) {
        when (btListState[number]) {
            false -> {
                btListState[number] = true
                view.setBackgroundResource(R.drawable.ripple_effect_pink)
                (view as Button).setTextColor(resources.getColor(R.color.white))
            }
            true -> {
                btListState[number] = false
                view.setBackgroundResource(R.drawable.ripple_effect_white)
                (view as Button).setTextColor(resources.getColor(R.color.colorPrimary))
            }
        }
    }


}

