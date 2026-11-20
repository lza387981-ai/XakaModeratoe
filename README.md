# XakaModerator — Fabric 1.21.4

Client-side moderation GUI for server staff who already have the relevant permissions.

## Current implementation
- Right Shift opens and closes the GUI.
- Player nickname input.
- Ban and mute actions.
- Duration input (`30d`, `15m`, etc.).
- Reason selection.
- Commands preserve the server's exact pipe separator:
  - `/ban <nick> <duration> | <reason>`
  - `/mute <nick> <duration> | <reason>`

## Important limitations
- This is a client-side interface; it does not grant permissions or bypass server rules.
- The server must accept these commands and the logged-in account must have permission.
- Kick, unban, unmute, and punishment history are intentionally not wired yet because their exact server command syntax has not been supplied.
- Online-player autocomplete/listing is not implemented in this first scaffold yet.
- The reason-code choices currently included are 2.6, 3.5, and 3.3, based on the supplied examples. Confirm which codes are valid for each punishment before using them.

## Build
Requires JDK 21 and Gradle. From this folder run:

    gradle build

The JAR will be in `build/libs/`.
