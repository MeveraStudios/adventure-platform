# adventure-platform

[![MIT License](https://img.shields.io/badge/license-MIT-blue)](license.txt)

Adventure platform implementations, for servers and proxies including [Paper](https://papermc.io)/Spigot/Bukkit and [BungeeCord](https://www.spigotmc.org/go/bungeecord). Other platforms may be supported through native integration, or other libraries.

This is a fork of the original [adventure-platform](https://github.com/PaperMC/adventure-platform) (now archived), maintained independently. Sponge support has been removed.

## Dependency

**Group:** `studio.mevera.adventure`. Maven Central publishing coming soon.

In the meantime, artifacts are available on [GraveMC's repository](https://repo.gravemc.net):

```xml
<repository>
  <id>gravemc-releases</id>
  <url>https://repo.gravemc.net/releases/</url>
</repository>
```

```xml
<!-- Bukkit/Spigot/Paper -->
<dependency>
  <groupId>studio.mevera.adventure</groupId>
  <artifactId>adventure-platform-bukkit</artifactId>
  <version>1.0.0</version>
</dependency>

<!-- BungeeCord -->
<dependency>
  <groupId>studio.mevera.adventure</groupId>
  <artifactId>adventure-platform-bungeecord</artifactId>
  <version>1.0.0</version>
</dependency>
```

Gradle (Kotlin DSL):

```kotlin
repositories {
  maven("https://repo.gravemc.net/releases/")
}

dependencies {
  implementation("studio.mevera.adventure:adventure-platform-bukkit:1.0.0")
  // or
  implementation("studio.mevera.adventure:adventure-platform-bungeecord:1.0.0")
}
```

## Modules

| Artifact                               | Description                |
|----------------------------------------|----------------------------|
| `adventure-platform-api`               | Core API                   |
| `adventure-platform-bukkit`            | Bukkit / Spigot / Paper    |
| `adventure-platform-bungeecord`        | BungeeCord                 |
| `adventure-platform-facet`             | Shared facet layer         |
| `adventure-platform-viaversion`        | ViaVersion bridge          |
| `adventure-text-serializer-bungeecord` | BungeeCord text serializer |

## Building

Requires JDK 21+.

```
./gradlew build
```

## License

`adventure-platform` is released under the terms of the [MIT License](license.txt).
