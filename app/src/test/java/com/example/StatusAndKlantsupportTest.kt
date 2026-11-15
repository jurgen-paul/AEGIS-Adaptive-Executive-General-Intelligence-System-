package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.StatusAndKlantsupportComponent
import com.example.ui.theme.AegisTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StatusAndKlantsupportTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `status and klantsupport string resources and bundled assets exist`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val statusUrl = context.getString(R.string.status_page_url)
        val supportUrl = context.getString(R.string.klantsupport_page_url)
        val supportEmail = context.getString(R.string.support_email)

        assertTrue(statusUrl.endsWith("/status.html"))
        assertTrue(supportUrl.endsWith("/klantsupport.html"))
        assertTrue(supportEmail.contains("@"))

        val statusHtml = context.assets.open("status.html").bufferedReader().use { it.readText() }
        val supportHtml = context.assets.open("klantsupport.html").bufferedReader().use { it.readText() }
        val indexHtml = context.assets.open("index.html").bufferedReader().use { it.readText() }

        assertTrue(statusHtml.contains("AEGIS System Status"))
        assertTrue(statusHtml.contains("klantsupport.html"))
        assertTrue(supportHtml.contains("AEGIS Klantsupport"))
        assertTrue(supportHtml.contains("status.html"))
        assertTrue(supportHtml.contains(supportEmail))
        assertTrue(indexHtml.contains("status.html"))
        assertTrue(indexHtml.contains("klantsupport.html"))
    }

    @Test
    fun `StatusAndKlantsupportComponent renders controls and opens in-app portal viewer`() {
        composeTestRule.setContent {
            AegisTheme {
                StatusAndKlantsupportComponent(
                    securityMode = "strict",
                    activeTaskCount = 4
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("status_klantsupport_card").assertExists()
        composeTestRule.onNodeWithTag("open_status_page_btn").assertExists()
        composeTestRule.onNodeWithTag("open_klantsupport_page_btn").assertExists()
        composeTestRule.onNodeWithTag("copy_status_url_btn").assertExists()
        composeTestRule.onNodeWithTag("copy_klantsupport_url_btn").assertExists()
        composeTestRule.onNodeWithTag("email_klantsupport_btn").assertExists()

        // Clicking View Status Page opens the portal dialog
        composeTestRule.onNodeWithTag("open_status_page_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("portal_webview_dialog").assertExists()
    }
}
