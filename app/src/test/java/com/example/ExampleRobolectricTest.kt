package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.system.HomeRoleHandler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app_name string from context matches VOID Launcher`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VOID Launcher", appName)
  }

  @Test
  fun `home role handler initializes and creates valid intent`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val handler = HomeRoleHandler(context)
    val intent = handler.createRequestDefaultLauncherIntent()
    assertNotNull(intent)
  }

  @Test
  fun `openDefaultLauncherSettings executes without crashing`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val handler = HomeRoleHandler(context)
    val opened = handler.openDefaultLauncherSettings(context)
    // Runs gracefully and returns true or false without throwing
    assertNotNull(opened)
  }
}
