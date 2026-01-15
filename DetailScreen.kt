package com.example.kotlinshowcaseapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Composable funkce pro obrazovku úpravy profilu.
@Composable
fun DetailScreen(
    navController: NavController,
    currentName: String, // Aktuální jméno předané z předchozí obrazovky
    currentAge: String   // Aktuální věk předaný z předchozí obrazovky
) {
    // OPRAVA PÁDU: Pokud je předaný věk řetězec "null" (což se stane, když je původní hodnota null),
    // použijeme prázdný řetězec, aby se v textovém poli nezobrazoval text "null".
    val displayAge = if (currentAge == "null") "" else currentAge

    // `remember` a `mutableStateOf` si pamatují stav jména a věku zadaného do textových polí.
    // `by` delegát umožňuje přímý přístup k hodnotě (místo `name.value`).
    var name by remember { mutableStateOf(currentName) }
    var age by remember { mutableStateOf(displayAge) }

    // `Column` uspořádá prvky uživatelského rozhraní vertikálně.
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Upravit profil")

        // `TextField` je Composable pro zadávání textu.
        TextField(
            value = name, // Aktuální hodnota textového pole
            onValueChange = { name = it }, // Lambda, která se zavolá při změně textu
            label = { Text("Jméno") } // Popisek, který se zobrazí, když je pole prázdné
        )

        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Věk") }
        )

        // Tlačítko pro uložení změn.
        Button(onClick = {
            // Uložíme nové hodnoty do `savedStateHandle` předchozí obrazovky.
            // `previousBackStackEntry` nám dává přístup k `ShowcaseScreen` v navigačním zásobníku.
            // Operátor bezpečného volání `?.` zajišťuje, že kód nespadne, pokud by `previousBackStackEntry` byl `null`.
            navController.previousBackStackEntry?.savedStateHandle?.set("newName", name)
            navController.previousBackStackEntry?.savedStateHandle?.set("newAge", age)
            // `popBackStack()` nás vrátí na předchozí obrazovku (`ShowcaseScreen`).
            navController.popBackStack()
        }) {
            Text("Uložit")
        }
    }
}