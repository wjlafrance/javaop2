# JavaOp2

It's a chatbot for [Classic Battle.net](http://classic.battle.net/)!

## System requirements

JavaOp2 requires Java 17. Give me lambda or give me death!

## Supported games

Diablo (`DRTL`), Diablo Shareware (`DSHR`), StarCraft, Brood War, Warcraft II, Diablo II, Diablo II: LoD, Warcraft III and The Frozen Throne are selectable in the Battle.net Login plugin's `game` setting.

Diablo and Diablo Shareware send no CD key. useast answers them (version byte 0x2A) with a Lockdown version check, which JavaOp2 cannot compute locally, so leave BNLS enabled (the default). They log on with SID_LOGONRESPONSE (0x29), no account is created automatically, and chat is limited to the channel menu: the bot joins "Diablo" / "Diablo Shareware" and the `statstring` setting holds the character statstring (e.g. `LTRD 23 0 0 85 10 50 50 3742 0`) that class and level channels look at.

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

## History

JavaOp2 was written by Ron Bowes (iago[x86]); the project site described it as "based originally on the code for
JBBot". This repository continues from that code. The chronology below is reconstructed from the sources named at
the end.

| Date | Event |
|---|---|
| 2004-06-12 | Earliest JavaOp mention in the Valhalla Legends forum archive |
| 2004-08-15 | iago announces the JavaOp release with Warcraft III support |
| 2005-01-16 | First beta of JavaOp2 released on javaop.com. |
| 2005-08-22 | JavaOp2 beta40b, the last numbered release listed on javaop.com. Plugin-only update `Plugins-beta40c` follows on 2006-09-12 ("Battle.net changes"). |
| 2008-07-18 | iago on Warden: JavaOp2 "can decrypt the traffic and download modules, but that's it" (vL msg 178998). |
| 2008-10-14 | First commit of the Google Code SVN repository (`javaop`, "Initial directory structure"). |
| 2009-05-19 | SVN r3: initial import of the JavaOp2 source (commit author `scotta`). |
| 2009-10-26 | SVN r6: first commit by joe[x86], adding BNLS client support. Through 2010-01 the BNLS and versioning code is reworked. |
| 2010-02-26 | JavaOp 2.1.3 RC1 uploaded to Google Code. |
| 2010-06-19 | JavaOp 2.1.3 final `jo213final.zip` uploaded; `JavaOp 2.1.3 r69.zip` follows on 2010-06-24. |
| 2026-10-08 | iago[x86]: "If you had told 20 year old me that people would still be talking about javaop2 when I was 43, I might have put more work into the name if nothing else 😄" |

### Where the old releases live
- Google Code Archive, project `javaop` (SVN): `source-archive.zip` and the 2.1.3 release zips.
- The Wayback Machine's copies of `www.javaop.com/javaop2/` (betas 1 to 40b as `Core-betaN.tgz` / `Plugins-betaN.tgz`).

### Licensing
The original code is public domain per Ron ("all code from it is considered Public Domain"). The Google Code project
page listed a BSD license. New code in this repository is CC0.

### Sources
vL forum archive posts (iago, 2004 to 2008); javaop.com home, download and contributors pages (Wayback, 2007-06-29);
Google Code Archive project metadata; this repository's `git-svn` history (`git-svn-id:
http://javaop.googlecode.com/svn/trunk@N`).

## License

The original code written by Ron (iago[x86]) was released into the Public Domain. Keeping in that spirit, new code committed to this repository is released under the [Creative Commons Zero](https://creativecommons.org/publicdomain/zero/1.0/) license.
