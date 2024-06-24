package com.example.diabetapp

import android.content.res.AssetManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class SimulasiModel : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private val mModelPath = "diabet4.tflite"

    private lateinit var resultText: TextView
    private lateinit var edtPregnancies: EditText
    private lateinit var edtGlucose: EditText
    private lateinit var edtBloodPressure: EditText
    private lateinit var edtSkinThickness: EditText
    private lateinit var edtInsulin: EditText
    private lateinit var edtBMI: EditText
    private lateinit var edtDiabetesPedigreeFunction: EditText
    private lateinit var edtAge: EditText
    private lateinit var checkButton: Button
    private lateinit var btnBack: ImageView

    private lateinit var clearPregnancies: ImageView
    private lateinit var clearGlucose: ImageView
    private lateinit var clearBloodPressure: ImageView
    private lateinit var clearSkinThickness: ImageView
    private lateinit var clearInsulin: ImageView
    private lateinit var clearBMI: ImageView
    private lateinit var clearDiabetesPedigreeFunction: ImageView
    private lateinit var clearAge: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simulasi_model)

        resultText = findViewById(R.id.txtResult)
        edtPregnancies = findViewById(R.id.editPregnancies)
        edtGlucose = findViewById(R.id.editGlucose)
        edtBloodPressure = findViewById(R.id.editBloodPressure)
        edtSkinThickness = findViewById(R.id.editSkinThickness)
        edtInsulin = findViewById(R.id.editInsulin)
        edtBMI = findViewById(R.id.editBMI)
        edtDiabetesPedigreeFunction = findViewById(R.id.editDiabetesPedigreeFunction)
        edtAge = findViewById(R.id.editAge)
        checkButton = findViewById(R.id.btnCheck)
        btnBack = findViewById(R.id.btnBack)

        clearPregnancies = findViewById(R.id.clearPregnancies)
        clearGlucose = findViewById(R.id.clearGlucose)
        clearBloodPressure = findViewById(R.id.clearBloodPressure)
        clearSkinThickness = findViewById(R.id.clearSkinThickness)
        clearInsulin = findViewById(R.id.clearInsulin)
        clearBMI = findViewById(R.id.clearBMI)
        clearDiabetesPedigreeFunction = findViewById(R.id.clearDiabetesPedigreeFunction)
        clearAge = findViewById(R.id.clearAge)

        // Initialize TextWatchers
        initTextWatchers()

        // Set onClickListeners for clear icons
        clearPregnancies.setOnClickListener {
            edtPregnancies.text.clear()
            clearPregnancies.visibility = View.INVISIBLE
        }

        clearGlucose.setOnClickListener {
            edtGlucose.text.clear()
            clearGlucose.visibility = View.INVISIBLE
        }

        clearBloodPressure.setOnClickListener {
            edtBloodPressure.text.clear()
            clearBloodPressure.visibility = View.INVISIBLE
        }

        clearSkinThickness.setOnClickListener {
            edtSkinThickness.text.clear()
            clearSkinThickness.visibility = View.INVISIBLE
        }

        clearInsulin.setOnClickListener {
            edtInsulin.text.clear()
            clearInsulin.visibility = View.INVISIBLE
        }

        clearBMI.setOnClickListener {
            edtBMI.text.clear()
            clearBMI.visibility = View.INVISIBLE
        }

        clearDiabetesPedigreeFunction.setOnClickListener {
            edtDiabetesPedigreeFunction.text.clear()
            clearDiabetesPedigreeFunction.visibility = View.INVISIBLE
        }

        clearAge.setOnClickListener {
            edtAge.text.clear()
            clearAge.visibility = View.INVISIBLE
        }

        checkButton.setOnClickListener {
            if (validateInputs()) {
                try {
                    val result = doInference(
                        edtPregnancies.text.toString().toFloat(),
                        edtGlucose.text.toString().toFloat(),
                        edtBloodPressure.text.toString().toFloat(),
                        edtSkinThickness.text.toString().toFloat(),
                        edtInsulin.text.toString().toFloat(),
                        edtBMI.text.toString().toFloat(),
                        edtDiabetesPedigreeFunction.text.toString().toFloat(),
                        edtAge.text.toString().toFloat()
                    )
                    runOnUiThread {
                        resultText.text = if (result == 0) "Tidak Diabetes" else "Diabetes"
                    }
                } catch (e: Exception) {
                    resultText.text = "Prediksi Gagal: ${e.message}"
                }
            } else {
                showSnackbar("Isi semua kolom input")
            }
        }

        btnBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya atau tutup activity ini
        }

        initInterpreter()
    }

    private fun initTextWatchers() {
        edtPregnancies.addTextChangedListener(createTextWatcher(clearPregnancies, edtPregnancies))
        edtGlucose.addTextChangedListener(createTextWatcher(clearGlucose, edtGlucose))
        edtBloodPressure.addTextChangedListener(createTextWatcher(clearBloodPressure, edtBloodPressure))
        edtSkinThickness.addTextChangedListener(createTextWatcher(clearSkinThickness, edtSkinThickness))
        edtInsulin.addTextChangedListener(createTextWatcher(clearInsulin, edtInsulin))
        edtBMI.addTextChangedListener(createTextWatcher(clearBMI, edtBMI))
        edtDiabetesPedigreeFunction.addTextChangedListener(createTextWatcher(clearDiabetesPedigreeFunction, edtDiabetesPedigreeFunction))
        edtAge.addTextChangedListener(createTextWatcher(clearAge, edtAge))
    }

    private fun createTextWatcher(clearIcon: ImageView, editText: EditText): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun validateInputs(): Boolean {
        return edtPregnancies.text.isNotEmpty() &&
                edtGlucose.text.isNotEmpty() &&
                edtBloodPressure.text.isNotEmpty() &&
                edtSkinThickness.text.isNotEmpty() &&
                edtInsulin.text.isNotEmpty() &&
                edtBMI.text.isNotEmpty() &&
                edtDiabetesPedigreeFunction.text.isNotEmpty() &&
                edtAge.text.isNotEmpty()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
    }

    private fun initInterpreter() {
        val options = Interpreter.Options()
        options.setNumThreads(5)
        options.setUseNNAPI(true)
        interpreter = Interpreter(loadModelFile(assets, mModelPath), options)
    }

    private fun doInference(
        pregnancies: Float, glucose: Float, bloodPressure: Float, skinThickness: Float,
        insulin: Float, bmi: Float, diabetesPedigreeFunction: Float, age: Float
    ): Int {
        val inputVal = floatArrayOf(
            pregnancies, glucose, bloodPressure, skinThickness,
            insulin, bmi, diabetesPedigreeFunction, age
        )
        val output = Array(1) { FloatArray(2) }
        interpreter.run(arrayOf(inputVal), output)
        Log.e("result", output[0].contentToString())
        return output[0].indexOfFirst { it == output[0].maxOrNull() }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
