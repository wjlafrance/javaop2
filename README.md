# JavaOp2

It's a chatbot for [Classic Battle.net](http://classic.battle.net/)!

## System requirements

JavaOp2 requires Java 17. Give me lambda or give me death!

## Building and running

    $ git clone git@github.com:wjlafrance/javaop2.git
    $ cd javaop2
    $ mvn package
    $ mkdir plugins-build && cp `find plugins/ | grep jar$ | xargs` plugins-build
    $ java -jar javaop2/target/javaop2-2.2.0-SNAPSHOT.jar

`mvn package` also copies the runtime libraries (FlatLaf) into `javaop2/target/lib/`; the jar's manifest points at that directory, so keep `lib/` next to the jar when you move it.

Select the `plugins-build` directory when prompted on the first bot load. This prompt only appears in dev runs like this one: the packaged app (below) finds its bundled plugins by itself.

## macOS app

    $ ./mvnw -B -Pmac-app package -DskipTests

This builds everything, stages `dist/target/dist/` (`javaop2.jar`, `lib/`, and all plugin jars in `plugins/`), and runs `jpackage`. The result is `dist/target/app/JavaOp2.app` (about 74 MB; it carries its own trimmed Java runtime, so no Java install is needed). Add `-Dapp.type=dmg` to get `JavaOp2-<version>.dmg` instead. The profile needs a JDK 17+ with `jpackage` (it uses the JDK running Maven) and is not part of the default build or CI.

Run it with `open dist/target/app/JavaOp2.app`. Settings still live in `~/.javaop2`.

The plugins sit in `JavaOp2.app/Contents/app/plugins/` and are found automatically (a `plugins` folder next to the app jar), without being saved to `_PluginPaths.txt`, so you can move the app around. Plugin folders chosen by hand still work in addition.

The app is not signed with a Developer ID (jpackage only ad-hoc signs it), so Gatekeeper may block it when it comes from a download. Right-click > Open, or run `xattr -dr com.apple.quarantine JavaOp2.app`.

Other platforms aren't packaged yet. It would extend the same way: run `jpackage` on each OS (`--type app-image`, `exe`/`msi`, `deb`/`rpm`) from the same staged `dist/` folder, e.g. a CI matrix over macos/windows/ubuntu runners. The bundled-plugin lookup already understands the Windows/Linux jpackage layout (`<app>/app/plugins`), and the placeholder icon would need `.ico`/`.png` versions (`packaging/macos/MakeIcon.java` generates the current one).

## License

The original code written by Ron (iago[x86]) was released into the Public Domain. Keeping in that spirit, new code committed to this repository is released under the [Creative Commons Zero](https://creativecommons.org/publicdomain/zero/1.0/) license.
