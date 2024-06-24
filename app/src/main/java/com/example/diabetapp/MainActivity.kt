package com.example.diabetapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    private lateinit var tentangAplikasiCard: CardView
    private lateinit var datasetCard: CardView
    private lateinit var fiturCard: CardView
    private lateinit var modelCard: CardView
    private lateinit var simulasiModelCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tentangAplikasiCard = findViewById(R.id.tentangAplikasiCard)
        datasetCard = findViewById(R.id.datasetCard)
        fiturCard = findViewById(R.id.fiturCard)
        modelCard = findViewById(R.id.modelCard)
        simulasiModelCard = findViewById(R.id.simulasiModelCard)

        tentangAplikasiCard.setOnClickListener {
            val intent = Intent(this, TentangAplikasi::class.java)
            startActivity(intent)
        }

        datasetCard.setOnClickListener {
            val intent = Intent(this, Dataset::class.java)
            startActivity(intent)
        }

        fiturCard.setOnClickListener {
            val intent = Intent(this, Fitur::class.java)
            startActivity(intent)
        }

        modelCard.setOnClickListener {
            val intent = Intent(this, Model::class.java)
            startActivity(intent)
        }

        simulasiModelCard.setOnClickListener {
            val intent = Intent(this, SimulasiModel::class.java)
            startActivity(intent)
        }
    }
}
