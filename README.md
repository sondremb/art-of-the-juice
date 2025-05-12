# The Art of Juice - Spillutviklingsworkshop i Kotlin med LibGDX

## Test ut spillet

For å teste ut spillet, gjør en av følgende:
* Finn profilen `Lwjgl3Launcher` øverst til høyre i IntelliJ
* Gå til fila [`Lwjgl3Launcher.kt`](lwjgl3/src/main/kotlin/dev/bakke/artofjuice/lwjgl3/Lwjgl3Launcher.kt) og kjør main-metoden der (IntelliJ bør gi deg en grønn pil)
* Kjør `./gradlew lwjgl3:run` i terminalen.

Prøv ut spillet! Keybinds:

| Tast           | Funksjon               | Info                         |
|----------------|------------------------|------------------------------|
| \[←\] \[→\]    | Bevege deg             |                              |
| \[LEFT SHIFT\] | Skyte                  | Hold inne                    |
| \[TAB\]        | Bytte våpen            |                              |
| \[SPACE\]      | Hoppe                  | Hold inne for å hoppe høyere |
| \[C\]          | Kaste granat           |                              |
| \[F1\]         | Toggle debug rendering | Huskes mellom launcher       |


## Lett oppvarming: parametertukling

Spillet er konfigurert med masse forskjellige parametere.

I fila [Enemy.kt](core/src/main/kotlin/dev/bakke/artofjuice/enemy/Enemy.kt) ser du hvordan fiendene "spawnes".
Prøv å justere på fiendenes hastighet, max helse, eller tyngdeakslerasjon! Kanskje verdiene kunne vært tilfeldige?

Fila [PlayerInputComponent.kt](core/src/main/kotlin/dev/bakke/artofjuice/player/PlayerInputComponent.kt) inneholder mesteparten av spillerens bevegelse-logikk.
Det er mye rart der, inkludert konstanter som styrer spillerens hastighet og hopp!

Fila [GunStats.kt](core/src/main/kotlin/dev/bakke/artofjuice/gun/GunStats.kt) styrer parameterne for våpnene til spilleren.
Tukle litt med verdiene! Hvis du vil legge til et nytt våpen, ta en titt i [GunInventoryComponent.kt](core/src/main/kotlin/dev/bakke/artofjuice/player/GunInventoryComponent.kt)

Fila [GrenadeThrowerComponent.kt](core/src/main/kotlin/dev/bakke/artofjuice/player/GrenadeThrowerComponent.kt) holder paremetrene for granaten.

> 📈 **Akser og enheter**  
>Positiv x-akse er mot høyre, og positiv y-akse er oppover.  
>Spillet er satt opp slik at det er 1 "enhet" = 1 pixel.
>Når f.eks. fiender har en "speed" på 100, så betyr det at de beveger seg 100 enheter = 100 pixels i sekundet.

## Oppgaver

### Oppgave 1: Skade-animasjon på fiende

Spillet funker, men fiendene er litt kjedelige - de reagerer jo ikke når du treffer de!  
Vi kan fikse dette ved å legge til en animasjon når fienden tar skade.

I fila [EnemyAIComponent.kt](core/src/main/kotlin/dev/bakke/artofjuice/enemy/EnemyAIComponent.kt) finner du en tom funksjon `onHit()`.

<details>
<summary>Løsningsforslag</summary>

```kotlin
private fun onHit(damage: Int) {
    animatedSprite.requestTransition(EnemyAnimatedSprite.State.HURT)
}
```
</details>

* Legg til animasjon på hit
* Impuls/knockback på hit
* Legg til screenshake
* Forbedre kamerabevegelse
* Endre til OnDeath
* Muzzle flash?
* Skru på post-processing
* Skriv din egen shader
* Legg til poeng-system?
* Flytt på fiende-spawner / legg til ny


## Jeg vil endre på "mappet"!

Synd 🤡🤠

Mappet er laget med den gratis programvaren [Tiled Map Editor](https://www.mapeditor.org/).

Oppsettet jeg har gått for er litt kronglete. Jeg vil **ikke** anbefale å prøve å få til noe særlig her i løpet av workshopen.
Hvis du likevel ønsker å gjøre endringer, for eksempel hvis du er Sondre og lurer på hvordan i all verden det var satt opp igjen, les videre.

Selve tilemappet ligger i fila [map.tmx](assets/map.tmx) i assets - åpne den for å redigere.  
Det er satt opp "automapping" i Tiled - i laget `proto`, tegn med den nesten helt mørke tilen (`ID: 1`) for å få til automapping, som dukker opp i laget `metal`.  

Kollisjon bestemmes av rectangle-objektene i laget `metal_collision`.  
Hvis du er Sondre, så har du satt opp en plugin som lager dette for deg - marker laget "metal" og trykk `⌘K`, eller gå til `Edit > My Custom Action`.
Hvis du ikke er Sondre, eller hvis du har mistet pluginen, så må du tegne kollisjonsrektanglene selv. Mas på Sondre for å få tak i pluginen.

Objektene i laget `Player` bestemmer hvor spiller spawner (`Player`), og hvor enemy-spawneren er (`Enemy`).

## Assets

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a Kotlin project template that includes Kotlin application launchers and [KTX](https://libktx.github.io/) utilities.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
