package com.example.moneycalc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.moneycalc.DefaultValues.DefaultValues.money_with_profit
import com.example.moneycalc.DefaultValues.DefaultValues.vacation_money_from_big_half
import com.example.moneycalc.DefaultValues.DefaultValues.vacation_money_from_little_half
import com.facebook.stetho.Stetho
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var sPref: SharedPreferences
    lateinit var sPrefSettings: SharedPreferences

    private var moneyMap = mutableMapOf(
        "today_money" to 0,
        "play_money" to 0,
        "vacation_money" to 0,
        "salary_money" to 0,
        "salary_money__days" to 0,
        "mom_money" to 0,
        "percent_money" to 0,
        "summary_money" to 0,

        "no_space_money" to 0,

        /// variables //// need to * 100
        "daily_money" to 0,
        "salary_money_summary" to 0,
        "salary_money_little_half" to 0,
        "salary_money_big_half" to 0,
        "money_with_profit" to 0,
        "vacation_money_from_little_half" to 0,
        "vacation_money_from_big_half" to 0,
        "play_money_from_salary" to 0
    )

    lateinit var progressBar: ProgressBar

    lateinit var todayMoney: TextView

    lateinit var playMoney: TextView
    lateinit var vacationMoney: TextView
    lateinit var salaryMoney : TextView
    lateinit var salaryMoneyDays: TextView
    lateinit var momMoney : TextView
    lateinit var percentMoney : TextView
    lateinit var summaryMoney: TextView

    lateinit var noSpaceMoney: TextView





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Stetho.initializeWithDefaults(this);


        sPref = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sPrefSettings = PreferenceManager.getDefaultSharedPreferences(this)


            updateMoneyData()

            progressBar = findViewById<ProgressBar>(R.id.progressBar)

            todayMoney = findViewById<TextView>(R.id.today_money)

            playMoney = findViewById<TextView>(R.id.play_money)
            vacationMoney = findViewById<TextView>(R.id.vacation_money)
            salaryMoney = findViewById<TextView>(R.id.salary_money)
            salaryMoneyDays = findViewById<TextView>(R.id.salary_money__days)
            momMoney = findViewById<TextView>(R.id.mom_money)
            percentMoney = findViewById<TextView>(R.id.percent_money)
            summaryMoney = findViewById<TextView>(R.id.summary_money)
            noSpaceMoney = findViewById<TextView>(R.id.no_space_money)



            updateMoneyView()


        var handler = Handler()


        swipeRefreshLayout.setOnRefreshListener {

            updateMoneyData()
            updateMoneyView()

            swipeRefreshLayout.isRefreshing = false

        }

        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

            val todayDate = getDateString()

            if (sPref.getString("daily_money_date", "") != todayDate) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.new_day)
                    .setMessage(R.string.new_daily_money)
                    .setNeutralButton(R.string.later) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(R.string.yes){ dialog, which ->
                        moneyMap["play_money"] = moneyMap["today_money"]!! + moneyMap["play_money"]!!
                        moneyMap["today_money"] = moneyMap["daily_money"]!!
                        if (moneyMap["salary_money"]!! - moneyMap["daily_money"]!! < 0) {
                            moneyMap["play_money"] = moneyMap["play_money"]!! - (moneyMap["daily_money"]!! - moneyMap["salary_money"]!!)
                            moneyMap["salary_money"] = 0
                            Toast.makeText(
                                applicationContext,
                                "Денег не хватило, взяли из Развлекух. Ждем зарплату, как соловей лета!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            moneyMap["salary_money"] = moneyMap["salary_money"]!! - moneyMap["daily_money"]!!
                            Toast.makeText(applicationContext, "Денежки начислены!", Toast.LENGTH_SHORT).show()
                        }
                        refreshMoney()
                        sPref.edit().putString("daily_money_date", todayDate).apply()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.not_today){ dialog, which ->
                        sPref.edit().putString("daily_money_date", todayDate).apply()
                        dialog.dismiss()
                    }
                    .show()
            }

    }

    override fun onResume() {
        super.onResume()
        refreshMoney()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }


    private fun updateMoneyData() {
        ///// variables /////
        moneyMap["daily_money"] = sPrefSettings.getString("daily_money", "0")!!.toInt()*100
        moneyMap["salary_money_summary"] = sPrefSettings.getString("salary_money_summary", "0")!!.toInt()*100
        moneyMap["salary_money_little_half"] = sPrefSettings.getString("salary_money_little_half", "0")!!.toInt()*100
        moneyMap["salary_money_big_half"] = sPrefSettings.getString("salary_money_big_half", "0")!!.toInt()*100
        moneyMap["money_with_profit"] = sPrefSettings.getString("money_with_profit", "0")!!.toInt()*100
        moneyMap["vacation_money_from_little_half"] = sPrefSettings.getString("vacation_money_from_little_half", "0")!!.toInt()*100
        moneyMap["vacation_money_from_big_half"] = sPrefSettings.getString("vacation_money_from_big_half", "0")!!.toInt()*100
        moneyMap["play_money_from_salary"] = sPrefSettings.getString("play_money_from_salary", "0")!!.toInt()*100
        ///// variables /////


        moneyMap["today_money"] = sPref.getInt("today_money", 0)
        moneyMap["play_money"] = sPref.getInt("play_money", 0)
        moneyMap["vacation_money"] = sPref.getInt("vacation_money", 0)

        moneyMap["salary_money"] = sPref.getInt("salary_money", 0)
        moneyMap["salary_money__days"] = moneyMap["salary_money"]!! / moneyMap["daily_money"]!!

        moneyMap["mom_money"] = sPref.getInt("mom_money", 0)
        moneyMap["percent_money"] = sPref.getInt("percent_money", 0)
        moneyMap["summary_money"] = moneyMap["today_money"]!! + moneyMap["play_money"]!! + moneyMap["vacation_money"]!! + moneyMap["mom_money"]!! + moneyMap["percent_money"]!! //without salary money
        if (moneyMap["summary_money"]!! > money_with_profit) {
            moneyMap["no_space_money"] =  moneyMap["summary_money"]!! - money_with_profit
            moneyMap["summary_money"] = money_with_profit
        } else {
            moneyMap["no_space_money"] = 0
        }
    }

    private fun updateMoneyView() {
        progressBar.setProgress(
            100 - (moneyMap["today_money"]!!.toFloat() / moneyMap["daily_money"]!!.toFloat() * 100).toInt(),
            true
        )

        todayMoney.text = moneyMap["today_money"]!!.toRubbles()

        playMoney.text = moneyMap["play_money"]!!.toRubbles()
        vacationMoney.text = moneyMap["vacation_money"]!!.toRubbles()
        salaryMoney.text = (moneyMap["salary_money"]!! / 100).toString()
        salaryMoneyDays.text = moneyMap["salary_money__days"].toString() + " дней"
        momMoney.text = moneyMap["mom_money"]!!.toRubbles()
        percentMoney.text = moneyMap["percent_money"]!!.toRubbles()
        summaryMoney.text = moneyMap["summary_money"]!!.toRubbles()
        noSpaceMoney.text = moneyMap["no_space_money"]!!.toRubbles()
    }


    private fun writeMoney() {
        sPref.edit()
            .putInt("today_money", moneyMap["today_money"]!!)
            .putInt("play_money", moneyMap["play_money"]!!)
            .putInt("vacation_money", moneyMap["vacation_money"]!!)

            .putInt("salary_money", moneyMap["salary_money"]!!)
            .putInt("mom_money", moneyMap["mom_money"]!!)
            .putInt("percent_money", moneyMap["percent_money"]!!)
            .apply()
    }


    private fun refreshMoney() {
        writeMoney()
        updateMoneyData()
        updateMoneyView()
    }


    fun changeTodayMoney(view: View) {
        var isMinusMoney = true

        val numberView = layoutInflater.inflate(R.layout.money_adding_dialog_layout, null)


        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.whatsup)
            .setView(numberView)
            .setNeutralButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.ok){ dialog, which ->
                val money = if (numberView.findViewById<EditText>(R.id.editTextNumber).text.isNotEmpty()) (numberView.findViewById<EditText>(R.id.editTextNumber).text.toString().toFloat()*100).toInt() else 0
                lateinit var toast: Toast

                if (isMinusMoney) {
                    if (moneyMap["today_money"]!! - money < 0) {
                        moneyMap["play_money"] = moneyMap["play_money"]!! - (money - moneyMap["today_money"]!!)
                        toast = Toast.makeText(
                            applicationContext,
                            "Не хватило денег, взяли из развлекух " + (money - moneyMap["today_money"]!!).toRubbles() + " руб.",
                            Toast.LENGTH_SHORT
                        )
                        moneyMap["today_money"] = 0
                    } else {
                        moneyMap["today_money"] = moneyMap["today_money"]!! - money
                        toast = Toast.makeText(
                            applicationContext,
                            "Потрачено " + (money.toRubbles()) + " руб.",
                            Toast.LENGTH_SHORT
                        )
                    }
                } else {
                    if (moneyMap["today_money"]!! + money > moneyMap["daily_money"]!!) {
                        moneyMap["play_money"] = moneyMap["play_money"]!! + (moneyMap["today_money"]!! + money - moneyMap["daily_money"]!!)
                        toast = Toast.makeText(
                            applicationContext,
                            "Привалило, закинули в развлекухи " + (moneyMap["today_money"]!! + money - moneyMap["daily_money"]!!).toRubbles() + " руб.",
                            Toast.LENGTH_SHORT
                        )
                        moneyMap["today_money"] = moneyMap["daily_money"]!!
                    } else {
                        moneyMap["today_money"] = moneyMap["today_money"]!! + money
                        toast = Toast.makeText(
                            applicationContext,
                            "Привалило " + (money).toRubbles() + " руб.",
                            Toast.LENGTH_SHORT
                        )
                    }
                }

                toast.show()
                refreshMoney()
                dialog.dismiss()
            }.show()


        val IBPlus = dialog.findViewById<ImageButton>(R.id.IBPlus)
        val IBMinus = dialog.findViewById<ImageButton>(R.id.IBMinus)

        if (isMinusMoney) {
            IBMinus!!.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorPrimary
                )
            )
            IBPlus!!.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorGrey
                )
            )
        } else {
            IBPlus!!.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorPrimary
                )
            )
            IBMinus!!.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorGrey
                )
            )
        }

        IBPlus.setOnClickListener {
            isMinusMoney = false
            IBPlus.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorPrimary
                )
            )
            IBMinus.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorGrey
                )
            )
        }

        IBMinus.setOnClickListener {
            isMinusMoney = true
            IBMinus.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorPrimary
                )
            )
            IBPlus.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorGrey
                )
            )
        }
    }

    fun changePlayMoney(view: View) {
        openAddingOrChangingMoneyDialog("play_money")
    }
    fun changeVacationMoney(view: View) {
        openAddingOrChangingMoneyDialog("vacation_money")
    }
    fun changeSalaryMoney(view: View) {
        openSalaryAddingDialog()
    }
    fun changeMomMoney(view: View) {
        openAddingOrChangingMoneyDialog("mom_money")
    }
    fun changePercentMoney(view: View) {
        openAddingOrChangingMoneyDialog("percent_money")
    }



    private fun openSalaryAddingDialog() {
        val options = arrayOf("Пришла большая часть!", "Пришла меньшая часть", "Изменить вручную")


        MaterialAlertDialogBuilder(this)
            .setTitle("Что с зарплатой?")
            .setItems(options) { dialog, which ->
                when(which) {
                    0, 1 -> {
                        val savings_vacation =
                            when (which) {
                                0 -> moneyMap["vacation_money_from_big_half"]!!
                                1 -> moneyMap["vacation_money_from_little_half"]!!
                                else -> 0
                            }
                        val savings_play = moneyMap["play_money_from_salary"]!! / 2

                        val salary =
                            when (which) {
                                0 -> moneyMap["salary_money_big_half"]!!
                                1 -> moneyMap["salary_money_little_half"]!!
                                else -> 0
                            }
                        val needed_money =
                            moneyMap["daily_money"]!! * 15 + savings_play + savings_vacation

                        moneyMap["salary_money"] =
                            moneyMap["salary_money"]!! + moneyMap["daily_money"]!! * 15
                        moneyMap["play_money"] =
                            moneyMap["play_money"]!! + (salary - needed_money) + savings_play
                        moneyMap["vacation_money"] =
                            moneyMap["vacation_money"]!! + savings_vacation

                        refreshMoney()
                        dialog.dismiss()
                    }
                    2 -> openChangingDialog("salary_money")
                }
            }.show()
    }







    private fun openAddingOrChangingMoneyDialog(account: String) {

        var title = "Что делаем? ("

        when(account) {
            "play_money" -> {
                title += "На развлекухи)"
            }
            "vacation_money" -> {
                title += "На отдых)"
            }
            "mom_money" -> {
                title += "Мамины денежки)"
            }
            "percent_money" -> {
                title += "Процентики)"
            }
        }

        val options = arrayOf("Добавить / убавить", "Изменить")


        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setItems(options) { dialog, which ->
                when(which) {
                    0 -> openAddingDialog(account)
                    1 -> openChangingDialog(account)
                }
            }.show()

    }


    fun openAddingDialog(account: String){
        var isMinusMoney = true
        val numberView = layoutInflater.inflate(R.layout.money_adding_dialog_layout, null)


        ////////// диалог прибавления бабла для развлекух, отдыха и маминых денежек //////////

        val dialogAddRemove = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.whatsup)
            .setView(numberView)
            .setNeutralButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.ok){ dialog, which ->
                val money = if (numberView.findViewById<EditText>(R.id.editTextNumber).text.toString().isNotEmpty()) (numberView.findViewById<EditText>(R.id.editTextNumber).text.toString().toFloat()*100).toInt()  else 0
                lateinit var toast: Toast


                if (isMinusMoney) {
                    if (moneyMap[account]!! - money < 0) {
                        toast = Toast.makeText(
                            applicationContext,
                            "Нет столько денег",
                            Toast.LENGTH_SHORT
                        )
                    } else {
                        moneyMap[account] = moneyMap[account]!! - money
                        toast = Toast.makeText(
                            applicationContext,
                            "Вычтено " + (money.toRubbles()) + " руб.",
                            Toast.LENGTH_SHORT
                        )
                    }
                } else {
                    moneyMap[account] =moneyMap[account]!! + money
                    toast = Toast.makeText(
                        applicationContext,
                        "Прибавлено " + (money).toRubbles() + " руб.",
                        Toast.LENGTH_SHORT
                    )
                }
                toast.show()
                refreshMoney()
                dialog.dismiss()
            }.show()


        val IBPlus = dialogAddRemove.findViewById<ImageButton>(R.id.IBPlus)
        val IBMinus = dialogAddRemove.findViewById<ImageButton>(R.id.IBMinus)

        if (isMinusMoney) {
            IBMinus!!.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorPrimary
                )
            )
            IBPlus!!.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorGrey
                )
            )
        } else {
            IBPlus!!.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorPrimary
                )
            )
            IBMinus!!.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorGrey
                )
            )
        }

        IBPlus.setOnClickListener {
            isMinusMoney = false
            IBPlus.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorPrimary
                )
            )
            IBMinus.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorGrey
                )
            )
        }

        IBMinus.setOnClickListener {
            isMinusMoney = true
            IBMinus.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorPrimary
                )
            )
            IBPlus.background.setTint(
                ContextCompat.getColor(
                    this@MainActivity,
                    R.color.colorGrey
                )
            )
        }

        ////////// диалог прибавления бабла для развлекух, отдыха и маминых денежек //////////


    }


    private fun openChangingDialog(account: String){
        val numberView = layoutInflater.inflate(R.layout.money_changing_dialog_layout, null)


        ////////// диалог изменения бабла для развлекух, отдыха и маминых денежек //////////
        //////// и зарплаты, но не напрямую /////////

        var dialogChange = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.how_much)
            .setView(numberView)
            .setNeutralButton(R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.ok){ dialog, which ->
                val money = if (!numberView.findViewById<EditText>(R.id.editTextNumber2).text.toString().isEmpty()) (numberView.findViewById<EditText>(R.id.editTextNumber2).text.toString().toFloat()*100).toInt() else 0
                lateinit var toast: Toast

                moneyMap[account] = money
                Toast.makeText(
                    applicationContext,
                    "Изменено на " + (money).toRubbles() + " руб.",
                    Toast.LENGTH_SHORT
                ).show()

                refreshMoney()
                dialog.dismiss()
            }.show()


        ////////// диалог изменения бабла для развлекух, отдыха и маминых денежек //////////


    }

    fun openSettings(item: MenuItem) {
        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
        startActivity(intent)
    }

}