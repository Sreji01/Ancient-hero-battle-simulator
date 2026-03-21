# ⚔️ Ancient Hero Battle Simulator

A turn-based card battle game written in **Clojure**, played entirely in the terminal.

---

## 📖 Overview

Two players compete by drafting or randomly receiving decks of heroes, actions, traps, and equipment cards. Each round, players draw cards, play them strategically during the selection phase, then send their heroes into combat during the attack phase. The game ends when one player's HP reaches zero.

---

## 🎮 Gameplay

Deck composition varies by match size. In a 1v1, each player gets 2 heroes, 3 actions, 3 traps, and 1 equipment card. Larger formats (2v2 through 4v4) scale the counts accordingly.

Each round consists of three phases:

| Phase | Description |
|-------|-------------|
| **Draw Phase** | Players draw one card per turn. The first draw is larger and guaranteed to include a hero. |
| **Selection Phase** | Players may play one card of each category: a hero, an action, a trap, and equipment. |
| **Attack Phase** | Each hero on the field attacks once. Players choose targets manually. |

---

## 🃏 Card Types

### 🦸 Heroes
Placed on the field and fight during the attack phase. Each hero has five stats:

- `power` — determines attack damage
- `defense` — reduces incoming damage
- `agility` — affects dodge chance
- `intelligence` — improves hit accuracy
- `health` — total HP pool

### ⚡ Actions
Single-use cards with immediate effects. Types include:

- **Attack** — deal damage to an enemy hero or all enemies, or deal direct damage to the enemy player
- **Heal** — restore HP to a single ally or all allies
- **Buff** — boost a hero's stats for the current round
- **Defense** — grant damage reduction or a dodge chance to a hero
- **Utility** — draw cards, skip an enemy's attack, or take control of an enemy hero for one round

### 🪤 Traps
Placed face-down and trigger on player confirmation when specific events occur:

| Trigger | Fires when... |
|---------|---------------|
| `enemy-action` | The opponent plays an action card |
| `enemy-attack` | An enemy hero attacks |
| `enemy-hero-placed` | The opponent places a hero on the field |
| `player-attack` | Your own hero is about to attack |

Trap effects include damage, debuffs, poison (damage-over-time), attack reflection, action negation, and temporary hero control.

### 🛡️ Equipment
Permanently attached to a hero — **one per hero**. Boosts stats for the rest of the game. Removed from the field if the equipped hero is defeated.

---

## ⚔️ Combat

Damage is calculated as:

```
damage = max(5, attacker.power − (target.defense × 0.5) − damage_reduction)
```

Heroes can be given **dodge chances** or **damage reduction** through action cards before the attack phase. Player HP is also reduced when their heroes take damage, so losing heroes hurts you on two fronts.

Traps can fire mid-combat to debuff attackers, reflect damage, or apply damage-over-time effects.

---

## 🎯 Card Selection Modes

- **Draft** — players alternate picking cards from the full pool, category by category
- **Random** — cards are shuffled and distributed automatically

---

## 🚀 Running the Game

Requires [Leiningen](https://leiningen.org/) or the [Clojure CLI](https://clojure.org/guides/install_clojure).

```bash
# With Leiningen
lein run

# With Clojure CLI
clojure -M -m ancient-hero-battle-simulator.core
```

---

## 🗂️ Project Structure

```
src/ancient_hero_battle_simulator/
├── core.clj                        # Entry point
├── cards/
│   ├── heroes.clj                  # Hero card definitions
│   ├── actions.clj                 # Action card definitions
│   ├── traps.clj                   # Trap card definitions
│   └── equipment.clj               # Equipment card definitions
└── game/
    ├── game_flow.clj               # Round and turn orchestration
    ├── game_state.clj              # State management and field operations
    ├── logic.clj                   # Card play execution
    ├── combat_logic.clj            # Attack phase and damage calculation
    ├── deck_menagment.clj          # Drawing, drafting, and card selection
    ├── ui.clj                      # Terminal output and board display
    ├── utilility.clj               # Input helpers
    └── card_logic/
        ├── action_logic.clj        # Action card effects
        ├── trap_logic.clj          # Trap triggering and effects
        └── equipment_logic.clj     # Equipment attachment and stat bonuses
```

---

## 🛠️ Built With

- [Clojure](https://clojure.org/) — functional programming on the JVM
- Terminal I/O — no GUI, pure readline interaction
