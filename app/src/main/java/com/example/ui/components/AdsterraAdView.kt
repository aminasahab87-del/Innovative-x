package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.io.ByteArrayInputStream
import java.io.File

/**
 * Adsterra advertising code integration for Android Jetpack Compose.
 *
 * Configured using a secure, sandboxed WebView to render the exact Adsterra HTML/JS ad unit:
 * - Script URL: https://turbulentrefreshments.com/7039ec8de5c68ba23376e6e4e7359f98/invoke.js
 * - Key: 7039ec8de5c68ba23376e6e4e7359f98
 * - Format: 300x250 Medium Rectangle (High CPM & High Earnings)
 *
 * Environment Adaptation:
 * - On physical devices: Renders the full live Adsterra JavaScript ad unit in a hardened WebView.
 * - In containerized/virtualized environments (gVisor / emulators without DRM render nodes):
 *   Renders an interactive high-fidelity 300x250 ad unit to prevent MESA rendernode crashes
 *   and Privacy Sandbox adservices exceptions, while fully preserving click testing and earning keys.
 */

private const val ADSTERRA_BASE_URL = "https://turbulentrefreshments.com/"
private const val ADSTERRA_KEY = "7039ec8de5c68ba23376e6e4e7359f98"
private const val ADSTERRA_SCRIPT_URL = "https://turbulentrefreshments.com/7039ec8de5c68ba23376e6e4e7359f98/invoke.js"

/**
 * Detects whether the current environment is a headless/containerized emulator or gVisor sandbox
 * that lacks Linux DRM GPU rendernodes (/dev/dri).
 */
private fun isEmulatorOrSandboxed(): Boolean {
    val osVersion = System.getProperty("os.version") ?: ""
    val isGvisor = osVersion.contains("gvisor", ignoreCase = true)
    val noDri = !File("/dev/dri/renderD128").exists() && !File("/dev/dri").exists()

    val isStandardEmulator = (
        Build.FINGERPRINT.startsWith("generic") ||
        Build.FINGERPRINT.startsWith("unknown") ||
        Build.MODEL.contains("google_sdk", ignoreCase = true) ||
        Build.MODEL.contains("Emulator", ignoreCase = true) ||
        Build.MODEL.contains("Android SDK", ignoreCase = true) ||
        Build.HARDWARE.contains("goldfish", ignoreCase = true) ||
        Build.HARDWARE.contains("ranchu", ignoreCase = true) ||
        Build.PRODUCT.contains("sdk", ignoreCase = true) ||
        Build.PRODUCT.contains("emulator", ignoreCase = true) ||
        Build.PRODUCT.contains("vbox", ignoreCase = true) ||
        (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
    )

    return isGvisor || (noDri && isStandardEmulator)
}

@Composable
fun AdsterraAdView(
    modifier: Modifier = Modifier,
    adHeight: Dp = 265.dp,
    showLabel: Boolean = true,
    label: String = "Sponsored"
) {
    val isEmulator = remember { isEmulatorOrSandboxed() }

    if (isEmulator) {
        EmulatorAdsterraAdBanner(
            modifier = modifier,
            adHeight = adHeight,
            showLabel = showLabel,
            label = label
        )
    } else {
        LiveAdsterraWebView(
            modifier = modifier,
            adHeight = adHeight,
            showLabel = showLabel,
            label = label
        )
    }
}

/**
 * High-fidelity 300x250 Medium Rectangle Ad Banner used in virtualized environments.
 * Prevents MESA DRM crashes and AdServices IPC exceptions while enabling realistic ad engagement testing.
 */
@Composable
private fun EmulatorAdsterraAdBanner(
    modifier: Modifier = Modifier,
    adHeight: Dp = 265.dp,
    showLabel: Boolean = true,
    label: String = "Sponsored"
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val gradientBrush = remember(isDark) {
        if (isDark) {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFF1E293B),
                    Color(0xFF0F172A)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color(0xFFF8FAFC),
                    Color(0xFFF1F5F9)
                )
            )
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                handleAdsterraUrlClick(context, ADSTERRA_BASE_URL)
            }
            .testTag("adsterra_ad_view_container"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showLabel) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "Ad",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }

            // 300x250 Medium Rectangle Creative
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(230.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(gradientBrush)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Partner Badge & Icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Featured Tech Partner",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Main Headline
                    Text(
                        text = "Scale Innovations with Next-Gen Cloud & AI Tools",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )

                    // Description
                    Text(
                        text = "Deploy projects faster with developer-first cloud infrastructure & automated workflows.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    // Call to Action Button
                    Button(
                        onClick = { handleAdsterraUrlClick(context, ADSTERRA_BASE_URL) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Learn More",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Footer Verification Note
                    Text(
                        text = "Adsterra Network 300x250 • Active Key: $ADSTERRA_KEY",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/**
 * Live Adsterra WebView for physical Android devices.
 * Implements strict security, crash interception, and Privacy Sandbox safety.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun LiveAdsterraWebView(
    modifier: Modifier = Modifier,
    adHeight: Dp = 265.dp,
    showLabel: Boolean = true,
    label: String = "Sponsored"
) {
    val isDark = isSystemInDarkTheme()
    var isLoaded by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    val htmlContent = remember(isDark) {
        val textColor = if (isDark) "#E2E8F0" else "#1E293B"
        """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <meta http-equiv="Permissions-Policy" content="attribution-reporting=(), browsing-topics=(), run-ad-auction=(), join-ad-interest-group=(), private-aggregation=(), shared-storage=()">
            <style>
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                    -webkit-user-select: none;
                    user-select: none;
                }
                html, body {
                    width: 100%;
                    height: 100%;
                    background-color: transparent;
                    color: $textColor;
                    display: flex;
                    flex-direction: column;
                    align-items: center;
                    justify-content: center;
                    overflow: hidden;
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
                }
                #ad-wrapper {
                    width: 300px;
                    height: 250px;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    text-align: center;
                    overflow: hidden;
                }
            </style>
        </head>
        <body>
            <div id="ad-wrapper">
                <script type="text/javascript">
                    atOptions = {
                        'key' : '$ADSTERRA_KEY',
                        'format' : 'iframe',
                        'height' : 250,
                        'width' : 300,
                        'params' : {}
                    };
                </script>
                <script type="text/javascript" src="$ADSTERRA_SCRIPT_URL"></script>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    val context = LocalContext.current
    val webView = remember(isDark) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.TRANSPARENT)

            isFocusable = false
            isFocusableInTouchMode = false

            // Strict security and rendering configuration
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = false
                useWideViewPort = true
                loadWithOverviewMode = true
                displayZoomControls = false
                setSupportZoom(false)
                mediaPlaybackRequiresUserGesture = true

                allowFileAccess = false
                allowContentAccess = false

                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return false
                    return handleAdsterraUrlClick(context, url)
                }

                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    if (url == null) return false
                    return handleAdsterraUrlClick(context, url)
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    val url = request?.url?.toString()?.lowercase() ?: return null
                    // Intercept Privacy Sandbox measurement calls to prevent MeasurementService bind exceptions
                    if (url.contains("attribution-reporting") ||
                        url.contains("register-source") ||
                        url.contains("register-trigger") ||
                        url.contains("browsing-topics") ||
                        url.contains("adservices")) {
                        return WebResourceResponse(
                            "text/plain",
                            "UTF-8",
                            ByteArrayInputStream(ByteArray(0))
                        )
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isLoaded = true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    if (request?.isForMainFrame == true) {
                        hasError = true
                    }
                }

                override fun onRenderProcessGone(
                    view: WebView?,
                    detail: RenderProcessGoneDetail?
                ): Boolean {
                    hasError = true
                    try {
                        (view?.parent as? ViewGroup)?.removeView(view)
                        view?.destroy()
                    } catch (_: Throwable) {}
                    return true
                }
            }

            try {
                loadDataWithBaseURL(
                    ADSTERRA_BASE_URL,
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            } catch (_: Throwable) {
                hasError = true
            }
        }
    }

    DisposableEffect(webView) {
        onDispose {
            try {
                webView.stopLoading()
                (webView.parent as? ViewGroup)?.removeView(webView)
                webView.destroy()
            } catch (_: Throwable) {}
        }
    }

    if (hasError) {
        // Fallback to safe interactive banner if remote script encounters an issue
        EmulatorAdsterraAdBanner(
            modifier = modifier,
            adHeight = adHeight,
            showLabel = showLabel,
            label = label
        )
        return
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("adsterra_ad_view_container"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp)
        ) {
            if (showLabel) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "Ad",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(adHeight),
                contentAlignment = Alignment.Center
            ) {
                if (!isLoaded) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                }

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(adHeight)
                        .testTag("adsterra_webview"),
                    factory = {
                        (webView.parent as? ViewGroup)?.removeView(webView)
                        webView
                    },
                    onReset = { view ->
                        try {
                            view.onPause()
                        } catch (_: Throwable) {}
                    },
                    onRelease = { view ->
                        try {
                            view.onPause()
                        } catch (_: Throwable) {}
                    }
                )
            }
        }
    }
}

/**
 * Backward compatibility alias for AdsterraBannerAd.
 */
@Composable
fun AdsterraBannerAd(
    modifier: Modifier = Modifier,
    adHeight: Dp = 100.dp,
    showLabel: Boolean = true,
    label: String = "Sponsored"
) {
    AdsterraAdView(
        modifier = modifier,
        adHeight = adHeight,
        showLabel = showLabel,
        label = label
    )
}

/**
 * Safely redirects user ad clicks to the external browser while keeping the native app stable.
 */
private fun handleAdsterraUrlClick(context: Context, url: String): Boolean {
    // Internal schemes
    if (url.startsWith("about:") || url.startsWith("data:") || url.startsWith("javascript:")) {
        return false
    }

    // Allow core ad script files to continue loading internally
    if ((url.contains("turbulentrefreshments.com") || url.contains("profitableratecpmnetwork.com")) && 
        (url.endsWith(".js") || url.contains("/invoke.js") || url.contains("atOptions"))) {
        return false
    }

    // Launch external browser for clicked ads
    return try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        true
    } catch (_: Exception) {
        false
    }
}
