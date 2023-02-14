# L2Library

## Package Structures

### dev.xkmc.l2library.serial
The auto-serialization package, 
as well as some auxiliary tools, 
such as network handling, config handling, 
and special stack serialization.

There are 2 major applications: data serialization and data synchronization.

Be aware that:
- All classes to be serialized need to be annotated as `@SerialClass`,
and all fields to be serialized need to be annotated as `@SerialField`.
- For entity/block entity fields that need to be synchronized to client, use `toClient=true`
- For capability fields that need to be synchronized to remote client, use `toTracking=true`
- All Collections need to be explicitly the leaf class that has a default constructor
(use `ArrayList` instead of `List`)
- For classes with generic types, generic type should not exist in any serializable fields
- For fields, define the fields using the leaf class if possible. If not, the actual class of the value will be
saved to the data as well, meaning that if the class is renamed or moved, the data will be invalid.

#### dev.xkmc.l2library.serial.codec
Interface for using serialization library.

#### dev.xkmc.l2library.serial.wrapper
Reflection Helper classes that caches reflection and
provides insight to generic parameters of known collections

#### dev.xkmc.l2library.serial.unified
Core serialization classes that converts between objects and JSON/NBT/Packet

#### dev.xkmc.l2library.serial.generic
Special serialization handlers for utility classes and collections

#### dev.xkmc.l2library.serial.handler
Special serialization handlers for minecraft classes

#### dev.xkmc.l2library.serial.nulldefer
Mark certain non-null values as null-like, such as `ItemStack.EMPTY`.
This is to make sure NBT-based synchronization works correctly.

#### dev.xkmc.l2library.serial.network
Packet handlers. Handle config optionally

#### dev.xkmc.l2library.serial.config
Config handlers

#### dev.xkmc.l2library.serial.stack
Special json stacks used in json-format recipes

### dev.xkmc.l2library.base
### dev.xkmc.l2library.block
### dev.xkmc.l2library.capability
### dev.xkmc.l2library.idea
### dev.xkmc.l2library.init
### dev.xkmc.l2library.mixin
### dev.xkmc.l2library.util