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
* Začínáme s vývojem UI a layoutů.
* Potřebujeme otestovat různé velikosti displejů (tablet vs. telefon).
* Chceme zkusit různé verze Androidu (API 24 vs API 34), které fyzicky nemáš.

### Použijeme Reálné zařízení, když:
* Ladíme výkon a plynulost animací.
* Pracujeme se senzory (poloha, kamera, mikrofon).
* Testujeme spotřebu baterie.
* Jdeme do produkce (vždy otestujme na skutečném telefonu!).

---
## Ukázka kódu: Moderní Vlákna (Kotlin Coroutines)

Ukázka toho, jak v moderním Androidu vyřešit stahování dat bez zamrznutí aplikace (ANR). Využíváme **Coroutines**.

```kotlin
fun stahniData() {
    
    // 1. Start: Uživatel klikl na tlačítko.
    // Jsme na hlavním vlákně (UI), takže můžeme měnit texty.
    infoText.text = "Čekám na data..."
    
    // Spustíme asynchronní akci (korutinu)
    lifecycleScope.launch {
        
        // 2. Přepnutí na pozadí (IO vlákno)
        // Tady říkáme: "Jdi pryč z hlavního vlákna a spočítej to na pozadí."
        val stazenaZprava = withContext(Dispatchers.IO) {
            
            // Tady simulujeme pomalý internet (čekáme 3 sekundy)
            // Aplikace ale NEZAMRZNE, protože jsme na pozadí!
            delay(3000) 
            
        }

        // 3. Návrat zpět
        // Jakmile blok nahoře skončí, automaticky jsme zpět na hlavním vlákně.
        // Můžeme bezpečně vypsat výsledek.
        infoText.text = "Hotovo: $stazenaZprava"
    }
}
