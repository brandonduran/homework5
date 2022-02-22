package com.example.contactlist

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Spinner

class EditorActivity : AppCompatActivity() {
    lateinit var items : EditText
    lateinit var cost : EditText
    lateinit var quantity : EditText
    var loc = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)
        items = findViewById(R.id.items)
        cost = findViewById(R.id.costs)
        quantity = findViewById(R.id.quantities)
        loc = intent.getIntExtra("Loc", -1)
        items.setText( itemsList[loc])
        cost.setText( costList[loc].toString() )
        quantity.setText( quantityList[loc])
    }//onCreate

    fun saveData(view : View) {
        itemsList[loc] = items.text.toString()
        costList[loc] = cost.text.toString().toFloat()
        quantityList[loc] = quantity.text.toString().toInt()
        arrayAdapter.notifyDataSetChanged();

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
}//EditorActivity