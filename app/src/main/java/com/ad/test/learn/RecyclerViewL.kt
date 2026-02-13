package com.ad.test.learn

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ad.test.R
import com.ad.test.ui.theme.TestTheme
import kotlin.random.Random

class RecyclerViewAc : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                Scaffold { paddingValues ->
                    val data = Bank().generateTransactions()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    ) {
                        var position by remember { mutableStateOf("") }
                        TextField(
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton({
                                    recyclerView.layoutManager!!.startSmoothScroll(object :
                                        LinearSmoothScroller(this@RecyclerViewAc) {

                                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float =
                                            super.calculateSpeedPerPixel(displayMetrics) / 2

                                        override fun getVerticalSnapPreference(): Int =
                                            SNAP_TO_END

                                    }.also { it.targetPosition = position.toIntOrNull() ?: 0 })
                                }) {
                                    Icon(painterResource(R.drawable.outline_wand_stars_24), null)
                                }
                            },
                            value = position,
                            onValueChange = { position = it },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        AndroidView(
                            factory = {
                                RecyclerView(it).apply {
                                    recyclerView = this
                                    addItemDecoration(
                                        DividerItemDecoration(
                                            this@RecyclerViewAc,
                                            DividerItemDecoration.VERTICAL
                                        ).apply {
                                            setDrawable(
                                                AppCompatResources.getDrawable(
                                                    this@RecyclerViewAc,
                                                    R.drawable.divider
                                                )!!
                                            )
                                        }
                                    )
                                    (itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                                        false
                                    layoutManager = LinearLayoutManager(this@RecyclerViewAc)
                                    adapter = TransactionsAdapter(data) { transaction ->
                                        Toast.makeText(
                                            this@RecyclerViewAc,
                                            transaction.receiver,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

data class Transaction(
    var receiver: String,
    var account: String,
    var amount: String,
    var status: String
)

class TransactionsAdapter(
    private val transactions: List<Transaction>,
    private val onTransactionClick: (Transaction) -> Unit,
) :
    ListAdapter<Transaction, TransactionsAdapter.TransactionViewHolder>(
        object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(
                oldItem: Transaction, newItem: Transaction,
            ) =
                oldItem.account == newItem.account &&
                        oldItem.receiver == newItem.receiver &&
                        oldItem.amount == newItem.amount

            override fun areContentsTheSame(
                oldItem: Transaction, newItem: Transaction,
            ) = (oldItem == newItem)
        }) {

    init {
        submitList(transactions)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int,
    ): TransactionViewHolder =
        TransactionViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaction, parent, false)
        ).apply {
            itemView.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos >= 0) {
                    onTransactionClick(transactions[pos])
                }
            }
        }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val t = getItem(position)
        holder.receiver?.text = t.receiver
        holder.account?.text = t.account
        holder.amount?.text = t.amount
        holder.status?.text = t.status
        if (position == 150) {
            holder.itemView.setBackgroundColor(android.graphics.Color.RED)
        }
    }

    fun add(transaction: Transaction) {
        submitList(currentList + transaction)
    }

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val receiver = view.findViewById<TextView?>(R.id.receiver_tv)
        val account = view.findViewById<TextView?>(R.id.account_tv)
        val amount = view.findViewById<TextView?>(R.id.amount_tv)
        val status = view.findViewById<TextView?>(R.id.status_tv)
    }
}

class Bank {

    private val receivers = listOf(
        "FreshBurgers",
        "NewPost Delivery",
        "GameStore",
        "Hyperskill",
        "NearbyGroceries",
        "MyCellularProvider",
        "Coffee Home"
    )
    private val accounts = listOf("Debit card", "Credit card")
    private val status = listOf("Successful", "Failed")
    private val random = Random

    fun generateTransactions(): ArrayList<Transaction> {
        val list = ArrayList<Transaction>()
        repeat(200) {
            list.add(
                Transaction(
                    getRandomReceiver(),
                    getRandomAccount(),
                    getRandomTransactionAmount(),
                    getRandomStatus()
                )
            )
        }
        return list
    }

    private fun getRandomReceiver(): String {
        return receivers.random()
    }

    private fun getRandomAccount(): String {
        return accounts.random()
    }

    private fun getRandomTransactionAmount(): String {
        return "$${random.nextInt(1, 100)}.00"
    }

    private fun getRandomStatus(): String {
        return status.random()
    }
}