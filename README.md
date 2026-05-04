# Slicef

Allows you to easily put browser inside of Minecraft. 

Note that this is under development, and bugs and lack of features are expected.

### Planned features

 - **Accelerated Paint:** Original implementation has CPU bottleneck, as pixel needs to
   travel through CPU from GPU then back to GPU. This is inefficient, and accelerated 
   paint is here for a fix. Pixel data will never leave GPU and be drawn straight to
   your screen.
 - **Permission handler:** Allows you to have explict control over permissions.
 - **IME support:** MCEF doesn't allow users to use IME, making typing experience for
   Koreans, Chinese, Japanese a pain. Allows for users to actually see what they
   are typing.
 - **Easier API:** Just call `addRenderableWidget`. There is no need to touch vertex
   data.
 - **Reasonable MCEF capability:** Slicef will be somewhat capable of running mods based on MCEF.

### Platform support

| Platform      | Support                      |
|---------------|------------------------------|
| Windows       | 🟩 **Working**               |
| MacOS         | 🟨 **Won't work**; planned   |
| Linux X11     | 🟥 **Won't work**; unplanned |
| Linux Wayland | 🟨 **Won't work**; planned   |

### Building this project

You will need to build [JCEF](https://github.com/IamTiji/java-cef) first. Follow
build instruction there, and put build binaries in `jcef` folder. There must be 
`jcef.jar` and `jcef.dll` in `jcef` folder.

After putting appropriate files, run `gradle runClient` to run Minecraft, and
`gradle build` to build the mod. Note that building as a standalone mod file
won't work right now.

### License

This project is licensed LGPL. If you haven't received a copy, you can get it from 
<https://www.gnu.org/licenses/lgpl+gpl-3.0.txt>.