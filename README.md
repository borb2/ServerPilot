# ServerPilot
Control center for Minecraft server owners, admins and developers. 

## Requirements

- Java 25
- Paper or Folia 26.2

## Build

```bash
./gradlew build
```

Jar lands in `build/libs/`. To target another Paper release, change `paperApiVersion` and
`paperApiTarget` in `gradle.properties`.

## Commands

`/serverpilot`, or `/sp`.

| Command | Description |
| --- | --- |
| `/sp` | Open the dashboard |
| `/sp open [section]` | `server`, `players`, `admin`, `dev`, `plugins`, `integrations`, `performance`, `settings` |
| `/sp debugwand` | Give yourself the debug wand |
| `/sp reload` | Re-read `config.yml` |
| `/sp help` | List subcommands |

## Permissions

All default to `op`. `serverpilot.admin` grants everything.

`serverpilot.use`, `serverpilot.section.<server|players|admintools|devtools|plugins|integrations|performance|settings>`,
`serverpilot.tool.debugwand`, `serverpilot.settings.reload`

## TODO

Integration adapters, plugin install/update, player moderation actions, developer tools
beyond the debug wand, editing settings from the GUI.

## Folia?

Yea.
