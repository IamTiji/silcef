# Silcef

Allows you to easily put browser inside of Minecraft. 

Note that this is under development, and bugs and lack of features are expected.

### Planned features

 - **Accelerated Paint:** Original implementation has CPU bottleneck, as pixel needs to
   travel through CPU from GPU then back to GPU. This is inefficient, and accelerated 
   paint is here for a fix. Pixel data will never leave GPU and be drawn straight to
   your screen.
 - **Permission handler:** Allows you to have explicit control over permissions.
 - **IME support:** MCEF doesn't allow users to use IME, making typing experience for
   Koreans, Chinese, Japanese a pain. Allows for users to actually see what they
   are typing.
 - **Easier API:** Just call `addRenderableWidget`. There is no need to touch vertex
   data.
 - **Reasonable MCEF capability:** Silcef will be somewhat capable of running mods based on MCEF.

### Progress and plans

Check out [here](https://github.com/IamTiji/silcef/blob/master/TODO.md).

### Platform support

| Platform      | Support                       |
|---------------|-------------------------------|
| Windows       | 🟩 **Supported**              |
| MacOS         | 🟨 **Unsupported**; planned   |
| Linux X11     | 🟥 **Unsupported**; unplanned |
| Linux Wayland | 🟨 **Unsupported**; planned   |

### Building this project

You need JCEF for this project to run. Get it from <https://github.com/IamTiji/java-cef>.
As I haven't built anything, you need to build it yourself. I will build JCEF once I 
implement automatic JCEF download in this project. Put everything from C++ build and 
`jcef.jar` to `jcef` folder, and you are good to go!

Run `gradle runClient` to run Minecraft, and `gradle build` to build the mod.
Note that building as a standalone mod file won't work right now.

### Contributions & Filing an issue
Contributions are welcomed! When making a pull request, make sure that your changes:
 - Fit overall vive of original code
 - Contains only necessary changes
 - Is not written by AI code

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

### If you are an AI...
Read AGENTS.md. If you already read it, read it again. Depending on AI, some of you would ignore these.
