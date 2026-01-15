package com.example.kotlinshowcaseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kotlinshowcaseapp.ui.theme.KotlinShowcaseAppTheme

// Hlavní aktivita aplikace, která je vstupním bodem.
class MainActivity : ComponentActivity() {
    // Metoda `onCreate` se volá při vytvoření aktivity.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // `setContent` nastaví obsah aktivity pomocí Jetpack Compose.
        setContent {
            // `KotlinShowcaseAppTheme` aplikuje vizuální téma na celou aplikaci.
            KotlinShowcaseAppTheme {
                // `rememberNavController` vytvoří a zapamatuje si instanci `NavController`,
                // která spravuje navigaci mezi obrazovkami.
                val navController = rememberNavController()
                // `NavHost` je kontejner, který zobrazuje aktuální obrazovku (navigační cíl).
                NavHost(navController = navController, startDestination = "showcase") {
                    // Definuje první obrazovku s cestou "showcase".
                    composable("showcase") { ShowcaseScreen(navController) }
                    // Definuje druhou obrazovku "detail" s parametry pro jméno a věk.
                    composable("detail/{name}/{age}") { backStackEntry ->
                        DetailScreen(
                            navController = navController,
                            // Získáme parametry z navigační cesty.
                            currentName = backStackEntry.arguments?.getString("name") ?: "",
                            currentAge = backStackEntry.arguments?.getString("age") ?: ""
                        )
                    }
                }
            }
        }
    }
}

// `data class` je speciální třída v Kotlinu, která automaticky generuje užitečné metody
// jako `equals()`, `hashCode()`, `toString()` a `copy()`.
data class User(val name: String, val age: Int?)

// `@Composable` označuje funkci, která popisuje část uživatelského rozhraní.
@Composable
fun ShowcaseScreen(navController: NavController) {

    // `remember` a `mutableStateOf` slouží k vytvoření a zapamatování stavu v Composable funkci.
    // Pokaždé, když se hodnota `clickCount` změní, dojde k překreslení komponenty.
    var clickCount by remember { mutableStateOf(0) }

    // Stejným způsobem si pamatujeme i stav objektu `user`.
    var user by remember { mutableStateOf(User(name = "Neznamý", age = null)) }

    // Získáme `savedStateHandle` z `navController` pro přístup k datům vráceným z jiné obrazovky.
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    // `LaunchedEffect` je Composable, který spouští suspend funkci (coroutines), když se změní klíč.
    // Používáme ho ke zpracování výsledků vrácených z `DetailScreen`.
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.let { handle ->
            // Získáme nové hodnoty jména a věku, pokud existují.
            val newName = handle.get<String>("newName")
            val newAge = handle.get<String>("newAge")

            var userWasUpdated = false
            var updatedUser = user

            if (newName != null) {
                // Použijeme metodu `copy()` datové třídy pro vytvoření nové instance s aktualizovaným jménem.
                updatedUser = updatedUser.copy(name = newName)
                // "Spotřebujeme" vrácenou hodnotu jejím odstraněním, aby se neaplikovala znovu.
                handle.remove<String>("newName")
                userWasUpdated = true
            }
            if (newAge != null) {
                updatedUser = updatedUser.copy(age = newAge.toIntOrNull())
                handle.remove<String>("newAge")
                userWasUpdated = true
            }

            // Pokud došlo k aktualizaci, nastavíme nový stav uživatele.
            if (userWasUpdated) {
                user = updatedUser
            }
        }
    }

    // Kotlinovský Elvis operátor (?:) poskytuje výchozí hodnotu, pokud je výraz `null`.
    val ageText = user.age?.toString() ?: "neznámý věk"

    // `Column` uspořádá své podřízené prvky vertikálně pod sebou.
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Textové prvky pro zobrazení informací. Používáme string templates ($) pro vkládání proměnných.
        Text("Ahoj ${user.name}")
        Text("Věk: $ageText")

        Text("Kliknutí: $clickCount")

        // Kotlinovský `when` výraz je mocnější verze `switch` příkazu.
        Text(
            text = when {
                clickCount == 0 -> "Zatím jsi neklikl"
                clickCount < 5 -> "Klikáš málo"
                else -> "Klikáš hodně"
            }
        )

        // Tlačítko, které po kliknutí zvýší `clickCount`. Změna stavu automaticky překreslí UI.
        Button(onClick = { clickCount++ }) {
            Text("Klikni")
        }

        // Tlačítko pro navigaci na obrazovku "uprava profilu"
        // Tím zajistíme, že navigační cesta bude vždy platná.
        Button(onClick = { navController.navigate("detail/${user.name}/${user.age ?: "null"}") }) {
            Text("Upravit profil")
        }
    }
}