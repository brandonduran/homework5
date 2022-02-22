package com.example.contactlist

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.io.IOException
import java.security.AccessController.getContext
import java.util.ArrayList
import java.util.HashSet
import android.R.string.no




lateinit var arrayAdapter: CustomListViewAdapter
var itemsList = ArrayList<String>()
var costList = ArrayList<Float>()
var quantityList = ArrayList<Int>()
var listTotal = 0.0
var flag = true
lateinit var sharedPreferences: SharedPreferences

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {
    lateinit var listView: ListView
    lateinit var totalCost : TextView
    lateinit var redx : ImageView
    lateinit var seekBar : SeekBar
    lateinit var limit : EditText
    lateinit var mediaPlayer: MediaPlayer
    lateinit var hornButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)
        totalCost = findViewById(R.id.totalTextView)
        redx = findViewById(R.id.imageView)
        seekBar = findViewById(R.id.seekBar)
        hornButton = findViewById(R.id.hornButton)
        limit = findViewById(R.id.totalTextView1)

        limit.setOnKeyListener(limitKey)
        limit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == KeyEvent.KEYCODE_ENTER) {
                hide()
                true
            }
            else false
        }//onEditorActionListener
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.airhorn)
        seekBar.setOnSeekBarChangeListener(this)

        totalCost.text = "$" + "%.2f".format(listTotal)
        getData()
        seekBar.progress = listTotal.toInt()

        arrayAdapter = CustomListViewAdapter(this, itemsList, costList, quantityList)
        //arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemsList)
        listView.adapter = arrayAdapter
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val intent = Intent(applicationContext, EditorActivity::class.java)
                intent.putExtra("Loc", i)
                startActivity(intent)
            }//OnItemClickListener

        listView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { adapterView, view, i, l ->

                val itemDelete = i
                AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_input_delete)
                    .setTitle("Remove Item")
                    .setMessage("Do you want to remove " + itemsList[itemDelete] + " from your shopping list?")
                    .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                        listTotal -= costList[itemDelete].toDouble() * quantityList[itemDelete]
                        itemsList.removeAt(itemDelete)
                        costList.removeAt(itemDelete)
                        quantityList.removeAt(itemDelete)
                        seekBar.progress = listTotal.toInt()
                        totalCost.text = "$" + "%.2f".format(listTotal)
                        arrayAdapter.notifyDataSetChanged()
                        sharedPreferences.edit()
                            .putString("theitems", ObjectSerializer.serialize(itemsList))
                            .apply()
                        sharedPreferences.edit()
                            .putString("thecosts", ObjectSerializer.serialize(costList))
                            .apply()
                        sharedPreferences.edit().putString(
                            "thequantities",
                            ObjectSerializer.serialize(quantityList)
                        )
                       .apply()
                        checkOver()
                    }
                    .setNegativeButton("No", null)
                    .show()

                return@OnItemLongClickListener true
            }//OnItemCLonglickListener

        if (listTotal > limit.text.toString().toDouble() ) {
            redx.animate().translationYBy(2000f).alpha(1f).rotation(720f).duration = 2200
        }

    }//onCreate

    val limitKey = object : View.OnKeyListener {
        override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
            if (p1 == KeyEvent.KEYCODE_ENTER) {
                hide()
                return true
            }
            else return false
        }
    }//limitKey

    fun hide() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        limit.isFocusable = false
        limit.isFocusableInTouchMode = true
        sharedPreferences.edit().putInt("currentLimit", limit.text.toString().toInt() ).apply()
        if (limit.text.toString() != "") {
            var userInput = limit.text.toString().toInt()
            seekBar.max = userInput
            seekBar.progress = listTotal.toInt()
        }
    }//hide

    fun checkOver() {
        if (listTotal > limit.text.toString().toDouble() ) {
            Toast.makeText(applicationContext, "Woah, be careful! You're over budget.", Toast.LENGTH_SHORT).show()
            if (flag) mediaPlayer.start() //only play air horn when enabled
        }
    }//checkover

    fun clearList(view: View) {
        itemsList.clear()
        costList.clear()
        quantityList.clear()
        arrayAdapter.clear()
        arrayAdapter.notifyDataSetChanged()
        sharedPreferences.edit()
            .putString("theitems", ObjectSerializer.serialize(itemsList))
            .apply()
        sharedPreferences.edit()
            .putString("thecosts", ObjectSerializer.serialize(costList))
            .apply()
        sharedPreferences.edit().putString("thequantities", ObjectSerializer.serialize(quantityList))
            .apply()
/*
        applicationContext.getSharedPreferences("totalCost", 0).edit().clear().apply()*/
        listTotal = 0.0
        totalCost.text = "$" + "%.2f".format(listTotal)
        seekBar.progress = 0
    }//clearList

    fun getData() {
        sharedPreferences = applicationContext.getSharedPreferences(
            "com.example.contactlist", Context.MODE_PRIVATE
        )

        var newFriends = ArrayList<String?>()
        var new1 = ArrayList<Float?>()
        var new2 = ArrayList<Int?>()
        var testF : Float?
        var currentLimit : Int?

        newFriends = ObjectSerializer.deserialize(
                sharedPreferences
                    .getString("theitems", ObjectSerializer.serialize(ArrayList<String>()))
            ) as ArrayList<String?>

        testF = sharedPreferences.getFloat("totalCost", 0.0f)
        listTotal = testF.toDouble()
        totalCost.text = "$" + "%.2f".format(listTotal)

        currentLimit = sharedPreferences.getInt("currentLimit", 0)
        limit.setText( currentLimit.toString() )
        seekBar.max = currentLimit

        if (newFriends.size != 0) {
            itemsList = ArrayList(newFriends)
        }

        new1 = ObjectSerializer.deserialize(
                sharedPreferences
                    .getString("thecosts", ObjectSerializer.serialize(ArrayList<Float>()))
            ) as ArrayList<Float?>

        if (new1.size != 0) {
            costList = ArrayList(new1)
        }

        new2 = ObjectSerializer.deserialize(
                sharedPreferences
                    .getString("thequantities", ObjectSerializer.serialize(ArrayList<Int>()))
            ) as ArrayList<Int?>

        if (new2.size != 0) {
            quantityList = ArrayList(new2)
        }

    }//getData

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add, menu)
        return super.onCreateOptionsMenu(menu)
    }//onCreateOptionsMenu

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.add) {
            val intent = Intent(applicationContext, AddActivity::class.java)
            startActivity(intent)
            return true
        }
        return false
    }//onOptionsItemSelected

    override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
        if ( seekBar.max < listTotal ) {
            seekBar.progress = seekBar.max
            checkOver()
        }
        else if (progress.toDouble() / seekBar.max > 0.75 && !fromUser) {
            Toast.makeText(
                applicationContext,
                "You have reached 75% or more of your spending limit!",
                Toast.LENGTH_SHORT
            ).show()
        }
        else return
    }//onProgressChanged, seekBar

    override fun onStartTrackingTouch(p0: SeekBar?) {}
    override fun onStopTrackingTouch(p0: SeekBar?) {
        seekBar.progress = listTotal.toInt()
    }

    fun disableHorn(view : View) {
        if (flag) hornButton.setImageResource(R.drawable.volume_on)
        else hornButton.setImageResource(R.drawable.volume_off)
        flag = !flag
    }//disableHorn

}//MainActivity
