package sv.edu.investigacionaplicada2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import sv.edu.investigacionaplicada2.api.RetrofitInstance
import sv.edu.investigacionaplicada2.models.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var editCity: EditText
    private lateinit var editCountry: EditText
    private lateinit var btnSearch: Button
    private lateinit var textCity: TextView
    private lateinit var textTemp: TextView
    private lateinit var textDescription: TextView
    private lateinit var imageWeather: ImageView

    private val apiKey = "57489722726186ea3201069fd57d252d"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editCity = findViewById(R.id.editCity)
        editCountry = findViewById(R.id.editCountry)
        btnSearch = findViewById(R.id.btnSearch)
        textCity = findViewById(R.id.textCity)
        textTemp = findViewById(R.id.textTemp)
        textDescription = findViewById(R.id.textDescription)
        imageWeather = findViewById(R.id.imageWeather)

        btnSearch.setOnClickListener {
            val cityName = editCity.text.toString().trim()
            val countryCode = editCountry.text.toString().trim().uppercase()

            if (cityName.isEmpty() || countryCode.isEmpty()) {
                Toast.makeText(this, "Ingresa ciudad y código del país", Toast.LENGTH_SHORT).show()
            } else {
                val query = "$cityName,$countryCode"
                getWeatherData(query)
            }
        }
    }

    private fun getWeatherData(city: String) {
        RetrofitInstance.api.getWeather(city, apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    if (weather != null) {
                        textCity.text = weather.name
                        textTemp.text = "${weather.main.temp} °C"
                        textDescription.text = weather.weather[0].description

                        val icon = weather.weather[0].icon
                        val iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png"
                        Glide.with(this@MainActivity).load(iconUrl).into(imageWeather)
                    } else {
                        Toast.makeText(this@MainActivity, "No se encontró información de la ciudad", Toast.LENGTH_SHORT).show()
                        clearWeatherData()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Ciudad no encontrada", Toast.LENGTH_SHORT).show()
                    clearWeatherData()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error de conexión. Verifica tu internet", Toast.LENGTH_SHORT).show()
                clearWeatherData()
            }
        })
    }

    private fun clearWeatherData() {
        textCity.text = ""
        textTemp.text = ""
        textDescription.text = ""
        imageWeather.setImageDrawable(null)
    }
}