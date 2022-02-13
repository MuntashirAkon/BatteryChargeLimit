// SPDX-License-Identifier: GPL-3.0-or-later

package io.github.muntashirakon.BatteryChargeLimiter.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.github.muntashirakon.BatteryChargeLimiter.Constants
import io.github.muntashirakon.BatteryChargeLimiter.R
import io.github.muntashirakon.BatteryChargeLimiter.Utils
import eu.chainfire.libsuperuser.Shell

class ControlBatteryChargeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (Constants.INTENT_CHANGE_LIMIT_ACTION == intent.action) {
            Utils.handleLimitChange(context, intent.extras?.get(Intent.EXTRA_TEXT))
        } else if (intent.action == Constants.INTENT_DISABLE_ACTION) {
            Utils.stopService(context, false)
        } else if (intent.action == Constants.INTENT_TOGGLE_ACTION) {
            val settings = Utils.getSettings(context)
            if (Shell.SU.available()) {
                val enable = !settings.getBoolean(Constants.CHARGE_LIMIT_ENABLED, false)
                settings.edit().putBoolean(Constants.CHARGE_LIMIT_ENABLED, enable).apply()
                if (enable) {
                    Utils.startServiceIfLimitEnabled(context)
                } else {
                    Utils.stopService(context)
                }
            } else {
                Toast.makeText(context, R.string.root_denied, Toast.LENGTH_LONG).show()
            }
        }
    }
}
