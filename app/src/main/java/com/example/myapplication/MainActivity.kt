package com.example.myapplication

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
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.geeksville.mesh.IMeshService

class MainActivity : ComponentActivity() {
    private val meshNodeChangeReceiver = MeshNodeChangeReceiver()

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
        val filter = IntentFilter("com.geeksville.mesh.NODE_CHANGE")
        registerReceiver(meshNodeChangeReceiver, filter)

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