package com.example.project6

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Data model
data class NasaApod(
    val title: String,
    val explanation: String,
    val url: String
)

// Retrofit API interface
interface NasaApiService {
    @GET("planetary/apod")
    fun getRandomApod(@Query("api_key") apiKey: String, @Query("count") count: Int): Call<List<NasaApod>>
}

// Adapter for RecyclerView
class ApodAdapter(private val items: List<NasaApod>) : RecyclerView.Adapter<ApodAdapter.ApodViewHolder>() {

    inner class ApodViewHolder(private val view: android.view.View) : RecyclerView.ViewHolder(view) {
        private val imageView: android.widget.ImageView = view.findViewById(R.id.imageView)
        private val tvTitle: android.widget.TextView = view.findViewById(R.id.tvTitle)
        private val tvDescription: android.widget.TextView = view.findViewById(R.id.tvDescription)

        fun bind(item: NasaApod) {
            tvTitle.text = item.title
            tvDescription.text = item.explanation
            Glide.with(view.context)
                .load(item.url)
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ApodViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_apod, parent, false)
        return ApodViewHolder(view)
    }

    override fun onBindViewHolder(holder: ApodViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}

// Main Activity
class MainActivity : AppCompatActivity() {

    private val apiKey = "MRi45BpBaDIc5YEvSPd0EVBYaSan7QZ5s9pwGq8I"

    private val apiService: NasaApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchApodData(recyclerView)
    }

    private fun fetchApodData(recyclerView: RecyclerView) {
        apiService.getRandomApod(apiKey, 15).enqueue(object : Callback<List<NasaApod>> {
            override fun onResponse(call: Call<List<NasaApod>>, response: Response<List<NasaApod>>) {
                if (response.isSuccessful && response.body() != null) {
                    recyclerView.adapter = ApodAdapter(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<NasaApod>>, t: Throwable) {
                // TODO: Show error message or retry option
            }
        })
    }
}
