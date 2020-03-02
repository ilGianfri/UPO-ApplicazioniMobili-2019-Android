package it.uniupo.spisso.upo_applicazionimobili.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.uniupo.spisso.upo_applicazionimobili.R

class NoConnectivityActivity : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connectivity)
    }

    override fun onStart()
    {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop()
    {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            val notConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if (!notConnected)
            {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
