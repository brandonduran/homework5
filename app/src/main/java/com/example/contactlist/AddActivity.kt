package com.example.contactlist

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.text.InputFilter




class AddActivity : AppCompatActivity() {
    lateinit var items : EditText
    lateinit var cost : EditText
    lateinit var quantity : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        items = findViewById(R.id.itemT)
        cost = findViewById(R.id.theCost)
        quantity = findViewById(R.id.theQuantity)
        cost.setFilters(arrayOf<InputFilter>(
                DigitsInputsFilter(Int.MAX_VALUE, 2, Double.POSITIVE_INFINITY)
            )
        )
    }//onCreate

    fun saveData(view : View) {
        itemsList.add(items.text.toString())
        costList.add(cost.text.toString().toFloat())
        quantityList.add(quantity.text.toString().toInt())
        listTotal += cost.text.toString().toFloat() * quantity.text.toString().toInt()
        arrayAdapter.notifyDataSetChanged()

        sharedPreferences = applicationContext.getSharedPreferences(
            "com.example.contactlist", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("theitems", ObjectSerializer.serialize(itemsList))
            .apply()
        sharedPreferences.edit().putString("thecosts", ObjectSerializer.serialize(costList))
            .apply()
        sharedPreferences.edit().putString("thequantities", ObjectSerializer.serialize(quantityList))
            .apply()
        sharedPreferences.edit().putFloat("totalCost", listTotal.toFloat()).apply()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }//saveData
}//AddActivity