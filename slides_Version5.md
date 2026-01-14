<!-- Title slide -->
# Vývoj mobilních aplikací: Android
## Od Javy ke Kotlinu a Jetpack Compose
---

## Proč vyvíjet pro Android?
- Tržní podíl: Android běží na cca 70 % mobilních zařízení na světě.
- Otevřenost: Open-source systém (AOSP).
- Diverzita zařízení: telefony, tablety, hodinky (Wear OS), televize (Android TV), auta (Android Auto).

---

## Historie a evoluce
- 2003: Založení Android Inc. (Andy Rubin).
- 2005: Google kupuje Android.
- 2008: První komerční telefon HTC Dream (T‑Mobile G1).
- Názvosloví: dříve podle sladkostí (Cupcake, Donut...), nyní číslování (Android 14, 15...).

---

## Programovací jazyky
### Java
- Tradiční jazyk, na kterém Android vyrostl.
- Obrovské množství existujícího kódu a knihoven.
- Pro nový vývoj dnes často považovaná za zastaralé.

### Kotlin
- Moderní jazyk, plně kompatibilní s Javou.
- Od roku 2019 "Google preferred" jazyk pro Android.
- Stručnější syntaxe, Null Safety a moderní funkce.

---

## Vývojové prostředí (IDE)
- Android Studio: oficiální IDE od Google (postavené na IntelliJ IDEA).

Klíčové nástroje:
- Gradle: build systém.
- Emulator: virtuální zařízení pro testování.
- Logcat: ladění a výpis chyb.

---

## Základní stavební kameny aplikace
- Activity: jedna obrazovka s UI (vstupní bod).
- Fragment: znovupoužitelná část obrazovky (modulární UI).
- Service: běží na pozadí (přehrávání hudby, stahování).
- Intent: zpráva pro komunikaci mezi komponentami (např. "Otevři fotoaparát").

---

## Tvorba UI: XML vs. Jetpack Compose
### Imperativní přístup (XML)
- Starší způsob: layouty v XML, logika v Kotlin/Javě.
- Složitější správa stavu.

### Deklarativní přístup (Jetpack Compose)
- Moderní toolkit, současný standard.
- UI se píše přímo v Kotlinu.
- Popisujete, jak má UI vypadat pro daný stav, ne jak se má měnit.

---

## Architektura aplikace
- Proč architektura? Aby byl kód čitelný, testovatelný a udržitelný.

### MVVM (Model-View-ViewModel)
- View: UI (co vidí uživatel).
- ViewModel: drží data pro UI a přežije otočení obrazovky.
- Model: data a logika (databáze, síť).

### Clean Architecture
- Rozdělení do vrstev: UI, Domain(podmínky), Data — lepší oddělení zodpovědností.

---

## Závěr
- Android = obrovský trh + široký ekosystém.
- Kotlin + Jetpack Compose = doporučená cesta pro nový vývoj.
``` ````
