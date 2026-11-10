package com.example.aiplanner.data.remote

import com.example.aiplanner.data.remote.dto.GenerateRequest
import com.example.aiplanner.data.remote.dto.GenerateResponse
import com.example.aiplanner.data.remote.dto.ReplanRequest
import com.example.aiplanner.data.remote.dto.ReplanResponse
import retrofit2.http.Body
import retrofit2.http.POST

/** Calls the AI planner proxy server (see /server) — never talks to OpenAI directly. */
interface AiPlannerApi {

    @POST("api/v1/schedule/generate")
    suspend fun generateSchedule(@Body request: GenerateRequest): GenerateResponse

    @POST("api/v1/schedule/replan")
    suspend fun replan(@Body request: ReplanRequest): ReplanResponse
}
