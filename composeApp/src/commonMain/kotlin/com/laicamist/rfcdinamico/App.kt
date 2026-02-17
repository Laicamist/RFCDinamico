package com.laicamist.rfcdinamico

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.Text
import rfcdinamico.composeapp.generated.resources.Res
import rfcdinamico.composeapp.generated.resources.compose_multiplatform
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.Key.Companion.Calendar
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import kotlinx.datetime.*
import kotlinx.coroutines.delay
import kotlin.collections.find

@Composable
@Preview
fun App() {
    MaterialTheme {
        val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val birthDateState = rememberDatePickerState(
            initialDisplayMode = DisplayMode.Input,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val date = Instant.fromEpochMilliseconds(utcTimeMillis)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    return date < hoy
                }
                override fun isSelectableYear(año: Int): Boolean = año <= hoy.year
            }
        )

        val dateMillis = birthDateState.selectedDateMillis
        val datoSeleccionable = dateMillis?.let {
            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date
        }
        val dia = datoSeleccionable?.dayOfMonth?.toString()?.padStart(2, '0') ?: "01"
        val mes = datoSeleccionable?.monthNumber?.toString()?.padStart(2, '0') ?: "01"
        val año = datoSeleccionable?.year?.toString() ?: "2000"

        var nombre by remember { mutableStateOf("") }
        var primerApellido by remember { mutableStateOf("") }
        var segundoApellido by remember { mutableStateOf("") }

        var datePickerEnabled by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(400)
            datePickerEnabled = true
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(8.dp, RoundedCornerShape(12.dp)) // Sombra con elevación
                .clip(RoundedCornerShape(12.dp)) // Bordes redondeados
                .background(MaterialTheme.colorScheme.surface) // Fondo
                .border(
                    BorderStroke(16.dp, Color.Black), // Borde
                    RoundedCornerShape(12.dp)
                )
                .padding(20.dp) // Espaciado interno del dibujo
        ) {
            Text("Generador de RFC dinamico", style = MaterialTheme.typography.headlineSmall) // Texto indica el objetivo del programa
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { // Colum para las instrucciones de uso
                Text("Instrucciones:")
                Text("Introduce los datos que se solicitan en pantalla y aprecia la generación de tu RFC")
            }
            // Cuadros de texto
            OutlinedTextField(
                value = nombre,
                onValueChange = { nuevo ->
                    if (nuevo.all { it.isLetter() || it.isWhitespace() || it in "áéíóúÁÉÍÓÚñÑüÜ" }) {
                        nombre = nuevo.trim().take(50)
                    }
                },
                label = { Text("Nombre(s)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = primerApellido,
                onValueChange = { nuevo ->
                    if (nuevo.all { it.isLetter() || it.isWhitespace() || it in "áéíóúÁÉÍÓÚñÑüÜ" }) {
                        primerApellido = nuevo.trim().take(30)
                    }
                },
                label = { Text("Primer Apellido *") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = segundoApellido,
                onValueChange = { nuevo ->
                    if (nuevo.all { it.isLetter() || it.isWhitespace() || it in "áéíóúÁÉÍÓÚñÑüÜ" }) {
                        segundoApellido = nuevo.trim().take(30)
                    }
                },
                label = { Text("Segundo Apellido") },
                modifier = Modifier.fillMaxWidth()
            )

            DatePicker(
                state = birthDateState,
                modifier = Modifier.fillMaxWidth(),
                headline = { Text("Fecha de Nacimiento") }
            )

            // Rfc dinamico
            val rfcGenerado by remember(nombre, primerApellido, segundoApellido, dia, mes, año) {
                derivedStateOf {
                    rfc(nombre, primerApellido, segundoApellido, dia, mes, año)
                }
            }

            Text(
                text = "RFC: $rfcGenerado",
                style = MaterialTheme.typography.headlineMedium,
                color = if (rfcGenerado.length == 13) Color.Blue else Color.Black
            )
        }
    }
}

fun rfc(nombre: String, primerApellido: String, segundoApellido: String, dia : String, mes : String, año : String): String {
    val banString = listOf("BUEI","KAKA","LOCA","jode","puta","coño","geys","homo","fack","fock","shit","poll","host",
        "mier","cabr","ping","verg","culo","teta","pnch","pndj","chng","fuck","dick","cunt","btch","d1ck")
    val nombresComunes = listOf("jose","maria","jesus")

    // Inicializar lista "Vacia"
    val rfcPartes = MutableList(13) { "X" }
    // Primer apellido
    if (primerApellido.isNotBlank()) {
        val apellido1Fragmentado = primerApellido.toList()
        rfcPartes[0] = apellido1Fragmentado[0].uppercaseChar().toString()
        val vocal = apellido1Fragmentado.find { it.lowercaseChar() in "aeiou" } ?: 'X'
        rfcPartes[1] = vocal.uppercaseChar().toString()
    }
    // Segundo apellido
    if (segundoApellido.isNotBlank()) {
        rfcPartes[2] = segundoApellido[0].uppercaseChar().toString()
    }
    // Nombre
    val nombresLimpios = nombre.split(" ").filter { it.isNotBlank() }
    if (nombresLimpios.isNotEmpty()) {
        val nombreRFC = if (nombresLimpios.size == 1) {
            nombresLimpios[0].first().uppercaseChar().toString()
        } else {
            nombresLimpios.lastOrNull { it.lowercase() !in nombresComunes }
                ?.first()?.uppercaseChar()?.toString()
                ?: nombresLimpios[0].first().uppercaseChar().toString()
        }
        rfcPartes[3] = nombreRFC
    }
    // Validar palabras malas
    val primerasLetras = rfcPartes.subList(0, 4).joinToString("")
    if (banString.any { it.equals(primerasLetras, ignoreCase = true) }) {
        rfcPartes[3] = "X"
    }

    // Fecha (posiciones 4 a 9)
    if (año.isNotBlank() && mes.isNotBlank() && dia.isNotBlank()) {
        val fecha = año.takeLast(2) + mes + dia
        for (i in fecha.indices) {
            rfcPartes[4 + i] = fecha[i].toString()
        }
    }

    // Homoclave aleatoria (posiciones 10 a 12)
    val abc = listOf("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
        "0","1","2","3","4","5","6","7","8","9")
    val homoclave = "" + abc.random() + abc.random() + abc.random()
    for (i in homoclave.indices) {
        rfcPartes[10 + i] = homoclave[i].toString()
    }

    return rfcPartes.joinToString("").uppercase()
}
