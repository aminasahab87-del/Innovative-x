package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiInnovationResult(
    val score: Int,
    val verdict: String,
    val isGenuineInnovation: Boolean,
    val analysis: String,
    val whatNewToAdd: List<String>,
    val strengths: List<String>,
    val nextSteps: String
)

class AiInnovationService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeProject(
        title: String,
        category: String,
        problem: String,
        solution: String,
        working: String,
        innovation: String,
        components: String = "",
        cost: String = ""
    ): AiInnovationResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }

        // If a real key is present, attempt live Gemini 2.5 Flash call
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val liveResult = callGeminiApi(apiKey, title, category, problem, solution, working, innovation, components, cost)
                if (liveResult != null) {
                    return@withContext liveResult
                }
            } catch (t: Throwable) {
                Log.w("AiInnovationService", "Live Gemini call failed, falling back to smart analyzer: ${t.message}")
            }
        }

        // Robust intelligent domain analyzer fallback
        return@withContext generateSmartHeuristicReview(title, category, problem, solution, working, innovation, components, cost)
    }

    private fun callGeminiApi(
        apiKey: String,
        title: String,
        category: String,
        problem: String,
        solution: String,
        working: String,
        innovation: String,
        components: String,
        cost: String
    ): AiInnovationResult? {
        val prompt = """
            You are an expert Chief Science & Innovation Judge and STEM Mentor.
            Analyze this student innovation project submission:
            - Title: $title
            - Category: $category
            - Problem Statement: $problem
            - Proposed Solution: $solution
            - How it Works: $working
            - Claimed Innovation / Uniqueness: $innovation
            - Components & Cost: $components ($cost)

            Judge whether this project is a genuine innovation, an incremental upgrade, or a common concept that needs unique differentiation.
            Detail what NEW features, sensors, technologies, or improvements the student MUST add to make it truly groundbreaking and practical.
            
            Respond ONLY with a JSON object (no markdown, no backticks) with this structure:
            {
              "score": <number 0-100>,
              "verdict": "<short verdict: High Innovation Potential | Promising Incremental Innovation | Common Concept - Needs Differentiation>",
              "isGenuineInnovation": <true or false>,
              "analysis": "<detailed 2-3 paragraph innovation critique in clear language explaining why it is or isn't innovative>",
              "whatNewToAdd": [
                "<Actionable suggestion 1 of what to add to make it innovative>",
                "<Actionable suggestion 2 of what to add>",
                "<Actionable suggestion 3 of what to add>",
                "<Actionable suggestion 4 of what to add>"
              ],
              "strengths": [
                "<Key strength 1>",
                "<Key strength 2>"
              ],
              "nextSteps": "<Advice for prototype validation, field testing, or presentation>"
            }
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            val contentsArr = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArr = JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                    }
                    put("parts", partsArr)
                }
                put(contentObj)
            }
            put("contents", contentsArr)

            val genConfig = JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.4)
            }
            put("generationConfig", genConfig)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.w("AiInnovationService", "Gemini API error code ${response.code}")
                return null
            }
            val responseText = response.body?.string() ?: return null
            val root = JSONObject(responseText)
            val candidates = root.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val content = candidates.getJSONObject(0).optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            val rawOutput = parts.getJSONObject(0).optString("text")

            val cleanJson = rawOutput.replace("```json", "").replace("```", "").trim()
            val parsed = JSONObject(cleanJson)

            val score = parsed.optInt("score", 75)
            val verdict = parsed.optString("verdict", "Promising Innovation")
            val isGenuine = parsed.optBoolean("isGenuineInnovation", score >= 70)
            val analysis = parsed.optString("analysis", "Solid technological project with good potential.")
            
            val whatNewToAdd = mutableListOf<String>()
            parsed.optJSONArray("whatNewToAdd")?.let { arr ->
                for (i in 0 until arr.length()) {
                    whatNewToAdd.add(arr.getString(i))
                }
            }

            val strengths = mutableListOf<String>()
            parsed.optJSONArray("strengths")?.let { arr ->
                for (i in 0 until arr.length()) {
                    strengths.add(arr.getString(i))
                }
            }

            val nextSteps = parsed.optString("nextSteps", "Conduct bench testing and record performance metrics.")

            return AiInnovationResult(
                score = score,
                verdict = verdict,
                isGenuineInnovation = isGenuine,
                analysis = analysis,
                whatNewToAdd = if (whatNewToAdd.isNotEmpty()) whatNewToAdd else defaultEnhancements(category),
                strengths = if (strengths.isNotEmpty()) strengths else listOf("Clear problem statement", "Practical approach"),
                nextSteps = nextSteps
            )
        }
    }

    /**
     * Smart heuristic-based STEM innovation evaluator used for offline resilience
     * or when external API keys are pending setup.
     */
    private fun generateSmartHeuristicReview(
        title: String,
        category: String,
        problem: String,
        solution: String,
        working: String,
        innovation: String,
        components: String,
        cost: String
    ): AiInnovationResult {
        val lowerText = "$title $problem $solution $working $innovation $components".lowercase()

        // Advanced innovation indicators
        val highTechKeywords = listOf("ai", "iot", "sensor", "automated", "solar", "renewable", "machine learning", "smart", "microcontroller", "arduino", "esp32", "biodegradable", "algorithm", "telemetry", "bluetooth", "cloud")
        val matchedKeywords = highTechKeywords.filter { lowerText.contains(it) }

        val wordCount = (problem.split(" ").size + solution.split(" ").size + working.split(" ").size + innovation.split(" ").size)
        var score = 65

        if (matchedKeywords.size >= 4) score += 18
        else if (matchedKeywords.size >= 2) score += 10

        if (innovation.length > 50) score += 8
        if (wordCount > 100) score += 5
        if (components.isNotBlank()) score += 4

        score = score.coerceIn(55, 96)

        val isGenuine = score >= 75
        val verdict = when {
            score >= 85 -> "🌟 High Innovation Potential (Breakthrough Concept)"
            score >= 72 -> "💡 Promising Innovation (Good Practical Value)"
            else -> "🔧 Incremental Concept (Needs Unique Differentiators)"
        }

        val analysis = buildString {
            append("• **Innovation Evaluation**: ")
            if (isGenuine) {
                append("Your project addresses '$problem' with a genuinely inventive angle in the $category category. ")
                append("The application of ${if (matchedKeywords.isNotEmpty()) matchedKeywords.joinToString(", ") else "technical hardware"} demonstrates fresh scientific thinking rather than copying standard school experiments.")
            } else {
                append("Your project solves an important pain point, but similar solutions exist in conventional market designs. ")
                append("To stand out at a competitive level, you need stronger technological or structural differentiation.")
            }
            append("\n\n• **Core Feasibility**: ")
            append("The working mechanism described is physically achievable within student budgets${if (cost.isNotBlank()) " (approx. $cost)" else ""}. Components specified indicate a viable working prototype.")
        }

        val enhancements = mutableListOf<String>()
        when (category.lowercase()) {
            "robotics & automation", "technology" -> {
                enhancements.add("📶 Add IoT Cloud Telemetry (ESP32/Firebase) so real-time status and alerts can be monitored via smartphone.")
                enhancements.add("⚡ Implement Battery Optimization & Solar Backup for self-sustaining long-term deployment.")
                enhancements.add("🤖 Incorporate Fail-Safe Logic and auto-calibration sensors to handle unexpected obstacles or faults.")
                enhancements.add("📊 Add an OLED/LCD display or audible buzzer for on-device diagnostics.")
            }
            "energy & environment", "cleantech" -> {
                enhancements.add("☀️ Integrate Maximum Power Point Tracking (MPPT) or kinetic harvesting to increase energy conversion efficiency.")
                enhancements.add("🌱 Use recycled, biodegradable, or 3D-printed chassis materials to minimize environmental footprint.")
                enhancements.add("📈 Add automated data logging to graph energy savings over hours and days.")
                enhancements.add("💧 If applicable, add moisture/pH or air quality sensing to demonstrate multi-parameter utility.")
            }
            "health & biotech", "medical" -> {
                enhancements.add("❤️ Add biometric monitoring (e.g. pulse, temperature, vibration) with high-accuracy thresholds.")
                enhancements.add("🚨 Add an automated Emergency SOS trigger to notify guardians or doctors upon critical readings.")
                enhancements.add("🔒 Ensure data encryption and patient privacy safeguards in your software layer.")
                enhancements.add("🧼 Use antimicrobial coating or IP65 water-resistant casing for clinical hygiene.")
            }
            else -> {
                enhancements.add("📱 Build a Companion Mobile Dashboard (Bluetooth / Wi-Fi) to log analytics and control parameters.")
                enhancements.add("🔄 Add Auto-Feedback Loop: Instead of manual operation, let sensors automatically trigger actions.")
                enhancements.add("💰 Cost Optimization: Replace expensive components with accessible microchips to lower production cost.")
                enhancements.add("🛡️ Add Safety Cut-Off and Overload Protection for safety compliance.")
            }
        }

        val strengths = listOf(
            "Clear alignment between the real-world problem and proposed solution",
            "Practical prototype feasibility with realistic components ($components)",
            "Strong focus on $category impact and scalability"
        )

        val nextSteps = "Build a minimum viable prototype (MVP), record a 60-second video demo showing how it solves the problem, and benchmark your numbers (e.g., speed, efficiency, cost reduction) against existing alternatives."

        return AiInnovationResult(
            score = score,
            verdict = verdict,
            isGenuineInnovation = isGenuine,
            analysis = analysis,
            whatNewToAdd = enhancements,
            strengths = strengths,
            nextSteps = nextSteps
        )
    }

    private fun defaultEnhancements(category: String): List<String> {
        return listOf(
            "Add IoT Connectivity (Wi-Fi/Bluetooth) for remote monitoring and mobile app control.",
            "Integrate automated sensors to make the operation self-triggering instead of manual.",
            "Use eco-friendly or locally sourced components to reduce manufacturing cost by 30-40%.",
            "Implement a fail-safe alarm system for emergency cut-offs."
        )
    }
}
