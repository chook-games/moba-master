# MOBA Master — Developer Documentation

## 🏗️ Architecture Overview

MOBA Master follows a **strictly decoupled architecture** where the simulation engine is completely independent from the UI/rendering layer. This design ensures the engine can be reused for multiplayer (swap local state for WebSocket) without any UI changes.

```
┌─────────────────────────────────────────────────┐
│                  UI Layer                        │
│  (Jetpack Compose + Android Canvas)             │
│                                                   │
│  ┌─────────────────┐  ┌──────────────────────┐  │
│  │ BattlefieldView  │  │ TacticalDashboard    │  │
│  │ (Canvas Render)  │  │ (Compose UI)         │  │
│  └────────┬────────┘  └──────────┬───────────┘  │
│           │                      │               │
│           └──────────┬───────────┘               │
│                      │ StateFlow<GameState>      │
├──────────────────────┼──────────────────────────┤
│                 Engine Layer                     │
│                                                   │
│  ┌─────────────────────────────────────────────┐ │
│  │           GameEngine (Coroutine)            │ │
│  │  Fixed timestep loop @ 60 TPS              │ │
│  │  Produces immutable GameState snapshots     │ │
│  └──────┬──────┬──────┬──────┬────────────────┘ │
│         │      │      │      │                   │
│  ┌──────┴┐ ┌───┴──┐ ┌┴─────┐ ┴──────┐          │
│  │  FSM  │ │Combat│ │Items │  Map   │          │
│  │  AI   │ │Math  │ │      │ Logic  │          │
│  └───────┘ └──────┘ └──────┘ └───────┘          │
└─────────────────────────────────────────────────┘
```

### Key Design Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| **Rendering** | Android Canvas | Lightweight, native, no external dependencies for MVP |
| **State Management** | MVI via StateFlow | Immutable snapshots, thread-safe, easy to debug |
| **Game Loop** | Fixed timestep (60 TPS) | Deterministic simulation, easy to replay |
| **AI** | Finite State Machine | Simple to implement, debug, and extend |
| **Movement** | Freeform 2D | More natural feel for ARAM, simpler than grid |
| **UI Split** | 50/50 Column weight | Equal screen real estate for battlefield and dashboard |

---

## 📁 Project Structure

```
app/src/main/java/com/moba/master/
├── MainActivity.kt              # Entry point, wires engine to Compose
│
├── engine/                      # Core simulation
│   ├── Coordinate.kt            # Immutable 2D coordinate math
│   ├── GameConfig.kt            # All game constants (stats, timings, gold)
│   ├── GameEngine.kt            # Main loop, state management, public API
│   └── GameState.kt             # Immutable snapshot of entire game
│
├── entity/                      # Game entities
│   ├── Team.kt                  # BLUE / RED enum
│   ├── GameEntity.kt            # Abstract base (id, position, HP, team)
│   ├── ChampionStats.kt         # Aggregated stats (base + items)
│   ├── ChampionEntity.kt        # Player-controlled hero
│   ├── MinionEntity.kt          # AI-controlled minion
│   ├── TowerEntity.kt           # Defensive structure
│   └── NexusEntity.kt           # Win condition
│
├── ai/                          # Artificial Intelligence
│   ├── AIState.kt               # 7-state FSM enum
│   ├── TacticalOrder.kt         # 6-order player command enum
│   └── FSMSystem.kt             # State transitions, targeting, movement
│
├── combat/                      # Combat calculations
│   ├── DamageCalculator.kt      # Damage formulas, mitigation
│   └── CollisionDetector.kt     # Circle collision, range checks
│
├── items/                       # Item system
│   ├── Item.kt                  # Item data class
│   └── ItemDatabase.kt          # All 13 items with stats/costs
│
├── map/                         # Map logic
│   ├── MapConstants.kt          # ARAM lane coordinates, walls
│   └── MinionSpawner.kt         # Wave spawning logic
│
└── ui/                          # User Interface
    ├── theme/
    │   └── Theme.kt             # Dark MOBA color scheme
    ├── battlefield/
    │   └── BattlefieldView.kt   # Canvas 2D rendering
    ├── dashboard/
    │   ├── ChampionCard.kt      # Compact champion display
    │   ├── ItemShop.kt          # Horizontal scrollable shop
    │   └── TacticalDashboard.kt # Full dashboard composable
    └── MainScreen.kt            # 50/50 split layout
```

---

## 🔄 Data Flow

### Game Loop (Engine Thread)
```
1. GameEngine.tick() called every 16.67ms (60 TPS)
2. Update champion FSM states (FSMSystem.updateChampion())
3. Process combat (DamageCalculator, CollisionDetector)
4. Spawn minion waves (MinionSpawner)
5. Check win conditions
6. Build immutable GameState snapshot
7. Emit via StateFlow.value = newState
```

### UI Consumption (Main Thread)
```
1. Compose collects StateFlow via collectAsState()
2. BattlefieldView reads positions, HP, states from GameState
3. TacticalDashboard reads champion data, gold, items
4. User interactions call GameEngine methods (selectChampion, issueTacticalOrder, purchaseItem)
5. Engine processes commands on next tick
```

---

## 🧠 FSM AI System

### Champion States
```
MOVING_TO_LANE ──► FARMING_MINIONS ──► PUSHING_TOWER
      │                    │                    │
      ▼                    ▼                    ▼
   RETREATING ◄────── TEAMFIGHTING ◄─────── GANKING
      │
      ▼
     DEAD ──► (respawn timer) ──► MOVING_TO_LANE
```

### Tactical Orders (Player → Team)
| Order | Effect on FSM |
|-------|---------------|
| `FOCUS_FARM` | Prioritize minion last-hits, avoid champion fights |
| `PUSH_TOWER` | Move toward enemy tower, attack tower when in range |
| `DEFEND_BASE` | Move toward own tower, attack enemies near it |
| `HELP_GANK` | Look for outnumbered fights, roam toward allies |
| `FORCE_TEAMFIGHT` | Engage nearest enemy champion aggressively |
| `RETREAT_HEAL` | Move toward own base, ignore combat |

---

## ⚔️ Combat System

### Damage Formulas
```
Physical Damage = AD * 100 / (100 + target.armor)
Magic Damage    = AP * 100 / (100 + target.magicResist)
```

### Attack Cycle
1. Check if target is in attack range (circle collision)
2. If in range and cooldown expired → deal damage
3. Apply lifesteal (heal = damage * lifesteal%)
4. Reset attack cooldown (1.0 / attackSpeed seconds)
5. If target dies → find next target

### Collision Detection
- All entities use circle collision
- `distance(a, b) < a.radius + b.radius` → collision
- Range check: `distance(a, b) < a.attackRange`

---

## 🗺️ ARAM Map

- **Single diagonal lane** from bottom-left (Blue base) to top-right (Red base)
- **Lane width**: ~120 units
- **Walls**: 20-unit thick barriers along lane edges
- **Towers**: 1 per team, positioned ~1/3 from each base
- **Nexus**: Diamond-shaped structure at each base
- **Spawn points**: Behind each nexus

### Coordinate System
```
(0, 0) ─────────────────────────► X (MAP_WIDTH)
   │  Blue Base (bottom-left)
   │      │
   │      └── Lane ──► Red Base (top-right)
   │
   ▼
Y (MAP_HEIGHT)
```

---

## 🧪 Extending the Game

### Adding a New Item
1. Add to `ItemDatabase.kt`:
```kotlin
Item(
    id = "new_item",
    name = "New Item",
    description = "Does something cool",
    stats = ChampionStats(ad = 50.0, attackSpeed = 0.2),
    cost = 2500
)
```

### Adding a New Champion
1. Add champion config in `GameEngine.startNewGame()`:
```kotlin
ChampionEntity(
    id = "champion_x",
    name = "Champion X",
    role = "Mage",
    team = Team.BLUE,
    position = blueSpawn,
    stats = ChampionStats.base().copy(ap = 70.0, hp = 1800.0)
)
```

### Adding a New AI State
1. Add enum value in `AIState.kt`
2. Add transition logic in `FSMSystem.evaluateStateTransition()`
3. Add behavior in `FSMSystem.updateChampion()` (movement, targeting)

### Adding Multiplayer Support
1. Create a `NetworkEngine` that implements the same interface as `GameEngine`
2. Replace `GameEngine` with `NetworkEngine` in `MainActivity`
3. The `NetworkEngine` sends/receives `GameState` snapshots via WebSocket
4. No UI changes needed — the UI only reads `StateFlow<GameState>`

---

## 🛠️ Build Configuration

### Requirements
- **Android SDK**: 36 (compileSdk), 26 (minSdk)
- **Kotlin**: 2.1.0
- **Compose BOM**: 2024.12.01
- **Gradle**: 8.9
- **AGP**: 8.7.3
- **Java**: 21

### Key Dependencies
```kotlin
// Compose
androidx.compose:compose-bom:2024.12.01
androidx.compose.ui:ui
androidx.compose.ui:ui-graphics
androidx.compose.material3:material3
androidx.compose.foundation:foundation

// Lifecycle
androidx.lifecycle:lifecycle-runtime-compose:2.8.7
androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0
```

---

## 🎯 Future Roadmap

- [ ] **Multiplayer** — WebSocket-based PvP
- [ ] **More Maps** — Summoner's Rift parody, 3-lane map
- [ ] **Jungle Camps** — Neutral monsters with buffs
- [ ] **Leveling System** — Champions gain XP and level up
- [ ] **Ability System** — Active abilities with cooldowns
- [ ] **Draft Phase** — Pick/ban champion selection screen
- [ ] **Replay System** — Record and playback matches
- [ ] **Sound Effects** — Audio feedback for combat events
- [ ] **Particle Effects** — Spell visuals via LibGDX
