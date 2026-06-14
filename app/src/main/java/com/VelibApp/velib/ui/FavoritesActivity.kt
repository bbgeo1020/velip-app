package com.VelibApp.velib.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.VelibApp.velib.R
import com.VelibApp.velib.data.local.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val listView = findViewById<ListView>(R.id.listFavorites)
        val dao = AppDatabase.getInstance(this).favoriteDao()

        lifecycleScope.launch {
            dao.getAll().collectLatest { favorites ->
                val items = if (favorites.isEmpty()) {
                    listOf("Aucun favori enregistré")
                } else {
                    favorites.map { "⭐ ${it.name}\nCapacité : ${it.capacity} places" }
                }
                listView.adapter = ArrayAdapter(
                    this@FavoritesActivity,
                    android.R.layout.simple_list_item_1,
                    items
                )
            }
        }
    }
}