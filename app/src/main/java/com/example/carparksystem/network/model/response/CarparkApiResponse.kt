package com.example.carparksystem.network.model.response

import com.google.gson.annotations.SerializedName

object CarparkApiResponse {
    data class CarparkAvailability(val items: List<CarparkItems>)

    data class CarparkItems (@SerializedName("timestamp") val timestamp:String,
                            @SerializedName("carpark_data") val carparkDataList:List<CarparkData>)

    data class CarparkData (@SerializedName("carpark_info") val carparkInfoList:List<CarparkInfo>,
                            @SerializedName("carpark_number") val carparkNumber:String,
                            @SerializedName("update_datetime") val updateDatetime:String)


    data class CarparkInfo (@SerializedName("total_lots") val totalLots:String,
                            @SerializedName("lot_type") val lotType:String,
                            @SerializedName("lots_available") val lotsAvailable:String)
}