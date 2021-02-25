package com.example.alarmled

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.icu.util.Calendar
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView


import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.card_captioned_image.view.*
import kotlinx.android.synthetic.main.fragment_dialog_alarm.*
import java.lang.StringBuilder


class CaptionedImagesAdapter(
    private val captions: MutableList<AlarmsFragment.Alarm>
) : RecyclerView.Adapter<CaptionedImagesAdapter.ViewHolder>() {
    private var listener: Listener? = null
    private lateinit var fM: FragmentManager
    private lateinit var context: Context
    private lateinit var listItemsToErase: ArrayList<AlarmsFragment.Alarm>


    interface Listener {
        fun onClick(position: Int)
    }

    class ViewHolder(internal val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    companion object {
        private const val id_ = "ID"
        private const val hour = "HOUR"
        private const val minute = "MINUTE"
        private const val listDays = "LIST_DAYS"
        private const val repeatMode = "REPEAT_MODE"
        private const val red = "RED"
        private const val green = "GREEN"
        private const val blue = "BLUE"

        var stateOfDelating = false
        private val listOfHolders = ArrayList<ViewHolder>()

        fun deinitDelating(context: Context) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, R.anim.scale_up)

            for (i in listOfHolders) {
                i.cardView.checkbox.isChecked = false
                i.cardView.checkbox.visibility = View.INVISIBLE
                i.cardView.switch3.startAnimation(animation)
                i.cardView.switch3.visibility = View.VISIBLE
            }
            stateOfDelating = false
            AlarmsFragment.fabId.hide()
            AlarmsFragment.fabId.setImageResource(R.drawable.ic_add_white_24dp)
            AlarmsFragment.fabId.startAnimation(animation)
            AlarmsFragment.fabId.show()
            AlarmsFragment.fabId.setOnClickListener {
                AlarmDialog().show(
                    (context as AppCompatActivity).supportFragmentManager,
                    "aa"
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return captions.size
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val cv = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_captioned_image, parent, false) as CardView
        context = parent.context
        fM = (parent.context as AppCompatActivity).supportFragmentManager
        return ViewHolder(cv)
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        listOfHolders.add(holder)
        val cardView = holder.cardView
        val switch = (cardView.findViewById<View>(R.id.switch3) as Switch)
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            AlarmsFragment.listAlarms[position].state = isChecked
            AlarmsFragment.updateAlarm(
                AlarmsFragment.listAlarms[position]
            )
            if (isChecked) {
                val c = Calendar.getInstance()
                val toast = Toast.makeText(
                    cardView.context,
                    AlarmDialog.CountDiff(
                        c.get(Calendar.HOUR),
                        c.get(Calendar.MINUTE),
                        AlarmsFragment.listAlarms[position].hour,
                        AlarmsFragment.listAlarms[position].minute
                    ),
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }

        val imgView = cardView.findViewById<ImageView>(R.id.colormark)

        val color: Int =
            (captions[position].red and 0xff shl 16) or (captions[position].green and 0xff shl 8) or (captions[position].blue and 0xff)

        imgView.background.setColorFilter(
            Color.parseColor(
                String.format(
                    "#%06X",
                    0xFFFFFF and color
                )
            ), PorterDuff.Mode.SRC_ATOP
        )


        val state = captions[position].state
        switch.isChecked = state

        val textView = cardView.findViewById<View>(R.id.info_text) as TextView
        textView.text = "${captions[position].hour}:${captions[position].minute}"

        cardView.setOnClickListener {
            if (stateOfDelating == false) {
                val bundle = Bundle()
                bundle.putString(id_, AlarmsFragment.listAlarms[position].id)
                bundle.putInt(hour, AlarmsFragment.listAlarms[position].hour)
                bundle.putInt(minute, AlarmsFragment.listAlarms[position].minute)
                bundle.putInt(listDays, AlarmsFragment.listAlarms[position].listDays)
                bundle.putInt(repeatMode, AlarmsFragment.listAlarms[position].repeatMode)
                bundle.putInt(red, AlarmsFragment.listAlarms[position].red)
                bundle.putInt(green, AlarmsFragment.listAlarms[position].green)
                bundle.putInt(blue, AlarmsFragment.listAlarms[position].blue)
                val aD = AlarmDialog()
                aD.arguments = bundle
                aD.show(fM, "aa")
            } else {
                val cB = cardView.findViewById<CheckBox>(R.id.checkbox)
                cB.isChecked = !cB.isChecked
                if (cB.isChecked) listItemsToErase.add(captions[position])
                else listItemsToErase.remove(captions[position])
            }
        }

        val checkBox = cardView.findViewById<CheckBox>(R.id.checkbox)


        if (stateOfDelating == true) {
            checkBox.visibility = View.VISIBLE
            switch.visibility = View.INVISIBLE
        }



        cardView.setOnLongClickListener {
            val animation: Animation =
                AnimationUtils.loadAnimation(cardView.context, R.anim.scale_up)

            if (stateOfDelating == false) {
                stateOfDelating = true
                listItemsToErase = ArrayList()
                for (i in listOfHolders) {
                    i.cardView.switch3.visibility = View.INVISIBLE
                    i.cardView.checkbox.startAnimation(animation)
                    i.cardView.checkbox.visibility = View.VISIBLE
                }

                AlarmsFragment.fabId.hide()
                AlarmsFragment.fabId.setImageResource(R.drawable.ic_trash)
                AlarmsFragment.fabId.startAnimation(animation)
                AlarmsFragment.fabId.show()
                AlarmsFragment.fabId.setOnClickListener {
                    CaptionedImagesAdapter.deinitDelating(context)

                    for (item in listItemsToErase) AlarmsFragment.deleteAlarm(item)

                    AlarmsFragment.queryData()
                    AlarmsFragment.adapter.notifyDataSetChanged()

                }
            }

            true
        }

        initTextDays(cardView, position)

    }

    fun initTextDays(cardView: CardView, position: Int) {
        val infoText2 = cardView.findViewById<TextView>(R.id.info_text2)
        val str = StringBuilder()
        val list =
            AlarmsFragment.Alarm.convertIntToArrayBoolean(AlarmsFragment.listAlarms[position].listDays)
        if (list[0]) str.append(context.getString(R.string.mon) + " ")
        if (list[1]) str.append(context.getString(R.string.tue) + " ")
        if (list[2]) str.append(context.getString(R.string.wed) + " ")
        if (list[3]) str.append(context.getString(R.string.thu) + " ")
        if (list[4]) str.append(context.getString(R.string.fri) + " ")
        if (list[5]) str.append(context.getString(R.string.sat) + " ")
        if (list[6]) str.append(context.getString(R.string.sun) + " ")
        str.append("| ")

        str.append(context.resources.getStringArray(R.array.spinnerItems)[AlarmsFragment.listAlarms[position].repeatMode])
        infoText2.text = str.toString()
    }

}