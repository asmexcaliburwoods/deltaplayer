# Copyright (C) 2007 Tobias Winterhalter (tobias@wcondev.de)
# Copyright (C) 2010 Evgeniy Philippov
# 
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

#
# makefile for usage with MinGW make tool: mingw32-make
#

# Make sure, MinGW binaries are within your PATH environment.

CC=gcc
CFLAGS=-Wall -D_JNI_IMPLEMENTATION_ -I$(JAVA_HOME)\include -I$(JAVA_HOME)\include\win32
LDFLAGS=-shared
#-Wl 
#--kill-at 
#-Wl
#,--kill-at

all: deltaplayer.dll

WinExt.o: WinExt.c
	$(CC) $(CFLAGS) -c WinExt.c

deltaplayer.dll: WinExt.o
	$(CC) $(LDFLAGS) -o ../../../../dist/deltaplayer.dll WinExt.o -lwinmm
