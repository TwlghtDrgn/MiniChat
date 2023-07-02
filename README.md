# MiniChat
A simple chat-formatting plugin for Paper 1.19+

Features:
- MiniMessage support
- Channels:
  - Global
  - Global (Cross-server)
  - Local + Global
  - Local + Global (Cross-server)
- PlaceholderAPI support
- A (very) simple setup

Requirements:
- Paper 1.19+ or any Paper fork
- [TwilightLib](https://github.com/TwlghtDrgn/TwilightLib)
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI/)
- SuperVanish / PremiumVanish (optional, but if you use any of them, then you can't send message accidentally when vanished)

Permissions
<table>
  <tr>
    <td>Permission</td>
    <td>Command</td>
    <td>Description</td>
  </tr>
  <tr>
    <td>minichat.command</td>
    <td>/minichat</td>
    <td>Required permission to use any plugin command</td>
  </tr>
  <tr>
    <td>minichat.command.reload</td>
    <td>/minichat reload</td>
    <td>Reloads config</td>
  </tr>
  <tr>
    <td>minichat.command.spy</td>
    <td>/minichat spy</td>
    <td>Allows to see local messages if global chat function is enabled</td>
  </tr>
  <tr>
    <td>minichat.colors</td>
    <td>  </td>
    <td>Allows player to use any MiniMessage formatting (but hyperlinks) in chat</td>
  </tr>
</table>