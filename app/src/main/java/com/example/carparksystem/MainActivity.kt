package com.example.carparksystem

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.example.carparksystem.network.api.ApiService
import com.example.carparksystem.network.model.response.CarparkApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {
    private val apiService = Retrofit.Builder()
        .baseUrl("https://api.data.gov.sg/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    private val smallLot: HashMap<String, Int> = hashMapOf()
    private val mediumLot: HashMap<String, Int> = hashMapOf()
    private val bigLot: HashMap<String, Int> = hashMapOf()
    private val largeLot: HashMap<String, Int> = hashMapOf()

    private val executor = Executors.newSingleThreadScheduledExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        executor.scheduleAtFixedRate({
            callService()
        }, 0, 60, TimeUnit.SECONDS)
    }

    override fun onDestroy() {
        super.onDestroy()

        executor.shutdown()
    }

    private fun callService() {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

        GlobalScope.launch(Dispatchers.IO) {
            apiService.getCarparkAvailability(formatter.format(currentDateTime)).body()?.let {
                it.items.forEach{ item ->
                    processCarparkData(item.carparkDataList)
                }
            }

            withContext(Dispatchers.Main) {
                displaySmallLot()
                displayMediumLot()
                displayBigLot()
                displayLargeLot()
            }
        }
    }

    private fun processCarparkData(carparkData: List<CarparkApiResponse.CarparkData>) {
        carparkData.forEach { data->
            val totalLots = data.carparkInfoList.sumOf { it.totalLots.toInt() }
            data.carparkInfoList.forEach { info ->
                when {
                    totalLots < 100 -> {
                        if(smallLot.containsKey(data.carparkNumber)) {
                            smallLot[data.carparkNumber]?.plus(info.lotsAvailable.toInt())
                        } else {
                            smallLot[data.carparkNumber] = info.lotsAvailable.toInt()
                        }
                    }
                    totalLots in 100..299 -> {
                        if(mediumLot.containsKey(data.carparkNumber)) {
                            mediumLot[data.carparkNumber]?.plus(info.lotsAvailable.toInt())
                        } else {
                            mediumLot[data.carparkNumber] = info.lotsAvailable.toInt()
                        }
                    }
                    totalLots in 300..399 -> {
                        if(bigLot.containsKey(data.carparkNumber)) {
                            bigLot[data.carparkNumber]?.plus(info.lotsAvailable.toInt())
                        } else {
                            bigLot[data.carparkNumber] = info.lotsAvailable.toInt()
                        }
                    }
                    totalLots > 400 -> {
                        if(largeLot.containsKey(data.carparkNumber)) {
                            largeLot[data.carparkNumber]?.plus(info.lotsAvailable.toInt())
                        } else {
                            largeLot[data.carparkNumber] = info.lotsAvailable.toInt()
                        }
                    }
                }
            }
        }
    }

    private fun getHighest(map: HashMap<String, Int>): Int? {
        return map.values.maxOrNull()
    }

    private fun getLowest(map: HashMap<String, Int>): Int? {
        return map.values.minOrNull()
    }

    private fun getLots(map: HashMap<String, Int>, lotValue: Int?): Set<String>? {
        return lotValue?.let { value ->
            map.filterValues { it == value }.keys
        }
    }

    private fun displaySmallLot(){
        findViewById<TextView>(R.id.highest_small_lot_val).text =
            "HIGHEST (" + getHighest(smallLot) + " lots available)"
        findViewById<TextView>(R.id.highest_small_lot_key).text =
            getLots(smallLot, getHighest(smallLot))?.joinToString(separator = "") { "$it, " }?.dropLast(2)

        findViewById<TextView>(R.id.lowest_small_lot_val).text =
            "LOWEST (" + getLowest(smallLot) + " lots available)"
        findViewById<TextView>(R.id.lowest_small_lot_key).text =
            getLots(smallLot, getLowest(smallLot))?.joinToString(separator = "") { "$it, " }?.dropLast(2)
    }

    private fun displayMediumLot(){
        findViewById<TextView>(R.id.highest_medium_lot_val).text =
            "HIGHEST (" + getHighest(mediumLot) + " lots available)"
        findViewById<TextView>(R.id.highest_medium_lot_key).text =
            getLots(mediumLot, getHighest(mediumLot))?.joinToString(separator = "") { "$it, " }?.dropLast(2)

        findViewById<TextView>(R.id.lowest_medium_lot_val).text =
            "LOWEST (" + getLowest(mediumLot) + " lots available)"
        findViewById<TextView>(R.id.lowest_medium_lot_key).text =
            getLots(mediumLot, getLowest(mediumLot))?.joinToString(separator = "") { "$it, " }?.dropLast(2)
    }

    private fun displayBigLot(){
        findViewById<TextView>(R.id.highest_big_lot_val).text =
            "HIGHEST (" + getHighest(bigLot) + " lots available)"
        findViewById<TextView>(R.id.highest_big_lot_key).text =
            getLots(bigLot, getHighest(bigLot))?.joinToString(separator = "") { "$it, " }?.dropLast(2)

        findViewById<TextView>(R.id.lowest_big_lot_val).text =
            "LOWEST (" + getLowest(bigLot) + " lots available)"
        findViewById<TextView>(R.id.lowest_big_lot_key).text =
            getLots(bigLot, getLowest(bigLot))?.joinToString(separator = "") { "$it, " }?.dropLast(2)
    }

    private fun displayLargeLot(){
        findViewById<TextView>(R.id.highest_large_lot_val).text =
            "HIGHEST (" + getHighest(largeLot) + " lots available)"
        findViewById<TextView>(R.id.highest_large_lot_key).text =
            getLots(largeLot, getHighest(largeLot))?.joinToString(separator = "") { "$it, " }?.dropLast(2)

        findViewById<TextView>(R.id.lowest_large_lot_val).text =
            "LOWEST (" + getLowest(bigLot) + " lots available)"
        findViewById<TextView>(R.id.lowest_large_lot_key).text =
            getLots(largeLot, getLowest(largeLot))?.joinToString(separator = "") { "$it, " }?.dropLast(2)
    }
}