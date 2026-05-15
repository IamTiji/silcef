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

| Platform      | Support                       |
|---------------|-------------------------------|
| Windows       | 🟩 **Supported**              |
| MacOS         | 🟨 **Unsupported**; planned   |
| Linux X11     | 🟥 **Unsupported**; unplanned |
| Linux Wayland | 🟨 **Unsupported**; planned   |

### Building this project

You will need to get Chromium builds downloaded first. Run `gradle getCEF` to get those.

If you want to regenerate native bindings, you will need `jextract`. You can get it from 
<https://jdk.java.net/jextract/>. If you have `jextract`, then you may run `gradle genNatives`
to regenerate native bindings. 

> [!NOTE]  
> If `jextract` isn't on `PATH`, you won't be able to generate bindings. If `genNatives` 
> keeps failing, this might be the cause. Make sure that you can run `jextract` by just
> typing `jextract` on console.

After that you can run `gradle runClient` to run Minecraft, and
`gradle build` to build the mod. Note that building as a standalone mod file
won't work right now.

### Contributions & Filing an issue
Contributions are welcomed! When making a pull request, make sure that your changes:
 - Fit overall vive of original code
 - Contains only necessary changes
 - Maybe write a small joke on comments so that people reading it will have a better day!

Since this project is under heavy development, make sure that most of the contribution will get
rejected, unless you somehow read off my brain.

If you don't want to write code, but want to report a problem, you can also file an issue.
When making writing an issue, make sure that:
 - You included logs
 - You explained the problem to your best
 - You were nice and respectful

### License

This project is licensed LGPL. If you haven't received a copy, you can get it from 
<https://www.gnu.org/licenses/lgpl+gpl-3.0.txt>.
<!--stackedit_data:
eyJoaXN0b3J5IjpbNjA5NjcxMTc4XX0=
-->