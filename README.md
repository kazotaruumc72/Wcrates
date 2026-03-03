# Wcrates

A simple crate system plugin for Minecraft (Spigot/Paper).

## Features

This plugin provides a GUI-based crate creation system with the following features:

- **3-row inventory menu** for crate configuration
- **Customizable GUI slots**:
  - Slot 4: Crate result display
  - Slot 10: Block type selector (vanilla blocks)
  - Slot 12: Crate name selector (name tag)
  - Slot 14: Crate ID selector (name tag)
  - Slot 16: Block placement mode activation
  - Slot 21: Key selector (placeholder for future implementation)
  - Slot 23: Close menu (barrier)
- **Block selection system**: Right-click or left-click on any block to transform it into your chosen crate block
- **Fully customizable language**: All text can be modified in `language.yml`

## Commands

- `/wcrates` (aliases: `/crate`, `/crates`) - Opens the crate creation menu
  - Permission: `wcrates.admin`

## Installation

1. Download the latest release
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure `language.yml` to customize messages

## Configuration

All text displayed in the plugin can be customized in `plugins/Wcrates/language.yml`:

- Menu titles and item names
- Block selection messages
- Title and subtitle when entering placement mode
- Success and error messages

## Usage

1. Run `/wcrates` to open the crate creation menu
2. Click on slot 16 to enter block placement mode
3. The menu will close temporarily and show a title: "&aSelectionnez le block"
4. Right-click or left-click any block to transform it into your crate block
5. The block will be transformed and placement mode will deactivate

## Building

```bash
mvn clean package
```

The compiled JAR will be in the `target/` directory.

## Requirements

- Spigot/Paper 1.16.5 or newer
- Java 8 or newer

## License

Apache License 2.0 - See LICENSE file for details