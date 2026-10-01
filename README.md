# Essentials-honk

An aggressive performance-oriented fork of [FreshSMP/Essentials](https://github.com/FreshSMP/Essentials) (`async-move`), which is a fork of [EssentialsX](https://github.com/EssentialsX/Essentials).

> [!WARNING]
> This fork makes opinionated trade-offs favoring server throughput and async dispatch over strict vanilla Essentials behavior.
> Do **NOT** seek support from EssentialsX or FreshSMP for issues occurring on this build.

---

## Fork Lineage

- **Base:** [EssentialsX](https://github.com/EssentialsX/Essentials) (`2.x`)
- **Upstream:** [FreshSMP/Essentials](https://github.com/FreshSMP/Essentials) (`async-move` branch: asynchronous location handling and Folia compatibility)
- **Origin:** `Essentials-honk` (Aggressive async offloading, listener stripping, and tab-complete caching)

---

## Origin Modifications & Optimizations

### 1. Asynchronous Offloading & Threading (`TaskUtil`)
- **Async Messaging & Components:** `User.sendMessage`, `User.sendComponent`, and `User.sendTl` are offloaded to dedicated single-thread async executors (`TaskUtil.runAsync`) to prevent chat formatting/deserialization from stalling tick threads.
- **Async User Data & Storage:** User profile persistence (`UserData.save()`, `startTransaction()`, `stopTransaction()`) is executed asynchronously.
- **Async Modern UUID Cache:** Name-to-UUID cache writes and removals are processed asynchronously (`ModernUUIDCache`).
- **Async Command Preprocessing:** `PlayerCommandPreprocessEvent` parsing and alias mapping run asynchronously.
- **Async LuckPerms Context:** Context calculation in `LuckPermsHandler` is executed via Bukkit async tasks.
- **Async Timers & Teleports:** `AsyncTimedTeleport` and `EssentialsTimer` use async scheduled tasks.

### 2. Tab Completion Whitelist & Caching
- **Whitelisted Tab Completion:** Tab completion is disabled globally by default to eliminate tab-complete lag exploits and overhead. Only commands defined in `tab-completable-commands` receive tab completion.
- **Cached Online Players:** Player name tab completion uses an in-memory Guava cache (`TAB_COMPLETE_CACHE`) with configurable TTL (`tab-complete-cache-time-ms`, default 60s) rather than repeatedly iterating online players.
- **Optimized `/warp` Tab Completion:** Directly returns warp names without iterating permission checks per warp.

### 3. Stripped High-Frequency Event Listeners
To maximize tick performance and eliminate overhead from unused features, several high-volume event handlers are disabled:
- **`EssentialsPlayerListener`:** Disabled `PlayerInteractEvent`, `SculkListener` (game events), and inventory view inspection for `invsee`/`enderchest`.
- **`EssentialsEntityListener`:** Disabled `EntityDamageEvent`, `EntityDamageByEntityEvent`, `EntityCombustEvent`, `EntityCombustByEntityEvent`, `FoodLevelChangeEvent`, `EntityRegainHealthEvent`, `PotionSplashEvent`, and `EntityTargetEvent`.
- **`EssentialsBlockListener`:** Disabled `BlockPlaceEvent`.
- **`SignBlockListener`:** Disabled `BlockBreakEvent` and `BlockPlaceEvent` sign checks.
- **`Jails`:** Disabled `BlockPlaceEvent` jail checks.
- **World Load Optimization:** Disabled `PermissionsDefaults.registerBackDefaultFor` on `WorldLoadEvent` for servers that load/unload worlds dynamically.

### 4. Feature Trimming & Tweaks
- **God Mode:** God mode listeners and disconnect/join checks disabled.
- **Invsee & SocialSpy:** Removed internal inventory synchronization checks; use dedicated inventory inspection plugins.
- **Direct Hat:** Hat placement retained via direct Bukkit permission checks without `User` lookups.
- **Vanish Decoupling:** Vanish state tracking stripped from playtime calculations and disconnect hooks (designed for use with external vanish solutions such as AdvancedVanish).

---

## Configuration Options

Add the following options to your `plugins/Essentials/config.yml`:

```yaml
# Tab Completion is disabled on all commands in EssX-honk by default.
# Specify commands below that should allow tab-completion.
# Aliases are covered automatically. Leave empty to disable globally.
tab-completable-commands: ["warp", "sethome", "home", "renamehome", "delhome", "kit"]

# Time in milliseconds to cache online player names for tab-completion (default: 60000ms = 1 minute)
tab-complete-cache-time-ms: 60000
```

---

## Building

Requires **JDK 21** or higher.

- **Linux / macOS:** `./gradlew build`
- **Windows:** `.\gradlew.bat build`

Compiled jars will be located in the `jars/` directory.

Checkstyle and publication configurations have been disabled in `build-logic` for faster local builds.
