# XBoard protocol

`Ronja.main` creates a `CommandParser` over `System.in` and `System.out`, then
executes commands in a loop until a `QuitCommand` arrives. `CommandParser` reads
one line, splits it at the first space into a command name and its arguments, and
asks `CommandFactory` to create the command. A line that `CanParser.isMove`
recognises, such as `e2e4`, becomes a `UserMoveCommand` instead.

`CommandFactory` holds a map from command name to command class and constructs
each command by reflection. Adding a command therefore takes two steps: a class in
`engine/ui/command/`, and an entry in that map. Commands write back through the
`Response` interface, which `PrintWriterResponse` implements.

## Reporting an error to the GUI

A command constructor throws `InvalidCommandException` when its arguments are
wrong. `CommandFactory` catches that exception and returns an `InvalidCommand`,
which writes `Error (<message>): <args>`. This is the error format XBoard expects.
An unknown command name takes the same path, with the message `unknown command`.
The engine never stops because of a bad command.

## The handshake

`ProtoverCommand` answers only when the GUI asks for protocol version 2 or higher.
It declines `analyze`, `colors`, `sigint`, and `sigterm`. It accepts `name`,
`ping`, `playother`, `setboard`, and `usermove`. It reports `variants="normal"`,
sends its name from `AppConfig.getEngineName`, and ends with `done=1`.

## Commands with no implementation

Beyond the features the handshake declines, these XBoard commands have no class in
`engine/ui/command/`, so `CommandFactory` answers them with `unknown command`:
`undo`, `draw`, `sd`, and `?` (move now). They are not implemented.
