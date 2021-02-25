package com.example.alarmled

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment
import com.yalantis.contextmenu.lib.MenuObject
import com.yalantis.contextmenu.lib.MenuParams
import kotlinx.android.synthetic.main.fragment_alarms.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity() {
    private lateinit var contextMenuDialogFragment: ContextMenuDialogFragment
    private var shareActionProvider: ShareActionProvider? = null
    private lateinit var chosenItem: MenuItem

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initMenuFragment()

        val animation2: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.scale_down)


        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        val pager: ViewPager = findViewById<ViewPager>(R.id.pager)
        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {


            val animation: Animation =
                AnimationUtils.loadAnimation(applicationContext, R.anim.scale_up)

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    1 -> {
                        fab.show()
                        fab.startAnimation(animation)
                    }
                    else -> fab.hide()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        val tabLayout: TabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(pager)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val tv = findViewById<TextView>(R.id.ChoiceTitle)
        //val item = findViewById(R.id.context_menu) as MenuItem
        val settings = applicationContext.getSharedPreferences("VAR", 0)
        val animation: Animation =
            AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)

        when (settings.getInt("MODE", 1)) {
            1 -> {
                tv.text = getString(R.string.always_turn_off)
                menu?.getItem(0)?.icon = getDrawable(

                    R.drawable.ic_brightness_5_white_48dp
                )
            }
            2 -> {
                tv.text = getString(R.string.only_alarms)
                menu?.getItem(0)?.icon = getDrawable(

                    R.drawable.ic_brightness_4_white_48dp
                )
            }
            3 -> {
                tv.text = getString(R.string.always_turn_on)
                menu?.getItem(0)?.icon = getDrawable(

                    R.drawable.ic_wb_sunny_white_48dp
                )
            }
        }
        tv.startAnimation(animation)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.context_menu -> {
                val tv = findViewById<TextView>(R.id.ChoiceTitle)
                val animation: Animation =
                    AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out)
                tv.startAnimation(animation)
                chosenItem = item
                showContextMenuDialogFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showContextMenuDialogFragment() {
        if (supportFragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
            contextMenuDialogFragment.show(supportFragmentManager, ContextMenuDialogFragment.TAG)
        }
    }

    private fun initToolbar() {

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setHomeButtonEnabled(false)
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
        }
/*
        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { onBackPressed() }
        }
*/

    }

    override fun onBackPressed() {
        if (CaptionedImagesAdapter.stateOfDelating) {
            CaptionedImagesAdapter.deinitDelating(applicationContext)
        } else
            super.onBackPressed()
    }

    private fun initMenuFragment() {
        val menuParams = MenuParams(
            actionBarSize = resources.getDimension(R.dimen.tool_bar_height).toInt(),
            menuObjects = getMenuObjects(),
            isClosableOutside = true,
            animationDuration = 100,
            backgroundColorAnimationDuration = 100


        )


        val animation: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)

        contextMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams).apply {
            menuItemClickListener = { view, position ->
                val tv = findViewById<TextView>(R.id.ChoiceTitle)

                when (position) {
                    0 -> {
                        tv.startAnimation(animation)
                    }
                    1 -> {
                        tv.startAnimation(animation)
                        tv.text = getString(R.string.always_turn_off)
                        chosenItem.icon = ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.ic_brightness_5_white_48dp
                        )
                    }
                    2 -> {
                        tv.startAnimation(animation)
                        tv.text = getString(R.string.only_alarms)
                        chosenItem.icon = ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.ic_brightness_4_white_48dp
                        )
                    }
                    3 -> {
                        tv.startAnimation(animation)
                        tv.text = getString(R.string.always_turn_on)
                        chosenItem.icon = ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.ic_wb_sunny_white_48dp
                        )
                    }
                }

                if (position != 0) {
                    val settings = applicationContext.getSharedPreferences("VAR", 0)
                    val editor = settings.edit()
                    editor.putInt("MODE", position)
                    editor.apply()
                }
            }
        }
    }

    private fun getMenuObjects() = mutableListOf<MenuObject>().apply {
        var darkMode = isUsingNightModeResources()
        val close = MenuObject().apply {
            setResourceValue(R.drawable.ic_arrow_back)
            if(darkMode)
            setBgColorValue(Color.parseColor("#bb000000"))
        }

        val low =
            MenuObject(getString(R.string.always_turn_off)).apply {

                if(darkMode) {
                    setResourceValue(R.drawable.ic_brightness_5_white_48dp)
                    setBgColorValue(Color.parseColor("#bb000000"))
                }
                else setResourceValue(R.drawable.ic_brightness_5_resource_color_48dp)
            }

        val alarms = MenuObject(getString(R.string.only_alarms)).apply {

            if(darkMode) {
                setBgColorValue(Color.parseColor("#bb000000"))
                setResourceValue(R.drawable.ic_brightness_4_white_48dp)
            }
            else setResourceValue(R.drawable.ic_brightness_4_resource_color_48dp)
        }
        val always = MenuObject(getString(R.string.always_turn_on)).apply {

            if(darkMode) {
                setBgColorValue(Color.parseColor("#bb000000"))
                setResourceValue(R.drawable.ic_wb_sunny_white_48dp)
            }
            else   setResourceValue(R.drawable.ic_wb_sunny_resource_color_48dp)
        }
        add(close)
        add(low)
        add(alarms)
        add(always)
    }

    private inner class SectionsPagerAdapter(fm: FragmentManager?) :
        FragmentStatePagerAdapter(fm!!) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return TopFragment()
                1 -> {
                    /*     val animation: Animation =
                             AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)

                         fab?.startAnimation(animation)*/
                    return AlarmsFragment()
                }
                2 -> return SettingFragment()

            }
            return null!!
        }

        override fun getCount(): Int {
            return 3
        }


        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return resources.getText(R.string.home_tab)
                1 -> return resources.getText(R.string.alarms_tab)
                2 -> return resources.getText(R.string.ustawienia)
            }
            return null
        }
    }
    private fun isUsingNightModeResources(): Boolean {
        return when (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

}