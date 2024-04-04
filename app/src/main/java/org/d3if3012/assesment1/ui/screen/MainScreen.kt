package org.d3if3012.mobpro1.ui.screen

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.d3if3012.assesment1.R
import org.d3if3012.mobpro1.ui.theme.Mobpro1Theme
import org.d3if3012.navigation.Screen
import org.d3if3012.assesment1.MainActivity
import java.util.Locale

sealed class Option(val text: String, val icon: Int, val price: Int) {
    object Option1 : Option("NMAX", R.drawable.nmax, 2708)
    object Option2 : Option("ZX25R", R.drawable.zx25r, 6292)
    object Option3 : Option("CBR25", R.drawable.cbr25, 4907)

    companion object {
        fun values(): List<Option> = listOf(Option1, Option2, Option3)
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current

    // Fungsi untuk mengubah bahasa aplikasi
    fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val configuration = Configuration().apply {
            setLocale(locale)
        }
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)

        // Restart activity agar perubahan bahasa diterapkan
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

        // Apply new configuration to the current activity
        context.startActivity(intent)
        (context as Activity).finish()
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFFB7950B), // Ganti dengan warna yang diinginkan untuk container
                    titleContentColor = Color(0xFF1A5276), // Ganti dengan warna yang diinginkan untuk teks judul
                ),
                actions = {
                    // Tombol untuk mengubah bahasa menjadi Bahasa Indonesia
                    IconButton(
                        onClick = { changeLanguage("id") }) {
                        Text(text = "ID")
                    }
                    // Tombol untuk mengubah bahasa menjadi Bahasa Inggris
                    IconButton(
                        onClick = { changeLanguage("en") }) {
                        Text(text = "EN")
                    }
                    IconButton(
                        onClick = { navController.navigate(Screen.About.route) }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.tentang_aplikasi),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }

    ) { padding ->
        ScreenContent(Modifier.padding(padding))
    }
}


@Composable
fun ScreenContent(modifier: Modifier) {
    var selectedOption by remember { mutableStateOf<Option?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var totalPrice by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current

    val shareData = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    // State untuk menandai apakah inputan nama kosong
    var isNameEmpty by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.title),
            style = MaterialTheme.typography.headlineSmall
        )

        Column(
            modifier = Modifier.selectableGroup()
        ) {
            Option.values().forEach { option: Option ->
                Row(
                    modifier = Modifier.selectable(
                        selected = (selectedOption == option),
                        onClick = { selectedOption = option }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = option.icon),
                        contentDescription = option.text,
                        modifier = Modifier.size(70.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = option.text,
                        modifier = Modifier.weight(1f)
                    )
                    RadioButton(
                        selected = (selectedOption == option),
                        onClick = { selectedOption = option },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Magenta,
                            unselectedColor = Color.Gray
                        )
                    )
                }
            }
        }

        selectedOption?.let {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    // Set isNameEmpty menjadi true jika inputan nama kosong
                    isNameEmpty = it.isEmpty()
                },
                label = { Text("Nama") },
                modifier = Modifier.padding(top = 16.dp)
            )

            // Menampilkan peringatan jika nama kosong
            ErrorHint(isNameEmpty)

            OutlinedTextField(
                value = quantity.toString(),
                onValueChange = { input ->
                    quantity = input.toIntOrNull()?.coerceAtLeast(1) ?: 1
                },
                label = { Text("Jumlah") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { /* Tindakan setelah pengguna selesai memasukkan jumlah */ }),
            )

            Button(
                onClick = {
                    // Memeriksa jika nama kosong
                    if (name.isNotEmpty()) {
                        val price = it.price * quantity
                        totalPrice = price
                    } else {
                        // Jika nama kosong, set isNameEmpty menjadi true
                        isNameEmpty = true
                    }
                },
            ) {
                Text(text = "Beli")
            }
        }

        if (totalPrice > 0) {
            Column(modifier = Modifier
                .padding(top =8.dp)
                .fillMaxWidth(), // Mengisi lebar maksimum agar tombol berada di tengah
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Jenis: ${selectedOption?.text}")
                Text(text = "Nama: $name")
                Text(text = "Jumlah: $quantity")
                Text(text = "Total Harga: $$totalPrice")
                Button(
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Type: ${selectedOption?.text}\nNama: ${name}\nJumlah: $quantity\nTotal Harga: $totalPrice")
                            type = "text/plain"
                        }
                        shareData.launch(Intent.createChooser(sendIntent, null))
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "Bagikan")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewScreenContent() {
    ScreenContent(modifier = Modifier)
}

@Composable
fun IconPicker(isError: Boolean, unit: String) {
    if (isError){
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null
        )
    }else {
        Text(text = unit)
    }
}

@Composable
fun ErrorHint(isError: Boolean) {
    if (isError){
        Text(text = stringResource(id = R.string.input_invalid))
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun GreetingPreview() {
    Mobpro1Theme {
        MainScreen(rememberNavController())
    }
}