# MiniChat
A simple chat-formatting plugin for Paper 1.19+

Features:
- MiniMessage support
- Modes:
  - Vanilla / Vanilla cross-server
  - Ranged + Global / Ranged + Global cross-server 
- PlaceholderAPI support
- A (very) simple setup

Requirements:
- MiniChat-Paper:
  - Paper 1.20+ or any Paper fork
- MiniChat-Velocity:
  - Velocity 3.2.0+
- [TwilightLib](https://github.com/TwlghtDrgn/TwilightLib)
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI/)
- SuperVanish / PremiumVanish (optional, but if you use any of them, then you can't send message accidentally when vanished)

Plugin permissions list:
<table>
  <tr>
    <td>Permission</td>
    <td>Command</td>
    <td>Description</td>
    <td>Platform</td>
  </tr>
  <tr>
    <td>minichat.command</td>
    <td>/minichat, /minichat-velocity</td>
    <td>Required permission to use any plugin command</td>
    <td>Paper, Velocity</td>
  </tr>
  <tr>
    <td>minichat.command.reload</td>
    <td>/minichat reload, /minichat-velocity reload</td>
    <td>Reloads config</td>
    <td>Paper, Velocity</td>
  </tr>
  <tr>
    <td>minichat.command.spy</td>
    <td>/minichat spy</td>
    <td>Allows to see local messages if global chat function is enabled</td>
    <td>Paper</td>
  </tr>
  <tr>
    <td>minichat.command.networkspy</td>
    <td>/minichat-velocity networkspy</td>
    <td>Allows to see cross-server messages</td>
    <td>Velocity</td>
  </tr>
  <tr>
    <td>minichat.command.socialspy</td>
    <td>/minichat-velocity socialspy</td>
    <td>Allows to see DMs</td>
    <td>Velocity</td>
  </tr>
  <tr>
    <td>minichat.command.alert</td>
    <td>/alert, /broadcast</td>
    <td>Broadcast a message across servers</td>
    <td>Velocity</td>
  </tr>
  <tr>
    <td>minichat.command.block</td>
    <td>/block, /unblock</td>
    <td>Allows a player to restrict someone from sending DMs</td>
    <td>Velocity</td>
  </tr>
  <tr>
    <td>minichat.command.message</td>
    <td>/msg, /message, ...</td>
    <td>Allows sending direct messages</td>
    <td>Velocity</td>
  </tr>
  <tr>
    <td>minichat.command.reply</td>
    <td>/reply, /r, ...</td>
    <td>Allows replying to direct messages</td>
    <td>Velocity</td>
  </tr>
  <tr>
    <td>minichat.command.reply</td>
    <td>/chat</td>
    <td>Toggles direct messages into a chat</td>
    <td>Velocity</td>
  </tr>
  <tr>
    <td>minichat.command.staff</td>
    <td>/staffchat, /sc, /mc..</td>
    <td>Without any arguments toggles staffchat into a chat mode; also allows to send a quick message without toggling (e.g. /mc hello)</td>
    <td>Velocity</td>
  </tr>
  <tr>
    <td>minichat.vanished</td>
    <td>  </td>
    <td>Allows sending messages to vanished players. Requires an additional plugin</td>
    <td>Velocity</td>
  </tr>
  <tr>
    <td>minichat.command.synced</td>
    <td>/minichat-velocity synced</td>
    <td>Allows to check if subservers are registered on the proxy</td>
    <td>Velocity</td>
  </tr>
  <tr>
    <td>minichat.color</td>
    <td>  </td>
    <td>Allows to use colors in the chat</td>
    <td>Paper</td>
  </tr>
  <tr>
    <td>minichat.all-formatting</td>
    <td></td>
    <td>Allows to use any MiniMessage formatting in the chat</td>
    <td>Paper</td>
  </tr>
</table>