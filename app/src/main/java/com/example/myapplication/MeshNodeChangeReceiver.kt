package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.geeksville.mesh.DataPacket
import com.geeksville.mesh.NodeInfo

class MeshNodeChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "com.geeksville.mesh.NODE_CHANGE" -> {
                // Perform actions or processing here based on the received broadcast intent
                // For example, you can extract data from the intent using extras
                val info: NodeInfo? = intent.getParcelableExtra("com.geeksville.mesh.NodeInfo")
                println("received $info")
            }

            "com.geeksville.mesh.RECEIVED.TEXT_MESSAGE_APP" -> {
                val payload: DataPacket? = intent.getParcelableExtra("com.geeksville.mesh.Payload")
                println("received $payload")
            }
        }
    }
}
