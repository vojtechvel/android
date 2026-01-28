# Android Vývoj: Emulátor vs. Reálné Zařízení

> **Téma:** Klíčové rozdíly mezi simulací aplikace v Android Studiu (AVD) a reálným provozem na fyzickém mobilu.

---

## Úvod
Při vývoji Android aplikací máme dvě hlavní možnosti, jak kód testovat:
1.  **Android Virtual Device (AVD)** – Emulátor běžící na PC.
2.  **Fyzické zařízení** – Reálný telefon nebo tablet připojený přes USB/WiFi.

Ačkoliv je emulátor podobný provozu telefonu, **není to dokonalá kopie reality**.

---

## Srovnávací Tabulka

| Vlastnost | Emulátor (Android Studio) | Reálné Zařízení (Mobil) |
| :--- | :--- | :--- |
| **Architektura** | Často **x64** (překlad instrukcí) | **ARM** (nativní mobilní čipset) |
| **Rychlost CPU** | Využívá výkon PC (rychlejší než mobil) | Limitováno mobilním procesorem |
| **Baterie & Teplo** | Neřeší (nekonečná baterie, žádné přehřívání) | Throttling při zahřátí, vybíjení |
| **Sítě** | Stabilní, rychlý internet z PC | 4G/5G, kolísání signálu, latence |
| **Senzory** | Simulované (GPS, akcelerometr není tak přesný) | Reálná data, gyroskop, magnetometr |
| **Vstupy** | Myš a klávesnice | Dotyk, gesta, multi-touch |
| **Periferie** | Bluetooth/NFC často nelze emulovat | Plná podpora BT, NFC, USB |

---

## Detailní Rozbor

### 1. Výkon a Hardware (CPU/RAM)
* **Emulátor:** Běží na našem PC. Pokud máme silný počítač, emulátor může být *rychlejší* než reálný telefon. To je zrádné – aplikace se zdá plynulá, ale na slabém telefonu se bude sekat.
* **Realita:** Musíš počítat s **Thermal Throttlingem** (zpomalení procesoru při zahřátí) a správou paměti RAM, kdy systém agresivněji "zabíjí" aplikace na pozadí.

### 2. Uživatelská Interakce (UX)
Simulace dotyků myší je nepřesná.
* **Pinch-to-zoom:** Na emulátoru krkolomné (CTRL + myš).
* **Velikost prstů:** Myš má pixelovou přesnost. Prst uživatele je tlustý a zakrývá část displeje. Tlačítka, která se na emulátoru zdají OK, mohou být na mobilu příliš malá.

### 3. Fotoaparát a Média
* **Emulátor:** Používá webkameru notebooku nebo statický obrázek. Nelze testovat ostření, blesk, ISO, nebo složité Camera2 API funkce.
* **Realita:** Různí výrobci (Samsung vs. Xiaomi vs. Pixel) mají různé implementace fotoaparátu a formátů videa.

### 4. Specifické API (Bluetooth, NFC, GPS)
Některé věci v emulátoru prostě **neotestujeme**:
* **Bluetooth:** Spárování se sluchátky nebo IoT zařízením.
* **NFC:** Platby nebo načítání tagů.
* **GPS:** Emulátor umí nastavit souřadnice, ale neumí simulovat "skákání" signálu v tunelu nebo mezi budovami.
### 5. Vlákna a Souběžnost (Threading)
Oblast, kde emulátor nejčastěji "lže", je rychlost a správa vláken.

| Situace | Emulátor | Reálný Mobil |
| :--- | :--- | :--- |
| **Výkon vláken** | Všechna vlákna běží na silném CPU počítače. | Vlákna na pozadí jsou často odsunuta na **pomalá úsporná jádra** (big.LITTLE). |
| **Race Conditions** | Díky rychlosti se často "náhodou" nestanou. | Pomalejší běh odhalí chybějící synchronizaci -> **PÁD**. |
| **Zabíjení vláken** | Systém je benevolentní, vlákna běží stále. | Agresivní "Battery Saver" může vlákna na pozadí kdykoliv zmrazit. |

> **Varování:** Nikdy nespoléhej na to, že "vlákno A bude hotové dřív než vlákno B" jen proto, že to tak funguje na emulátoru. Na mobilu to bude jinak.

---

## Kdy co použít?

### Použij Emulátor, když:
* Začínáš s vývojem UI a layoutů.
* Potřebuješ otestovat různé velikosti displejů (tablet vs. telefon).
* Chceš zkusit různé verze Androidu (API 24 vs API 34), které fyzicky nemáš.

### Použij Reálné zařízení, když:
* Ladíš výkon a plynulost animací.
* Pracuješ se senzory (poloha, kamera, mikrofon).
* Testuješ spotřebu baterie.
* Jdeš do produkce (vždy otestuj na skutečném telefonu!).

---
## Ukázka kódu: Moderní Vlákna (Kotlin Coroutines)

Ukázka toho, jak v moderním Androidu vyřešit stahování dat bez zamrznutí aplikace (ANR). Využíváme **Coroutines**, které kód zpřehledňují – čte se to shora dolů, i když to běží na různých vláknech.

```kotlin
// Funkce v aktivitě (UI vrstva)
fun stahniDataTlacitko() {
    // 1. Jsme na Hlavním vlákně (Main Thread)
    // Můžeme bezpečně měnit UI
    progressBar.visibility = View.VISIBLE
    infoText.text = "Stahuji data..."

    // Spustíme korutinu v rámci životního cyklu aktivity
    lifecycleScope.launch {
        
        // 2. Tady se "přepneme" na pozadí
        // Voláme funkci označenou jako 'suspend'
        val vysledek = ziskejDataZeServeru() 

        // 3. Jakmile funkce skončí, jsme automaticky zpět na Hlavním vlákně
        // Můžeme zobrazit výsledek
        progressBar.visibility = View.GONE
        infoText.text = "Hotovo: $vysledek"
    }
}

// Funkce simulující těžkou práci (např. síť nebo databáze)
// Klíčové slovo 'suspend' říká, že tato funkce umí "pozastavit" korutinu
suspend fun ziskejDataZeServeru(): String {
    // withContext(Dispatchers.IO) explicitně přesune práci na vlákna optimalizovaná pro Input/Output
    return withContext(Dispatchers.IO) {
        
        // Simulace práce (2 sekundy čekání)
        // Na mobilu by tady probíhalo reálné stahování
        Thread.sleep(2000) 
        
        // Vracíme výsledek
        "Uživatelská data stažena!"
    }
}
