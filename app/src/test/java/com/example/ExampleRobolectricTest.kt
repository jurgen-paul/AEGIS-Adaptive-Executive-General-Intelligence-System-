package com.example

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.AegisLockScreen
import com.example.ui.theme.AegisTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AEGIS", appName)
  }

  @Test
  fun `aegis_shield_bg drawable resolves and decodes cleanly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val drawable = ContextCompat.getDrawable(context, R.drawable.aegis_shield_bg)
    assertNotNull(drawable)

    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.aegis_shield_bg)
    assertNotNull(bitmap)
    assertTrue(bitmap.width > 0 && bitmap.height > 0)
  }

  @Test
  fun `aegis lock screen renders without ResourceResolutionException`() {
    composeTestRule.setContent {
      AegisTheme {
        AegisLockScreen(onUnlocked = {})
      }
    }
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("aegis_lock_screen").assertExists()
  }
}
