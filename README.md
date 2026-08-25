# Silcef

Allows you to easily put browser inside of Minecraft. 

Note that this is under development, and bugs and lack of features are expected.

### Features

 - **Accelerated Paint:** Original implementation has CPU bottleneck, as pixel needs to
   travel through CPU from GPU then back to GPU. This is inefficient, and accelerated 
   paint is here for a fix. Pixel data will never leave GPU and be drawn straight to
   your screen.
 - **Permission handler:** Allows you to have explicit control over permissions.
 - **Easier API:** Just call `addRenderableWidget`. Nothing more.
 - **Reasonable MCEF capability:** Silcef will be somewhat capable of running mods based on MCEF.

### Known issues and limitations

 - Making any interaction with browser will print `Exception in thread "Silcef CEF Message Worker"`
   message in console, on `stderr` that is not handled by JVM at all.
 - On some JVMs, this mod will cause a JVM crash from unhandled hardware exception.
 - Only Windows is supported; on other OS, it will either crash or not load at all.
 - MCEF capability layer only implements features exposed to MCEF, nothing more. (Excluding 
   accelerated paint)
 - MCEF capability layer is only tested against BrowserMod by McJunky33. File an issue if
   other MCEF dependent mod is broken with Silcef.

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
Get the latest release binary, and extract everything to `jcef` folder relative to project 
root. 

### Contributions & Filing an issue
Contributions are welcomed! When making a pull request, make sure that your changes:
 - Fit overall vive of original code
 - Contains only necessary changes
 - Is not written by AI

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
