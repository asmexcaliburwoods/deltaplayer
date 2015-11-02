Δ is a Java SWT GUI front-end for `mplayer` http://www.mplayerhq.hu/ command line music player. Δ shows a tree of file system on the left pane, and a musical contents of a selected folder on the right, and allows to play it.

Δ is alpha quality---there is a lot of known bugs and inconsistencies.

Δ is a fork of [dplayer](https://sourceforge.net/projects/directoryplayer). Δ and dplayer are made after [1by1](http://mpesch3.de1.cc/) music player.

# Screenshot of the last version #

http://sourceforge.net/dbimage.php?id=308793

# News #

  * 2011 Jun 28: New bug: <font color='red'>cpu usage = 10...15% during very long periods.</font> The htop utility was useful to uncover this.
  * 2011 Jan 28: New bug: see Issues.
  * 2011 Jan 28: Bug in launcher still seems to exist, the same symptoms as below.
  * 2010 Dec 31: Bug found in launcher: still fails to find java sometimes.
  * 2010 Dec 29: In a 2010 Dec version, Linux launcher has been improved---it was often failing to launch in earlier versions.

# Installation under Linux #

1. Either

sudo aptitude install mplayer

or

Install mplayer from http://www.mplayerhq.hu/ .

2. Unpack [deltaplayer-linux-bin-2010\_DEC\_29\_\_0004](http://code.google.com/p/deltaplayer/downloads/detail?name=deltaplayer-linux-bin-2010_DEC_29__0004.zip&can=2&q=) into some dir, for example into `~/bin/deltaplayer/`.

3. Run `~/bin/deltaplayer/deltaplayer.sh` to play music. If this sctipt fails, fix it by editing it.

4. Done.

# Installation under Windows 32-bit #

1. Unpack [mplayer1.0rc2-4.2.1-win32.zip](http://deltaplayer.googlecode.com/files/mplayer1.0rc2-4.2.1-win32.zip) into some dir.

2. Unpack [deltaplayer-win32-bin-2010\_SEPT\_29\_\_0002.zip](http://deltaplayer.googlecode.com/files/deltaplayer-win32-bin-2010_SEPT_29__0002.zip) into some dir.

3. Launch dp.bat or deltaplayer.jar, and edit its Settings from its GUI to specify the location for mplayer.exe.

4. Done.