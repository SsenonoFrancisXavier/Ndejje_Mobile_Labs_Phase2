package com.ndejje.momocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ndejje.momocalc.ui.theme.MoMoCalculatorAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme (typography = MoMoTypography) {
                Surface (modifier = Modifier.fillMaxSize())
                {
                    MoMoCalcScreen()
                }
            }
        }
    }
}

@Composable
fun HoistedAmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    isError: Boolean = false
) {
    Column {
        TextField(
            value = amount,
            onValueChange = onAmountChange,
            isError = isError,
            label = {
                Text(stringResource(R.string.enter_amount))
            },
            supportingText = {
                if (isError) {
                    Text(
                        text = stringResource(R.string.error_numbers_only),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
    }
}

@Composable
fun MoMoCalcScreen() {
    var amountInput by remember { mutableStateOf("") }
    var isCalculating by remember { mutableStateOf(false) } //Track loading state
    var showResult by remember { mutableStateOf(false) } //Track result to show fee

    val numericAmount = amountInput.toDoubleOrNull() ?: 0.0
    val isError =amountInput.isNotEmpty() && amountInput.toDoubleOrNull() == null

    //Calculate fee based on the selected network. in this case MTN
    val fee = calculateMtnFee(numericAmount)

    //Format fee for display
    val formattedFee = "UGX %,.0f" .format(fee)

    //LaunchedEffect reacts to every change in amountInput
    LaunchedEffect(amountInput) {
        if (amountInput.isNotEmpty() && !isError){
            showResult = false          // Hide old result while typing
            delay(1000)      // Wait for 1 second of "no typing"
            isCalculating = true        // Start simulation
            delay(500)       // Simulate network latency
            isCalculating = false       // End simulation
            showResult = true           // Show final result
        } else {
            showResult = false
            isCalculating = false
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.padding(16.dp))
        HoistedAmountInput(
            amount = amountInput,
            onAmountChange = { amountInput = it },
            isError = isError
        )
        Spacer(modifier = Modifier.padding(12.dp))

        //UI Logic based on the simulation state
        when {
            isCalculating -> {
                Text(text = "Applying tiered rates...", style = MaterialTheme.typography.bodyLarge)
            }
            showResult && !isError -> {
                Text(
                    text = stringResource(R.string.fee_label, formattedFee),
                    style = MaterialTheme.typography.bodyLarge
                )
                val rateLabel = if (numericAmount < 250000) "3% rate applied" else "1.5% rate applied"
                Text(
                    text = rateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )

            }
        }
    }
}

@Composable
fun calculateMtnFee(amount: Double): Double {
    return if (amount < 2500000) {
        //UGX 0 – 2,499,999 - 3%
        amount * 0.03
    } else {
        //UGX 2,500,000 and above - 1.5%
        amount * 0.015
    }
}

@Preview(showBackground = true)
@Composable
fun MoMoCalcPreview() {
    MaterialTheme {
        MoMoCalcScreen()
    }
}



