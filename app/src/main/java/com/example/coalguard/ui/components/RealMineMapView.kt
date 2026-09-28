package com.example.coalguard.ui.components

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.coalguard.data.model.Mine

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RealMineMapView(
    latitude: Double = 23.7923,
    longitude: Double = 86.4253,
    mineName: String = "Govindpur Colliery (BCCL)",
    radiusMeters: Int = 500,
    isSatellite: Boolean = true,
    allMines: List<Mine> = emptyList(),
    modifier: Modifier = Modifier
) {
    // Generate Leaflet.js OpenStreetMap HTML string with real India coalfield pins & geofence polygon
    val htmlContent = remember(latitude, longitude, mineName, radiusMeters, isSatellite, allMines) {
        val markersScript = if (allMines.isNotEmpty()) {
            allMines.joinToString("\n") { m ->
                val lat = m.latitude ?: 23.7923
                val lng = m.longitude ?: 86.4253
                val nameEscaped = m.name.replace("'", "\\'")
                val subEscaped = (m.subsidiary ?: "CIL").replace("'", "\\'")
                val isSelected = m.latitude == latitude && m.longitude == longitude
                val pinColor = if (isSelected) "#0284c7" else "#d97706"

                """
                var marker_$m.id = L.circleMarker([$lat, $lng], {
                    radius: ${if (isSelected) 10 else 7},
                    fillColor: "$pinColor",
                    color: "#ffffff",
                    weight: 2,
                    opacity: 1,
                    fillOpacity: 0.9
                }).addTo(map);
                marker_$m.id.bindPopup("<b>$nameEscaped</b><br>$subEscaped • ${m.type}");
                """.trimIndent()
            }
        } else {
            ""
        }

        val tileUrl = if (isSatellite) {
            "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"
        } else {
            "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        }

        val attribution = if (isSatellite) "Esri World Imagery & DGMS Satellite" else "OpenStreetMap Contributors"

        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body, html, #map {
                    margin: 0;
                    padding: 0;
                    height: 100%;
                    width: 100%;
                    background-color: #070d18;
                }
                .leaflet-popup-content-wrapper {
                    background: #002b49;
                    color: #ffffff;
                    font-family: sans-serif;
                    font-size: 11px;
                    border-radius: 8px;
                    border: 1px solid #d97706;
                }
                .leaflet-popup-tip {
                    background: #002b49;
                }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', {
                    center: [$latitude, $longitude],
                    zoom: 13,
                    zoomControl: false
                });

                L.tileLayer('$tileUrl', {
                    maxZoom: 18,
                    attribution: '$attribution'
                }).addTo(map);

                // Geofence Circle Radius Polygon
                var geofenceCircle = L.circle([$latitude, $longitude], {
                    color: '#d97706',
                    fillColor: '#d97706',
                    fillOpacity: 0.15,
                    weight: 2,
                    dashArray: '5, 5',
                    radius: $radiusMeters
                }).addTo(map);

                // Main Selected Mine Pin
                var mainMarker = L.marker([$latitude, $longitude]).addTo(map);
                mainMarker.bindPopup("<b>${mineName.replace("'", "\\'")}</b><br>Geofence Polygon: ${radiusMeters}m").openPopup();

                $markersScript
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                }
                loadDataWithBaseURL("https://server.arcgisonline.com/", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://server.arcgisonline.com/", htmlContent, "text/html", "UTF-8", null)
        }
    )
}
