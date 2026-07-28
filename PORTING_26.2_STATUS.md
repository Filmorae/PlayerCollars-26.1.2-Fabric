# Player Collars 26.2 Porting Status

## Baseline

- Target repository: `Filmorae/PlayerCollars-26.1.2-Fabric`
- Target branch: `26.2`
- Baseline `origin/main` commit: `ad1adb7a90cb24f162ecec52eb9f644f88f8f649`
- Branch creation: `26.2` was created from the baseline commit without modifying `main`.

## Environment

- Distribution: Ubuntu 24.04.3 LTS
- Architecture: x86_64
- JDK: Eclipse Temurin OpenJDK 25.0.3+9 LTS, x64
- `JAVA_HOME`: session-local Eclipse Temurin JDK 25 installation outside the repository
- Java compiler: `javac 25.0.3`
- Git: 2.51.1
- GitHub CLI: 2.96.0
- Gradle Wrapper: 9.5.0
- Gradle launcher and daemon JVM: Eclipse Temurin 25.0.3+9 LTS

The system package database is read-only, so JDK 25 and GitHub CLI were installed under the writable workspace. Their official archives and SHA-256 checksums were verified before extraction. Gradle's Java process could not use the Work proxy for the initial wrapper download, so the official Gradle distributions were downloaded with `curl` and verified against Gradle's published SHA-256 checksums.

## Current Phase

Migration complete; awaiting interactive Windows gameplay and legacy-world tests.

## Completed Work

- Verified the connected GitHub identity is `Filmorae`.
- Verified repository ownership, default branch, origin URL, and push permission.
- Synchronized local `main` with `origin/main` using fast-forward-only semantics.
- Created and published the `26.2` branch from the exact baseline commit.
- Read the build configuration, metadata, mixin configuration, access widener, entrypoints, registration, networking, interaction, leash, item/block, rendering, serialization, and recent-history areas of the project.
- Confirmed that datagen Java sources were intentionally excluded during the 26.1.2 migration because Fabric's datagen/model provider APIs changed substantially. Existing generated resources remain runtime inputs. Restoring datagen compilation is out of scope unless it becomes necessary for a correct 26.2 runtime build.
- Confirmed Minecraft 26.2 is a release and declares Java 25 in Mojang's version metadata.
- Confirmed Fabric Loader 0.19.3 is the current stable loader exposed for Minecraft 26.2.
- Confirmed Fabric API `0.155.2+26.2` exists in the official Fabric Maven.
- Confirmed Fabric Loom `1.17.17` is a published stable plugin release.
- Confirmed Trinkets Updated `4.1.0-beta.3+26.2` is published by the official project on Modrinth and the Nucleoid Maven. The downloaded Modrinth JAR matched its published SHA-1, identifies itself as `trinkets_updated`, and declares Minecraft `26.2.x`.
- Compared the official Trinkets Updated source tags `4.0.0-beta.2+26.1` and `4.1.0-beta.3+26.2`. The attachment iteration, slot access, component-equipment, and renderer APIs used by this project remain available. The notable removal of tuple-returning `getEquipped` methods does not affect the current `EquippedTrinkets` adapter, which already uses iteration and predicates.
- Updated the project dependency set and metadata to Minecraft 26.2, Fabric API `0.155.2+26.2`, Loom `1.17.17`, and Trinkets Updated `4.1.0-beta.3+26.2`.
- Set the mod version to `1.6.1+26.2` and narrowed runtime dependency declarations to the verified versions.
- Upgraded the Gradle Wrapper to 9.5.0 and pinned Gradle's official SHA-256.
- Generated and inspected the mapped Minecraft 26.2 sources before changing APIs.
- Migrated screen access to the new `Minecraft.gui` owner, block-position
  bottom-center lookup to `Vec3.atBottomCenterOf`, the turtle registry constant
  to `EntityTypes.TURTLE`, and the camera accessor to `GameRenderer.mainCamera`.
- Migrated the laser marker from immediate buffer access to Fabric's
  `COLLECT_SUBMITS` event and `SubmitNodeCollector` custom geometry path.
- Removed the obsolete `DogBedBlock.newBlockEntity` override after `BedBlock`
  stopped implementing the block-entity contract; Dog Bed still remains a
  normal two-block bed with the existing custom shape and interaction logic.
- Updated the inactive Accessories compatibility renderer signature to the
  26.2 submit-node rendering type. Runtime equipment behavior continues to use
  Trinkets Updated.
- Completed `compileJava`, access-widener validation, resource processing,
  remapping, checks, and a clean full build on Java 25.
- Added an explicit isolated `run/server` Loom configuration for repeatable
  dedicated-server validation.
- Fixed `BoneItemMixin` after runtime validation showed that `Items` registration
  now uses `ResourceKey<Item>` rather than string IDs. The redirect preserves
  all vanilla simple item registrations and adds the existing head-equippable
  component only when the key equals `ItemIds.BONE`.
- Removed the dangling `fabric-datagen` entrypoint from production metadata
  because its source set remains deliberately excluded. Generated runtime
  assets stay packaged, and no missing datagen class is declared by the JAR.
- Created the complete Windows gameplay and 26.1.2 data-compatibility checklist
  in `GAMEPLAY_TEST_26.2.md`.

## Important Decisions

- Preserve all registry IDs, payload IDs, data-component IDs/codecs, tags, resource paths, package names, and current gameplay behavior.
- Keep the existing generated resources and datagen exclusion strategy.
- Use real published dependency versions and let compilation/runtime errors drive localized source changes.
- Use Gradle Wrapper 9.5.0. Loom 1.17.17 declares Gradle Plugin API 9.5.0;
  Gradle 9.4.0 rejected Loom's `runtimeElements` variant before project
  configuration, proving the wrapper upgrade necessary.
- Use Fabric Loader `0.19.3`, Fabric API `0.155.2+26.2`, Fabric Loom `1.17.17`, and Trinkets Updated `4.1.0-beta.3+26.2`.
- Declare Minecraft compatibility as exact `26.2`. Fabric dependency comparisons ignore build metadata, so exact `0.155.2` and `4.1.0-beta.3` predicates match the verified `+26.2` artifacts without claiming unrelated API versions.

## Validation Results

### Toolchain

- `java -version`: Temurin OpenJDK 25.0.3+9 LTS
- `javac -version`: 25.0.3
- `git --version`: 2.51.1
- `gh --version`: 2.96.0
- `gradlew --version`: Gradle 9.5.0 on Java 25.0.3

### Minecraft 26.2 Build

Commands:

- `./gradlew compileJava --offline --stacktrace`
- `./gradlew clean build --offline --stacktrace`
- `./gradlew clean build --stacktrace` (final acceptance build)

Result: both succeeded. The clean build compiled runtime sources, processed
resources and nested dependencies, produced the remapped production JAR,
validated the access widener, and completed all available checks. The project
currently has no Java unit-test sources, so Gradle reported `test NO-SOURCE`.
The final exact acceptance command also succeeded in 8 seconds with all six
applicable build/check tasks successful or up-to-date.

### Dedicated Server

The first runtime attempt reached Minecraft bootstrap but failed before world
creation because `BoneItemMixin` still shadowed the removed string-based
`Items.registerItem` overload. This was treated as a failed attempt even though
the Loom JavaExec task returned a successful Gradle status.

After the Mixin fix:

- Fresh start: loaded Minecraft 26.2, 48 Fabric mods, Player Collars
  `1.6.1+26.2`, Trinkets Updated `4.1.0-beta.3+26.2`, 1643 recipes, and 1745
  advancements; created a new world and reached `Done (2.066s)`.
- Clean stop: the `stop` console command saved players, all worlds, all chunks,
  and all three dimensions before the Gradle task exited successfully.
- Repeat start: reopened the saved world and reached `Done (0.238s)`.
- Repeat clean stop: again saved all dimensions and exited successfully.

No Player Collars Mixin, access-widener, registry, resource, payload, recipe,
tag-loading, Trinkets attachment, or data-save fatal error remained in either
successful server run.

Non-fatal external/environment warnings:

- Trinkets Updated logs one missing client-only
  `CreativeModeInventoryScreenAccessor` target while loading its Mixin
  configuration on a dedicated server. This originates from the official
  dependency JAR and did not load the class or prevent either server start.
- Minecraft's system report could not query process metrics through OSHI in the
  managed container.
- Mojang's public-key request could not resolve through the restricted runtime
  network. The offline test server still completed world load.
- Fabric convention tags emitted its generic development warning for missing
  optional display translations.

### Client Boundary

An opportunity client launch was not attempted: no Xvfb or active X11 display is
installed, the system package database is read-only, and setting up a graphical
stack would exceed the requested cloud-client boundary. Client Mixin target
signatures and the `PlayerModel.slim` access widener were checked against mapped
26.2 sources; actual interactive client/gameplay verification remains for the
Windows checklist.

### Production JAR

- File: `build/libs/PlayerCollars-1.6.1+26.2.jar`
- Type: Loom-remapped production JAR (not dev, sources, test, or unmapped)
- Size: 331001 bytes
- SHA-256: `6a70d11b70bbc61a8afca396ddf1ac12aad130dda662e0fe74f0c51318ea5859`
- Corresponding source: final `26.2` branch HEAD

Archive integrity and metadata checks passed. The JAR contains `fabric.mod.json`,
both Mixin configurations, the access widener, main and client entrypoint
classes, two language resources, 133 model entries, 17 texture entries, 59
recipes, tags, enchantments, loot/advancement data, sounds, and all other
generated runtime resources. It contains no datagen classes, dangling datagen
entrypoint, Work path, dev JAR, sources JAR, or test artifact. Embedded metadata
reports mod ID `playercollars`, version `1.6.1+26.2`, exact Minecraft `26.2`,
Java 25+, Loader 0.19.3+, Fabric API 0.155.2, and Trinkets Updated
4.1.0-beta.3.

### 26.1.2 Baseline Build

Command:

`bash ./gradlew clean build --stacktrace`

Result: failed during plugin resolution before project compilation. `net.fabricmc.fabric-loom:1.16-SNAPSHOT` was not available from the configured plugin repositories. This is a stale/unreproducible dependency baseline, not a Java compilation failure.

The repository's `gradlew` was tracked without the executable bit, so direct
`./gradlew` initially failed with `Permission denied`. The `26.2` branch restored
the executable bit in its baseline status commit.

## Failed Attempts

- System package installation: blocked because `/var/lib/apt` is read-only.
- Initial Gradle Wrapper download from Java: blocked by the Work network proxy not being inherited by Java. Resolved by downloading and checksum-verifying the official distribution with `curl`.
- Baseline build: blocked by the unavailable `1.16-SNAPSHOT` Loom plugin. This will be resolved by selecting a real 26.2-compatible Loom release.
- Loom 1.17.17 on Gradle 9.4.0: plugin resolution reached the official Maven
  successfully, but variant selection failed because Loom requires Gradle Plugin
  API 9.5.0. Resolved by updating the wrapper to Gradle 9.5.0.
- The managed Work sandbox denied Loom's Unix-domain-socket capability probe
  with `Operation not permitted`. An environment-only patch in the local Gradle
  artifact cache treats that result as “unsupported,” which only disables
  decompiler worker socket logging. No proxy, sandbox path, patched dependency,
  or Work-specific setting is present in the repository or production JAR.
- First dedicated-server runtime: failed at Minecraft bootstrap because
  `BoneItemMixin` targeted the removed
  `registerItem(String, Item.Properties)` method. Resolved against mapped 26.2
  `Items` sources with a `ResourceKey<Item>` redirect; two later starts passed.

## Open Issues

- Perform the interactive Windows gameplay checklist, including visual
  rendering, permissions, multiplayer synchronization, and backed-up 26.1.2
  world/player data compatibility.
- The non-fatal dedicated-server client-Mixin warning belongs to the official
  Trinkets Updated 4.1.0-beta.3+26.2 JAR and cannot be corrected inside Player
  Collars without replacing or patching that dependency.

## Next Step

Install the production JAR with the exact dependency set on Windows and execute
`GAMEPLAY_TEST_26.2.md`, starting with multiplayer ownership/leash behavior and
a backed-up 26.1.2 compatibility world.
