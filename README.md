# Wcrates

A simple crate system plugin for Minecraft (Spigot/Paper) with placeholder-based rewards.

## Features

This plugin provides a GUI-based crate creation system with animated reward scrolling:

- **3-row inventory menu** for crate configuration
- **Customizable GUI slots**:
  - Slot 4: Crate result display
  - Slot 10: Block type selector (vanilla blocks)
  - Slot 12: Crate name selector (name tag)
  - Slot 14: Crate ID selector (name tag)
  - Slot 16: Block placement mode activation
  - Slot 21: Key selector (placeholder for future implementation)
  - Slot 22: Create crate button (generates both crate and placeholder files)
  - Slot 23: Close menu (barrier)
- **Block selection system**: Right-click or left-click on any block to transform it into your chosen crate block
- **Animated placeholder scrolling**: Values scroll from min to max with customizable animation
- **Range-based rewards**: Define rewards that trigger when the placeholder lands in a specific range (e.g., 140-168)
- **Crate-specific placeholders**: Each crate has its own unique placeholders to prevent conflicts
- **Automatic file generation**: Create both crate config and placeholder files with a single click
- **Fully customizable language**: All text can be modified in `language.yml`
- **Customizable crate configurations**: Each crate has its own YAML configuration file

## Commands

- `/wcrates` (aliases: `/crate`, `/crates`) - Opens the crate creation menu
  - Permission: `wcrates.admin`

## Installation

1. Download the latest release
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure `language.yml` to customize messages

## Configuration

### Language Configuration

All text displayed in the plugin can be customized in `plugins/Wcrates/language.yml`:

- Menu titles and item names
- Block selection messages
- Crate interaction messages
- Title and subtitle when entering placement mode
- Success and error messages

### Crate Configuration

Crates are configured in YAML files in the `plugins/Wcrates/crates/` directory. Each crate configuration includes:

```yaml
crate:
  id: "example_crate"
  name: "&6&lExample Crate"
  block: "CHEST"

  animation:
    duration: 100  # Duration in ticks (20 ticks = 1 second)
    speed: 2       # Ticks between updates
    min: 1         # Minimum value for scrolling
    max: 200       # Maximum value for scrolling

rewards:
  placeholders:
    1:
      between: '%wcrates_example_crate_140-168%'
      name: "&a&lRare Reward"
      commands:
        - "give %player% diamond 5"
        - "give %player% emerald 3"
      messages:
        - "&aYou won the Rare Reward!"
```

**Key Configuration Options:**

- `id`: Unique identifier for the crate
- `name`: Display name with color codes
- `block`: Material type for the crate block
- `animation.min/max`: Range of values to scroll through
- `animation.duration`: How long the animation runs (in ticks)
- `animation.speed`: Update interval (in ticks)
- `rewards.placeholders`: Numbered entries with placeholder-based ranges
- `between`: Placeholder format `%wcrates_CRATEID_MIN-MAX%` defining the value range (includes crate ID to prevent conflicts)

### Placeholder Files

Each crate also has a corresponding placeholder file in `plugins/Wcrates/placeholders/` that documents the available placeholders for that crate. This helps prevent conflicts between different crates by ensuring each crate has unique placeholder identifiers.

## Usage

### Creating a Crate

1. Run `/wcrates` to open the crate creation menu
2. Click on slot 12 to set the crate name (optional)
3. Click on slot 14 to set the crate ID (required - must be unique)
4. Click on slot 10 to select the block type (optional)
5. Click on slot 22 "Create Crate" to generate the configuration files
   - This creates a file in `crates/CRATEID.yml`
   - This also creates a file in `placeholders/CRATEID.yml`
6. Click on slot 16 to enter block placement mode
7. Right-click or left-click any block to transform it into a crate block
8. The block will be marked with metadata linking it to the crate configuration

### Opening a Crate

1. Right-click on a placed crate block
2. Watch the animated value scroll from min to max
3. When the animation completes, the final value determines your reward
4. Rewards are given based on which range the final value falls into
5. Commands are executed and messages are displayed to the player

### How the Placeholder System Works

The plugin uses an internal placeholder system that scrolls through values:

1. When a player opens a crate, a random final value is chosen
2. The animation starts at the minimum value and scrolls rapidly through all values
3. As the animation progresses, it slows down and approaches the final value
4. The current value is displayed to the player using the action bar or title
5. When the animation completes, the final value determines which reward range matches
6. The corresponding reward is given to the player

**Important**: Each crate has unique placeholders that include the crate ID (e.g., `%wcrates_example_crate_140-168%`). This prevents conflicts when multiple crates are used on the same server. The crate ID ensures that placeholders from one crate won't interfere with placeholders from another crate.

## Building

```bash
mvn clean package
```

The compiled JAR will be in the `target/` directory.

## Requirements

- Spigot/Paper 1.16.5 or newer (compatible up to 1.21.1+)
- Java 8 or newer

## Example Rewards Configuration

The plugin includes an example crate configuration with multiple reward tiers:

- **Common Reward** (1-50): 10 iron ingots
- **Uncommon Reward** (51-100): 5 gold ingots + 15 iron ingots
- **Rare Reward I** (101-139): 3 diamonds
- **Rare Reward** (140-168): 5 diamonds + 3 emeralds
- **Epic Reward** (169-200): 1 netherite ingot + 10 diamonds

You can customize these ranges and rewards in `plugins/Wcrates/crates/example_crate.yml`

## License

Apache License 2.0 - See LICENSE file for details