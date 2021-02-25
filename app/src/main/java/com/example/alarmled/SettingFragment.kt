package com.example.alarmled

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment


class SettingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numbers = context?.getSharedPreferences("VAR", 0)
        val ip = numbers?.getString("IP", "192.168.1.1")
        var time = numbers?.getInt("TIME", 15)

        val iPText = view.findViewById<TextView>(R.id.IP_text_view)!!
        iPText.text = ip
        val timeText = view.findViewById<TextView>(R.id.time_text_view)!!
        timeText.text = time.toString()

        iPText.setOnClickListener {
            val d = Dialog(view.context)
            if (d.window != null) {
                d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                d.window?.requestFeature(Window.FEATURE_NO_TITLE)
                // dialog?.window?.getAttributes()?.windowAnimations = R.style.MyAnimation_Window;
            }
            d.setTitle("IpPicker")
            d.setContentView(R.layout.ip_picker_dialog2)

            val cancel = d.findViewById<View>(R.id.button1) as ImageButton
            val ok = d.findViewById<View>(R.id.button2) as ImageButton

            val editIp1 = d.findViewById<EditText>(R.id.editIp1)
            val editIp2 = d.findViewById<EditText>(R.id.editIp2)
            val editIp3 = d.findViewById<EditText>(R.id.editIp3)
            val editIp4 = d.findViewById<EditText>(R.id.editIp4)

            editIp1.imeOptions = EditorInfo.IME_ACTION_NONE
            editIp2.imeOptions = EditorInfo.IME_ACTION_NONE
            editIp3.imeOptions = EditorInfo.IME_ACTION_NONE
            editIp4.imeOptions = EditorInfo.IME_ACTION_NONE

            fun onEnter(
                editText: EditText
            ): (View, Int, KeyEvent) -> Boolean =
                { _: View, i: Int, keyEvent: KeyEvent ->
                    if ((keyEvent.action == KeyEvent.ACTION_DOWN) and (i == KeyEvent.KEYCODE_ENTER)) {
                        editText.requestFocus()
                        true
                    }
                    false
                }

            editIp1.addTextChangedListener {
                if (editIp1.text.length == 3) {
                    editIp2.requestFocus()
                }
            }
            editIp1.setOnKeyListener(onEnter(editIp2))

            editIp2.addTextChangedListener {
                if (editIp2.text.length == 3) {
                    editIp3.requestFocus()
                }
            }
            editIp2.setOnKeyListener(onEnter(editIp3))

            editIp3.addTextChangedListener {
                if (editIp3.text.length == 3) {
                    editIp4.requestFocus()
                }
            }
            editIp3.setOnKeyListener(onEnter(editIp4))

            editIp4.setOnKeyListener { _: View, i: Int, keyEvent: KeyEvent ->
                if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    ok.callOnClick()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }

            cancel.setOnClickListener {
                d.dismiss()
            }

            ok.setOnClickListener {
                if (editIp1.text.isEmpty() || editIp3.text.isEmpty() || editIp3.text.isEmpty() || editIp4.text.isEmpty()) {
                    val toast = Toast.makeText(
                        AlarmsFragment.appContext, "IP is invalid",
                        Toast.LENGTH_LONG
                    )
                    toast.show()
                } else {
                    val str = "${editIp1.text}.${editIp2.text}.${editIp3.text}.${editIp4.text}"
                    iPText.text = str
                    val editor = context?.getSharedPreferences("VAR", 0)?.edit()
                    editor?.putString("IP", str)
                    editor?.apply()
                    d.dismiss()
                }
            }
            d.show()
        }



        timeText.setOnClickListener {

            val d = Dialog(view.context)
            if (d.window != null) {
                d.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                d.window?.requestFeature(Window.FEATURE_NO_TITLE)
                // dialog?.window?.getAttributes()?.windowAnimations = R.style.MyAnimation_Window;
            }
            d.setTitle("NumberPicker")
            d.setContentView(R.layout.time_picker_dialog)
            val cancel = d.findViewById<View>(R.id.button1) as ImageButton
            val ok = d.findViewById<View>(R.id.button2) as ImageButton
            val np = d.findViewById<View>(R.id.numberPicker1) as NumberPicker
            val textViewInfo = d.findViewById<TextView>(R.id.text_view_info)
            textViewInfo.text = "Led will start \nshining ${time!!} minutes  \nbefore alarm"
            np.setOnValueChangedListener { _, _, newVal ->
                textViewInfo.text = "Led will start \nshining ${newVal} minutes  \nbefore alarm"
            }
            np.maxValue = 30
            np.minValue = 0
            np.value = time!!
            np.wrapSelectorWheel = false

            cancel.setOnClickListener {
                d.dismiss()
            }

            ok.setOnClickListener {
                timeText.text = np.value.toString()
                time = np.value
                val editor = context?.getSharedPreferences("VAR", 0)?.edit()
                editor?.putInt("TIME", np.value)
                editor?.apply()
                d.dismiss()
            }
            d.show()
        }
    }
}

