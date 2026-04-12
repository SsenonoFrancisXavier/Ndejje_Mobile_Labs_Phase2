package com.ndejje.momocalc

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoMoCalculatorAppTheme {             // our custom theme (Part B)
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        topBar = { MoMoTopBar() }
                    ) { innerPadding ->
                        MoMoCalcScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoMoTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_momo_logo),
                contentDescription = "MoMo Logo",
                modifier = Modifier
                    .padding(start = dimensionResource(R.dimen.spacing_medium))
                    .height(32.dp)
                    .wrapContentWidth(),
                contentScale = ContentScale.Fit
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun HoistedAmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier //new parameter with safe default
) {
    Column (modifier = modifier) {  //modifier applied to outer column
        TextField(
            value = amount,
            onValueChange = onAmountChange,
            isError = isError,
            modifier = Modifier.fillMaxWidth(), //fills full width of parent
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
fun MoMoCalcScreen(modifier: Modifier = Modifier) {
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

    Column(modifier = Modifier
        .fillMaxSize()  //occupy full screen - centering needs space
        .padding(dimensionResource(R.dimen.screen_padding)),
        verticalArrangement = Arrangement.Center, //vertical in the middle
        horizontalAlignment = Alignment.CenterHorizontally //horizontal centre

    ) {
        Text(
            text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center //centres within its own bounding box
        )
        Spacer(modifier = Modifier.height(
            dimensionResource(R.dimen.spacing_large)
        ))

        HoistedAmountInput(
            amount = amountInput,
            onAmountChange = { amountInput = it },
            isError = isError,
            modifier = Modifier.fillMaxWidth() //input stretches full width
        )
        Spacer(modifier = Modifier.height(
            dimensionResource(R.dimen.spacing_medium)
        ))

        //UI Logic based on the simulation state
        when {
            isCalculating -> {
                Text(text = "Applying tiered rates...", style = MaterialTheme.typography.bodyLarge)
            }
            showResult && !isError -> {
                // Wrapping the fee display in a themed, shaped Surface
                Surface(
                    shape = MaterialTheme.shapes.medium,  // 16dp rounded corners
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.fee_label, formattedFee),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                val rateLabel = if (numericAmount < 250000) "3% rate applied" else "1.5% rate applied"
                Text(
                    text = rateLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
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


@Composable
fun MoMoCalculatorAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // auto-detect by default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = MoMoTypography,
        shapes      = MoMoShapes,
        content     = content
    )
}

@Preview(showBackground = true)

@Preview(name = "Light Mode", showBackground = true)
@Composable
fun PreviewLight() {
    MoMoCalculatorAppTheme (darkTheme = false) {
        MoMoCalcScreen()
    }
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDark() {
    MoMoCalculatorAppTheme(darkTheme = true) {
        MoMoCalcScreen()
    }
}

@Preview(name = "Fee Card – Light", showBackground = true)
@Preview(
    name = "Fee Card – Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewFeeCard() {
    MoMoAppTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            MoMoCalcScreen()
        }
    }
}


@Composable
fun MoMoCalcPreview() {
    MaterialTheme {
        MoMoCalcScreen()
    }
}



