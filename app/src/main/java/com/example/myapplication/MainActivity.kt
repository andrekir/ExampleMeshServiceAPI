package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.geeksville.mesh.DataPacket
import com.geeksville.mesh.IMeshService
import com.geeksville.mesh.NodeInfo

inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(key: String?): T? =
    IntentCompat.getParcelableExtra(this, key, T::class.java)

class MainActivity : ComponentActivity() {
    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "com.geeksville.mesh.NODE_CHANGE" -> {
                    // Perform actions or processing here based on the received broadcast intent
                    // For example, you can extract data from the intent using extras
                    val info: NodeInfo? =
                        intent.getParcelableExtraCompat("com.geeksville.mesh.NodeInfo")
                    println("received $info")
                }

                "com.geeksville.mesh.RECEIVED.TEXT_MESSAGE_APP" -> {
                    val payload: DataPacket? =
                        intent.getParcelableExtraCompat("com.geeksville.mesh.Payload")
                    println("received $payload")
                }
            }
        }
    }

    // Service connection to handle the communication with the service
    private var meshService: IMeshService? = null
    private var isServiceBound = false

    // Define the service connection
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // Service is connected. Get the binder and cast it to our service
            meshService = IMeshService.Stub.asInterface(service)
            isServiceBound = true
            // You can now call methods on the MeshService instance if needed
            // ie. meshService.send(datapacket)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Service disconnected, clean up any references
            isServiceBound = false
            meshService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter().apply {
            addAction("com.geeksville.mesh.NODE_CHANGE")
            addAction("com.geeksville.mesh.RECEIVED.TEXT_MESSAGE_APP")
        }
        ContextCompat.registerReceiver(
            this,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )

        val intent = Intent().apply {
            setClassName(
                "com.geeksville.mesh",
                "com.geeksville.mesh.service.MeshService"
            )
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}